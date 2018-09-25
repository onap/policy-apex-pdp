/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.runtime.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameterConstants;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the engine worker class.
 */
public class EngineWorkerTest {
    private final ApplicationThreadFactory atFactory = new ApplicationThreadFactory("apex-engine-service", 512);

    private static String simpleModelString;
    private static String mfpModelString;
    private static AxPolicyModel simpleModel;

    /**
     * Read the models into strings.
     * 
     * @throws IOException on model reading errors
     * @throws ApexModelException on model reading exceptions
     */
    @BeforeClass
    public static void readSimpleModel() throws IOException, ApexModelException {
        simpleModelString = TextFileUtils
                        .getTextFileAsString("src/test/resources/policymodels/SamplePolicyModelJAVASCRIPT.json");

        mfpModelString = TextFileUtils.getTextFileAsString("src/test/resources/policymodels/MyFirstPolicyModel.json");

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        simpleModel = modelReader.read(new ByteArrayInputStream(simpleModelString.getBytes()));
    }

    /**
     * Initialize default parameters.
     */
    @BeforeClass
    public static void initializeDefaultParameters() {
        ParameterService.clear();
        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters);

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.register(contextParameters);

        final DistributorParameters distributorParameters = new DistributorParameters();
        distributorParameters.setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.register(distributorParameters);

        final LockManagerParameters lockManagerParameters = new LockManagerParameters();
        lockManagerParameters.setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.register(lockManagerParameters);

        final PersistorParameters persistorParameters = new PersistorParameters();
        persistorParameters.setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.register(persistorParameters);

