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

import java.util.Map;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;

/**
 * The Interface ContextAlbum is implemented by all classes that manage context in Apex. Context albums may store
 * context in memory, on disk, in a repository or in a mechanism such as a distributed map.
 * <p>
 * A context album uses plugins to handle its context schemas, its distribution, its locking, and its persistence.
 * <p>
 * The schema that defines the items in a context album is interpreted by a plugin that implements the
 * {@link SchemaHelper} interface. The schema helper uses the schema definition to provide new instances for a context
 * album. By default, context albums use Java schemas.
 * <p>
 * Context albums may be shared across an arbitrary number of JVMs using a distribution mechanism. Apex context
 * distributed context albums using plugins that implement the {@link Distributor} interface. By default, context albums
 * use JVM local distribution, that is context albums are only available in a single JVM
 * <p>
 * Items in a context album may be locked across all distributed instances of an album. Apex locks instances on context
 * albums using the distributed locking mechanism in a plugin that implements the {@link LockManager} interface. By
 * default, context albums use Java locking local to a single JVM on each context album instance.
 * <p>
 * Context albums may be persisted to disk, database, or any other repository. Apex persists context albums using the
 * persistence mechanism in a plugin that implements the {@link Persistor} interface. By default, context albums use a
 * dummy persistor plugin that does not persist context albums.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ContextAlbum extends Map<String, Object> {
    /**
     * Gets the key of the context album instance.
     *
     * @return the key
     */
    AxArtifactKey getKey();

    /**
     * Gets the name of the context album instance.
     *
     * @return the name
     */
    String getName();

    /**
     * Get the current context album with values.
     *
     * @return the current context runtime values
     */
    AxContextAlbum getAlbumDefinition();

    /**
     * Get the schema helper for the technology that is handling the schema for this album.
     *
     * @return the schema helper
     */
    SchemaHelper getSchemaHelper();

    /**
     * Place a read lock on a key in this album across the entire cluster.
     *
     * @param key The key to lock
     * @throws ContextException on locking errors
     */
    void lockForReading(String key) throws ContextException;

    /**
     * Place a write lock on a key in this album across the entire cluster.
     *
     * @param key The key to lock
     * @throws ContextException on locking errors
     */
    void lockForWriting(String key) throws ContextException;

    /**
     * Release the the read lock on a key in this album across the entire cluster.
     *
     * @param key The key to unlock
     * @throws ContextException on locking errors
     */
    void unlockForReading(String key) throws ContextException;

    /**
     * Release the the write lock on a key in this album across the entire cluster.
     *
     * @param key The key to unlock
     * @throws ContextException on locking errors
     */
    void unlockForWriting(String key) throws ContextException;

    /**
     * Get the stack of artifact keys currently using this context item.
     *
     * @return the keys of the artifacts using the context album at the moment
     */
    AxConcept[]  getUserArtifactStack();

    /**
     * Set the stack of artifact keys currently using this context item.
     *
     * @param userArtifactStack the keys of the artifacts using the context album at the moment
     */
    void setUserArtifactStack(AxConcept[] userArtifactStack);

    /**
     * Flush the context album to the distribution and persistence mechanism.
     *
     * @throws ContextException On context flush errors
     */
    void flush() throws ContextException;
}
