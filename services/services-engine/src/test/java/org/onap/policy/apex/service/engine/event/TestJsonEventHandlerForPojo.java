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

package org.onap.policy.apex.service.engine.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.testpojos.TestPojo;
import org.onap.policy.apex.service.engine.event.testpojos.TestPojoList;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Test JSON Event Handler.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestJsonEventHandlerForPojo {
    private static final XLogger logger = XLoggerFactory.getXLogger(TestJsonEventHandlerForPojo.class);

    /**
     * Setup event model.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexModelException the apex model exception
     */
    @BeforeClass
    public static void setupEventModel() throws IOException, ApexModelException {
        final String policyModelString = TextFileUtils
                        .getTextFileAsString("src/test/resources/policymodels/PojoEventModel.json");
        final ApexModelReader<AxPolicyModel> modelReader = new ApexModelReader<AxPolicyModel>(AxPolicyModel.class);
        modelReader.setValidateFlag(false);
        final AxPolicyModel apexPolicyModel = modelReader.read(new ByteArrayInputStream(policyModelString.getBytes()));

        // Set up the models in the model service
        apexPolicyModel.register();
    }

    /**
     * Initialize default schema parameters.
     */
    @BeforeClass
    public static void initializeDefaultSchemaParameters() {
        ParameterService.clear();
        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.register(schemaParameters);
    }

    /**
     * Teardown default schema parameters.
     */
    @AfterClass
    public static void teardownDefaultSchemaParameters() {
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    /**
     * Test POJO to apex event and back.
     *
     * @throws ApexException the apex exception
     * @throws IOException on IO exceptions
     */
    @Test
    public void testJsonPojotoApexEvent() throws ApexException, IOException {
        final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
        assertNotNull(jsonEventConverter);

        JsonEventProtocolParameters pars = new JsonEventProtocolParameters();
        pars.setPojoField("POJO_PAR");
        jsonEventConverter.init(pars);

        final String apexEventJsonStringIn = TextFileUtils
                        .getTextFileAsString("src/test/resources/events/TestPojoEvent.json");

        logger.debug("input event\n" + apexEventJsonStringIn);

        final List<ApexEvent> apexEventList = jsonEventConverter.toApexEvent("PojoEvent", apexEventJsonStringIn);
        assertEquals(1, apexEventList.size());
        final ApexEvent apexEvent = apexEventList.get(0);
        assertNotNull(apexEvent);

        logger.debug(apexEvent.toString());

        assertEquals("PojoEvent", apexEvent.getName());
        assertEquals("0.0.1", apexEvent.getVersion());
        assertEquals("org.onap.policy.apex.service.engine.event.testpojos", apexEvent.getNameSpace());
        assertEquals("Outside", apexEvent.getSource());
        assertEquals("Apex", apexEvent.getTarget());

        TestPojo testPojo = (TestPojo) apexEvent.get("POJO_PAR");

        assertEquals(1, testPojo.getAnInt());
        assertEquals(2, testPojo.getAnInteger().intValue());
        assertEquals("a string", testPojo.getSomeString());

        assertEquals(10, testPojo.getTestSubPojo().getAnInt());
        assertEquals(20, testPojo.getTestSubPojo().getAnInteger().intValue());
        assertEquals("a sub string", testPojo.getTestSubPojo().getSomeString());

        assertEquals(100, testPojo.getTestSubPojo().getTestSubSubPojo().getAnInt());
        assertEquals(200, testPojo.getTestSubPojo().getTestSubSubPojo().getAnInteger().intValue());
        assertEquals("a sub sub string", testPojo.getTestSubPojo().getTestSubSubPojo().getSomeString());

        String eventBackInJson = (String)jsonEventConverter.fromApexEvent(apexEvent);
        assertEquals(apexEventJsonStringIn.replaceAll("\\s+", ""), eventBackInJson.replaceAll("\\s+", ""));
    }

    /**
     * Test POJO List to apex event and back.
     *
     * @throws ApexException the apex exception
     * @throws IOException on IO exceptions
     */
    @Test
    public void testJsonPojoListToApexEvent() throws ApexException, IOException {
        final Apex2JsonEventConverter jsonEventConverter = new Apex2JsonEventConverter();
        assertNotNull(jsonEventConverter);

        JsonEventProtocolParameters pars = new JsonEventProtocolParameters();
        pars.setPojoField("POJO_LIST_PAR");
        jsonEventConverter.init(pars);

        final String apexEventJsonStringIn = TextFileUtils
                        .getTextFileAsString("src/test/resources/events/TestPojoListEvent.json");

        logger.debug("input event\n" + apexEventJsonStringIn);

        final List<ApexEvent> apexEventList = jsonEventConverter.toApexEvent("PojoListEvent", apexEventJsonStringIn);
        assertEquals(1, apexEventList.size());
        final ApexEvent apexEvent = apexEventList.get(0);
        assertNotNull(apexEvent);

        logger.debug(apexEvent.toString());

        assertEquals("PojoListEvent", apexEvent.getName());
        assertEquals("0.0.1", apexEvent.getVersion());
        assertEquals("org.onap.policy.apex.service.engine.event.testpojos", apexEvent.getNameSpace());
        assertEquals("Outside", apexEvent.getSource());
        assertEquals("Apex", apexEvent.getTarget());

        TestPojoList testPojoList = (TestPojoList) apexEvent.get("POJO_LIST_PAR");

        for (TestPojo testPojo: testPojoList.getTestPojoList()) {
            assertEquals(1, testPojo.getAnInt());
            assertEquals(2, testPojo.getAnInteger().intValue());
            assertEquals("a string", testPojo.getSomeString());

            assertEquals(10, testPojo.getTestSubPojo().getAnInt());
            assertEquals(20, testPojo.getTestSubPojo().getAnInteger().intValue());
            assertEquals("a sub string", testPojo.getTestSubPojo().getSomeString());

            assertEquals(100, testPojo.getTestSubPojo().getTestSubSubPojo().getAnInt());
            assertEquals(200, testPojo.getTestSubPojo().getTestSubSubPojo().getAnInteger().intValue());
            assertEquals("a sub sub string", testPojo.getTestSubPojo().getTestSubSubPojo().getSomeString());
        }
        String eventBackInJson = (String)jsonEventConverter.fromApexEvent(apexEvent);
        assertEquals(apexEventJsonStringIn.replaceAll("\\s+", ""), eventBackInJson.replaceAll("\\s+", ""));
    }
}
