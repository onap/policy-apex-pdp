/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2022 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.parameters;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParametersJsonAdapter;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParametersJsonAdapter;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParametersJsonAdapter;
import org.onap.policy.common.parameters.ParameterException;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles reading, parsing and validating of Apex parameters from JSON files.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexParameterHandler {
    private static final String EVENT_OUTPUT_PARAMETERS = "eventOutputParameters";

    private static final String EVENT_INPUT_PARAMETERS = "eventInputParameters";

    private static final String ENGINE_SERVICE_PARAMETERS = "engineServiceParameters";

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexParameterHandler.class);

    private static final String POLICY_TYPE_IMPL = "policy_type_impl";
    private static final String APEX_POLICY_MODEL = "apexPolicyModel";

    private String policyModel;
    private String apexConfig;

    /**
     * Read the parameters from the parameter file.
     *
     * @param arguments the arguments passed to Apex
     * @return the parameters read from the configuration file
     * @throws ParameterException on parameter exceptions
     */
    public ApexParameters getParameters(final ApexCommandLineArguments arguments) throws ParameterException {

        ApexParameters parameters = null;
        String toscaPolicyFilePath = arguments.getToscaPolicyFilePath();
        // Read the parameters
        try {
            parseConfigAndModel(toscaPolicyFilePath);
            // Register the adapters for our carrier technologies and event protocols with GSON
            // @formatter:off
            final var gson = new GsonBuilder()
                .registerTypeAdapter(EngineParameters.class,
                    new EngineServiceParametersJsonAdapter())
                .registerTypeAdapter(CarrierTechnologyParameters.class,
                    new CarrierTechnologyParametersJsonAdapter())
                .registerTypeAdapter(EventProtocolParameters.class,
                    new EventProtocolParametersJsonAdapter())
                .create();
            // @formatter:on
            parameters = gson.fromJson(apexConfig, ApexParameters.class);
        } catch (final Exception e) {
            final String errorMessage = "error reading parameters from \"" + toscaPolicyFilePath + "\"\n" + "("
                + e.getClass().getSimpleName() + "):" + e.getMessage();
            throw new ParameterException(errorMessage, e);
        }

        // The JSON processing returns null if there is an empty file
        if (parameters == null) {
            final String errorMessage = "no parameters found in \"" + toscaPolicyFilePath + "\"";
            throw new ParameterException(errorMessage);
        }

        if (null != parameters.getEngineServiceParameters()) {
            parameters.getEngineServiceParameters().setPolicyModel(policyModel);
        }

        // Validate the parameters
        final ValidationResult validationResult = parameters.validate();
        if (!validationResult.isValid()) {
            String returnMessage = "validation error(s) on parameters from \"" + toscaPolicyFilePath + "\"\n";
            returnMessage += validationResult.getResult();
            throw new ParameterException(returnMessage);
        }

        if (!validationResult.isClean()) {
            String returnMessage = "validation messages(s) on parameters from \"" + toscaPolicyFilePath + "\"\n";
            returnMessage += validationResult.getResult();

            LOGGER.info(returnMessage);
        }

        return parameters;
    }

    /**
     * Register all the incoming parameters with the parameter service.
     *
     * @param parameters The parameters to register
     */
    public void registerParameters(ApexParameters parameters) {
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

    private void parseConfigAndModel(final String toscaPolicyFilePath) throws ApexException {
        policyModel = null;
        apexConfig = null;
        final var standardCoder = new StandardCoder();
        var apexConfigJsonObject = new JsonObject();
        try {
            var toscaServiceTemplate = standardCoder
                .decode(Files.readString(Paths.get(toscaPolicyFilePath)), ToscaServiceTemplate.class);
            for (Entry<String, Object> property : toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0)
                .entrySet().iterator().next().getValue().getProperties().entrySet()) {
                JsonElement body = null;
                if ("javaProperties".equals(property.getKey())) {
                    body = standardCoder.convert(property.getValue(), JsonArray.class);
                } else if (EVENT_INPUT_PARAMETERS.equals(property.getKey())
                    || ENGINE_SERVICE_PARAMETERS.equals(property.getKey())
                    || EVENT_OUTPUT_PARAMETERS.equals(property.getKey())) {
                    body = standardCoder.convert(property.getValue(), JsonObject.class);
                    if (ENGINE_SERVICE_PARAMETERS.equals(property.getKey())) {
                        policyModel = extractPolicyModel(standardCoder, body);
                    }
                }
                apexConfigJsonObject.add(property.getKey(), body);
            }
            apexConfig = standardCoder.encode(apexConfigJsonObject);
        } catch (Exception e) {
            throw new ApexException("Parsing config and model from the tosca policy failed.", e);
        }
    }

    private String extractPolicyModel(StandardCoder standardCoder, JsonElement body) throws CoderException {
        // Check for "policy_type_impl"
        JsonElement policyTypeImplObject = ((JsonObject) body).get(POLICY_TYPE_IMPL);
        if (null == policyTypeImplObject) {
            return null;
        }

        // "policy_type_impl" found
        if (policyTypeImplObject instanceof JsonObject) {

            // Check for "apexPolicyModel", this is used to encapsulate policy models sometimes
            JsonElement policyModelObject = ((JsonObject) policyTypeImplObject).get(APEX_POLICY_MODEL);

            if (policyModelObject != null) {
                // Policy model encased in an "apexPolicyModel" object
                return standardCoder.encode(policyModelObject);
            } else {
                // No encasement
                return standardCoder.encode(policyTypeImplObject);
            }
        } else {
            return policyTypeImplObject.getAsString();
        }
    }
}
