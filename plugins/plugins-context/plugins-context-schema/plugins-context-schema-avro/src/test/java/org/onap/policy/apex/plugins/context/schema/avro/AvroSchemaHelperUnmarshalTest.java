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

import org.apache.avro.util.Utf8;
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

/**
 * The Class TestAvroSchemaHelperUnmarshal.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaHelperUnmarshalTest {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;

    /**
     * Inits the test.
     */
    @BeforeEach
    void initTest() {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
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
     * Test null unmarshal.
     */
    @Test
    void testNullUnmarshal() {
        final AxContextSchema avroNullSchema = new AxContextSchema(new AxArtifactKey("AvroNull", "0.0.1"), "AVRO",
            "{\"type\": \"null\"}");

        schemas.getSchemasMap().put(avroNullSchema.getKey(), avroNullSchema);
        final SchemaHelper schemaHelper0 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroNullSchema.getKey());

        assertThatThrownBy(schemaHelper0::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance, schema class for the schema is null");
        assertEquals(null, schemaHelper0.unmarshal("null"));

        assertThatThrownBy(() -> schemaHelper0.unmarshal("123"))
            .hasMessage("AvroTest:0.0.1: object \"123\" Avro unmarshalling failed.");
    }

    /**
     * Test boolean unmarshal.
     */
    @Test
    void testBooleanUnmarshal() {
        final AxContextSchema avroBooleanSchema = new AxContextSchema(new AxArtifactKey("AvroBoolean", "0.0.1"), "AVRO",
            "{\"type\": \"boolean\"}");

        schemas.getSchemasMap().put(avroBooleanSchema.getKey(), avroBooleanSchema);
        final SchemaHelper schemaHelper1 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroBooleanSchema.getKey());

        assertThatThrownBy(schemaHelper1::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Boolean\" "
                + "using the default constructor \"Boolean()\"");
        assertEquals(true, schemaHelper1.createNewInstance("true"));

        assertEquals(true, schemaHelper1.unmarshal("true"));
        assertEquals(false, schemaHelper1.unmarshal("false"));
        assertThatThrownBy(() -> schemaHelper1.unmarshal(0))
            .hasMessage("AvroTest:0.0.1: object \"0\" of type \"java.lang.Integer\" must be assignable to "
                + "\"java.lang.Boolean\" or be a Json string representation of it for "
                + "Avro unmarshalling");
    }

    /**
     * Test int unmarshal.
     */
    @Test
    void testIntUnmarshal() {
        final AxContextSchema avroIntSchema = new AxContextSchema(new AxArtifactKey("AvroInt", "0.0.1"), "AVRO",
            "{\"type\": \"int\"}");

        schemas.getSchemasMap().put(avroIntSchema.getKey(), avroIntSchema);
        final SchemaHelper schemaHelper2 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroIntSchema.getKey());

        assertThatThrownBy(schemaHelper2::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Integer\" "
                + "using the default constructor \"Integer()\"");
        assertEquals(123, schemaHelper2.createNewInstance("123"));

        assertEquals(0, schemaHelper2.unmarshal("0"));
        assertEquals(1, schemaHelper2.unmarshal("1"));
        assertEquals(-1, schemaHelper2.unmarshal("-1"));
        assertEquals(1, schemaHelper2.unmarshal("1.23"));
        assertEquals(-1, schemaHelper2.unmarshal("-1.23"));
        assertEquals(2147483647, schemaHelper2.unmarshal("2147483647"));
        assertEquals(-2147483648, schemaHelper2.unmarshal("-2147483648"));
        assertThatThrownBy(() -> schemaHelper2.unmarshal("2147483648"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"2147483648\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper2.unmarshal("-2147483649"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"-2147483649\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper2.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }

    /**
     * Test long unmarshal.
     */
    @Test
    void testLongUnmarshal() {
        final AxContextSchema avroLongSchema = new AxContextSchema(new AxArtifactKey("AvroLong", "0.0.1"), "AVRO",
            "{\"type\": \"long\"}");

        schemas.getSchemasMap().put(avroLongSchema.getKey(), avroLongSchema);
        final SchemaHelper schemaHelper3 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroLongSchema.getKey());

        assertThatThrownBy(schemaHelper3::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Long\" "
                + "using the default constructor \"Long()\"");
        assertEquals(123456789L, schemaHelper3.createNewInstance("123456789"));

        assertEquals(0L, schemaHelper3.unmarshal("0"));
        assertEquals(1L, schemaHelper3.unmarshal("1"));
        assertEquals(-1L, schemaHelper3.unmarshal("-1"));
        assertEquals(1L, schemaHelper3.unmarshal("1.23"));
        assertEquals(-1L, schemaHelper3.unmarshal("-1.23"));
        assertEquals(9223372036854775807L, schemaHelper3.unmarshal("9223372036854775807"));
        assertEquals(-9223372036854775808L, schemaHelper3.unmarshal("-9223372036854775808"));
        assertThatThrownBy(() -> schemaHelper3.unmarshal("9223372036854775808"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"9223372036854775808\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper3.unmarshal("-9223372036854775809"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"-9223372036854775809\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper3.unmarshal("\"Hello\""))
            .hasMessage("AvroTest:0.0.1: object \"\"Hello\"\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper3.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }

    /**
     * Test float unmarshal.
     */
    @Test
    void testFloatUnmarshal() {
        final AxContextSchema avroFloatSchema = new AxContextSchema(new AxArtifactKey("AvroFloat", "0.0.1"), "AVRO",
            "{\"type\": \"float\"}");

        schemas.getSchemasMap().put(avroFloatSchema.getKey(), avroFloatSchema);
        final SchemaHelper schemaHelper4 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroFloatSchema.getKey());

        assertThatThrownBy(schemaHelper4::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Float\" "
                + "using the default constructor \"Float()\"");
        assertEquals(1.2345F, schemaHelper4.createNewInstance("1.2345"));

        assertEquals(0.0F, schemaHelper4.unmarshal("0"));
        assertEquals(1.0F, schemaHelper4.unmarshal("1"));
        assertEquals(-1.0F, schemaHelper4.unmarshal("-1"));
        assertEquals(1.23F, schemaHelper4.unmarshal("1.23"));
        assertEquals(-1.23F, schemaHelper4.unmarshal("-1.23"));
        assertEquals(9.223372E18F, schemaHelper4.unmarshal("9223372036854775807"));
        assertEquals(-9.223372E18F, schemaHelper4.unmarshal("-9223372036854775808"));
        assertEquals(9.223372E18F, schemaHelper4.unmarshal("9223372036854775808"));
        assertEquals(-9.223372E18F, schemaHelper4.unmarshal("-9223372036854775809"));
        assertThatThrownBy(() -> schemaHelper4.unmarshal("\"Hello\""))
            .hasMessage("AvroTest:0.0.1: object \"\"Hello\"\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper4.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }

    /**
     * Test double unmarshal.
     */
    @Test
    void testDoubleUnmarshal() {
        final AxContextSchema avroDoubleSchema = new AxContextSchema(new AxArtifactKey("AvroDouble", "0.0.1"), "AVRO",
            "{\"type\": \"double\"}");

        schemas.getSchemasMap().put(avroDoubleSchema.getKey(), avroDoubleSchema);
        final SchemaHelper schemaHelper5 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroDoubleSchema.getKey());

        assertThatThrownBy(schemaHelper5::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"java.lang.Double\" "
                + "using the default constructor \"Double()\"");
        assertEquals(1.2345E06, schemaHelper5.createNewInstance("1.2345E06"));

        assertEquals(0.0, schemaHelper5.unmarshal("0"));
        assertEquals(1.0, schemaHelper5.unmarshal("1"));
        assertEquals(-1.0, schemaHelper5.unmarshal("-1"));
        assertEquals(1.23, schemaHelper5.unmarshal("1.23"));
        assertEquals(-1.23, schemaHelper5.unmarshal("-1.23"));
        assertEquals(9.223372036854776E18, schemaHelper5.unmarshal("9223372036854775807"));
        assertEquals(-9.223372036854776E18, schemaHelper5.unmarshal("-9223372036854775808"));
        assertEquals(9.223372036854776E18, schemaHelper5.unmarshal("9223372036854775808"));
        assertEquals(-9.223372036854776E18, schemaHelper5.unmarshal("-9223372036854775809"));
        assertThatThrownBy(() -> schemaHelper5.unmarshal("\"Hello\""))
            .hasMessage("AvroTest:0.0.1: object \"\"Hello\"\" Avro unmarshalling failed.");
        assertThatThrownBy(() -> schemaHelper5.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }

    /**
     * Test string unmarshal.
     */
    @Test
    void testStringUnmarshal() {
        final AxContextSchema avroStringSchema = new AxContextSchema(new AxArtifactKey("AvroString", "0.0.1"), "AVRO",
            "{\"type\": \"string\"}");

        schemas.getSchemasMap().put(avroStringSchema.getKey(), avroStringSchema);
        final SchemaHelper schemaHelper7 = new SchemaHelperFactory().createSchemaHelper(testKey,
            avroStringSchema.getKey());

        assertEquals("", schemaHelper7.createNewInstance(""));
        assertEquals("1.2345E06", schemaHelper7.createNewInstance("1.2345E06"));

        assertEquals("0", schemaHelper7.unmarshal("0"));
        assertEquals("1", schemaHelper7.unmarshal("1"));
        assertEquals("-1", schemaHelper7.unmarshal("-1"));
        assertEquals("1.23", schemaHelper7.unmarshal("1.23"));
        assertEquals("-1.23", schemaHelper7.unmarshal("-1.23"));
        assertEquals("9223372036854775807", schemaHelper7.unmarshal("9223372036854775807"));
        assertEquals("-9223372036854775808", schemaHelper7.unmarshal("-9223372036854775808"));
        assertEquals("9223372036854775808", schemaHelper7.unmarshal("9223372036854775808"));
        assertEquals("-9223372036854775809", schemaHelper7.unmarshal("-9223372036854775809"));
        assertEquals("Hello", schemaHelper7.unmarshal("Hello"));
        assertEquals("Hello", schemaHelper7.unmarshal(new Utf8("Hello")));
        assertThatThrownBy(() -> schemaHelper7.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }

    /**
     * Test bytes unmarshal.
     */
    @Test
    void testBytesUnmarshal() {
        final AxContextSchema avroSchema = new AxContextSchema(new AxArtifactKey("AvroString", "0.0.1"), "AVRO",
            "{\"type\": \"bytes\"}");

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        assertThatThrownBy(schemaHelper::createNewInstance)
            .hasMessage("AvroTest:0.0.1: could not create an instance of class \"[Ljava.lang.Byte;\" "
                + "using the default constructor \"Byte[]()\"");
        final byte[] newBytes = (byte[]) schemaHelper.createNewInstance("\"hello\"");
        assertEquals(5, newBytes.length);
        assertEquals(104, newBytes[0]);
        assertEquals(101, newBytes[1]);
        assertEquals(108, newBytes[2]);
        assertEquals(108, newBytes[3]);
        assertEquals(111, newBytes[4]);

        assertThatThrownBy(() -> schemaHelper.unmarshal(null))
            .hasMessage("AvroTest:0.0.1: object \"null\" Avro unmarshalling failed.");
    }
}
