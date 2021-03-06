/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.examples.myfirstpolicy;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * A factory for creating MFPDomainModel objects.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class MfpDomainModelFactory {

    private static final String MFP1PATH =
            "target/classes/examples/models/MyFirstPolicy/1/MyFirstPolicyModelMvel_0.0.1.json";
    private static final String MFP1_ALT_PATH =
            "target/classes/examples/models/MyFirstPolicy/1/MyFirstPolicyModelJavascript_0.0.1.json";
    private static final String MFP2PATH =
            "target/classes/examples/models/MyFirstPolicy/2/MyFirstPolicyModel_0.0.1.json";

    /**
     * Gets the MyFirstPolicy#1 policy model.
     *
     * @return the MyFirstPolicy#1 policy model
     */
    public AxPolicyModel getMfp1PolicyModel() {
        java.util.TimeZone.getTimeZone("gmt");
        try {
            final ApexModelReader<AxPolicyModel> reader = new ApexModelReader<>(AxPolicyModel.class);
            return reader.read(ResourceUtils.getResourceAsString(MfpDomainModelFactory.MFP1PATH));
        } catch (final Exception e) {
            throw new ApexRuntimeException("Failed to build MyFirstPolicy from path: " + MfpDomainModelFactory.MFP1PATH,
                    e);
        }
    }

    /**
     * Gets the MyFirstPolicy#1 policy model, with alternate JavaScript task logic.
     *
     * @return the MyFirstPolicy#1 policy model
     */
    public AxPolicyModel getMfp1AltPolicyModel() {
        java.util.TimeZone.getTimeZone("gmt");
        try {
            final ApexModelReader<AxPolicyModel> reader = new ApexModelReader<>(AxPolicyModel.class);
            return reader.read(ResourceUtils.getResourceAsString(MfpDomainModelFactory.MFP1_ALT_PATH));
        } catch (final Exception e) {
            throw new ApexRuntimeException(
                    "Failed to build MyFirstPolicy_ALT from path: " + MfpDomainModelFactory.MFP1_ALT_PATH, e);
        }
    }

    /**
     * Gets the MyFirstPolicy#1 policy model.
     *
     * @return the MyFirstPolicy#1 policy model
     */
    public AxPolicyModel getMfp2PolicyModel() {
        try {
            final ApexModelReader<AxPolicyModel> reader = new ApexModelReader<>(AxPolicyModel.class);
            return reader.read(ResourceUtils.getResourceAsString(MfpDomainModelFactory.MFP2PATH));
        } catch (final Exception e) {
            throw new ApexRuntimeException("Failed to build MyFirstPolicy from path: " + MfpDomainModelFactory.MFP2PATH,
                    e);
        }
    }

}
