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

package org.onap.policy.apex.service.parameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParametersJsonAdapter;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParametersJsonAdapter;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParametersJsonAdapter;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterException;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles reading, parsing and validating of Apex parameters from JSON files.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexParameterHandler {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexParameterHandler.class);

    /**
     * Read the parameters from the parameter file.
     *
     * @param arguments the arguments passed to Apex
     * @return the parameters read from the configuration file
     * @throws ParameterException on parameter exceptions
     */
    public ApexParameters getParameters(final ApexCommandLineArguments arguments) throws ParameterException {
        // Clear all existing parameters
        ParameterService.clear();

        ApexParameters parameters = null;

        // Read the parameters
        try {
            // Register the adapters for our carrier technologies and event protocols with GSON
            // @formatter:off
            final Gson gson = new GsonBuilder()
                            .registerTypeAdapter(EngineParameters           .class, 
                                            new EngineServiceParametersJsonAdapter())
                            .registerTypeAdapter(CarrierTechnologyParameters.class, 
                                            new CarrierTechnologyParametersJsonAdapter())
                            .registerTypeAdapter(EventProtocolParameters    .class, 
                                            new EventProtocolParametersJsonAdapter())
                            .create();
            // @formatter:on
            parameters = gson.fromJson(new FileReader(arguments.getFullConfigurationFilePath()), ApexParameters.class);
        } catch (final Exception e) {
            final String errorMessage = "error reading parameters from \"" + arguments.getConfigurationFilePath()
                            + "\"\n" + "(" + e.getClass().getSimpleName() + "):" + e.getMessage();
            LOGGER.error(errorMessage, e);
            throw new ParameterException(errorMessage, e);
        }

        // The JSON processing returns null if there is an empty file
        if (parameters == null) {
            final String errorMessage = "no parameters found in \"" + arguments.getConfigurationFilePath() + "\"";
            LOGGER.error(errorMessage);
            throw new ParameterException(errorMessage);
        }

        // Check if we should override the model file parameter
        final String modelFilePath = arguments.getModelFilePath();
        if (modelFilePath != null && modelFilePath.replaceAll("\\s+", "").length() > 0) {
            parameters.getEngineServiceParameters().setPolicyModelFileName(modelFilePath);
        }

        // Validate the parameters
        final GroupValidationResult validationResult = parameters.validate();
        if (!validationResult.isValid()) {
            String returnMessage = "validation error(s) on parameters from \"" + arguments.getConfigurationFilePath()
                            + "\"\n";
            returnMessage += validationResult.getResult();

            LOGGER.error(returnMessage);
            throw new ParameterException(returnMessage);
        }

        if (!validationResult.isClean()) {
            String returnMessage = "validation messages(s) on parameters from \"" + arguments.getConfigurationFilePath()
                            + "\"\n";
            returnMessage += validationResult.getResult();

            LOGGER.info(returnMessage);
        }

        // Register the parameters with the parameter service
        registerParameters(parameters);

        return parameters;
    }

    /**
     * Register all the incoming parameters with the parameter service.
     * 
     * @param parameters The parameters to register
     */
    private void registerParameters(ApexParameters parameters) {
        ParameterService.register(parameters);
        ParameterService.register(parameters.getEngineServiceParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters().getContextParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                        .getSchemaParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                        .getDistributorParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                        .getLockManagerParameters());
        ParameterService.register(parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                        .getPersistorParameters());
    }
}
