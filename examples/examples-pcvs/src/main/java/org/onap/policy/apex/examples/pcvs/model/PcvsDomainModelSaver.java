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

package org.onap.policy.apex.examples.pcvs.model;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelSaver;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The Class PcvsDomainModelSaver.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class PcvsDomainModelSaver {

    /** Private constructor to prevent instantiation. */
    private PcvsDomainModelSaver() {}

    /**
     * Write all PCVS models to args[0].
     *
     * @param args uses <code>arg[0]</code> for directory information
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        if (args.length != 2) {
            System.err.println(
                    "usage: " + PcvsDomainModelSaver.class.getCanonicalName() + " workingDirectory modelDirectory");
            return;
        }

        final AxPolicyModel pcvsPolicyModel = new PcvsDomainModelFactory().getPcvsVpnSlaSPolicyModel(args[0]);
        final ApexModelSaver<AxPolicyModel> pcvsModelSaver =
                new ApexModelSaver<>(AxPolicyModel.class, pcvsPolicyModel, args[1] + "vpnsla/");
        pcvsModelSaver.apexModelWriteJson();
        pcvsModelSaver.apexModelWriteXml();

    }
}
