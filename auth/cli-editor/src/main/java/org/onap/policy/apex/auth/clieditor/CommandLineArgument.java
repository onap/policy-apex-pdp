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

import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class holds the definition of an argument of a CLI command.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CommandLineArgument implements Comparable<CommandLineArgument> {
    private final String argumentName;
    private final boolean nullable;
    private final String description;

    /**
     * This Constructor constructs a non nullable command line argument with a blank name and
     * description.
     */
    public CommandLineArgument() {
        this("", false, "");
    }

    /**
     * This Constructor constructs a non nullable command line argument with the given name and
     * description.
     *
     * @param incomingArgumentName the argument name
     */
    public CommandLineArgument(final String incomingArgumentName) {
        this(incomingArgumentName, false, "");
    }

    /**
     * This Constructor constructs a command line argument with the given name, nullability, and
     * description.
     *
     * @param argumentName the argument name
     * @param nullable the nullable
     * @param description the description
     */
    public CommandLineArgument(final String argumentName, final boolean nullable, final String description) {
        this.argumentName = argumentName;
        this.nullable = nullable;
        this.description = description;
    }

    /**
     * Gets the argument name.
     *
     * @return the argument name
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * Checks if the argument is nullable.
     *
     * @return true, if checks if the argument is nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Gets the argument description.
     *
     * @return the argument description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the argument help.
     *
     * @return the argument help
     */
    public String getHelp() {
        final StringBuilder builder = new StringBuilder();
        builder.append(argumentName);
        builder.append(nullable ? ": (O) " : ": (M) ");
        builder.append(description);
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CLIArgument [argumentName=" + argumentName + ", nullable=" + nullable + ", description=" + description
                + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final CommandLineArgument otherArgument) {
        Assertions.argumentNotNull(otherArgument, "comparison object may not be null");

        if (this == otherArgument) {
            return 0;
        }
        if (getClass() != otherArgument.getClass()) {
            return this.hashCode() - otherArgument.hashCode();
        }

        final CommandLineArgument other = otherArgument;

        if (!argumentName.equals(other.argumentName)) {
            return argumentName.compareTo(other.argumentName);
        }
        if (nullable != other.nullable) {
            return (this.hashCode() - other.hashCode());
        }
        return description.compareTo(otherArgument.description);
    }
}
