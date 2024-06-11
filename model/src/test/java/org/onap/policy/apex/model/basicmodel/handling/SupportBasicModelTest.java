/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022, 2024 Nordix Foundation
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

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;

class SupportBasicModelTest {

    @Test
    void testNormalModelCreator() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<>(AxModel.class,
            new DummyApexBasicModelCreator());

        testApexModel.testApexModelValid();
        assertThatThrownBy(testApexModel::testApexModelValidateObservation)
            .hasMessage("model should have observations");
        testApexModel.testApexModelValidateWarning();
        testApexModel.testApexModelValidateInvalidModel();
        testApexModel.testApexModelValidateMalstructured();

        testApexModel.testApexModelWriteReadJson();
    }

    @Test
    void testModelsUnequal() {
        final TestApexModel<AxModel> testApexModel0 = new TestApexModel<>(AxModel.class,
            new DummyApexBasicModelCreator());
        final TestApexModel<AxModel> testApexModel1 = new TestApexModel<>(AxModel.class,
            new DummyApexBasicModelCreator());

        testApexModel1.getModel().getKey().setVersion("0.0.2");

        assertThatThrownBy(() -> testApexModel0.checkModelEquality(testApexModel0.getModel(), testApexModel1.getModel(),
                "Models are not equal")).hasMessage("Models are not equal");
    }

    @Test
    void testModelCreator0() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<>(AxModel.class,
            new SupportApexModelCreator0());

        testApexModel.testApexModelValid();
        assertThatThrownBy(testApexModel::testApexModelValidateObservation)
            .hasMessage("model should have observations");
        assertThatThrownBy(testApexModel::testApexModelValidateWarning)
            .hasMessage("model should have warnings");
        assertThatThrownBy(testApexModel::testApexModelValidateInvalidModel)
            .hasMessage("model should not be valid ***validation of model successful***");
        assertThatThrownBy(testApexModel::testApexModelValidateMalstructured)
            .hasMessage("model should not be valid ***validation of model successful***");
    }

    @Test
    void testModelCreator1() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<AxModel>(AxModel.class,
            new SupportApexModelCreator1());

        assertThatThrownBy(testApexModel::testApexModelValid)
            .hasMessageStartingWith("model is invalid");
        assertThatThrownBy(testApexModel::testApexModelValidateObservation)
            .hasMessageStartingWith("model is invalid");
        assertThatThrownBy(testApexModel::testApexModelValidateWarning)
            .hasMessageStartingWith("model is invalid");
        testApexModel.testApexModelValidateInvalidModel();
        testApexModel.testApexModelValidateMalstructured();
    }

    @Test
    void testModelCreator2() throws ApexException {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<>(AxModel.class,
            new SupportApexModelCreator2());

        testApexModel.testApexModelValid();
        testApexModel.testApexModelValidateObservation();
        assertThatThrownBy(testApexModel::testApexModelValidateWarning)
            .hasMessage("model should have warnings");
    }

    @Test
    void testModelCreator1Json() {
        final TestApexModel<AxModel> testApexModel = new TestApexModel<>(AxModel.class,
            new SupportApexModelCreator1());

        assertThatThrownBy(testApexModel::testApexModelWriteReadJson)
            .hasMessageStartingWith("error processing file");
    }
}
