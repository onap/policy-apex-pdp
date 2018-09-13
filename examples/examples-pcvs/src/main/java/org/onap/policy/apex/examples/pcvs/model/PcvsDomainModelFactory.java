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

import java.io.File;

import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * A factory for creating PCVSDomainModel objects.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class PcvsDomainModelFactory {

    /**
     * Generates the PCVS VPN-SLA policy model from CLI commands and creates an APEX model.
     *
     * @param workingDirectory The working directory for the CLI editor for includes
     *
     * @return the PCVS VPN-SLA policy model
     */
    public AxPolicyModel getPcvsVpnSlaSPolicyModel(final String workingDirectory) {
        final String path = "target/model-gen/pcvs/vpnsla";
        final String file = "policy.json";
        final String full = path + "/" + file;

        final File pathFile = new File(path);
        pathFile.mkdirs();

        final String[] args =
                new String[] {"-c", "src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/vpnsla.apex", "-wd",
                    workingDirectory, "-o", full};

        final ApexCommandLineEditorMain cliEditor = new ApexCommandLineEditorMain(args);
        if (cliEditor.getErrorCount() > 0) {
            throw new ApexRuntimeException(
                    "Apex CLI editor execution failed with " + cliEditor.getErrorCount() + " errors");
        }

        java.util.TimeZone.getTimeZone("gmt");
        try {
            final ApexModelReader<AxPolicyModel> reader = new ApexModelReader<>(AxPolicyModel.class);
            return reader.read(ResourceUtils.getResourceAsString(full));
        } catch (final Exception e) {
            throw new ApexRuntimeException("Failed to build PCVS SLA1 policy from path: " + full, e);
        }
    }

}
