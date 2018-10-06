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

package org.onap.policy.apex.core.engine.engine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameterConstants;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.StateMachineExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the engine implementation.
 */
public class ApexEngineImplTest {
    private AxPolicyModel policyModel;
    private AxPolicyModel incompatiblePolicyModel;
    private AxPolicyModel policyModelWithStates;

    @Mock
    StateMachineHandler smHandlerMock;

    /**
     * Set up services.
     */
    @BeforeClass
    public static void setup() {
        ParameterService.register(new SchemaParameters());
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
        ParameterService.register(new EngineParameters());
    }

    /**
     * Set up mocking.
     */
    @Before
    public void initializeMocking() throws ApexException {
        MockitoAnnotations.initMocks(this);

        Mockito.doThrow(new StateMachineException("mocked state machine exception",
                        new IOException("nexted exception"))).when(smHandlerMock).execute(Mockito.anyObject());
    }

    /**
     * Create policy models.
     */
    @Before
    public void createPolicyModels() {
        AxArtifactKey modelKey = new AxArtifactKey("PolicyModel:0.0.1");
        policyModel = new AxPolicyModel(modelKey);

        AxArtifactKey schemaKey = new AxArtifactKey("Schema:0.0.1");
        AxContextSchema schema = new AxContextSchema(schemaKey, "Java", "java.lang.String");
        policyModel.getSchemas().getSchemasMap().put(schemaKey, schema);

        AxArtifactKey albumKey = new AxArtifactKey("Album:0.0.1");
        AxContextAlbum album = new AxContextAlbum(albumKey, "Policy", true, schemaKey);

        policyModel.getAlbums().getAlbumsMap().put(albumKey, album);

        AxEvents events = new AxEvents();
        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        AxEvent event = new AxEvent(eventKey, "event.name.space", "source", "target");
        events.getEventMap().put(eventKey, event);
        policyModel.setEvents(events);

        AxArtifactKey incompatibleModelKey = new AxArtifactKey("IncompatiblePolicyModel:0.0.2");
        incompatiblePolicyModel = new AxPolicyModel(incompatibleModelKey);

        AxArtifactKey incompatibleSchemaKey = new AxArtifactKey("IncompatibleSchema:0.0.1");
        AxContextSchema incompatibleSchema = new AxContextSchema(incompatibleSchemaKey, "Java", "java.lang.Integer");
        incompatiblePolicyModel.getSchemas().getSchemasMap().put(incompatibleSchemaKey, incompatibleSchema);

        AxContextAlbum incompatibleAlbum = new AxContextAlbum(albumKey, "Policy", true, incompatibleSchemaKey);
        incompatiblePolicyModel.getAlbums().getAlbumsMap().put(albumKey, incompatibleAlbum);

        AxArtifactKey modelKeyStates = new AxArtifactKey("PolicyModelStates:0.0.1");
        policyModelWithStates = new AxPolicyModel(modelKeyStates);
        policyModelWithStates.getSchemas().getSchemasMap().put(schemaKey, schema);
        policyModelWithStates.getAlbums().getAlbumsMap().put(albumKey, album);
        policyModelWithStates.setEvents(events);

        AxPolicy policy0 = new AxPolicy(new AxArtifactKey("Policy0:0.0.1"));
        AxState state0 = new AxState(new AxReferenceKey(policy0.getKey(), "state0"));
        state0.setTrigger(eventKey);
        policy0.getStateMap().put(state0.getKey().getLocalName(), state0);
        policy0.setFirstState(state0.getKey().getLocalName());

        policyModelWithStates.getPolicies().getPolicyMap().put(policy0.getKey(), policy0);

        AxPolicy policy1 = new AxPolicy(new AxArtifactKey("Policy1:0.0.1"));
        AxState state1 = new AxState(new AxReferenceKey(policy1.getKey(), "state1"));
        state1.setTrigger(eventKey);
        policy1.getStateMap().put(state1.getKey().getLocalName(), state1);
        policy1.setFirstState(state1.getKey().getLocalName());

        policyModelWithStates.getPolicies().getPolicyMap().put(policy1.getKey(), policy1);
    }

