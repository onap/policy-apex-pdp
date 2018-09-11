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

package org.onap.policy.apex.auth.clieditor;

import java.util.ArrayList;
import java.util.List;

import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class represents a single Apex CLI command that is issued to the Apex Editor Java API
 * {@link org.onap.policy.apex.model.modelapi.ApexEditorApi}.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CLICommand implements Comparable<CLICommand> {
    private String name = "";
    private final List<String> keywordlist = new ArrayList<>();
    private final List<CLIArgument> argumentList = new ArrayList<>();
    private String apiMethod = "";
    private boolean systemCommand = false;
    private String description = "";

    /**
     * Gets the class name of the class that executes this command in the Java API.
     *
     * @return the class name of the class that executes this command in the Java API
     */
    public String getAPIClassName() {
        final int lastDotPos = apiMethod.lastIndexOf('.');
        if (lastDotPos == -1) {
            throw new CLIException("invalid API method name specified on command \"" + name
                    + "\", class name not found: " + apiMethod);
        }
        return apiMethod.substring(0, lastDotPos);
    }

    /**
     * Gets the method name of the method that executes this command in the Java API.
     *
     * @return the the method name of the method that executes this command in the Java API
     */
    public String getAPIMethodName() {
        final int lastDotPos = apiMethod.lastIndexOf('.');
        if (lastDotPos == -1) {
            throw new CLIException("invalid API method name specified on command \"" + name
                    + "\", class name not found: " + apiMethod);
        }
        if (lastDotPos == apiMethod.length() - 1) {
            throw new CLIException("no API method name specified on command \"" + name + "\": " + apiMethod);
        }
        return apiMethod.substring(lastDotPos + 1);
    }

    /**
     * Gets the name of the editor command.
     *
     * @return the name of the editor command
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the editor command.
     *
     * @param name the name of the editor command
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the list of keywords for this command.
     *
     * @return the list of keywords for this command
     */
    public List<String> getKeywordlist() {
        return keywordlist;
    }

    /**
     * Gets the list of arguments for this command.
     *
     * @return the list of arguments for this command
     */
    public List<CLIArgument> getArgumentList() {
        return argumentList;
    }

    /**
     * Gets the method of the method that executes this command in the Java API.
     *
     * @return the method of the method that executes this command in the Java API
     */
    public String getApiMethod() {
        return apiMethod;
    }

    /**
     * Sets the method of the method that executes this command in the Java API.
     *
     * @param apiMethod the method of the method that executes this command in the Java API
     */
    public void setApiMethod(final String apiMethod) {
        this.apiMethod = apiMethod;
    }

    /**
     * Gets the description of the command.
     *
     * @return the description of the command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the command.
     *
     * @param description the description of the command
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Checks if this command is a system command.
     *
     * @return true, if this command is a system command
     */
    public boolean isSystemCommand() {
        return systemCommand;
    }

    /**
     * Sets whether this command is a system command.
     *
     * @param systemCommand whether this command is a system command
     */
    public void setSystemCommand(final boolean systemCommand) {
        this.systemCommand = systemCommand;
    }

    /**
     * Gets help for this command.
     *
     * @return the help for this command
     */
    public String getHelp() {
        final StringBuilder builder = new StringBuilder();
        for (final String keyword : keywordlist) {
            builder.append(keyword);
            builder.append(' ');
        }
        builder.append('{');
        builder.append(name);
        builder.append("}: ");
        builder.append(description);

        for (final CLIArgument argument : argumentList) {
            if (argument == null) {
                continue;
            }
            builder.append("\n\t");
            builder.append(argument.getHelp());
        }
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CLICommand [name=" + name + ",keywordlist=" + keywordlist + ", argumentList=" + argumentList
                + ", apiMethod=" + apiMethod + ", systemCommand=" + systemCommand + ", description=" + description
                + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final CLICommand otherCommand) {
        Assertions.argumentNotNull(otherCommand, "comparison object may not be null");

        if (this == otherCommand) {
            return 0;
        }
        if (getClass() != otherCommand.getClass()) {
            return this.hashCode() - otherCommand.hashCode();
        }

        final CLICommand other = otherCommand;

        for (int i = 0, j = 0;; i++, j++) {
            if (i < keywordlist.size() && j < otherCommand.keywordlist.size()) {
                if (!keywordlist.get(i).equals(other.keywordlist.get(j))) {
                    return keywordlist.get(i).compareTo(other.keywordlist.get(j));
                }
            } else if (i == keywordlist.size() && j == otherCommand.keywordlist.size()) {
                break;
            } else if (i == keywordlist.size()) {
                return -1;
            } else {
                return 1;
            }
        }
        if (!argumentList.equals(other.argumentList)) {
            return (argumentList.hashCode() - other.argumentList.hashCode());
        }
        if (systemCommand != other.systemCommand) {
            return (this.hashCode() - other.hashCode());
        }
        return apiMethod.compareTo(other.apiMethod);
    }
}
