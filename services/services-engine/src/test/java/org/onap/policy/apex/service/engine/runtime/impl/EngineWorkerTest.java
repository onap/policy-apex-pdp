/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021-2022 Bell Canada Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.common.utils.services.Registry;

/**
 * Test the engine worker class.
 */
class EngineWorkerTest {
    private final ApplicationThreadFactory atFactory = new ApplicationThreadFactory("apex-engine-service", 512);

    private static String simpleModelString;
    private static String differentModelString;
    private static AxPolicyModel simpleModel;

    /**
     * Read the models into strings.
     *
     * @throws IOException        on model reading errors
     * @throws ApexModelException on model reading exceptions
     */
    @BeforeAll
    static void readSimpleModel() throws IOException, ApexModelException {
        simpleModelString = TextFileUtils.getTextFileAsString("src/test/resources/policymodels/SmallModel.json");

        differentModelString =
            TextFileUtils.getTextFileAsString("src/test/resources/policymodels/SmallModelDifferent.json");

        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<>(AxPolicyModel.class);
        simpleModel = modelReader.read(new ByteArrayInputStream(simpleModelString.getBytes()));
    }

    /**
     * Initialize default parameters.
     */
    @BeforeAll
    static void initializeDefaultParameters() {
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
        ExecutorParameters jsExecutorParameters = getExecutorParameters("JAVASCRIPT");
        engineParameters.getExecutorParameterMap().put("JAVASCRIPT", jsExecutorParameters);
        getExecutorParameters("MVEL");
        engineParameters.getExecutorParameterMap().put("MVEL", jsExecutorParameters);
        ParameterService.register(engineParameters);

    }

    private static ExecutorParameters getExecutorParameters(String lang) {
        ExecutorParameters jsExecutorParameters = new ExecutorParameters();
        jsExecutorParameters.setName(lang);
        jsExecutorParameters
            .setTaskSelectionExecutorPluginClass("org.onap.policy.apex.service.engine.runtime.impl.DummyTse");
        jsExecutorParameters.setTaskExecutorPluginClass("org.onap.policy.apex.service.engine.runtime.impl.DummyTe");
        jsExecutorParameters
            .setStateFinalizerExecutorPluginClass("org.onap.policy.apex.service.engine.runtime.impl.DummySfe");
        return jsExecutorParameters;
    }

