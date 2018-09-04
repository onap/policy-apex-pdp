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

package org.onap.policy.apex.plugins.executor.test.script.handling;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class TestApexSamplePolicyCreateModelFiles {
    @Test
    public void testModelWriteReadJava() throws Exception {
        final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator("JAVA");
        final TestApexModel<AxPolicyModel> testApexPolicyModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
        testApexPolicyModel.testApexModelWriteReadXml();
        testApexPolicyModel.testApexModelWriteReadJson();
    }

    @Test
    public void testModelWriteReadJavascript() throws Exception {
        final TestApexSamplePolicyModelCreator apexPolicyModelCreator =
                new TestApexSamplePolicyModelCreator("JAVASCRIPT");
        final TestApexModel<AxPolicyModel> testApexPolicyModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
        testApexPolicyModel.testApexModelWriteReadXml();
        testApexPolicyModel.testApexModelWriteReadJson();
    }

    @Test
    public void testModelWriteReadJRuby() throws Exception {
        final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator("JRUBY");
        final TestApexModel<AxPolicyModel> testApexPolicyModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
        testApexPolicyModel.testApexModelWriteReadXml();
        testApexPolicyModel.testApexModelWriteReadJson();
    }

    @Test
    public void testModelWriteReadJython() throws Exception {
        final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator("JYTHON");
        final TestApexModel<AxPolicyModel> testApexPolicyModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
        testApexPolicyModel.testApexModelWriteReadXml();
        testApexPolicyModel.testApexModelWriteReadJson();
    }

    @Test
    public void testModelWriteReadMvel() throws Exception {
        final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator("MVEL");
        final TestApexModel<AxPolicyModel> testApexPolicyModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
        testApexPolicyModel.testApexModelWriteReadXml();
        testApexPolicyModel.testApexModelWriteReadJson();
    }
}
