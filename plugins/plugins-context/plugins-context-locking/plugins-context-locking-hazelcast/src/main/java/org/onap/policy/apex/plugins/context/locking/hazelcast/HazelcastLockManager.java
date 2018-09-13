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

package org.onap.policy.apex.plugins.context.locking.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.concurrent.locks.ReadWriteLock;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.locking.AbstractLockManager;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class HazelcastLockManager manages Hazelcast locks for locks on items in Apex context albums.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class HazelcastLockManager extends AbstractLockManager {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(HazelcastLockManager.class);

    private HazelcastInstance hazelcastInstance;

    /**
     * Constructor, set up a lock manager that uses Hazelcast locking.
     *
     * @throws ContextException On errors connecting to the Hazelcast cluster
     */
    public HazelcastLockManager() throws ContextException {
        LOGGER.entry("HazelcastLockManager(): setting up the Hazelcast lock manager . . .");

        LOGGER.exit("HazelcastLockManager(): Hazelcast lock manager set up");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.impl.locking.AbstractLockManager#init(org.onap.policy.apex.
     * model. basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("init(" + key + ")");

        super.init(key);

        // Set up the Hazelcast instance for lock handling
        hazelcastInstance = Hazelcast.newHazelcastInstance();

        LOGGER.exit("init(" + key + ")");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.impl.locking.AbstractLockManager#getReentrantReadWriteLock(
     * java.lang.String)
     */
    @Override
    public ReadWriteLock getReentrantReadWriteLock(final String lockId) throws ContextException {
        // Check if the framework is active
        if (hazelcastInstance != null && hazelcastInstance.getLifecycleService().isRunning()) {
            return new HazelcastLock(hazelcastInstance, lockId);
        } else {
            throw new ContextException("creation of hazelcast lock failed, see error log for details");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#shutdown()
     */
    @Override
    public void shutdown() {
        if (hazelcastInstance == null) {
            return;
        }
        hazelcastInstance.shutdown();
        hazelcastInstance = null;
    }
}
