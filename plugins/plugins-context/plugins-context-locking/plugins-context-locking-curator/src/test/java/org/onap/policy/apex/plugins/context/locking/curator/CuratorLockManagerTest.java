/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.common.parameters.ParameterService;


class CuratorLockManagerTest {

    @Test
    void testLockManagerInvalidParameter() throws ContextException {
        var manager = new CuratorLockManager();
        var key = new AxArtifactKey("test", "1.0.1");

        var parameters = new LockManagerParameters();
        parameters.setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.register(parameters);

        assertThatThrownBy(() -> manager.init(key)).isInstanceOf(ContextException.class)
                .hasMessageContaining("curator lock manager parameters are not set");

        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testLockManagerValidParams() throws ContextException {
        var manager = new CuratorLockManager();
        var key = new AxArtifactKey("test", "1.0.1");
        var params = new CuratorLockManagerParameters();
        params.setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.register(params);

        assertThatThrownBy(() -> manager.init(key)).isInstanceOf(ContextException.class)
                .hasMessageContaining("could not connect to Zookeeper server");

        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testNullZookeeperAddr() throws ContextException {
        var params = new CuratorLockManagerParameters();
        params.setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        params.setZookeeperAddress("");
        ParameterService.register(params);

        var manager = new CuratorLockManager();
        var key = new AxArtifactKey("test", "1.0.1");

        assertThatThrownBy(() -> manager.init(key)).isInstanceOf(ContextException.class)
                .hasMessageContaining("check if the curator Zookeeper address parameter is set correctly");

        assertThatThrownBy(() -> manager.getReentrantReadWriteLock("test")).isInstanceOf(ContextException.class)
                .hasMessageContaining("creation of lock using Zookeeper server at \"\", failed");

        assertDoesNotThrow(manager::shutdown);

        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testLockManagerParameters() {
        var params = new CuratorLockManagerParameters();
        assertDoesNotThrow(params::toString);
        assertEquals("localhost:2181", params.getZookeeperAddress());
        assertEquals(1000, params.getZookeeperConnectSleepTime());
        assertEquals(3, params.getZookeeperContextRetries());
    }

    @Test
    void testReentrantReadWriteLock() {
        var curatorLock = new CuratorReentrantReadWriteLock(mock(CuratorFramework.class), "/test");
        assertEquals("/test", curatorLock.getLockId());
        assertDoesNotThrow(curatorLock::readLock);
        assertDoesNotThrow(curatorLock::writeLock);
    }

}
