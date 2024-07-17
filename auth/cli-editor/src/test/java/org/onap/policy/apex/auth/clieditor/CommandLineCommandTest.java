/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020-2021, 2024 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandLineCommandTest {

    CommandLineCommand commandLineCommand = null;

    @BeforeEach
    void initializeCommandLineCommand() {
        commandLineCommand = new CommandLineCommand();
    }

    @Test
    void testCommandLine() {
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

    @Test
    void testInvalidApiClassName() {
        assertThrows(CommandLineException.class, () -> commandLineCommand.getApiClassName());
    }

    @Test
    void testGetValidApiClassName() {
        commandLineCommand.setApiMethod("Java.Get");
        assertEquals("Java", commandLineCommand.getApiClassName());
    }

    @Test
    void testInvalidApiMethodName() {
        assertThrows(CommandLineException.class, () -> commandLineCommand.getApiMethodName());
    }

    @Test()
    void testInvalidApiMethod() {
        commandLineCommand.setApiMethod("fail.");
        assertEquals("fail.", commandLineCommand.getApiMethod());
        assertThrows(CommandLineException.class, () -> commandLineCommand.getApiMethodName());
    }

    @Test
    void testValidApiMethodName() {
        commandLineCommand.setApiMethod("Java.Get");
        assertEquals("Get", commandLineCommand.getApiMethodName());
    }

    @Test
    void testGetHelp() {
        List<String> keywordList = commandLineCommand.getKeywordlist();
        List<CommandLineArgument> argumentList = commandLineCommand.getArgumentList();
        assertEquals("{}: ", commandLineCommand.getHelp());
        keywordList.add("TestKeyword");
        argumentList.add(new CommandLineArgument("TestArgument"));
        argumentList.add(null);
        assertEquals("TestKeyword {}: \n" + "\tTestArgument: (M) ", commandLineCommand.getHelp());
    }

    @Test
    void testCompareTo() {
        CommandLineCommand otherCommand = new CommandLineCommand();
        otherCommand.setSystemCommand(true);
        assertThat(commandLineCommand).isNotEqualByComparingTo(otherCommand);
        otherCommand.getArgumentList().add(new CommandLineArgument("testArgument"));
        assertThat(commandLineCommand).isNotEqualByComparingTo(otherCommand);
    }

    @Test
    void testCompareKeywordList() {
        CommandLineCommand otherCommand = new CommandLineCommand();
        otherCommand.getKeywordlist().add("test");
        assertThat(commandLineCommand).isNotEqualByComparingTo(otherCommand);
        commandLineCommand.getKeywordlist().add("test");
        assertEquals(0, commandLineCommand.compareTo(otherCommand));
        commandLineCommand.getKeywordlist().add("test2");
        assertThat(commandLineCommand).isNotEqualByComparingTo(otherCommand);
        otherCommand.getKeywordlist().add("test3");
        assertThat(commandLineCommand).isNotEqualByComparingTo(otherCommand);
    }

    @Test
    void testHashCode() {
        CommandLineCommand otherCommand = new CommandLineCommand();
        assertEquals(commandLineCommand.hashCode(), otherCommand.hashCode());
        commandLineCommand.getKeywordlist().add("Test");
        otherCommand.setDescription(null);
        otherCommand.setApiMethod(null);
        otherCommand.setName(null);
        assertNotEquals(commandLineCommand.hashCode(), otherCommand.hashCode());
    }

    @Test
    void testEquals() {
        assertNotEquals(commandLineCommand, new Object());
        assertNotEquals(null, commandLineCommand);

        CommandLineCommand otherCommand = new CommandLineCommand();
        assertEquals(commandLineCommand, otherCommand);
        otherCommand.getKeywordlist().add("TestKeyword");
        assertNotEquals(commandLineCommand, otherCommand);
    }

    @Test
    void testExecuteInvalidCommand() {
        var handler = new ApexModelHandler(new Properties());
        var command = new CommandLineCommand();
        command.setApiMethod("java.invalid"); // invalid class
        assertThrows(CommandLineException.class, () -> handler.executeCommand(command, null, null));

        command.setApiMethod("org.onap.policy.apex.model.modelapi.ApexModel.invalid"); //invalid method
        assertThrows(CommandLineException.class, () -> handler.executeCommand(command, null, null));

        command.setApiMethod("org.onap.policy.apex.model.modelapi.ApexModel.saveToFile");
        SortedMap<String, CommandLineArgumentValue> map = new TreeMap<>();
        map.put("key", new CommandLineArgumentValue(null));
        assertThrows(CommandLineException.class, () -> handler.executeCommand(command, map, null));
    }

    @Test
    void testCommandLineArgument() {
        var argument = new CommandLineArgument();
        assertThat(argument).isEqualByComparingTo(argument);
        var otherArgument = new CommandLineArgument("otherArgument");
        assertThat(argument).isLessThan(otherArgument);

    }

    @Test
    void testKeyWordNodeCommands() {
        var kn = new KeywordNode("test");
        var cmd = new CommandLineCommand();
        List<String> keywordList = List.of("key1", "key2");
        kn.processKeywords(keywordList, cmd);
        assertNotNull(kn.getCommands());
    }
}
