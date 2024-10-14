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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.jupiter.api.Test;

class CuratorLockFacadeTest {

    @Test
    void testLock() throws Exception {
        var mutex = mock(InterProcessMutex.class);
        var curatorLockFacade = new CuratorLockFacade(mutex, "test");
        assertDoesNotThrow(curatorLockFacade::lock);
        doThrow(new RuntimeException()).when(mutex).acquire();
        assertDoesNotThrow(curatorLockFacade::lock);
        assertDoesNotThrow(curatorLockFacade::lockInterruptibly);
        assertFalse(curatorLockFacade.tryLock());
        doNothing().when(mutex).acquire();
        assertTrue(curatorLockFacade.tryLock());
    }

    @Test
    void testLockWithTime() throws Exception {
        var mutex = mock(InterProcessMutex.class);
        var curatorLockFacade = new CuratorLockFacade(mutex, "test");
        assertTrue(curatorLockFacade.tryLock(2L, TimeUnit.MILLISECONDS));

        doThrow(new RuntimeException()).when(mutex).acquire(2L, TimeUnit.MILLISECONDS);
        assertFalse(curatorLockFacade.tryLock(2L, TimeUnit.MILLISECONDS));

        assertDoesNotThrow(curatorLockFacade::unlock);
        doThrow(new RuntimeException()).when(mutex).release();
        assertDoesNotThrow(curatorLockFacade::unlock);

        assertNull(curatorLockFacade.newCondition());
    }

}
