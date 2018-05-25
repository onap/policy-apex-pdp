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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey.Compatibility;

public class testKeyUse {

    @Test
    public void test() {
        assertNotNull(new AxKeyUse());
        assertNotNull(new AxKeyUse(new AxArtifactKey()));
        assertNotNull(new AxKeyUse(new AxReferenceKey()));
        
        AxArtifactKey key = new AxArtifactKey("Key", "0.0.1");
        AxKeyUse keyUse = new AxKeyUse();
        keyUse.setKey(key);
        assertEquals(key, keyUse.getKey());
        assertEquals("Key:0.0.1", keyUse.getID());
        assertEquals(key, keyUse.getKeys().get(0));
        
        assertEquals(Compatibility.IDENTICAL, keyUse.getCompatibility(key));
        assertTrue(keyUse.isCompatible(key));
        
        keyUse.clean();
        assertNotNull(keyUse);
        
        AxValidationResult result = new AxValidationResult();
        result = keyUse.validate(result);
        assertNotNull(result);
        
        assertNotEquals(0, keyUse.hashCode());
        
        AxKeyUse clonedKeyUse = new AxKeyUse(keyUse);
        assertEquals("AxKeyUse:(usedKey=AxArtifactKey:(name=Key,version=0.0.1))", clonedKeyUse.toString());
        
        assertFalse(keyUse.hashCode() == 0);
        
        assertTrue(keyUse.equals(keyUse));
        assertTrue(keyUse.equals(clonedKeyUse));
        assertFalse(keyUse.equals("Hello"));
        assertTrue(keyUse.equals(new AxKeyUse(key)));
        
        assertEquals(0, keyUse.compareTo(keyUse));
        assertEquals(0, keyUse.compareTo(clonedKeyUse));
        assertNotEquals(0, keyUse.compareTo(new AxArtifactKey()));
        assertEquals(0, keyUse.compareTo(new AxKeyUse(key)));
        
        AxKeyUse keyUseNull = new AxKeyUse(AxArtifactKey.getNullKey());
        AxValidationResult resultNull = new AxValidationResult();
        assertEquals(false, keyUseNull.validate(resultNull).isValid());
    }
}
