/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf;

import org.apache.commons.cli.Option;
import org.onap.policy.apex.services.onappf.exception.ApexStarterRunTimeException;
import org.onap.policy.common.utils.cmd.CommandLineArgumentsHandler;
import org.onap.policy.common.utils.cmd.CommandLineException;

/**
 * This class reads and handles command line parameters for the apex starter.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterCommandLineArguments extends CommandLineArgumentsHandler {
    private static final String APEX_COMPONENT = "policy apex starter";

    /**
     * Construct the options for the CLI editor.
     */
    public ApexStarterCommandLineArguments() {
        super(ApexStarterMain.class.getName(), APEX_COMPONENT, apexPropertyFileOpt());
    }

    /**
     * Construct the options for the CLI editor and parse in the given arguments.
     *
     * @param args The command line arguments
     */
    public ApexStarterCommandLineArguments(final String[] args) {
        this();

        try {
            parse(args);
        } catch (final CommandLineException e) {
            throw new ApexStarterRunTimeException("parse error on apex starter parameters", e);
        }
    }

    /**
     * Build the extra option property-file for ApexStarter.
     *
     * @return the property-file option
     */
    private static Option apexPropertyFileOpt() {
        return Option.builder("p").longOpt("property-file")
                .desc("the full path to the topic property file to use, "
                        + "the property file contains the policy apex starter properties")
                .hasArg().argName("PROP_FILE").required(false).type(String.class).build();
    }
}
