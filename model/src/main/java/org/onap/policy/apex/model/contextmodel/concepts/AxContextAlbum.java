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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is used to define an album of context.
 *
 * <p>A context album is a distributed map of context that will be distributed across all process instances that require
 * access to it. This class defines the schema (structure) of the items in the context album, whether the items on the
 * context album are writable or not, and what the scope of the context album is.
 *
 * <p>The structure of items (objects) the context album is defined as a schema, which is understood by whatever schema
 * implementation is being used for the context album.
 *
 * <p>The scope of a context album is a string field, understood by whatever distribution mechanism is being used for
 * the context album. The distribution mechanism uses the scope of the context album to decide to which executable
 * entities a given context album is distributed.
 *
 * <p>The writable flag on a context album defines whether users of a context album can write to the context album or
 * just read objects from the context album.
 *
 * <p>Validation checks that the album key and the context schema key are not null and that the scope field is not
 * undefined and matches the regular expression SCOPE_REGEXP.
 */

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)

public class AxContextAlbum extends AxConcept {
    private static final String SCOPE_STRING = "scope";

    @Serial
    private static final long serialVersionUID = 4290442590545820316L;

    /**
     * The legal values for the scope of a context album is constrained by this regular expression.
     */
    public static final String SCOPE_REGEXP = "[A-Za-z0-9\\-_]+";

    /**
     * The value of scope for a context album for which a scope has not been specified.
     */
    public static final String SCOPE_UNDEFINED = "UNDEFINED";

    private AxArtifactKey key;
    private String scope;

    @Setter
    private boolean isWritable;

    private AxArtifactKey itemSchema;

    /**
     * The default constructor creates a context album with a null artifact key. The scope of the context album is set
     * as SCOPE_UNDEFINED, the album is writable, and the artifact key of the context schema is set to the null artifact
     * key.
     */
    public AxContextAlbum() {
        this(new AxArtifactKey());
        scope = SCOPE_UNDEFINED;
        isWritable = true;
        itemSchema = AxArtifactKey.getNullKey();
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxContextAlbum(final AxContextAlbum copyConcept) {
        super(copyConcept);
    }

    /**
     * The keyed constructor creates a context album with the specified artifact key. The scope of the context album is
     * set as SCOPE_UNDEFINED, the album is writable, and the artifact key of the context schema is set to the null
     * artifact key.
     *
     * @param key the key of the context album
     */
    public AxContextAlbum(final AxArtifactKey key) {
        this(key, SCOPE_UNDEFINED, true, AxArtifactKey.getNullKey());
    }

    /**
     * Constructor that sets all the fields of the context album.
     *
     * @param key        the key of the context album
     * @param scope      the scope field, must match the regular expression SCOPE_REGEXP
     * @param isWritable specifies whether the context album will be writable or not
     * @param itemSchema the artifact key of the context schema to use for this context album
     */
    public AxContextAlbum(final AxArtifactKey key, final String scope, final boolean isWritable,
                          final AxArtifactKey itemSchema) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(scope, "scope may not be null");
        Assertions.argumentNotNull(itemSchema, "itemSchema may not be null");

        this.key = key;
        this.scope = Assertions.validateStringParameter(SCOPE_STRING, scope, SCOPE_REGEXP);
        this.isWritable = isWritable;
        this.itemSchema = itemSchema;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
        keyList.add(new AxKeyUse(itemSchema.getKey()));

        return keyList;
    }

    /**
     * Sets the key of the context album.
     *
     * @param key the context album key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Sets the scope of the context album.
     *
     * @param scope the context album scope
     */
    public void setScope(final String scope) {
        Assertions.argumentNotNull(scope, "scope may not be null");
        this.scope = Assertions.validateStringParameter(SCOPE_STRING, scope, SCOPE_REGEXP);
    }

    /**
     * Sets the artifact key of the item schema of this context album.
     *
     * @param itemSchema the item schema key
     */
    public void setItemSchema(final AxArtifactKey itemSchema) {
        Assertions.argumentNotNull(itemSchema, "itemSchema key may not be null");
        this.itemSchema = itemSchema;
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

        if (scope.replaceAll(WHITESPACE_REGEX, "").isEmpty() || scope.equals(SCOPE_UNDEFINED)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "scope is not defined"));
        }

        var stringCheckResult = Assertions.getStringParameterValidationMessage(SCOPE_STRING, scope, SCOPE_REGEXP);
        if (stringCheckResult != null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "scope invalid-" + stringCheckResult));
        }

        if (itemSchema.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "itemSchema reference is a null key, an item schema must be specified"));
        }
        result = itemSchema.validate(result);

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        scope = Assertions.validateStringParameter(SCOPE_STRING, scope, SCOPE_REGEXP);
        itemSchema.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "targetObject may not be null");

        Assertions.instanceOf(target, AxContextAlbum.class);

        final AxContextAlbum copy = ((AxContextAlbum) target);
        copy.setKey(new AxArtifactKey(key));
        copy.setScope(scope);
        copy.setWritable(isWritable);
        copy.setItemSchema(new AxArtifactKey(itemSchema));

        return copy;
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

        final AxContextAlbum other = (AxContextAlbum) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!scope.equals(other.scope)) {
            return scope.compareTo(other.scope);
        }
        if (isWritable != other.isWritable) {
            return (isWritable ? 1 : -1);
        }
        return itemSchema.compareTo(other.itemSchema);
    }
}
