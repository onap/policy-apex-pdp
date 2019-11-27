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

package org.onap.policy.apex.examples.adaptive.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelSaver;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class saves sample domain models to disk in XML and JSON format.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class AdaptiveDomainModelSaver {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AdaptiveDomainModelSaver.class);

    /**
     * Private default constructor to prevent subclassing.
     */
    private AdaptiveDomainModelSaver() {
        // Prevent subclassing
    }

    /**
     * Write the AADM model to args[0].
     *
     * @param args Not used
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != 1) {
            LOGGER.error("usage: " + AdaptiveDomainModelSaver.class.getName() + " modelDirectory");
            return;
        }

        // Save Anomaly Detection model
        final AxPolicyModel adPolicyModel = new AdaptiveDomainModelFactory().getAnomalyDetectionPolicyModel();
        final ApexModelSaver<AxPolicyModel> adModelSaver = new ApexModelSaver<>(AxPolicyModel.class, adPolicyModel,
                        args[0]);
        adModelSaver.apexModelWriteJson();
        adModelSaver.apexModelWriteXml();

        // Save Auto Learn model
        final AxPolicyModel alPolicyModel = new AdaptiveDomainModelFactory().getAutoLearnPolicyModel();
        final ApexModelSaver<AxPolicyModel> alModelSaver = new ApexModelSaver<>(AxPolicyModel.class, alPolicyModel,
                        args[0]);
        alModelSaver.apexModelWriteJson();
        alModelSaver.apexModelWriteXml();
    }
}