        final EngineParameters engineParameters = new EngineParameters();
        engineParameters.setName(EngineParameterConstants.MAIN_GROUP_NAME);
        ExecutorParameters jsExecutorParameters = new ExecutorParameters();
        jsExecutorParameters.setName("JAVASCRIPT");
        jsExecutorParameters.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.service.engine.runtime.impl.DummyTse");
        jsExecutorParameters.setTaskExecutorPluginClass("org.onap.policy.apex.service.engine.runtime.impl.DummyTe");
        jsExecutorParameters.setStateFinalizerExecutorPluginClass(
                        "org.onap.policy.apex.service.engine.runtime.impl.DummySfe");
        engineParameters.getExecutorParameterMap().put("JAVASCRIPT", jsExecutorParameters);
        ExecutorParameters mvvelExecutorParameters = new ExecutorParameters();
        mvvelExecutorParameters.setName("MVEL");
        mvvelExecutorParameters.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.service.engine.runtime.impl.DummyTse");
        mvvelExecutorParameters.setTaskExecutorPluginClass("org.onap.policy.apex.service.engine.runtime.impl.DummyTe");
        mvvelExecutorParameters.setStateFinalizerExecutorPluginClass(
                        "org.onap.policy.apex.service.engine.runtime.impl.DummySfe");
        engineParameters.getExecutorParameterMap().put("MVEL", jsExecutorParameters);
        ParameterService.register(engineParameters);
    }

    /**
     * Teardown default parameters.
     */
    @AfterClass
    public static void teardownDefaultParameters() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }
    
    @After
    public void cleardownTest() {
        ModelService.clear();
    }

    @Test
    public void testEngineWorker() {
        BlockingQueue<ApexEvent> eventQueue = new LinkedBlockingQueue<>();

        EngineWorker worker = new EngineWorker(new AxArtifactKey("Worker", "0.0.1"), eventQueue, atFactory);

        worker.registerActionListener(null, null);
        worker.registerActionListener("DummyListener", null);
        worker.registerActionListener(null, new DummyApexEventListener());

        worker.registerActionListener("DummyListener", new DummyApexEventListener());
        worker.deregisterActionListener(null);
        worker.deregisterActionListener("DummyListener");

        try {
            worker.getEngineServiceEventInterface();
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("getEngineServiceEventInterface() call is not allowed on an Apex Engine Worker",
                            apEx.getMessage());
        }

        try {
            worker.startPeriodicEvents(100000);
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("startPeriodicEvents() call is not allowed on an Apex Engine Worker", apEx.getMessage());
        }

        try {
            worker.stopPeriodicEvents();
            fail("test should throw an exception");
        } catch (Exception apEx) {
            assertEquals("stopPeriodicEvents() call is not allowed on an Apex Engine Worker", apEx.getMessage());
        }

        assertEquals("Worker:0.0.1", worker.getEngineKeys().iterator().next().getId());

        assertNull(worker.getApexModelKey());

        String runtimeInfo = worker.getRuntimeInfo(worker.getEngineKeys().iterator().next());
        assertEquals("{\n  \"TimeStamp\":", runtimeInfo.substring(0, 16));

        assertEquals(AxEngineState.STOPPED, worker.getState());

        String status = worker.getStatus(worker.getEngineKeys().iterator().next());
        assertEquals("{\n   \"apexEngineModel\" :", status.substring(0, 24));

        assertFalse(worker.isStarted());
        assertFalse(worker.isStarted(null));
        assertFalse(worker.isStarted(new AxArtifactKey("DummyKey", "0.0.1")));
        assertFalse(worker.isStarted(worker.getEngineKeys().iterator().next()));
        assertTrue(worker.isStopped());
        assertTrue(worker.isStopped(null));
        assertTrue(worker.isStopped(new AxArtifactKey("DummyKey", "0.0.1")));
        assertTrue(worker.isStopped(worker.getEngineKeys().iterator().next()));

        try {
            worker.start(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }

        try {
            worker.start(worker.getEngineKeys().iterator().next());
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("start()<-Worker:0.0.1,STOPPED,  cannot start engine, engine has not been initialized, "
                            + "its model is not loaded", apEx.getMessage());
        }

        try {
            worker.startAll();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("start()<-Worker:0.0.1,STOPPED,  cannot start engine, "
                            + "engine has not been initialized, its model is not loaded", apEx.getMessage());
        }

        try {
            worker.stop(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }

        try {
            worker.stop(worker.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.stop();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.clear(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }

        try {
            worker.clear(worker.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.clear();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "", true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("failed to unmarshal the apex model on engine DummyKey:0.0.1", apEx.getMessage());
        }

        try {
            worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "I am not an Apex model", true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("failed to unmarshal the apex model on engine DummyKey:0.0.1", apEx.getMessage());
        }

        try {
            worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModelString, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }

        try {
            worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (AxPolicyModel) null, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }

        try {
            worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModel, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine",
                            apEx.getMessage());
        }
    }
    

    @Test
    public void testApexImplModelWIthModel() throws ApexException {
        BlockingQueue<ApexEvent> eventQueue = new LinkedBlockingQueue<>();

        EngineWorker worker = new EngineWorker(new AxArtifactKey("Worker", "0.0.1"), eventQueue, atFactory);
        assertEquals("Worker:0.0.1", worker.getKey().getId());

        try {
            worker.updateModel(worker.getKey(), simpleModelString, false);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }
        
        eventQueue.add(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget"));

        try {
            worker.updateModel(worker.getKey(), mfpModelString, false);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex model update failed, supplied model with key \"MyFirstPolicyModel:0.0.1\" is not a "
                            + "compatible model update "
                            + "from the existing engine model with key \"SamplePolicyModelJAVASCRIPT:0.0.1\"",
                            apEx.getMessage());
        }

        try {
            worker.updateModel(worker.getKey(), mfpModelString, true);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.updateModel(worker.getKey(), simpleModelString, true);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        String runtimeInfo = worker.getRuntimeInfo(worker.getEngineKeys().iterator().next());
        assertEquals("{\n  \"TimeStamp\":", runtimeInfo.substring(0, 16));

        assertEquals(AxEngineState.STOPPED, worker.getState());
        worker.startAll();
        
        assertEquals(AxEngineState.READY, worker.getState());

        String status = worker.getStatus(worker.getEngineKeys().iterator().next());
        assertEquals("{\n   \"apexEngineModel\" :", status.substring(0, 24));

        assertTrue(worker.isStarted());
        assertTrue(worker.isStarted(worker.getEngineKeys().iterator().next()));
        assertFalse(worker.isStopped());
        assertFalse(worker.isStopped(worker.getEngineKeys().iterator().next()));

        try {
            worker.start(worker.getEngineKeys().iterator().next());
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex engine for engine key Worker:0.0.1 is already running with state READY",
                            apEx.getMessage());
        }

        try {
            worker.startAll();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex engine for engine key Worker:0.0.1 is already running with state READY",
                            apEx.getMessage());
        }

        try {
            worker.stop(worker.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.start(worker.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.stop();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.startAll();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        worker.stop();
        worker.startAll();

        try {
            worker.clear(worker.getEngineKeys().iterator().next());
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("clear()<-Worker:0.0.1,READY, cannot clear engine, engine is not stopped", apEx.getMessage());
        }

        try {
            worker.stop(worker.getEngineKeys().iterator().next());
            worker.clear(worker.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            worker.clear();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }
        
        assertNotNull(worker.getApexModelKey());
    }
}
