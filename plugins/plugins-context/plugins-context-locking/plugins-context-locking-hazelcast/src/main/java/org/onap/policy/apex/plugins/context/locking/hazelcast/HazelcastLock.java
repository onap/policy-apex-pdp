/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import lombok.Getter;

/**
 * This class maps a Hazelcast {@link ILock} to a Java {@link ReadWriteLock}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class HazelcastLock implements ReadWriteLock {
    // The Lock ID
    @Getter
    private final String lockId;

    // The hazelcast lock
    private final FencedLock readLock;
    private final FencedLock writeLock;

    /**
     * Create a Hazelcast lock.
     *
     * @param hazelcastInstance the hazelcast instance to use to create the lock
     * @param lockId The unique ID of the lock.
     */
    public HazelcastLock(final HazelcastInstance hazelcastInstance, final String lockId) {
        this.lockId = lockId;

        // Create the Hazelcast read and write locks
        readLock = hazelcastInstance.getCPSubsystem().getLock(lockId + "_READ");
        writeLock = hazelcastInstance.getCPSubsystem().getLock(lockId + "_WRITE");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Lock readLock() {
        return readLock;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Lock writeLock() {
        return writeLock;
    }
}
