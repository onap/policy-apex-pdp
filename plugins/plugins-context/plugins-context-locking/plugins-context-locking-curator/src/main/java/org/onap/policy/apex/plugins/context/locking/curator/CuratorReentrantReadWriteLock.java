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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

/**
 * This class maps a Curator {@link InterProcessReadWriteLock} to a Java {@link ReadWriteLock}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CuratorReentrantReadWriteLock implements ReadWriteLock {
    // The Lock ID
    private final String lockId;

    // The Curator lock
    private final InterProcessReadWriteLock curatorReadWriteLock;

    // The Curator Lock facades for read and write locks
    private final CuratorLockFacade readLockFacade;
    private final CuratorLockFacade writeLockFacade;

    /**
     * Create a Curator lock.
     *
     * @param curatorFramework the Curator framework to use to create the lock
     * @param lockId The unique ID of the lock.
     */
    public CuratorReentrantReadWriteLock(final CuratorFramework curatorFramework, final String lockId) {
        this.lockId = lockId;

        // Create the Curator lock
        curatorReadWriteLock = new InterProcessReadWriteLock(curatorFramework, lockId);

        // Create the lock facades
        readLockFacade = new CuratorLockFacade(curatorReadWriteLock.readLock(), lockId);
        writeLockFacade = new CuratorLockFacade(curatorReadWriteLock.writeLock(), lockId);
    }

    /**
     * Get the lock Id of the lock.
     *
     * @return the lock ID
     */
    public String getLockId() {
        return lockId;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.locks.ReadWriteLock#readLock()
     */
    @Override
    public Lock readLock() {
        return readLockFacade;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.locks.ReadWriteLock#writeLock()
     */
    @Override
    public Lock writeLock() {
        return writeLockFacade;
    }
}
