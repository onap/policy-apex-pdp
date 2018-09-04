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

package org.onap.policy.apex.test.common.model;

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
    private static final int THIRD_ENTRY = 3;

    /**
     * Get a sample policy model.
     *
     * @param axLogicExecutorType The type of logic executor, the scripting language being used
     * @return the sample policy model
     */
    // CHECKSTYLE:OFF: checkstyle:maximumMethodLength
    public AxPolicyModel getSamplePolicyModel(final String axLogicExecutorType) {
        AxContextSchema testSlogan =
                new AxContextSchema(new AxArtifactKey("TestSlogan", "0.0.1"), "Java", "java.lang.String");
        AxContextSchema testCase =
                new AxContextSchema(new AxArtifactKey("TestCase", "0.0.1"), "Java", "java.lang.Byte");
        AxContextSchema testTimestamp =
                new AxContextSchema(new AxArtifactKey("TestTimestamp", "0.0.1"), "Java", "java.lang.Long");
        AxContextSchema testTemperature =
                new AxContextSchema(new AxArtifactKey("TestTemperature", "0.0.1"), "Java", "java.lang.Double");

        AxContextSchema testContextItem000 = new AxContextSchema(new AxArtifactKey("TestContextItem000", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem000");
        AxContextSchema testContextItem001 = new AxContextSchema(new AxArtifactKey("TestContextItem001", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem001");
        AxContextSchema testContextItem002 = new AxContextSchema(new AxArtifactKey("TestContextItem002", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem002");
        AxContextSchema testContextItem003 = new AxContextSchema(new AxArtifactKey("TestContextItem003", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem003");
        AxContextSchema testContextItem004 = new AxContextSchema(new AxArtifactKey("TestContextItem004", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem004");
        AxContextSchema testContextItem005 = new AxContextSchema(new AxArtifactKey("TestContextItem005", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem005");
        AxContextSchema testContextItem006 = new AxContextSchema(new AxArtifactKey("TestContextItem006", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem006");
        AxContextSchema testContextItem007 = new AxContextSchema(new AxArtifactKey("TestContextItem007", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem007");
        AxContextSchema testContextItem008 = new AxContextSchema(new AxArtifactKey("TestContextItem008", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem008");
        AxContextSchema testContextItem009 = new AxContextSchema(new AxArtifactKey("TestContextItem009", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem009");
        AxContextSchema testContextItem00A = new AxContextSchema(new AxArtifactKey("TestContextItem00A", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem00A");
        AxContextSchema testContextItem00B = new AxContextSchema(new AxArtifactKey("TestContextItem00B", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem00B");
        AxContextSchema testContextItem00C = new AxContextSchema(new AxArtifactKey("TestContextItem00C", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestContextItem00C");

        AxContextSchema testPolicyContextItem = new AxContextSchema(new AxArtifactKey("TestPolicyContextItem", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestPolicyContextItem");
        AxContextSchema testGlobalContextItem = new AxContextSchema(new AxArtifactKey("TestGlobalContextItem", "0.0.1"),
                "Java", "org.onap.policy.apex.context.test.concepts.TestGlobalContextItem");
        AxContextSchema testExternalContextItem =
                new AxContextSchema(new AxArtifactKey("TestExternalContextItem", "0.0.1"), "Java",
                        "org.onap.policy.apex.context.test.concepts.TestExternalContextItem");

        AxContextSchemas axContextSchemas = new AxContextSchemas(new AxArtifactKey("TestDatatypes", "0.0.1"));
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

        AxEvent event0000 = new AxEvent(new AxArtifactKey("Event0000", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0000.setSource("Outside");
        event0000.setTarget("Match");
        event0000.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0000.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0000.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0000.getKey(), "TestMatchCase"), testCase.getKey()));
        event0000.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0000.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0000.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0000.getKey(), "TestTemperature"), testTemperature.getKey()));

        AxEvent event0001 = new AxEvent(new AxArtifactKey("Event0001", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0001.setSource("Match");
        event0001.setTarget("Establish");
        event0001.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0001.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestMatchCase"), testCase.getKey()));
        event0001.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0001.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0001.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0001.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0001.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));

        AxEvent event0002 = new AxEvent(new AxArtifactKey("Event0002", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0002.setSource("Establish");
        event0002.setTarget("Decide");
        event0002.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0002.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestMatchCase"), testCase.getKey()));
        event0002.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0002.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0002.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0002.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0002.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0002.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0002.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));

        AxEvent event0003 = new AxEvent(new AxArtifactKey("Event0003", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0003.setSource("Decide");
        event0003.setTarget("Act");
        event0003.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0003.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestMatchCase"), testCase.getKey()));
        event0003.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0003.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0003.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0003.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0003.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0003.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));
        event0003.getParameterMap().put("TestDecideCaseSelected",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestDecideCaseSelected"), testCase.getKey()));
        event0003.getParameterMap().put("TestDecideStateTime",
                new AxField(new AxReferenceKey(event0003.getKey(), "TestDecideStateTime"), testTimestamp.getKey()));

        AxEvent event0004 = new AxEvent(new AxArtifactKey("Event0004", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0004.setSource("Act");
        event0004.setTarget("Outside");
        event0004.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0004.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestMatchCase"), testCase.getKey()));
        event0004.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0004.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0004.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0004.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0004.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0004.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));
        event0004.getParameterMap().put("TestDecideCaseSelected",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestDecideCaseSelected"), testCase.getKey()));
        event0004.getParameterMap().put("TestDecideStateTime",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestDecideStateTime"), testTimestamp.getKey()));
        event0004.getParameterMap().put("TestActCaseSelected",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestActCaseSelected"), testCase.getKey()));
        event0004.getParameterMap().put("TestActStateTime",
                new AxField(new AxReferenceKey(event0004.getKey(), "TestActStateTime"), testTimestamp.getKey()));

        AxEvent event0100 = new AxEvent(new AxArtifactKey("Event0100", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0100.setSource("Outside");
        event0100.setTarget("Match");
        event0100.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0100.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0100.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0100.getKey(), "TestMatchCase"), testCase.getKey()));
        event0100.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0100.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0100.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0100.getKey(), "TestTemperature"), testTemperature.getKey()));

        AxEvent event0101 = new AxEvent(new AxArtifactKey("Event0101", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0101.setSource("Match");
        event0101.setTarget("Establish");
        event0101.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0101.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestMatchCase"), testCase.getKey()));
        event0101.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0101.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0101.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0101.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0101.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));

        AxEvent event0102 = new AxEvent(new AxArtifactKey("Event0102", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0102.setSource("Establish");
        event0102.setTarget("Decide");
        event0102.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0102.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestMatchCase"), testCase.getKey()));
        event0102.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0102.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0102.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0102.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0102.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0102.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0102.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));

        AxEvent event0103 = new AxEvent(new AxArtifactKey("Event0103", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0103.setSource("Decide");
        event0103.setTarget("Act");
        event0103.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0103.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestMatchCase"), testCase.getKey()));
        event0103.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0103.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0103.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0103.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0103.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0103.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));
        event0103.getParameterMap().put("TestDecideCaseSelected",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestDecideCaseSelected"), testCase.getKey()));
        event0103.getParameterMap().put("TestDecideStateTime",
                new AxField(new AxReferenceKey(event0103.getKey(), "TestDecideStateTime"), testTimestamp.getKey()));

        AxEvent event0104 = new AxEvent(new AxArtifactKey("Event0104", "0.0.1"), "org.onap.policy.apex.sample.events");
        event0104.setSource("Act");
        event0104.setTarget("Outside");
        event0104.getParameterMap().put("TestSlogan",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestSlogan"), testSlogan.getKey()));
        event0104.getParameterMap().put("TestMatchCase",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestMatchCase"), testCase.getKey()));
        event0104.getParameterMap().put("TestTimestamp",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestTimestamp"), testTimestamp.getKey()));
        event0104.getParameterMap().put("TestTemperature",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestTemperature"), testTemperature.getKey()));
        event0104.getParameterMap().put("TestMatchCaseSelected",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestMatchCaseSelected"), testCase.getKey()));
        event0104.getParameterMap().put("TestMatchStateTime",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestMatchStateTime"), testTimestamp.getKey()));
        event0104.getParameterMap().put("TestEstablishCaseSelected",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestEstablishCaseSelected"), testCase.getKey()));
        event0104.getParameterMap().put("TestEstablishStateTime",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestEstablishStateTime"), testTimestamp.getKey()));
        event0104.getParameterMap().put("TestDecideCaseSelected",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestDecideCaseSelected"), testCase.getKey()));
        event0104.getParameterMap().put("TestDecideStateTime",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestDecideStateTime"), testTimestamp.getKey()));
        event0104.getParameterMap().put("TestActCaseSelected",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestActCaseSelected"), testCase.getKey()));
        event0104.getParameterMap().put("TestActStateTime",
                new AxField(new AxReferenceKey(event0104.getKey(), "TestActStateTime"), testTimestamp.getKey()));

        AxEvents events = new AxEvents(new AxArtifactKey("Events", "0.0.1"));
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

        AxContextAlbum externalContextAlbum = new AxContextAlbum(new AxArtifactKey("ExternalContextAlbum", "0.0.1"),
                "EXTERNAL", false, testExternalContextItem.getKey());
        AxContextAlbum globalContextAlbum = new AxContextAlbum(new AxArtifactKey("GlobalContextAlbum", "0.0.1"),
                "GLOBAL", true, testGlobalContextItem.getKey());
        AxContextAlbum policy0ContextAlbum = new AxContextAlbum(new AxArtifactKey("Policy0ContextAlbum", "0.0.1"),
                "APPLICATION", true, testPolicyContextItem.getKey());
        AxContextAlbum policy1ContextAlbum = new AxContextAlbum(new AxArtifactKey("Policy1ContextAlbum", "0.0.1"),
                "APPLICATION", true, testPolicyContextItem.getKey());

        AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("Context", "0.0.1"));
        albums.getAlbumsMap().put(externalContextAlbum.getKey(), externalContextAlbum);
        albums.getAlbumsMap().put(globalContextAlbum.getKey(), globalContextAlbum);
        albums.getAlbumsMap().put(policy0ContextAlbum.getKey(), policy0ContextAlbum);
        albums.getAlbumsMap().put(policy1ContextAlbum.getKey(), policy1ContextAlbum);

        Set<AxArtifactKey> referenceKeySet0 = new TreeSet<AxArtifactKey>();
        referenceKeySet0.add(policy0ContextAlbum.getKey());
        referenceKeySet0.add(policy1ContextAlbum.getKey());
        referenceKeySet0.add(globalContextAlbum.getKey());
        referenceKeySet0.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet1 = new TreeSet<AxArtifactKey>();
        referenceKeySet1.add(policy0ContextAlbum.getKey());
        referenceKeySet1.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet2 = new TreeSet<AxArtifactKey>();
        referenceKeySet2.add(policy1ContextAlbum.getKey());
        referenceKeySet2.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> referenceKeySet3 = new TreeSet<AxArtifactKey>();
        referenceKeySet3.add(globalContextAlbum.getKey());
        referenceKeySet3.add(externalContextAlbum.getKey());

        List<Set<AxArtifactKey>> referenceKeySetList = new ArrayList<Set<AxArtifactKey>>();
        referenceKeySetList.add(referenceKeySet0);
        referenceKeySetList.add(referenceKeySet1);
        referenceKeySetList.add(referenceKeySet2);
        referenceKeySetList.add(referenceKeySet3);

        AxTasks tasks = new AxTasks(new AxArtifactKey("Tasks", "0.0.1"));
        tasks.getTaskMap().putAll(getTaskMap("Match", event0000.getParameterMap(), event0001.getParameterMap(),
                referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap("Establish", event0001.getParameterMap(), event0002.getParameterMap(),
                referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap("Decide", event0002.getParameterMap(), event0003.getParameterMap(),
                referenceKeySetList, axLogicExecutorType));
        tasks.getTaskMap().putAll(getTaskMap("Act", event0003.getParameterMap(), event0004.getParameterMap(),
                referenceKeySetList, axLogicExecutorType));

        Set<AxArtifactKey> matchTasks = new TreeSet<AxArtifactKey>();
        Set<AxArtifactKey> establishTasks = new TreeSet<AxArtifactKey>();
        Set<AxArtifactKey> decideTasks = new TreeSet<AxArtifactKey>();
        Set<AxArtifactKey> actTasks = new TreeSet<AxArtifactKey>();
        for (AxTask task : tasks.getTaskMap().values()) {
            if (task.getKey().getName().contains("Match")) {
                matchTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Establish")) {
                establishTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Decide")) {
                decideTasks.add(task.getKey());
            }
            if (task.getKey().getName().contains("Act")) {
                actTasks.add(task.getKey());
            }
        }
        List<Set<AxArtifactKey>> taskReferenceList = new ArrayList<Set<AxArtifactKey>>();
        taskReferenceList.add(matchTasks);
        taskReferenceList.add(establishTasks);
        taskReferenceList.add(decideTasks);
        taskReferenceList.add(actTasks);

        List<AxArtifactKey> p0InEventList = new ArrayList<AxArtifactKey>();
        p0InEventList.add(event0000.getKey());
        p0InEventList.add(event0001.getKey());
        p0InEventList.add(event0002.getKey());
        p0InEventList.add(event0003.getKey());

        List<AxArtifactKey> p0OutEventList = new ArrayList<AxArtifactKey>();
        p0OutEventList.add(event0001.getKey());
        p0OutEventList.add(event0002.getKey());
        p0OutEventList.add(event0003.getKey());
        p0OutEventList.add(event0004.getKey());

        List<AxArtifactKey> p0defaultTaskList = new ArrayList<AxArtifactKey>();
        p0defaultTaskList.add(tasks.get("Task_Match0").getKey());
        p0defaultTaskList.add(tasks.get("Task_Establish2").getKey());
        p0defaultTaskList.add(tasks.get("Task_Decide3").getKey());
        p0defaultTaskList.add(tasks.get("Task_Act1").getKey());

        List<AxArtifactKey> p1InEventList = new ArrayList<AxArtifactKey>();
        p1InEventList.add(event0100.getKey());
        p1InEventList.add(event0101.getKey());
        p1InEventList.add(event0102.getKey());
        p1InEventList.add(event0103.getKey());

        List<AxArtifactKey> p1OutEventList = new ArrayList<AxArtifactKey>();
        p1OutEventList.add(event0101.getKey());
        p1OutEventList.add(event0102.getKey());
        p1OutEventList.add(event0103.getKey());
        p1OutEventList.add(event0104.getKey());

        List<AxArtifactKey> p1defaultTaskList = new ArrayList<AxArtifactKey>();
        p1defaultTaskList.add(tasks.get("Task_Match3").getKey());
        p1defaultTaskList.add(tasks.get("Task_Establish1").getKey());
        p1defaultTaskList.add(tasks.get("Task_Decide3").getKey());
        p1defaultTaskList.add(tasks.get("Task_Act0").getKey());

        Set<AxArtifactKey> p0ReferenceKeySet0 = new TreeSet<AxArtifactKey>();
        p0ReferenceKeySet0.add(policy0ContextAlbum.getKey());
        p0ReferenceKeySet0.add(globalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet1 = new TreeSet<AxArtifactKey>();
        p0ReferenceKeySet1.add(policy1ContextAlbum.getKey());
        p0ReferenceKeySet1.add(globalContextAlbum.getKey());
        p0ReferenceKeySet1.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet2 = new TreeSet<AxArtifactKey>();
        p0ReferenceKeySet2.add(policy0ContextAlbum.getKey());
        p0ReferenceKeySet2.add(globalContextAlbum.getKey());
        p0ReferenceKeySet2.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p0ReferenceKeySet3 = new TreeSet<AxArtifactKey>();
        p0ReferenceKeySet3.add(globalContextAlbum.getKey());

        List<Set<AxArtifactKey>> p0ReferenceKeySetList = new ArrayList<Set<AxArtifactKey>>();
        p0ReferenceKeySetList.add(p0ReferenceKeySet0);
        p0ReferenceKeySetList.add(p0ReferenceKeySet1);
        p0ReferenceKeySetList.add(p0ReferenceKeySet2);
        p0ReferenceKeySetList.add(p0ReferenceKeySet3);

        AxPolicy policy0 = new AxPolicy(new AxArtifactKey("Policy0", "0.0.1"));
        policy0.setTemplate("MEDA");
        policy0.setStateMap(getStateMap(policy0.getKey(), p0InEventList, p0OutEventList, p0ReferenceKeySetList,
                axLogicExecutorType, p0defaultTaskList, taskReferenceList));
        policy0.setFirstState(policy0.getStateMap().get("Match").getKey().getLocalName());

        Set<AxArtifactKey> p1ReferenceKeySet0 = new TreeSet<AxArtifactKey>();
        p1ReferenceKeySet0.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet0.add(globalContextAlbum.getKey());
        p1ReferenceKeySet0.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet1 = new TreeSet<AxArtifactKey>();
        p1ReferenceKeySet1.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet1.add(globalContextAlbum.getKey());
        p1ReferenceKeySet1.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet2 = new TreeSet<AxArtifactKey>();
        p1ReferenceKeySet2.add(policy1ContextAlbum.getKey());
        p1ReferenceKeySet2.add(globalContextAlbum.getKey());
        p1ReferenceKeySet2.add(externalContextAlbum.getKey());

        Set<AxArtifactKey> p1ReferenceKeySet3 = new TreeSet<AxArtifactKey>();
        p1ReferenceKeySet3.add(globalContextAlbum.getKey());

        List<Set<AxArtifactKey>> p1ReferenceKeySetList = new ArrayList<Set<AxArtifactKey>>();
        p1ReferenceKeySetList.add(p1ReferenceKeySet0);
        p1ReferenceKeySetList.add(p1ReferenceKeySet1);
        p1ReferenceKeySetList.add(p1ReferenceKeySet2);
        p1ReferenceKeySetList.add(p1ReferenceKeySet3);

        AxPolicy policy1 = new AxPolicy(new AxArtifactKey("Policy1", "0.0.1"));
        policy1.setTemplate("MEDA");
        policy1.setStateMap(getStateMap(policy1.getKey(), p1InEventList, p1OutEventList, p1ReferenceKeySetList,
                axLogicExecutorType, p1defaultTaskList, taskReferenceList));
        policy1.setFirstState(policy1.getStateMap().get("Match").getKey().getLocalName());

        AxPolicies policies = new AxPolicies(new AxArtifactKey("Policies", "0.0.1"));
        policies.getPolicyMap().put(policy0.getKey(), policy0);
        policies.getPolicyMap().put(policy1.getKey(), policy1);

        AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInformation", "0.0.1"));
        AxPolicyModel policyModel =
                new AxPolicyModel(new AxArtifactKey("SamplePolicyModel" + axLogicExecutorType, "0.0.1"));
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
        AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName())
                .setDefaultLogic("DefaultState_Logic");

        AxState actState = new AxState(new AxReferenceKey(policyKey, "Act"));
        actState.setTrigger(inEventKeyList.get(3));
        AxStateOutput act2Out =
                new AxStateOutput(actState.getKey(), AxReferenceKey.getNullKey(), outEventKeyList.get(3));
        actState.getStateOutputs().put(act2Out.getKey().getLocalName(), act2Out);
        actState.setContextAlbumReferences(referenceKeySetList.get(3));
        actState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(actState.getKey(), "TaskSelectionLigic", axLogicExecutorType, logicReader));
        actState.setDefaultTask(defaultTaskList.get(3));
        for (AxArtifactKey taskKey : taskKeySetList.get(3)) {
            actState.getTaskReferences().put(taskKey, new AxStateTaskReference(actState.getKey(), taskKey,
                    AxStateTaskOutputType.DIRECT, act2Out.getKey()));
        }

        AxState decideState = new AxState(new AxReferenceKey(policyKey, "Decide"));
        decideState.setTrigger(inEventKeyList.get(2));
        AxStateOutput decide2Act = new AxStateOutput(decideState.getKey(), actState.getKey(), outEventKeyList.get(2));
        decideState.getStateOutputs().put(decide2Act.getKey().getLocalName(), decide2Act);
        decideState.setContextAlbumReferences(referenceKeySetList.get(2));
        decideState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(decideState.getKey(), "TaskSelectionLigic", axLogicExecutorType, logicReader));
        decideState.setDefaultTask(defaultTaskList.get(2));
        for (AxArtifactKey taskKey : taskKeySetList.get(2)) {
            decideState.getTaskReferences().put(taskKey, new AxStateTaskReference(decideState.getKey(), taskKey,
                    AxStateTaskOutputType.DIRECT, decide2Act.getKey()));
        }

        AxState establishState = new AxState(new AxReferenceKey(policyKey, "Establish"));
        establishState.setTrigger(inEventKeyList.get(1));
        AxStateOutput establish2Decide =
                new AxStateOutput(establishState.getKey(), decideState.getKey(), outEventKeyList.get(1));
        establishState.getStateOutputs().put(establish2Decide.getKey().getLocalName(), establish2Decide);
        establishState.setContextAlbumReferences(referenceKeySetList.get(1));
        establishState.setTaskSelectionLogic(new AxTaskSelectionLogic(establishState.getKey(), "TaskSelectionLigic",
                axLogicExecutorType, logicReader));
        establishState.setDefaultTask(defaultTaskList.get(1));
        for (AxArtifactKey taskKey : taskKeySetList.get(1)) {
            establishState.getTaskReferences().put(taskKey, new AxStateTaskReference(establishState.getKey(), taskKey,
                    AxStateTaskOutputType.DIRECT, establish2Decide.getKey()));
        }

        AxState matchState = new AxState(new AxReferenceKey(policyKey, "Match"));
        matchState.setTrigger(inEventKeyList.get(0));
        AxStateOutput match2Establish =
                new AxStateOutput(matchState.getKey(), establishState.getKey(), outEventKeyList.get(0));
        matchState.getStateOutputs().put(match2Establish.getKey().getLocalName(), match2Establish);
        matchState.setContextAlbumReferences(referenceKeySetList.get(0));
        matchState.setTaskSelectionLogic(
                new AxTaskSelectionLogic(matchState.getKey(), "TaskSelectionLigic", axLogicExecutorType, logicReader));
        matchState.setDefaultTask(defaultTaskList.get(0));
        for (AxArtifactKey taskKey : taskKeySetList.get(0)) {
            matchState.getTaskReferences().put(taskKey, new AxStateTaskReference(matchState.getKey(), taskKey,
                    AxStateTaskOutputType.DIRECT, match2Establish.getKey()));
        }

        Map<String, AxState> stateMap = new TreeMap<String, AxState>();
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
        AxLogicReader logicReader = new PolicyLogicReader().setLogicPackage(this.getClass().getPackage().getName())
                .setDefaultLogic("DefaultTask_Logic");

        AxTask testTask0 = new AxTask(new AxArtifactKey("Task_" + state + "0", "0.0.1"));
        testTask0.duplicateInputFields(inputFields);
        testTask0.duplicateOutputFields(outputFields);
        AxTaskParameter parameter00 =
                new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), "Parameter0"), "DefaultValue0");
        AxTaskParameter parameter01 =
                new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), "Parameter1"), "DefaultValue1");
        AxTaskParameter parameter02 =
                new AxTaskParameter(new AxReferenceKey(testTask0.getKey(), "Parameter2"), "DefaultValue2");
        testTask0.getTaskParameters().put(parameter00.getKey().getLocalName(), parameter00);
        testTask0.getTaskParameters().put(parameter01.getKey().getLocalName(), parameter01);
        testTask0.getTaskParameters().put(parameter02.getKey().getLocalName(), parameter02);
        testTask0.setContextAlbumReferences(referenceKeySetList.get(0));
        testTask0.setTaskLogic(getTaskLogic(testTask0, logicReader, axLogicExecutorType, state, "2"));

        AxTask testTask1 = new AxTask(new AxArtifactKey("Task_" + state + "1", "0.0.1"));
        testTask1.duplicateInputFields(inputFields);
        testTask1.duplicateOutputFields(outputFields);
        AxTaskParameter parameter10 =
                new AxTaskParameter(new AxReferenceKey(testTask1.getKey(), "Parameter0"), "DefaultValue0");
        AxTaskParameter parameter11 =
                new AxTaskParameter(new AxReferenceKey(testTask1.getKey(), "Parameter1"), "DefaultValue1");
        testTask1.getTaskParameters().put(parameter10.getKey().getLocalName(), parameter10);
        testTask1.getTaskParameters().put(parameter11.getKey().getLocalName(), parameter11);
        testTask1.setContextAlbumReferences(referenceKeySetList.get(1));
        testTask1.setTaskLogic(getTaskLogic(testTask1, logicReader, axLogicExecutorType, state, "3"));

        AxTask testTask2 = new AxTask(new AxArtifactKey("Task_" + state + "2", "0.0.1"));
        testTask2.duplicateInputFields(inputFields);
        testTask2.duplicateOutputFields(outputFields);
        AxTaskParameter parameter20 =
                new AxTaskParameter(new AxReferenceKey(testTask2.getKey(), "Parameter0"), "DefaultValue0");
        testTask2.getTaskParameters().put(parameter20.getKey().getLocalName(), parameter20);
        testTask2.setContextAlbumReferences(referenceKeySetList.get(2));
        testTask2.setTaskLogic(getTaskLogic(testTask2, logicReader, axLogicExecutorType, state, "0"));

        AxTask testTask3 = new AxTask(new AxArtifactKey("Task_" + state + "3", "0.0.1"));
        testTask3.duplicateInputFields(inputFields);
        testTask3.duplicateOutputFields(outputFields);
        AxTaskParameter parameter30 =
                new AxTaskParameter(new AxReferenceKey(testTask3.getKey(), "Parameter0"), "DefaultValue0");
        testTask3.getTaskParameters().put(parameter30.getKey().getLocalName(), parameter30);
        testTask3.setContextAlbumReferences(referenceKeySetList.get(THIRD_ENTRY));
        testTask3.setTaskLogic(getTaskLogic(testTask3, logicReader, axLogicExecutorType, state, "1"));

        Map<AxArtifactKey, AxTask> taskMap = new TreeMap<AxArtifactKey, AxTask>();
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
        AxTaskLogic axLogic =
                new AxTaskLogic(new AxReferenceKey(task.getKey(), "_TaskLogic"), logicFlavour, logicReader);

        axLogic.setLogic(axLogic.getLogic().replaceAll("<STATE_NAME>", stateName)
                .replaceAll("<TASK_NAME>", task.getKey().getName()).replaceAll("<RANDOM_BYTE_VALUE>", caseToUse));

        return axLogic;
    }
}
