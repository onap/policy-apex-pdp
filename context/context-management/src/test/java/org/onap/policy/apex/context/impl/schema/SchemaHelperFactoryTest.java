/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

package org.onap.policy.apex.context.impl.schema;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.parameters.ParameterService;

public class SchemaHelperFactoryTest {
    private static AxContextSchema intSchema;
    private static AxContextSchemas schemas;
    private static AxContextSchema badSchema;

    /**
     * Set-ups schema for the test.
     */
    @BeforeAll
    public static void setupSchema() {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);

        intSchema = new AxContextSchema(new AxArtifactKey("IntSchema", "0.0.1"), "JAVA", "java.lang.Integer");
        badSchema = new AxContextSchema(new AxArtifactKey("IntSchema", "0.0.1"), "JAVA", "java.lang.Bad");
    }

    @AfterAll
    public static void clearParameters() {
        ParameterService.clear();
    }

    @Test
    void testSchemaHelperFactory() {
        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(null, null))
            .hasMessage("Parameter \"owningEntityKey\" may not be null");
        AxArtifactKey ownerKey = new AxArtifactKey("Owner", "0.0.1");
        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(ownerKey, null))
            .hasMessage("Parameter \"schemaKey\" may not be null");
        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(ownerKey, intSchema.getKey()))
            .hasMessage("schema \"IntSchema:0.0.1\" for entity Owner:0.0.1 does not exist");
        schemas.getSchemasMap().put(intSchema.getKey(), intSchema);
        SchemaParameters schemaParameters0 = new SchemaParameters();
        schemaParameters0.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters0);
        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(ownerKey, intSchema.getKey()))
            .hasMessage("context schema helper parameters not found for context schema  \"JAVA\"");
        ParameterService.deregister(schemaParameters0);

        SchemaParameters schemaParameters1 = new SchemaParameters();
        schemaParameters1.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters1);
        schemaParameters1.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());
        assertNotNull(new SchemaHelperFactory().createSchemaHelper(ownerKey, intSchema.getKey()));

        schemas.getSchemasMap().put(intSchema.getKey(), badSchema);
        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(ownerKey, badSchema.getKey()))
            .hasMessage("Owner:0.0.1: class/type java.lang.Bad for context schema \"IntSchema:0.0.1\" "
                            + "not found. Check the class path of the JVM");
    }
}
