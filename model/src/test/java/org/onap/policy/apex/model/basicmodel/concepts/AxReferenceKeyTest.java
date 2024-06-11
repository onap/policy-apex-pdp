/*
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;


class AxReferenceKeyTest {

    @Test
    void testAxReferenceKey() {
        assertConstructor();
        assertEquals(AxReferenceKey.getNullKey().getKey(), AxReferenceKey.getNullKey());
        assertEquals("NULL:0.0.0:NULL:NULL", AxReferenceKey.getNullKey().getId());

        AxReferenceKey testReferenceKey = new AxReferenceKey();
        assertSetValues(testReferenceKey);

        assertCompatibility(testReferenceKey);

        AxValidationResult result = new AxValidationResult();
        result = testReferenceKey.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        testReferenceKey.clean();

        AxReferenceKey clonedReferenceKey = new AxReferenceKey(testReferenceKey);
        assertEquals("AxReferenceKey:(parentKeyName=NPKN,parentKeyVersion=0.0.1,parentLocalName=NPKLN,localName=NLN)",
            clonedReferenceKey.toString());

        assertNotEquals(0, testReferenceKey.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(testReferenceKey, testReferenceKey); // NOSONAR
        assertEquals(testReferenceKey, clonedReferenceKey);
        assertNotEquals(testReferenceKey, (Object) "Hello");
        assertNotEquals(testReferenceKey, new AxReferenceKey("PKN", "0.0.2", "PLN", "LN"));
        assertNotEquals(testReferenceKey, new AxReferenceKey("NPKN", "0.0.2", "PLN", "LN"));
        assertNotEquals(testReferenceKey, new AxReferenceKey("NPKN", "0.0.1", "PLN", "LN"));
        assertNotEquals(testReferenceKey, new AxReferenceKey("NPKN", "0.0.1", "NPLN", "LN"));
        assertEquals(testReferenceKey, new AxReferenceKey("NPKN", "0.0.1", "NPKLN", "NLN"));

        assertCompareTo(testReferenceKey, clonedReferenceKey);

        assertNotNull(testReferenceKey.getKeys());

        assertExceptions(testReferenceKey);
        AxReferenceKey targetRefKey = new AxReferenceKey();
        assertEquals(testReferenceKey, testReferenceKey.copyTo(targetRefKey));
    }

    private static void assertCompareTo(AxReferenceKey testReferenceKey, AxReferenceKey clonedReferenceKey) {
        assertEquals(0, testReferenceKey.compareTo(testReferenceKey));
        assertEquals(0, testReferenceKey.compareTo(clonedReferenceKey));
        assertNotEquals(0, testReferenceKey.compareTo(new AxArtifactKey()));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("PKN", "0.0.2", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.2", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "NPLN", "LN")));
        assertEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "NPKLN", "NLN")));
    }

    private static void assertExceptions(AxReferenceKey testReferenceKey) {
        assertThatThrownBy(() -> testReferenceKey.equals(null))
            .hasMessage("comparison object may not be null");
        assertThatThrownBy(() -> testReferenceKey.copyTo(null))
            .hasMessage("target may not be null");
        assertThatThrownBy(() -> testReferenceKey.copyTo(new AxArtifactKey("Key", "0.0.1")))
            .hasMessage("org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey is not an instance of "
                + "org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey");
    }

    private static void assertCompatibility(AxReferenceKey testReferenceKey) {
        assertFalse(testReferenceKey.isCompatible(AxArtifactKey.getNullKey()));
        assertFalse(testReferenceKey.isCompatible(AxReferenceKey.getNullKey()));
        assertTrue(testReferenceKey.isCompatible(testReferenceKey));

        assertEquals(AxKey.Compatibility.DIFFERENT, testReferenceKey.getCompatibility(AxArtifactKey.getNullKey()));
        assertEquals(AxKey.Compatibility.DIFFERENT, testReferenceKey.getCompatibility(AxReferenceKey.getNullKey()));
        assertEquals(AxKey.Compatibility.IDENTICAL, testReferenceKey.getCompatibility(testReferenceKey));
    }

    private static void assertSetValues(AxReferenceKey testReferenceKey) {
        testReferenceKey.setParentArtifactKey(new AxArtifactKey("PN", "0.0.1"));
        assertEquals("PN:0.0.1", testReferenceKey.getParentArtifactKey().getId());

        testReferenceKey.setParentReferenceKey(new AxReferenceKey("PN", "0.0.1", "LN"));
        assertEquals("PN:0.0.1:NULL:LN", testReferenceKey.getParentReferenceKey().getId());

        testReferenceKey.setParentKeyName("NPKN");
        assertEquals("NPKN", testReferenceKey.getParentKeyName());

        testReferenceKey.setParentKeyVersion("0.0.1");
        assertEquals("0.0.1", testReferenceKey.getParentKeyVersion());

        testReferenceKey.setParentLocalName("NPKLN");
        assertEquals("NPKLN", testReferenceKey.getParentLocalName());

        testReferenceKey.setLocalName("NLN");
        assertEquals("NLN", testReferenceKey.getLocalName());
    }

    private static void assertConstructor() {
        assertNotNull(new AxReferenceKey());
        assertNotNull(new AxReferenceKey(new AxArtifactKey()));
        assertNotNull(new AxReferenceKey(new AxArtifactKey(), "LocalName"));
        assertNotNull(new AxReferenceKey(new AxReferenceKey()));
        assertNotNull(new AxReferenceKey(new AxReferenceKey(), "LocalName"));
        assertNotNull(new AxReferenceKey(new AxArtifactKey(), "ParentLocalName", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName", "0.0.1", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName", "0.0.1", "ParentLocalName", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName:0.0.1:ParentLocalName:LocalName"));
    }

    @Test
    void testValidation() throws IllegalArgumentException, IllegalAccessException,
        NoSuchFieldException, SecurityException {
        AxReferenceKey testReferenceKey = new AxReferenceKey();
        testReferenceKey.setParentArtifactKey(new AxArtifactKey("PN", "0.0.1"));
        assertEquals("PN:0.0.1", testReferenceKey.getParentArtifactKey().getId());

        Field parentNameField = testReferenceKey.getClass().getDeclaredField("parentKeyName");
        parentNameField.setAccessible(true);
        parentNameField.set(testReferenceKey, "Parent Name");
        AxValidationResult validationResult = new AxValidationResult();
        testReferenceKey.validate(validationResult);
        parentNameField.set(testReferenceKey, "ParentName");
        parentNameField.setAccessible(false);
        assertEquals(
            "parentKeyName invalid-parameter parentKeyName with value Parent Name "
                + "does not match regular expression [A-Za-z0-9\\-_\\.]+",
            validationResult.getMessageList().get(0).getMessage());

        Field parentVersionField = testReferenceKey.getClass().getDeclaredField("parentKeyVersion");
        parentVersionField.setAccessible(true);
        parentVersionField.set(testReferenceKey, "Parent Version");
        AxValidationResult validationResultPV = new AxValidationResult();
        testReferenceKey.validate(validationResultPV);
        parentVersionField.set(testReferenceKey, "0.0.1");
        parentVersionField.setAccessible(false);
        assertEquals(
            "parentKeyVersion invalid-parameter parentKeyVersion with value Parent Version "
                + "does not match regular expression [A-Za-z0-9.]+",
            validationResultPV.getMessageList().get(0).getMessage());

        Field parentLocalNameField = testReferenceKey.getClass().getDeclaredField("parentLocalName");
        parentLocalNameField.setAccessible(true);
        parentLocalNameField.set(testReferenceKey, "Parent Local Name");
        AxValidationResult validationResultPL = new AxValidationResult();
        testReferenceKey.validate(validationResultPL);
        parentLocalNameField.set(testReferenceKey, "ParentLocalName");
        parentLocalNameField.setAccessible(false);
        assertEquals(
            "parentLocalName invalid-parameter parentLocalName with value "
                + "Parent Local Name does not match regular expression [A-Za-z0-9\\-_\\.]+|^$",
            validationResultPL.getMessageList().get(0).getMessage());

        Field localNameField = testReferenceKey.getClass().getDeclaredField("localName");
        localNameField.setAccessible(true);
        localNameField.set(testReferenceKey, "Local Name");
        AxValidationResult validationResultLN = new AxValidationResult();
        testReferenceKey.validate(validationResultLN);
        localNameField.set(testReferenceKey, "LocalName");
        localNameField.setAccessible(false);
        assertEquals(
            "localName invalid-parameter localName with value Local Name "
                + "does not match regular expression [A-Za-z0-9\\-_\\.]+|^$",
            validationResultLN.getMessageList().get(0).getMessage());

    }
}
