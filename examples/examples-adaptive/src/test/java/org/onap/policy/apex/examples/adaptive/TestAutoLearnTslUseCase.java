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
 * Test Auto learning in TSL.
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
public class TestAutoLearnTslUseCase {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestAutoLearnTslUseCase.class);

    private static final int MAXITERATIONS = 1000;
    private static final Random rand = new Random(System.currentTimeMillis());

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
    public void testAutoLearnTsl() throws ApexException, InterruptedException, IOException {
        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAutoLearnPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener", listener1);
        apexEngine1.updateModel(apexPolicyModel);
        apexEngine1.start();
        final EnEvent triggerEvent = apexEngine1.createEvent(new AxArtifactKey("AutoLearnTriggerEvent", "0.0.1"));
        final double rval = rand.nextGaussian();
        triggerEvent.put("MonitoredValue", rval);
        triggerEvent.put("LastMonitoredValue", 0D);
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
     * @throws InterruptedException the interrupted exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    // @Test
    public void testAutoLearnTslMain() throws ApexException, InterruptedException, IOException {

        final double dwant = 50.0;
        final double toleranceTileJump = 3.0;

        final AxPolicyModel apexPolicyModel = new AdaptiveDomainModelFactory().getAutoLearnPolicyModel();
        assertNotNull(apexPolicyModel);

        final AxValidationResult validationResult = new AxValidationResult();
        apexPolicyModel.validate(validationResult);
        assertTrue(validationResult.isValid());

        final AxArtifactKey key = new AxArtifactKey("AADMApexEngine", "0.0.1");
        final EngineParameters parameters = new EngineParameters();
        parameters.getExecutorParameterMap().put("MVEL", new MVELExecutorParameters());
        parameters.getExecutorParameterMap().put("JAVA", new JavaExecutorParameters());

        final ApexEngine apexEngine1 = new ApexEngineFactory().createApexEngine(key);

        final TestApexActionListener listener1 = new TestApexActionListener("TestListener1");
        apexEngine1.addEventListener("listener1", listener1);
        apexEngine1.updateModel(apexPolicyModel);
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
            LOGGER.info("Triggering policy in Engine 1 with " + triggerEvent);
            apexEngine1.handleEvent(triggerEvent);
            final EnEvent result = listener1.getResult();
            LOGGER.info("Receiving action event {} ", result);
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
            Thread.sleep(1);
        }

        apexEngine1.stop();
        Thread.sleep(1000);

    }

    public static void main(final String[] args) throws ApexException, InterruptedException, IOException {
        new TestAutoLearnTslUseCase().testAutoLearnTslMain();
    }
}
