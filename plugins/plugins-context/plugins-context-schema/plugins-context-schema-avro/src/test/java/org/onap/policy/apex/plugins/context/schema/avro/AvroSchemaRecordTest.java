/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
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
 * The Class TestAvroSchemaRecord.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaRecordTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String recordSchema;
    private String recordSchemaVpn;
    private String recordSchemaVpnReuse;
    private String recordSchemaInvalidFields;

    /**
     * Inits the test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        recordSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExample.avsc");
        recordSchemaVpn = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPN.avsc");
        recordSchemaVpnReuse = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPNReuse.avsc");
        recordSchemaInvalidFields =
            TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleInvalidFields.avsc");
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
     * Test record init.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testRecordInit() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final GenericRecord newRecordEmpty = (GenericRecord) schemaHelper.createNewInstance();
        assertNull(newRecordEmpty.get("passwordHash"));

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/RecordExampleFull.json");
        final GenericRecord newRecordFull = (GenericRecord) schemaHelper.createNewInstance(inString);
        assertEquals("gobbledygook", newRecordFull.get("passwordHash").toString());
    }

    /**
     * Test record unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleFull.json");
    }

    /**
     * Test record create.
     *
     */
    @Test
    void testRecordCreateRecord() {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final GenericRecord subRecord0 = (GenericRecord) schemaHelper.createNewSubInstance("AddressUSRecord");
        assertThatThrownBy(() -> subRecord0.get("address")).hasMessage("Not a valid schema field: address");

        final GenericRecord subRecord1 = (GenericRecord) schemaHelper.createNewSubInstance("EmailAddress");
        assertThatThrownBy(() -> subRecord1.get("EmailAddress")).hasMessage("Not a valid schema field: EmailAddress");

        assertThatThrownBy(() -> schemaHelper.createNewSubInstance("IDontExist"))
            .hasMessage("AvroTest:0.0.1: the schema \"User\" does not have a subtype of type \"IDontExist\"");
    }

    /**
     * Test record unmarshal marshal invalid.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testRecordUnmarshalMarshalInvalid() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaInvalidFields);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleInvalidFields.json");
    }

    /**
     * Test VPN record unmarshal marshal.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    void testVpnRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaVpn);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleVPNFull.json");
    }

    /**
     * Test VPN record reuse.
     *
     */
    @Test
    void testVpnRecordReuse() {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "AVRO", recordSchemaVpnReuse);
        assertNotNull(avroSchema);
        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());
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
        final GenericRecord decodedObject = (GenericRecord) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(decodedObject);
        assertEquals(inString.replaceAll("\\s+", ""), outString.replaceAll("\\s+", ""));
    }
}
