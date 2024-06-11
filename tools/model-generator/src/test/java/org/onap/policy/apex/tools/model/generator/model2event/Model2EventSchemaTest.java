/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.tools.model.generator.model2event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.common.parameters.ParameterService;
import org.stringtemplate.v4.STGroupFile;

/**
 * Test the Model2EventSchema.
 */
class Model2EventSchemaTest {
    String modelFile = "src/test/resources/blankSchema.json";
    String type = "stimuli";

    /**
     * The name of the application.
     */
    private static final String APP_NAME = "gen-model2eventSchema";

    /**
     * Set-ups parameterService for the test.
     */
    @BeforeAll
    static void prepareForTest() {
        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        if (!ParameterService.contains(ContextParameterConstants.SCHEMA_GROUP_NAME)) {
            ParameterService.register(schemaParameters);
        }
    }

    @Test
    void testEventSchemaBadModelFile() {
        Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
        assertThatCode(() -> {
            int ret = app.runApp();
            assertEquals(-1, ret);
        }).doesNotThrowAnyException();

    }

    @Test
    void testEventSchemaBadType() {
        modelFile = "src/test/resources/SmallModel.json";
        type = "default";
        Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
        assertThatCode(() -> {
            int ret = app.runApp();
            assertEquals(-1, ret);
        }).doesNotThrowAnyException();
    }

    @Test
    void testEventSchemaStimuli() throws ApexException {
        modelFile = "src/test/resources/SmallModel.json";

        String[] types = {"stimuli", "response", "internal"};
        for (String type2 : types) {
            type = type2;
            Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
            assertEquals(0, app.runApp(), type);
        }
    }

    @Test
    void testEventSchemaNotSimpleType() {
        modelFile = "src/test/resources/ExecutionPropertiesRestTestPolicyModel.json";
        type = "internal";
        Model2JsonEventSchema app = new Model2JsonEventSchema(modelFile, type, APP_NAME);
        final STGroupFile stg = new STGroupFile("org/onap/policy/apex/tools/model/generator/event-json.stg");

        Field stringField = new Field("string", Schema.create(Type.STRING), null, null);
        Field enumField =
            new Field("enum", Schema.createEnum("my_enum", "doc", null, Arrays.asList("a", "b", "c")), null, null);
        Schema schema = Schema.createRecord("my_record", "doc", "mytest", false);
        schema.setFields(Arrays.asList(stringField, enumField));
        Schema arrayOut = Schema.createArray(schema);
        Schema mapOut = Schema.createMap(arrayOut);
        app.addFieldType(mapOut, stg);
        assertThatCode(() -> {
            int ret = app.runApp();
            assertEquals(0, ret);
        }).doesNotThrowAnyException();
    }

    @AfterAll
    static void cleanTest() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ModelService.clear();
    }
}
