/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Bell Canada. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.schema.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.gson.JsonObject;
import com.worldturner.medeia.api.ValidationFailedException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.common.utils.coder.CoderException;

class JsonSchemaHelperUnmarshalTest extends CommonTestData {

    /**
     * Test Boolean.
     */
    @Test
    void testBooleanUnmarshal() {
        var schemaHelper = createSchema(BOOLEAN_SCHEMA);
        assertThat(schemaHelper.createNewInstance(BOOLEAN_DATA)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
    }

    /**
     * Test null.
     */
    @Test
    void testNullUnmarshal() {
        var schemaHelper = createSchema(NULL_SCHEMA);
        assertThat(schemaHelper.createNewInstance(NULL_DATA)).isNull();
    }

    /**
     * Test Array.
     */
    @Test
    void testArrayUnmarshal() {
        var schemaHelper = createSchema(MEASUREMENTGROUPS_TYPE);
        var obj = schemaHelper.createNewInstance(MEASUREMENTGROUPS);
        assertThat(obj).isInstanceOf(ArrayList.class);
    }

    /**
     * Test invalid schema.
     */
    @Test
    void testSchemaInvalid() {
        String schemaDef = "{\"type\": \"object\"}";
        final AxContextSchema jsonSchema =
            new AxContextSchema(new AxArtifactKey("JsonObject", VERSION), JSON, schemaDef);
        var jsonSchemaHelper = new JsonSchemaHelper();
        assertThatThrownBy(() -> jsonSchemaHelper.init(testKey, jsonSchema))
            .isInstanceOf(ContextRuntimeException.class).hasMessageContaining("schema is invalid");
    }

    /**
     * Test Object unmarshal valid scenario using JSON Schema draft 04.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchemaDraft04_valid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataReturned = validateAndUnmarshal(COMMONHEADERTYPE_DRAFT04, COMMONHEADER);
        assertThat(dataReturned).isEqualTo(dataAsObject);
    }

    /**
     * Test Object unmarshal valid scenario using JSON Schema draft 07.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchemaDraft07_valid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataReturned = validateAndUnmarshal(COMMONHEADERTYPE_DRAFT07, COMMONHEADER);
        assertThat(dataReturned).isEqualTo(dataAsObject);
    }

    /**
     * Test Object unmarshal invalid scenario using JSON Schema draft 07.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchemaDraft07_invalid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, JsonObject.class);
        dataAsObject.addProperty("requestId", "abcd");
        assertThatThrownBy(() -> validateAndUnmarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject))
            .isInstanceOf(ValidationFailedException.class)
            .hasMessageContaining("Pattern ^[0-9]*-[0-9]*$ is not contained in text");
    }

    /**
     * Test createInstance using invalid format data.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testCreateNewInstanceInvalid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        assertThatThrownBy(() -> validateAndUnmarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject))
            .isInstanceOf(ContextRuntimeException.class).hasMessageContaining("not an instance of JsonObject");
    }

    /**
     * Test Object unmarshal invalid - required field missing scenario.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchema_fieldMissing() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, JsonObject.class);
        dataAsObject.remove(TEST_ID);
        assertThatThrownBy(() -> validateAndUnmarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject))
            .isInstanceOf(ValidationFailedException.class)
            .hasMessageContaining("Required property testId is missing from object");
    }

    /**
     * Test Object unmarshal with optional field.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchema_OptionalField() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataAsjsonObject = coder.decode(COMMONHEADER, JsonObject.class);
        dataAsObject.remove(TEST_ID);
        dataAsjsonObject.remove(TEST_ID);
        var dataReturned = validateAndUnmarshal(COMMONHEADERTYPE_WITH_OPTIONAL, dataAsjsonObject);
        assertThat(dataReturned).isEqualTo(dataAsObject);
    }

    private Object validateAndUnmarshal(String schemaDef, Object data) {
        var schemaHelper = createSchema(schemaDef);
        if (data instanceof String) {
            return schemaHelper.createNewInstance((String) data);
        } else {
            return schemaHelper.createNewInstance(data);
        }
    }

}
