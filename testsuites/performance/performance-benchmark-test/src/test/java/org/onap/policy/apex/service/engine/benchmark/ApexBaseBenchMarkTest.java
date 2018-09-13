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
package org.onap.policy.apex.service.engine.benchmark;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.plugins.context.schema.avro.AvroSchemaHelperParameters;
import org.onap.policy.apex.plugins.executor.java.JavaExecutorParameters;
import org.onap.policy.apex.plugins.executor.javascript.JavascriptExecutorParameters;
import org.onap.policy.apex.plugins.executor.jruby.JrubyExecutorParameters;
import org.onap.policy.apex.plugins.executor.jython.JythonExecutorParameters;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.engine.runtime.ApexServiceModelUpdateTest;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class ApexBaseBenchMarkTest {
    private static final long STOP_TIME_OUT = TimeUnit.SECONDS.toMillis(30);
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexServiceModelUpdateTest.class);
    private static final long MAX_START_WAIT = TimeUnit.SECONDS.toMillis(10);
    private final AxArtifactKey engineServiceKey = new AxArtifactKey("Machine-1_process-1_engine-1", "0.0.0");
    private final String model;
    private final int threads;
    private final ApexEventListener listener;
    private EngineService service;
    private EngineServiceEventInterface engineServiceEventInterface;


    public ApexBaseBenchMarkTest(final String model, final int threads, final ApexEventListener listener) {
        this.model = model;
        this.threads = threads;
        this.listener = listener;
    }

    public void setUp() throws Exception {
        final EngineServiceParameters parameters = new EngineServiceParameters();
        parameters.setInstanceCount(threads);
        parameters.setName(engineServiceKey.getName());
        parameters.setVersion(engineServiceKey.getVersion());
        parameters.setId(100);

        final EngineParameters engineParameters = parameters.getEngineParameters();
        final Map<String, ExecutorParameters> executorParameterMap = engineParameters.getExecutorParameterMap();
        executorParameterMap.put("MVEL", new MvelExecutorParameters());
        executorParameterMap.put("JAVASCRIPT", new JavascriptExecutorParameters());
        executorParameterMap.put("JYTHON", new JythonExecutorParameters());
        executorParameterMap.put("JAVA", new JavaExecutorParameters());
        executorParameterMap.put("JRUBY", new JrubyExecutorParameters());

        final ContextParameters contextParameters = engineParameters.getContextParameters();
        final SchemaParameters schemaParameters = contextParameters.getSchemaParameters();
        schemaParameters.getSchemaHelperParameterMap().put("Avro", new AvroSchemaHelperParameters());
        schemaParameters.getSchemaHelperParameterMap().put("Java", new JavaSchemaHelperParameters());
        service = EngineServiceImpl.create(parameters);

        service = EngineServiceImpl.create(parameters);
        service.registerActionListener("listener", listener);
        service.updateModel(parameters.getEngineKey(), model, true);

        LOGGER.info("Starting EngineService ... ");
        service.startAll();

        final long starttime = System.currentTimeMillis();
        while (!service.isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT) {
            ThreadUtilities.sleep(50);
        }
        if (!service.isStarted()) {
            LOGGER.error("Apex Service {} failed to start after {} ms", service.getKey(), MAX_START_WAIT);
            new ApexException("Unable to start engine service ");
        }

        engineServiceEventInterface = service.getEngineServiceEventInterface();
    }

    public void sendEvents(final List<ApexEvent> events) {
        for (final ApexEvent event : events) {
            engineServiceEventInterface.sendEvent(event);
        }
    }

    public void sendEvent(final ApexEvent event) {
        engineServiceEventInterface.sendEvent(event);
    }


    public EngineService getService() {
        return service;
    }

    public void destroy() throws Exception {
        if (service != null) {
            LOGGER.info("Stopping EngineService ... ");
            service.stop();
            final long currentTimeInMillSec = System.currentTimeMillis();
            while (!service.isStopped()) {
                if (System.currentTimeMillis() - currentTimeInMillSec > STOP_TIME_OUT) {
                    LOGGER.warn("Timed Out EngineService status: ", service.isStopped());
                    break;
                }
                ThreadUtilities.sleep(500);
            }
            service = null;
        }
    }

}
