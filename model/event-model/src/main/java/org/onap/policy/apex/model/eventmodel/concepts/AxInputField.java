/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

/**
 * This class specializes the {@link AxField} class for use as input fields on events.
 */
public class AxInputField extends AxField {
    private static final long serialVersionUID = 2090324845463750391L;

    /**
     * The default constructor creates a field with a null artifact and schema key.
     */
    public AxInputField() {
        super();
    }

    /**
     * The default constructor creates a field with the given artifact key and a null schema key.
     *
     * @param key the field key
     */
    public AxInputField(final AxReferenceKey key) {
        super(key);
    }

    /**
     * Constructor to create the field with both its keys defined.
     *
     * @param key the field key
     * @param fieldSchemaKey the key of the field schema to use for this field
     */
    public AxInputField(final AxReferenceKey key, final AxArtifactKey fieldSchemaKey) {
        super(key, fieldSchemaKey);
    }

    /**
     * Constructor to create the field with both its keys defined and optional flag specified.
     *
     * @param key the field key
     * @param fieldSchemaKey the key of the field schema to use for this field
     * @param optional true if the task field is optional, false otherwise
     */
    public AxInputField(final AxReferenceKey key, final AxArtifactKey fieldSchemaKey, final boolean optional) {
        super(key, fieldSchemaKey, optional);
    }

    /**
     * Constructor to create the field with the local name of its reference key defined and its schema key defined.
     *
     * @param localName the local name of the field reference key
     * @param fieldSchemaKey the key of the field schema to use for this field
     */
    public AxInputField(final String localName, final AxArtifactKey fieldSchemaKey) {
        super(localName, fieldSchemaKey);
    }

    /**
     * Copy constructor, create an input field as a copy of another input field.
     *
     * @param field the input field to copy from
     */
    public AxInputField(final AxInputField field) {
        super(new AxReferenceKey(field.getKey()), new AxArtifactKey(field.getSchema()), field.getOptional());
    }
}
