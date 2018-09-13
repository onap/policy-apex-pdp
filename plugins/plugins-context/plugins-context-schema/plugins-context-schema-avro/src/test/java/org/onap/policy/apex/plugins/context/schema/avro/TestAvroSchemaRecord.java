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
 * The Class TestAvroSchemaRecord.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version 
 */
public class TestAvroSchemaRecord {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String recordSchema;
    private String recordSchemaVpn;
    private String recordSchemaVpnReuse;
    private String recordSchemaInvalidFields;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        recordSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExample.avsc");
        recordSchemaVpn = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPN.avsc");
        recordSchemaVpnReuse = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPNReuse.avsc");
        recordSchemaInvalidFields =
                TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleInvalidFields.avsc");
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
     * Test record init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRecordInit() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final GenericRecord newRecordEmpty = (GenericRecord) schemaHelper.createNewInstance();
        assertEquals(null, newRecordEmpty.get("passwordHash"));

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/RecordExampleFull.json");
        final GenericRecord newRecordFull = (GenericRecord) schemaHelper.createNewInstance(inString);
        assertEquals("gobbledygook", newRecordFull.get("passwordHash").toString());
    }

    /**
     * Test record unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleFull.json");
    }

    /**
     * Test record unmarshal marshal invalid.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRecordUnmarshalMarshalInvalid() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaInvalidFields);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleInvalidFields.json");
    }

    /**
     * Test VPN record unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testVpnRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaVpn);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleVPNFull.json");
    }

    /**
     * Test VPN record reuse.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testVpnRecordReuse() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaVpnReuse);
        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());
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
        final GenericRecord decodedObject = (GenericRecord) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(decodedObject);
        assertEquals(inString.replaceAll("\\s+", ""), outString.replaceAll("\\s+", ""));
    }
}
