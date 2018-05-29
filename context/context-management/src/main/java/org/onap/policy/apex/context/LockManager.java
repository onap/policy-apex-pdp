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

package org.onap.policy.apex.context;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * This interface provides a facade to hide implementation details of various lock managers that may be used to manage
 * locking of context items.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface LockManager {

    /**
     * Initialize the lock manager with its properties.
     *
     * @param key The key of this lock manager
     * @throws ContextException On errors initializing the persistor
     */
    void init(AxArtifactKey key) throws ContextException;

    /**
     * Get the key of the lock manager.
     *
     * @return the managers key
     */
    AxArtifactKey getKey();

    /**
     * Place a read lock on a lock type and key across the entire cluster.
     *
     * @param lockTypeKey The key of the map where the context item to lock is
     * @param lockKey The key on the map to lock
     * @throws ContextException on locking errors
     */
    void lockForReading(String lockTypeKey, String lockKey) throws ContextException;

    /**
     * Place a write lock on a lock type and key across the entire cluster.
     *
     * @param lockTypeKey The key of the map where the context item to lock is
     * @param lockKey The key on the map to lock
     * @throws ContextException on locking errors
     */
    void lockForWriting(String lockTypeKey, String lockKey) throws ContextException;

    /**
     * Release a read lock on a lock type and key across the entire cluster.
     *
     * @param lockTypeKey The key of the map where the context item to lock is
     * @param lockKey The key on the map to lock
     * @throws ContextException on locking errors
     */
    void unlockForReading(String lockTypeKey, String lockKey) throws ContextException;

    /**
     * Release a write lock on a lock type and key across the entire cluster.
     *
     * @param lockTypeKey The key of the map where the context item to lock is
     * @param lockKey The key on the map to lock
     * @throws ContextException on locking errors
     */
    void unlockForWriting(String lockTypeKey, String lockKey) throws ContextException;

    /**
     * Shut down the lock manager and clear any connections or data it is using.
     */
    void shutdown();
}
