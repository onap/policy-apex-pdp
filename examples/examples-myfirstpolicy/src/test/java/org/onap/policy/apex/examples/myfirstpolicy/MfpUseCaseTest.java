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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineFactory;
import org.onap.policy.apex.core.engine.engine.impl.ApexEngineImpl;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.javascript.JavascriptExecutorParameters;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * Test MyFirstPolicy Use Case.
 */
class MfpUseCaseTest {
    // CHECKSTYLE:OFF: MagicNumber

    private static ApexEngineImpl apexEngine;

    /**
     * Test MFP use case setup.
     */
    @BeforeAll
    static void testMfpUseCaseSetup() {
        final AxArtifactKey key = new AxArtifactKey("MyFirstPolicyApexEngine", "0.0.1");
        apexEngine = (ApexEngineImpl) new ApexEngineFactory().createApexEngine(key);
    }

    private static ContextParameters contextParameters;
    private static SchemaParameters schemaParameters;
    private static EngineParameters engineParameters;

    /**
     * Before test.
     */
    @BeforeAll
    static void beforeTest() {
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
        engineParameters.getExecutorParameterMap().put("JAVASCRIPT", new JavascriptExecutorParameters());
        ParameterService.register(engineParameters);
    }

    /**
     * After test.
     */
    @AfterAll
    static void afterTest() {
        ParameterService.deregister(engineParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);

        ParameterService.deregister(schemaParameters);
    }

    /**
     * Test MyFirstPolicy#1 use case.
     *
     * @throws ApexException if there is an Apex error
     */
    @Test
    void testMfp1Case() throws ApexException {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp1PolicyModel();
        assertNotNull(apexPolicyModel);

        final TestSaleAuthListener listener = new TestSaleAuthListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel, false);
        apexEngine.start();

        final AxEvent axEventin = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_INPUT:0.0.1"));
        assertNotNull(axEventin);
        final AxEvent axEventout = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_AUTH:0.0.1"));
        assertNotNull(axEventout);

        EnEvent event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_084106GMT.json");
        apexEngine.handleEvent(event);
        EnEvent resultout = listener.getResult();
        EnEvent resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_084106GMT.json");
        assertEquals(resulexpected, resultout);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_201713GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_201713GMT.json");
        assertHandledEventResult(resulexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_NonBoozeItem_101309GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_NonBoozeItem_101309GMT.json");
        assertHandledEventResult(resulexpected, resultout, event);

        apexEngine.stop();
    }

    /**
     * Test MyFirstPolicy#1 use case.
     *
     * @throws ApexException if there is an Apex error
     */
    @Test
    void testMfp1AltCase() throws ApexException {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp1AltPolicyModel();
        assertNotNull(apexPolicyModel);

        final TestSaleAuthListener listener = new TestSaleAuthListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel, false);
        apexEngine.start();

        final AxEvent axEventin = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_INPUT:0.0.1"));
        assertNotNull(axEventin);
        final AxEvent axEventout = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_AUTH:0.0.1"));
        assertNotNull(axEventout);

        EnEvent event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_084106GMT.json");
        apexEngine.handleEvent(event);
        EnEvent resultout = listener.getResult();
        EnEvent resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_084106GMT.json");
        resultout.put("message", "");
        resulexpected.put("message", "");
        assertEquals(resulexpected, resultout);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_201713GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_201713GMT.json");
        resultout.put("message", "");
        resulexpected.put("message", "");
        assertHandledEventResult(resulexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_NonBoozeItem_101309GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resulexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_NonBoozeItem_101309GMT.json");
        resultout.put("message", "");
        resulexpected.put("message", "");
        assertHandledEventResult(resulexpected, resultout, event);

        apexEngine.stop();
    }

