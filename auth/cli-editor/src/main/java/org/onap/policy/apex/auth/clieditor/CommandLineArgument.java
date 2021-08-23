/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * This class holds the definition of an argument of a CLI command.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@ToString
@EqualsAndHashCode
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
     * Gets the argument help.
     *
     * @return the argument help
     */
    public String getHelp() {
        final var builder = new StringBuilder();
        builder.append(argumentName);
        builder.append(nullable ? ": (O) " : ": (M) ");
        builder.append(description);
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(@NonNull final CommandLineArgument otherArgument) {
        if (this == otherArgument) {
            return 0;
        }
        return new CompareToBuilder()
                        .append(argumentName, otherArgument.argumentName)
                        .append(nullable, otherArgument.nullable)
                        .append(description, otherArgument.description)
                        .append(getClass(), otherArgument.getClass())
                        .toComparison();
    }
}
