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

package org.onap.policy.apex.context.impl.distribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    // The key of this distributor
    private AxArtifactKey key = null;

    // The context albums for this context set indexed by their keys
    private static Map<AxArtifactKey, ContextAlbum> albumMaps =
            Collections.synchronizedMap(new HashMap<AxArtifactKey, ContextAlbum>());

    // Lock manager for this distributor
    private static LockManager lockManager = null;

    // Hold a persistor for this distributor
    private Persistor persistor = null;

    // Hold a flush timer for this context distributor
    private static DistributorFlushTimerTask flushTimer = null;

    /**
     * Create an instance of an abstract Context Distributor.
     */
    public AbstractDistributor() {
        LOGGER.entry("AbstractContextDistributor()");
        LOGGER.exit("AbstractContextDistributor()");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.ContextDistributor#init(org.onap.policy.apex.model.basicmodel.concepts.
     * AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey distributorKey) throws ContextException {
        LOGGER.entry("init(" + distributorKey + ")");

        // Record parameters and key
        this.key = distributorKey;

        // Create the lock manager if it doesn't already exist
        if (lockManager == null) {
            lockManager = new LockManagerFactory().createLockManager(key);
        }

        // Set up flushing on the context distributor if its not set up already
        if (flushTimer == null) {
            flushTimer = new DistributorFlushTimerTask(this);
        }

        // Create a new persistor for this key
        persistor = new PersistorFactory().createPersistor(key);
        LOGGER.exit("init(" + key + ")");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.ContextDistributor#shutdown()
     */
    @Override
    public abstract void shutdown();

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.ContextDistributor#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /**
     * Create a context album using whatever underlying mechanism we are using for albums.
     *
     * @param contextAlbumKey The key of the album
     * @return The album as a string-object map
     */
    public abstract Map<String, Object> getContextAlbumMap(AxArtifactKey contextAlbumKey);


    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.Distributor#registerModel(org.onap.policy.apex.model.contextmodel.concepts.
     * AxContextModel)
     */
    @Override
    public void registerModel(final AxContextModel contextModel) throws ContextException {
        ModelService.registerModel(AxKeyInformation.class, contextModel.getKeyInformation());
        ModelService.registerModel(AxContextSchemas.class, contextModel.getSchemas());
        ModelService.registerModel(AxContextAlbums.class, contextModel.getAlbums());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.ContextDistributor#createContextAlbum(org.onap.policy.apex.core.basicmodel.
     * concepts. AxArtifactKey)
     */
    @Override
    public synchronized ContextAlbum createContextAlbum(final AxArtifactKey axContextAlbumKey) throws ContextException {
        // Get the context album definition
        final AxContextAlbum album = ModelService.getModel(AxContextAlbums.class).get(axContextAlbumKey);
        if (album == null) {
            final String resultString = "context album " + axContextAlbumKey.getID() + " does not exist";
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        // Check if the context album is valid
        final AxValidationResult result = album.validate(new AxValidationResult());
        if (!result.isValid()) {
            final String resultString =
                    "context album definition for " + album.getKey().getID() + " is invalid" + result;
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        // Get the schema of the context album
        final AxContextSchema schema = ModelService.getModel(AxContextSchemas.class).get(album.getItemSchema());
        if (schema == null) {
            final String resultString = "schema \"" + album.getItemSchema().getID() + "\" for context album "
                    + album.getKey().getID() + " does not exist";
            LOGGER.warn(resultString);
            throw new ContextException(resultString);
        }

        // Check if the map has already been instantiated
        if (!albumMaps.containsKey(album.getKey())) {
            // Instantiate the album map for this context album that we'll distribute using the distribution mechanism
            final Map<String, Object> newContextAlbumMap = getContextAlbumMap(album.getKey());

            // The distributed context album will have content from another process instance if the album exists in
            // another process,
            // if not, we have to try to read the content from persistence
            if (newContextAlbumMap.isEmpty()) {
                // Read entries from persistence
                // TODO: READ ITEMS FROM PRESISTENCE!!!!
            }

            // Create the context album and put the context album object onto the distributor
            albumMaps.put(album.getKey(), new ContextAlbumImpl(album, this, newContextAlbumMap));
        }

        return albumMaps.get(album.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.ContextDistributor#removeContextAlbum(org.onap.policy.apex.core.basicmodel.
     * concepts. AxArtifactKey)
     */
    @Override
    public void removeContextAlbum(final AxContextAlbum contextAlbum) throws ContextException {
        // Check if the map already exists, if not return
        if (!albumMaps.containsKey(contextAlbum.getKey())) {
            LOGGER.warn("map remove failed, supplied map is null");
            throw new ContextException("map update failed, supplied map is null");
        }

        // Remove the map from the distributor
        albumMaps.remove(contextAlbum.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#flush()
     */
    @Override
    public void flush() throws ContextException {
        // Flush all the maps
        for (final Entry<AxArtifactKey, ContextAlbum> distributorMapEntry : albumMaps.entrySet()) {
            // Let the persistor write each of the entries
            for (final Object contextItem : distributorMapEntry.getValue().values()) {
                LOGGER.debug(contextItem.toString());
                // persistor.writeContextItem((AxContextSchema) contextItem);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#flushContextAlbum(org.onap.policy.apex.core.context.
     * ContextAlbum)
     */
    @Override
    public void flushContextAlbum(final ContextAlbum contextAlbum) throws ContextException {
        // Check if the map already exists, if not return
        if (!albumMaps.containsKey(contextAlbum.getKey())) {
            LOGGER.warn("map flush failed, supplied map is null");
            throw new ContextException("map flush failed, supplied map is null");
        }

        // Let the persistor flush the items on the map
        for (final Object contextItem : albumMaps.get(contextAlbum.getKey()).values()) {
            persistor.writeContextItem((AxContextSchema) contextItem);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#lockForReading(java.lang.String)
     */
    @Override
    public synchronized void lockForReading(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Lock using the lock manager
        lockManager.lockForReading(mapKey.getID(), itemKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#lockForWriting(java.lang.String)
     */
    @Override
    public synchronized void lockForWriting(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Lock using the lock manager
        lockManager.lockForWriting(mapKey.getID(), itemKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#unlockForReading(java.lang.String)
     */
    @Override
    public void unlockForReading(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Unlock using the lock manager
        lockManager.unlockForReading(mapKey.getID(), itemKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#unlockForWriting(java.lang.String)
     */
    @Override
    public void unlockForWriting(final AxArtifactKey mapKey, final String itemKey) throws ContextException {
        // Unlock using the lock manager
        lockManager.unlockForWriting(mapKey.getID(), itemKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.ContextDistributor#clear()
     */
    @Override
    public void clear() {
        // Shut down the lock manager
        if (lockManager != null) {
            lockManager.shutdown();
            lockManager = null;
        }

        albumMaps.clear();

        // Turn off the flush timer
        flushTimer.cancel();

        // Shut down the specialization of the context distributor
        shutdown();
    }
}
