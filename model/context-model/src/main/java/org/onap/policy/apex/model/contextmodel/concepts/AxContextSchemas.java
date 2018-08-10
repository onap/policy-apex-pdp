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
import javax.persistence.ManyToMany;
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
 * This class is a context schema container and holds a map of the context schemas for an entire
 * Apex model. All Apex models that use context schemas must have an {@link AxContextSchemas} field.
 * The {@link AxContextSchemas} class implements the helper methods of the {@link AxConceptGetter}
 * interface to allow {@link AxContextSchema} instances to be retrieved by calling methods directly
 * on this class without referencing the contained map.
 * 
 * <p>Validation checks that the container key is not null. An error is issued if no context schemas
 * are defined in the container. Each context schema entry is checked to ensure that its key and
 * value are not null and that the key matches the key in the map value. Each context schema entry
 * is then validated individually.
 */
@Entity
@Table(name = "AxContextSchemas")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxContextSchemas", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = { "key", "schemas" })

public class AxContextSchemas extends AxConcept implements AxConceptGetter<AxContextSchema> {
    private static final long serialVersionUID = -3203734282886453582L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "contextSchemasName", referencedColumnName = "name"),
                    @JoinColumn(name = "contextSchemasVersion", referencedColumnName = "version")},
            inverseJoinColumns = {@JoinColumn(name = "contextSchemaName", referencedColumnName = "name"),
                    @JoinColumn(name = "contextSchemaVersion", referencedColumnName = "version")})
    @XmlElement(name = "schemas", required = true)
    private Map<AxArtifactKey, AxContextSchema> schemas;
    // @formatter:on

    /**
     * The Default Constructor creates a {@link AxContextSchemas} object with a null artifact key and creates an empty
     * context schemas map.
     */
    public AxContextSchemas() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxContextSchemas(final AxContextSchemas copyConcept) {
        super(copyConcept);
    }

    /**
     * The Key Constructor creates a {@link AxContextSchemas} object with the given artifact key and creates an empty
     * context schemas map.
     *
     * @param key the key of the context album container
     */
    public AxContextSchemas(final AxArtifactKey key) {
        this(key, new TreeMap<AxArtifactKey, AxContextSchema>());
    }

    /**
     * This Constructor creates a {@link AxContextSchemas} object with all its fields defined.
     *
     * @param key the key of the context schema container
     * @param schemas a map of the schemas to insert in the context schema container
     */
    public AxContextSchemas(final AxArtifactKey key, final Map<AxArtifactKey, AxContextSchema> schemas) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(schemas, "schemas may not be null");

        this.key = key;
        this.schemas = new TreeMap<>();
        this.schemas.putAll(schemas);
    }

    /**
     * When a model is unmarshalled from disk or from the database, the context schema map is returned as a raw hash
     * map. This method is called by JAXB after unmarshaling and is used to convert the hash map to a
     * {@link NavigableMap} so that it will work with the {@link AxConceptGetter} interface.
     *
     * @param u the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller u, final Object parent) {
        // The map must be navigable to allow name and version searching, unmarshaling returns a
        // hash map
        final NavigableMap<AxArtifactKey, AxContextSchema> navigableContextSchemas = new TreeMap<>();
        navigableContextSchemas.putAll(schemas);
        schemas = navigableContextSchemas;
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
        keyList.addAll(schemas.keySet());

        return keyList;
    }

    /**
     * Sets the key of the context schema container.
     *
     * @param key the key of the container
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the map of context schemas in this container.
     *
     * @return the map of schemas
     */
    public Map<AxArtifactKey, AxContextSchema> getSchemasMap() {
        return schemas;
    }

    /**
     * Sets the map of context schemas in this container.
     *
     * @param schemasMap the map of schemas
     */
    public void setSchemasMap(final Map<AxArtifactKey, AxContextSchema> schemasMap) {
        Assertions.argumentNotNull(schemasMap, "schemasMap may not be null");

        this.schemas = new TreeMap<>();
        this.schemas.putAll(schemasMap);
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
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (schemas.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "contextSchemas may not be empty"));
        } else {
            for (final Entry<AxArtifactKey, AxContextSchema> contextSchemaEntry : schemas.entrySet()) {
                if (contextSchemaEntry.getKey().equals(AxArtifactKey.getNullKey())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key on schemas entry " + contextSchemaEntry.getKey() + " may not be the null key"));
                } else if (contextSchemaEntry.getValue() == null) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "value on schemas entry " + contextSchemaEntry.getKey() + " may not be null"));
                } else {
                    if (!contextSchemaEntry.getKey().equals(contextSchemaEntry.getValue().getKey())) {
                        result.addValidationMessage(new AxValidationMessage(key, this.getClass(),
                                ValidationResult.INVALID, "key on schemas entry " + contextSchemaEntry.getKey()
                                        + " does not equal entry key " + contextSchemaEntry.getValue().getKey()));
                    }

                    result = contextSchemaEntry.getValue().validate(result);
                }
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxContextSchema> contextSchemaEntry : schemas.entrySet()) {
            contextSchemaEntry.getKey().clean();
            contextSchemaEntry.getValue().clean();
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
        builder.append("key=");
        builder.append(key);
        builder.append(",schemas=");
        builder.append(schemas);
        builder.append(")");
        return builder.toString();
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
        Assertions.instanceOf(copyObject, AxContextSchemas.class);

        final AxContextSchemas copy = ((AxContextSchemas) copyObject);
        copy.setKey(new AxArtifactKey(key));

        final Map<AxArtifactKey, AxContextSchema> newcontextSchemas = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxContextSchema> contextSchemasEntry : schemas.entrySet()) {
            newcontextSchemas.put(new AxArtifactKey(contextSchemasEntry.getKey()),
                    new AxContextSchema(contextSchemasEntry.getValue()));
        }
        copy.setSchemasMap(newcontextSchemas);

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
        result = prime * result + schemas.hashCode();
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

        final AxContextSchemas other = (AxContextSchemas) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return schemas.equals(other.schemas);
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

        final AxContextSchemas other = (AxContextSchemas) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!schemas.equals(other.schemas)) {
            return (schemas.hashCode() - other.schemas.hashCode());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(org.onap.policy.apex.core.
     * basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public AxContextSchema get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextSchema>) schemas).get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang.String)
     */
    @Override
    public AxContextSchema get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextSchema>) schemas).get(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang.String, java.lang.String)
     */
    @Override
    public AxContextSchema get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextSchema>) schemas).get(conceptKeyName,
                conceptKeyVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String)
     */
    @Override
    public Set<AxContextSchema> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextSchema>) schemas).getAll(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String, java.lang.String)
     */
    @Override
    public Set<AxContextSchema> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxContextSchema>) schemas).getAll(conceptKeyName,
                conceptKeyVersion);
    }
}
