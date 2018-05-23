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

package org.onap.apex.model.basicmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxKey;
import org.onap.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.apex.model.basicmodel.concepts.AxKey.Compatibility;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AxKeyTest {

    @Test
    public void testArtifactKey() {
        AxArtifactKey aKey0 = new AxArtifactKey();
        AxArtifactKey aKey1 = new AxArtifactKey("name", "0.0.1");
        AxArtifactKey aKey2 = new AxArtifactKey(aKey1);
        AxArtifactKey aKey3 = new AxArtifactKey(aKey1.getID());
        AxArtifactKey aKey4 = new AxArtifactKey(aKey1);
        AxArtifactKey aKey5 = new AxArtifactKey(aKey1);
        AxArtifactKey aKey6 = new AxArtifactKey(aKey1);

        try {
            new AxArtifactKey("some bad key id");
            fail("This test should throw an exception");
        }
        catch (IllegalArgumentException e) {
            assertEquals("parameter \"id\": value \"some bad key id\", does not match regular expression \"[A-Za-z0-9\\-_\\.]+:[0-9].[0-9].[0-9]\"", e.getMessage());
        }
        
        assertEquals(AxArtifactKey.getNullKey(), aKey0);
        assertEquals(aKey1, aKey2);
        assertEquals(aKey1, aKey3);
        
        assertEquals(aKey2, aKey1.getKey());
        assertEquals(1, aKey1.getKeys().size());
        
        aKey0.setName("zero");
        aKey0.setVersion("0.0.2");
        aKey3.setVersion("0.0.2");
        aKey4.setVersion("0.1.2");
        aKey5.setVersion("1.2.2");
        aKey6.setVersion("3");
        
        assertEquals(Compatibility.DIFFERENT, aKey0.getCompatibility(new AxReferenceKey()));
        assertEquals(Compatibility.DIFFERENT, aKey0.getCompatibility(aKey1));
        assertEquals(Compatibility.IDENTICAL, aKey2.getCompatibility(aKey1));
        assertEquals(Compatibility.PATCH,     aKey3.getCompatibility(aKey1));
        assertEquals(Compatibility.MINOR,     aKey4.getCompatibility(aKey1));
        assertEquals(Compatibility.MAJOR,     aKey5.getCompatibility(aKey1));
        assertEquals(Compatibility.MAJOR,     aKey6.getCompatibility(aKey1));
        
        assertTrue(aKey1.isCompatible(aKey2));
        assertTrue(aKey1.isCompatible(aKey3));
        assertTrue(aKey1.isCompatible(aKey4));
        assertFalse(aKey1.isCompatible(aKey0));
        assertFalse(aKey1.isCompatible(aKey5));
        assertFalse(aKey1.isCompatible(new AxReferenceKey()));
        
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey0.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey1.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey2.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey3.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey4.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey5.validate(new AxValidationResult()).getValidationResult());
        assertEquals(AxValidationResult.ValidationResult.VALID, aKey6.validate(new AxValidationResult()).getValidationResult());
        
        aKey0.clean();
        assertNotNull(aKey0.toString());
        
        AxArtifactKey aKey7 = new AxArtifactKey(aKey1);
        assertEquals(150332875, aKey7.hashCode());
        assertEquals(0, aKey7.compareTo(aKey1));
        assertEquals(-12, aKey7.compareTo(aKey0));
        
        try {
            aKey0.compareTo(null);
        }
        catch (IllegalArgumentException e) {
            assertEquals("comparison object may not be null", e.getMessage());
        }
        
        assertEquals(0, aKey0.compareTo(aKey0));
        assertEquals(353602977, aKey0.compareTo(new AxReferenceKey()));
        
        assertFalse(aKey0.equals(null));
        assertTrue(aKey0.equals(aKey0));
        assertFalse(((AxKey)aKey0).equals(new AxReferenceKey()));
    }

}
