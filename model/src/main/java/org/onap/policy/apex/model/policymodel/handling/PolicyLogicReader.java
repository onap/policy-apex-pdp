/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.policymodel.handling;

import static org.onap.policy.apex.model.basicmodel.concepts.AxConcept.WHITESPACE_REGEX;

import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.policymodel.concepts.AxLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxLogicReader;
import org.onap.policy.apex.model.policymodel.concepts.PolicyRuntimeException;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to read Task Logic and Task Selection Logic from files into a string. A
 * {@link PolicyLogicReader} can then be used to provide the logic on a {@link AxLogic} class
 * constructor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyLogicReader implements AxLogicReader {
    private static final String DOT_JAVA = ".java.";

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyModelSplitter.class);

    // The path of the logic package
    private String logicPackage = "";

    // Flag indicating if default logic should be returned
    private String defaultLogic;

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getLogicPackage() {
        return logicPackage;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxLogicReader setLogicPackage(final String incomingLogicPackage) {
        this.logicPackage = incomingLogicPackage;
        return this;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getDefaultLogic() {
        return defaultLogic;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxLogicReader setDefaultLogic(final String incomingDefaultLogic) {
        this.defaultLogic = incomingDefaultLogic;
        return this;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String readLogic(final AxLogic axLogic) {
        // Java uses compiled logic, other executor types run scripts
        if ("JAVA".equals(axLogic.getLogicFlavour())) {
            // Check if we're using the default logic
            if (defaultLogic != null) {
                // Return the java class name for the default logic
                return logicPackage + DOT_JAVA + defaultLogic;
            } else {
                // Return the java class name for the logic
                if (axLogic.getKey().getParentLocalName().equals(AxKey.NULL_KEY_NAME)) {
                    return logicPackage + DOT_JAVA + axLogic.getKey().getParentKeyName()
                        + axLogic.getKey().getLocalName();
                } else {
                    return logicPackage + DOT_JAVA + axLogic.getKey().getParentKeyName()
                        + axLogic.getKey().getParentLocalName() + axLogic.getKey().getLocalName();
                }
            }
        }
        // Now, we read in the script

        // Get the package name of the current package and convert dots to slashes for the file path
        String fullLogicFilePath = getFullLogicFilePath(axLogic);

        final String logicString = ResourceUtils.getResourceAsString(fullLogicFilePath);

        // Check if the logic was found
        if (logicString == null || logicString.isEmpty()) {
            String errorMessage = "logic not found for logic \"" + fullLogicFilePath + "\"";
            LOGGER.warn(errorMessage);
            throw new PolicyRuntimeException(errorMessage);
        }

        // Return the right trimmed logic string
        return logicString.replaceAll(WHITESPACE_REGEX, "");
    }

    private String getFullLogicFilePath(AxLogic axLogic) {
        String fullLogicFilePath = logicPackage.replace(".", "/");

        // Now, the logic should be in a subdirectory for the logic executor type
        fullLogicFilePath += "/" + axLogic.getLogicFlavour().toLowerCase();

        // Check if we're using the default logic
        if (defaultLogic != null) {
            // Default logic
            fullLogicFilePath += "/" + defaultLogic;
        } else {
            if (axLogic.getKey().getParentLocalName().equals(AxKey.NULL_KEY_NAME)) {
                fullLogicFilePath += "/" + axLogic.getKey().getParentKeyName() + axLogic.getKey().getLocalName();
            } else {
                fullLogicFilePath += "/" + axLogic.getKey().getParentKeyName()
                    + axLogic.getKey().getParentLocalName() + axLogic.getKey().getLocalName();
            }
        }

        // Now get the type of executor to find the extension of the file
        fullLogicFilePath += "." + axLogic.getLogicFlavour().toLowerCase();
        return fullLogicFilePath;
    }
}
