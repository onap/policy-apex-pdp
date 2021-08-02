/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import lombok.Getter;
import lombok.ToString;

/**
 * This class represents an argument used on a command and its value.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@ToString
public class CommandLineArgumentValue {
    private final CommandLineArgument cliArgument;
    private boolean specified;
    private String value;

    /**
     * The Constructor creates an argument value for the given argument, has not been set, and has
     * no value.
     *
     * @param cliArgument the argument for which this object is a value
     */
    public CommandLineArgumentValue(final CommandLineArgument cliArgument) {
        this.cliArgument = cliArgument;
        specified = false;
        value = null;
    }

    /**
     * Sets the argument value.
     *
     * @param value the argument value
     */
    public void setValue(final String value) {
        this.value = value;
        specified = true;
    }
}