    /**
     * Test MyFirstPolicy#2 use case.
     *
     * @throws ApexException if there is an Apex error
     */
    @Test
    void testMfp2Case() throws ApexException {
        final AxPolicyModel apexPolicyModel = new MfpDomainModelFactory().getMfp2PolicyModel();
        assertNotNull(apexPolicyModel);

        final TestSaleAuthListener listener = new TestSaleAuthListener("Test");
        apexEngine.addEventListener("listener", listener);
        apexEngine.updateModel(apexPolicyModel, false);
        apexEngine.start();

        final AxEvent axEventin = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_INPUT:0.0.1"));
        assertNotNull(axEventin);
        final AxEvent axEventout = apexPolicyModel.getEvents().get(new AxArtifactKey("SALE_AUTH:0.0.1"));
        assertNotNull(axEventout);

        EnEvent event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_084106GMT.json");
        apexEngine.handleEvent(event);
        EnEvent resultout = listener.getResult();
        EnEvent resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_084106GMT.json");
        assertHandledEventResult(resultexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_BoozeItem_201713GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_BoozeItem_201713GMT.json");
        assertHandledEventResult(resultexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/1/EventIn_NonBoozeItem_101309GMT.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/1/EventOut_NonBoozeItem_101309GMT.json");
        assertHandledEventResult(resultexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/2/EventIn_BoozeItem_101433CET_thurs.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/2/EventOut_BoozeItem_101433CET_thurs.json");
        assertHandledEventResult(resultexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/2/EventIn_BoozeItem_171937CET_sun.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/2/EventOut_BoozeItem_171937CET_sun.json");
        assertHandledEventResult(resultexpected, resultout, event);

        event = fillTriggerEvent(axEventin, "examples/events/MyFirstPolicy/2/EventIn_NonBoozeItem_111309CET_mon.json");
        apexEngine.handleEvent(event);
        resultout = listener.getResult();
        resultexpected =
            fillResultEvent(axEventout, "examples/events/MyFirstPolicy/2/EventOut_NonBoozeItem_111309CET_mon.json");
        assertHandledEventResult(resultexpected, resultout, event);

        apexEngine.stop();
    }

    private static void assertHandledEventResult(EnEvent resultexpected, EnEvent resultout, EnEvent event) {
        assertEquals(resultexpected, resultout);
        assertEquals(event.getExecutionId(), resultout.getExecutionId());
    }

    /**
     * Fill trigger event for test.
     *
     * @param event     the event
     * @param inputFile the input file
     * @return the filled event
     */
    private EnEvent fillTriggerEvent(final AxEvent event, final String inputFile) {
        final EnEvent ret = new EnEvent(event.getKey());
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final JsonObject jsonObject =
            gb.create().fromJson(ResourceUtils.getResourceAsString(inputFile), JsonObject.class);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.has("name"));
        assertEquals(ret.getName(), jsonObject.get("name").getAsString());
        assertEquals(ret.getAxEvent().getKey().getName(), jsonObject.get("name").getAsString());
        assertTrue(jsonObject.has("nameSpace"));
        assertEquals(ret.getAxEvent().getNameSpace(), jsonObject.get("nameSpace").getAsString());
        assertTrue(jsonObject.has("version"));
        assertEquals(ret.getAxEvent().getKey().getVersion(), jsonObject.get("version").getAsString());
        final List<String> reserved = Arrays.asList("name", "nameSpace", "version", "source", "target");
        for (final Map.Entry<String, ?> e : jsonObject.entrySet()) {
            if (reserved.contains(e.getKey())) {
                continue;
            }
            assertTrue((event.getParameterMap().containsKey(e.getKey())),
                "Event file " + inputFile + " has a field " + e.getKey() + " but this is not defined for "
                    + event.getId());
            if (jsonObject.get(e.getKey()).isJsonNull()) {
                ret.put(e.getKey(), null);
            }
        }
        for (final AxField field : event.getFields()) {
            if (!field.getOptional()) {
                assertTrue(jsonObject.has(field.getKey().getLocalName()),
                    "Event file " + inputFile + " is missing a mandatory field " + field.getKey().getLocalName()
                        + " for " + event.getId());
            } else {
                ret.put(field.getKey().getLocalName(), null);
            }
        }
        if (jsonObject.has("time") && !jsonObject.get("time").isJsonNull()) {
            ret.put("time", jsonObject.get("time").getAsLong());
        }
        if (jsonObject.has("sale_ID") && !jsonObject.get("sale_ID").isJsonNull()) {
            ret.put("sale_ID", jsonObject.get("sale_ID").getAsLong());
        }
        if (jsonObject.has("amount") && !jsonObject.get("amount").isJsonNull()) {
            ret.put("amount", jsonObject.get("amount").getAsDouble());
        }
        if (jsonObject.has("item_ID") && !jsonObject.get("item_ID").isJsonNull()) {
            ret.put("item_ID", jsonObject.get("item_ID").getAsLong());
        }
        if (jsonObject.has("quantity") && !jsonObject.get("quantity").isJsonNull()) {
            ret.put("quantity", jsonObject.get("quantity").getAsInt());
        }
        if (jsonObject.has("assistant_ID") && !jsonObject.get("assistant_ID").isJsonNull()) {
            ret.put("assistant_ID", jsonObject.get("assistant_ID").getAsLong());
        }
        if (jsonObject.has("branch_ID") && !jsonObject.get("branch_ID").isJsonNull()) {
            ret.put("branch_ID", jsonObject.get("branch_ID").getAsLong());
        }
        if (jsonObject.has("notes") && !jsonObject.get("notes").isJsonNull()) {
            ret.put("notes", jsonObject.get("notes").getAsString());
        }
        return ret;
    }

    /**
     * Fill result event for test.
     *
     * @param event     the event
     * @param inputFile the input file
     * @return the filled event
     */
    private EnEvent fillResultEvent(final AxEvent event, final String inputFile) {
        final EnEvent ret = new EnEvent(event.getKey());
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final JsonObject jsonObject =
            gb.create().fromJson(ResourceUtils.getResourceAsString(inputFile), JsonObject.class);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.has("name"));
        assertEquals(ret.getName(), jsonObject.get("name").getAsString());
        assertEquals(ret.getAxEvent().getKey().getName(), jsonObject.get("name").getAsString());
        assertTrue(jsonObject.has("nameSpace"));
        assertEquals(ret.getAxEvent().getNameSpace(), jsonObject.get("nameSpace").getAsString());
        assertTrue(jsonObject.has("version"));
        assertEquals(ret.getAxEvent().getKey().getVersion(), jsonObject.get("version").getAsString());
        final List<String> reserved = Arrays.asList("name", "nameSpace", "version", "source", "target");
        for (final Map.Entry<String, ?> e : jsonObject.entrySet()) {
            if (reserved.contains(e.getKey())) {
                continue;
            }
            assertTrue((event.getParameterMap().containsKey(e.getKey())),
                "Event file " + inputFile + " has a field " + e.getKey() + " but this is not defined for "
                    + event.getId());
            if (jsonObject.get(e.getKey()).isJsonNull()) {
                ret.put(e.getKey(), null);
            }
        }
        for (final AxField field : event.getFields()) {
            if (!field.getOptional()) {
                assertTrue(jsonObject.has(field.getKey().getLocalName()),
                    "Event file " + inputFile + " is missing a mandatory field " + field.getKey().getLocalName()
                        + " for " + event.getId());
            } else {
                ret.put(field.getKey().getLocalName(), null);
            }
        }
        if (jsonObject.has("time") && !jsonObject.get("time").isJsonNull()) {
            ret.put("time", jsonObject.get("time").getAsLong());
        }
        if (jsonObject.has("sale_ID") && !jsonObject.get("sale_ID").isJsonNull()) {
            ret.put("sale_ID", jsonObject.get("sale_ID").getAsLong());
        }
        if (jsonObject.has("amount") && !jsonObject.get("amount").isJsonNull()) {
            ret.put("amount", jsonObject.get("amount").getAsDouble());
        }
        if (jsonObject.has("item_ID") && !jsonObject.get("item_ID").isJsonNull()) {
            ret.put("item_ID", jsonObject.get("item_ID").getAsLong());
        }
        if (jsonObject.has("quantity") && !jsonObject.get("quantity").isJsonNull()) {
            ret.put("quantity", jsonObject.get("quantity").getAsInt());
        }
        if (jsonObject.has("assistant_ID") && !jsonObject.get("assistant_ID").isJsonNull()) {
            ret.put("assistant_ID", jsonObject.get("assistant_ID").getAsLong());
        }
        if (jsonObject.has("branch_ID") && !jsonObject.get("branch_ID").isJsonNull()) {
            ret.put("branch_ID", jsonObject.get("branch_ID").getAsLong());
        }
        if (jsonObject.has("notes") && !jsonObject.get("notes").isJsonNull()) {
            ret.put("notes", jsonObject.get("notes").getAsString());
        }
        if (jsonObject.has("authorised") && !jsonObject.get("authorised").isJsonNull()) {
            ret.put("authorised", jsonObject.get("authorised").getAsString());
        }
        if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
            ret.put("message", jsonObject.get("message").getAsString());
        }
        return ret;
    }
}
