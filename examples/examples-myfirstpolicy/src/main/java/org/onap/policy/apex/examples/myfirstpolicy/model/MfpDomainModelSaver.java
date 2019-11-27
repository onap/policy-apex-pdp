/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.examples.myfirstpolicy.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelSaver;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class MFPDomainModelSaver.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public final class MfpDomainModelSaver {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MfpDomainModelSaver.class);

    /** Private constructor to prevent instantiation. */
    private MfpDomainModelSaver() {
    }

    /**
     * Write the MyFirstPolicy model to args[0].
     *
     * @param args uses <code>arg[0]</code> for directory information
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != 1) {
            LOGGER.error("usage: " + MfpDomainModelSaver.class.getName() + " modelDirectory");
            return;
        }

        // Save Java model
        AxPolicyModel mfpPolicyModel = new MfpDomainModelFactory().getMfp1PolicyModel();
        ApexModelSaver<AxPolicyModel> mfpModelSaver = new ApexModelSaver<>(AxPolicyModel.class, mfpPolicyModel,
                        args[0] + "/1/");
        mfpModelSaver.apexModelWriteJson();
        mfpModelSaver.apexModelWriteXml();

        mfpPolicyModel = new MfpDomainModelFactory().getMfp2PolicyModel();
        mfpModelSaver = new ApexModelSaver<>(AxPolicyModel.class, mfpPolicyModel, args[0] + "/2/");
        mfpModelSaver.apexModelWriteJson();
        mfpModelSaver.apexModelWriteXml();

    }
}
