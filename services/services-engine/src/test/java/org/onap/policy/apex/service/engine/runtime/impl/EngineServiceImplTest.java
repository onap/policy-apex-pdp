/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2022 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.engine.runtime.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
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
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Test the engine service implementation.
 */
class EngineServiceImplTest {

    private static String simpleModelString;
    private static String differentModelString;
    private static AxPolicyModel simpleModel;

    /**
     * Read the models into strings.
     *
     * @throws IOException on model reading errors
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
        ModelService.clear();
    }

    private EngineServiceParameters makeConfig() {
        EngineServiceParameters config = new EngineServiceParameters();
        config.setInstanceCount(0);
        config.setId(123);
        config.setEngineKey(new AxArtifactKey("Engine", "0.0.1"));
        config.setInstanceCount(1);
        config.setPolicyModel("policyModelContent");
        return config;
    }

    @Test
    void testEngineServiceImplSanity() throws ApexException {
        assertThatThrownBy(() -> EngineServiceImpl.create(null)).isInstanceOf(ApexException.class)
            .hasMessage("Engine service configuration parameters are null");

        EngineServiceParameters invalidConfig = new EngineServiceParameters();
        invalidConfig.setInstanceCount(0);
        assertThatThrownBy(() -> EngineServiceImpl.create(invalidConfig)).isInstanceOf(ApexException.class)
            .hasMessageContaining("Invalid engine service configuration parameters");

        EngineServiceParameters config = makeConfig();
        EngineServiceImpl esImpl = EngineServiceImpl.create(config);
        assertEquals("Engine:0.0.1", esImpl.getKey().getId());

        esImpl.registerActionListener(null, null);
        esImpl.registerActionListener("DummyListener", null);
        esImpl.registerActionListener(null, new DummyApexEventListener());

        esImpl.registerActionListener("DummyListener", new DummyApexEventListener());
        assertThatThrownBy(() -> esImpl.deregisterActionListener(null))
            .hasMessage("removeEventListener()<-Engine-0:0.0.1,STOPPED, listenerName is null");

        esImpl.deregisterActionListener("DummyListener");

        assertEquals(esImpl, esImpl.getEngineServiceEventInterface());
        assertEquals(1, esImpl.getEngineKeys().size());

        assertNull(esImpl.getApexModelKey());

        assertThatThrownBy(() -> esImpl.getRuntimeInfo(null)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.getRuntimeInfo(new AxArtifactKey("DummyKey", "0.0.1")))
            .isInstanceOf(ApexException.class).hasMessage("engine with key DummyKey:0.0.1 not found in engine service");

        String runtimeInfo = esImpl.getRuntimeInfo(esImpl.getEngineKeys().iterator().next());
        assertEquals("{\n  \"TimeStamp\":", runtimeInfo.substring(0, 16));

        assertEquals(AxEngineState.STOPPED, esImpl.getState());

        assertThatThrownBy(() -> esImpl.getStatus(null)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");
        assertThatThrownBy(() -> esImpl.getStatus(new AxArtifactKey("DummyKey", "0.0.1")))
            .isInstanceOf(ApexException.class).hasMessage("engine with key DummyKey:0.0.1 not found in engine service");

        String status = esImpl.getStatus(esImpl.getEngineKeys().iterator().next());
        assertTrue(status.contains("\"timestamp\":"));

        assertFalse(esImpl.isStarted());
        assertFalse(esImpl.isStarted(null));
        assertFalse(esImpl.isStarted(new AxArtifactKey("DummyKey", "0.0.1")));
        assertFalse(esImpl.isStarted(esImpl.getEngineKeys().iterator().next()));
        assertTrue(esImpl.isStopped());
        assertTrue(esImpl.isStopped(null));
        assertTrue(esImpl.isStopped(new AxArtifactKey("DummyKey", "0.0.1")));
        assertTrue(esImpl.isStopped(esImpl.getEngineKeys().iterator().next()));
    }

    @Test
    void testEngineServiceExceptions() throws ApexException {
        EngineServiceParameters config = makeConfig();
        EngineServiceImpl esImpl = EngineServiceImpl.create(config);
        assertThatThrownBy(() -> esImpl.start(null)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.start(new AxArtifactKey("DummyKey", "0.0.1"))).isInstanceOf(ApexException.class)
            .hasMessage("engine with key DummyKey:0.0.1 not found in engine service");

        assertThatThrownBy(() -> esImpl.start(esImpl.getEngineKeys().iterator().next()))
            .isInstanceOf(ApexException.class).hasMessage("start()<-Engine-0:0.0.1,STOPPED,  cannot start engine, "
                + "engine has not been initialized, its model is not loaded");

        assertThatThrownBy(esImpl::startAll).isInstanceOf(ApexException.class)
            .hasMessage("start()<-Engine-0:0.0.1,STOPPED,  cannot start engine, "
                + "engine has not been initialized, its model is not loaded");

        assertThatThrownBy(() -> esImpl.stop(null)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.stop(new AxArtifactKey("DummyKey", "0.0.1"))).isInstanceOf(ApexException.class)
            .hasMessage("engine with key DummyKey:0.0.1 not found in engine service");

        esImpl.stop(esImpl.getEngineKeys().iterator().next());

        esImpl.stop();
        esImpl.sendEvent(null);
        esImpl.sendEvent(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget", ""));

        esImpl.startPeriodicEvents(100000);

        assertThatThrownBy(() -> esImpl.startPeriodicEvents(100000)).isInstanceOf(ApexException.class)
            .hasMessage("Periodic event generation already running on engine Engine:0.0.1, ApexPeriodicEventGenerator "
                + "[period=100000, firstEventTime=0, lastEventTime=0, eventCount=0]");

        esImpl.stopPeriodicEvents();

        assertThatThrownBy(esImpl::stopPeriodicEvents).isInstanceOf(ApexException.class)
            .hasMessage("Periodic event generation not running on engine Engine:0.0.1");

        assertThatThrownBy(() -> esImpl.clear(null)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.clear(new AxArtifactKey("DummyKey", "0.0.1"))).isInstanceOf(ApexException.class)
            .hasMessage("engine with key DummyKey:0.0.1 not found in engine service");
        esImpl.clear(esImpl.getEngineKeys().iterator().next());
        esImpl.clear();

        assertThatThrownBy(() -> esImpl.updateModel(null, (String) null, true)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (String) null, true))
            .isInstanceOf(ApexException.class)
            .hasMessage("model for updating engine service with key DummyKey:0.0.1 is empty");

        assertThatThrownBy(() -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "", true))
            .isInstanceOf(ApexException.class)
            .hasMessage("model for updating engine service with key DummyKey:0.0.1 is empty");

        assertThatThrownBy(
            () -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), "I am not an Apex model", true))
                .isInstanceOf(ApexException.class)
                .hasMessage("failed to unmarshal the apex model on engine service DummyKey:0.0.1");

        assertThatThrownBy(() -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModelString, true))
            .isInstanceOf(ApexException.class)
            .hasMessage("engine service key DummyKey:0.0.1 does not match the keyEngine:0.0.1 of this engine service");

        assertThatThrownBy(() -> esImpl.updateModel(null, simpleModelString, true)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.updateModel(null, (AxPolicyModel) null, true)).isInstanceOf(ApexException.class)
            .hasMessage("engine key must be specified and may not be null");

        assertThatThrownBy(() -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), (AxPolicyModel) null, true))
            .isInstanceOf(ApexException.class)
            .hasMessage("model for updating on engine service with key DummyKey:0.0.1 is null");

        assertThatThrownBy(() -> esImpl.updateModel(new AxArtifactKey("DummyKey", "0.0.1"), simpleModel, true))
            .isInstanceOf(ApexException.class)
            .hasMessage("engine service key DummyKey:0.0.1 does not match the keyEngine:0.0.1 of this engine service");
    }

    @Test
    void testApexImplModelWIthModel() throws ApexException {
        EngineServiceParameters config = makeConfig();
        EngineServiceImpl esImpl = EngineServiceImpl.create(config);
        assertEquals("Engine:0.0.1", esImpl.getKey().getId());

        try {
            esImpl.updateModel(config.getEngineKey(), simpleModelString, false);
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        try {
            esImpl.updateModel(config.getEngineKey(), differentModelString, false);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("apex model update failed, supplied model with key \"SmallModelDifferent:0.0.1\" is not a "
                    + "compatible model update " + "from the existing engine model with key \"SmallModel:0.0.1\"",
                    apEx.getMessage());
        }

        try {
            esImpl.updateModel(config.getEngineKey(), differentModelString, true);
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
        assertTrue(status.contains("\"timestamp\":"));
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
            esImpl.sendEvent(new ApexEvent("SomeEvent", "0.0.1", "the.event.namespace", "EventSource", "EventTarget",
                    ""));
        } catch (ApexException apEx) {
            fail("test should not throw an exception");
        }

        assertPeriodicEvents(esImpl);
    }

    private static void assertPeriodicEvents(EngineServiceImpl esImpl) throws ApexException {
        esImpl.startPeriodicEvents(100000);
        esImpl.stop();
        esImpl.startAll();
        esImpl.stopPeriodicEvents();

        esImpl.startPeriodicEvents(100000);
        try {
            esImpl.startPeriodicEvents(100000);
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Periodic event generation already running on engine Engine:0.0.1, ApexPeriodicEventGenerator "
                    + "[period=100000, firstEventTime=0, lastEventTime=0, eventCount=0]", apEx.getMessage());
        }

        esImpl.stopPeriodicEvents();
        try {
            esImpl.stopPeriodicEvents();
            fail("test should throw an exception");
        } catch (ApexException apEx) {
            assertEquals("Periodic event generation not running on engine Engine:0.0.1", apEx.getMessage());
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
