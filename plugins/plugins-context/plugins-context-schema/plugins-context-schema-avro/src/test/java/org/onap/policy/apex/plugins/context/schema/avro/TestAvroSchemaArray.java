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

import java.io.IOException;

import org.apache.avro.generic.GenericData.Array;
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
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
public class TestAvroSchemaArray {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String longArraySchema;
    private String addressArraySchema;

    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        longArraySchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/ArrayExampleLong.avsc");
        addressArraySchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/ArrayExampleAddress.avsc");
    }

    @Before
    public void initContext() {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("AVRO", new AvroSchemaHelperParameters());
        ParameterService.register(schemaParameters);
        
    }

    @After
    public void clearContext() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    public void testArrayInit() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", addressArraySchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final Array<?> newArrayEmpty = (Array<?>) schemaHelper.createNewInstance();
        assertEquals(0, newArrayEmpty.size());

        final String inString =
                TextFileUtils.getTextFileAsString("src/test/resources/data/ArrayExampleAddressFull.json");
        final Array<?> newArrayFull = (Array<?>) schemaHelper.createNewInstance(inString);
        assertEquals("{\"streetaddress\": \"1600 Pennsylvania Avenue\", \"city\": \"Washington DC\"}",
                newArrayFull.get(0).toString());
    }

    @Test
    public void testLongArrayUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroArray", "0.0.1"), "AVRO", longArraySchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/ArrayExampleLongNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/ArrayExampleLongFull.json");
    }

    @Test
    public void testAddressArrayUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroArray", "0.0.1"), "AVRO", addressArraySchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/ArrayExampleAddressNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/ArrayExampleAddressFull.json");
    }

    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String inString = TextFileUtils.getTextFileAsString(fileName);
        final Array<?> schemaObject = (Array<?>) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(schemaObject);
        assertEquals(inString.replaceAll("\\s+", ""), outString.replaceAll("\\s+", ""));
    }
}