    /**
     * Clear registrations.
     */
    @AfterClass
    public static void teardown() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }

    @Test
    public void testSanity() {
        AxArtifactKey engineKey = new AxArtifactKey("Engine:0.0.1");
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        try {
            engine.start();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("start()<-Engine:0.0.1,STOPPED,  cannot start engine, "
                            + "engine has not been initialized, its model is not loaded", ae.getMessage());
        }

        try {
            engine.stop();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("stop()<-Engine:0.0.1,STOPPED, cannot stop engine, " + "engine is already stopped",
                            ae.getMessage());
        }

        assertEquals(AxEngineState.STOPPED, engine.getState());
        assertEquals(0, engine.getEngineContext().size());
        assertEquals(engineKey, engine.getEngineStatus().getKey());
        assertNull(engine.getInternalContext());

        try {
            engine.clear();
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.addEventListener(null, null);
            fail("test should throw an exception");
        } catch (ApexRuntimeException ae) {
            assertEquals("addEventListener()<-Engine:0.0.1,STOPPED, listenerName is null", ae.getMessage());
        }

        try {
            engine.addEventListener("myListener", null);
            fail("test should throw an exception");
        } catch (ApexRuntimeException ae) {
            assertEquals("addEventListener()<-Engine:0.0.1,STOPPED, listener is null", ae.getMessage());
        }

        try {
            engine.removeEventListener(null);
            fail("test should throw an exception");
        } catch (ApexRuntimeException ae) {
            assertEquals("removeEventListener()<-Engine:0.0.1,STOPPED, listenerName is null", ae.getMessage());
        }

        try {
            engine.addEventListener("myListener", new DummyListener());
            engine.removeEventListener("myListener");
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        assertNull(engine.createEvent(null));

        assertFalse(engine.handleEvent(null));

        try {
            engine.updateModel(null);
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("updateModel()<-Engine:0.0.1, Apex model is not defined, it has a null value",
                            ae.getMessage());
        }

        try {
            engine.updateModel(policyModel);
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        // Force a context exception
        ModelService.registerModel(AxPolicyModel.class, new AxPolicyModel());
        try {
            engine.updateModel(incompatiblePolicyModel);
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("updateModel()<-Engine:0.0.1, error setting the context for engine \"Engine:0.0.1\"",
                            ae.getMessage());
        }

        try {
            engine.updateModel(policyModel);
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertNotNull(engine.getInternalContext());
        assertEquals(1, engine.getEngineContext().size());

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("start()<-Engine:0.0.1,READY, cannot start engine, engine not in state STOPPED",
                            ae.getMessage());
        }

        try {
            engine.clear();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("clear()<-Engine:0.0.1,READY, cannot clear engine, engine is not stopped", ae.getMessage());
        }

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.clear();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("start()<-Engine:0.0.1,STOPPED,  cannot start engine, "
                            + "engine has not been initialized, its model is not loaded", ae.getMessage());
        }

        try {
            engine.updateModel(policyModel);
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertNull(engine.createEvent(null));

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.addEventListener("myListener", new DummyListener());
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.updateModel(policyModel);
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("updateModel()<-Engine:0.0.1, cannot update model, "
                            + "engine should be stopped but is in state READY", ae.getMessage());
        }

        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.addEventListener("badListener", new BadListener());
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.removeEventListener("badListener");
            engine.addEventListener("slowListener", new SlowListener());
        } catch (Exception e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testState() throws InterruptedException {
        AxArtifactKey engineKey = new AxArtifactKey("Engine:0.0.1");
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        try {
            engine.updateModel(policyModel);
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        SlowListener slowListener = new SlowListener();
        try {
            engine.addEventListener("slowListener", slowListener);
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertEquals(AxEngineState.READY, engine.getState());

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        // 1 second is less than the 3 second wait on engine stopping
        slowListener.setWaitTime(1000);
        (new Thread() {
            public void run() {
                assertTrue(engine.handleEvent(event));
                assertEquals(AxEngineState.STOPPED, engine.getState());
            }
        }).start();

        Thread.sleep(50);
        assertEquals(AxEngineState.EXECUTING, engine.getState());

        assertFalse(engine.handleEvent(event));
        assertNotNull(engine.createEvent(eventKey));

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        // 4 seconds is more than the 3 second wait on engine stopping
        slowListener.setWaitTime(4000);
        (new Thread() {
            public void run() {
                assertTrue(engine.handleEvent(event));
                assertEquals(AxEngineState.STOPPED, engine.getState());
            }
        }).start();

        Thread.sleep(50);
        assertEquals(AxEngineState.EXECUTING, engine.getState());
        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("stop()<-Engine:0.0.1,STOPPED, error stopping engine, engine stop timed out", ae.getMessage());
        }

        try {
            engine.clear();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testStateMachineError() throws InterruptedException, IllegalArgumentException, IllegalAccessException,
                    NoSuchFieldException, SecurityException {

        AxArtifactKey engineKey = new AxArtifactKey("Engine:0.0.1");
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        try {
            engine.updateModel(policyModel);
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        final Field smHandlerField = engine.getClass().getDeclaredField("stateMachineHandler");
        smHandlerField.setAccessible(true);
        smHandlerField.set(engine, smHandlerMock);

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertEquals(AxEngineState.READY, engine.getState());

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        try {
            Mockito.doThrow(new StateMachineException("mocked state machine exception",
                            new IOException("nexted exception"))).when(smHandlerMock).start();

            engine.start();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("updateModel()<-Engine:0.0.1, error starting the engine state machines \"Engine:0.0.1\"",
                            ae.getMessage());
        }

        assertEquals(AxEngineState.STOPPED, engine.getState());

        try {
            engine.clear();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testStateMachineHandler() throws InterruptedException, IllegalArgumentException, IllegalAccessException,
                    NoSuchFieldException, SecurityException {
        AxArtifactKey engineKey = new AxArtifactKey("Engine:0.0.1");
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        try {
            engine.updateModel(policyModelWithStates);
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }
        
        assertEquals(AxEngineState.STOPPED, engine.getState());

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }

        assertEquals(AxEngineState.READY, engine.getState());

        // Can't work, state is not fully defined
        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());

        final Field smHandlerField = engine.getClass().getDeclaredField("stateMachineHandler");
        smHandlerField.setAccessible(true);
        StateMachineHandler smHandler = (StateMachineHandler) smHandlerField.get(engine);

        final Field smExecutorMapField = smHandler.getClass().getDeclaredField("stateMachineExecutorMap");
        smExecutorMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<AxEvent, StateMachineExecutor> smExMap = (HashMap<AxEvent, StateMachineExecutor>) smExecutorMapField
                        .get(smHandler);

        assertEquals(1, smExMap.size());
        DummySmExecutor dummyExecutor = new DummySmExecutor(null, event.getKey());
        smExMap.put(event.getAxEvent(), dummyExecutor);
        
        try {
            ApexInternalContext internalContext = new ApexInternalContext(policyModelWithStates);
            dummyExecutor.setContext(null, null, internalContext);
        } catch (Exception e) {
            // Ignore this exception, we just need to set the internal context
        }

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }
        
        try {
            engine.start();
            fail("test should throw an exception");
        } catch (ApexException ae) {
            assertEquals("updateModel()<-Engine:0.0.1, error starting the engine state machines \"Engine:0.0.1\"",
                            ae.getMessage());
        }

        assertEquals(AxEngineState.STOPPED, engine.getState());

        try {
            engine.start();
            assertEquals(AxEngineState.READY, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        // Works, Dummy executor fakes event execution
        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());

        try {
            engine.stop();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException ae) {
            fail("test should not throw an exception");
        }

        try {
            engine.clear();
            assertEquals(AxEngineState.STOPPED, engine.getState());
        } catch (ApexException e) {
            fail("test should not throw an exception");
        }
    }
}
