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

import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Create the MyFirstPolicyModel - base class.
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
public abstract class TestMfpModelCreator implements TestApexModelCreator<AxPolicyModel> {

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxPolicyModel getMalstructuredModel() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxPolicyModel getObservationModel() {
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxPolicyModel getWarningModel() {
        return getModel();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxPolicyModel getInvalidModel() {
        return null;
    }

    /**
     * Create the MyFirstPolicyModel #1.
     */
    public static class TestMfp1ModelCreator extends TestMfpModelCreator {

        /**
         * {@inheritDoc}.
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

        /**
         * {@inheritDoc}.
         */
        @Override
        public AxPolicyModel getModel() {
            return new MfpDomainModelFactory().getMfp2PolicyModel();
        }
    }
}
