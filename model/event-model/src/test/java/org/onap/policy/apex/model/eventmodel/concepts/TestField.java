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

package org.onap.policy.apex.model.eventmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;

/**
 * Test fields.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestField {

    @Test
    public void testField() {
        assertNotNull(new AxField());
        assertNotNull(new AxField(new AxReferenceKey()));
        assertNotNull(new AxField(new AxReferenceKey(), new AxArtifactKey()));
        assertNotNull(new AxField(new AxReferenceKey(), new AxArtifactKey(), false));
        assertNotNull(new AxField("LocalName", new AxArtifactKey(), false));
        assertNotNull(new AxField("LocalName", new AxArtifactKey()));
        assertNotNull(new AxField("LocalName", new AxArtifactKey(), false));

        assertNotNull(new AxInputField());
        assertNotNull(new AxInputField(new AxReferenceKey()));
        assertNotNull(new AxInputField(new AxReferenceKey(), new AxArtifactKey()));
        assertNotNull(new AxInputField(new AxReferenceKey(), new AxArtifactKey(), true));
        assertNotNull(new AxInputField("LocalName", new AxArtifactKey()));
        assertNotNull(new AxInputField(new AxInputField()));

        assertNotNull(new AxOutputField());
        assertNotNull(new AxOutputField(new AxReferenceKey()));
        assertNotNull(new AxOutputField(new AxReferenceKey(), new AxArtifactKey()));
        assertNotNull(new AxOutputField(new AxReferenceKey(), new AxArtifactKey(), false));
        assertNotNull(new AxOutputField("LocalName", new AxArtifactKey()));
        assertNotNull(new AxOutputField(new AxOutputField()));

        final AxField field = new AxField();

        final AxReferenceKey fieldKey = new AxReferenceKey("FieldName", "0.0.1", "PLN", "LN");
        field.setKey(fieldKey);
        assertEquals("FieldName:0.0.1:PLN:LN", field.getKey().getId());
        assertEquals("FieldName:0.0.1:PLN:LN", field.getKeys().get(0).getId());

        final AxArtifactKey schemaKey = new AxArtifactKey("SchemaName", "0.0.1");
        field.setSchema(schemaKey);
        assertEquals("SchemaName:0.0.1", field.getSchema().getId());

        assertEquals(false, field.getOptional());
        field.setOptional(true);
        assertEquals(true, field.getOptional());

        AxValidationResult result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        field.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        field.setKey(fieldKey);
        result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        field.setSchema(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        field.setSchema(schemaKey);
        result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        field.clean();

        final AxField clonedField = new AxField(field);
        assertEquals("AxField:(key=AxReferenceKey:(parentKeyName=FieldName,parentKeyVersion=0.0.1,"
                        + "parentLocalName=PLN,localName=LN),fieldSchemaKey="
                        + "AxArtifactKey:(name=SchemaName,version=0.0.1),optional=true)", clonedField.toString());

        assertFalse(field.hashCode() == 0);

        assertTrue(field.equals(field));
        assertTrue(field.equals(clonedField));
        assertFalse(field.equals(null));
        assertFalse(field.equals("Hello"));
        assertFalse(field.equals(new AxField(AxReferenceKey.getNullKey(), AxArtifactKey.getNullKey(), false)));
        assertFalse(field.equals(new AxField(fieldKey, AxArtifactKey.getNullKey(), false)));
        assertFalse(field.equals(new AxField(fieldKey, schemaKey, false)));
        assertTrue(field.equals(new AxField(fieldKey, schemaKey, true)));

        assertEquals(0, field.compareTo(field));
        assertEquals(0, field.compareTo(clonedField));
        assertNotEquals(0, field.compareTo(new AxArtifactKey()));
        assertNotEquals(0, field.compareTo(null));
        assertNotEquals(0,
                        field.compareTo(new AxField(AxReferenceKey.getNullKey(), AxArtifactKey.getNullKey(), false)));
        assertNotEquals(0, field.compareTo(new AxField(fieldKey, AxArtifactKey.getNullKey(), false)));
        assertNotEquals(0, field.compareTo(new AxField(fieldKey, schemaKey, false)));
        assertEquals(0, field.compareTo(new AxField(fieldKey, schemaKey, true)));

        assertNotNull(field.getKeys());
    }
}
