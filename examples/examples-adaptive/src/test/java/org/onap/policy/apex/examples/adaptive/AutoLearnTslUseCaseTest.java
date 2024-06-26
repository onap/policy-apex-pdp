/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.java.JavaExecutorParameters;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Test Auto learning in TSL.
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
class AutoLearnTslUseCaseTest {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AutoLearnTslUseCaseTest.class);

    private static final int MAXITERATIONS = 1000;
    private static final Random rand = new Random(System.currentTimeMillis());
    private static final String RECEIVING_ACTION_EVENT = "Receiving action event {} ";
    private static final String TRIGGER_MESSAGE = "Triggering policy in Engine 1 with {}";

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;


    /**
     * Before test.
     */
    @BeforeEach
    void beforeTest() {
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
        engineParameters.getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        engineParameters.getExecutorParameterMap().put("JAVA", new JavaExecutorParameters());
        ParameterService.register(engineParameters);
    }

    /**
     * After test.
     */
    @AfterEach
    void afterTest() {
        ParameterService.deregister(engineParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    /**
     * Test auto learn tsl.
     *
     * @throws ApexException the apex exception
     */
    @Test
    // once through the long-running test below
    void testAutoLearnTsl() throws ApexException {
        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAutoLearnPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener", listener1);
        apexEngine1.updateModel(apexPolicyModel, false);
        apexEngine1.start();
        final EnEvent triggerEvent = apexEngine1.createEvent(new AxArtifactKey("AutoLearnTriggerEvent", "0.0.1"));
        final double rval = rand.nextGaussian();
        triggerEvent.put("MonitoredValue", rval);
        triggerEvent.put("LastMonitoredValue", 0D);

        LOGGER.info(TRIGGER_MESSAGE, triggerEvent);
        apexEngine1.handleEvent(triggerEvent);
        final EnEvent result = listener1.getResult();
        LOGGER.info(RECEIVING_ACTION_EVENT, result);
        assertEquals(triggerEvent.getExecutionId(), result.getExecutionId(), "ExecutionIDs are different");
        triggerEvent.clear();
        result.clear();
        await().atLeast(10, TimeUnit.MILLISECONDS).until(() -> triggerEvent.isEmpty() && result.isEmpty());
        apexEngine1.stop();
    }

    /**
     * This policy passes, and receives a Double event context filed called "EVCDouble"<br>
     * The policy tries to keep the value at 50, with a Min -100, Max 100 (These should probably be set using
     * TaskParameters!)<br>
     * The policy has 7 Decide Tasks that manipulate the value of this field in unknown ways.<br>
     * The Decide TSL learns the effect of each task, and then selects the appropriate task to get the value back to
     * 50<br>
     * After the value settles close to 50 for a while, the test Rests the value to to random number and then
     * continues<br>
     * To plot the results grep stdout debug results for the string "*******", paste into excel and delete non-relevant
     * columns<br>
     *
     * @throws ApexException the apex exception
     */
    // @Test
    void testAutoLearnTslMain() throws ApexException {

        final double dwant = 50.0;
        final double toleranceTileJump = 3.0;

        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAutoLearnPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");
        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        parameters.getExecutorParameterMap().put("JAVA", new JavaExecutorParameters());

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener1", listener1);
        apexEngine1.updateModel(apexPolicyModel, false);
        apexEngine1.start();

        final EnEvent triggerEvent = apexEngine1.createEvent(new AxArtifactKey("AutoLearnTriggerEvent", "0.0.1"));
        assertNotNull(triggerEvent);
        final double dmin = -100;
        final double dmax = 100;

        double rval = (((rand.nextGaussian() + 1) / 2) * (dmax - dmin)) + dmin;
        triggerEvent.put("MonitoredValue", rval);
        triggerEvent.put("LastMonitoredValue", 0);

        double avval = 0;
        double distance;
        double avcount = 0;

        for (int iteration = 0; iteration < MAXITERATIONS; iteration++) {
            // Trigger the policy in engine 1
            LOGGER.info(TRIGGER_MESSAGE, triggerEvent);
            apexEngine1.handleEvent(triggerEvent);
            final EnEvent result = listener1.getResult();
            LOGGER.info(RECEIVING_ACTION_EVENT, result);
            triggerEvent.clear();

            double val = (Double) result.get("MonitoredValue");
            final double prevval = (Double) result.get("LastMonitoredValue");

            triggerEvent.put("MonitoredValue", prevval);
            triggerEvent.put("LastMonitoredValue", val);

            avcount = Math.min((avcount + 1), 20); // maintain average of only the last 20 values
            avval = ((avval * (avcount - 1)) + val) / (avcount);

            distance = Math.abs(dwant - avval);
            if (distance < toleranceTileJump) {
                rval = (((rand.nextGaussian() + 1) / 2) * (dmax - dmin)) + dmin;
                val = rval;
                triggerEvent.put("MonitoredValue", val);
                LOGGER.info("Iteration " + iteration + ": Average " + avval + " has become closer (" + distance
                    + ") than " + toleranceTileJump + " to " + dwant + " so reseting val:\t\t\t\t\t\t\t\t" + val);
                avval = 0;
                avcount = 0;
            }
            LOGGER.info("Iteration " + iteration + ": \tpreval\t" + prevval + "\tval\t" + val + "\tavval\t" + avval);

            result.clear();
            await().atLeast(10, TimeUnit.MILLISECONDS).until(() -> !result.isEmpty());
        }

        apexEngine1.stop();
        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> apexEngine1.getState().equals(AxEngineState.STOPPED));
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws ApexException the apex exception
     */
    public static void main(final String[] args) throws ApexException {
        new AutoLearnTslUseCaseTest().testAutoLearnTslMain();
    }
}
