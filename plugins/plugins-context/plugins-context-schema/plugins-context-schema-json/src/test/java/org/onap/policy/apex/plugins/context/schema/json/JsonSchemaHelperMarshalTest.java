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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.worldturner.medeia.api.ValidationFailedException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.utils.coder.CoderException;

class JsonSchemaHelperMarshalTest extends CommonTestData {

    /**
     * Test Boolean.
     */
    @Test
    void testBooleanMarshal() {
        var schemaHelper = createSchema(BOOLEAN_SCHEMA);
        assertThat(schemaHelper.marshal2String(Boolean.TRUE)).isEqualTo("true");
    }

    /**
     * Test null.
     */
    @Test
    void testNullMarshal() {
        var schemaHelper = createSchema(NULL_SCHEMA);
        assertThat(schemaHelper.marshal2String(null)).isEqualTo("null");
    }

    /**
     * Test Array.
     *
     * @throws CoderException the coder exception
     */
    @Test
    void testArrayMarshal() throws CoderException {
        var schemaHelper = createSchema(MEASUREMENTGROUPS_TYPE);
        var object = coder.decode(MEASUREMENTGROUPS, Object.class);
        assertThat(object).isInstanceOf(ArrayList.class);
        var marshalledString = schemaHelper.marshal2String(object);
        assertThat(marshalledString).isEqualTo(MEASUREMENTGROUPS);
        var marshalledObject = schemaHelper.marshal2Object(object);
        assertThat(marshalledObject).isInstanceOf(JsonArray.class);
    }

    /**
     * Test Object marshal valid scenario using JSON Schema draft 04.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchemaDraft04_valid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataReturned = validateAndMarshal(COMMONHEADERTYPE_DRAFT04, dataAsObject, true);
        assertThat(dataReturned).isEqualTo(COMMONHEADER);
    }

    /**
     * Test Object marshal valid scenario using JSON Schema draft 07.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchemaDraft07_valid() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataReturned = validateAndMarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject, true);
        assertThat(dataReturned).isEqualTo(COMMONHEADER);
        assertThat(validateAndMarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject, false)).isInstanceOf(JsonObject.class);
    }

    /**
     * Test Object marshal invalid - required field missing scenario.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchema_fieldMissing() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        dataAsObject.remove(TEST_ID);
        assertThatThrownBy(() -> validateAndMarshal(COMMONHEADERTYPE_DRAFT07, dataAsObject, true))
            .isInstanceOf(ValidationFailedException.class)
            .hasMessageContaining("Required property testId is missing from object");
    }

    /**
     * Test Object marshal with optional field.
     *
     * @throws CoderException the coderException
     */
    @Test
    void testObjectSchema_OptionalField() throws CoderException {
        var dataAsObject = coder.decode(COMMONHEADER, Map.class);
        var dataAsjsonObject = coder.decode(COMMONHEADER, JsonObject.class);
        dataAsObject.remove(TEST_ID);
        dataAsjsonObject.remove(TEST_ID);
        var dataReturned = validateAndMarshal(COMMONHEADERTYPE_WITH_OPTIONAL, dataAsObject, false);
        assertThat(dataReturned).isEqualTo(dataAsjsonObject);
    }

    private Object validateAndMarshal(String schemaDef, Object obj, boolean marshalAsString) {
        var schemaHelper = createSchema(schemaDef);
        if (marshalAsString) {
            return schemaHelper.marshal2String(obj);
        } else {
            return schemaHelper.marshal2Object(obj);
        }
    }

}
