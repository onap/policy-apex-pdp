/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.AfterEach;
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
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * The Class TestAvroSchemaAai.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaAaiTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String aaiInventoryResponseSchema;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);

        aaiInventoryResponseSchema =
            TextFileUtils.getTextFileAsString("src/test/resources/avsc/AAIInventoryResponseItemType.avsc");
    }

    /**
     * Inits the context.
     */
    @BeforeEach
    void initContext() {
        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("AVRO", new AvroSchemaHelperParameters());
        ParameterService.register(schemaParameters);

    }

    /**
     * Clear context.
     */
    @AfterEach
    void clearContext() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    /**
     * Test AAI response policy.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testAaiResponsePolicy() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", aaiInventoryResponseSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final Array<?> newArrayEmpty = (Array<?>) schemaHelper.createNewInstance();
        assertEquals(0, newArrayEmpty.size());

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/AAIResponse4Policy.json");
        final Array<?> newArrayFull = (Array<?>) schemaHelper.createNewInstance(inString);
        final String vnfName = ((GenericRecord) ((GenericRecord) newArrayFull.get(0)).get("generic_DasH_vnf"))
            .get("vnf_DasH_name").toString();
        assertEquals("ZRDM2MMEX39", vnfName);
    }
}
