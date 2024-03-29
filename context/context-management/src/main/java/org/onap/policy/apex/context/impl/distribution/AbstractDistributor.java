/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.context.impl.distribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.LockManager;
import org.onap.policy.apex.context.Persistor;
import org.onap.policy.apex.context.impl.ContextAlbumImpl;
import org.onap.policy.apex.context.impl.locking.LockManagerFactory;
import org.onap.policy.apex.context.impl.persistence.PersistorFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This context distributor implements the mechanism-neutral parts of a context distributor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class AbstractDistributor implements Distributor {

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AbstractDistributor.class);

    // The context albums for this context set indexed by their keys
    private static Map<AxArtifactKey, ContextAlbum> albumMaps = Collections
                    .synchronizedMap(new HashMap<AxArtifactKey, ContextAlbum>());

    // Lock manager for this distributor
    @Setter(AccessLevel.PRIVATE)
    private static LockManager lockManager = null;

    // Hold a flush timer for this context distributor
    @Setter(AccessLevel.PRIVATE)
    private static DistributorFlushTimerTask flushTimer = null;

    // The key of this distributor
    @Getter
    private AxArtifactKey key = null;

    // Hold a persistor for this distributor
    private Persistor persistor = null;

    /**
     * Create an instance of an abstract Context Distributor.
     */
    protected AbstractDistributor() {
        LOGGER.entry("AbstractContextDistributor()");
        LOGGER.exit("AbstractContextDistributor()");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final AxArtifactKey distributorKey) throws ContextException {
        LOGGER.entry("init(" + distributorKey + ")");

        // Record parameters and key
        this.key = distributorKey;

        // Create the lock manager if it doesn't already exist
        if (lockManager == null) {
            setLockManager(new LockManagerFactory().createLockManager(key));
        }

        // Set up flushing on the context distributor if its not set up already
        if (flushTimer == null) {
            setFlushTimer(new DistributorFlushTimerTask(this));
        }

        // Create a new persistor for this key
        persistor = new PersistorFactory().createPersistor(key);
        LOGGER.exit("init(" + key + ")");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public abstract void shutdown();

    /**
     * Create a context album using whatever underlying mechanism we are using for albums.
     *
     * @param contextAlbumKey The key of the album
     * @return The album as a string-object map
     */
    public abstract Map<String, Object> getContextAlbumMap(AxArtifactKey contextAlbumKey);

    /**
     * {@inheritDoc}.
     */
    @Override
    public void registerModel(final AxContextModel contextModel) throws ContextException {
        ModelService.registerModel(AxKeyInformation.class, contextModel.getKeyInformation());
        ModelService.registerModel(AxContextSchemas.class, contextModel.getSchemas());
        ModelService.registerModel(AxContextAlbums.class, contextModel.getAlbums());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public synchronized ContextAlbum createContextAlbum(final AxArtifactKey axContextAlbumKey) throws ContextException {
        // Get the context album definition
        final AxContextAlbum album = ModelService.getModel(AxContextAlbums.class).get(axContextAlbumKey);
        if (album == null) {
            final var resultString = "context album " + axContextAlbumKey.getId() + " does not exist";
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        // Check if the context album is valid
        final AxValidationResult result = album.validate(new AxValidationResult());
        if (!result.isValid()) {
            final var resultString = "context album definition for " + album.getKey().getId() + " is invalid"
                            + result;
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        // Get the schema of the context album
        final AxContextSchema schema = ModelService.getModel(AxContextSchemas.class).get(album.getItemSchema());
        if (schema == null) {
            final var resultString = "schema \"" + album.getItemSchema().getId() + "\" for context album "
                            + album.getKey().getId() + " does not exist";
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        synchronized (albumMaps) {
            // Check if the map has already been instantiated
            if (!albumMaps.containsKey(album.getKey())) {
                // Instantiate the album map for this context album that we'll distribute using the distribution
                // mechanism
                final Map<String, Object> newContextAlbumMap = getContextAlbumMap(album.getKey());

                // The distributed context album will have content from another process instance if the album exists in
                // another process, if not, we have to try to read the content from persistence
                if (newContextAlbumMap.isEmpty()) {
                    // Read entries from persistence, (Not implemented yet)
                }

                // Create the context album and put the context album object onto the distributor
                albumMaps.put(album.getKey(), new ContextAlbumImpl(album, this, newContextAlbumMap));
            }

            return albumMaps.get(album.getKey());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void removeContextAlbum(final AxArtifactKey axContextAlbumKey) throws ContextException {
        synchronized (albumMaps) {
            // Remove the map from the distributor
            if (null == albumMaps.remove(axContextAlbumKey)) {
                throw new ContextException("map update failed, supplied map is null");
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void flush() throws ContextException {
        synchronized (albumMaps) {
            // Flush all the maps
            for (final Entry<AxArtifactKey, ContextAlbum> distributorMapEntry : albumMaps.entrySet()) {
                // Let the persistor write each of the entries
                for (final Object contextItem : distributorMapEntry.getValue().values()) {
                    persistor.writeContextItem(contextItem);
                }
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void flushContextAlbum(final ContextAlbum contextAlbum) throws ContextException {
        synchronized (albumMaps) {
            // Check if the map already exists, if not return
            if (!albumMaps.containsKey(contextAlbum.getKey())) {
                LOGGER.warn("map flush failed, supplied map is null");
                throw new ContextException("map flush failed, supplied map is null");
            }

            // Let the persistor flush the items on the map
            for (final Object contextItem : albumMaps.get(contextAlbum.getKey()).values()) {
                persistor.writeContextItem(contextItem);
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public synchronized void lockForReading(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Lock using the lock manager
        lockManager.lockForReading(mapKey.getId(), itemKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public synchronized void lockForWriting(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Lock using the lock manager
        lockManager.lockForWriting(mapKey.getId(), itemKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForReading(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Unlock using the lock manager
        lockManager.unlockForReading(mapKey.getId(), itemKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForWriting(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Unlock using the lock manager
        lockManager.unlockForWriting(mapKey.getId(), itemKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear() {
        // Shut down the lock manager
        if (lockManager != null) {
            lockManager.shutdown();
            setLockManager(null);
        }

        synchronized (albumMaps) {
            albumMaps.clear();
        }

        // Turn off the flush timer
        flushTimer.cancel();

        // Shut down the specialization of the context distributor
        shutdown();
    }
}
