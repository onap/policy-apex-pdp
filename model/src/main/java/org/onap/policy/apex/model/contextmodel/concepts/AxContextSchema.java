/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.contextmodel.concepts;

import java.io.Serial;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class holds a data schema definition in Apex. A data schema describes the structure of a single atom of data
 * handled by Apex. This atom of data can be a primitive type such as an integer or a string, or it can be a more
 * complex data type such as a Java object or an object described using a data definition language such as Avro. The
 * schema flavour defines the type of schema being defined and the schema itself defines the schema. The schema flavour
 * is used by Apex to look up and load a plugin class that understands and interprets the schema definition and can
 * create instances of classes for the schema.
 *
 * <p>An {@link AxContextSchema} is used to define each parameter in Apex events, the messages that enter, exit, and are
 * passed internally in Apex. In addition, an Apex {@link AxContextAlbum} instances hold a map of
 * {@link AxContextSchema} instances to represent the context being managed as an {@link AxContextAlbum}. For example,
 * the state of all cells in a mobile network might be represented as an {@link AxContextAlbum} with its
 * {@link AxContextSchema} being defined as @code cell} objects.
 *
 * <p>Validation checks that the schema key is not null. It also checks that the schema flavour is defined and matches
 * the regular expression SCHEMA_FLAVOUR_REGEXP. Finally, validation checks that the defined schema is not a blank or
 * empty string.
 */
@Getter
@ToString
public class AxContextSchema extends AxConcept {
    private static final String SCHEMA_FLAVOUR = "schemaFlavour";

    @Serial
    private static final long serialVersionUID = -6443016863162692288L;

    /**
     * Regular expression that constrains what values a schema flavour can have.
     */
    public static final String SCHEMA_FLAVOUR_REGEXP = "[A-Za-z0-9\\-_]+";

    /**
     * An undefined schema flavour has this value.
     */
    public static final String SCHEMA_FLAVOUR_UNDEFINED = "UNDEFINED";

    /**
     * The maximum permissible size of a schema definition.
     */
    public static final int MAX_SCHEMA_SIZE = 32672; // The maximum size supported by Apache Derby

    private AxArtifactKey key;
    private String schemaFlavour;

    @Getter(AccessLevel.NONE)
    private String schemaDefinition;

    /**
     * The default constructor creates a context schema with a null artifact key. The flavour of the context album is
     * set as SCHEMA_FLAVOUR_UNDEFINED and the schema itself is defined as an empty string.
     */
    public AxContextSchema() {
        this(new AxArtifactKey());
        schemaFlavour = SCHEMA_FLAVOUR_UNDEFINED;
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxContextSchema(final AxContextSchema copyConcept) {
        super(copyConcept);
    }

    /**
     * The key constructor creates a context schema with the given artifact key. The flavour of the context album is set
     * as SCHEMA_FLAVOUR_UNDEFINED and the schema itself is defined as an empty string.
     *
     * @param key the key
     */
    public AxContextSchema(final AxArtifactKey key) {
        this(key, SCHEMA_FLAVOUR_UNDEFINED, "");
    }

    /**
     * This Constructor creates a context schema with all of its fields defined.
     *
     * @param key              the key
     * @param schemaFlavour    the schema flavour
     * @param schemaDefinition the schema definition
     */
    public AxContextSchema(final AxArtifactKey key, final String schemaFlavour, final String schemaDefinition) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(schemaFlavour, "schemaFlavour may not be null");
        Assertions.argumentNotNull(schemaDefinition, "schemaDefinition may not be null");

        this.key = key;
        this.schemaFlavour = Assertions.validateStringParameter(SCHEMA_FLAVOUR, schemaFlavour, SCHEMA_FLAVOUR_REGEXP);
        this.schemaDefinition = schemaDefinition.replaceAll(WHITESPACE_REGEX, "");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return key.getKeys();
    }

    /**
     * Sets the key of the context schema.
     *
     * @param key the key of the context schema
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Sets the schema flavour, which defines the type of schema definition being used.
     *
     * @param schemaFlavour the schema flavour
     */
    public void setSchemaFlavour(final String schemaFlavour) {
        this.schemaFlavour = Assertions.validateStringParameter(SCHEMA_FLAVOUR, schemaFlavour, SCHEMA_FLAVOUR_REGEXP);
    }

    /**
     * Gets the schema, which defines the structure of this data schema atom.
     *
     * @return the schema definition
     */
    public String getSchema() {
        return schemaDefinition;
    }

    /**
     * Sets the schema, which defines the structure of this data schema atom.
     *
     * @param schema the schema definition
     */
    public void setSchema(final String schema) {
        Assertions.argumentNotNull(schema, "schema may not be null");
        this.schemaDefinition = schema.replaceAll(WHITESPACE_REGEX, "");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "key is a null key"));
        }

        result = key.validate(result);

        if (schemaFlavour.replaceAll(WHITESPACE_REGEX, "").isEmpty()
            || schemaFlavour.equals(SCHEMA_FLAVOUR_UNDEFINED)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "schema flavour is not defined"));
        }

        var flavourValidationResult = Assertions.getStringParameterValidationMessage(SCHEMA_FLAVOUR, schemaFlavour,
            SCHEMA_FLAVOUR_REGEXP);
        if (flavourValidationResult != null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "schema flavour invalid-" + flavourValidationResult));
        }

        if (schemaDefinition.replaceAll(WHITESPACE_REGEX, "").isEmpty()) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "no schemaDefinition specified, schemaDefinition may not be blank"));
        }

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        schemaFlavour = Assertions.validateStringParameter(SCHEMA_FLAVOUR, schemaFlavour, SCHEMA_FLAVOUR_REGEXP);
        schemaDefinition = schemaDefinition.replaceAll(WHITESPACE_REGEX, "");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        Assertions.instanceOf(target, AxContextSchema.class);

        final AxContextSchema copy = ((AxContextSchema) target);
        copy.setKey(new AxArtifactKey(key));
        copy.setSchemaFlavour(schemaFlavour);
        copy.setSchema(schemaDefinition);

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final var prime = 31;
        var result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + schemaFlavour.hashCode();

        final String thisSchema = schemaDefinition.replace("\n", "");
        result = prime * result + thisSchema.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
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

        final AxContextSchema other = (AxContextSchema) obj;

        if (!key.equals(other.key)) {
            return false;
        }
        if (!schemaFlavour.equals(other.schemaFlavour)) {
            return false;
        }
        return schemaDefinition.equals(other.schemaDefinition);
    }

    /**
     * {@inheritDoc}.
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

        final AxContextSchema other = (AxContextSchema) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!schemaFlavour.equals(other.schemaFlavour)) {
            return schemaFlavour.compareTo(other.schemaFlavour);
        }
        return schemaDefinition.compareTo(other.schemaDefinition);
    }
}
