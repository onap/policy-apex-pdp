/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.handler;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates the Apex Engine based on instruction from PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexEngineHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexEngineHandler.class);

    private ApexMain apexMain;

    /**
     * Constructs the object. Extracts the config and model files from each policy and instantiates the apex engine.
     *
     * @param policies the list of policies
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public ApexEngineHandler(List<ToscaPolicy> policies)  throws ApexStarterException {
        Map<ToscaPolicyIdentifier, String[]> policyArgsMap = createPolicyArgsMap(policies);
        LOGGER.debug("Starting apex engine.");
        try {
            apexMain = new ApexMain(policyArgsMap);
        } catch (ApexException e) {
            throw new ApexStarterException(e);
        }
    }

    /**
     * Updates the Apex Engine with the policy model created from new list of policies.
     *
     * @param policies the list of policies
     * @throws ApexStarterException if the apex engine instantiation failed using the policies passed
     */
    public void updateApexEngine(List<ToscaPolicy> policies) throws ApexStarterException {
        if (null == apexMain || !apexMain.isAlive()) {
            throw new ApexStarterException("Apex Engine not initialized.");
        }
        Map<ToscaPolicyIdentifier, String[]> policyArgsMap = createPolicyArgsMap(policies);
        try {
            apexMain.updateModel(policyArgsMap);
        } catch (ApexException e) {
            throw new ApexStarterException(e);
        }
    }

    private Map<ToscaPolicyIdentifier, String[]> createPolicyArgsMap(List<ToscaPolicy> policies)
        throws ApexStarterException {
        Map<ToscaPolicyIdentifier, String[]> policyArgsMap = new LinkedHashMap<>();
        for (ToscaPolicy policy : policies) {
            Object properties = policy.getProperties().get("content");
            final StandardCoder standardCoder = new StandardCoder();
            String policyModel;
            String apexConfig;
            try {
                JsonObject body = standardCoder.decode(standardCoder.encode(properties), JsonObject.class);
                final JsonObject engineServiceParameters = body.get("engineServiceParameters").getAsJsonObject();
                policyModel = standardCoder.encode(engineServiceParameters.get("policy_type_impl"));
                engineServiceParameters.remove("policy_type_impl");
                apexConfig = standardCoder.encode(body);
            } catch (final CoderException e) {
                throw new ApexStarterException(e);
            }

            final String modelFilePath = createFile(policyModel, "modelFile");

            final String apexConfigFilePath = createFile(apexConfig, "apexConfigFile");
            final String[] apexArgs = { "-c", apexConfigFilePath, "-m", modelFilePath };
            policyArgsMap.put(policy.getIdentifier(), apexArgs);
        }
        return policyArgsMap;
    }

    /**
     * Method to create the policy model file.
     *
     * @param fileContent the content of the file
     * @param fileName the name of the file
     * @throws ApexStarterException if the file creation failed
     */
    private String createFile(final String fileContent, final String fileName) throws ApexStarterException {
        try {
            final Path path = Files.createTempFile(fileName, ".json");
            Files.write(path, fileContent.getBytes(StandardCharsets.UTF_8));
            return path.toAbsolutePath().toString();
        } catch (final IOException e) {
            final String errorMessage = "error creating  from the properties received in PdpUpdate.";
            LOGGER.error(errorMessage, e);
            throw new ApexStarterException(errorMessage, e);
        }
    }

    /**
     * Method to check whether the apex engine is running or not.
     */
    public boolean isApexEngineRunning() {
        return null != apexMain && apexMain.isAlive();
    }

    /**
     * Method that return the list of running policies in the apex engine.
     */
    public List<ToscaPolicyIdentifier> getRunningPolicies() {
        return new ArrayList<>(apexMain.getApexParametersMap().keySet());
    }

    /**
     * Method to shut down the apex engine.
     */
    public void shutdown() throws ApexStarterException {
        try {
            LOGGER.debug("Shutting down apex engine.");
            apexMain.shutdown();
            apexMain = null;
        } catch (final ApexException e) {
            throw new ApexStarterException(e);
        }
    }
}
