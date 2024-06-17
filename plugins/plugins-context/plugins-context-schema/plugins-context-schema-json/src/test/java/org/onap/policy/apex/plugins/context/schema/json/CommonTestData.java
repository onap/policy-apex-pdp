/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Bell Canada. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.schema.json;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.TextFileUtils;

public class CommonTestData {

    protected static final String VERSION = "0.0.1";
    protected static final String JSON = "JSON";
    protected static final String TEST_ID = "testId";
    protected final AxKey testKey = new AxArtifactKey("JsonTest", VERSION);
    protected AxContextSchemas schemas;
    protected final StandardCoder coder = new StandardCoder();

    protected static final String BOOLEAN_SCHEMA =
        "{\"$schema\": \"http://json-schema.org/draft-07/schema#\",\"type\": \"boolean\"}";
    protected static final String BOOLEAN_DATA = "true";
    protected static final String NULL_SCHEMA =
        "{\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"type\":\"null\"}";
    protected static final String NULL_DATA = "null";
    protected static String COMMONHEADERTYPE_DRAFT04;
    protected static String COMMONHEADERTYPE_DRAFT07;
    protected static String COMMONHEADERTYPE_WITH_OPTIONAL;
    protected static String COMMONHEADER;
    protected static String MEASUREMENTGROUPS_TYPE;
    protected static String MEASUREMENTGROUPS;

    /**
     * Setup before all tests.
     */
    @BeforeAll
    public static void setUpBeforeClass() throws IOException {
        COMMONHEADERTYPE_DRAFT04 =
            TextFileUtils.getTextFileAsString("src/test/resources/schema/commonHeaderType_draft04.json");
        COMMONHEADERTYPE_DRAFT07 =
            TextFileUtils.getTextFileAsString("src/test/resources/schema/commonHeaderType_draft07.json");
        COMMONHEADERTYPE_WITH_OPTIONAL =
            TextFileUtils.getTextFileAsString("src/test/resources/schema/commonHeaderTypeWithOptional.json");
        COMMONHEADER =
            TextFileUtils.getTextFileAsString("src/test/resources/data/commonHeader.json").replaceAll("\r", "").trim();
        MEASUREMENTGROUPS_TYPE =
            TextFileUtils.getTextFileAsString("src/test/resources/schema/measurementGroupsType.json");
        MEASUREMENTGROUPS = TextFileUtils.getTextFileAsString("src/test/resources/data/measurementGroups.json")
            .replaceAll("\r", "").trim();
    }

    /**
     * Setup before test.
     */
    @BeforeEach
    public void setUp() {
        schemas = new AxContextSchemas(new AxArtifactKey("JsonSchema", VERSION));
        ModelService.registerModel(AxContextSchemas.class, schemas);

        SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put(JSON, new JsonSchemaHelperParameters());
        ParameterService.register(schemaParameters);
    }

    /**
     * Teardown after test.
     */
    @AfterEach
    public void tearDown() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ModelService.clear();
    }

    /**
     * Method to create JsonSchemaHelper instance from schema content.
     *
     * @param schema the schema content as string
     * @return schemaHelper instance
     */
    protected SchemaHelper createSchema(String schema) {
        final AxContextSchema jsonSchema = new AxContextSchema(new AxArtifactKey("SchemaTest", VERSION), JSON, schema);
        schemas.getSchemasMap().put(jsonSchema.getKey(), jsonSchema);
        return new SchemaHelperFactory().createSchemaHelper(testKey, jsonSchema.getKey());
    }
}
