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

package org.onap.policy.apex.model.eventmodel.concepts;

import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.basicmodel.xml.AxReferenceKeyAdapter;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * In Apex, a field is an input or output parameter to or from a concept. For example, the parameters of an event are
 * fields and the input and output of a task is defined as a collection of fields.
 *
 * <p>A field has an {@link AxReferenceKey} key that defines its name and parent, and a {@link AxArtifactKey} key to a
 * context schema that defines the structure of the data atom that holds the value of the field. Fields can be specified
 * as being optional but are mandatory by default.
 *
 * <p>Validation checks that the field key and the field schema reference key are not null.
 */
@Entity
@Table(name = "AxField")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexField", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxField", namespace = "http://www.onap.org/policy/apex-pdp", propOrder =
    { "key", "fieldSchemaKey", "optional" })

public class AxField extends AxConcept {
    private static final String KEY_MAY_NOT_BE_NULL = "key may not be null";
    private static final String FIELD_SCHEMA_KEY_MAY_NOT_BE_NULL = "fieldSchemaKey may not be null";

    private static final long serialVersionUID = -6443016863162692288L;

    @EmbeddedId()
    @XmlElement(name = "key", required = true)
    @XmlJavaTypeAdapter(AxReferenceKeyAdapter.class)
    private AxReferenceKey key;

    // @formatter:off
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "fieldSchemaName"))
    @AttributeOverride(name = "version", column = @Column(name = "fieldSchemaVersion"))
    @Column(name = "fieldSchemaKey")
    @XmlElement(required = true)
    @Getter(AccessLevel.NONE)
    private AxArtifactKey fieldSchemaKey;
    // @formatter:on

    @Column(name = "optional")
    @XmlElement(required = false)
    @Setter
    private boolean optional;

    /**
     * The default constructor creates a field with a null artifact and schema key.
     */
    public AxField() {
        this(new AxReferenceKey());
        optional = false;
    }

    /**
     * The default constructor creates a field with the given artifact key and a null schema key.
     *
     * @param key the field key
     */
    public AxField(final AxReferenceKey key) {
        this(key, new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxField(final AxField copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor to create the field with both its keys defined.
     *
     * @param key the field key
     * @param fieldSchemaKey the key of the field schema to use for this field
     */
    public AxField(final AxReferenceKey key, final AxArtifactKey fieldSchemaKey) {
        super();
        Assertions.argumentNotNull(key, KEY_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(fieldSchemaKey, FIELD_SCHEMA_KEY_MAY_NOT_BE_NULL);

        this.key = key;
        this.fieldSchemaKey = fieldSchemaKey;
    }

    /**
     * Constructor to create the field with all its fields defined.
     *
     * @param key the field key
     * @param fieldSchemaKey the key of the field schema to use for this field
     * @param optional true if this field is optional
     */
    public AxField(final AxReferenceKey key, final AxArtifactKey fieldSchemaKey, final boolean optional) {
        super();
        Assertions.argumentNotNull(key, KEY_MAY_NOT_BE_NULL);
        Assertions.argumentNotNull(fieldSchemaKey, FIELD_SCHEMA_KEY_MAY_NOT_BE_NULL);

        this.key = key;
        this.fieldSchemaKey = fieldSchemaKey;
        this.optional = optional;
    }

    /**
     * Constructor to create the field with the local name of its reference key defined and its schema key defined.
     *
     * @param localName the local name of the field reference key
     * @param fieldSchemaKey the key of the field schema to use for this field
     */
    public AxField(final String localName, final AxArtifactKey fieldSchemaKey) {
        super();
        Assertions.argumentNotNull(localName, "localName may not be null");
        Assertions.argumentNotNull(fieldSchemaKey, FIELD_SCHEMA_KEY_MAY_NOT_BE_NULL);

        key = new AxReferenceKey();
        key.setLocalName(localName);
        this.fieldSchemaKey = fieldSchemaKey;
    }

    /**
     * Constructor to create the field with the local name of its reference key defined, its schema key and optionality
     * defined.
     *
     * @param localName the local name of the field reference key
     * @param fieldSchemaKey the key of the field schema to use for this field
     * @param optional true if this field is optional
     */
    public AxField(final String localName, final AxArtifactKey fieldSchemaKey, final boolean optional) {
        super();
        Assertions.argumentNotNull(localName, "localName may not be null");
        Assertions.argumentNotNull(fieldSchemaKey, FIELD_SCHEMA_KEY_MAY_NOT_BE_NULL);

        key = new AxReferenceKey();
        key.setLocalName(localName);
        this.fieldSchemaKey = fieldSchemaKey;
        this.optional = optional;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
        keyList.add(new AxKeyUse(fieldSchemaKey));
        return keyList;
    }

    /**
     * Sets the reference key of the field.
     *
     * @param key the field reference key
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, KEY_MAY_NOT_BE_NULL);
        this.key = key;
    }

    /**
     * Gets the key of the field schema.
     *
     * @return the field schema key
     */
    public AxArtifactKey getSchema() {
        return fieldSchemaKey;
    }

    /**
     * Sets the key of the field schema.
     *
     * @param schema the field schema key
     */
    public void setSchema(final AxArtifactKey schema) {
        Assertions.argumentNotNull(schema, "schema may not be null");
        this.fieldSchemaKey = schema;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key is a null key"));
        }

        result = key.validate(result);

        if (fieldSchemaKey.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "fieldSchemaKey is a null key: " + fieldSchemaKey));
        }
        return fieldSchemaKey.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        fieldSchemaKey.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxField.class);

        final AxField copy = ((AxField) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setSchema(new AxArtifactKey(fieldSchemaKey));
        copy.setOptional(optional);
        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return 1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (!(otherObj instanceof AxField)) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxField other = (AxField) otherObj;
        if (!key.getLocalName().equals(other.key.getLocalName())) {
            return key.getLocalName().compareTo(other.key.getLocalName());
        }
        if (optional != other.optional) {
            return (optional ? 1 : -1);
        }
        return fieldSchemaKey.compareTo(other.fieldSchemaKey);
    }
}
