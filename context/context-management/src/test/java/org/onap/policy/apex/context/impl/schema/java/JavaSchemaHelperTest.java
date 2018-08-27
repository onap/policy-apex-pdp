/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;

import org.junit.Test;

import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;


public class JavaSchemaHelperTest {

    @Test
    public void testJavaSchemaHelperInit() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema badJavaTypeSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Rubbish");

        try {
            new JavaSchemaHelper().init(userKey, badJavaTypeSchema);
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: class/type java.lang.Rubbish for context schema"
                    + " \"SchemaKey:0.0.1\" not found. Check the class path of the JVM", e.getMessage());
        }

        AxContextSchema builtInJavaTypeSchema = new AxContextSchema(schemaKey, "Java", "short");

        try {
            new JavaSchemaHelper().init(userKey, builtInJavaTypeSchema);
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: class/type short for context schema "
                    + "\"SchemaKey:0.0.1\" not found. Primitive types are not supported."
                    + " Use the appropriate Java boxing type instead.", e.getMessage());
        }
    }

    @Test
    public void testJavaSchemaHelperMethods() {
        SchemaHelper intSchemaHelper = new JavaSchemaHelper();

        assertEquals(AxArtifactKey.getNullKey(), intSchemaHelper.getUserKey());
        assertEquals(null, intSchemaHelper.getSchema());
        assertEquals(null, intSchemaHelper.getSchemaClass());
        assertEquals(null, intSchemaHelper.getSchemaObject());

        try {
            intSchemaHelper.createNewInstance();
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("NULL:0.0.0: could not create an instance, schema class for the schema is null",
                    e.getMessage());
        }

        try {
            intSchemaHelper.createNewInstance(Float.parseFloat("1.23"));
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("NULL:0.0.0: could not create an instance, schema class for the schema is null",
                    e.getMessage());
        }

        try {
            intSchemaHelper.createNewInstance("hello");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("NULL:0.0.0: could not create an instance, schema class for the schema is null",
                    e.getMessage());
        }
        
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");
        AxContextSchema intSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Integer");
        
        intSchemaHelper.init(userKey, intSchema);
        assertEquals(userKey, intSchemaHelper.getUserKey());
        assertEquals(intSchema, intSchemaHelper.getSchema());
        assertEquals(Integer.class, intSchemaHelper.getSchemaClass());
        assertEquals(null, intSchemaHelper.getSchemaObject());

        try {
            intSchemaHelper.createNewInstance();
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: could not create an instance of class "
                    + "\"java.lang.Integer\" using the default constructor \"Integer()\"", e.getMessage());
        }

        try {
            intSchemaHelper.createNewInstance(Float.parseFloat("1.23"));
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: the object \"1.23\" of type "
                    + "\"java.lang.Float\" is not an instance of JsonObject and is not "
                    + "assignable to \"java.lang.Integer\"", e.getMessage());
        }

        try {
            intSchemaHelper.createNewInstance("hello");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: could not create an instance of class \"java.lang.Integer\" "
                    + "using the string constructor \"Integer(String)\"", e.getMessage());
        }

        JsonElement jsonIntElement = null;
        assertEquals(null, intSchemaHelper.createNewInstance(jsonIntElement));

        jsonIntElement = new JsonParser().parse("123");

        assertEquals(123, intSchemaHelper.createNewInstance(jsonIntElement));
        assertEquals(123, intSchemaHelper.createNewInstance(Integer.parseInt("123")));

        assertEquals(null, intSchemaHelper.unmarshal(null));
        assertEquals(123, intSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Double.parseDouble("123")));
    }

    @Test
    public void testJavaSchemaHelperUnmarshal() {
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
        
        AxContextSchema myBaseClassSchema =
                new AxContextSchema(schemaKey, "Java", "org.onap.policy.apex.context.impl.schema.java.MyBaseClass");
        SchemaHelper myBaseClassSchemaHelper = new JavaSchemaHelper();
        myBaseClassSchemaHelper.init(userKey, myBaseClassSchema);

        assertEquals(null, byteSchemaHelper.unmarshal(null));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal("123"));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(new Byte("123"), byteSchemaHelper.unmarshal(Double.parseDouble("123")));
        try {
            byteSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Byte\"", e.getMessage());
        }

        assertEquals(null, shortSchemaHelper.unmarshal(null));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal("123"));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(new Short("123"), shortSchemaHelper.unmarshal(Double.parseDouble("123")));
        try {
            shortSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Short\"", e.getMessage());
        }

        assertEquals(null, intSchemaHelper.unmarshal(null));
        assertEquals(123, intSchemaHelper.unmarshal("123"));
        assertEquals(123, intSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123, intSchemaHelper.unmarshal(Double.parseDouble("123")));
        try {
            intSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Integer\"", e.getMessage());
        }

        assertEquals(null, longSchemaHelper.unmarshal(null));
        assertEquals(123L, longSchemaHelper.unmarshal("123"));
        assertEquals(123L, longSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(123L, longSchemaHelper.unmarshal(Double.parseDouble("123")));
        try {
            longSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Long\"", e.getMessage());
        }

        assertEquals(null, floatSchemaHelper.unmarshal(null));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal("123"));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(new Float("123"), floatSchemaHelper.unmarshal(Double.parseDouble("123")));
        try {
            floatSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Float\"", e.getMessage());
        }

        assertEquals(null, doubleSchemaHelper.unmarshal(null));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal("123"));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Integer.parseInt("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Byte.parseByte("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Short.parseShort("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Long.parseLong("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Float.parseFloat("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(Double.parseDouble("123")));
        assertEquals(new Double("123"), doubleSchemaHelper.unmarshal(BigDecimal.valueOf(123)));
        try {
            doubleSchemaHelper.unmarshal("one two three");
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"one two three\" of class \"java.lang.String\" not "
                    + "compatible with class \"java.lang.Double\"", e.getMessage());
        }

        assertEquals("123", stringSchemaHelper.unmarshal(123));

        MySubClass subClassInstance = new MySubClass("123");
        assertEquals(subClassInstance, myBaseClassSchemaHelper.unmarshal(subClassInstance));
    }

    @Test
    public void testJavaSchemaHelperMarshal() {
        AxArtifactKey schemaKey = new AxArtifactKey("SchemaKey", "0.0.1");
        AxArtifactKey userKey = new AxArtifactKey("UserKey", "0.0.1");

        AxContextSchema intSchema = new AxContextSchema(schemaKey, "Java", "java.lang.Integer");
        SchemaHelper intSchemaHelper = new JavaSchemaHelper();
        intSchemaHelper.init(userKey, intSchema);

        assertEquals("null", intSchemaHelper.marshal2String(null));
        assertEquals("123", intSchemaHelper.marshal2String(123));
        try {
            intSchemaHelper.marshal2String(123.45);
            fail("test should throw an exception here");
        } catch (ContextRuntimeException e) {
            assertEquals("UserKey:0.0.1: object \"123.45\" of class \"java.lang.Double\" not "
                    + "compatible with class \"java.lang.Integer\"", e.getMessage());
        }

        JsonPrimitive intJsonPrimitive = (JsonPrimitive) intSchemaHelper.marshal2Object(123);
        assertEquals(123, intJsonPrimitive.getAsInt());
    }
}
