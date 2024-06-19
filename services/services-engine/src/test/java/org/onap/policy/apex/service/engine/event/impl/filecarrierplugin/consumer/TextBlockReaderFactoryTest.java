/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

class TextBlockReaderFactoryTest {
    private TextBlockReaderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TextBlockReaderFactory();
    }

    @Test
    void getTaggedReaderTextCharDelimitedParametersParams() throws ApexEventException {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        final EventProtocolParameters parameters = new JsonEventProtocolParameters();

        final TextBlockReader actual = factory.getTaggedReader(inputStream, parameters);
        assertThat(actual).isNotInstanceOf(HeaderDelimitedTextBlockReader.class);
    }

    @Test
    void getTaggedReaderTextTokenDelimitedParams() throws ApexEventException {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        new ApexEventProtocolParameters();
        final EventProtocolParameters parameters = new SuperTokenDelimitedEventProtocolParameters();

        final TextBlockReader actual = factory.getTaggedReader(inputStream, parameters);
        assertThat(actual).isInstanceOf(HeaderDelimitedTextBlockReader.class);
    }

    @Test
    void getTaggedReaderNotSupportedParams() {
        final String text = RandomStringUtils.randomAlphanumeric(22);
        final InputStream inputStream = prepareInputStream(text);
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();
        assertThatThrownBy(() -> factory.getTaggedReader(inputStream, parameters))
            .isInstanceOf(ApexEventException.class);
    }


    private InputStream prepareInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }
}