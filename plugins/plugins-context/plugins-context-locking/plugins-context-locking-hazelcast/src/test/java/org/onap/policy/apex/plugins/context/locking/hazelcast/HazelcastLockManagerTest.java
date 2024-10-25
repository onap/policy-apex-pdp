/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

class HazelcastLockManagerTest {

    private HazelcastLockManager lockManager;
    private HazelcastInstance mockHazelcastInstance;
    private LifecycleService mockLifecycleService;
    private MockedStatic<Hazelcast> mockedHazelcast;

    @BeforeEach
    void setUp() throws ContextException {
        mockHazelcastInstance = mock(HazelcastInstance.class);
        mockLifecycleService = mock(LifecycleService.class);

        mockedHazelcast = mockStatic(Hazelcast.class);
        mockedHazelcast.when(Hazelcast::newHazelcastInstance).thenReturn(mockHazelcastInstance);

        when(mockHazelcastInstance.getLifecycleService()).thenReturn(mockLifecycleService);
        when(mockLifecycleService.isRunning()).thenReturn(true);

        lockManager = new HazelcastLockManager();
    }

    @AfterEach
    void tearDown() {
        mockedHazelcast.close();
    }

    @Test
    void testInit() {
        AxArtifactKey testKey = new AxArtifactKey("TestKey", "1.0");

        assertDoesNotThrow(() -> lockManager.init(testKey));

        mockedHazelcast.verify(Hazelcast::newHazelcastInstance);
    }

    @Test
    void testGetReentrantReadWriteLockWhenHazelcastNotRunning() {
        when(mockLifecycleService.isRunning()).thenReturn(false);

        String lockId = "testLock";
        assertThrows(ContextException.class, () -> lockManager.getReentrantReadWriteLock(lockId));
    }

    @Test
    void testShutdown() throws ContextException {
        lockManager.init(new AxArtifactKey("TestKey", "1.0"));
        assertDoesNotThrow(() -> lockManager.shutdown());

        verify(mockHazelcastInstance).shutdown();
    }

    @Test
    void testShutdownWithoutInit() {
        assertDoesNotThrow(() -> lockManager.shutdown());
        verify(mockHazelcastInstance, never()).shutdown();
    }
}
