/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.context.test.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test the AxArtifactKey test entity.
 *
 */
public class ArtifactKeyTestEntityTest {

    @Test
    public void testTestEntity() {
        ArtifactKeyTestEntity testEntity = new ArtifactKeyTestEntity();

        ArtifactKeyTestEntity testEntityCopy = new ArtifactKeyTestEntity();
        assertEquals(120390253, testEntityCopy.hashCode());

        testEntity.setKey(null);
        testEntity.copyTo(testEntityCopy);
        assertTrue(testEntity.equals((testEntityCopy)));
        assertFalse(testEntity.checkSetKey());
        AxArtifactKey key = new AxArtifactKey("TestKey", "0.0.1");

        testEntity.setKey(key);
        testEntity.clean();
        AxValidationResult result = testEntity.validate(new AxValidationResult());
        assertEquals(ValidationResult.VALID, result.getValidationResult());
        assertEquals(key, testEntity.getKey());
        assertEquals(key, testEntity.getKeys().get(0));
        assertEquals(key.getId(), testEntity.getId());
        assertEquals((Double) 0.0, (Double) testEntity.getDoubleValue());
        assertTrue(testEntity.checkSetKey());
        assertEquals((Double) 0.0, (Double) testEntity.getDoubleValue());
        testEntity.setDoubleValue(3.14);
        assertEquals((Double) 3.14, (Double) testEntity.getDoubleValue());
        assertTrue(testEntity.checkSetKey());
        assertEquals("ArtifactKeyTestEntity [key=AxArtifactKey:(name=TestKey,version=0.0.1), doubleValue=3.14]",
                        testEntity.toString());
        ArtifactKeyTestEntity testEntityClone = new ArtifactKeyTestEntity();
        testEntity.copyTo(testEntityClone);
        assertTrue(testEntity.equals(testEntity));
        assertTrue(testEntity.equals(testEntityClone));
        ArtifactKeyTestEntity testEntityNew = null;
        testEntityNew = (ArtifactKeyTestEntity) testEntity.copyTo(testEntityNew);
        assertTrue(testEntityNew.equals(testEntityNew));
        assertTrue(testEntity.equals(testEntityNew));
        ReferenceKeyTestEntity testEntityBad = new ReferenceKeyTestEntity();
        testEntityBad = (ReferenceKeyTestEntity) testEntity.copyTo(testEntityBad);
        assertNull(testEntityBad);
        
        testEntityBad = new ReferenceKeyTestEntity();
        assertEquals(-1036664728, testEntity.hashCode());
        assertFalse(testEntity.equals(null));
        assertEquals(-1, testEntity.compareTo(null));
        assertTrue(testEntity.equals(testEntity));
        assertEquals(0, testEntity.compareTo(testEntity));
        assertFalse(testEntity.equals(testEntityBad));
        assertEquals(-1, testEntity.compareTo(testEntityBad));
        assertFalse(testEntityCopy.equals(testEntity));
        assertEquals(1, testEntityCopy.compareTo(testEntity));
        testEntityClone.setKey(key);
        testEntityNew.setKey(AxArtifactKey.getNullKey());
        assertFalse(testEntityNew.equals(testEntityClone));
        assertEquals(-6, testEntityNew.compareTo(testEntityClone));
        testEntityClone.setKey(null);
        testEntityNew.setKey(null);
        assertTrue(testEntityNew.equals(testEntityClone));
        assertEquals(0, testEntityNew.compareTo(testEntityClone));
        testEntityCopy.setKey(AxArtifactKey.getNullKey());
        assertFalse(testEntityCopy.equals(testEntity));
        assertEquals(-6, testEntityCopy.compareTo(testEntity));
        testEntityClone.setKey(key);
        testEntityClone.setDoubleValue(1.23);
        assertFalse(testEntity.equals(testEntityClone));
        assertEquals(1, testEntity.compareTo(testEntityClone));
        
        ArtifactKeyTestEntity entity2 = new ArtifactKeyTestEntity(3.14);
        assertEquals((Double)3.14, (Double)entity2.getDoubleValue());
        ArtifactKeyTestEntity entity3 = new ArtifactKeyTestEntity(key, 3.14);
        assertEquals(key, entity3.getKey());
        
        entity3.setKey(null);
        assertEquals(31, entity3.hashCode());
    }
}
