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

package org.onap.policy.apex.plugins.executor.test.script.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MVELExecutorParameters;
import org.onap.policy.apex.plugins.executor.test.script.engine.TestApexActionListener;
import org.onap.policy.apex.test.common.model.SampleDomainModelFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestApexEngine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextUpdateModel {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestContextUpdateModel.class);


    @Test
    public void testContextUpdateModel() throws ApexException, InterruptedException, IOException {
        final AxArtifactKey key = new AxArtifactKey("TestApexEngine", "0.0.1");

        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());

        final ApexEngine apexEngine = new ApexEngineFactory().createApexEngine(key);
        final TestApexActionListener listener = new TestApexActionListener("Test");
        apexEngine.addEventListener("listener", listener);

        final AxPolicyModel model1 = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(model1);
        assertEquals(2, model1.getPolicies().getPolicyMap().size());

        apexEngine.updateModel(model1);
        apexEngine.start();
        sendEvent(apexEngine, listener, "Event0000", true);
        sendEvent(apexEngine, listener, "Event0100", true);
        apexEngine.stop();

        final AxPolicyModel model2 = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(model2);
        model2.getPolicies().getPolicyMap().remove(new AxArtifactKey("Policy0", "0.0.1"));
        assertEquals(1, model2.getPolicies().getPolicyMap().size());
        apexEngine.updateModel(model2);
        apexEngine.start();
        sendEvent(apexEngine, listener, "Event0000", false);
        sendEvent(apexEngine, listener, "Event0100", true);
        apexEngine.stop();

        final AxPolicyModel model3 = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(model3);
        model3.getPolicies().getPolicyMap().remove(new AxArtifactKey("Policy1", "0.0.1"));
        assertEquals(1, model3.getPolicies().getPolicyMap().size());
        apexEngine.updateModel(model3);
        apexEngine.start();
        sendEvent(apexEngine, listener, "Event0000", true);
        sendEvent(apexEngine, listener, "Event0100", false);
        apexEngine.stop();

        final AxPolicyModel model4 = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(model4);
        assertEquals(2, model4.getPolicies().getPolicyMap().size());
        apexEngine.updateModel(model4);
        apexEngine.start();
        sendEvent(apexEngine, listener, "Event0100", true);
        sendEvent(apexEngine, listener, "Event0000", true);
        apexEngine.stop();

        apexEngine.clear();
    }

    @After
    public void testContextUpdateModelAfter() {}

    private void sendEvent(final ApexEngine apexEngine, final TestApexActionListener listener, final String eventName,
            final boolean shouldWork) throws ContextException {
        final Date aDate = new Date(1433453067123L);
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", new Byte((byte) 123));
        eventDataMap.put("TestTimestamp", aDate.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final EnEvent event0 = apexEngine.createEvent(new AxArtifactKey(eventName, "0.0.1"));
        event0.putAll(eventDataMap);
        apexEngine.handleEvent(event0);

        final EnEvent result = listener.getResult(true);
        logger.debug("result 1 is:" + result);
        checkResult(result, shouldWork);
    }

    private void checkResult(final EnEvent result, final boolean shouldWork) {
        if (!shouldWork) {
            assertNotNull(result.getExceptionMessage());
            return;
        }

        assertTrue(result.getName().equals("Event0004") || result.getName().equals("Event0104"));

        if (result.getName().equals("Event0004")) {
            assertEquals("This is a test slogan", result.get("TestSlogan"));
            assertEquals((byte) 123, result.get("TestMatchCase"));
            assertEquals(34.5445667, result.get("TestTemperature"));
            assertEquals((byte) 2, result.get("TestMatchCaseSelected"));
            assertEquals((byte) 0, result.get("TestEstablishCaseSelected"));
            assertEquals((byte) 1, result.get("TestDecideCaseSelected"));
            assertEquals((byte) 3, result.get("TestActCaseSelected"));
        } else {
            assertEquals("This is a test slogan", result.get("TestSlogan"));
            assertEquals((byte) 123, result.get("TestMatchCase"));
            assertEquals(34.5445667, result.get("TestTemperature"));
            assertEquals((byte) 1, result.get("TestMatchCaseSelected"));
            assertEquals((byte) 3, result.get("TestEstablishCaseSelected"));
            assertEquals((byte) 1, result.get("TestDecideCaseSelected"));
            assertEquals((byte) 2, result.get("TestActCaseSelected"));
        }
    }
}
