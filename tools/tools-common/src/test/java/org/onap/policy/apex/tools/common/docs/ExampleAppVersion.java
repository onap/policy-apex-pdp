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

package org.onap.policy.apex.tools.common.docs;

////
//// NOTE: This file contains tags for ASCIIDOC
//// DO NOT REMOVE any of those tag lines, e.g.
////// tag::**
////// end::**
////

import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.onap.policy.apex.tools.common.CliOptions;
import org.onap.policy.apex.tools.common.CliParser;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Examples for documentation using {@link CliParser#getAppVersion()}.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class ExampleAppVersion {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ExampleAppVersion.class);

    /** Test example app version. */
    @Test
    public void testExampleAppVersion() {
        final String[] args = new String[] { "-v" };

        // tag::setupParser[]
        final CliParser cli = new CliParser();
        cli.addOption(CliOptions.VERSION);
        final CommandLine cmd = cli.parseCli(args);
        // end::setupParser[]

        // tag::processCliVersion[]
        // version is an exit option, print version and exit
        if (cmd.hasOption('v') || cmd.hasOption("version")) {
            LOGGER.info("myApp" + " " + cli.getAppVersion());
            return;
        }
        // end::processCliVersion[]
    }
}
