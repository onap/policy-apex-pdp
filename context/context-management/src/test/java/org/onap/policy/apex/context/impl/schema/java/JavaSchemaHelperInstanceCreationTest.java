/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation
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

package org.onap.policy.apex.context.impl.schema.java;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

/**
 * JavaSchemaHelperInstanceCreationTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class JavaSchemaHelperInstanceCreationTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;

    /**
     * Set-ups everything for the test.
     */
    @BeforeEach
    public void initTest() {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);

        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    @AfterAll
    public static void cleanUpAfterTest() {
        ParameterService.clear();
    }


    @Test
    void testNullEncoding() {
        final AxContextSchema javaBooleanSchema =
            new AxContextSchema(new AxArtifactKey("Boolean", "0.0.1"), "Java", "java.lang.Boolean");
        final AxContextSchema javaLongSchema =
            new AxContextSchema(new AxArtifactKey("Long", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema javaStringSchema =
            new AxContextSchema(new AxArtifactKey("String", "0.0.1"), "Java", "java.lang.String");

        schemas.getSchemasMap().put(javaBooleanSchema.getKey(), javaBooleanSchema);
        schemas.getSchemasMap().put(javaLongSchema.getKey(), javaLongSchema);
        schemas.getSchemasMap().put(javaStringSchema.getKey(), javaStringSchema);

        final SchemaHelper schemaHelper0 =
            new SchemaHelperFactory().createSchemaHelper(testKey, javaBooleanSchema.getKey());
        final SchemaHelper schemaHelper1 =
            new SchemaHelperFactory().createSchemaHelper(testKey, javaLongSchema.getKey());
        final SchemaHelper schemaHelper2 =
            new SchemaHelperFactory().createSchemaHelper(testKey, javaStringSchema.getKey());

        assertThatThrownBy(schemaHelper0::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Boolean\""
                + " using the default constructor \"Boolean()\"");
        assertEquals(true, schemaHelper0.createNewInstance("true"));

        assertThatThrownBy(schemaHelper1::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Long\""
                + " using the default constructor \"Long()\"");
        assertEquals(65536L, schemaHelper1.createNewInstance("65536"));

        assertEquals("", schemaHelper2.createNewInstance());
        assertEquals("true", schemaHelper2.createNewInstance("true"));

        assertThatThrownBy(() -> schemaHelper1.createNewSubInstance("SomeSubtype"))
            .hasMessage("sub types are not supported on this schema helper");
    }
}
