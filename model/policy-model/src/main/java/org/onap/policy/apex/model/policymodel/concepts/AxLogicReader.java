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

package org.onap.policy.apex.model.policymodel.concepts;

/**
 * This interface is used to provide logic to a {@link AxLogic} instance. Implementations usually
 * store logic on disk in a structure similar to Java package naming conventions. The logic package
 * gives the package path, a directory where a set of logic is defined. Default logic is logic that
 * can be used as dummy logic in tasks or states that are filler tasks or states. The actual logic
 * is returned by the {@code readLogic()} method. The interface is used mainly by unit test classes
 * that generate test logic.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface AxLogicReader {

    /**
     * Get the logic package path.
     *
     * @return the logic package path
     */
    String getLogicPackage();

    /**
     * Set the logic package path.
     *
     * @param logicPackage the name of the package that contains the logic for this logic reader
     * @return the logic reader on which this method was called, used for daisy chaining
     *         configuration
     */
    AxLogicReader setLogicPackage(final String logicPackage);

    /**
     * Get the default logic name.
     *
     * @return the default logic name
     */
    String getDefaultLogic();

    /**
     * Set the default logic name.
     *
     * @param defaultLogic the default logic name
     * @return the logic reader on which this method was called, used for daisy chaining
     *         configuration
     */
    AxLogicReader setDefaultLogic(final String defaultLogic);

    /**
     * Read the logic for an AxLogic object.
     *
     * @param axLogic the AxLogic object
     * @return the logic as a string
     */
    String readLogic(final AxLogic axLogic);
}
