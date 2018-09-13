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

package org.onap.policy.apex.examples.aadm.model;

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
public final class AadmDomainModelSaver {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AadmDomainModelSaver.class);

    /**
     * Private default constructor to prevent subclassing.
     */
    private AadmDomainModelSaver() {}

    /**
     * Write the AADM model to args[0].
     *
     * @param args Not used
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != 1) {
            LOGGER.error("usage: " + AadmDomainModelSaver.class.getCanonicalName() + " modelDirectory");
            return;
        }

        // Save Java model
        final AxPolicyModel aadmPolicyModel = new AadmDomainModelFactory().getAadmPolicyModel();
        final ApexModelSaver<AxPolicyModel> aadmModelSaver =
                new ApexModelSaver<>(AxPolicyModel.class, aadmPolicyModel, args[0]);
        aadmModelSaver.apexModelWriteJson();
        aadmModelSaver.apexModelWriteXml();
    }
}
