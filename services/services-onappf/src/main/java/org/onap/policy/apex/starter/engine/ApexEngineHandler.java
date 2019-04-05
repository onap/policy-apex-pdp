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

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
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
     * Constructs the object. Extracts the apex config and model files and instantiates the apex engine.
     *
     * @param properties
     * @return ApexEngineHandler
     * @throws ApexStarterException
     */

    public ApexEngineHandler(final String properties) throws ApexStarterException {
        final StandardCoder standardCoder = new StandardCoder();
        JsonObject body;
        try {
            body = standardCoder.decode(new StringReader(properties), JsonObject.class);
        } catch (final CoderException e) {
            throw new ApexStarterException(e);
        }
        final JsonObject engineServiceParameters = body.get("engineServiceParameters").getAsJsonObject();
        final String policyModel = engineServiceParameters.get("policy_type_impl").toString();
        engineServiceParameters.remove("policy_type_impl");
        final String apexConfig = body.toString();

        final String modelFilePath = createFile(policyModel, "modelFile");

        final String apexConfigFilePath = createFile(apexConfig, "apexConfigFile");

        final String[] apexArgs = { "-rfr", "target/classes", "-c", apexConfigFilePath, "-m", modelFilePath };
        apexMain = new ApexMain(apexArgs);
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
            Files.write(path, fileContent.getBytes(StandardCharsets.UTF_8));
            return path.toAbsolutePath().toString();
        } catch (final IOException e) {
            final String errorMessage = "error creating  from the properties received in PdpUpdate.";
            LOGGER.error(errorMessage, e);
            throw new ApexStarterException(errorMessage, e);
        }
    }

    public boolean isApexEngineRunning() {
        return null != apexMain;
    }

    public void shutdown() throws ApexStarterException {
        try {
            apexMain.shutdown();
            apexMain = null;
        } catch (final ApexException e) {
            throw new ApexStarterException(e);
        }
    }
}