    /**
     * Teardown default parameters.
     */
    @AfterAll
    static void teardownDefaultParameters() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.deregister(EngineParameterConstants.MAIN_GROUP_NAME);
    }

    @AfterEach
    void clearDownTest() {
        ModelService.clear();
    }

    @Test
    void testEngineWorker() {

        BlockingQueue<ApexEvent> eventQueue = new LinkedBlockingQueue<>();

        EngineWorker worker = new EngineWorker(new AxArtifactKey("Worker", "0.0.1"), eventQueue, atFactory);

        assertThatThrownBy(() -> worker.registerActionListener(null, null))
            .hasMessageContaining("addEventListener()<-Worker:0.0.1,STOPPED, listenerName is null");

        worker.registerActionListener("DummyListener", null);

        assertThatThrownBy(() -> worker.registerActionListener(null, new DummyApexEventListener()))
            .hasMessageContaining("addEventListener()<-Worker:0.0.1,STOPPED, listenerName is null");

        worker.registerActionListener("DummyListener", new DummyApexEventListener());

        assertThatThrownBy(() -> worker.deregisterActionListener(null))
            .hasMessageContaining("removeEventListener()<-Worker:0.0.1,STOPPED, listenerName is null");

        worker.deregisterActionListener("DummyListener");

        assertThatThrownBy(worker::getEngineServiceEventInterface)
            .hasMessageContaining("getEngineServiceEventInterface() call is not allowed on an Apex Engine Worker");

        assertThatThrownBy(() -> worker.startPeriodicEvents(100000))
            .hasMessageContaining("startPeriodicEvents() call is not allowed on an Apex Engine Worker");

        assertThatThrownBy(worker::stopPeriodicEvents)
            .hasMessageContaining("stopPeriodicEvents() call is not allowed on an Apex Engine Worker");

        assertEquals("Worker:0.0.1", worker.getEngineKeys().iterator().next().getId());

        assertNull(worker.getApexModelKey());

        assertEngineWorkerStartStop(worker);

        assertThatThrownBy(() -> worker.clear(new AxArtifactKey("DummyKey", "0.0.1")))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");

        assertDoesNotThrow(() -> worker.clear(worker.getEngineKeys().iterator().next()));
        assertDoesNotThrow(() -> worker.clear());

        assertUpdateEngineModel(worker);
    }

    @Test
    void testApexImplModelWIthModel() throws ApexException {
        Registry.newRegistry();
        Registry.register(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER, new ApexPolicyStatisticsManager());
        BlockingQueue<ApexEvent> eventQueue = new LinkedBlockingQueue<>();

        EngineWorker worker = new EngineWorker(new AxArtifactKey("Worker", "0.0.1"), eventQueue, atFactory);
        assertEquals("Worker:0.0.1", worker.getKey().getId());

        assertDoesNotThrow(() -> worker.updateModel(worker.getKey(), simpleModelString, false));

        eventQueue.add(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget", ""));

        assertThatThrownBy(() -> worker.updateModel(worker.getKey(), differentModelString, false))
            .hasMessageContaining("apex model update failed, supplied model with key "
                + "\"SmallModelDifferent:0.0.1\" is not a compatible model update "
                + "from the existing engine model with key \"SmallModel:0.0.1\"");

        assertDoesNotThrow(() -> worker.updateModel(worker.getKey(), differentModelString, true));

        assertDoesNotThrow(() -> worker.updateModel(worker.getKey(), simpleModelString, true));

        String runtimeInfo = worker.getRuntimeInfo(worker.getEngineKeys().iterator().next());
        assertEquals("{\"TimeStamp\":", runtimeInfo.replaceAll("\\s+", "").substring(0, 13));

        assertEquals(AxEngineState.STOPPED, worker.getState());
        worker.startAll();

        assertEquals(AxEngineState.READY, worker.getState());

        String status = worker.getStatus(worker.getEngineKeys().iterator().next());
        assertNotNull(status);
        assertEquals("{\"timestamp\":", status.replaceAll("\\s+", "").substring(0, 13));

        assertTrue(worker.isStarted());
        assertTrue(worker.isStarted(worker.getEngineKeys().iterator().next()));
        assertFalse(worker.isStopped());
        assertFalse(worker.isStopped(worker.getEngineKeys().iterator().next()));

        assertWorkerStartStopWithModel(worker);
    }

    private static void assertUpdateEngineModel(EngineWorker worker) {
        assertThatThrownBy(() -> worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "", true))
            .hasMessageContaining("failed to unmarshal the apex model on engine DummyKey:0.0.1");

        assertThatThrownBy(
            () -> worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "I am not an Apex model", true))
            .hasMessageContaining("failed to unmarshal the apex model on engine DummyKey:0.0.1");

        assertThatThrownBy(() -> worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModelString, true))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");

        assertThatThrownBy(
            () -> worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (AxPolicyModel) null, true))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");

        assertThatThrownBy(() -> worker.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModel, true))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");
    }

    private static void assertEngineWorkerStartStop(EngineWorker worker) {
        String runtimeInfo = worker.getRuntimeInfo(worker.getEngineKeys().iterator().next());
        assertEquals("{\"TimeStamp\":", runtimeInfo.replaceAll("\\s+", "").substring(0, 13));

        assertEquals(AxEngineState.STOPPED, worker.getState());

        assertEquals("{\"TimeStamp\":", runtimeInfo.replaceAll("\\s+", "").substring(0, 13));

        assertFalse(worker.isStarted());
        assertFalse(worker.isStarted(null));
        assertFalse(worker.isStarted(new AxArtifactKey("DummyKey", "0.0.1")));
        assertFalse(worker.isStarted(worker.getEngineKeys().iterator().next()));
        assertTrue(worker.isStopped());
        assertTrue(worker.isStopped(null));
        assertTrue(worker.isStopped(new AxArtifactKey("DummyKey", "0.0.1")));
        assertTrue(worker.isStopped(worker.getEngineKeys().iterator().next()));

        assertThatThrownBy(() -> worker.start(new AxArtifactKey("DummyKey", "0.0.1")))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");

        assertThatThrownBy(() -> worker.start(worker.getEngineKeys().iterator().next()))
            .hasMessageContaining(
                "start()<-Worker:0.0.1,STOPPED,  cannot start engine, engine has not been initialized, "
                    + "its model is not loaded");

        assertThatThrownBy(worker::startAll).hasMessageContaining(
            "start()<-Worker:0.0.1,STOPPED,  cannot start engine, engine has not been initialized, its "
                + "model is not loaded");

        assertThatThrownBy(() -> worker.stop(new AxArtifactKey("DummyKey", "0.0.1")))
            .hasMessageContaining("engine key DummyKey:0.0.1 does not match the keyWorker:0.0.1 of this engine");

        assertDoesNotThrow(() -> worker.stop(worker.getEngineKeys().iterator().next()));

        assertDoesNotThrow(() -> worker.stop());
    }

    private static void assertWorkerStartStopWithModel(EngineWorker worker) throws ApexException {
        assertThatThrownBy(() -> worker.start(worker.getEngineKeys().iterator().next()))
            .hasMessageContaining("apex engine for engine key Worker:0.0.1 is already running with state READY");

        assertThatThrownBy(worker::startAll)
            .hasMessageContaining("apex engine for engine key Worker:0.0.1 is already running with state READY");

        assertDoesNotThrow(() -> worker.stop(worker.getEngineKeys().iterator().next()));
        assertDoesNotThrow(() -> worker.start(worker.getEngineKeys().iterator().next()));
        assertDoesNotThrow(() -> worker.stop());
        assertDoesNotThrow(worker::startAll);

        worker.stop();
        worker.startAll();

        assertThatThrownBy(() -> worker.clear(worker.getEngineKeys().iterator().next()))
            .hasMessageContaining("clear()<-Worker:0.0.1,READY, cannot clear engine, engine is not stopped");

        assertDoesNotThrow(() -> worker.stop(worker.getEngineKeys().iterator().next()));
        assertDoesNotThrow(() -> worker.clear(worker.getEngineKeys().iterator().next()));
        assertDoesNotThrow(() -> worker.clear());

        assertNotNull(worker.getApexModelKey());

        final ApexPolicyStatisticsManager policyCounter = ApexPolicyStatisticsManager.getInstanceFromRegistry();
        assertNotNull(policyCounter);
        assertEquals(policyCounter.getPolicyExecutedCount(),
            policyCounter.getPolicyExecutedFailCount() + policyCounter.getPolicyExecutedSuccessCount());
    }
}
