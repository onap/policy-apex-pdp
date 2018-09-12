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

package org.onap.policy.apex.model.contextmodel.concepts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetterImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class is a context album container and holds a map of the context albums for an entire Apex model. All Apex
 * models that use context albums must have an {@link AxContextAlbums} field. The {@link AxContextAlbums} class
 * implements the helper methods of the {@link AxConceptGetter} interface to allow {@link AxContextAlbum} instances to
 * be retrieved by calling methods directly on this class without referencing the contained map.
 * 
 * <p>Validation checks that the container key is not null. An observation is issued if no context albums are defined in
 * the container. If context albums do exist, they are checked to ensure that keys and values are not null and that the
 * map key matches the key in the map value for all album entries. Each context album entry is then validated
 * individually.
 */
@Entity
@Table(name = "AxContextAlbums")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxContextAlbums", namespace = "http://www.onap.org/policy/apex-pdp", propOrder =
    { "key", "albums" })

public final class AxContextAlbums extends AxConcept implements AxConceptGetter<AxContextAlbum> {
    private static final long serialVersionUID = -4844259809024470975L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(joinColumns = {@JoinColumn(name = "contextName", referencedColumnName = "name"),
            @JoinColumn(name = "contextVersion", referencedColumnName = "version")})
    @XmlElement(name = "albums", required = true)
    private Map<AxArtifactKey, AxContextAlbum> albums;
    // @formatter:on

    /**
     * The Default Constructor creates a {@link AxContextAlbums} object with a null artifact key and creates an empty
     * context album map.
     */
    public AxContextAlbums() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxContextAlbums(final AxContextAlbums copyConcept) {
        super(copyConcept);
    }

    /**
     * The Key Constructor creates a {@link AxContextAlbums} object with the given artifact key and creates an empty
     * context album map.
     *
     * @param key the key of the context album container
     */
    public AxContextAlbums(final AxArtifactKey key) {
        this(key, new TreeMap<AxArtifactKey, AxContextAlbum>());
    }

    /**
     * Constructor that creates the context album map with the given albums and key.
     *
     * @param key the key of the context album container
     * @param albums the context albums to place in this context album container
     */
    public AxContextAlbums(final AxArtifactKey key, final Map<AxArtifactKey, AxContextAlbum> albums) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(albums, "albums may not be null");

        this.key = key;
        this.albums = new TreeMap<>();
        this.albums.putAll(albums);
    }

    /**
     * When a model is unmarshalled from disk or from the database, the context album map is returned as a raw hash map.
     * This method is called by JAXB after unmarshaling and is used to convert the hash map to a {@link NavigableMap} so
     * that it will work with the {@link AxConceptGetter} interface.
     *
     * @param unmarsaller the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller unmarsaller, final Object parent) {
        Assertions.argumentNotNull(unmarsaller, "unmarsaller should not be null");
        Assertions.argumentNotNull(parent, "parent should not be null");

        // The map must be navigable to allow name and version searching, unmarshaling returns a
        // hash map
        final NavigableMap<AxArtifactKey, AxContextAlbum> navigableAlbums = new TreeMap<>();
        navigableAlbums.putAll(albums);
        albums = navigableAlbums;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();

        for (final AxContextAlbum contextAlbum : albums.values()) {
            keyList.addAll(contextAlbum.getKeys());
        }

        return keyList;
    }

    /**
     * Sets the key of the context album container.
     *
     * @param key the context album container key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the map of context albums from the context album container.
     *
     * @return the context album map
     */
    public Map<AxArtifactKey, AxContextAlbum> getAlbumsMap() {
        return albums;
    }

    /**
     * Sets the map of context albums from the context album container.
     *
     * @param albumsMap the map of context albums to place in the container
     */
    public void setAlbumsMap(final Map<AxArtifactKey, AxContextAlbum> albumsMap) {
        Assertions.argumentNotNull(albumsMap, "albums may not be null");
        this.albums = new TreeMap<>();
        this.albums.putAll(albumsMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxContextAlbum> contextAlbumEntry : albums.entrySet()) {
            contextAlbumEntry.getKey().clean();
            contextAlbumEntry.getValue().clean();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("key=");
        builder.append(key);
        builder.append(",albums=");
        builder.append(albums);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key is a null key"));
        }

        result = key.validate(result);

        if (albums.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                            "albums are empty"));
        } else {
            for (final Entry<AxArtifactKey, AxContextAlbum> contextAlbumEntry : albums.entrySet()) {
                if (contextAlbumEntry.getKey().equals(AxArtifactKey.getNullKey())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "key on context album entry " + contextAlbumEntry.getKey()
                                                    + " may not be the null key"));
                } else if (contextAlbumEntry.getValue() == null) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "value on context album entry " + contextAlbumEntry.getKey() + " may not be null"));
                } else {
                    validateContextAlbumKey(result, contextAlbumEntry);

                    result = contextAlbumEntry.getValue().validate(result);
                }
            }
        }

        return result;
    }

    private void validateContextAlbumKey(final AxValidationResult result,
                    final Entry<AxArtifactKey, AxContextAlbum> contextAlbumEntry) {
        if (!contextAlbumEntry.getKey().equals(contextAlbumEntry.getValue().getKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key on context album entry key " + contextAlbumEntry.getKey()
                                            + " does not equal context album value key "
                                            + contextAlbumEntry.getValue().getKey()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxContextAlbums.class);

        final AxContextAlbums copy = ((AxContextAlbums) copyObject);
        copy.setKey(key);
        final Map<AxArtifactKey, AxContextAlbum> newContextAlbum = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxContextAlbum> contextAlbumEntry : albums.entrySet()) {
            newContextAlbum.put(new AxArtifactKey(contextAlbumEntry.getKey()),
                            new AxContextAlbum(contextAlbumEntry.getValue()));
        }
        copy.setAlbumsMap(newContextAlbum);

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + albums.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxContextAlbums other = (AxContextAlbums) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return albums.equals(other.albums);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxContextAlbums other = (AxContextAlbums) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!albums.equals(other.albums)) {
            return (albums.hashCode() - other.albums.hashCode());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(org.onap.policy.apex.
     * model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public AxContextAlbum get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextAlbum>) albums).get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String)
     */
    @Override
    public AxContextAlbum get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextAlbum>) albums).get(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String, java.lang.String)
     */
    @Override
    public AxContextAlbum get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextAlbum>) albums).get(conceptKeyName,
                        conceptKeyVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String)
     */
    @Override
    public Set<AxContextAlbum> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextAlbum>) albums).getAll(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String, java.lang.String)
     */
    @Override
    public Set<AxContextAlbum> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextAlbum>) albums).getAll(conceptKeyName,
                        conceptKeyVersion);
    }
}
