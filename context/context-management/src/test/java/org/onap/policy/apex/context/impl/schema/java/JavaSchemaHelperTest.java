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

package org.onap.policy.apex.context.impl.schema.java;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.common.parameters.ParameterService;

class JavaSchemaHelperTest {
    /**
     * Initialize JSON adapters.
     */
    @BeforeAll
    public static void registerParameters() {
        JavaSchemaHelperParameters javaSchemaHelperPars = new JavaSchemaHelperParameters();

        JavaSchemaHelperJsonAdapterParameters stringAdapterPars = new JavaSchemaHelperJsonAdapterParameters();
        stringAdapterPars.setAdaptedClass("java.lang.String");
        stringAdapterPars.setAdaptorClass("org.onap.policy.apex.context.impl.schema.java.SupportJsonAdapter");

        javaSchemaHelperPars.getJsonAdapters().put("String", stringAdapterPars);

        SchemaParameters schemaPars = new SchemaParameters();
        schemaPars.getSchemaHelperParameterMap().put("Java", javaSchemaHelperPars);

        ParameterService.register(schemaPars);
    }

    @AfterAll
    public static void deregisterParameters() {
        ParameterService.clear();
    }

    @Test
    void testJavaSchemaHelperInit() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema badJavaTypeSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Rubbish");

        assertThatThrownBy(() -> new JavaSchemaHelper().init(userKey, badJavaTypeSchema))
            .hasMessage("UserKey:0.0.1: class/type java.lang.Rubbish for context schema"
                + " \"SchemaKey:0.0.1\" not found. Check the class path of the JVM");
        AxContextSchema builtInJavaTypeSchema = new AxContextSchema(schemaKey, "Java", "short");

