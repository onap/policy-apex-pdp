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

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
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

/**
 * The Class TestHealthCheckSchema.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class HealthCheckSchemaTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String healthCheckSchema;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);

        healthCheckSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/HealthCheckBodyType.avsc");
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
     * Test health check.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testHealthCheck() throws IOException {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO",
                        healthCheckSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/HealthCheckEvent.json");

        final GenericRecord healthCheckRecord = (Record) schemaHelper.createNewInstance();
        final Schema healthCheckRecordSchema = healthCheckRecord.getSchema();

        final GenericRecord inputRecord = new GenericData.Record(healthCheckRecordSchema.getField("input").schema());
        final Schema inputRecordRecordSchema = inputRecord.getSchema();

        final GenericRecord actionIndentifiersRecord = new GenericData.Record(
                        inputRecordRecordSchema.getField("action_DasH_identifiers").schema());

        final GenericRecord commonHeaderRecord = new GenericData.Record(
                        inputRecordRecordSchema.getField("common_DasH_header").schema());
        final Schema commonHeaderRecordSchema = commonHeaderRecord.getSchema();

        final GenericRecord commonHeaderFlagsRecord = new GenericData.Record(
                        commonHeaderRecordSchema.getField("flags").schema());

        healthCheckRecord.put("input", inputRecord);
        inputRecord.put("action_DasH_identifiers", actionIndentifiersRecord);
        inputRecord.put("common_DasH_header", commonHeaderRecord);
        commonHeaderRecord.put("flags", commonHeaderFlagsRecord);

        inputRecord.put("action", "HealthCheck");
        inputRecord.put("payload", "{\"host-ip-address\":\"131.160.203.125\",\"input.url\":\"131.160.203.125/afr\","
                        + "\"request-action-type\":\"GET\",\"request-action\":\"AFR\"}");

        actionIndentifiersRecord.put("vnf_DasH_id", "49414df5-3482-4fd8-9952-c463dff2770b");

        commonHeaderRecord.put("request_DasH_id", "afr-request3");
        commonHeaderRecord.put("originator_DasH_id", "AFR");
        commonHeaderRecord.put("api_DasH_ver", "2.15");
        commonHeaderRecord.put("sub_DasH_request_DasH_id", "AFR-subrequest");
        commonHeaderRecord.put("timestamp", "2017-11-06T15:15:18.97Z");

        commonHeaderFlagsRecord.put("ttl", "10000");
        commonHeaderFlagsRecord.put("force", "TRUE");
        commonHeaderFlagsRecord.put("mode", "EXCLUSIVE");

        final String eventString = TextFileUtils.getTextFileAsString("src/test/resources/data/HealthCheckEvent.json");
        final String outString = schemaHelper.marshal2String(healthCheckRecord);
        assertEquals(eventString.toString().replaceAll("\\s+", ""), outString.replaceAll("\\s+", ""));
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
