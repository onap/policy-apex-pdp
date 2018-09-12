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

package org.onap.policy.apex.context.impl.locking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.LockManager;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class implements the {@link LockManager} functionality that is common across all implementations. Lock managers
 * for specific lock mechanisms specialize this class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class AbstractLockManager implements LockManager {
    // Recurring string constants
    private static final String CONTEXT_ITEM = " context item ";

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AbstractLockManager.class);

    // The key of this lock manager
    private AxArtifactKey key = null;

    // Map of locks in use on this distributor for each context map
    private final Map<String, Map<String, ReadWriteLock>> lockMaps = Collections
                    .synchronizedMap(new HashMap<String, Map<String, ReadWriteLock>>());

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.LockManager#init(org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey lockManagerKey) throws ContextException {
        this.key = lockManagerKey;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.LockManager#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#lockForReading(org.onap.policy.apex.core.model.concepts.
     * AxArtifactKey, java.lang.String)
     */
    @Override
    public synchronized void lockForReading(final String lockTypeKey, final String lockKey) throws ContextException {
        LOGGER.entry("lockForReading(" + lockTypeKey + "_" + lockKey + ")");

        // Find the lock or create a new one
        final ReadWriteLock lock = getLock(lockTypeKey, lockKey, true);

        try {
            lock.readLock().lock();
            LOGGER.exit("lockForReading(" + lockTypeKey + "_" + lockKey + ")");
        } catch (final Exception e) {
            LOGGER.warn("error acquiring read lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
            throw new ContextException(
                            "error acquiring read lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#lockForWriting(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized void lockForWriting(final String lockTypeKey, final String lockKey) throws ContextException {
        LOGGER.entry("lockForWriting(" + lockTypeKey + "_" + lockKey + ")");

        // Find the lock or create a new one
        final ReadWriteLock lock = getLock(lockTypeKey, lockKey, true);

        try {
            lock.writeLock().lock();
            LOGGER.exit("lockForWriting(" + lockTypeKey + "_" + lockKey + ")");
        } catch (final Exception e) {
            LOGGER.warn("error acquiring write lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
            throw new ContextException(
                            "error acquiring write lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#unlockForReading(java.lang.String, java.lang.String)
     */
    @Override
    public void unlockForReading(final String lockTypeKey, final String lockKey) throws ContextException {
        LOGGER.entry("unlockForReading(" + lockTypeKey + "_" + lockKey + ")");

        // Find the lock
        final ReadWriteLock lock = getLock(lockTypeKey, lockKey, false);

        try {
            lock.readLock().unlock();
            LOGGER.exit("unlockForReading(" + lockTypeKey + "_" + lockKey + ")");
        } catch (final Exception e) {
            LOGGER.warn("error releasing read lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
            throw new ContextException(
                            "error releasing read lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.LockManager#unlockForWriting(java.lang.String, java.lang.String)
     */
    @Override
    public void unlockForWriting(final String lockTypeKey, final String lockKey) throws ContextException {
        LOGGER.entry("unlockForWriting(" + lockTypeKey + "_" + lockKey + ")");

        // Find the lock
        final ReadWriteLock lock = getLock(lockTypeKey, lockKey, false);

        try {
            lock.writeLock().unlock();
            LOGGER.exit("unlockForWriting(" + lockTypeKey + "_" + lockKey + ")");
        } catch (final Exception e) {
            LOGGER.warn("error releasing write lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
            throw new ContextException(
                            "error releasing write lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey, e);
        }
    }

    /**
     * Get a reentrant read write lock from whatever locking mechanism is in use.
     *
     * @param lockId The unique ID of the lock.
     * @return The lock
     * @throws ContextException On errors getting a lock
     */
    protected abstract ReadWriteLock getReentrantReadWriteLock(String lockId) throws ContextException;

    /**
     * Get a lock for a context item in a context map.
     *
     * @param lockTypeKey The key of the map where the context item to lock is
     * @param lockKey The key on the map to lock
     * @param createMode if true, create a lock if it does not exist
     * @return The lock
     * @throws ContextException On errors getting the lock
     */
    private ReadWriteLock getLock(final String lockTypeKey, final String lockKey, final boolean createMode)
                    throws ContextException {
        // Check if we have a lock type map for this lock type yet
        if (!lockMaps.containsKey(lockTypeKey)) {
            // Create a lock type map for the lock type
            lockMaps.put(lockTypeKey, Collections.synchronizedMap(new HashMap<String, ReadWriteLock>()));
        }

        // Find or create a lock in the lock map
        ReadWriteLock lock = lockMaps.get(lockTypeKey).get(lockKey);
        if (lock != null) {
            return lock;
        }

        // Should we create a lock?
        String errorMessage = "error getting lock on context map " + lockTypeKey + CONTEXT_ITEM + lockKey;
        if (!createMode) {
            String message = errorMessage + ", lock does not exist";
            LOGGER.warn(message);
            throw new ContextException(message);
        }

        try {
            // Create the lock using the specialization of this abstract class
            lock = getReentrantReadWriteLock(lockTypeKey + "_" + lockKey);

            // Add the lock to the lock map
            lockMaps.get(lockTypeKey).put(lockKey, lock);

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("created lock {}_{}", lockTypeKey, lockKey);
            }
            return lock;
        } catch (final Exception e) {
            LOGGER.warn(errorMessage, e);
            throw new ContextException(errorMessage, e);
        }
    }
}
