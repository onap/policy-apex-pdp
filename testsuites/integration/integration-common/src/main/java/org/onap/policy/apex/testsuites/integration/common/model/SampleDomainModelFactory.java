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

package org.onap.policy.apex.testsuites.integration.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxLogicReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.apex.model.policymodel.handling.PolicyLogicReader;

/**
 * This class creates sample Policy Models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SampleDomainModelFactory {
    // Recurring string constants
    private static final String TASK = "Task_";
    private static final String PARAMETER2 = "Parameter2";
    private static final String PARAMETER1 = "Parameter1";
    private static final String DEFAULT_VALUE2 = "DefaultValue2";
    private static final String DEFAULT_VALUE1 = "DefaultValue1";
    private static final String PARAMETER0 = "Parameter0";
    private static final String DEFAULT_VALUE0 = "DefaultValue0";
    private static final String TASK_SELECTION_LIGIC = "TaskSelectionLigic";
    private static final String TEST_ACT_STATE_TIME = "TestActStateTime";
    private static final String TEST_ACT_CASE_SELECTED = "TestActCaseSelected";
    private static final String TEST_DECIDE_STATE_TIME = "TestDecideStateTime";
    private static final String TEST_DECIDE_CASE_SELECTED = "TestDecideCaseSelected";
    private static final String TEST_ESTABLISH_STATE_TIME = "TestEstablishStateTime";
    private static final String TEST_ESTABLISH_CASE_SELECTED = "TestEstablishCaseSelected";
    private static final String DECIDE = "Decide";
    private static final String TEST_MATCH_STATE_TIME = "TestMatchStateTime";
    private static final String TEST_MATCH_CASE_SELECTED = "TestMatchCaseSelected";
    private static final String ESTABLISH = "Establish";
    private static final String TEST_MATCH_CASE = "TestMatchCase";
    private static final String MATCH = "Match";
    private static final String DEFAULT_SOURCE = "Outside";
    private static final String DEFAULT_NAMESPACE = "org.onap.policy.apex.sample.events";
    private static final String TEST_TEMPERATURE = "TestTemperature";
    private static final String TEST_TIMESTAMP = "TestTimestamp";
    private static final String TEST_SLOGAN = "TestSlogan";
    private static final String DEFAULT_VERSION = "0.0.1";

    private static final int THIRD_ENTRY = 3;

    /**
     * Get a sample policy model.
     *
     * @param axLogicExecutorType The type of logic executor, the scripting language being used
     * @return the sample policy model
     */
    // CHECKSTYLE:OFF: checkstyle:maximumMethodLength
    public AxPolicyModel getSamplePolicyModel(final String axLogicExecutorType) {
        AxContextSchema testSlogan = new AxContextSchema(new AxArtifactKey(TEST_SLOGAN, DEFAULT_VERSION), "Java",
                        "java.lang.String");
        AxContextSchema testCase = new AxContextSchema(new AxArtifactKey("TestCase", DEFAULT_VERSION), "Java",
                        "java.lang.Byte");
        AxContextSchema testTimestamp = new AxContextSchema(new AxArtifactKey(TEST_TIMESTAMP, DEFAULT_VERSION), "Java",
                        "java.lang.Long");
        AxContextSchema testTemperature = new AxContextSchema(new AxArtifactKey(TEST_TEMPERATURE, DEFAULT_VERSION),
                        "Java", "java.lang.Double");

        AxContextSchema testContextItem000 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem000", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem000");
        AxContextSchema testContextItem001 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem001", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem001");
        AxContextSchema testContextItem002 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem002", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem002");
        AxContextSchema testContextItem003 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem003", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem003");
        AxContextSchema testContextItem004 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem004", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem004");
        AxContextSchema testContextItem005 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem005", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem005");
        AxContextSchema testContextItem006 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem006", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem006");
        AxContextSchema testContextItem007 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem007", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem007");
        AxContextSchema testContextItem008 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem008", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem008");
        AxContextSchema testContextItem009 = new AxContextSchema(
                        new AxArtifactKey("TestContextItem009", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem009");
        AxContextSchema testContextItem00A = new AxContextSchema(
                        new AxArtifactKey("TestContextItem00A", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem00A");
        AxContextSchema testContextItem00B = new AxContextSchema(
                        new AxArtifactKey("TestContextItem00B", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem00B");
        AxContextSchema testContextItem00C = new AxContextSchema(
                        new AxArtifactKey("TestContextItem00C", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestContextItem00C");

        AxContextSchema testPolicyContextItem = new AxContextSchema(
                        new AxArtifactKey("TestPolicyContextItem", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestPolicyContextItem");
        AxContextSchema testGlobalContextItem = new AxContextSchema(
                        new AxArtifactKey("TestGlobalContextItem", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestGlobalContextItem");
        AxContextSchema testExternalContextItem = new AxContextSchema(
                        new AxArtifactKey("TestExternalContextItem", DEFAULT_VERSION), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestExternalContextItem");

        AxContextSchemas axContextSchemas = new AxContextSchemas(new AxArtifactKey("TestDatatypes", DEFAULT_VERSION));
        axContextSchemas.getSchemasMap().put(testSlogan.getKey(), testSlogan);
        axContextSchemas.getSchemasMap().put(testCase.getKey(), testCase);
        axContextSchemas.getSchemasMap().put(testTimestamp.getKey(), testTimestamp);
        axContextSchemas.getSchemasMap().put(testTemperature.getKey(), testTemperature);

        axContextSchemas.getSchemasMap().put(testContextItem000.getKey(), testContextItem000);
        axContextSchemas.getSchemasMap().put(testContextItem001.getKey(), testContextItem001);
        axContextSchemas.getSchemasMap().put(testContextItem002.getKey(), testContextItem002);
        axContextSchemas.getSchemasMap().put(testContextItem003.getKey(), testContextItem003);
        axContextSchemas.getSchemasMap().put(testContextItem004.getKey(), testContextItem004);
        axContextSchemas.getSchemasMap().put(testContextItem005.getKey(), testContextItem005);
        axContextSchemas.getSchemasMap().put(testContextItem006.getKey(), testContextItem006);
        axContextSchemas.getSchemasMap().put(testContextItem007.getKey(), testContextItem007);
        axContextSchemas.getSchemasMap().put(testContextItem008.getKey(), testContextItem008);
        axContextSchemas.getSchemasMap().put(testContextItem009.getKey(), testContextItem009);
        axContextSchemas.getSchemasMap().put(testContextItem00A.getKey(), testContextItem00A);
        axContextSchemas.getSchemasMap().put(testContextItem00B.getKey(), testContextItem00B);
        axContextSchemas.getSchemasMap().put(testContextItem00C.getKey(), testContextItem00C);

        axContextSchemas.getSchemasMap().put(testPolicyContextItem.getKey(), testPolicyContextItem);
        axContextSchemas.getSchemasMap().put(testGlobalContextItem.getKey(), testGlobalContextItem);
        axContextSchemas.getSchemasMap().put(testExternalContextItem.getKey(), testExternalContextItem);

        AxEvent event0000 = new AxEvent(new AxArtifactKey("Event0000", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0000.setSource(DEFAULT_SOURCE);
        event0000.setTarget(MATCH);
        event0000.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0000.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0000.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0000.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0000.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0000.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0000.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0000.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));

        AxEvent event0001 = new AxEvent(new AxArtifactKey("Event0001", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0001.setSource(MATCH);
        event0001.setTarget(ESTABLISH);
        event0001.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0001.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0001.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0001.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0001.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0001.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0001.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0001.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0001.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0001.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0001.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0001.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0002 = new AxEvent(new AxArtifactKey("Event0002", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0002.setSource(ESTABLISH);
        event0002.setTarget(DECIDE);
        event0002.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0002.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0002.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0002.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0002.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0002.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0002.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0002.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0002.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0002.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0002.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0002.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0002.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0002.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0002.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0002.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0003 = new AxEvent(new AxArtifactKey("Event0003", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0003.setSource(DECIDE);
        event0003.setTarget("Act");
        event0003.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0003.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0003.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0003.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0003.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0003.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0003.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0003.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0003.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0003.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0003.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));
        event0003.getParameterMap().put(TEST_DECIDE_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_DECIDE_CASE_SELECTED), testCase.getKey()));
        event0003.getParameterMap().put(TEST_DECIDE_STATE_TIME, new AxField(
                        new AxReferenceKey(event0003.getKey(), TEST_DECIDE_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0004 = new AxEvent(new AxArtifactKey("Event0004", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0004.setSource("Act");
        event0004.setTarget(DEFAULT_SOURCE);
        event0004.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0004.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0004.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0004.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0004.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0004.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0004.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0004.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0004.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0004.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0004.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));
        event0004.getParameterMap().put(TEST_DECIDE_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_DECIDE_CASE_SELECTED), testCase.getKey()));
        event0004.getParameterMap().put(TEST_DECIDE_STATE_TIME, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_DECIDE_STATE_TIME), testTimestamp.getKey()));
        event0004.getParameterMap().put(TEST_ACT_CASE_SELECTED,
                        new AxField(new AxReferenceKey(event0004.getKey(), TEST_ACT_CASE_SELECTED), testCase.getKey()));
        event0004.getParameterMap().put(TEST_ACT_STATE_TIME, new AxField(
                        new AxReferenceKey(event0004.getKey(), TEST_ACT_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0100 = new AxEvent(new AxArtifactKey("Event0100", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0100.setSource(DEFAULT_SOURCE);
        event0100.setTarget(MATCH);
        event0100.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0100.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0100.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0100.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0100.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0100.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0100.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0100.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));

        AxEvent event0101 = new AxEvent(new AxArtifactKey("Event0101", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0101.setSource(MATCH);
        event0101.setTarget(ESTABLISH);
        event0101.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0101.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0101.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0101.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0101.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0101.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0101.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0101.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0101.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0101.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0101.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0101.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0102 = new AxEvent(new AxArtifactKey("Event0102", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0102.setSource(ESTABLISH);
        event0102.setTarget(DECIDE);
        event0102.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0102.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0102.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0102.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0102.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0102.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0102.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0102.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0102.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0102.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0102.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0102.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0102.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0102.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0102.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0102.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0103 = new AxEvent(new AxArtifactKey("Event0103", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0103.setSource(DECIDE);
        event0103.setTarget("Act");
        event0103.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0103.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0103.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0103.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0103.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0103.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0103.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0103.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0103.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0103.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0103.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));
        event0103.getParameterMap().put(TEST_DECIDE_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_DECIDE_CASE_SELECTED), testCase.getKey()));
        event0103.getParameterMap().put(TEST_DECIDE_STATE_TIME, new AxField(
                        new AxReferenceKey(event0103.getKey(), TEST_DECIDE_STATE_TIME), testTimestamp.getKey()));

        AxEvent event0104 = new AxEvent(new AxArtifactKey("Event0104", DEFAULT_VERSION),
                        DEFAULT_NAMESPACE);
        event0104.setSource("Act");
        event0104.setTarget(DEFAULT_SOURCE);
        event0104.getParameterMap().put(TEST_SLOGAN,
                        new AxField(new AxReferenceKey(event0104.getKey(), TEST_SLOGAN), testSlogan.getKey()));
        event0104.getParameterMap().put(TEST_MATCH_CASE,
                        new AxField(new AxReferenceKey(event0104.getKey(), TEST_MATCH_CASE), testCase.getKey()));
        event0104.getParameterMap().put(TEST_TIMESTAMP,
                        new AxField(new AxReferenceKey(event0104.getKey(), TEST_TIMESTAMP), testTimestamp.getKey()));
        event0104.getParameterMap().put(TEST_TEMPERATURE, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_TEMPERATURE), testTemperature.getKey()));
        event0104.getParameterMap().put(TEST_MATCH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_MATCH_CASE_SELECTED), testCase.getKey()));
        event0104.getParameterMap().put(TEST_MATCH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_MATCH_STATE_TIME), testTimestamp.getKey()));
        event0104.getParameterMap().put(TEST_ESTABLISH_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_ESTABLISH_CASE_SELECTED), testCase.getKey()));
        event0104.getParameterMap().put(TEST_ESTABLISH_STATE_TIME, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_ESTABLISH_STATE_TIME), testTimestamp.getKey()));
        event0104.getParameterMap().put(TEST_DECIDE_CASE_SELECTED, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_DECIDE_CASE_SELECTED), testCase.getKey()));
        event0104.getParameterMap().put(TEST_DECIDE_STATE_TIME, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_DECIDE_STATE_TIME), testTimestamp.getKey()));
        event0104.getParameterMap().put(TEST_ACT_CASE_SELECTED,
                        new AxField(new AxReferenceKey(event0104.getKey(), TEST_ACT_CASE_SELECTED), testCase.getKey()));
        event0104.getParameterMap().put(TEST_ACT_STATE_TIME, new AxField(
                        new AxReferenceKey(event0104.getKey(), TEST_ACT_STATE_TIME), testTimestamp.getKey()));

        AxEvents events = new AxEvents(new AxArtifactKey("Events", DEFAULT_VERSION));
        events.getEventMap().put(event0000.getKey(), event0000);
        events.getEventMap().put(event0001.getKey(), event0001);
        events.getEventMap().put(event0002.getKey(), event0002);
        events.getEventMap().put(event0003.getKey(), event0003);
        events.getEventMap().put(event0004.getKey(), event0004);
        events.getEventMap().put(event0100.getKey(), event0100);
        events.getEventMap().put(event0101.getKey(), event0101);
        events.getEventMap().put(event0102.getKey(), event0102);
        events.getEventMap().put(event0103.getKey(), event0103);
        events.getEventMap().put(event0104.getKey(), event0104);

        AxContextAlbum externalContextAlbum = new AxContextAlbum(
                        new AxArtifactKey("ExternalContextAlbum", DEFAULT_VERSION), "EXTERNAL", false,
                        testExternalContextItem.getKey());
        AxContextAlbum globalContextAlbum = new AxContextAlbum(new AxArtifactKey("GlobalContextAlbum", DEFAULT_VERSION),
                        "GLOBAL", true, testGlobalContextItem.getKey());
        AxContextAlbum policy0ContextAlbum = new AxContextAlbum(
                        new AxArtifactKey("Policy0ContextAlbum", DEFAULT_VERSION), "APPLICATION", true,
                        testPolicyContextItem.getKey());
        AxContextAlbum policy1ContextAlbum = new AxContextAlbum(
                        new AxArtifactKey("Policy1ContextAlbum", DEFAULT_VERSION), "APPLICATION", true,
                        testPolicyContextItem.getKey());

        AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("Context", DEFAULT_VERSION));
        albums.getAlbumsMap().put(externalContextAlbum.getKey(), externalContextAlbum);
        albums.getAlbumsMap().put(globalContextAlbum.getKey(), globalContextAlbum);
        albums.getAlbumsMap().put(policy0ContextAlbum.getKey(), policy0ContextAlbum);
        albums.getAlbumsMap().put(policy1ContextAlbum.getKey(), policy1ContextAlbum);

        Set<AxArtifactKey> referenceKeySet0 = new TreeSet<>();
        referenceKeySet0.add(policy0ContextAlbum.getKey());
        referenceKeySet0.add(policy1ContextAlbum.getKey());
        referenceKeySet0.add(globalContextAlbum.getKey());
        referenceKeySet0.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet1 = new TreeSet<>();
        referenceKeySet1.add(policy0ContextAlbum.getKey());
        referenceKeySet1.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet2 = new TreeSet<>();
        referenceKeySet2.add(policy1ContextAlbum.getKey());
        referenceKeySet2.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet3 = new TreeSet<>();
        referenceKeySet3.add(globalContextAlbum.getKey());
        referenceKeySet3.add(externalContextAlbum.getKey());

        List<Set<AxArtifactKey>> referenceKeySetList = new ArrayList<>();
        referenceKeySetList.add(referenceKeySet0);
        referenceKeySetList.add(referenceKeySet1);
        referenceKeySetList.add(referenceKeySet2);
        referenceKeySetList.add(referenceKeySet3);

        AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", DEFAULT_VERSION));
        tasks.getTaskMap().putAll(getTaskMap(MATCH, event0000.getParameterMap(), event0001.getParameterMap(),
                        referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap(ESTABLISH, event0001.getParameterMap(), event0002.getParameterMap(),
                        referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap(DECIDE, event0002.getParameterMap(), event0003.getParameterMap(),
                        referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap("Act", event0003.getParameterMap(), event0004.getParameterMap(),
                        referenceKeySetList, axLogicExecutorType));

        Set<AxArtifactKey> matchTasks = new TreeSet<>();
        Set<AxArtifactKey> establishTasks = new TreeSet<>();
        Set<AxArtifactKey> decideTasks = new TreeSet<>();
        Set<AxArtifactKey> actTasks = new TreeSet<>();
        for (AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains(MATCH)) {
                matchTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains(ESTABLISH)) {
                establishTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains(DECIDE)) {
                decideTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Act")) {
                actTasks.add(task.getKey());
            }
        }
        List<Set<AxArtifactKey>> taskReferenceList = new ArrayList<>();
        taskReferenceList.add(matchTasks);
        taskReferenceList.add(establishTasks);
        taskReferenceList.add(decideTasks);
        taskReferenceList.add(actTasks);

        List<AxArtifactKey> p0InEventList = new ArrayList<>();
        p0InEventList.add(event0000.getKey());
        p0InEventList.add(event0001.getKey());
        p0InEventList.add(event0002.getKey());
        p0InEventList.add(event0003.getKey());

        List<AxArtifactKey> p0OutEventList = new ArrayList<>();
        p0OutEventList.add(event0001.getKey());
        p0OutEventList.add(event0002.getKey());
        p0OutEventList.add(event0003.getKey());
        p0OutEventList.add(event0004.getKey());

        List<AxArtifactKey> p0defaultTaskList = new ArrayList<>();
        p0defaultTaskList.add(tasks.get("Task_Match0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Establish2").getKey());
        p0defaultTaskList.add(tasks.get("Task_Decide3").getKey());
        p0defaultTaskList.add(tasks.get("Task_Act1").getKey());

        List<AxArtifactKey> p1InEventList = new ArrayList<>();
        p1InEventList.add(event0100.getKey());
        p1InEventList.add(event0101.getKey());
        p1InEventList.add(event0102.getKey());
        p1InEventList.add(event0103.getKey());

        List<AxArtifactKey> p1OutEventList = new ArrayList<>();
        p1OutEventList.add(event0101.getKey());
        p1OutEventList.add(event0102.getKey());
        p1OutEventList.add(event0103.getKey());
        p1OutEventList.add(event0104.getKey());

        List<AxArtifactKey> p1defaultTaskList = new ArrayList<>();
        p1defaultTaskList.add(tasks.get("Task_Match3").getKey());
        p1defaultTaskList.add(tasks.get("Task_Establish1").getKey());
        p1defaultTaskList.add(tasks.get("Task_Decide3").getKey());
        p1defaultTaskList.add(tasks.get("Task_Act0").getKey());

        Set<AxArtifactKey> p0ReferenceKeySet0 = new TreeSet<>();
        p0ReferenceKeySet0.add(policy0ContextAlbum.getKey());
        p0ReferenceKeySet0.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet1 = new TreeSet<>();
        p0ReferenceKeySet1.add(policy1ContextAlbum.getKey());
        p0ReferenceKeySet1.add(globalContextAlbum.getKey());
        p0ReferenceKeySet1.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet2 = new TreeSet<>();
        p0ReferenceKeySet2.add(policy0ContextAlbum.getKey());
        p0ReferenceKeySet2.add(globalContextAlbum.getKey());
        p0ReferenceKeySet2.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet3 = new TreeSet<>();
        p0ReferenceKeySet3.add(globalContextAlbum.getKey());

        List<Set<AxArtifactKey>> p0ReferenceKeySetList = new ArrayList<>();
        p0ReferenceKeySetList.add(p0ReferenceKeySet0);
        p0ReferenceKeySetList.add(p0ReferenceKeySet1);
        p0ReferenceKeySetList.add(p0ReferenceKeySet2);
        p0ReferenceKeySetList.add(p0ReferenceKeySet3);

        AxPolicy policy0 = new AxPolicy(new AxArtifactKey("Policy0", DEFAULT_VERSION));
        policy0.setTemplate("MEDA");
        policy0.setStateMap(getStateMap(policy0.getKey(), p0InEventList, p0OutEventList, p0ReferenceKeySetList,
                        axLogicExecutorType, p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get(MATCH).getKey().getLocalName());

        Set<AxArtifactKey> p1ReferenceKeySet0 = new TreeSet<>();
        p1ReferenceKeySet0.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet0.add(globalContextAlbum.getKey());
        p1ReferenceKeySet0.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet1 = new TreeSet<>();
        p1ReferenceKeySet1.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet1.add(globalContextAlbum.getKey());
        p1ReferenceKeySet1.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet2 = new TreeSet<>();
        p1ReferenceKeySet2.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet2.add(globalContextAlbum.getKey());
        p1ReferenceKeySet2.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet3 = new TreeSet<>();
        p1ReferenceKeySet3.add(globalContextAlbum.getKey());

        List<Set<AxArtifactKey>> p1ReferenceKeySetList = new ArrayList<>();
        p1ReferenceKeySetList.add(p1ReferenceKeySet0);
        p1ReferenceKeySetList.add(p1ReferenceKeySet1);
        p1ReferenceKeySetList.add(p1ReferenceKeySet2);
        p1ReferenceKeySetList.add(p1ReferenceKeySet3);

        AxPolicy policy1 = new AxPolicy(new AxArtifactKey("Policy1", DEFAULT_VERSION));
        policy1.setTemplate("MEDA");
        policy1.setStateMap(getStateMap(policy1.getKey(), p1InEventList, p1OutEventList, p1ReferenceKeySetList,
                        axLogicExecutorType, p1defaultTaskList, taskReferenceList));
        policy1.setFirstState(policy1.getStateMap().get(MATCH).getKey().getLocalName());

        AxPolicies policies = new AxPolicies(new AxArtifactKey("Policies", DEFAULT_VERSION));
        policies.getPolicyMap().put(policy0.getKey(), policy0);
        policies.getPolicyMap().put(policy1.getKey(), policy1);

        AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInformation", DEFAULT_VERSION));
        AxPolicyModel policyModel = new AxPolicyModel(
                        new AxArtifactKey("SamplePolicyModel" + axLogicExecutorType, DEFAULT_VERSION));
        policyModel.setKeyInformation(keyInformation);
        policyModel.setPolicies(policies);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setAlbums(albums);
        policyModel.setSchemas(axContextSchemas);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        AxValidationResult result = policyModel.validate(new AxValidationResult());
        if (!result.getValidationResult().equals(AxValidationResult.ValidationResult.VALID)) {
            throw new ApexRuntimeException("model " + policyModel.getId() + " is not valid" + result);
        }
        return policyModel;
    }

    /**
     * Gets the state map.
     *
     * @param policyKey the policy key
     * @param inEventKeyList the in event key list
     * @param outEventKeyList the out event key list
     * @param referenceKeySetList the reference key set list
     * @param axLogicExecutorType the ax logic executor type
     * @param defaultTaskList the default task list
     * @param taskKeySetList the task key set list
     * @return the state map
     */
    private Map<String, AxState> getStateMap(final AxArtifactKey policyKey, final List<AxArtifactKey> inEventKeyList,
                    final List<AxArtifactKey> outEventKeyList, final List<Set<AxArtifactKey>> referenceKeySetList,
                    final String axLogicExecutorType, final List<AxArtifactKey> defaultTaskList,
                    final List<Set<AxArtifactKey>> taskKeySetList) {

        AxState actState = new AxState(new AxReferenceKey(policyKey, "Act"));
        actState.setTrigger(inEventKeyList.get(3));
        AxStateOutput act2Out = new AxStateOutput(actState.getKey(), AxReferenceKey.getNullKey(),
                        outEventKeyList.get(3));
        actState.getStateOutputs().put(act2Out.getKey().getLocalName(), act2Out);
        actState.setContextAlbumReferences(referenceKeySetList.get(3));

        AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName())
                        .setDefaultLogic("DefaultStateLogic");
        actState.setTaskSelectionLogic(new AxTaskSelectionLogic(actState.getKey(), TASK_SELECTION_LIGIC,
                        axLogicExecutorType, logicReader));
        actState.setDefaultTask(defaultTaskList.get(3));
        for (AxArtifactKey taskKey : taskKeySetList.get(3)) {
            actState.getTaskReferences().put(taskKey, new AxStateTaskReference(actState.getKey(), taskKey,
                            AxStateTaskOutputType.DIRECT, act2Out.getKey()));
        }

        AxState decideState = new AxState(new AxReferenceKey(policyKey, DECIDE));
        decideState.setTrigger(inEventKeyList.get(2));
        AxStateOutput decide2Act = new AxStateOutput(decideState.getKey(), actState.getKey(), outEventKeyList.get(2));
        decideState.getStateOutputs().put(decide2Act.getKey().getLocalName(), decide2Act);
        decideState.setContextAlbumReferences(referenceKeySetList.get(2));
        decideState.setTaskSelectionLogic(new AxTaskSelectionLogic(decideState.getKey(), TASK_SELECTION_LIGIC,
                        axLogicExecutorType, logicReader));
        decideState.setDefaultTask(defaultTaskList.get(2));
        for (AxArtifactKey taskKey : taskKeySetList.get(2)) {
            decideState.getTaskReferences().put(taskKey, new AxStateTaskReference(decideState.getKey(), taskKey,
                            AxStateTaskOutputType.DIRECT, decide2Act.getKey()));
        }

        AxState establishState = new AxState(new AxReferenceKey(policyKey, ESTABLISH));
        establishState.setTrigger(inEventKeyList.get(1));
        AxStateOutput establish2Decide = new AxStateOutput(establishState.getKey(), decideState.getKey(),
                        outEventKeyList.get(1));
        establishState.getStateOutputs().put(establish2Decide.getKey().getLocalName(), establish2Decide);
        establishState.setContextAlbumReferences(referenceKeySetList.get(1));
        establishState.setTaskSelectionLogic(new AxTaskSelectionLogic(establishState.getKey(), TASK_SELECTION_LIGIC,
                        axLogicExecutorType, logicReader));
        establishState.setDefaultTask(defaultTaskList.get(1));
        for (AxArtifactKey taskKey : taskKeySetList.get(1)) {
            establishState.getTaskReferences().put(taskKey, new AxStateTaskReference(establishState.getKey(), taskKey,
                            AxStateTaskOutputType.DIRECT, establish2Decide.getKey()));
        }

        AxState matchState = new AxState(new AxReferenceKey(policyKey, MATCH));
        matchState.setTrigger(inEventKeyList.get(0));
        AxStateOutput match2Establish = new AxStateOutput(matchState.getKey(), establishState.getKey(),
                        outEventKeyList.get(0));
        matchState.getStateOutputs().put(match2Establish.getKey().getLocalName(), match2Establish);
        matchState.setContextAlbumReferences(referenceKeySetList.get(0));
        matchState.setTaskSelectionLogic(new AxTaskSelectionLogic(matchState.getKey(), TASK_SELECTION_LIGIC,
                        axLogicExecutorType, logicReader));
        matchState.setDefaultTask(defaultTaskList.get(0));
        for (AxArtifactKey taskKey : taskKeySetList.get(0)) {
            matchState.getTaskReferences().put(taskKey, new AxStateTaskReference(matchState.getKey(), taskKey,
                            AxStateTaskOutputType.DIRECT, match2Establish.getKey()));
        }

        Map<String, AxState> stateMap = new TreeMap<>();
        stateMap.put(matchState.getKey().getLocalName(), matchState);
        stateMap.put(establishState.getKey().getLocalName(), establishState);
        stateMap.put(decideState.getKey().getLocalName(), decideState);
        stateMap.put(actState.getKey().getLocalName(), actState);

        return stateMap;
    }
    // CHECKSTYLE:ON: checkstyle:maximumMethodLength

    /**
     * Gets the task map.
     *
     * @param state the state
     * @param inputFields the input fields
     * @param outputFields the output fields
     * @param referenceKeySetList the reference key set list
     * @param axLogicExecutorType the ax logic executor type
     * @return the task map
     */
    private Map<AxArtifactKey, AxTask> getTaskMap(final String state, final Map<String, AxField> inputFields,
                    final Map<String, AxField> outputFields, final List<Set<AxArtifactKey>> referenceKeySetList,
                    final String axLogicExecutorType) {

        AxTask testTask0 = new AxTask(new AxArtifactKey(TASK + state + "0", DEFAULT_VERSION));
        testTask0.duplicateInputFields(inputFields);
        testTask0.duplicateOutputFields(outputFields);
        AxTaskParameter parameter00 = new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), PARAMETER0),
                        DEFAULT_VALUE0);
        AxTaskParameter parameter01 = new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), PARAMETER1),
                        DEFAULT_VALUE1);
        AxTaskParameter parameter02 = new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), PARAMETER2),
                        DEFAULT_VALUE2);
        testTask0.getTaskParameters().put(parameter00.getKey().getLocalName(), parameter00);
        testTask0.getTaskParameters().put(parameter01.getKey().getLocalName(), parameter01);
        testTask0.getTaskParameters().put(parameter02.getKey().getLocalName(), parameter02);
        testTask0.setContextAlbumReferences(referenceKeySetList.get(0));

        AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName())
                        .setDefaultLogic("DefaultTaskLogic");
        testTask0.setTaskLogic(getTaskLogic(testTask0, logicReader, axLogicExecutorType, state, "2"));

        AxTask testTask1 = new AxTask(new AxArtifactKey(TASK + state + "1", DEFAULT_VERSION));
        testTask1.duplicateInputFields(inputFields);
        testTask1.duplicateOutputFields(outputFields);
        AxTaskParameter parameter10 = new AxTaskParameter(new AxReferenceKey(testTask1.getKey(), PARAMETER0),
                        DEFAULT_VALUE0);
        AxTaskParameter parameter11 = new AxTaskParameter(new AxReferenceKey(testTask1.getKey(), PARAMETER1),
                        DEFAULT_VALUE1);
        testTask1.getTaskParameters().put(parameter10.getKey().getLocalName(), parameter10);
        testTask1.getTaskParameters().put(parameter11.getKey().getLocalName(), parameter11);
        testTask1.setContextAlbumReferences(referenceKeySetList.get(1));
        testTask1.setTaskLogic(getTaskLogic(testTask1, logicReader, axLogicExecutorType, state, "3"));

        AxTask testTask2 = new AxTask(new AxArtifactKey(TASK + state + "2", DEFAULT_VERSION));
        testTask2.duplicateInputFields(inputFields);
        testTask2.duplicateOutputFields(outputFields);
        AxTaskParameter parameter20 = new AxTaskParameter(new AxReferenceKey(testTask2.getKey(), PARAMETER0),
                        DEFAULT_VALUE0);
        testTask2.getTaskParameters().put(parameter20.getKey().getLocalName(), parameter20);
        testTask2.setContextAlbumReferences(referenceKeySetList.get(2));
        testTask2.setTaskLogic(getTaskLogic(testTask2, logicReader, axLogicExecutorType, state, "0"));

        AxTask testTask3 = new AxTask(new AxArtifactKey(TASK + state + "3", DEFAULT_VERSION));
        testTask3.duplicateInputFields(inputFields);
        testTask3.duplicateOutputFields(outputFields);
        AxTaskParameter parameter30 = new AxTaskParameter(new AxReferenceKey(testTask3.getKey(), PARAMETER0),
                        DEFAULT_VALUE0);
        testTask3.getTaskParameters().put(parameter30.getKey().getLocalName(), parameter30);
        testTask3.setContextAlbumReferences(referenceKeySetList.get(THIRD_ENTRY));
        testTask3.setTaskLogic(getTaskLogic(testTask3, logicReader, axLogicExecutorType, state, "1"));

        Map<AxArtifactKey, AxTask> taskMap = new TreeMap<>();
        taskMap.put(testTask0.getKey(), testTask0);
        taskMap.put(testTask1.getKey(), testTask1);
        taskMap.put(testTask2.getKey(), testTask2);
        taskMap.put(testTask3.getKey(), testTask3);

        return taskMap;
    }

    /**
     * Gets the task logic.
     *
     * @param task the task
     * @param logicReader the logic reader
     * @param logicFlavour the logic flavour
     * @param stateName the state name
     * @param caseToUse the case to use
     * @return the task logic
     */
    private AxTaskLogic getTaskLogic(final AxTask task, final AxLogicReader logicReader, final String logicFlavour,
                    final String stateName, final String caseToUse) {
        AxTaskLogic axLogic = new AxTaskLogic(new AxReferenceKey(task.getKey(), "_TaskLogic"), logicFlavour,
                        logicReader);

        axLogic.setLogic(axLogic.getLogic().replaceAll("<STATE_NAME>", stateName)
                        .replaceAll("<TASK_NAME>", task.getKey().getName())
                        .replaceAll("<RANDOM_BYTE_VALUE>", caseToUse));

        return axLogic;
    }
}
