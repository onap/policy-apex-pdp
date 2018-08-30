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

import org.apache.avro.generic.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class TestAvroSchemaUnion {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String uinionSchema;

    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        uinionSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/UnionExample.avsc");
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

    @Ignore
    @Test
    public void testUnionAllFields() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", uinionSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/UnionExampleAllFields.json");
        final GenericRecord user = (GenericRecord) schemaHelper.createNewInstance(inString);

        assertEquals("Ben", user.get("name").toString());
        assertEquals(7, user.get("favourite_number"));
        assertEquals("red", user.get("favourite_colour").toString());
    }

    @Ignore
    @Test
    public void testUnionOptionalField() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", uinionSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final String inString =
                TextFileUtils.getTextFileAsString("src/test/resources/data/UnionExampleOptionalField.json");
        final GenericRecord user = (GenericRecord) schemaHelper.createNewInstance(inString);

        assertEquals("Ben", user.get("name").toString());
        assertEquals(7, user.get("favourite_number"));
        assertEquals("red", user.get("favourite_colour").toString());
    }

    @Ignore
    @Test
    public void testUnionNullField() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", uinionSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/UnionExampleNullField.json");
        final GenericRecord user = (GenericRecord) schemaHelper.createNewInstance(inString);

        assertEquals("Ben", user.get("name").toString());
        assertEquals(7, user.get("favourite_number"));
        assertEquals("red", user.get("favourite_colour").toString());
    }
}
