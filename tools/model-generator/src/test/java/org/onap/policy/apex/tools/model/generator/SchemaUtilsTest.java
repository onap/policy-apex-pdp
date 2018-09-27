/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.tools.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.plugins.context.schema.avro.AvroSchemaHelper;
import org.onap.policy.apex.plugins.context.schema.avro.AvroSchemaHelperParameters;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the Key Info Getter.
 */
public class SchemaUtilsTest {
    private static AxPolicyModel avroModel;

    /**
     * Read the models into strings.
     * 
     * @throws IOException on model reading errors
     * @throws ApexModelException on model reading exceptions
     */
    @BeforeClass
    public static void readSimpleModel() throws IOException, ApexModelException {
        String avroModelString = TextFileUtils.getTextFileAsString("src/test/resources/models/AvroModel.json");

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        avroModel = modelReader.read(new ByteArrayInputStream(avroModelString.getBytes()));
    }

    @Test
    public void testSchemaUtilsErrors() throws ApexEventException {
        AxEvent event = avroModel.getEvents().get("CustomerContextEventIn");
        AxContextSchema avroCtxtSchema = avroModel.getSchemas().get("ctxtTopologyNodesDecl");

        AxArtifactKey topoNodesKey = new AxArtifactKey("albumTopoNodes", "0.0.1");
        try {
            SchemaUtils.getEventSchema(event);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("Model for org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas"
                            + " not found in model service", apEx.getMessage());
        }

        try {
            Map<String, Schema> preexistingParamSchemas = new LinkedHashMap<>();
            SchemaUtils.getEventParameterSchema(event.getParameterMap().get("links"), preexistingParamSchemas);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("Model for org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas"
                            + " not found in model service", apEx.getMessage());
        }

        List<Field> skeletonFields = SchemaUtils.getSkeletonEventSchemaFields();
        assertEquals(5, skeletonFields.size());

        try {
            AvroSchemaHelper schemaHelper = (AvroSchemaHelper) new SchemaHelperFactory()
                            .createSchemaHelper(topoNodesKey, avroCtxtSchema.getKey());

            Map<String, Schema> schemaMap = new LinkedHashMap<>();
            SchemaUtils.processSubSchemas(schemaHelper.getAvroSchema(), schemaMap);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("Model for org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas"
                            + " not found in model service", apEx.getMessage());
        }
    }

    @Test
    public void testSchemaUtils() throws ApexEventException {
        ParameterService.clear();
        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters);

        ModelService.registerModel(AxModel.class, avroModel);
        ModelService.registerModel(AxContextSchemas.class, avroModel.getSchemas());

        AxEvent event = avroModel.getEvents().get("CustomerContextEventIn");
        AxContextSchema avroCtxtSchema = avroModel.getSchemas().get("ctxtTopologyNodesDecl");
        AxArtifactKey topoNodesKey = new AxArtifactKey("albumTopoNodes", "0.0.1");

        Schema eventSchema = SchemaUtils.getEventSchema(event);
        assertEquals("{\"type\":\"record\",\"name\":\"CustomerContextEventIn\"",
                        eventSchema.toString().substring(0, 48));

        Map<String, Schema> preexistingParamSchemas = new LinkedHashMap<>();
        Schema epSchema = SchemaUtils.getEventParameterSchema(event.getParameterMap().get("links"),
                        preexistingParamSchemas);
        assertEquals("\"string\"", epSchema.toString());

        List<Field> skeletonFields = SchemaUtils.getSkeletonEventSchemaFields();
        assertEquals(5, skeletonFields.size());

        try {
            AvroSchemaHelper schemaHelper = (AvroSchemaHelper) new SchemaHelperFactory()
                            .createSchemaHelper(topoNodesKey, avroCtxtSchema.getKey());

            Map<String, Schema> schemaMap = new LinkedHashMap<>();
            SchemaUtils.processSubSchemas(schemaHelper.getAvroSchema(), schemaMap);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("context schema helper parameters not found for context schema  \"Avro\"", apEx.getMessage());
        }

        schemaParameters.getSchemaHelperParameterMap().put("Avro", new AvroSchemaHelperParameters());

        AvroSchemaHelper schemaHelper = (AvroSchemaHelper) new SchemaHelperFactory().createSchemaHelper(topoNodesKey,
                        avroCtxtSchema.getKey());

        Map<String, Schema> schemaMap = new LinkedHashMap<>();
        try {
            SchemaUtils.processSubSchemas(schemaHelper.getAvroSchema(), schemaMap);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }

        eventSchema = SchemaUtils.getEventSchema(event);
        assertEquals("{\"type\":\"record\",\"name\":\"CustomerContextEventIn\"",
                        eventSchema.toString().substring(0, 48));

        epSchema = SchemaUtils.getEventParameterSchema(event.getParameterMap().get("links"), preexistingParamSchemas);
        assertEquals("\"string\"", epSchema.toString());

        AxInputField inField = new AxInputField(new AxReferenceKey("FieldParent", "0.0.1", "Field"),
                        avroCtxtSchema.getKey(), false);

        Schema ep2Schema = SchemaUtils.getEventParameterSchema(inField, preexistingParamSchemas);
        assertEquals("{\"type\":\"record\",\"name\":\"TopologyNodes\"", ep2Schema.toString().substring(0, 39));

        skeletonFields = SchemaUtils.getSkeletonEventSchemaFields();
        assertEquals(5, skeletonFields.size());

        schemaParameters.getSchemaHelperParameterMap().put("Avro", new JavaSchemaHelperParameters());
        try {
            ep2Schema = SchemaUtils.getEventParameterSchema(inField, preexistingParamSchemas);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("FieldParent:0.0.1:NULL:Field: class/type", apEx.getMessage().substring(0, 40));
        }

        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ModelService.clear();
    }
}
