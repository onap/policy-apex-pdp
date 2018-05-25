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

package org.onap.policy.apex.model.contextmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextSchemas {

    @Test
    public void testContextSchemas() {
        assertNotNull(new AxContextSchema());
        assertNotNull(new AxContextSchema(new AxArtifactKey(), "SchemaFlavour", "SchemaDefinition"));

        final AxContextSchema schema =
                new AxContextSchema(new AxArtifactKey("SchemaName", "0.0.1"), "SchemaFlavour", "SchemaDefinition");
        assertNotNull(schema);

        final AxArtifactKey newKey = new AxArtifactKey("NewSchemaName", "0.0.1");
        schema.setKey(newKey);
        assertEquals("NewSchemaName:0.0.1", schema.getKey().getID());
        assertEquals("NewSchemaName:0.0.1", schema.getKeys().get(0).getID());

        try {
            schema.setSchemaFlavour("");
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "parameter \"schemaFlavour\": value \"\", does not match regular expression \"[A-Za-z0-9\\-_]+\"",
                    e.getMessage());
        }

        schema.setSchemaFlavour("NewSchemaFlavour");
        assertEquals("NewSchemaFlavour", schema.getSchemaFlavour());

        schema.setSchema("NewSchemaDefinition");
        assertEquals("NewSchemaDefinition", schema.getSchema());

        AxValidationResult result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schema.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schema.setKey(newKey);
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schema.setSchemaFlavour("UNDEFINED");
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schema.setSchemaFlavour("NewSchemaFlavour");
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schema.setSchema("");
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schema.setSchema("NewSchemaDefinition");
        result = new AxValidationResult();
        result = schema.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schema.clean();

        final AxContextSchema clonedSchema = new AxContextSchema(schema);
        assertEquals(
                "AxContextSchema:(key=AxArtifactKey:(name=NewSchemaName,version=0.0.1),schemaFlavour=NewSchemaFlavour,schemaDefinition=NewSchemaDefinition)",
                clonedSchema.toString());

        assertFalse(schema.hashCode() == 0);

        assertTrue(schema.equals(schema));
        assertTrue(schema.equals(clonedSchema));
        assertFalse(schema.equals(null));
        assertFalse(schema.equals("Hello"));
        assertFalse(schema.equals(new AxContextSchema(new AxArtifactKey(), "Flavour", "Def")));
        assertFalse(schema.equals(new AxContextSchema(newKey, "Flavour", "Def")));
        assertFalse(schema.equals(new AxContextSchema(newKey, "NewSchemaFlavour", "Def")));
        assertTrue(schema.equals(new AxContextSchema(newKey, "NewSchemaFlavour", "NewSchemaDefinition")));

        assertEquals(0, schema.compareTo(schema));
        assertEquals(0, schema.compareTo(clonedSchema));
        assertNotEquals(0, schema.compareTo(null));
        assertNotEquals(0, schema.compareTo(new AxArtifactKey()));
        assertNotEquals(0, schema.compareTo(new AxContextSchema(new AxArtifactKey(), "Flavour", "Def")));
        assertNotEquals(0, schema.compareTo(new AxContextSchema(newKey, "Flavour", "Def")));
        assertNotEquals(0, schema.compareTo(new AxContextSchema(newKey, "NewSchemaFlavour", "Def")));
        assertEquals(0, schema.compareTo(new AxContextSchema(newKey, "NewSchemaFlavour", "NewSchemaDefinition")));

        final AxContextSchemas schemas = new AxContextSchemas();
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Still invalid, no schemas in schema map
        schemas.setKey(new AxArtifactKey("SchemasKey", "0.0.1"));
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schemas.getSchemasMap().put(newKey, schema);
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schemas.getSchemasMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schemas.getSchemasMap().remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schemas.getSchemasMap().put(new AxArtifactKey("NullValueKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        schemas.getSchemasMap().remove(new AxArtifactKey("NullValueKey", "0.0.1"));
        result = new AxValidationResult();
        result = schemas.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        schemas.clean();

        final AxContextSchemas clonedSchemas = new AxContextSchemas(schemas);
        assertTrue(clonedSchemas.toString()
                .startsWith("AxContextSchemas:(key=AxArtifactKey:(name=SchemasKey,version=0.0.1),"));

        assertFalse(schemas.hashCode() == 0);

        assertTrue(schemas.equals(schemas));
        assertTrue(schemas.equals(clonedSchemas));
        assertFalse(schemas.equals(null));
        assertFalse(schemas.equals("Hello"));
        assertFalse(schemas.equals(new AxContextSchemas(new AxArtifactKey())));

        assertEquals(0, schemas.compareTo(schemas));
        assertEquals(0, schemas.compareTo(clonedSchemas));
        assertNotEquals(0, schemas.compareTo(null));
        assertNotEquals(0, schemas.compareTo(new AxArtifactKey()));
        assertNotEquals(0, schemas.compareTo(new AxContextSchemas(new AxArtifactKey())));

        clonedSchemas.get(newKey).setSchemaFlavour("YetAnotherFlavour");
        assertNotEquals(0, schemas.compareTo(clonedSchemas));

        assertEquals("NewSchemaName", schemas.get("NewSchemaName").getKey().getName());
        assertEquals("NewSchemaName", schemas.get("NewSchemaName", "0.0.1").getKey().getName());
        assertEquals(1, schemas.getAll("NewSchemaName", "0.0.1").size());
        assertEquals(0, schemas.getAll("NonExistantSchemaName").size());
    }
}
