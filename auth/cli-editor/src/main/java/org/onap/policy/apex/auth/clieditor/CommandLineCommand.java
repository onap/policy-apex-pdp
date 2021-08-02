/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class represents a single Apex CLI command that is issued to the Apex Editor Java API
 * {@link org.onap.policy.apex.model.modelapi.ApexEditorApi}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CommandLineCommand implements Comparable<CommandLineCommand> {
    private String name = "";
    private final List<String> keywordlist = new ArrayList<>();
    private final List<CommandLineArgument> argumentList = new ArrayList<>();
    private String apiMethod = "";
    private boolean systemCommand = false;
    private String description = "";

    /**
     * Gets the class name of the class that executes this command in the Java API.
     *
     * @return the class name of the class that executes this command in the Java API
     */
    public String getApiClassName() {
        final int lastDotPos = apiMethod.lastIndexOf('.');
        if (lastDotPos == -1) {
            throw new CommandLineException("invalid API method name specified on command \"" + name
                    + "\", class name not found: " + apiMethod);
        }
        return apiMethod.substring(0, lastDotPos);
    }

    /**
     * Gets the method name of the method that executes this command in the Java API.
     *
     * @return the the method name of the method that executes this command in the Java API
     */
    public String getApiMethodName() {
        final int lastDotPos = apiMethod.lastIndexOf('.');
        if (lastDotPos == -1) {
            throw new CommandLineException("invalid API method name specified on command \"" + name
                    + "\", class name not found: " + apiMethod);
        }
        if (lastDotPos == apiMethod.length() - 1) {
            throw new CommandLineException("no API method name specified on command \"" + name + "\": " + apiMethod);
        }
        return apiMethod.substring(lastDotPos + 1);
    }

    /**
     * Gets help for this command.
     *
     * @return the help for this command
     */
    public String getHelp() {
        final var builder = new StringBuilder();
        for (final String keyword : keywordlist) {
            builder.append(keyword);
            builder.append(' ');
        }
        builder.append('{');
        builder.append(name);
        builder.append("}: ");
        builder.append(description);

        for (final CommandLineArgument argument : argumentList) {
            if (argument == null) {
                continue;
            }
            builder.append("\n\t");
            builder.append(argument.getHelp());
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final CommandLineCommand otherCommand) {
        Assertions.argumentNotNull(otherCommand, "comparison object may not be null");

        if (this == otherCommand) {
            return 0;
        }
        if (getClass() != otherCommand.getClass()) {
            return this.hashCode() - otherCommand.hashCode();
        }

        int result = compareKeywordList(otherCommand);
        if (result != 0) {
            return result;
        }

        if (!argumentList.equals(otherCommand.argumentList)) {
            return (argumentList.hashCode() - otherCommand.argumentList.hashCode());
        }

        if (systemCommand != otherCommand.systemCommand) {
            return (this.hashCode() - otherCommand.hashCode());
        }

        return apiMethod.compareTo(otherCommand.apiMethod);
    }

    /**
     * Compare the keyword lists of the commands.
     *
     * @param otherCommand the command to compare with
     * @return the int
     */
    private int compareKeywordList(final CommandLineCommand otherCommand) {
        for (int i = 0, j = 0;; i++, j++) {
            if (i < keywordlist.size() && j < otherCommand.keywordlist.size()) {
                if (!keywordlist.get(i).equals(otherCommand.keywordlist.get(j))) {
                    return keywordlist.get(i).compareTo(otherCommand.keywordlist.get(j));
                }
            } else if (i == keywordlist.size() && j == otherCommand.keywordlist.size()) {
                break;
            } else if (i == keywordlist.size()) {
                return -1;
            } else {
                return 1;
            }
        }

        return 0;
    }
}
