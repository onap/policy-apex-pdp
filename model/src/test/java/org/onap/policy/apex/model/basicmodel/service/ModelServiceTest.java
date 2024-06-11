/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

package org.onap.policy.apex.model.basicmodel.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.handling.DummyApexBasicModelCreator;

class ModelServiceTest {

    @Test
    void testModelService() {
        ModelService.clear();

        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        assertThatThrownBy(() -> ModelService.getModel(AxKeyInformation.class))
            .hasMessage("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service");
        ModelService.registerModel(AxKeyInformation.class,
                        new DummyApexBasicModelCreator().getModel().getKeyInformation());
        assertTrue(ModelService.existsModel(AxKeyInformation.class));
        assertNotNull(ModelService.getModel(AxKeyInformation.class));

        ModelService.deregisterModel(AxKeyInformation.class);

        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        assertThatThrownBy(() -> ModelService.getModel(AxKeyInformation.class))
            .hasMessage("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service");
        ModelService.registerModel(AxKeyInformation.class,
                        new DummyApexBasicModelCreator().getModel().getKeyInformation());
        assertTrue(ModelService.existsModel(AxKeyInformation.class));
        assertNotNull(ModelService.getModel(AxKeyInformation.class));

        ModelService.clear();
        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        assertThatThrownBy(() -> ModelService.getModel(AxKeyInformation.class))
            .hasMessage("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service");
    }
}
