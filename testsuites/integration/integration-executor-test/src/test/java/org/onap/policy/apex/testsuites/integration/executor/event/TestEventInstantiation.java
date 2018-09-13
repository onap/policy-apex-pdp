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

package org.onap.policy.apex.testsuites.integration.executor.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.apex.testsuites.integration.common.model.SampleDomainModelFactory;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestEventInstantiation.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestEventInstantiation {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestEventInstantiation.class);

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;
    private EngineParameters engineParameters;

    /**
     * Before test.
     */
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
        engineParameters.getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        ParameterService.register(engineParameters);
    }

    /**
     * After test.
     */
    @After
    public void afterTest() {
        ParameterService.deregister(engineParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    /**
     * Test event instantiation.
     *
     * @throws ApexModelException on errors in handling Apex models
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexException the apex exception
     */
    @Test
    public void testEventInstantiation() throws ApexModelException, IOException, ApexException {
        final String xmlFileName = "xml/ApexModel_MVEL.xml";

        logger.debug("Running TestEventInstantiation test  on file {} . . .", xmlFileName);

        final AxPolicyModel apexPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(apexPolicyModel);

        final ApexEngine apexEngine = new ApexEngineFactory().createApexEngine(apexPolicyModel.getKey());
        apexEngine.updateModel(apexPolicyModel);
        apexEngine.start();

        final EnEvent event = apexEngine.createEvent(new AxArtifactKey("Event0000", "0.0.1"));

        Object slogan1 = event.put("TestSlogan", "This is a slogan");
        assertNull(slogan1);
        slogan1 = event.get("TestSlogan");
        assertNotNull(slogan1);
        assertEquals("This is a slogan", slogan1);

        Object mc1 = event.put("TestMatchCase", new Byte("4"));
        assertNull(mc1);
        mc1 = event.get("TestMatchCase");
        assertNotNull(mc1);
        assertEquals((byte) 4, mc1);

        Object mc2 = event.put("TestMatchCase", new Byte("16"));
        assertNotNull(mc2);
        assertEquals((byte) 4, mc2);
        mc2 = event.get("TestMatchCase");
        assertNotNull(mc2);
        assertEquals((byte) 16, mc2);

        final Date timeNow = new Date();
        Object timestamp1 = event.put("TestTimestamp", timeNow.getTime());
        assertNull(timestamp1);
        timestamp1 = event.get("TestTimestamp");
        assertNotNull(timestamp1);
        assertEquals(timeNow.getTime(), timestamp1);

        final double temperature = 123.456789;
        Object temp1 = event.put("TestTemperature", temperature);
        assertNull(temp1);
        temp1 = event.get("TestTemperature");
        assertNotNull(temp1);
        assertEquals(temperature, temp1);

        Object value = event.put("TestMatchCase", null);
        assertEquals(16, ((Byte) value).intValue());
        value = event.get("TestMatchCase");
        assertNull(value);

        try {
            event.put("TestMatchCase", "Hello");
        } catch (final Exception e) {
            assertEquals("Event0000:0.0.1:NULL:TestMatchCase: object \"Hello\" of class \"java.lang.String\" "
                            + "not compatible with class \"java.lang.Byte\"", e.getMessage());
        }

        try {
            event.put("TestMatchCase", 123.45);
        } catch (final Exception e) {
            assertEquals("Event0000:0.0.1:NULL:TestMatchCase: object \"123.45\" of class \"java.lang.Double\" "
                            + "not compatible with class \"java.lang.Byte\"", e.getMessage());
        }

        event.put("TestMatchCase", new Byte("16"));

        final String slogan2 = (String) event.get("TestSlogan");
        assertNotNull(slogan2);
        assertEquals("This is a slogan", slogan2);

        final byte mc21 = (byte) event.get("TestMatchCase");
        assertNotNull(mc21);
        assertEquals(16, mc21);

        final byte mc22 = (byte) event.get("TestMatchCase");
        assertNotNull(mc22);
        assertEquals((byte) 16, mc22);

        final long timestamp2 = (Long) event.get("TestTimestamp");
        assertNotNull(timestamp2);
        assertEquals(timestamp2, timestamp1);

        final double temp2 = (double) event.get("TestTemperature");
        assertNotNull(temp2);
        assertTrue(temp2 == 123.456789);

        final Double temp3 = (Double) event.get("TestTemperature");
        assertNotNull(temp3);
        assertTrue(temp3 == 123.456789);

        final Date aDate = new Date(1433453067123L);
        final Map<String, Object> eventDataList = new HashMap<String, Object>();
        eventDataList.put("TestSlogan", "This is a test slogan");
        eventDataList.put("TestMatchCase", new Byte("123"));
        eventDataList.put("TestTimestamp", aDate.getTime());
        eventDataList.put("TestTemperature", 34.5445667);

        event.putAll(eventDataList);

        final String slogan3 = (String) event.get("TestSlogan");
        assertNotNull(slogan3);
        assertEquals("This is a test slogan", slogan3);

        final byte mc31 = (byte) event.get("TestMatchCase");
        assertNotNull(mc31);
        assertEquals((byte) 123, mc31);

        final long timestamp3 = (Long) event.get("TestTimestamp");
        assertNotNull(timestamp3);
        assertEquals(timestamp3, aDate.getTime());

        final double temp4 = (double) event.get("TestTemperature");
        assertNotNull(temp4);
        assertTrue(temp4 == 34.5445667);

        logger.debug(event.toString());
    }
}
