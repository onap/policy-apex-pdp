/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.auth.clieditor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class CommandLineCommandTest {

    CommandLineCommand commandLineCommand = null;

    @Before
    public void initializeCommandLineCommand() {
        commandLineCommand = new CommandLineCommand();
    }

    @Test
    public void testCommandLine() {
        commandLineCommand.setName("TestName");
        commandLineCommand.setDescription("testDescription");
        commandLineCommand.setSystemCommand(true);
        assertTrue(commandLineCommand.isSystemCommand());
        assertEquals("testDescription", commandLineCommand.getDescription());
        assertEquals("TestName", commandLineCommand.getName());
        assertEquals(
            "CommandLineCommand(name=TestName, keywordlist=[], argumentList=[], apiMethod=, systemCommand=true,"
            + " description=testDescription)", commandLineCommand.toString());
    }

    @Test(expected = CommandLineException.class)
    public void testInvalidApiClassName() {
        commandLineCommand.getApiClassName();
    }

    @Test
    public void testGetValidApiClassName() {
        commandLineCommand.setApiMethod("Java.Get");
        assertEquals("Java", commandLineCommand.getApiClassName());
    }

    @Test(expected = CommandLineException.class)
    public void testInvalidApiMethodName() {
        commandLineCommand.getApiMethodName();
    }

    @Test()
    public void testInvalidApiMethod() {
        commandLineCommand.setApiMethod("fail.");
        assertEquals("fail.", commandLineCommand.getApiMethod());
        assertThrows(CommandLineException.class, () -> commandLineCommand.getApiMethodName());
    }

    @Test
    public void testValidApiMethodName() {
        commandLineCommand.setApiMethod("Java.Get");
        assertEquals("Get", commandLineCommand.getApiMethodName());
    }

    @Test
    public void testGetHelp() {
        List<String> keywordList = commandLineCommand.getKeywordlist();
        List<CommandLineArgument> argumentList = commandLineCommand.getArgumentList();
        assertEquals("{}: ", commandLineCommand.getHelp());
        keywordList.add("TestKeyword");
        argumentList.add(new CommandLineArgument("TestArgument"));
        argumentList.add(null);
        assertEquals("TestKeyword {}: \n" + "\tTestArgument: (M) ", commandLineCommand.getHelp());
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, commandLineCommand.compareTo(commandLineCommand));
        CommandLineCommand otherCommand = new CommandLineCommand();
        otherCommand.setSystemCommand(true);
        assertThat(commandLineCommand.compareTo(otherCommand)).isNotZero();
        otherCommand.getArgumentList().add(new CommandLineArgument("testArgument"));
        assertThat(commandLineCommand.compareTo(otherCommand)).isNotZero();
    }

    @Test
    public void testCompareKeywordList() {
        CommandLineCommand otherCommand = new CommandLineCommand();
        otherCommand.getKeywordlist().add("test");
        assertThat(commandLineCommand.compareTo(otherCommand)).isNotZero();
        commandLineCommand.getKeywordlist().add("test");
        assertEquals(0, commandLineCommand.compareTo(otherCommand));
        commandLineCommand.getKeywordlist().add("test2");
        assertThat(commandLineCommand.compareTo(otherCommand)).isNotZero();
        otherCommand.getKeywordlist().add("test3");
        assertThat(commandLineCommand.compareTo(otherCommand)).isNotZero();
    }

    @Test
    public void testHashCode() {
        CommandLineCommand otherCommand = new CommandLineCommand();
        assertEquals(commandLineCommand.hashCode(), otherCommand.hashCode());
        commandLineCommand.getKeywordlist().add("Test");
        otherCommand.setDescription(null);
        otherCommand.setApiMethod(null);
        otherCommand.setName(null);
        assertNotEquals(commandLineCommand.hashCode(), otherCommand.hashCode());
    }

    @Test
    public void testEquals() {
        assertNotEquals(commandLineCommand, new Object());
        assertEquals(commandLineCommand, commandLineCommand);
        assertNotEquals(commandLineCommand, null);

        CommandLineCommand otherCommand = new CommandLineCommand();
        assertEquals(commandLineCommand, otherCommand);
        otherCommand.getKeywordlist().add("TestKeyword");
        assertNotEquals(commandLineCommand, otherCommand);
    }
}
