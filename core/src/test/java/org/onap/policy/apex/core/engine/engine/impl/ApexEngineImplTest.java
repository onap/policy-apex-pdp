/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2023-2025 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.engine.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
class ApexEngineImplTest {
    private static final String ENGINE_ID = "Engine:0.0.1";

    private AxPolicyModel policyModel;
    private AxPolicyModel incompatiblePolicyModel;
    private AxPolicyModel policyModelWithStates;

    @Mock
    StateMachineHandler smHandlerMock;

    /**
     * Set up services.
     */
    @BeforeAll
    static void setup() {
        ParameterService.register(new SchemaParameters());
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
        ParameterService.register(new EngineParameters());
    }

    /**
     * Set up mocking.
     */
    @BeforeEach
    void initializeMocking() throws ApexException {
        Mockito.lenient().doThrow(new StateMachineException("mocked state machine exception",
            new IOException("nexted exception"))).when(smHandlerMock).execute(Mockito.any());
    }

    /**
     * Create policy models.
     */
    @BeforeEach
    void createPolicyModels() {
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
    @AfterAll
    static void teardown() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }

    @Test
    void testSanity() throws ApexException {
        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        assertThatThrownBy(engine::start).hasMessage("start()<-Engine:0.0.1,STOPPED,  cannot start engine, "
            + "engine has not been initialized, its model is not loaded");

        assertThatThrownBy(engine::stop)
            .hasMessage("stop()<-Engine:0.0.1,STOPPED, cannot stop engine, " + "engine is already stopped");

        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
        assertEquals(0, engine.getEngineContext().size());
        assertEquals(engineKey, engine.getEngineStatus().getKey());
        assertNull(engine.getInternalContext());

        engine.clear();

        assertThatThrownBy(() -> engine.addEventListener(null, null))
            .hasMessage("addEventListener()<-Engine:0.0.1,STOPPED, listenerName is null");

        assertThatThrownBy(() -> engine.addEventListener("myListener", null))
            .hasMessage("addEventListener()<-Engine:0.0.1,STOPPED, listener is null");

        assertThatThrownBy(() -> engine.removeEventListener(null))
            .hasMessage("removeEventListener()<-Engine:0.0.1,STOPPED, listenerName is null");
    }

    @Test
    void testListener() throws ApexException {
        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);

        engine.addEventListener("myListener", new DummyListener());
        engine.removeEventListener("myListener");

        assertNull(engine.createEvent(null));

        assertFalse(engine.handleEvent(null));

        assertThatThrownBy(() -> engine.updateModel(null, false))
            .hasMessage("updateModel()<-Engine:0.0.1, Apex model is not defined, it has a null value");

        engine.updateModel(policyModel, false);

        // Force a context exception
        ModelService.registerModel(AxPolicyModel.class, new AxPolicyModel());
        assertThatThrownBy(() -> engine.updateModel(incompatiblePolicyModel, false))
            .hasMessage("updateModel()<-Engine:0.0.1, error setting the context for engine \"Engine:0.0.1\"");

        engine.updateModel(policyModel, false);

        assertNotNull(engine.getInternalContext());
        assertEquals(1, engine.getEngineContext().size());

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        assertThatThrownBy(engine::start)
            .hasMessage("start()<-Engine:0.0.1,READY, cannot start engine, engine not in state STOPPED");

        assertThatThrownBy(engine::clear)
            .hasMessage("clear()<-Engine:0.0.1,READY, cannot clear engine, engine is not stopped");

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.clear();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        assertThatThrownBy(engine::start).hasMessage("start()<-Engine:0.0.1,STOPPED,  cannot start engine, "
            + "engine has not been initialized, its model is not loaded");

