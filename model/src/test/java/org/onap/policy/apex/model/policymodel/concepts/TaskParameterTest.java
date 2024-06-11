/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

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

        assertThatThrownBy(() -> par.setDefaultValue(null))
            .hasMessage("defaultValue may not be null");
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

        assertNotEquals(0, par.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(par, par); // NOSONAR
        assertEquals(par, clonedPar);
        assertNotNull(par);
        assertNotEquals(par, (Object) "Hello");
        assertNotEquals(par, new AxTaskParameter(AxReferenceKey.getNullKey(), "DefaultValue"));
        assertNotEquals(par, new AxTaskParameter(parKey, "OtherDefaultValue"));
        assertEquals(par, new AxTaskParameter(parKey, "DefaultValue"));

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
