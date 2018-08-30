/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.plugins.context.locking.curator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.locking.AbstractLockManager;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class CuratorLockManager manages the Curator interface towards Zookeeper for administering the Apex Context Album
 * instance locks..
 */
public class CuratorLockManager extends AbstractLockManager {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CuratorLockManager.class);

    // The Curator framework used for locking
    private CuratorFramework curatorFramework;

    // The address of the Zookeeper server
    private String curatorZookeeperAddress;

    /**
     * Constructor, set up a lock manager that uses Curator locking.
     *
     * @throws ContextException On errors connecting to Curator
     */
    public CuratorLockManager() throws ContextException {
        LOGGER.entry("CuratorLockManager(): setting up the Curator lock manager . . .");

        LOGGER.exit("CuratorLockManager(): Curator lock manager set up");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.impl.locking.AbstractLockManager#init(org.onap.policy.apex. model.
     * basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("init(" + key + ")");

        super.init(key);

        // Get the lock manager parameters
        final CuratorLockManagerParameters lockParameters = ParameterService
                        .get(CuratorLockManagerParameters.class.getSimpleName());

        // Check if the curator address has been set
        curatorZookeeperAddress = lockParameters.getZookeeperAddress();
        if (curatorZookeeperAddress == null || curatorZookeeperAddress.trim().length() == 0) {
            LOGGER.warn("could not set up Curator locking, check if the curator Zookeeper address parameter is set correctly");
            throw new ContextException(
                            "could not set up Curator locking, check if the curator Zookeeper address parameter is set correctly");
        }

        // Set up the curator framework we'll use
        curatorFramework = CuratorFrameworkFactory.builder().connectString(curatorZookeeperAddress)
                        .retryPolicy(new ExponentialBackoffRetry(lockParameters.getZookeeperConnectSleepTime(),
                                        lockParameters.getZookeeperContextRetries()))
                        .build();

        // Listen for changes on the Curator connection
        curatorFramework.getConnectionStateListenable().addListener(new CuratorManagerConnectionStateListener());

        // Start the framework and specify Ephemeral nodes
        curatorFramework.start();

        // Wait for the connection to be made
        try {
            curatorFramework.blockUntilConnected(
                            lockParameters.getZookeeperConnectSleepTime() * lockParameters.getZookeeperContextRetries(),
                            TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            // restore the interrupt status
            Thread.currentThread().interrupt();
            String message = "error connecting to Zookeeper server at \"" + curatorZookeeperAddress
                            + "\", wait for connection timed out";
            LOGGER.warn(message);
            throw new ContextException(message);
        }

        if (!curatorFramework.getZookeeperClient().isConnected()) {
            String message = "could not connect to Zookeeper server at \"" + curatorZookeeperAddress
                            + "\", see error log for details";
            LOGGER.warn(message);
            throw new ContextException(message);
        }

        // We'll use Ephemeral nodes for locks on the Zookeeper server
        curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL);

        LOGGER.exit("init(" + key + "," + lockParameters + ")");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.impl.locking.AbstractLockManager#getReentrantReadWriteLock(
     * java.lang.String)
     */
    @Override
    public ReadWriteLock getReentrantReadWriteLock(final String lockId) throws ContextException {
        // Check if the framework is active
        if (curatorFramework != null && curatorFramework.getZookeeperClient().isConnected()) {
            return new CuratorReentrantReadWriteLock(curatorFramework, "/" + lockId);
        } else {
            throw new ContextException("creation of lock using Zookeeper server at \"" + curatorZookeeperAddress
                            + "\", failed, see error log for details");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#shutdown()
     */
    @Override
    public void shutdown() {
        if (curatorFramework == null) {
            return;
        }
        CloseableUtils.closeQuietly(curatorFramework);
        curatorFramework = null;
    }

    /**
     * This class is a callback class for state changes on the curator to Zookeeper connection.
     */
    private class CuratorManagerConnectionStateListener implements ConnectionStateListener {

        /*
         * (non-Javadoc)
         *
         * @see org.apache.curator.framework.state.ConnectionStateListener#stateChanged(org.apache.
         * curator.framework.CuratorFramework, org.apache.curator.framework.state.ConnectionState)
         */
        @Override
        public void stateChanged(final CuratorFramework incomngCuratorFramework, final ConnectionState newState) {
            // Is the state changed for this curator framework?
            if (!incomngCuratorFramework.equals(curatorFramework)) {
                return;
            }

            LOGGER.info("curator state of client \"{}\" connected to \"{}\" changed to {}", curatorFramework,
                            curatorZookeeperAddress, newState);

            if (newState != ConnectionState.CONNECTED) {
                shutdown();
            }
        }
    }
}
