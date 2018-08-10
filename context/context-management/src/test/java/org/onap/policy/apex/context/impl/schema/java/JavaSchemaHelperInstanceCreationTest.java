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

package org.onap.policy.apex.context.impl.schema.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * JavaSchemaHelperInstanceCreationTest.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
public class JavaSchemaHelperInstanceCreationTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;

    @Before
    public void initTest() {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        new SchemaParameters();
    }

    @Test
    public void testNullEncoding() {
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

        try {
            schemaHelper0.createNewInstance();
            fail("this test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("AvroTest:0.0.1: could not create an instance of class \"java.lang.Boolean\" using the default"
                    + " constructor \"Boolean()\"", e.getMessage());
        }
        assertEquals(true, schemaHelper0.createNewInstance("true"));


        try {
            schemaHelper1.createNewInstance();
            fail("this test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("AvroTest:0.0.1: could not create an instance of class \"java.lang.Long\" using the default "
                    + "constructor \"Long()\"", e.getMessage());
        }
        assertEquals(65536L, schemaHelper1.createNewInstance("65536"));

        assertEquals("", schemaHelper2.createNewInstance());
        assertEquals("true", schemaHelper2.createNewInstance("true"));
    }
}
