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

package org.onap.policy.apex.model.basicmodel.handling;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;

public class SupportBasicModelTester {

    @Test
    public void testNormalModelCreator() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new DummyApexBasicModelCreator());

        testApexModel.testApexModelValid();
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateObservation())
            .hasMessageContaining("model should have observations");
        testApexModel.testApexModelVaidateWarning();
        testApexModel.testApexModelVaidateInvalidModel();
        testApexModel.testApexModelVaidateMalstructured();

        testApexModel.testApexModelWriteReadJson();
        testApexModel.testApexModelWriteReadXml();
    }

    @Test
    public void testModelsUnequal() throws ApexException {
        final TestApexModel<AxModel> testApexModel0 = new TestApexModel<AxModel>(AxModel.class,
            new DummyApexBasicModelCreator());
        final TestApexModel<AxModel> testApexModel1 = new TestApexModel<AxModel>(AxModel.class,
            new DummyApexBasicModelCreator());

        testApexModel1.getModel().getKey().setVersion("0.0.2");

        assertThatThrownBy(() -> testApexModel0.checkModelEquality(testApexModel0.getModel(), testApexModel1.getModel(),
                "Models are not equal")).hasMessageContaining("Models are not equal");
    }

    @Test
    public void testModelCreator0() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new SupportApexModelCreator0());

        testApexModel.testApexModelValid();
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateObservation())
            .hasMessageContaining("model should have observations");
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateWarning())
            .hasMessageContaining("model should have warnings");
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateInvalidModel())
            .hasMessageContaining("model should not be valid ***validation of model successful***");
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateMalstructured())
            .hasMessageContaining("model should not be valid ***validation of model successful***");
    }

    @Test
    public void testModelCreator1() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new SupportApexModelCreator1());

        assertThatThrownBy(() -> testApexModel.testApexModelValid())
            .hasMessageContaining("model is invalid");
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateObservation())
            .hasMessageContaining("model is invalid");
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateWarning())
            .hasMessageContaining("model is invalid");
        testApexModel.testApexModelVaidateInvalidModel();
        testApexModel.testApexModelVaidateMalstructured();
    }

    @Test
    public void testModelCreator2() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new SupportApexModelCreator2());

        testApexModel.testApexModelValid();
        testApexModel.testApexModelVaidateObservation();
        assertThatThrownBy(() -> testApexModel.testApexModelVaidateWarning())
            .hasMessageContaining("model should have warnings");
    }

    @Test
    public void testModelCreator1XmlJson() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new SupportApexModelCreator1());

        assertThatThrownBy(() -> testApexModel.testApexModelWriteReadJson())
            .hasMessageContaining("error processing file");

        assertThatThrownBy(() -> testApexModel.testApexModelWriteReadXml())
            .hasMessageContaining("error processing file");
    }
}
