/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.protocol.jms;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.TextBlock;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.common.parameters.ParameterService;

public class Apex2JmsTextEventConverterTest {
    private Apex2JmsTextEventConverter converter;

    @Before
    public void setUp() {
        converter = new Apex2JmsTextEventConverter();
        ModelService.registerModel(AxContextSchemas.class, new AxContextSchemas());
        ModelService.registerModel(AxEvents.class, new AxEvents());
        ParameterService.register(new SchemaParameters());
    }

    @After
    public void tearDown() {
        ModelService.deregisterModel(AxContextSchema.class);
        ModelService.deregisterModel(AxEvents.class);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    public void toApexEventNull() {
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, null))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void toApexEventObject() {
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        assertThatThrownBy(() -> converter.toApexEvent(eventName, new Object()))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void toApexEventJsonString() throws ApexEventException {
        final String eventName = RandomStringUtils.randomAlphabetic(4);
        final String eventVersion = "0.0.1";
        final String source = RandomStringUtils.randomAlphabetic(5);
        final String target = RandomStringUtils.randomAlphabetic(6);
        final String nameSpace = "a.name.space";

        // Prepare Json String to be translated into ApexEvent
        final TextBlock object =
            new TextBlock(false, "{\"name\": \"" + eventName + "\", \"version\":\"" + eventVersion + "\"}");

        // Prepare Model service
        final AxArtifactKey eventKey = new AxArtifactKey(eventName + ":" + eventVersion);
        final AxEvent axEvent = new AxEvent(eventKey, nameSpace, source, target);
        ModelService.getModel(AxEvents.class).getEventMap().put(eventKey, axEvent);

        // prepare converter
        converter.init(new JsonEventProtocolParameters());

        // execute test
        final List<ApexEvent> apexEvents = converter.toApexEvent(eventName, object);

        final ApexEvent expectedEvent = new ApexEvent(eventName, eventVersion, nameSpace, source, target);

        // Reset executionId
        expectedEvent.setExecutionId(0);
        for (ApexEvent event : apexEvents) {
            event.setExecutionId(0);
        }
        Object[] expected = {expectedEvent};

        assertArrayEquals(expected, apexEvents.toArray());
    }

    @Test
    public void fromApexNull() {
        assertThatThrownBy(() -> converter.fromApexEvent(null)).isInstanceOf(ApexEventException.class);
    }

    @Test
    public void fromApex() throws ApexEventException {
        final String name = RandomStringUtils.randomAlphabetic(4);
        final String version = "0.2.3";
        final String nameSpace = "a.name.space";
        final String source = RandomStringUtils.randomAlphabetic(6);
        final String target = RandomStringUtils.randomAlphabetic(7);

        final String expected = "{\n" +
            "  \"name\": \"" + name + "\",\n" +
            "  \"version\": \"" + version + "\",\n" +
            "  \"nameSpace\": \"" + nameSpace + "\",\n" +
            "  \"source\": \"" + source + "\",\n" +
            "  \"target\": \"" + target + "\"\n" +
            "}";

        // Prepare Model service
        final AxArtifactKey eventKey = new AxArtifactKey(name + ":" + version);
        final AxEvent axEvent = new AxEvent(eventKey, nameSpace, source, target);
        ModelService.getModel(AxEvents.class).getEventMap().put(eventKey, axEvent);

        converter.init(new JsonEventProtocolParameters());

        final ApexEvent apexEvent = new ApexEvent(name, version, nameSpace, source, target);
        final Object actual = converter.fromApexEvent(apexEvent);

        assertEquals(expected, actual);
    }

}
