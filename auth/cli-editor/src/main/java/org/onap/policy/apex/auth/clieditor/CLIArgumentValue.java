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

/**
 * This class represents an argument used on a command and its value.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class CLIArgumentValue {
    private final CLIArgument cliArgument;
    private boolean specified;
    private String value;

    /**
     * The Constructor creates an argument value for the given argument, has not been set, and has
     * no value.
     *
     * @param cliArgument the argument for which this object is a value
     */
    public CLIArgumentValue(final CLIArgument cliArgument) {
        this.cliArgument = cliArgument;
        specified = false;
        value = null;
    }

    /**
     * Gets the argument for which this object is a value.
     *
     * @return the argument for which this object is a value
     */
    public CLIArgument getCliArgument() {
        return cliArgument;
    }

    /**
     * Checks if the argument value is specified.
     *
     * @return true, if the argument value is specified
     */
    public boolean isSpecified() {
        return specified;
    }

    /**
     * Gets the argument value.
     *
     * @return the argument value
     */
    public String getValue() {
        return value;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CLIArgumentValue [cliArgument=" + cliArgument + ", specified=" + specified + ", value=" + value + "]";
    }
}
