/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.tools.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.cli.Option;
import org.junit.jupiter.api.Test;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;


/**
 * Tests for {@link CliParser}.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
class CliParserTest {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(CliParserTest.class);

    /**
     * Test app version.
     */
    @Test
    void testAppVersion() {
        final CliParser cli = new CliParser();
        assertNotNull(cli);
        LOGGER.info(cli.getAppVersion());
    }

    /**
     * testAddAndGetOptionException.
     */
    @Test
    void testAddAndGetOptionException() {
        final CliParser cli = new CliParser();
        assertThatThrownBy(() -> {
            cli.addOption(null);
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining("CLI parser: given option was null");
    }

    /**
     * testParseAndGetCli.
     */
    @Test
    void testParseAndGetCli() {
        final CliParser cli = new CliParser();
        final Option option = new Option("g", "Good option.");
        cli.addOption(option);
        cli.parseCli(new String[] {"-g"});
        assertThat(cli.getCommandLine().hasOption("-g")).isTrue();
    }
}
