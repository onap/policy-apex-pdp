/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.executor.engine;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.testsuites.integration.common.model.SampleDomainModelFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TestApexEngine {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestApexEngine.class);

    /**
     * Instantiates a new test apex engine.
     *
     * @param axLogicExecutorType the type of logic executor to use to construct the sample policy model for this test
     * @throws ApexException the apex exception
     */
    public TestApexEngine(final String axLogicExecutorType) throws ApexException {
        logger.debug("Running TestApexEngine test for + {}logic . . .", axLogicExecutorType);

        final AxPolicyModel apexPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel(axLogicExecutorType);
        assertNotNull(apexPolicyModel);
        final AxArtifactKey key = new AxArtifactKey("TestApexEngine", "0.0.1");

        final ApexEngine apexEngine = new ApexEngineFactory().createApexEngine(key);
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel, false);
        apexEngine.start();

        for (final AxEvent axEvent : apexPolicyModel.getEvents().getEventMap().values()) {
            final EnEvent event = apexEngine.createEvent(axEvent.getKey());

            final Date aDate = new Date(1433453067123L);
            final Map<String, Object> eventDataMap = new HashMap<>();
            eventDataMap.put("TestSlogan", "This is a test slogan for event " + event.getName());
            eventDataMap.put("TestMatchCase", (byte) 123);
            eventDataMap.put("TestTimestamp", aDate.getTime());
            eventDataMap.put("TestTemperature", 34.5445667);

            event.putAll(eventDataMap);

            apexEngine.handleEvent(event);
        }

        EnEvent result = listener.getResult(false);
        logger.debug("result 1 is:{}", result);
        checkResult(result);
        result = listener.getResult(false);
        logger.debug("result 2 is:{}", result);
        checkResult(result);

        final Map<AxArtifactKey, Map<String, Object>> apexContext = apexEngine.getEngineContext();
        assertNotNull(apexContext);
        apexEngine.stop();

        await().atMost(3L, TimeUnit.SECONDS).until(() -> AxEngineState.STOPPED.equals(apexEngine.getState()));
    }

    /**
     * Check result.
     *
     * @param result the result
     */
    private void checkResult(final EnEvent result) {
        if (result.getExceptionMessage() == null) {
            assertTrue(result.getName().equals("Event0004") || result.getName().equals("Event0104"));

            assertTrue(((String) result.get("TestSlogan")).startsWith("This is a test slogan for event "));
            assertTrue(((String) result.get("TestSlogan")).contains(result.getName().substring(0, 8)));

            assertEquals((byte) 123, result.get("TestMatchCase"));
            assertEquals(34.5445667, result.get("TestTemperature"));
            assertTrue((Byte) result.get("TestMatchCaseSelected") >= 0
                && (Byte) result.get("TestMatchCaseSelected") <= 4);
            assertTrue((Byte) result.get("TestEstablishCaseSelected") >= 0
                && (Byte) result.get("TestEstablishCaseSelected") <= 4);
            assertTrue((Byte) result.get("TestDecideCaseSelected") >= 0
                && (Byte) result.get("TestDecideCaseSelected") <= 4);
            assertTrue((Byte) result.get("TestActCaseSelected") >= 0
                && (Byte) result.get("TestActCaseSelected") <= 4);
        } else {
            // assertTrue(result.getName().equals("Event0001") || result.getName().equals("Event0104"));

            assertTrue(((String) result.get("TestSlogan")).startsWith("This is a test slogan for event "));
            assertTrue(((String) result.get("TestSlogan")).contains(result.getName().substring(0, 8)));

            assertEquals((byte) 123, result.get("TestMatchCase"));
            assertEquals(34.5445667, result.get("TestTemperature"));
        }
    }
}
