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

package org.onap.policy.apex.model.basicmodel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.handling.TestApexBasicModelCreator;

public class ModelServiceTest {

    @Test
    public void testModelService() {
        ModelService.clear();

        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        try {
            ModelService.getModel(AxKeyInformation.class);
        } catch (final Exception e) {
            assertEquals("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service", e.getMessage());
        }

        ModelService.registerModel(AxKeyInformation.class,
                        new TestApexBasicModelCreator().getModel().getKeyInformation());
        assertTrue(ModelService.existsModel(AxKeyInformation.class));
        assertNotNull(ModelService.getModel(AxKeyInformation.class));

        ModelService.deregisterModel(AxKeyInformation.class);

        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        try {
            ModelService.getModel(AxKeyInformation.class);
        } catch (final Exception e) {
            assertEquals("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service", e.getMessage());
        }

        ModelService.registerModel(AxKeyInformation.class,
                        new TestApexBasicModelCreator().getModel().getKeyInformation());
        assertTrue(ModelService.existsModel(AxKeyInformation.class));
        assertNotNull(ModelService.getModel(AxKeyInformation.class));

        ModelService.clear();
        assertFalse(ModelService.existsModel(AxKeyInformation.class));
        try {
            ModelService.getModel(AxKeyInformation.class);
        } catch (final Exception e) {
            assertEquals("Model for org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation "
                            + "not found in model service", e.getMessage());
        }

    }
}
