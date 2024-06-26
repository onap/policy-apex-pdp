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
 * The Class TestAvroSchemaHelperMarshal.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class AvroSchemaHelperMarshalTest {
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
     * Test null marshal.
     */
    @Test
    void testNullMarshal() {
        final AxContextSchema avroNullSchema =
            new AxContextSchema(new AxArtifactKey("AvroNull", "0.0.1"), "AVRO", "{\"type\": \"null\"}");

        schemas.getSchemasMap().put(avroNullSchema.getKey(), avroNullSchema);
        final SchemaHelper schemaHelper0 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroNullSchema.getKey());

        assertEquals("null", schemaHelper0.marshal2String(null));
        assertEquals("null", schemaHelper0.marshal2String(123));
        assertEquals("null", schemaHelper0.marshal2String("Everything is marshalled to Null, no matter what it is"));
    }

    /**
     * Test boolean marshal.
     */
    @Test
    void testBooleanMarshal() {
        final AxContextSchema avroBooleanSchema =
            new AxContextSchema(new AxArtifactKey("AvroBoolean", "0.0.1"), "AVRO", "{\"type\": \"boolean\"}");

        schemas.getSchemasMap().put(avroBooleanSchema.getKey(), avroBooleanSchema);
        final SchemaHelper schemaHelper1 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroBooleanSchema.getKey());

        assertEquals("true", schemaHelper1.marshal2String(true));
        assertEquals("false", schemaHelper1.marshal2String(false));
        assertThatThrownBy(() -> schemaHelper1.marshal2String(0))
            .hasMessage("AvroTest:0.0.1: object \"0\" Avro marshalling failed.");
        assertThatThrownBy(() -> schemaHelper1.marshal2String("0"))
            .hasMessage("AvroTest:0.0.1: object \"0\" Avro marshalling failed.");
    }

    /**
     * Test int marshal.
     */
    @Test
    void testIntMarshal() {
        final AxContextSchema avroIntSchema =
            new AxContextSchema(new AxArtifactKey("AvroInt", "0.0.1"), "AVRO", "{\"type\": \"int\"}");

        schemas.getSchemasMap().put(avroIntSchema.getKey(), avroIntSchema);
        final SchemaHelper schemaHelper2 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroIntSchema.getKey());

        assertEquals("0", schemaHelper2.marshal2String(0));
        assertEquals("1", schemaHelper2.marshal2String(1));
        assertEquals("-1", schemaHelper2.marshal2String(-1));
        assertEquals("1", schemaHelper2.marshal2String(1.23));
        assertEquals("-1", schemaHelper2.marshal2String(-1.23));
        assertEquals("2147483647", schemaHelper2.marshal2String(2147483647));
        assertEquals("-2147483648", schemaHelper2.marshal2String(-2147483648));
        assertThatThrownBy(() -> schemaHelper2.marshal2String("Hello"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"Hello\" Avro marshalling failed.");
        assertThatThrownBy(() -> schemaHelper2.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"java.lang.Integer\"");
    }

    /**
     * Test long marshal.
     */
    @Test
    void testLongMarshal() {
        final AxContextSchema avroLongSchema =
            new AxContextSchema(new AxArtifactKey("AvroLong", "0.0.1"), "AVRO", "{\"type\": \"long\"}");

        schemas.getSchemasMap().put(avroLongSchema.getKey(), avroLongSchema);
        final SchemaHelper schemaHelper3 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroLongSchema.getKey());

        assertEquals("0", schemaHelper3.marshal2String(0L));
        assertEquals("1", schemaHelper3.marshal2String(1L));
        assertEquals("-1", schemaHelper3.marshal2String(-1L));
        assertEquals("9223372036854775807", schemaHelper3.marshal2String(9223372036854775807L));
        assertEquals("-9223372036854775808", schemaHelper3.marshal2String(-9223372036854775808L));
        assertThatThrownBy(() -> schemaHelper3.marshal2String("Hello"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"Hello\" Avro marshalling failed.");
        assertThatThrownBy(() -> schemaHelper3.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"java.lang.Long\"");
    }

    /**
     * Test float marshal.
     */
    @Test
    void testFloatMarshal() {
        final AxContextSchema avroFloatSchema =
            new AxContextSchema(new AxArtifactKey("AvroFloat", "0.0.1"), "AVRO", "{\"type\": \"float\"}");

        schemas.getSchemasMap().put(avroFloatSchema.getKey(), avroFloatSchema);
        final SchemaHelper schemaHelper4 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroFloatSchema.getKey());

        assertEquals("0.0", schemaHelper4.marshal2String(0F));
        assertEquals("1.0", schemaHelper4.marshal2String(1F));
        assertEquals("-1.0", schemaHelper4.marshal2String(-1F));
        assertEquals("1.23", schemaHelper4.marshal2String(1.23F));
        assertEquals("-1.23", schemaHelper4.marshal2String(-1.23F));
        assertEquals("9.223372E18", schemaHelper4.marshal2String(9.223372E18F));
        assertEquals("-9.223372E18", schemaHelper4.marshal2String(-9.223372E18F));
        assertEquals("9.223372E18", schemaHelper4.marshal2String(9.223372E18F));
        assertEquals("-9.223372E18", schemaHelper4.marshal2String(-9.223372E18F));
        assertThatThrownBy(() -> schemaHelper4.marshal2String("Hello"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"Hello\" Avro marshalling failed.");
        assertThatThrownBy(() -> schemaHelper4.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"java.lang.Float\"");
    }

    /**
     * Test double marshal.
     */
    @Test
    void testDoubleMarshal() {
        final AxContextSchema avroDoubleSchema =
            new AxContextSchema(new AxArtifactKey("AvroDouble", "0.0.1"), "AVRO", "{\"type\": \"double\"}");

        schemas.getSchemasMap().put(avroDoubleSchema.getKey(), avroDoubleSchema);
        final SchemaHelper schemaHelper5 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroDoubleSchema.getKey());

        assertEquals("0.0", schemaHelper5.marshal2String(0D));
        assertEquals("1.0", schemaHelper5.marshal2String(1D));
        assertEquals("-1.0", schemaHelper5.marshal2String(-1D));
        assertEquals("1.23", schemaHelper5.marshal2String(1.23));
        assertEquals("-1.23", schemaHelper5.marshal2String(-1.23));
        assertEquals("9.223372036854776E18", schemaHelper5.marshal2String(9.223372036854776E18));
        assertEquals("-9.223372036854776E18", schemaHelper5.marshal2String(-9.223372036854776E18));
        assertEquals("9.223372036854776E18", schemaHelper5.marshal2String(9.223372036854776E18));
        assertEquals("-9.223372036854776E18", schemaHelper5.marshal2String(-9.223372036854776E18));
        assertThatThrownBy(() -> schemaHelper5.marshal2String("Hello"))
            .hasMessageStartingWith("AvroTest:0.0.1: object \"Hello\" Avro marshalling failed.");
        assertThatThrownBy(() -> schemaHelper5.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"java.lang.Double\"");
    }

    /**
     * Test string marshal.
     */
    @Test
    void testStringMarshal() {
        final AxContextSchema avroStringSchema =
            new AxContextSchema(new AxArtifactKey("AvroString", "0.0.1"), "AVRO", "{\"type\": \"string\"}");

        schemas.getSchemasMap().put(avroStringSchema.getKey(), avroStringSchema);
        final SchemaHelper schemaHelper7 =
            new SchemaHelperFactory().createSchemaHelper(testKey, avroStringSchema.getKey());

        assertEquals("\"0\"", schemaHelper7.marshal2String("0"));
        assertEquals("\"1\"", schemaHelper7.marshal2String("1"));
        assertEquals("\"-1\"", schemaHelper7.marshal2String("-1"));
        assertEquals("\"1.23\"", schemaHelper7.marshal2String("1.23"));
        assertEquals("\"-1.23\"", schemaHelper7.marshal2String("-1.23"));
        assertEquals("\"9223372036854775807\"", schemaHelper7.marshal2String("9223372036854775807"));
        assertEquals("\"-9223372036854775808\"", schemaHelper7.marshal2String("-9223372036854775808"));
        assertEquals("\"9223372036854775808\"", schemaHelper7.marshal2String("9223372036854775808"));
        assertEquals("\"-9223372036854775809\"", schemaHelper7.marshal2String("-9223372036854775809"));
        assertEquals("\"Hello\"", schemaHelper7.marshal2String("Hello"));
        assertThatThrownBy(() -> schemaHelper7.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"java.lang.String\"");
    }

    /**
     * Test bytes marshal.
     */
    @Test
    void testBytesMarshal() {
        final AxContextSchema avroSchema =
            new AxContextSchema(new AxArtifactKey("AvroString", "0.0.1"), "AVRO", "{\"type\": \"bytes\"}");

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final byte[] helloBytes = {104, 101, 108, 108, 111};
        final String helloOut = schemaHelper.marshal2String(helloBytes);
        assertEquals("\"hello\"", helloOut);

        assertThatThrownBy(() -> schemaHelper.marshal2String(null))
            .hasMessageStartingWith("AvroTest:0.0.1: cannot encode a null object of class \"[Ljava.lang.Byte;\"");
    }
}
