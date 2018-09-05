/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.test.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ZooKeeperServerServiceProvider provides a zookeeper service to a caller.
 */
public class ZooKeeperServerServiceProvider {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ZooKeeperServerServiceProvider.class);

    private NIOServerCnxnFactory zookeeperFactory;
    private File zookeeperDirectory;
    private InetSocketAddress addr;

    /**
     * Instantiates a new zoo keeper server service provider.
     *
     * @param zookeeperDirectory the zookeeper directory
     * @param addr the addr
     */
    public ZooKeeperServerServiceProvider(final File zookeeperDirectory, final InetSocketAddress addr) {
        this.zookeeperDirectory = zookeeperDirectory;
        this.addr = addr;
    }

    /**
     * Instantiates a new zoo keeper server service provider.
     *
     * @param zookeeperDirectory the zookeeper directory
     * @param addr the addr
     * @param port the port
     */
    public ZooKeeperServerServiceProvider(final File zookeeperDirectory, final String addr, final int port) {
        this.zookeeperDirectory = zookeeperDirectory;
        this.addr = new InetSocketAddress(addr, port);
    }

    /**
     * Start the Zookeeper server.
     * @throws IOException the IO exception occurs while setting up Zookeeper server
     * @throws InterruptedException the interrupted exception occurs while setting up Zookeeper server
     */
    public void startZookeeperServer() throws IOException, InterruptedException {
        LOGGER.info("Starting up ZooKeeperServer using address: {} and port: {}", addr.getAddress(), addr.getPort());
        final ZooKeeperServer server = new ZooKeeperServer(zookeeperDirectory, zookeeperDirectory, 5000);
        zookeeperFactory = new NIOServerCnxnFactory();
        zookeeperFactory.configure(addr, 100);
        zookeeperFactory.startup(server);
    }

    /**
     * Stop the Zookeeper server.
     */
    public void stopZookeeperServer() {
        LOGGER.info("Stopping ZooKeeperServer for address: {} and port: {}", addr.getAddress(), addr.getPort());
        if (zookeeperFactory != null) {
            zookeeperFactory.shutdown();
        }
    }
}
