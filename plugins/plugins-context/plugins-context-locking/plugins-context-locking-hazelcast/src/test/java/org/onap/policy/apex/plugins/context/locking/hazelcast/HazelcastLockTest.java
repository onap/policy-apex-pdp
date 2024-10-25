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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import java.util.concurrent.locks.Lock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HazelcastLockTest {

    private HazelcastInstance hazelcastInstance;
    private CPSubsystem cpSubsystem;
    private FencedLock mockReadLock;
    private FencedLock mockWriteLock;

    private HazelcastLock hazelcastLock;
    private final String lockId = "testLock";

    @BeforeEach
    void setUp() {
        hazelcastInstance = mock(HazelcastInstance.class);
        cpSubsystem = mock(CPSubsystem.class);
        mockReadLock = mock(FencedLock.class);
        mockWriteLock = mock(FencedLock.class);

        when(hazelcastInstance.getCPSubsystem()).thenReturn(cpSubsystem);
        when(cpSubsystem.getLock(lockId + "_READ")).thenReturn(mockReadLock);
        when(cpSubsystem.getLock(lockId + "_WRITE")).thenReturn(mockWriteLock);

        hazelcastLock = new HazelcastLock(hazelcastInstance, lockId);
    }

    @Test
    void testConstructor() {
        assertEquals(lockId, hazelcastLock.getLockId());

        verify(cpSubsystem).getLock(lockId + "_READ");
        verify(cpSubsystem).getLock(lockId + "_WRITE");
    }

    @Test
    void testReadLock() {
        Lock readLock = hazelcastLock.readLock();
        assertEquals(mockReadLock, readLock);
    }

    @Test
    void testWriteLock() {
        Lock writeLock = hazelcastLock.writeLock();
        assertEquals(mockWriteLock, writeLock);
    }
}
