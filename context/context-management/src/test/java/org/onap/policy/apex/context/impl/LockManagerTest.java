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

package org.onap.policy.apex.context.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.locking.LockManagerFactory;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.common.parameters.ParameterService;

class LockManagerTest {

    @Test
    void testLock() {
        var lockManager = new JvmLocalLockManager();
        assertDoesNotThrow(() -> lockManager.lockForReading("test", "test"));
        assertDoesNotThrow(() -> lockManager.unlockForReading("test", "test"));
        assertThrows(ContextException.class, () -> lockManager.unlockForReading("test", "test"));
        assertDoesNotThrow(() -> lockManager.lockForWriting("test", "test"));
        assertDoesNotThrow(() -> lockManager.unlockForWriting("test", "test"));
        assertThrows(ContextException.class, () -> lockManager.unlockForWriting("test", "test"));
    }

    @Test
    void testShutDown() {
        var lockManager = new JvmLocalLockManager();
        assertDoesNotThrow(lockManager::shutdown);
    }

    @Test
    void testCreateLockManager() {
        var lockManagerFactory = new LockManagerFactory();
        var parameters = new LockManagerParameters();
        parameters.setPluginClass("invalid.class");
        ParameterService.register(parameters);
        assertThrows(ContextException.class, () -> lockManagerFactory.createLockManager(new AxArtifactKey()));
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);

        // different plugin instance
        parameters.setPluginClass(JvmLocalDistributor.class.getName());
        ParameterService.register(parameters);
        assertThrows(ContextException.class, () -> lockManagerFactory.createLockManager(new AxArtifactKey()));
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);

        parameters.setPluginClass(LockManagerParameters.DEFAULT_LOCK_MANAGER_PLUGIN_CLASS);
        ParameterService.register(parameters);
        assertDoesNotThrow(() -> lockManagerFactory.createLockManager(new AxArtifactKey()));
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
    }
}
