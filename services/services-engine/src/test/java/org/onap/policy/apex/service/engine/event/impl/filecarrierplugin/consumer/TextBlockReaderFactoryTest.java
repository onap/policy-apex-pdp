/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021  Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

public class TextBlockReaderFactoryTest {
    private TextBlockReaderFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new TextBlockReaderFactory();
    }

    @Test
    public void getTaggedReaderTextCharDelimitedParametersParams() throws ApexEventException {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        final EventProtocolParameters parameters = new JsonEventProtocolParameters();

        final TextBlockReader actual = factory.getTaggedReader(inputStream, parameters);
        assertNotNull(actual);
        assertFalse(actual instanceof HeaderDelimitedTextBlockReader);
    }

    @Test
    public void getTaggedReaderTextTokenDelimitedParams() throws ApexEventException {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        new ApexEventProtocolParameters();
        final EventProtocolParameters parameters = new SuperTokenDelimitedEventProtocolParameters();

        final TextBlockReader actual = factory.getTaggedReader(inputStream, parameters);
        assertTrue(actual instanceof HeaderDelimitedTextBlockReader);
    }

    @Test(expected = ApexEventException.class)
    public void getTaggedReaderNotSupportedParams() throws ApexEventException {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();

        final TextBlockReader actual = factory.getTaggedReader(inputStream, parameters);
        assertTrue(actual instanceof HeaderDelimitedTextBlockReader);
    }


    private InputStream prepareInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }
}