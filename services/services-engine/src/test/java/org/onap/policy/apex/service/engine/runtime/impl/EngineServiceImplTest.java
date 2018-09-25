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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the engine service implementation.
 */
public class EngineServiceImplTest {

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
        ModelService.clear();
    }

    @Test
    public void testEngineServiceImplSanity() throws ApexException {
        try {
            EngineServiceImpl.create(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine service configuration parameters are null", apEx.getMessage());
        }

        EngineServiceParameters config = new EngineServiceParameters();
        config.setInstanceCount(0);

        try {
            EngineServiceImpl.create(config);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Invalid engine service configuration parameters:", apEx.getMessage().substring(0, 48));
        }

        config.setId(123);
        config.setEngineKey(new AxArtifactKey("Engine", "0.0.1"));
        config.setInstanceCount(1);

        EngineServiceImpl esImpl = EngineServiceImpl.create(config);
        assertEquals("Engine:0.0.1", esImpl.getKey().getId());

        esImpl.registerActionListener(null, null);
        esImpl.registerActionListener("DummyListener", null);
        esImpl.registerActionListener(null, new DummyApexEventListener());

        esImpl.registerActionListener("DummyListener", new DummyApexEventListener());
        esImpl.deregisterActionListener(null);
        esImpl.deregisterActionListener("DummyListener");

        assertEquals(esImpl, esImpl.getEngineServiceEventInterface());
        assertEquals(1, esImpl.getEngineKeys().size());

        assertNull(esImpl.getApexModelKey());

        try {
            esImpl.getRuntimeInfo(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.getRuntimeInfo(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine with key DummyKey:0.0.1 not found in engine service", apEx.getMessage());
        }

        String runtimeInfo = esImpl.getRuntimeInfo(esImpl.getEngineKeys().iterator().next());
        assertEquals("{\n  \"TimeStamp\":", runtimeInfo.substring(0, 16));

        assertEquals(AxEngineState.STOPPED, esImpl.getState());

        try {
            esImpl.getStatus(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.getStatus(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine with key DummyKey:0.0.1 not found in engine service", apEx.getMessage());
        }

        String status = esImpl.getStatus(esImpl.getEngineKeys().iterator().next());
        assertEquals("{\n   \"apexEngineModel\" :", status.substring(0, 24));

        assertFalse(esImpl.isStarted());
        assertFalse(esImpl.isStarted(null));
        assertFalse(esImpl.isStarted(new AxArtifactKey("DummyKey", "0.0.1")));
        assertFalse(esImpl.isStarted(esImpl.getEngineKeys().iterator().next()));
        assertTrue(esImpl.isStopped());
        assertTrue(esImpl.isStopped(null));
        assertTrue(esImpl.isStopped(new AxArtifactKey("DummyKey", "0.0.1")));
        assertTrue(esImpl.isStopped(esImpl.getEngineKeys().iterator().next()));

        try {
            esImpl.start(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.start(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine with key DummyKey:0.0.1 not found in engine service", apEx.getMessage());
        }

        try {
            esImpl.start(esImpl.getEngineKeys().iterator().next());
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("start()<-Engine-0:0.0.1,STOPPED,  cannot start engine, "
                            + "engine has not been initialized, its model is not loaded", apEx.getMessage());
        }

        try {
            esImpl.startAll();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("start()<-Engine-0:0.0.1,STOPPED,  cannot start engine, "
                            + "engine has not been initialized, its model is not loaded", apEx.getMessage());
        }

        try {
            esImpl.stop(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.stop(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine with key DummyKey:0.0.1 not found in engine service", apEx.getMessage());
        }

        try {
            esImpl.stop(esImpl.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.stop();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.sendEvent(null);
        } catch (Exception apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.sendEvent(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget"));
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        esImpl.startPeriodicEvents(100000);

        try {
            esImpl.startPeriodicEvents(100000);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Peiodic event geneation already running on engine Engine:0.0.1, ApexPeriodicEventGenerator "
                            + "[period=100000, firstEventTime=0, lastEventTime=0, eventCount=0]", apEx.getMessage());
        }

        esImpl.stopPeriodicEvents();
        try {
            esImpl.stopPeriodicEvents();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Peiodic event geneation not running on engine Engine:0.0.1", apEx.getMessage());
        }

        try {
            esImpl.clear(null);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.clear(new AxArtifactKey("DummyKey", "0.0.1"));
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine with key DummyKey:0.0.1 not found in engine service", apEx.getMessage());
        }

        try {
            esImpl.clear(esImpl.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.clear();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.updateModel(null, (String) null, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (String) null, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("model for updating engine service with key DummyKey:0.0.1 is empty", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "", true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("model for updating engine service with key DummyKey:0.0.1 is empty", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "I am not an Apex model", true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("failed to unmarshal the apex model on engine service DummyKey:0.0.1", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModelString, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine service key DummyKey:0.0.1 does not match the keyEngine:0.0.1 of this engine service",
                            apEx.getMessage());
        }

        try {
            esImpl.updateModel(null, simpleModelString, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.updateModel(null, (AxPolicyModel) null, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine key must be specified and may not be null", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (AxPolicyModel) null, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("model for updating on engine service with key DummyKey:0.0.1 is null", apEx.getMessage());
        }

        try {
            esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModel, true);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("engine service key DummyKey:0.0.1 does not match the keyEngine:0.0.1 of this engine service",
                            apEx.getMessage());
        }

    }

    @Test
    public void testApexImplModelWIthModel() throws ApexException {
        EngineServiceParameters config = new EngineServiceParameters();
        config.setId(123);
        config.setEngineKey(new AxArtifactKey("Engine", "0.0.1"));
        config.setInstanceCount(1);

        EngineServiceImpl esImpl = EngineServiceImpl.create(config);
        assertEquals("Engine:0.0.1", esImpl.getKey().getId());

        try {
            esImpl.updateModel(config.getEngineKey(), simpleModelString, false);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.updateModel(config.getEngineKey(), mfpModelString, false);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex model update failed, supplied model with key \"MyFirstPolicyModel:0.0.1\" is not a "
                            + "compatible model update "
                            + "from the existing engine model with key \"SamplePolicyModelJAVASCRIPT:0.0.1\"",
                            apEx.getMessage());
        }

        try {
            esImpl.updateModel(config.getEngineKey(), mfpModelString, true);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.updateModel(config.getEngineKey(), simpleModelString, true);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        String runtimeInfo = esImpl.getRuntimeInfo(esImpl.getEngineKeys().iterator().next());
        assertEquals("{\n  \"TimeStamp\":", runtimeInfo.substring(0, 16));

        assertEquals(AxEngineState.EXECUTING, esImpl.getState());

        String status = esImpl.getStatus(esImpl.getEngineKeys().iterator().next());
        assertEquals("{\n   \"apexEngineModel\" :", status.substring(0, 24));

        assertTrue(esImpl.isStarted());
        assertTrue(esImpl.isStarted(esImpl.getEngineKeys().iterator().next()));
        assertFalse(esImpl.isStopped());
        assertFalse(esImpl.isStopped(esImpl.getEngineKeys().iterator().next()));

        try {
            esImpl.start(esImpl.getEngineKeys().iterator().next());
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex engine for engine key Engine-0:0.0.1 is already running with state READY",
                            apEx.getMessage());
        }

        try {
            esImpl.startAll();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex engine for engine key Engine-0:0.0.1 is already running with state READY",
                            apEx.getMessage());
        }

        try {
            esImpl.stop(esImpl.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.start(esImpl.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.stop();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.startAll();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.sendEvent(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget"));
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        esImpl.startPeriodicEvents(100000);
        esImpl.stop();
        esImpl.startAll();
        esImpl.stopPeriodicEvents();

        esImpl.startPeriodicEvents(100000);
        try {
            esImpl.startPeriodicEvents(100000);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Peiodic event geneation already running on engine Engine:0.0.1, ApexPeriodicEventGenerator "
                            + "[period=100000, firstEventTime=0, lastEventTime=0, eventCount=0]", apEx.getMessage());
        }

        esImpl.stopPeriodicEvents();
        try {
            esImpl.stopPeriodicEvents();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Peiodic event geneation not running on engine Engine:0.0.1", apEx.getMessage());
        }

        try {
            esImpl.clear(esImpl.getEngineKeys().iterator().next());
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.clear();
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }
    }
}
