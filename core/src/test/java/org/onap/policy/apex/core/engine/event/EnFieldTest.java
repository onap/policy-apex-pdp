/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the EnField class.
 */
class EnFieldTest {
    /**
     * Set up the services.
     */
    @BeforeEach
    void setupServices() {
        AxContextSchemas schemas = new AxContextSchemas();
        ModelService.registerModel(AxContextSchemas.class, schemas);
        ParameterService.register(new SchemaParameters());
    }

    /**
     * Tear down the services.
     */
    @AfterEach
    void teardownServices() {
        ModelService.deregisterModel(AxContextSchemas.class);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    void testEnField() {
        AxReferenceKey fieldKey = new AxReferenceKey("Parent", "0.0.1", "MyParent", "MyField");
        AxArtifactKey fieldSchemaKey = new AxArtifactKey("FieldSchema:0.0.1");
        AxField axField = new AxField(fieldKey, fieldSchemaKey);

        assertThatThrownBy(() -> new EnField(axField, null))
            .hasMessage("schema helper cannot be created for parameter with key \"Parent:0.0.1:MyParent:My"
                + "Field\" with schema \"AxArtifactKey:(name=FieldSchema,version=0.0.1)\"");
        AxContextSchema schema = new AxContextSchema(fieldSchemaKey, "Java", "java.lang.Integer");
        ModelService.getModel(AxContextSchemas.class).getSchemasMap().put(fieldSchemaKey, schema);
        EnField field = new EnField(axField, 123);

        assertEquals(axField, field.getAxField());
        assertEquals(123, field.getValue());
        assertEquals(fieldKey, field.getKey());
        assertEquals("MyField", field.getName());
        assertEquals("org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelper",
            field.getSchemaHelper().getClass().getName());
        assertEquals(123, field.getAssignableValue());
        assertEquals("EnField [axField=AxField:(key=AxReferenceKey:(parentKeyName=Parent,parentKeyVersion=0.0.1,"
            + "parentLocalName=MyParent,localName=MyField),fieldSchemaKey=AxArtifactKey:"
            + "(name=FieldSchema,version=0.0.1),optional=false), value=123]", field.toString());
        assertTrue(field.isAssignableValue());

        field = new EnField(axField, "Hello");
        assertFalse(field.isAssignableValue());
    }
}
