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

package org.onap.policy.apex.examples.adaptive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.ApexEngine;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.examples.adaptive.model.AdaptiveDomainModelFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.java.JavaExecutorParameters;
import org.onap.policy.apex.plugins.executor.mvel.MVELExecutorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This policy passes, and recieves a Double event context filed called "EVCDouble".<br>
 * The policy tries to detect anomalies in the pattern of values for EVCDouble<br>
 * See the 2 test cases below (1 short, 1 long)
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
public class TestAnomalyDetectionTslUseCase {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestAnomalyDetectionTslUseCase.class);

    private static final int MAXITERATIONS = 3660;
    private static final Random RAND = new Random(System.currentTimeMillis());

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;

    @Before
    public void beforeTest() {
        schemaParameters = new SchemaParameters();
        
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
        
        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());
        
        engineParameters = new EngineParameters();
        engineParameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());
        engineParameters.getExecutorParameterMap().put("JAVA", new JavaExecutorParameters());
        ParameterService.register(engineParameters);
    }

    @After
    public void afterTest() {
        ParameterService.deregister(engineParameters);
        
        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    @Test
    // once through the long running test below
    public void testAnomalyDetectionTsl() throws ApexException, InterruptedException, IOException {
        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAnomalyDetectionPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AnomalyTSLApexEngine", "0.0.1");

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener", listener1);
        apexEngine1.updateModel(apexPolicyModel);
        apexEngine1.start();
        final EnEvent triggerEvent =
                apexEngine1.createEvent(new AxArtifactKey("AnomalyDetectionTriggerEvent", "0.0.1"));
        final double rval = RAND.nextGaussian();
        triggerEvent.put("Iteration", 0);
        triggerEvent.put("MonitoredValue", rval);
        LOGGER.info("Triggering policy in Engine 1 with " + triggerEvent);
        apexEngine1.handleEvent(triggerEvent);
        final EnEvent result = listener1.getResult();
        LOGGER.info("Receiving action event {} ", result);
        assertEquals("ExecutionIDs are different", triggerEvent.getExecutionId(), result.getExecutionId());
        triggerEvent.clear();
        result.clear();
        Thread.sleep(1);
        apexEngine1.stop();
    }

    /**
     * This policy passes, and recieves a Double event context filed called "EVCDouble"<br>
     * The policy tries to detect anomalies in the pattern of values for EVCDouble <br>
     * This test case generates a SineWave-like pattern for the parameter, repeating every 360 iterations. (These Period
     * should probably be set using TaskParameters!) Every 361st value is a random number!, so should be identified as
     * an Anomaly. The policy has 3 Decide Tasks, and the Decide TaskSelectionLogic picks one depending on the
     * 'Anomaliness' of the input data. <br>
     * To plot the results grep debug results for the string "************", paste into excel and delete non-relevant
     * columns<br>
     *
     * @throws ApexException the apex exception
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    // Test is disabled by default. uncomment below, or execute using the main() method
    // @Test
    // EG Dos command: apex-core.engine> mvn
    // -Dtest=org.onap.policy.apex.core.engine.ml.TestAnomalyDetectionTslUseCase test | findstr /L /C:"Apex [main] DEBUG
    // c.e.a.e.TaskSelectionExecutionLogging -
    // TestAnomalyDetectionTSL_Policy0000DecideStateTaskSelectionLogic.getTask():"
    public void testAnomalyDetectionTslmain() throws ApexException, InterruptedException, IOException {

        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAnomalyDetectionPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AnomalyTSLApexEngine", "0.0.1");
        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());
        parameters.getExecutorParameterMap().put("JAVA", new JavaExecutorParameters());

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener1", listener1);
        apexEngine1.updateModel(apexPolicyModel);
        apexEngine1.start();

        final EnEvent triggerEvent =
                apexEngine1.createEvent(new AxArtifactKey("AnomalyDetectionTriggerEvent", "0.0.1"));
        assertNotNull(triggerEvent);

        for (int iteration = 0; iteration < MAXITERATIONS; iteration++) {
            // Trigger the policy in engine 1

            double value = (Math.sin(Math.toRadians(iteration))) + (RAND.nextGaussian() / 25.0);
            // lets make every 361st number a random value to perhaps flag as an anomaly
            if (((iteration + 45) % 361) == 0) {
                value = (RAND.nextGaussian() * 2.0);
            }
            triggerEvent.put("Iteration", iteration);
            triggerEvent.put("MonitoredValue", value);
            LOGGER.info("Iteration " + iteration + ":\tTriggering policy in Engine 1 with " + triggerEvent);
            apexEngine1.handleEvent(triggerEvent);
            final EnEvent result = listener1.getResult();
            LOGGER.info("Iteration " + iteration + ":\tReceiving action event {} ", result);
            triggerEvent.clear();
            result.clear();
        }
        apexEngine1.stop();
        Thread.sleep(1000);
    }

    public static void main(final String[] args) throws ApexException, InterruptedException, IOException {
        new TestAnomalyDetectionTslUseCase().testAnomalyDetectionTslmain();
    }
}
