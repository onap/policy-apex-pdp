/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2022 Nordix Foundation.
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

package org.onap.policy.apex.auth.clieditor.utils;

import com.google.gson.JsonObject;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.auth.clieditor.CommandLineException;
import org.onap.policy.apex.auth.clieditor.CommandLineParameters;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaParameters;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the utility methods specifically for Apex CLI Editor.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CliUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliUtils.class);

    // Recurring string constants
    private static final String OF_TYPE_TAG = " of type ";
    private static final int MAX_HELP_LINE_LENGTH = 120;

    /**
     * Method to create apex policy in TOSCA service template.
     *
     * @param parameters containing paths to the apex config and tosca template skeleton file
     * @param policyModelFilePath path of apex policy model
     * @param nodeType node type name if node template is generated, null by default
     */
    public static void createToscaPolicy(ApexCliToscaParameters parameters, String policyModelFilePath, String nodeType)
            throws IOException, CoderException {
        final var standardCoder = new StandardCoder();
        var apexConfigJson = getJsonObject(parameters.getApexConfigFileName());
        var policyModelJson = getJsonObject(policyModelFilePath);
        var toscaTemplateJson = getJsonObject(parameters.getInputToscaTemplateFileName());

        var toscaPolicyProperties = toscaTemplateJson.get("topology_template").getAsJsonObject();
        var toscaPolicy = toscaPolicyProperties.get("policies").getAsJsonArray().get(0).getAsJsonObject();
        var toscaProperties = toscaPolicy.get(toscaPolicy.keySet().toArray()[0].toString()).getAsJsonObject()
                .get("properties").getAsJsonObject();

        // Populate metadataSet reference in Tosca policy and create node template file if nodeType is provided
        if (nodeType != null) {
            var metadataSetName = toscaPolicy.get(toscaPolicy.keySet().toArray()[0].toString()).getAsJsonObject()
                    .get("name").getAsString() + ".metadataSet";
            var metadataSetVersion = toscaPolicy.get(toscaPolicy.keySet().toArray()[0].toString())
                    .getAsJsonObject().get("version").getAsString();
            JsonObject metadata = new JsonObject();
            metadata.addProperty("metadataSetName", metadataSetName);
            metadata.addProperty("metadataSetVersion", metadataSetVersion);
            toscaPolicy.add("metadata", metadata);

            createToscaNodeTemplate(parameters, policyModelJson, nodeType, metadataSetName, metadataSetVersion);
        }
        apexConfigJson.entrySet().forEach(entry -> {
            if ("engineServiceParameters".equals(entry.getKey()) && nodeType == null) {
                entry.getValue().getAsJsonObject().add("policy_type_impl", policyModelJson);
            }
            toscaProperties.add(entry.getKey(), entry.getValue());
        });
        final var toscaPolicyString = standardCoder.encode(toscaTemplateJson);
        final String toscaPolicyFileName = parameters.getOutputToscaPolicyFileName();
        if (StringUtils.isNotBlank(toscaPolicyFileName)) {
            TextFileUtils.putStringAsTextFile(toscaPolicyString, toscaPolicyFileName);
        } else {
            LOGGER.debug("Output file name not specified. Resulting tosca policy is {}", toscaPolicyString);
        }
    }

    /**
     * Method to create tosca node template with metadataSet.
     *
     * @param parameters containing tosca node template skeleton file
     * @param policyModelJson path of apex policy model
     * @param nodeType node type name for the node template
     * @param metadataSetName name of the node template
     * @param metadataSetVersion version of the node template
     */
    public static void createToscaNodeTemplate(ApexCliToscaParameters parameters, JsonObject policyModelJson,
                                               String nodeType, String metadataSetName, String metadataSetVersion)
            throws IOException, CoderException {
        final var standardCoder = new StandardCoder();

        JsonObject nodeTemplateFile = new JsonObject();
        nodeTemplateFile.addProperty("tosca_definitions_version", "tosca_simple_yaml_1_1_0");

        JsonObject metadata = new JsonObject();
        metadata.add("policyModel", policyModelJson);

        JsonObject nodeTemplate = new JsonObject();
        nodeTemplate.addProperty("type", nodeType);
        nodeTemplate.addProperty("type_version", "1.0.0");
        nodeTemplate.addProperty("version", metadataSetVersion);
        nodeTemplate.addProperty("description", "MetadataSet for policy containing policy model");
        nodeTemplate.add("metadata", metadata);

        JsonObject metadataSet = new JsonObject();
        metadataSet.add(metadataSetName, nodeTemplate);

        JsonObject nodeTemplates = new JsonObject();
        nodeTemplates.add("node_templates", metadataSet);

        nodeTemplateFile.add("topology_template", nodeTemplates);

        final var toscaNodeTemplateString = standardCoder.encode(nodeTemplateFile);
        final String toscaNodeTemplateFileName = parameters.getOutputNodeTemplateFileName();
        if (StringUtils.isNotBlank(toscaNodeTemplateFileName)) {
            TextFileUtils.putStringAsTextFile(toscaNodeTemplateString, toscaNodeTemplateFileName);
        } else {
            LOGGER.debug("Output file name not specified. Resulting tosca node template: {}", toscaNodeTemplateString);
        }
    }

    /**
     * Validate that a file is readable.
     *
     * @param fileTag the file tag, a tag used for information and error messages
     * @param fileName the file name to check
     */
    public static void validateReadableFile(final String fileTag, final String fileName) {
        if (fileName == null) {
            return;
        }
        final var theFile = new File(fileName);
        final String prefixExceptionMessage = "File " + fileName + OF_TYPE_TAG + fileTag;

        if (!theFile.exists()) {
            throw new CommandLineException(prefixExceptionMessage + " does not exist");
        }
        if (!theFile.isFile()) {
            throw new CommandLineException(prefixExceptionMessage + " is not a normal file");
        }
        if (!theFile.canRead()) {
            throw new CommandLineException(prefixExceptionMessage + " is unreadable");
        }
    }

    /**
     * Validate that a file is writable.
     *
     * @param fileTag the file tag, a tag used for information and error messages
     * @param fileName the file name to check
     */
    public static void validateWritableFile(final String fileTag, final String fileName) {
        if (fileName == null) {
            return;
        }
        final var theFile = new File(fileName);
        final String prefixExceptionMessage = "File " + fileName + OF_TYPE_TAG + fileTag;
        if (theFile.exists()) {
            if (!theFile.isFile()) {
                throw new CommandLineException(prefixExceptionMessage + " is not a normal file");
            }
            if (!theFile.canWrite()) {
                throw new CommandLineException(prefixExceptionMessage + " cannot be written");
            }
        } else {
            try {
                if (!theFile.getParentFile().exists()) {
                    theFile.getParentFile().mkdirs();
                }
                if (theFile.createNewFile()) {
                    LOGGER.info("File {} does not exist. New file created.", fileName);
                }
            } catch (final IOException e) {
                throw new CommandLineException(prefixExceptionMessage + " cannot be created: ", e);
            }
        }
    }

    /**
     * Validate that a directory exists and is writable.
     *
     * @param directoryTag the directory tag, a tag used for information and error messages
     * @param directoryName the directory name to check
     */
    public static void validateWritableDirectory(final String directoryTag, final String directoryName) {
        if (directoryName == null) {
            return;
        }
        final var theDirectory = new File(directoryName);
        final String prefixExceptionMessage = "directory " + directoryName + OF_TYPE_TAG + directoryTag;

        if (theDirectory.exists()) {
            if (!theDirectory.isDirectory()) {
                throw new CommandLineException(prefixExceptionMessage + " is not a directory");
            }
            if (!theDirectory.canWrite()) {
                throw new CommandLineException(prefixExceptionMessage + " cannot be written");
            }
        } else {
            if (!theDirectory.mkdir()) {
                throw new CommandLineException(prefixExceptionMessage + " doesn't exist and cannot be created.");
            }
        }
    }

    /**
     * Print help information.
     *
     * @param mainClassName the main class name
     * @param options the options for cli editor
     */
    public static void help(final String mainClassName, Options options) {
        final var helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(MAX_HELP_LINE_LENGTH, mainClassName + " [options...]", "options", options, "");
    }

    /**
     * Method to generate arguments required for APEX CLI editor.
     *
     * @param parameters the command line parameters
     * @param optionVariableMap the properties object containing the option and corresponding variable name
     * @param class1 the class type in which the variable names has to be looked for
     * @return list of arguments
     */
    public static List<String> generateArgumentsForCliEditor(CommandLineParameters parameters,
            Properties optionVariableMap, Class<?> class1) {

        List<String> cliArgsList = new ArrayList<>();
        PropertyDescriptor pd;
        Method getter;
        Object argValue;

        for (Entry<Object, Object> entry : optionVariableMap.entrySet()) {
            try {
                pd = new PropertyDescriptor(entry.getValue().toString(), class1);
                getter = pd.getReadMethod();
                argValue = getter.invoke(parameters);
                var key = entry.getKey().toString();

                if (argValue instanceof String && !key.equals("o")) {
                    cliArgsList.add("-" + key);
                    cliArgsList.add(argValue.toString());
                } else if (argValue instanceof Boolean && (boolean) argValue) {
                    cliArgsList.add("-" + key);
                }
            } catch (Exception e) {
                LOGGER.error("Invalid getter method for the argument specfied.", e);
            }
        }
        return cliArgsList;
    }

    private static JsonObject getJsonObject(String filePath) throws IOException, CoderException {
        final var standardCoder = new StandardCoder();
        var contentString = TextFileUtils.getTextFileAsString(filePath);
        return standardCoder.decode(contentString, JsonObject.class);
    }
}
