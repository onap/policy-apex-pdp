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

package org.onap.policy.apex.model.policymodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;

/**
 * Test task parameters.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TaskParameterTest {

    @Test
    public void testTaskParameter() {
        assertNotNull(new AxTaskParameter());
        assertNotNull(new AxTaskParameter(new AxReferenceKey()));
        assertNotNull(new AxTaskParameter(new AxReferenceKey(), "DefaultValue"));

        final AxTaskParameter par = new AxTaskParameter();

        final AxReferenceKey parKey = new AxReferenceKey("ParParentName", "0.0.1", "PLN", "LN");
        par.setKey(parKey);
        assertEquals("ParParentName:0.0.1:PLN:LN", par.getKey().getId());
        assertEquals("ParParentName:0.0.1:PLN:LN", par.getKeys().get(0).getId());

        par.setDefaultValue("DefaultValue");
        assertEquals("DefaultValue", par.getTaskParameterValue());

        AxValidationResult result = new AxValidationResult();
        result = par.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        par.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = par.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        par.setKey(parKey);
        result = new AxValidationResult();
        result = par.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        try {
            par.setDefaultValue(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("defaultValue may not be null", e.getMessage());
        }

        par.setDefaultValue("");
        result = new AxValidationResult();
        result = par.validate(result);
        assertEquals(ValidationResult.WARNING, result.getValidationResult());

        par.setDefaultValue("DefaultValue");
        result = new AxValidationResult();
        result = par.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        par.clean();

        final AxTaskParameter clonedPar = new AxTaskParameter(par);
        assertEquals("AxTaskParameter:(key=AxReferenceKey:(parentKeyName=ParParentName,parentKeyVersion=0.0.1,"
                        + "parentLocalName=PLN,localName=LN),defaultValue=DefaultValue)", clonedPar.toString());

        assertFalse(par.hashCode() == 0);

        assertTrue(par.equals(par));
        assertTrue(par.equals(clonedPar));
        assertFalse(par.equals(null));
        assertFalse(par.equals("Hello"));
        assertFalse(par.equals(new AxTaskParameter(AxReferenceKey.getNullKey(), "DefaultValue")));
        assertFalse(par.equals(new AxTaskParameter(parKey, "OtherDefaultValue")));
        assertTrue(par.equals(new AxTaskParameter(parKey, "DefaultValue")));

        assertEquals(0, par.compareTo(par));
        assertEquals(0, par.compareTo(clonedPar));
        assertNotEquals(0, par.compareTo(new AxArtifactKey()));
        assertNotEquals(0, par.compareTo(null));
        assertNotEquals(0, par.compareTo(new AxTaskParameter(AxReferenceKey.getNullKey(), "DefaultValue")));
        assertNotEquals(0, par.compareTo(new AxTaskParameter(parKey, "OtherDefaultValue")));
        assertEquals(0, par.compareTo(new AxTaskParameter(parKey, "DefaultValue")));

        assertNotNull(par.getKeys());
    }
}
