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

import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.test.common.model.SampleDomainModelFactory;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexSamplePolicyModelCreator implements TestApexModelCreator<AxPolicyModel> {
    private String logicExecutorType;

    public TestApexSamplePolicyModelCreator(final String logicExecutorType) {
        this.logicExecutorType = logicExecutorType;
    }

    @Override
    public AxPolicyModel getModel() {
        return new SampleDomainModelFactory().getSamplePolicyModel(logicExecutorType);
    }

    @Override
    public AxPolicyModel getMalstructuredModel() {
        return null;
    }

    @Override
    public AxPolicyModel getObservationModel() {
        return null;
    }

    @Override
    public AxPolicyModel getWarningModel() {
        return getModel();
    }

    @Override
    public AxPolicyModel getInvalidModel() {
        return null;
    }
}
