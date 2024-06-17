/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021, 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * The Class TestAvroSchemaMap.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaMapTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String longMapSchema;
    private String addressMapSchema;
    private String addressMapSchemaInvalidFields;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        longMapSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/MapExampleLong.avsc");
        addressMapSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/MapExampleAddress.avsc");
        addressMapSchemaInvalidFields =
            TextFileUtils.getTextFileAsString("src/test/resources/avsc/MapExampleAddressInvalidFields.avsc");
    }

    /**
     * Inits the context.
     */
    @BeforeEach
    void initContext() {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("AVRO", new AvroSchemaHelperParameters());
        ParameterService.register(schemaParameters);
    }

    /**
     * Clear context.
     */
    @AfterEach
    void clearContext() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    /**
     * Test valid schemas with substitutions.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testValidSubstitutions() throws IOException {
        final String subst1 = "{\"type\":\"record\",\"name\":\"Subst1\","
            + "\"fields\":[{\"name\": \"A_DasH_B\",\"type\":\"string\"}]}";
        final AxContextSchema avroSubstSchema1 = new AxContextSchema(
            new AxArtifactKey("AvroSubst1", "0.0.1"), "AVRO", subst1);
        schemas.getSchemasMap().put(avroSubstSchema1.getKey(), avroSubstSchema1);

        SchemaHelper schemaHelperSubst1 = new SchemaHelperFactory()
            .createSchemaHelper(testKey, avroSubstSchema1.getKey());
        final GenericRecord subst1A = (GenericRecord) schemaHelperSubst1.unmarshal("{\"A-B\":\"foo\"}");
        assertEquals(new Utf8("foo"), subst1A.get("A_DasH_B"));
        assertThatThrownBy(() -> subst1A.get("A-B")).hasMessage("Not a valid schema field: A-B");

        final Throwable exception1 = assertThrows(ContextRuntimeException.class,
            () -> schemaHelperSubst1.unmarshal("{\"A-B\":123}"));
        assertNotNull(exception1.getCause());
        assertEquals("Expected string. Got VALUE_NUMBER_INT", exception1.getCause().getMessage());

        final String subst2 = "{\"type\":\"record\",\"name\":\"Subst2\","
            + "\"fields\":[{\"name\": \"C_DoT_D\",\"type\":\"int\"}]}";
        final AxContextSchema avroSubstSchema2 = new AxContextSchema(
            new AxArtifactKey("AvroSubst2", "0.0.1"), "AVRO", subst2);
        schemas.getSchemasMap().put(avroSubstSchema2.getKey(), avroSubstSchema2);

        final SchemaHelper schemaHelperSubst2 = new SchemaHelperFactory()
            .createSchemaHelper(testKey, avroSubstSchema2.getKey());
        final GenericRecord subst2A = (GenericRecord) schemaHelperSubst2.unmarshal("{\"C.D\":123}");
        assertEquals(123, subst2A.get("C_DoT_D"));
        assertThatThrownBy(() -> subst2A.get("C.D")).hasMessage("Not a valid schema field: C.D");

        final Throwable exception2 = assertThrows(ContextRuntimeException.class,
            () -> schemaHelperSubst2.unmarshal("{\"C_DoT_D\":\"bar\"}"));
        assertNotNull(exception2.getCause());
        assertEquals("Expected int. Got VALUE_STRING", exception2.getCause().getMessage());

        final String subst3 = "{\"type\":\"record\",\"name\":\"Subst3\","
            + "\"fields\":[{\"name\": \"E_ColoN_F\",\"type\":\"boolean\"}]}";
        final AxContextSchema avroSubstSchema3 = new AxContextSchema(
            new AxArtifactKey("AvroSubst3", "0.0.1"), "AVRO", subst3);
        schemas.getSchemasMap().put(avroSubstSchema3.getKey(), avroSubstSchema3);

        final SchemaHelper schemaHelperSubst3 = new SchemaHelperFactory()
            .createSchemaHelper(testKey, avroSubstSchema3.getKey());
        final GenericRecord subst3A = (GenericRecord) schemaHelperSubst3.unmarshal("{\"E:F\":true}");
        assertEquals(true, subst3A.get("E_ColoN_F"));
        assertThatThrownBy(() -> subst3A.get("E:F")).hasMessage("Not a valid schema field: E:F");

        final Throwable exception3 = assertThrows(ContextRuntimeException.class,
            () -> schemaHelperSubst3.unmarshal("{\"E_ColoN_F\":\"gaz\"}"));
        assertNotNull(exception3.getCause());
        assertEquals("Expected boolean. Got VALUE_STRING", exception3.getCause().getMessage());
    }

    /**
     * Test invalid schemas without substitutions.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testInValidSubstitutions() throws IOException {
        final String fail1 = "{\"type\":\"record\",\"name\":\"Fail1\","
            + "\"fields\":[{\"name\": \"A-B\",\"type\":\"string\"}]}";
        final AxContextSchema avroFailSchema1 = new AxContextSchema(
            new AxArtifactKey("AvroFail1", "0.0.1"), "AVRO", fail1);
        schemas.getSchemasMap().put(avroFailSchema1.getKey(), avroFailSchema1);

        SchemaHelperFactory sh = new SchemaHelperFactory();
        AxArtifactKey ak = avroFailSchema1.getKey();
        final Throwable exception1 = assertThrows(ContextRuntimeException.class,
            () -> sh.createSchemaHelper(testKey, ak));
        assertNotNull(exception1.getCause());
        assertEquals("Illegal character in: A-B", exception1.getCause().getMessage());

        final String fail2 = "{\"type\":\"record\",\"name\":\"Fail2\","
            + "\"fields\":[{\"name\": \"C.D\",\"type\":\"int\"}]}";
        final AxContextSchema avroFailSchema2 = new AxContextSchema(
            new AxArtifactKey("AvroFail2", "0.0.1"), "AVRO", fail2);
        schemas.getSchemasMap().put(avroFailSchema2.getKey(), avroFailSchema2);

        AxArtifactKey ak2 = avroFailSchema2.getKey();
        final Throwable exception2 = assertThrows(ContextRuntimeException.class,
            () -> sh.createSchemaHelper(testKey, ak2));
        assertNotNull(exception2.getCause());
        assertEquals("Illegal character in: C.D", exception2.getCause().getMessage());

        final String fail3 = "{\"type\":\"record\",\"name\":\"Fail3\","
            + "\"fields\":[{\"name\": \"E:F\",\"type\":\"boolean\"}]}";
        final AxContextSchema avroFailSchema3 = new AxContextSchema(
            new AxArtifactKey("AvroFail3", "0.0.1"), "AVRO", fail3);
        schemas.getSchemasMap().put(avroFailSchema3.getKey(), avroFailSchema3);
        AxArtifactKey ak3 = avroFailSchema3.getKey();
        final Throwable exception3 = assertThrows(ContextRuntimeException.class,
            () -> sh.createSchemaHelper(testKey, ak3));
        assertNotNull(exception3.getCause());
        assertEquals("Illegal character in: E:F", exception3.getCause().getMessage());
    }

    /**
     * Test map init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testMapInit() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", addressMapSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final HashMap<?, ?> newMapEmpty = (HashMap<?, ?>) schemaHelper.createNewInstance();
        assertEquals(0, newMapEmpty.size());

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/MapExampleAddressFull.json");
        final HashMap<?, ?> newMapFull = (HashMap<?, ?>) schemaHelper.createNewInstance(inString);

        assertEquals("{\"streetaddress\": \"221 B Baker St.\", \"city\": \"London\"}",
            newMapFull.get(new Utf8("address2")).toString());
    }

    /**
     * Test long map unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testLongMapUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO", longMapSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleLongNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleLongFull.json");
    }

    /**
     * Test address map unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testAddressMapUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO", addressMapSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleAddressNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleAddressFull.json");
    }

    /**
     * Test sub record create.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testSubRecordCreateRecord() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO", addressMapSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final GenericRecord subRecord = (GenericRecord) schemaHelper.createNewSubInstance("AddressUSRecord");
        assertThatThrownBy(() -> subRecord.get("streetAddress")).hasMessage("Not a valid schema field: streetAddress");

    }

    /**
     * Test address map unmarshal marshal invalid fields.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testAddressMapUnmarshalMarshalInvalidFields() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO", addressMapSchemaInvalidFields);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleAddressInvalidFields.json");

        String vals = TextFileUtils.getTextFileAsString("src/test/resources/data/MapExampleAddressInvalidFields.json");
        final HashMap<?, ?> newMapFull = (HashMap<?, ?>) schemaHelper.createNewInstance(vals);
        final String expect = "{\"street_DasH_address\": \"Wayne Manor\", \"the_DoT_city\": \"Gotham City\", "
            + "\"the_ColoN_code\": \"BatCave7\"}";
        assertEquals(expect, newMapFull.get(new Utf8("address_DoT_3")).toString());
    }

    /**
     * Test unmarshal marshal.
     *
     * @param schemaHelper the schema helper
     * @param fileName     the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String originalInString = TextFileUtils.getTextFileAsString(fileName);
        final HashMap<?, ?> firstDecodedMap = (HashMap<?, ?>) schemaHelper.unmarshal(originalInString);

        final String outString = schemaHelper.marshal2String(firstDecodedMap);

        final File tempOutFile = File.createTempFile("ApexAvro", ".json");
        TextFileUtils.putStringAsFile(outString, tempOutFile);

        final String decodeEncodeInString = TextFileUtils.getTextFileAsString(fileName);
        assertTrue(tempOutFile.delete());

        final HashMap<?, ?> secondDecodedMap = (HashMap<?, ?>) schemaHelper.unmarshal(decodeEncodeInString);

        // Now check that our doubly encoded map equals the first decoded map, Java map equals
        // checks values and keys
        assertEquals(firstDecodedMap, secondDecodedMap);
    }
}
