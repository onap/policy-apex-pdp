/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022-2024 Nordix Foundation
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

@ExtendWith(MockitoExtension.class)
class ApexModelWriterTest {

    @Test
    void testModelWriter() throws ApexException {
        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<>(AxModel.class);

        modelWriter.setValidate(true);
        assertTrue(modelWriter.isValidate());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        AxModel model = new DummyApexBasicModelCreator().getModel();

        modelWriter.write(model, baos);

        modelWriter.setValidate(false);
        modelWriter.write(model, baos);

        modelWriter.setValidate(true);
        model.getKeyInformation().getKeyInfoMap().clear();
        assertThatThrownBy(() -> modelWriter.write(model, baos))
            .hasMessageContaining("Apex concept (BasicModel:0.0.1) validation failed");
        model.getKeyInformation().generateKeyInfo(model);

        assertThatThrownBy(() -> modelWriter.write(null, baos))
            .hasMessage("concept may not be null");

        ByteArrayOutputStream nullBaos = null;
        assertThatThrownBy(() -> modelWriter.write(model, nullBaos))
            .hasMessage("concept stream may not be null");
    }
}
