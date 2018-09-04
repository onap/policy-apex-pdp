/*
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey.Compatibility;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

public class AxKeyTest {

    @Test
    public void testArtifactKey() {
        try {
            new AxArtifactKey("some bad key id");
            fail("This test should throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("parameter \"id\": value \"some bad key id\", "
                            + "does not match regular expression \"[A-Za-z0-9\\-_\\.]+:[0-9].[0-9].[0-9]\"",
                            e.getMessage());
        }

        AxArtifactKey someKey0 = new AxArtifactKey();
        assertEquals(AxArtifactKey.getNullKey(), someKey0);

        AxArtifactKey someKey1 = new AxArtifactKey("name", "0.0.1");
        AxArtifactKey someKey2 = new AxArtifactKey(someKey1);
        AxArtifactKey someKey3 = new AxArtifactKey(someKey1.getId());
        assertEquals(someKey1, someKey2);
        assertEquals(someKey1, someKey3);

        assertEquals(someKey2, someKey1.getKey());
        assertEquals(1, someKey1.getKeys().size());

        someKey0.setName("zero");
        someKey0.setVersion("0.0.2");

        someKey3.setVersion("0.0.2");

        AxArtifactKey someKey4 = new AxArtifactKey(someKey1);
        someKey4.setVersion("0.1.2");

        AxArtifactKey someKey5 = new AxArtifactKey(someKey1);
        someKey5.setVersion("1.2.2");

        AxArtifactKey someKey6 = new AxArtifactKey(someKey1);
        someKey6.setVersion("3");

        assertEquals(Compatibility.DIFFERENT, someKey0.getCompatibility(new AxReferenceKey()));
        assertEquals(Compatibility.DIFFERENT, someKey0.getCompatibility(someKey1));
        assertEquals(Compatibility.IDENTICAL, someKey2.getCompatibility(someKey1));
        assertEquals(Compatibility.PATCH, someKey3.getCompatibility(someKey1));
        assertEquals(Compatibility.MINOR, someKey4.getCompatibility(someKey1));
        assertEquals(Compatibility.MAJOR, someKey5.getCompatibility(someKey1));
        assertEquals(Compatibility.MAJOR, someKey6.getCompatibility(someKey1));

        assertTrue(someKey1.isCompatible(someKey2));
        assertTrue(someKey1.isCompatible(someKey3));
        assertTrue(someKey1.isCompatible(someKey4));
        assertFalse(someKey1.isCompatible(someKey0));
        assertFalse(someKey1.isCompatible(someKey5));
        assertFalse(someKey1.isCompatible(new AxReferenceKey()));

        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey0.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey1.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey2.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey3.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey4.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey5.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID,
                        someKey6.validate(new AxValidationResult()).getValidationResult());

        someKey0.clean();
        assertNotNull(someKey0.toString());

        AxArtifactKey someKey7 = new AxArtifactKey(someKey1);
        assertEquals(150332875, someKey7.hashCode());
        assertEquals(0, someKey7.compareTo(someKey1));
        assertEquals(-12, someKey7.compareTo(someKey0));

        try {
            someKey0.compareTo(null);
        } catch (IllegalArgumentException e) {
            assertEquals("comparison object may not be null", e.getMessage());
        }

        assertEquals(0, someKey0.compareTo(someKey0));
        assertEquals(353602977, someKey0.compareTo(new AxReferenceKey()));

        assertFalse(someKey0.equals(null));
        assertTrue(someKey0.equals(someKey0));
        assertFalse(((AxKey) someKey0).equals(new AxReferenceKey()));
    }

}