        engine.updateModel(policyModel, false);
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        assertNull(engine.createEvent(null));
    }

    @Test
    void testEventKey() throws ApexException {
        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        engine.updateModel(policyModel, false);
        engine.start();

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.addEventListener("myListener", new DummyListener());

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        assertThatThrownBy(() -> engine.updateModel(policyModel, false)).hasMessage(
            "updateModel()<-Engine:0.0.1, cannot update model, engine should be stopped but is in state READY");

        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.addEventListener("badListener", new DummyEnEventListener());

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);
        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.removeEventListener("badListener");
        engine.addEventListener("slowListener", new DummySlowEnEventListener());
    }

    @Test
    void testState() throws ApexException {
        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        engine.updateModel(policyModel, false);
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        DummySlowEnEventListener slowListener = new DummySlowEnEventListener();
        engine.addEventListener("slowListener", slowListener);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        // 1 second is less than the 3 seconds wait on engine stopping
        slowListener.setWaitTime(1000);
        (new Thread(() -> engine.handleEvent(event))).start();
        await().atLeast(50, TimeUnit.MILLISECONDS).until(() -> engine.getState().equals(AxEngineState.EXECUTING));
        assertEquals(AxEngineState.EXECUTING, engine.getState());
        checkAxEngineStateMetric(AxEngineState.EXECUTING);

        assertFalse(engine.handleEvent(event));
        assertNotNull(engine.createEvent(eventKey));

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        // 4 seconds is more than the 3 seconds wait on engine stopping
        slowListener.setWaitTime(4000);
        (new Thread(() -> engine.handleEvent(event))).start();

        await().atLeast(50, TimeUnit.MILLISECONDS).until(() -> engine.getState().equals(AxEngineState.EXECUTING));
        assertEquals(AxEngineState.EXECUTING, engine.getState());
        checkAxEngineStateMetric(AxEngineState.EXECUTING);
        assertThatThrownBy(engine::stop)
            .hasMessage("stop()<-Engine:0.0.1,STOPPED, error stopping engine, engine stop timed out");
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
        engine.clear();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
    }

    @Test
    void testStateMachineError() throws IllegalArgumentException, IllegalAccessException,
        NoSuchFieldException, SecurityException, ApexException {

        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        engine.updateModel(policyModel, false);
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        final Field smHandlerField = engine.getClass().getDeclaredField("stateMachineHandler");
        smHandlerField.setAccessible(true);
        smHandlerField.set(engine, smHandlerMock);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
        Mockito.doThrow(new StateMachineException("mocked state machine exception",
            new IOException("nexted exception"))).when(smHandlerMock).start();
        assertThatThrownBy(engine::start).hasMessage("updateModel()<-Engine:0.0.1, error starting the engine state "
            + "machines \"Engine:0.0.1\"");
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.clear();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
    }

    @Test
    void testStateMachineHandler() throws IllegalArgumentException, IllegalAccessException,
        NoSuchFieldException, SecurityException, ApexException {
        AxArtifactKey engineKey = new AxArtifactKey(ENGINE_ID);
        ApexEngineImpl engine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(engineKey);
        assertNotNull(engine);
        assertEquals(engineKey, engine.getKey());

        engine.updateModel(policyModelWithStates, false);
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        AxArtifactKey eventKey = new AxArtifactKey("Event:0.0.1");
        EnEvent event = engine.createEvent(eventKey);
        assertEquals(eventKey, event.getKey());

        assertEngineStopStartState(engine, event);

        HashMap<AxEvent, StateMachineExecutor>
            smExMap = getAxEventStateMachineExecutorHashMap(engine);

        assertEquals(1, smExMap.size());
        DummySmExecutor dummyExecutor = new DummySmExecutor(null, event.getKey());
        smExMap.put(event.getAxEvent(), dummyExecutor);
        ApexInternalContext internalContext = new ApexInternalContext(policyModelWithStates);
        assertThatThrownBy(() -> dummyExecutor.setContext(null, null, internalContext))
            .isInstanceOf(NullPointerException.class);

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        assertThatThrownBy(engine::start).hasMessageContaining("updateModel()<-Engine:0.0.1, error starting the "
            + "engine state machines \"Engine:0.0.1\"");
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        // Works, Dummy executor fakes event execution
        assertTrue(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.clear();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);
    }

    private static HashMap<AxEvent, StateMachineExecutor> getAxEventStateMachineExecutorHashMap(ApexEngineImpl engine)
        throws NoSuchFieldException, IllegalAccessException {
        final Field smHandlerField = engine.getClass().getDeclaredField("stateMachineHandler");
        smHandlerField.setAccessible(true);
        StateMachineHandler smHandler = (StateMachineHandler) smHandlerField.get(engine);

        final Field smExecutorMapField = smHandler.getClass().getDeclaredField("stateMachineExecutorMap");
        smExecutorMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<AxEvent, StateMachineExecutor> smExMap = (HashMap<AxEvent, StateMachineExecutor>) smExecutorMapField
            .get(smHandler);
        return smExMap;
    }

    private void assertEngineStopStartState(ApexEngineImpl engine, EnEvent event) throws ApexException {
        engine.stop();
        assertEquals(AxEngineState.STOPPED, engine.getState());
        checkAxEngineStateMetric(AxEngineState.STOPPED);

        engine.start();
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);

        // Can't work, state is not fully defined
        assertFalse(engine.handleEvent(event));
        assertEquals(AxEngineState.READY, engine.getState());
        checkAxEngineStateMetric(AxEngineState.READY);
    }

    private void checkAxEngineStateMetric(AxEngineState state) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Gauge stateGauge = Gauge.builder()
            .name("pdpa_engine_state")
            .help("Current state of the PDPA engine")
            .labelNames("engine_instance_id")
            .register(registry);

        String labelValue = ENGINE_ID;
        stateGauge.labelValues(labelValue).set(state.getStateIdentifier());

        double stateMetric = stateGauge.labelValues(labelValue).get();
        assertEquals(state.getStateIdentifier(), (int) stateMetric);
    }
}