        assertThatThrownBy(() -> new JavaSchemaHelper().init(userKey, builtInJavaTypeSchema))
            .hasMessage("UserKey:0.0.1: class/type short for context schema "
                + "\"SchemaKey:0.0.1\" not found. Primitive types are not supported."
                + " Use the appropriate Java boxing type instead.");
    }

    @Test
    void testJavaSchemaHelperMethods() {
        SchemaHelper intSchemaHelper = new JavaSchemaHelper();

        assertEquals(AxArtifactKey.getNullKey(), intSchemaHelper.getUserKey());
        assertNull(intSchemaHelper.getSchema());
        assertNull(intSchemaHelper.getSchemaClass());
        assertNull(intSchemaHelper.getSchemaObject());

        assertThatThrownBy(intSchemaHelper::createNewInstance)
            .hasMessage("NULL:0.0.0: could not create an instance, schema class for the schema is null");
        assertThatThrownBy(() -> intSchemaHelper.createNewInstance(Float.parseFloat("1.23")))
            .hasMessage("NULL:0.0.0: could not create an instance, schema class for the schema is null");
        assertThatThrownBy(() -> intSchemaHelper.createNewInstance("hello"))
            .hasMessage("NULL:0.0.0: could not create an instance, schema class for the schema is null");
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");
        AxContextSchema intSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Integer");

        intSchemaHelper.init(userKey, intSchema);
        assertEquals(userKey, intSchemaHelper.getUserKey());
        assertEquals(intSchema, intSchemaHelper.getSchema());
        assertEquals(Integer.class, intSchemaHelper.getSchemaClass());
        assertNull(intSchemaHelper.getSchemaObject());

        assertThatThrownBy(intSchemaHelper::createNewInstance)
            .hasMessage("UserKey:0.0.1: could not create an instance of class "
                + "\"java.lang.Integer\" using the default constructor \"Integer()\"");
        assertThatThrownBy(() -> intSchemaHelper.createNewInstance(Float.parseFloat("1.23")))
            .hasMessage("UserKey:0.0.1: the object \"1.23\" of type "
                + "\"java.lang.Float\" is not an instance of JsonObject and is not "
                + "assignable to \"java.lang.Integer\"");
        assertThatThrownBy(() -> intSchemaHelper.createNewInstance("hello"))
            .hasMessage("UserKey:0.0.1: could not create an instance of class \"java.lang.Integer\" "
                + "using the string constructor \"Integer(String)\"");
        JsonElement jsonIntElement = null;
        assertNull(intSchemaHelper.createNewInstance(jsonIntElement));

        jsonIntElement = JsonParser.parseString("123");

        assertEquals(123, intSchemaHelper.createNewInstance(jsonIntElement));
        assertEquals(123, intSchemaHelper.createNewInstance(Integer.parseInt("123")));

        assertNull(intSchemaHelper.unmarshal(null));
        assertEquals(123, intSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Double.parseDouble("123")));
    }

    @Test
    void testJavaSchemaHelperUnmarshal() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema byteSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Byte");
        AxContextSchema shortSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Short");
        AxContextSchema intSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Integer");

        SchemaHelper byteSchemaHelper = new JavaSchemaHelper();
        SchemaHelper shortSchemaHelper = new JavaSchemaHelper();
        SchemaHelper intSchemaHelper = new JavaSchemaHelper();

        byteSchemaHelper.init(userKey, byteSchema);
        shortSchemaHelper.init(userKey, shortSchema);
        intSchemaHelper.init(userKey, intSchema);

        SchemaHelper longSchemaHelper = new JavaSchemaHelper();
        SchemaHelper floatSchemaHelper = new JavaSchemaHelper();

        AxContextSchema longSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Long");
        AxContextSchema floatSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Float");
        longSchemaHelper.init(userKey, longSchema);
        floatSchemaHelper.init(userKey, floatSchema);

        SchemaHelper doubleSchemaHelper = new JavaSchemaHelper();
        SchemaHelper stringSchemaHelper = new JavaSchemaHelper();
        AxContextSchema doubleSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Double");
        AxContextSchema stringSchema = new AxContextSchema(schemaKey, "Java", "java.lang.String");
        doubleSchemaHelper.init(userKey, doubleSchema);
        stringSchemaHelper.init(userKey, stringSchema);

        AxContextSchema myBaseClassSchema = new AxContextSchema(schemaKey, "Java",
            "org.onap.policy.apex.context.impl.schema.java.SupportBaseClass");
        SchemaHelper myBaseClassSchemaHelper = new JavaSchemaHelper();
        myBaseClassSchemaHelper.init(userKey, myBaseClassSchema);

        assertByteUnmarshal(byteSchemaHelper);
        assertShortUnmarshal(shortSchemaHelper);
        assertIntUnmarshal(intSchemaHelper);
        assertLongUnmarshal(longSchemaHelper);
        assertFloatUnmarshal(floatSchemaHelper);
        assertDoubleUnmarshal(doubleSchemaHelper);
        assertEquals("123", stringSchemaHelper.unmarshal(123));

        SupportSubClass subClassInstance = new SupportSubClass("123");
        assertEquals(subClassInstance, myBaseClassSchemaHelper.unmarshal(subClassInstance));
    }

    private static void assertDoubleUnmarshal(SchemaHelper doubleSchemaHelper) {
        assertNull(doubleSchemaHelper.unmarshal(null));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal("123"));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertEquals(Double.valueOf("123"), doubleSchemaHelper.unmarshal(BigDecimal.valueOf(123)));
        assertThatThrownBy(() -> doubleSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Double\"");
    }

    private static void assertFloatUnmarshal(SchemaHelper floatSchemaHelper) {
        assertNull(floatSchemaHelper.unmarshal(null));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal("123"));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(Float.valueOf("123"), floatSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertThatThrownBy(() -> floatSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Float\"");
    }

    private static void assertLongUnmarshal(SchemaHelper longSchemaHelper) {
        assertNull(longSchemaHelper.unmarshal(null));
        assertEquals(123L, longSchemaHelper.unmarshal("123"));
        assertEquals(123L, longSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertThatThrownBy(() -> longSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Long\"");
    }

    private static void assertIntUnmarshal(SchemaHelper intSchemaHelper) {
        assertNull(intSchemaHelper.unmarshal(null));
        assertEquals(123, intSchemaHelper.unmarshal("123"));
        assertEquals(123, intSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertThatThrownBy(() -> intSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Integer\"");
    }

    private static void assertShortUnmarshal(SchemaHelper shortSchemaHelper) {
        assertNull(shortSchemaHelper.unmarshal(null));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal("123"));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(Short.valueOf("123"), shortSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertThatThrownBy(() -> shortSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Short\"");
    }

    private static void assertByteUnmarshal(SchemaHelper byteSchemaHelper) {
        assertNull(byteSchemaHelper.unmarshal(null));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal("123"));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(Byte.valueOf("123"), byteSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertThatThrownBy(() -> byteSchemaHelper.unmarshal("one two three"))
            .hasMessage("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Byte\"");
    }

    @Test
    void testJavaSchemaHelperMarshal() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema intSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Integer");
        SchemaHelper intSchemaHelper = new JavaSchemaHelper();
        intSchemaHelper.init(userKey, intSchema);

        assertEquals("null", intSchemaHelper.marshal2String(null));
        assertEquals("123", intSchemaHelper.marshal2String(123));
        assertThatThrownBy(() -> intSchemaHelper.marshal2String(123.45))
            .hasMessage("UserKey:0.0.1: object \"123.45\" of class \"java.lang.Double\" not "
                + "compatible with class \"java.lang.Integer\"");
        JsonPrimitive intJsonPrimitive = (JsonPrimitive) intSchemaHelper.marshal2Object(123);
        assertEquals(123, intJsonPrimitive.getAsInt());
    }

    @Test
    void testJavaSchemaHelperAdapters() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema stringSchema = new AxContextSchema(schemaKey, "Java", "java.lang.String");
        SchemaHelper stringSchemaHelper = new JavaSchemaHelper();
        stringSchemaHelper.init(userKey, stringSchema);

        assertEquals("null", stringSchemaHelper.marshal2String(null));
        assertEquals("\"Hello\"", stringSchemaHelper.marshal2String("Hello"));
        assertThatThrownBy(() -> stringSchemaHelper.marshal2String(Instant.ofEpochMilli(1000)))
            .hasMessage("UserKey:0.0.1: object \"1970-01-01T00:00:01Z\" of class \"java.time.Instant\" "
                + "not compatible with class \"java.lang.String\"");
        JsonPrimitive stringJsonPrimitive = (JsonPrimitive) stringSchemaHelper.marshal2Object("Another String");
        assertEquals("Another String", stringJsonPrimitive.getAsString());
    }

    @Test
    void testJavaSchemaHelperBadAdapter() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        SchemaParameters pars = ParameterService.get(ContextParameterConstants.SCHEMA_GROUP_NAME);

        JavaSchemaHelperParameters javaShPars =
            (JavaSchemaHelperParameters) pars.getSchemaHelperParameterMap().get("Java");
        javaShPars.getJsonAdapters().get("String")
            .setAdaptorClass("org.onap.policy.apex.context.impl.schema.java.SupportBadJsonAdapter");

        AxContextSchema stringSchema = new AxContextSchema(schemaKey, "Java", "java.lang.String");
        SchemaHelper stringSchemaHelper = new JavaSchemaHelper();
        stringSchemaHelper.init(userKey, stringSchema);

        assertThatThrownBy(() -> stringSchemaHelper.marshal2String("Hello"))
            .hasMessage("UserKey:0.0.1: instantiation of adapter class "
                + "\"org.onap.policy.apex.context.impl.schema.java.SupportBadJsonAdapter\"  to decode and encode "
                + "class \"java.lang.String\" failed: null");
    }

    @Test
    void testJavaSchemaHelperDefaultAdapter() {
        SchemaParameters pars = ParameterService.get(ContextParameterConstants.SCHEMA_GROUP_NAME);

        JavaSchemaHelperParameters javaShPars =
            (JavaSchemaHelperParameters) pars.getSchemaHelperParameterMap().get("Java");

        pars.getSchemaHelperParameterMap().clear();

        testJavaSchemaHelperAdapters();

        pars.getSchemaHelperParameterMap().put("Java", javaShPars);
    }
}
