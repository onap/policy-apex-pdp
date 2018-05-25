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

package org.onap.policy.apex.model.basicmodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;

public class TestApexBasicModelConcepts {
    TestApexModel<AxModel> testApexModel;

    @Before
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexBasicModelCreator());
    }

    @Test
    public void testModelConcepts() {
        final AxModel model = testApexModel.getModel();
        assertNotNull(model);
        model.clean();
        assertNotNull(model);

        AxValidationResult result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.WARNING, result.getValidationResult());

        model.register();
        assertEquals(model.getKeyInformation(), ModelService.getModel(AxKeyInformation.class));

        final AxModel clonedModel = new AxModel(model);
        assertTrue(clonedModel.toString().startsWith("AxModel:(key=AxArtifactKey:(name=BasicModel"));

        assertFalse(model.hashCode() == 0);

        assertTrue(model.equals(model));
        assertTrue(model.equals(clonedModel));
        assertFalse(model.equals(null));
        assertFalse(model.equals("Hello"));
        clonedModel.getKey().setVersion("0.0.2");
        assertFalse(model.equals(clonedModel));
        clonedModel.getKey().setVersion("0.0.1");

        assertEquals(0, model.compareTo(model));
        assertNotEquals(0, model.compareTo(null));
        assertNotEquals(0, model.compareTo(new AxReferenceKey()));
        assertEquals(0, model.compareTo(clonedModel));
        clonedModel.getKey().setVersion("0.0.2");
        assertNotEquals(0, model.compareTo(clonedModel));
        clonedModel.getKey().setVersion("0.0.1");

        assertNotNull(model.getKeys());

        model.getKeyInformation().generateKeyInfo(model);
        assertNotNull(model.getKeyInformation());

        final AxKeyInformation keyI = model.getKeyInformation();
        final AxKeyInformation clonedKeyI = new AxKeyInformation(keyI);

        assertFalse(keyI.equals(null));
        assertFalse(keyI.equals(new AxArtifactKey()));
        assertTrue(keyI.equals(clonedKeyI));

        clonedKeyI.setKey(new AxArtifactKey());
        assertFalse(keyI.equals(clonedKeyI));
        clonedKeyI.setKey(keyI.getKey());

        assertEquals(0, keyI.compareTo(keyI));
        assertEquals(0, keyI.compareTo(clonedKeyI));
        assertNotEquals(0, keyI.compareTo(null));
        assertNotEquals(0, keyI.compareTo(new AxArtifactKey()));

        clonedKeyI.setKey(new AxArtifactKey());
        assertNotEquals(0, keyI.compareTo(clonedKeyI));
        clonedKeyI.setKey(keyI.getKey());
        assertEquals(0, keyI.compareTo(clonedKeyI));

        clonedKeyI.getKeyInfoMap().clear();
        assertNotEquals(0, keyI.compareTo(clonedKeyI));

        AxKeyInfo keyInfo = keyI.get("BasicModel");
        assertNotNull(keyInfo);

        keyInfo = keyI.get(new AxArtifactKey("BasicModel", "0.0.1"));
        assertNotNull(keyInfo);

        Set<AxKeyInfo> keyInfoSet = keyI.getAll("BasicModel");
        assertNotNull(keyInfoSet);

        keyInfoSet = keyI.getAll("BasicModel", "0..0.1");
        assertNotNull(keyInfoSet);

        List<AxKey> keys = model.getKeys();
        assertNotEquals(0, keys.size());

        keys = keyI.getKeys();
        assertNotEquals(0, keys.size());

        model.getKeyInformation().generateKeyInfo(model);
        assertNotNull(model.getKeyInformation());
        model.getKeyInformation().getKeyInfoMap().clear();
        model.getKeyInformation().generateKeyInfo(model);
        assertNotNull(model.getKeyInformation());

        clonedKeyI.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        clonedKeyI.setKey(keyI.getKey());

        clonedKeyI.getKeyInfoMap().clear();
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        clonedKeyI.generateKeyInfo(model);

        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        clonedKeyI.getKeyInfoMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        clonedKeyI.getKeyInfoMap().clear();
        clonedKeyI.generateKeyInfo(model);

        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        clonedKeyI.getKeyInfoMap().put(new AxArtifactKey("SomeKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        clonedKeyI.getKeyInfoMap().clear();
        clonedKeyI.generateKeyInfo(model);

        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxKeyInfo mk = clonedKeyI.get(new AxArtifactKey("BasicModel", "0.0.1"));
        assertNotNull(mk);
        mk.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        clonedKeyI.getKeyInfoMap().clear();
        clonedKeyI.generateKeyInfo(model);

        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        clonedModel.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = clonedModel.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        clonedModel.setKey(model.getKey());
        result = new AxValidationResult();
        result = clonedKeyI.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }

    @Test
    public void testModelConceptsWithReferences() {
        final AxModelWithReferences mwr = new TestApexBasicModelCreator().getModelWithReferences();
        assertNotNull(mwr);
        mwr.getKeyInformation().getKeyInfoMap().clear();
        mwr.getKeyInformation().generateKeyInfo(mwr);

        AxValidationResult result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Duplicate key error
        mwr.addKey(mwr.getKey());
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        mwr.removeKey(mwr.getKey());

        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Null Reference Key
        mwr.addKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        mwr.removeKey(AxReferenceKey.getNullKey());

        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Duplicate Reference Key
        final AxReferenceKey rKey = new AxReferenceKey(mwr.getKey(), "LocalName");
        mwr.addKey(rKey);
        mwr.addKey(rKey);
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        mwr.removeKey(rKey);
        mwr.removeKey(rKey);

        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Key Use is legal
        final AxKeyUse keyU = new AxKeyUse(mwr.getKey());
        mwr.addKey(keyU);
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
        mwr.removeKey(keyU);

        // Key Use on bad artifact key
        final AxKeyUse keyBU = new AxKeyUse(new AxArtifactKey("SomeKey", "0.0.1"));
        mwr.addKey(keyBU);
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        mwr.removeKey(keyBU);

        // Key Use on bad reference key
        final AxKeyUse keyBRU = new AxKeyUse(new AxReferenceKey("SomeKey", "0.0.1", "Local"));
        mwr.addKey(keyBRU);
        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());
        mwr.removeKey(keyBRU);

        result = new AxValidationResult();
        result = mwr.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }
}
