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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.avro.generic.GenericData.EnumSymbol;
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

// TODO: Auto-generated Javadoc
/**
 * The Class TestAvroSchemaEnum.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version 
 */
public class AvroSchemaEnumTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String enumSchema;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        enumSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/EnumSchema.avsc");
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
     * Test enum init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testEnumInit() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", enumSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final EnumSymbol newEnumEmpty = (EnumSymbol) schemaHelper.createNewInstance();
        assertEquals("SPADES", newEnumEmpty.toString());

        final EnumSymbol newEnumFull = (EnumSymbol) schemaHelper.createNewInstance("\"HEARTS\"");
        assertEquals("HEARTS", newEnumFull.toString());
    }

    /**
     * Test enum unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testEnumUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroArray", "0.0.1"), "AVRO", enumSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/EnumExampleHearts.json");

        try {
            testUnmarshalMarshal(schemaHelper, "src/test/resources/data/EnumExampleNull.json");
            fail("This test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed: Expected fixed. Got VALUE_NULL",
                    e.getMessage());
        }
        try {
            testUnmarshalMarshal(schemaHelper, "src/test/resources/data/EnumExampleNull.json");
            fail("This test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed: Expected fixed. Got VALUE_NULL",
                    e.getMessage());
        }
        try {
            testUnmarshalMarshal(schemaHelper, "src/test/resources/data/EnumExampleBad0.json");
            fail("This test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("AvroTest:0.0.1: object \"\"TWEED\"\" Avro unmarshalling failed: Unknown symbol in enum TWEED",
                    e.getMessage());
        }
        try {
            testUnmarshalMarshal(schemaHelper, "src/test/resources/data/EnumExampleBad1.json");
            fail("This test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "AvroTest:0.0.1: object \"\"Hearts\"\" Avro unmarshalling failed: Unknown symbol in enum Hearts",
                    e.getMessage());
        }
    }

    /**
     * Test unmarshal marshal.
     *
     * @param schemaHelper the schema helper
     * @param fileName the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String inString = TextFileUtils.getTextFileAsString(fileName);
        final EnumSymbol decodedObject = (EnumSymbol) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(decodedObject);
        assertEquals(inString.replaceAll("[\\r?\\n]+", " "), outString.replaceAll("[\\r?\\n]+", " "));
    }
}
