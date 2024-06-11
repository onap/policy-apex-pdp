/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2024 Nordix Foundation.
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Console}.
 */

class ConsoleTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    void testConsole() {
        Console.CONSOLE.setAppName(null);
        Console.CONSOLE.info("");
        Console.CONSOLE.error("");
        Console.CONSOLE.debug("");
        Console.CONSOLE.progress("");
        Console.CONSOLE.warn("");
        Console.CONSOLE.trace("");
        Console.CONSOLE.stacktrace(null);
        assertThat(errContent.toString().trim()).isEmpty();

        Console.CONSOLE.setAppName("");
        Console.CONSOLE.set(Console.TYPE_DEBUG, Console.TYPE_ERROR, Console.TYPE_INFO, Console.TYPE_PROGRESS,
            Console.TYPE_WARNING, Console.TYPE_TRACE, Console.TYPE_STACKTRACE);
        Console.CONSOLE.configure(Console.CONFIG_COLLECT_WARNINGS);
        logMessage();
        assertThat(errContent.toString().trim()).contains("debug: debug message.")
            .contains("error: error message.").contains("info message.").contains("progress: progress message.")
            .contains("warning: warn message.").contains("trace: trace message.")
            .contains("exception message: Exception message.");
        reset();

        Console.CONSOLE.setAppName("ConsoleTest");
        Console.CONSOLE.configure(Console.CONFIG_COLLECT_ERRORS);
        logMessage();
        assertThat(errContent.toString().trim())
            .contains("ConsoleTest: debug: debug message.").contains("ConsoleTest: error: error message.")
            .contains("ConsoleTest: info message.").contains("ConsoleTest: progress: progress message.")
            .contains("ConsoleTest: warning: warn message.").contains("ConsoleTest: trace: trace message.")
            .contains("ConsoleTest:  exception message: Exception message.");
        reset();

        Console.CONSOLE.deActivate(Console.TYPE_DEBUG);
        Console.CONSOLE.deActivate(Console.TYPE_ERROR);
        Console.CONSOLE.deActivate(Console.TYPE_INFO);
        Console.CONSOLE.deActivate(Console.TYPE_PROGRESS);
        Console.CONSOLE.deActivate(Console.TYPE_WARNING);
        Console.CONSOLE.deActivate(Console.TYPE_TRACE);
        logMessage();
        assertThat(errContent.toString().trim()).isEmpty();
        reset();

        Console.CONSOLE.set(Console.TYPE_STACKTRACE);
        Console.CONSOLE.setAppName(null);
        Console.CONSOLE.stacktrace(new Exception("Exception message.", new Throwable("test stacktrace!")));
        assertThat(errContent.toString()).contains("exception message: Exception message.")
            .contains("exception cause: java.lang.Throwable: test stacktrace!");
        reset();
    }

    private void logMessage() {
        Console.CONSOLE.debug("debug message.");
        Console.CONSOLE.error("error message.");
        Console.CONSOLE.info("info message.");
        Console.CONSOLE.progress("progress message.");
        Console.CONSOLE.warn("warn message.");
        Console.CONSOLE.trace("trace message.");
        Console.CONSOLE.stacktrace(new Exception("Exception message."));
    }

    private void reset() {
        Console.CONSOLE.resetErrors();
        Console.CONSOLE.resetWarnings();
        errContent.reset();
    }
}
