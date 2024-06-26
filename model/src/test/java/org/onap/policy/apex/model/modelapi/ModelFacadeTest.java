/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation
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

package org.onap.policy.apex.model.modelapi;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.impl.ModelFacade;

class ModelFacadeTest {

    @Test
    void testModelFacade() {
        assertThatThrownBy(() -> new ModelFacade(null, null))
            .hasMessage("apexModel may not be null");
        final ApexModel apexModel = new ApexModelFactory().createApexModel(null);

        assertThatThrownBy(() -> new ModelFacade(apexModel, null))
            .hasMessage("apexProperties may not be null");
        final Properties modelProperties = new Properties();
        final ModelFacade mf = new ModelFacade(apexModel, modelProperties);

        ApexApiResult result = mf.createModel(null, null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mf.createModel("ModelName", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mf.createModel("ModelName", "0.0.1", null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        modelProperties.setProperty("DEFAULT_CONCEPT_VERSION", "");
        result = mf.createModel("ModelName", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        modelProperties.setProperty("DEFAULT_CONCEPT_VERSION", "£$£$£$");
        result = mf.createModel("ModelName", null, null, null);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        modelProperties.setProperty("DEFAULT_CONCEPT_VERSION", "0.0.1");
        result = mf.createModel("ModelName", null, null, null);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());
        result = mf.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = mf.createModel("ModelName", null, null, null);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mf.updateModel("ModelName", null, UUID.randomUUID().toString(), "New Description");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mf.updateModel("ModelName", "0.0.1", UUID.randomUUID().toString(), "New Description");
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        modelProperties.remove("DEFAULT_CONCEPT_VERSION");
        result = mf.updateModel("ModelName", null, UUID.randomUUID().toString(), "New Description");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mf.updateModel(null, null, UUID.randomUUID().toString(), "New Description");
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = mf.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = mf.updateModel("name", "0.0.1", UUID.randomUUID().toString(), "New Description");
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = mf.getModelKey();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mf.listModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = mf.deleteModel();
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        assertNotNull(mf);
    }
}
