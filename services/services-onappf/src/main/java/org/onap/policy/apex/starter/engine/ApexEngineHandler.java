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

package org.onap.policy.apex.starter.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class instantiates the Apex Engine based on instruction from PAP.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexEngineHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexEngineHandler.class);

    /**
     * Extracts the apex configuration and policy model from the properties and instantiates apex engine.
     *
     * @param properties
     * @return ApexMain
     * @throws ApexStarterException
     */
    public ApexMain instantiateApexEngine(final String properties) throws ApexStarterException {

        final Gson gson = new Gson();
        final JsonObject body = gson.fromJson(new StringReader(properties), JsonObject.class);
        final JsonObject engineServiceParameters = body.get("engineServiceParameters").getAsJsonObject();
        final String policyModel = engineServiceParameters.get("policy_type_impl").toString();
        engineServiceParameters.remove("policy_type_impl");
        final String apexConfig = body.toString();

        final String modelFilePath = createFile(policyModel, "modelFile");

        final String apexConfigFilePath = createFile(apexConfig, "apexConfigFile");

        final String[] apexArgs = { "-rfr", "target/classes", "-c", apexConfigFilePath, "-m", modelFilePath };
        return new ApexMain(apexArgs);
    }

    /**
     * Method to create the policy model file
     *
     * @param policyModel
     * @param modelFilePath
     * @throws ApexStarterException
     */
    private String createFile(final String fileContent, final String fileName) throws ApexStarterException {
        try {
            final Path path = Files.createTempFile(fileName, ".json");
            Files.write(path, fileContent.getBytes());
            return path.toAbsolutePath().toString();
        } catch (final IOException e) {
            final String errorMessage = "error creating  from the properties received in PdpUpdate.";
            LOGGER.error(errorMessage, e);
            throw new ApexStarterException(errorMessage, e);
        }
    }

}
