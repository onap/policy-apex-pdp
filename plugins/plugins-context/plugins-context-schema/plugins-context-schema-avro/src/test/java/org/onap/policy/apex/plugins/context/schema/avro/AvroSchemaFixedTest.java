/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.avro.generic.GenericData.Fixed;
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
 * The Class TestAvroSchemaFixed.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaFixedTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String fixedSchema;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        fixedSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/FixedSchema.avsc");
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
     * Test fixed init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testFixedInit() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", fixedSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        assertThatThrownBy(schemaHelper::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance "
                + "of class \"org.apache.avro.generic.GenericData$Fixed\" "
                + "using the default constructor \"Fixed()\"");
        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/FixedExampleGood.json");
        final Fixed newFixedFull = (Fixed) schemaHelper.createNewInstance(inString);
        assertTrue(newFixedFull.toString().startsWith("[48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65"));
        assertTrue(newFixedFull.toString().endsWith("53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70]"));
    }

    /**
     * Test fixed unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testFixedUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroArray", "0.0.1"), "AVRO", fixedSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/FixedExampleGood.json");

        assertThatThrownBy(() -> testUnmarshalMarshal(schemaHelper, "src/test/resources/data/FixedExampleNull.json"))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> testUnmarshalMarshal(schemaHelper, "src/test/resources/data/FixedExampleNull.json"))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> testUnmarshalMarshal(schemaHelper, "src/test/resources/data/FixedExampleBad0.json"))
            .hasMessage("AvroTest:0.0.1: object \"\"BADBAD\"\" " + "Avro unmarshalling failed.");
        assertThatThrownBy(() -> testUnmarshalMarshal(schemaHelper, "src/test/resources/data/FixedExampleBad1.json"))
            .hasMessage(
                "AvroTest:0.0.1: object " + "\"\"0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0\"\" "
                    + "Avro unmarshalling failed.");
    }

    /**
     * Test unmarshal marshal.
     *
     * @param schemaHelper the schema helper
     * @param fileName     the file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String inString = TextFileUtils.getTextFileAsString(fileName);
        final Fixed decodedObject = (Fixed) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(decodedObject);
        assertEquals(inString.replaceAll("[\\r?\\n]+", " "), outString.replaceAll("[\\r?\\n]+", " "));
    }
}
