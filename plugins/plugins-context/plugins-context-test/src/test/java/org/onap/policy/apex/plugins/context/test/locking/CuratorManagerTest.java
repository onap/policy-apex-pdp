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

package org.onap.policy.apex.plugins.context.test.locking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManager;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManagerParameters;

public class CuratorManagerTest {
    // Zookeeper test server
    TestingServer zkTestServer;

    @Before
    public void beforeTest() throws Exception {
        zkTestServer = new TestingServer(62181);
    }

    @After
    public void afterTest() throws IOException {
        zkTestServer.stop();
    }

    @Test
    public void testCuratorManagerConfigProperty() {
        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.setLockManagerParameters(new CuratorLockManagerParameters());

        ((CuratorLockManagerParameters) contextParameters.getLockManagerParameters()).setZookeeperAddress(null);

        try {
            final CuratorLockManager curatorManager = new CuratorLockManager();
            curatorManager.init(new AxArtifactKey());
            assertNull(curatorManager);
        } catch (final ContextException e) {
            assertEquals(e.getMessage(),
                    "could not set up Curator locking, check if the curator Zookeeper address parameter is set correctly");
        }

        ((CuratorLockManagerParameters) contextParameters.getLockManagerParameters()).setZookeeperAddress("zooby");

        try {
            final CuratorLockManager curatorManager = new CuratorLockManager();
            curatorManager.init(new AxArtifactKey());
            fail("Curator manager test should fail");
        } catch (final ContextException e) {
            assertEquals(e.getMessage(),
                    "could not connect to Zookeeper server at \"zooby\", see error log for details");
        }

        ((CuratorLockManagerParameters) contextParameters.getLockManagerParameters())
                .setZookeeperAddress("localhost:62181");

        try {
            final CuratorLockManager curatorManager0 = new CuratorLockManager();
            curatorManager0.init(new AxArtifactKey());
            assertNotNull(curatorManager0);

            final CuratorLockManager curatorManager1 = new CuratorLockManager();
            curatorManager1.init(new AxArtifactKey());
            assertNotNull(curatorManager1);

            curatorManager0.shutdown();
            curatorManager1.shutdown();
        } catch (final ContextException e) {
            assertNull(e);
        }
    }
}
