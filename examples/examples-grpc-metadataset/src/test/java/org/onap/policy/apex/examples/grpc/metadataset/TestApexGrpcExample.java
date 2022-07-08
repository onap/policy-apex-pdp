/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.apex.examples.grpc.metadataset;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.AfterClass;
import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;

/**
 * Class to test the generation of tosca policy with/without policy models, and metadataSet json file
 * in APEX CLI editor.
 */
public class TestApexGrpcExample {

    private static final String APEX_COMMAND_FILE = "src/main/resources/policy/APEXgRPCPolicy.apex";
    private static final String APEX_LOG_FILE = "target/test/APEXgRPCPolicyModel.log";
    private static final String APEX_CONFIG = "src/main/resources/examples/config/APEXgRPC/ApexConfig.json";
    private static final String TOSCA_TEMPLATE = "src/main/resources/tosca/ToscaTemplate.json";
    private static final String NODE_TEMPLATE = "src/main/resources/tosca/NodeTemplate.json";
    private static final String TOSCA_OUTPUT_FILE = "target/test/APEXgRPCPolicy.json";
    private static final String METADATASET_OUTPUT_FILE = "target/test/APEXgRPCMetadataSet.json";

    @Test
    public void testGenerateGrpcToscaPolicy() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c", APEX_COMMAND_FILE,
            "-l", APEX_LOG_FILE,
            "-ac", APEX_CONFIG,
            "-t", TOSCA_TEMPLATE,
            "-ot", TOSCA_OUTPUT_FILE
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        File generatedToscaPolicy = new File(TOSCA_OUTPUT_FILE);
        assertTrue(generatedToscaPolicy.exists());
        assertTrue(generatedToscaPolicy.length() > 0);
        assertTrue(Files.lines(Paths.get(generatedToscaPolicy.toString()))
                .anyMatch(l -> l.contains("policy_type_impl")));
    }

    @Test
    public void testGenerateGrpcToscaMetadataSet() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c", APEX_COMMAND_FILE,
            "-l", APEX_LOG_FILE,
            "-t", NODE_TEMPLATE,
            "-ot", METADATASET_OUTPUT_FILE
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        File generatedToscaMetadataSet = new File(METADATASET_OUTPUT_FILE);
        assertTrue(generatedToscaMetadataSet.exists());
        assertTrue(generatedToscaMetadataSet.length() > 0);
    }

    @Test
    public void testGenerateGrpcToscaWithoutModel() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-l", APEX_LOG_FILE,
            "-ac", APEX_CONFIG,
            "-t", TOSCA_TEMPLATE,
            "-ot", TOSCA_OUTPUT_FILE
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        File generatedToscaPolicy = new File(TOSCA_OUTPUT_FILE);
        assertTrue(generatedToscaPolicy.exists());
        assertTrue(generatedToscaPolicy.length() > 0);
        assertTrue(Files.lines(Paths.get(generatedToscaPolicy.toString()))
                .noneMatch(l -> l.contains("policy_type_impl")));

    }

    /**
     * Clean up the target folder with generated test files.
     * @throws IOException throws IO Exception in case of error
     */
    @AfterClass
    public static void cleanUp() throws IOException {
        Files.walk(Path.of("target/test/"))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

}