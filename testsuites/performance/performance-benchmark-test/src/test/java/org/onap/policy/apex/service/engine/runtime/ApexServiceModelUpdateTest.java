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

package org.onap.policy.apex.service.engine.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.engine.utils.Utils;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.test.common.model.SampleDomainModelFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;


/**
 * The Class ApexServiceTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexServiceModelUpdateTest {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexServiceModelUpdateTest.class);

    private final AxArtifactKey engineServiceKey = new AxArtifactKey("Machine-1_process-1_engine-1", "0.0.0");
    private final EngineServiceParameters parameters = new EngineServiceParameters();
    private EngineService service = null;
    private TestListener listener = null;
    private int actionEventsReceived = 0;

    private AxPolicyModel apexSamplePolicyModel = null;
    private String apexSampleModelString;

    /**
     * Sets up the test by creating an engine and reading in the test policy.
     *
     * @throws ApexException if something goes wron
     * @throws IOException
     */
    @Before
    public void setUp() throws ApexException, IOException {
        // create engine with 3 threads
        parameters.setInstanceCount(3);
        parameters.setName(engineServiceKey.getName());
        parameters.setVersion(engineServiceKey.getVersion());
        parameters.setId(100);
        parameters.getEngineParameters().getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        service = EngineServiceImpl.create(parameters);

        LOGGER.debug("Running TestApexEngine. . .");

        apexSamplePolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(apexSamplePolicyModel);

        apexSampleModelString = Utils.getModelString(apexSamplePolicyModel);

        // create engine
        listener = new TestListener();
        service.registerActionListener("MyListener", listener);
    }

    /**
     * Tear down the the test infrastructure.
     *
     * @throws ApexException if there is an error
     */
    @After
    public void tearDown() throws Exception {
        if (service != null) {
            service.stop();
        }
        service = null;
    }

    /**
     * Test start with no model.
     */
    @Test
    public void testNoModelStart() {
        try {
            service.startAll();
            fail("Engine should not start with no model");
        } catch (final Exception e) {
            e.printStackTrace();
            assertEquals("start()<-Machine-1_process-1_engine-1-0:0.0.0,STOPPED,  cannot start engine, "
                    + "engine has not been initialized, its model is not loaded", e.getMessage());
        }
    }

    /**
     * Test model update with string model without force.
     *
     * @throws ApexException if there is an error
     */
    @Test
    public void testModelUpdateStringNewNoForce() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexSampleModelString, false);
        service.startAll();
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());
    }

    /**
     * Test model update with string model with force.
     *
     * @throws ApexException if there is an error
     */
    @Test
    public void testModelUpdateStringNewForce() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexSampleModelString, true);
        service.startAll();
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());
    }

    /**
     * Test model update with a new string model without force.
     *
     * @throws ApexException if there is an error
     */
    @Test
    public void testModelUpdateStringNewNewNoForce() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexSampleModelString, false);
        service.startAll();
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());

        sendEvents();

        service.updateModel(parameters.getEngineKey(), apexSampleModelString, false);
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());

        sendEvents();
    }

    /**
     * Test incompatible model update with a model object without force.
     *
     * @throws ApexException if there is an error
     */
    @Test
    public void testModelUpdateIncoNoForce() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexSamplePolicyModel, false);
        service.startAll();
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());

        // Different model name, incompatible
        final AxPolicyModel incoPolicyModel0 = new AxPolicyModel(apexSamplePolicyModel);
        incoPolicyModel0.getKey().setName("INCOMPATIBLE");

        try {
            service.updateModel(parameters.getEngineKey(), incoPolicyModel0, false);
            fail("model update should fail on incompatible model without force being true");
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            assertEquals(
                    "apex model update failed, supplied model with key \"INCOMPATIBLE:0.0.1\" is not a compatible "
                            + "model update from the existing engine model with key \"SamplePolicyModelMVEL:0.0.1\"",
                    e.getMessage());
        }

        // Still on old model
        sendEvents();

        // Different major version, incompatible
        final AxPolicyModel incoPolicyModel1 = new AxPolicyModel(apexSamplePolicyModel);
        incoPolicyModel1.getKey().setVersion("1.0.1");

        try {
            service.updateModel(parameters.getEngineKey(), incoPolicyModel1, false);
            fail("model update should fail on incompatible model without force being true");
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            assertEquals(
                    "apex model update failed, supplied model with key \"SamplePolicyModelMVEL:1.0.1\" is not a compatible "
                            + "model update from the existing engine model with key \"SamplePolicyModelMVEL:0.0.1\"",
                    e.getMessage());
        }

        // Still on old model
        sendEvents();

        // Different minor version, compatible
        final AxPolicyModel coPolicyModel0 = new AxPolicyModel(apexSamplePolicyModel);
        coPolicyModel0.getKey().setVersion("0.1.0");
        service.updateModel(parameters.getEngineKey(), coPolicyModel0, false);

        // On new compatible model
        sendEvents();

        // Different patch version, compatible
        final AxPolicyModel coPolicyModel1 = new AxPolicyModel(apexSamplePolicyModel);
        coPolicyModel1.getKey().setVersion("0.0.2");
        service.updateModel(parameters.getEngineKey(), coPolicyModel1, false);

        // On new compatible model
        sendEvents();

    }

    /**
     * Test incompatible model update with a model object with force.
     *
     * @throws ApexException if there is an error
     */
    @Test
    public void testModelUpdateIncoForce() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexSamplePolicyModel, false);
        service.startAll();
        assertEquals(apexSamplePolicyModel.getKey(), ModelService.getModel(AxPolicyModel.class).getKey());

        // Different model name, incompatible
        final AxPolicyModel incoPolicyModel0 = new AxPolicyModel(apexSamplePolicyModel);
        incoPolicyModel0.getKey().setName("INCOMPATIBLE");
        service.updateModel(parameters.getEngineKey(), incoPolicyModel0, true);

        // On updated model
        sendEvents();

        // Different major version, incompatible
        final AxPolicyModel incoPolicyModel1 = new AxPolicyModel(apexSamplePolicyModel);
        incoPolicyModel1.getKey().setVersion("1.0.1");
        service.updateModel(parameters.getEngineKey(), incoPolicyModel1, true);

        // On updated model
        sendEvents();

        // Different minor version, compatible
        final AxPolicyModel coPolicyModel0 = new AxPolicyModel(apexSamplePolicyModel);
        coPolicyModel0.getKey().setVersion("0.1.0");
        service.updateModel(parameters.getEngineKey(), coPolicyModel0, true);

        // On new compatible model
        sendEvents();

        // Different patch version, compatible
        final AxPolicyModel coPolicyModel1 = new AxPolicyModel(apexSamplePolicyModel);
        coPolicyModel1.getKey().setVersion("0.0.2");
        service.updateModel(parameters.getEngineKey(), coPolicyModel1, true);

        // On new compatible model
        sendEvents();

    }

    /**
     * Utility method to send some events into the test engine.
     * 
     * @throws ApexEventException if there is an error
     */
    private void sendEvents() throws ApexEventException {
        final EngineServiceEventInterface engineServiceEventInterface = service.getEngineServiceEventInterface();

        // Send some events
        final Date testStartTime = new Date();
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", (byte) 123);
        eventDataMap.put("TestTimestamp", testStartTime.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final ApexEvent event =
                new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event);

        final ApexEvent event2 =
                new ApexEvent("Event0100", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event2.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event2);

        // Wait for results
        while (actionEventsReceived < 2) {
            ThreadUtilities.sleep(100);
        }
        ThreadUtilities.sleep(500);
    }

    /**
     * The listener interface for receiving test events. The class that is interested in processing
     * a test event implements this interface, and the object created with that class is registered
     * with a component using the component's <code>addTestListener</code> method. When the test
     * event occurs, that object's appropriate method is invoked.
     *
     * @see TestEvent
     */
    private final class TestListener implements ApexEventListener {

        /*
         * (non-Javadoc)
         *
         * @see
         * org.onap.policy.apex.service.engine.runtime.ApexEventListener#onApexEvent(org.onap.policy
         * .apex.service.engine.event.ApexEvent)
         */
        @Override
        public synchronized void onApexEvent(final ApexEvent event) {
            LOGGER.debug("result 1 is:" + event);
            checkResult(event);
            actionEventsReceived++;

            final Date testStartTime = new Date((Long) event.get("TestTimestamp"));
            final Date testEndTime = new Date();

            LOGGER.info("policy execution time: " + (testEndTime.getTime() - testStartTime.getTime()) + "ms");
        }

        /**
         * Check result.
         *
         * @param result the result
         */
        private void checkResult(final ApexEvent result) {
            assertTrue(result.getName().startsWith("Event0004") || result.getName().startsWith("Event0104"));

            assertTrue(result.get("TestSlogan").equals("This is a test slogan"));
            assertTrue(result.get("TestMatchCase").equals(new Byte((byte) 123)));
            assertTrue(result.get("TestTemperature").equals(34.5445667));
            assertTrue(((byte) result.get("TestMatchCaseSelected")) >= 0
                    && ((byte) result.get("TestMatchCaseSelected") <= 3));
            assertTrue(((byte) result.get("TestEstablishCaseSelected")) >= 0
                    && ((byte) result.get("TestEstablishCaseSelected") <= 3));
            assertTrue(((byte) result.get("TestDecideCaseSelected")) >= 0
                    && ((byte) result.get("TestDecideCaseSelected") <= 3));
            assertTrue(
                    ((byte) result.get("TestActCaseSelected")) >= 0 && ((byte) result.get("TestActCaseSelected") <= 3));
        }
    }
}
