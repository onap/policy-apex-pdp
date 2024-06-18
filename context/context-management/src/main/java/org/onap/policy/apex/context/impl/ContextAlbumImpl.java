/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2024 Nordix Foundation.
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

package org.onap.policy.apex.context.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.monitoring.ContextMonitor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.common.utils.validation.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ContextAlbumImpl implements the methods on the {@link ContextAlbum} interface. It implements the getters
 * and setters on the {@link Map} and uses the {@link Distributor} to handle distribution and locking.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ContextAlbumImpl implements ContextAlbum, Comparable<ContextAlbumImpl> {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextAlbumImpl.class);

    // Recurring string constants
    private static final String NULL_VALUES_ILLEGAL = "null values are illegal on method parameter \"key\"";
    private static final String ALBUM = "album \"";

    // The definition of this context album
    @Getter
    @EqualsAndHashCode.Include
    private final AxContextAlbum albumDefinition;

    /// The map holding the items and their values for this context album
    @EqualsAndHashCode.Include
    private final Map<String, Object> albumMap;

    // The artifact stack of the artifacts currently using the context album
    @Getter
    @Setter
    private AxConcept[] userArtifactStack = null;

    // The context distributor we are using
    private final Distributor distributor;

    // The schema helper that handles translations of Java objects for this album
    @Getter
    private SchemaHelper schemaHelper;

    // The context monitor for this context album
    private ContextMonitor monitor = null;

    /**
     * Constructor, instantiate the context album.
     *
     * @param albumDefinition The model definition of this context album
     * @param distributor     The context distributor passed to us to distribute context across ContextAlbum instances
     * @param albumMap        the album map
     * @throws ContextException on errors creating context albums
     */
    public ContextAlbumImpl(final AxContextAlbum albumDefinition, final Distributor distributor,
                            final Map<String, Object> albumMap) throws ContextException {
        Assertions.argumentNotNull(albumDefinition, "Context album definition may not be null");
        Assertions.argumentNotNull(distributor, "Distributor may not be null");
        Assertions.argumentNotNull(albumMap, "Album map may not be null");

        this.albumDefinition = albumDefinition;

        // Use the context distributor passed to us
        this.distributor = distributor;

        // The map to use to store objects
        this.albumMap = albumMap;

        try {
            // Get a schema helper to manage the translations between objects on the album map for this album
            schemaHelper = new SchemaHelperFactory().createSchemaHelper(albumDefinition.getKey(),
                albumDefinition.getItemSchema());
        } catch (final ContextRuntimeException e) {
            final var resultString = "could not initiate schema management for context album " + albumDefinition;
            LOGGER.warn(resultString, e);
            throw new ContextException(resultString, e);
        }

        // Create the context monitor
        monitor = new ContextMonitor();

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return albumDefinition.getKey();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return albumDefinition.getKey().getName();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void lockForReading(final String keyOnAlbum) throws ContextException {
        distributor.lockForReading(albumDefinition.getKey(), keyOnAlbum);
        monitor.monitorReadLock(albumDefinition.getKey(), albumDefinition.getItemSchema(), keyOnAlbum,
            userArtifactStack);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void lockForWriting(final String keyOnAlbum) throws ContextException {
        distributor.lockForWriting(albumDefinition.getKey(), keyOnAlbum);
        monitor.monitorWriteLock(albumDefinition.getKey(), albumDefinition.getItemSchema(), keyOnAlbum,
            userArtifactStack);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForReading(final String keyOnAlbum) throws ContextException {
        distributor.unlockForReading(albumDefinition.getKey(), keyOnAlbum);
        monitor.monitorReadUnlock(albumDefinition.getKey(), albumDefinition.getItemSchema(), keyOnAlbum,
            userArtifactStack);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForWriting(final String keyOnAlbum) throws ContextException {
        distributor.unlockForWriting(albumDefinition.getKey(), keyOnAlbum);
        monitor.monitorWriteUnlock(albumDefinition.getKey(), albumDefinition.getItemSchema(), keyOnAlbum,
            userArtifactStack);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void flush() throws ContextException {
        distributor.flushContextAlbum(this);
    }

    /*
     * The Map interface
     */

    /**
     * {@inheritDoc}.
     */
    @Override
    public int size() {
        return albumMap.size();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isEmpty() {
        return albumMap.isEmpty();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            LOGGER.warn(NULL_VALUES_ILLEGAL);
            throw new ContextRuntimeException(NULL_VALUES_ILLEGAL);
        }

        return albumMap.containsKey(key);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            LOGGER.warn("null values are illegal on method parameter \"value\"");
            throw new ContextRuntimeException("null values are illegal on method parameter \"value\"");
        }

        return albumMap.containsValue(value);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object get(final Object key) {
        if (key == null) {
            final var returnString =
                ALBUM + albumDefinition.getId() + "\" null keys are illegal on keys for get()";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        final Object item = albumMap.get(key);
        if (item == null) {
            return null;
        }

        // Get the context value and monitor it
        monitor.monitorGet(albumDefinition.getKey(), albumDefinition.getItemSchema(), key.toString(), item,
            userArtifactStack);
        return item;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<String> keySet() {
        return albumMap.keySet();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<Object> values() {
        // Build the key set and return it
        final ArrayList<Object> valueList = new ArrayList<>();

        for (final Entry<String, Object> contextAlbumEntry : albumMap.entrySet()) {
            final Object item = contextAlbumEntry.getValue();
            monitor.monitorGet(albumDefinition.getKey(), albumDefinition.getItemSchema(), contextAlbumEntry.getKey(),
                item, userArtifactStack);
            valueList.add(contextAlbumEntry.getValue());
        }

        return valueList;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        // Build the entry set and return it
        final Set<Entry<String, Object>> entrySet = new HashSet<>();

        for (final Entry<String, Object> contextAlbumEntry : albumMap.entrySet()) {
            final Object item = contextAlbumEntry.getValue();
            monitor.monitorGet(albumDefinition.getKey(), albumDefinition.getItemSchema(), contextAlbumEntry.getKey(),
                item, userArtifactStack);
            entrySet.add(new SimpleEntry<>(contextAlbumEntry.getKey(), contextAlbumEntry.getValue()));
        }

        return entrySet;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object put(final String key, final Object incomingValue) {
        if (key == null) {
            final var returnString =
                ALBUM + albumDefinition.getId() + "\" null keys are illegal on keys for put()";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        if (incomingValue == null) {
            final var returnString = ALBUM + albumDefinition.getId() + "\" null values are illegal on key \""
                + key + "\" for put()";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        if (!albumDefinition.isWritable()) {
            final var returnString = ALBUM + albumDefinition.getId()
                + "\" put() not allowed on read only albums for key=\"" + key + "\", value=\"" + incomingValue;
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        try {
            // Translate the object to a schema object
            final Object valueToPut = schemaHelper.unmarshal(incomingValue);

            // Check if the key is already in the map
            if (albumMap.containsKey(key)) {
                // Update the value in the context item and in the context value map
                monitor.monitorSet(albumDefinition.getKey(), albumDefinition.getItemSchema(), key, incomingValue,
                    userArtifactStack);
            } else {
                // Update the value in the context item and in the context value map
                monitor.monitorInit(albumDefinition.getKey(), albumDefinition.getItemSchema(), key, incomingValue,
                    userArtifactStack);
            }

            // Put the translated value on the map and return the old map value
            return albumMap.put(key, valueToPut);
        } catch (final ContextRuntimeException e) {
            final var returnString = "Failed to set context value for key \"" + key + "\" in album \""
                + albumDefinition.getId() + "\": " + e.getMessage();
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void putAll(@NonNull final Map<? extends String, ?> incomingContextAlbum) {
        if (!albumDefinition.isWritable()) {
            final var returnString =
                ALBUM + albumDefinition.getId() + "\" putAll() not allowed on read only albums";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Sanity check on incoming context
        Assertions.argumentOfClassNotNull(incomingContextAlbum, ContextRuntimeException.class,
            "cannot update context, context album is null");

        // Iterate over the incoming context
        for (final Entry<String, Object> entry : albumMap.entrySet()) {
            synchronized (albumDefinition) {
                // Get the key for the incoming name
                final Object incomingDataItem = incomingContextAlbum.get(entry.getKey());
                if (incomingDataItem != null) {
                    // Update the value the context album
                    put(entry.getKey(), incomingDataItem);
                }
            }
        }

        // Put all the objects on the context album
        for (final Entry<? extends String, ?> incomingMapEntry : incomingContextAlbum.entrySet()) { // NOSONAR
            // Put the entry on the map
            this.put(incomingMapEntry.getKey(), incomingMapEntry.getValue());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object remove(final Object key) {
        if (!albumDefinition.isWritable()) {
            final var returnString = ALBUM + albumDefinition.getId()
                + "\" remove() not allowed on read only albums for key=\"" + key + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        if (key == null) {
            LOGGER.warn(NULL_VALUES_ILLEGAL);
            throw new ContextRuntimeException("null values are illegal on method parameter \"keyID\"");
        }

        // Delete the item
        final Object removedValue = albumMap.remove(key);
        monitor.monitorDelete(albumDefinition.getKey(), albumDefinition.getItemSchema(), key.toString(), removedValue,
            userArtifactStack);

        // Return the value of the deleted item
        return removedValue;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear() {
        if (!albumDefinition.isWritable()) {
            final var returnString =
                ALBUM + albumDefinition.getId() + "\" clear() not allowed on read only albums";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Monitor deletion of each item
        for (final Entry<String, Object> contextAlbumEntry : albumMap.entrySet()) {
            final Object item = contextAlbumEntry.getValue();
            monitor.monitorDelete(albumDefinition.getKey(), albumDefinition.getItemSchema(), contextAlbumEntry.getKey(),
                item, userArtifactStack);
        }

        // Clear the map
        albumMap.clear();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(ContextAlbumImpl otherContextAlbumImpl) {
        return (equals(otherContextAlbumImpl) ? 0 : 1);
    }
}
