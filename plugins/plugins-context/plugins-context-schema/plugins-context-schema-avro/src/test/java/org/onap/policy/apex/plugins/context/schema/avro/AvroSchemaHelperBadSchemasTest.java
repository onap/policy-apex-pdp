/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
 * The Class TestAvroSchemaHelperBadSchemas.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version 
 */
public class AvroSchemaHelperBadSchemasTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;

    /**
     * Inits the test.
     */
    @Before
    public void initTest() {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
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
     * Bad schema test.
     */
    @Test
    public void badSchemaTest() {
        final AxContextSchema avroBadSchema0 = new AxContextSchema(new AxArtifactKey("AvroBad0", "0.0.1"), "AVRO", "}");
        schemas.getSchemasMap().put(avroBadSchema0.getKey(), avroBadSchema0);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema0.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad0:0.0.1\" schema is invalid");
        final AxContextSchema avroBadSchema1 = new AxContextSchema(new AxArtifactKey("AvroBad1", "0.0.1"), "AVRO", "");
        schemas.getSchemasMap().put(avroBadSchema1.getKey(), avroBadSchema1);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema1.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad1:0.0.1\" schema is invalid");
        final AxContextSchema avroBadSchema2 =
                new AxContextSchema(new AxArtifactKey("AvroBad2", "0.0.1"), "AVRO", "{}");
        schemas.getSchemasMap().put(avroBadSchema2.getKey(), avroBadSchema2);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema2.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad2:0.0.1\" schema is invalid");
        final AxContextSchema avroBadSchema3 =
                new AxContextSchema(new AxArtifactKey("AvroBad3", "0.0.1"), "AVRO", "{zooby}");
        schemas.getSchemasMap().put(avroBadSchema3.getKey(), avroBadSchema3);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema3.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad3:0.0.1\" schema is invalid");
        final AxContextSchema avroBadSchema4 =
                new AxContextSchema(new AxArtifactKey("AvroBad4", "0.0.1"), "AVRO", "{\"zooby\"}");
        schemas.getSchemasMap().put(avroBadSchema4.getKey(), avroBadSchema4);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema4.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad4:0.0.1\" schema is invalid");
        final AxContextSchema avroBadSchema5 =
                new AxContextSchema(new AxArtifactKey("AvroBad5", "0.0.1"), "AVRO", "{\"type\": \"zooby\"}");
        schemas.getSchemasMap().put(avroBadSchema5.getKey(), avroBadSchema5);

        assertThatThrownBy(() -> new SchemaHelperFactory().createSchemaHelper(testKey, avroBadSchema5.getKey()))
            .hasMessageStartingWith("AvroTest:0.0.1: avro context schema \"AvroBad5:0.0.1\" schema is invalid");
    }
}
