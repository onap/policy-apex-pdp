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

package org.onap.policy.apex.examples.myfirstpolicy;

import org.onap.policy.apex.examples.myfirstpolicy.model.MfpDomainModelFactory;
import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Create the MyFirstPolicyModel - base class.
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
public abstract class TestMfpModelCreator implements TestApexModelCreator<AxPolicyModel> {

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator#getMalstructuredModel()
     */
    @Override
    public AxPolicyModel getMalstructuredModel() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator#getObservationModel()
     */
    @Override
    public AxPolicyModel getObservationModel() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator#getWarningModel()
     */
    @Override
    public AxPolicyModel getWarningModel() {
        return getModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator#getInvalidModel()
     */
    @Override
    public AxPolicyModel getInvalidModel() {
        return null;
    }

    /**
     * Create the MyFirstPolicyModel #1.
     */
    public static class TestMfp1ModelCreator extends TestMfpModelCreator {

        /*
         * (non-Javadoc)
         *
         * @see org.onap.policy.apex.model.basicmodel.handling.ApexModelCreator#getModel()
         */
        @Override
        public AxPolicyModel getModel() {
            return new MfpDomainModelFactory().getMfp1PolicyModel();
        }
    }

    /**
     * Create the MyFirstPolicyModel#2.
     */
    public static class TestMfp2ModelCreator extends TestMfpModelCreator {

        /*
         * (non-Javadoc)
         *
         * @see org.onap.policy.apex.model.basicmodel.handling.ApexModelCreator#getModel()
         */
        @Override
        public AxPolicyModel getModel() {
            return new MfpDomainModelFactory().getMfp2PolicyModel();
        }
    }

}
