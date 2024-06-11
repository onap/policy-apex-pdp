/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test fields.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class FieldTest {

    @Test
    void testField() {
        final AxField field = new AxField();

        final AxReferenceKey fieldKey = new AxReferenceKey("FieldName", "0.0.1", "PLN", "LN");
        field.setKey(fieldKey);
        assertEquals("FieldName:0.0.1:PLN:LN", field.getKey().getId());
        assertEquals("FieldName:0.0.1:PLN:LN", field.getKeys().get(0).getId());

        final AxArtifactKey schemaKey = new AxArtifactKey("SchemaName", "0.0.1");
        field.setSchema(schemaKey);
        assertEquals("SchemaName:0.0.1", field.getSchema().getId());

        assertFalse(field.getOptional());
        field.setOptional(true);
        assertTrue(field.getOptional());

        assertValidationResult(field, fieldKey, schemaKey);

        field.clean();

        assertCompareTo(field, fieldKey, schemaKey);
    }

    private static void assertValidationResult(AxField field, AxReferenceKey fieldKey, AxArtifactKey schemaKey) {
        AxValidationResult result = new AxValidationResult();
        result = field.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

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
    }

    private static void assertCompareTo(AxField field, AxReferenceKey fieldKey, AxArtifactKey schemaKey) {
        final AxField clonedField = new AxField(field);
        assertEquals("AxField:(key=AxReferenceKey:(parentKeyName=FieldName,parentKeyVersion=0.0.1,"
            + "parentLocalName=PLN,localName=LN),fieldSchemaKey="
            + "AxArtifactKey:(name=SchemaName,version=0.0.1),optional=true)", clonedField.toString());

        assertNotEquals(0, field.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(field, field); // NOSONAR
        assertEquals(field, clonedField);
        assertNotNull(field);
        assertNotEquals(field, (Object) "Hello");
        assertNotEquals(field, new AxField(AxReferenceKey.getNullKey(), AxArtifactKey.getNullKey(), false));
        assertNotEquals(field, new AxField(fieldKey, AxArtifactKey.getNullKey(), false));
        assertNotEquals(field, new AxField(fieldKey, schemaKey, false));
        assertEquals(field, new AxField(fieldKey, schemaKey, true));

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
