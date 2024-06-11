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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;

@ExtendWith(MockitoExtension.class)
class ApexModelReaderTest {

    @Test
    void testModelReader() throws IOException, ApexException {
        AxModel model = new DummyApexBasicModelCreator().getModel();
        AxModel invalidModel = new DummyApexBasicModelCreator().getInvalidModel();

        ApexModelWriter<AxModel> modelWriter = new ApexModelWriter<>(AxModel.class);
        modelWriter.setValidate(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        modelWriter.write(model, baos);

        ByteArrayOutputStream baosInvalid = new ByteArrayOutputStream();
        modelWriter.setValidate(false);
        modelWriter.write(invalidModel, baosInvalid);

        ApexModelReader<AxModel> modelReader = new ApexModelReader<>(AxModel.class, true);

        modelReader.setValidate(true);
        assertTrue(modelReader.isValidate());

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel = modelReader.read(bais);
        assertEquals(model, readModel);

        ByteArrayInputStream baisInvalid = new ByteArrayInputStream(baosInvalid.toByteArray());
        assertThatThrownBy(() -> modelReader.read(baisInvalid))
            .hasMessageStartingWith("Apex concept validation failed");
        modelReader.setValidate(false);
        assertFalse(modelReader.isValidate());

        ByteArrayInputStream bais2 = new ByteArrayInputStream(baos.toByteArray());
        AxModel readModel2 = modelReader.read(bais2);
        assertEquals(model, readModel2);

        ByteArrayOutputStream baosJson = new ByteArrayOutputStream();
        modelWriter.write(model, baosJson);

        ByteArrayInputStream baisJson = new ByteArrayInputStream(baosJson.toByteArray());
        AxModel readModelJson = modelReader.read(baisJson);
        assertEquals(model, readModelJson);

        String dummyString = "SomeDummyText";
        ByteArrayInputStream baisDummy = new ByteArrayInputStream(dummyString.getBytes());
        assertThatThrownBy(() -> modelReader.read(baisDummy))
            .hasMessageContaining("Unable to unmarshal Apex concept");
        assertThatThrownBy(() -> modelReader.read((java.io.InputStream) null))
            .hasMessage("concept stream may not be null");

        assertThatThrownBy(() -> {
            FileInputStream fis = new FileInputStream("somewhere/over/the/rainbow");
            modelReader.read(fis);
        }).hasMessageContaining("rainbow");
        final File tempFile = File.createTempFile("Apex", "Dummy");
        BufferedReader br = new BufferedReader(new FileReader(tempFile));
        br.close();
        assertThatThrownBy(() -> modelReader.read(br)).hasMessage("Unable to read Apex concept ");
        assertTrue(tempFile.delete());
    }
}
