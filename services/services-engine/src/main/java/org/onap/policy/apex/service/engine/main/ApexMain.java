/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modification Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.main;

import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class initiates Apex as a complete service from the command line.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexMain {
    private static final String APEX_SERVICE_FAILED_MSG = "start of Apex service failed";

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexMain.class);

    // The Apex Activator that activates the Apex engine
    private ApexActivator activator;

    // The parameters read in from JSON for each policy
    @Getter
    private Map<ToscaPolicyIdentifier, ApexParameters> apexParametersMap;

    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private volatile boolean alive = false;

    /**
     * Instantiates the Apex service.
     *
     * @param args the command line arguments
     */
    public ApexMain(final String[] args) {
        LOGGER.entry("Starting Apex service with parameters " + Arrays.toString(args) + " . . .");
        apexParametersMap = new LinkedHashMap<>();
        try {
            apexParametersMap.put(new ToscaPolicyIdentifier(), populateApexParameters(args));
        } catch (ApexException e) {
            LOGGER.error(APEX_SERVICE_FAILED_MSG, e);
            return;
        }

        // Now, create the activator for the Apex service
        activator = new ApexActivator(apexParametersMap);

        // Start the activator
        try {
            activator.initialize();
            setAlive(true);
        } catch (final ApexActivatorException e) {
            LOGGER.error("start of Apex service failed, used parameters are " + Arrays.toString(args), e);
            return;
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new ApexMainShutdownHookClass());
        LOGGER.exit("Started Apex");
    }

    /**
     * Instantiates the Apex service for multiple policies.
     *
     * @param policyArgumentsMap the map with command line arguments as value and policy-id as key
     * @throws ApexException on errors
     */
    public ApexMain(Map<ToscaPolicyIdentifier, String[]> policyArgumentsMap) throws ApexException {
        apexParametersMap = new LinkedHashMap<>();
        for ( Entry<ToscaPolicyIdentifier, String[]> policyArgsEntry: policyArgumentsMap.entrySet()) {
            try {
                apexParametersMap.put(policyArgsEntry.getKey(), populateApexParameters(policyArgsEntry.getValue()));
            } catch (ApexException e) {
                LOGGER.error("Invalid arguments specified for policy - " + policyArgsEntry.getKey().getName() + ":"
                    + policyArgsEntry.getKey().getVersion(), e);
            }
        }
        if (apexParametersMap.isEmpty()) {
            LOGGER.error(APEX_SERVICE_FAILED_MSG);
            return;
        }
        // Now, create the activator for the Apex service
        activator = new ApexActivator(apexParametersMap);

        // Start the activator
        try {
            activator.initialize();
            apexParametersMap = activator.getApexParametersMap();
            setAlive(true);
        } catch (final ApexActivatorException e) {
            LOGGER.error(APEX_SERVICE_FAILED_MSG, e);
            activator.terminate();
            return;
        }

        // Add a shutdown hook to shut everything down in an orderly manner
        Runtime.getRuntime().addShutdownHook(new ApexMainShutdownHookClass());
        LOGGER.exit("Started Apex");
    }

    private ApexParameters populateApexParameters(String[] args) throws ApexException {
        // Check the arguments
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        try {
            // The arguments return a string if there is a message to print and we should exit
            final String argumentMessage = arguments.parse(args);
            if (argumentMessage != null) {
                LOGGER.info(argumentMessage);
                throw new ApexException(argumentMessage);
            }

            // Validate that the arguments are sane
            arguments.validate();
        } catch (final ApexException e) {
            LOGGER.error("Arguments validation failed.", e);
            throw new ApexException("Arguments validation failed.", e);
        }

        ApexParameters axParameters;
        // Read the parameters
        try {
            ApexParameterHandler apexParameterHandler = new ApexParameterHandler();
            // In case of multiple policies received from PAP, do not clear ParameterService if parameters of one policy
            // already registered
            apexParameterHandler.setKeepParameterServiceFlag(null != apexParametersMap && !apexParametersMap.isEmpty());
            axParameters = apexParameterHandler.getParameters(arguments);
        } catch (final Exception e) {
            LOGGER.error("Cannot create APEX Parameters from the arguments provided.", e);
            throw new ApexException("Cannot create APEX Parameters from the arguments provided.", e);
        }

        // Set incoming Java properties
        setJavaProperties(axParameters);

        // Set the name of the event handler parameters for producers and consumers
        for (final Entry<String, EventHandlerParameters> ehParameterEntry : axParameters.getEventOutputParameters()
            .entrySet()) {
            if (!ehParameterEntry.getValue().checkSetName()) {
                ehParameterEntry.getValue().setName(ehParameterEntry.getKey());
            }
        }
        for (final Entry<String, EventHandlerParameters> ehParameterEntry : axParameters.getEventInputParameters()
            .entrySet()) {
            if (!ehParameterEntry.getValue().checkSetName()) {
                ehParameterEntry.getValue().setName(ehParameterEntry.getKey());
            }
        }
        return axParameters;
    }

    /**
     * Shut down Execution.
     *
     * @throws ApexException on shutdown errors
     */
    public void shutdown() throws ApexException {
        if (activator != null) {
            activator.terminate();
        }
        setAlive(false);
    }

    /**
     * The Class ApexMainShutdownHookClass terminates the Apex engine for the Apex service when its run method is
     * called.
     */
    private class ApexMainShutdownHookClass extends Thread {
        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            try {
                // Shutdown the Apex engine and wait for everything to stop
                activator.terminate();
                setAlive(false);
            } catch (final ApexException e) {
                LOGGER.warn("error occured during shut down of the Apex service", e);
            }
        }
    }

    /**
     * Set the Java properties specified in the parameters.
     *
     * @param parameters The incoming parameters
     */
    private void setJavaProperties(final ApexParameters parameters) {
        if (!parameters.checkJavaPropertiesSet()) {
            return;
        }

        // Set each Java property
        for (String[] javaProperty : parameters.getJavaProperties()) {
            String javaPropertyName = javaProperty[0];
            String javaPropertyValue = javaProperty[1];

            // Passwords are encoded using base64, better than sending passwords in the clear
            if (javaPropertyName.toLowerCase().contains("password")) {
                javaPropertyValue = new String(Base64.getDecoder().decode(javaPropertyValue.getBytes()));
            }

            // Set the Java property
            System.setProperty(javaPropertyName, javaPropertyValue);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        new ApexMain(args);
    }
}
