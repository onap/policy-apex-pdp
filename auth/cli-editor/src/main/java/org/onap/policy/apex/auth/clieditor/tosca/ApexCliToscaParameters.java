/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.auth.clieditor.tosca;

import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.auth.clieditor.CommandLineException;
import org.onap.policy.apex.auth.clieditor.CommandLineParameters;
import org.onap.policy.apex.auth.clieditor.utils.CliUtils;

/**
 * This class reads and handles command line parameters to the Apex CLI Tosca editor.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */

@Setter
@Getter
public class ApexCliToscaParameters extends CommandLineParameters {

    // The cli tosca editor parameters
    private String apexConfigFileName = null;
    private String inputToscaTemplateFileName = null;
    private String outputToscaPolicyFileName = null;

    /**
     * Validates the command line parameters.
     */
    @Override
    public void validate() {
        super.validate();
        CliUtils.validateReadableFile("Apex Config File", apexConfigFileName);
        CliUtils.validateReadableFile("Input Tosca Template File", inputToscaTemplateFileName);
        CliUtils.validateWritableFile("Output Tosca Policy File", outputToscaPolicyFileName);

        if (!isHelpSet()) {
            if ((null == apexConfigFileName) || (null == inputToscaTemplateFileName)) {
                throw new CommandLineException("Insufficient arguments provided.");
            } else if ((null == getCommandFileName()) && (null == getInputModelFileName())) {
                throw new CommandLineException("Insufficient arguments provided. Specify the command file or the policy"
                    + " model file. Either one of these is required to generate the ToscaPolicy.");
            }
        }
    }
}
