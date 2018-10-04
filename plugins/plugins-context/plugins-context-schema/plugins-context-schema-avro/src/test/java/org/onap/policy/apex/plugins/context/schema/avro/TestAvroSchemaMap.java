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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.common.parameters.ParameterService;

/**
 * The Class TestAvroSchemaMap.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
public class TestAvroSchemaMap {
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
    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        longMapSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/MapExampleLong.avsc");
        addressMapSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/MapExampleAddress.avsc");
        addressMapSchemaInvalidFields = TextFileUtils
                        .getTextFileAsString("src/test/resources/avsc/MapExampleAddressInvalidFields.avsc");
    }

    /**
     * Inits the context.
     */
    @Before
    public void initContext() {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("AVRO", new AvroSchemaHelperParameters());
        ParameterService.register(schemaParameters);

    }

    /**
     * Clear context.
     */
    @After
    public void clearContext() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    /**
     * Test map init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testMapInit() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO",
                        addressMapSchema);

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
    public void testLongMapUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO",
                        longMapSchema);

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
    public void testAddressMapUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO",
                        addressMapSchema);

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
    public void testSubRecordCreateRecord() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO",
                        addressMapSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        GenericRecord subRecord = (GenericRecord) schemaHelper.createNewSubInstance("AddressUSRecord");
        assertEquals(null, subRecord.get("streetAddress"));
    }

    /**
     * Test address map unmarshal marshal invalid fields.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testAddressMapUnmarshalMarshalInvalidFields() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroMap", "0.0.1"), "AVRO",
                        addressMapSchemaInvalidFields);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/MapExampleAddressInvalidFields.json");
    }

    /**
     * Test unmarshal marshal.
     *
     * @param schemaHelper the schema helper
     * @param fileName the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String originalInString = TextFileUtils.getTextFileAsString(fileName);
        final HashMap<?, ?> firstDecodedMap = (HashMap<?, ?>) schemaHelper.unmarshal(originalInString);

        final String outString = schemaHelper.marshal2String(firstDecodedMap);

        final File tempOutFile = File.createTempFile("ApexAvro", ".json");
        TextFileUtils.putStringAsFile(outString, tempOutFile);

        final String decodeEncodeInString = TextFileUtils.getTextFileAsString(fileName);
        tempOutFile.delete();

        final HashMap<?, ?> secondDecodedMap = (HashMap<?, ?>) schemaHelper.unmarshal(decodeEncodeInString);

        // Now check that our doubly encoded map equals the first decoded map, Java map equals
        // checks values and keys
        assertEquals(firstDecodedMap, secondDecodedMap);
    }
}
