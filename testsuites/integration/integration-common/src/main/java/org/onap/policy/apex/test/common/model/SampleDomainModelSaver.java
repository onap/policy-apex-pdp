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

package org.onap.policy.apex.test.common.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelSaver;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * This class saves sample domain models to disk in XML and JSON format.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class SampleDomainModelSaver {
    /**
     * Private default constructor to prevent subclassing.
     */
    private SampleDomainModelSaver() {
    }

    /**
     * Write the sample Models to args[0].
     *
     * @param args Not used
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != 1) {
            System.err.println("usage: " + SampleDomainModelSaver.class.getCanonicalName() + " modelDirectory");
            return;
        }

        // Save Java model
        final AxPolicyModel javaPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("JAVA");
        final ApexModelSaver<AxPolicyModel> javaModelSaver = new ApexModelSaver<AxPolicyModel>(AxPolicyModel.class, javaPolicyModel, args[0]);
        javaModelSaver.apexModelWriteJson();
        javaModelSaver.apexModelWriteXml();

        // Save Javascript model
        final AxPolicyModel javascriptPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("JAVASCRIPT");
        final ApexModelSaver<AxPolicyModel> javascriptModelSaver = new ApexModelSaver<AxPolicyModel>(AxPolicyModel.class, javascriptPolicyModel, args[0]);
        javascriptModelSaver.apexModelWriteJson();
        javascriptModelSaver.apexModelWriteXml();

        // Save JRuby model
        final AxPolicyModel jRubyPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("JRUBY");
        final ApexModelSaver<AxPolicyModel> jRubyModelSaver = new ApexModelSaver<AxPolicyModel>(AxPolicyModel.class, jRubyPolicyModel, args[0]);
        jRubyModelSaver.apexModelWriteJson();
        jRubyModelSaver.apexModelWriteXml();

        // Save Jython model
        final AxPolicyModel jythonPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("JYTHON");
        final ApexModelSaver<AxPolicyModel> jythonModelSaver = new ApexModelSaver<AxPolicyModel>(AxPolicyModel.class, jythonPolicyModel, args[0]);
        jythonModelSaver.apexModelWriteJson();
        jythonModelSaver.apexModelWriteXml();

        // Save MVEL model
        final AxPolicyModel mvelPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        final ApexModelSaver<AxPolicyModel> mvelModelSaver = new ApexModelSaver<AxPolicyModel>(AxPolicyModel.class, mvelPolicyModel, args[0]);
        mvelModelSaver.apexModelWriteJson();
        mvelModelSaver.apexModelWriteXml();
    }

}
