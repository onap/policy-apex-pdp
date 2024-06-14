/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.FileCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;

class ApexFileEventProducerTest {
    private final PrintStream out = System.out;
    private final Random random = new Random();
    private ApexFileEventProducer eventProducer;
    private EventHandlerParameters parameters;
    private FileCarrierTechnologyParameters technologyParameters;

    /**
     * Prepare for testing.
     */
    @BeforeEach
    void setUp() {
        eventProducer = new ApexFileEventProducer();
        parameters = new EventHandlerParameters();
        technologyParameters = new FileCarrierTechnologyParameters();
    }

    @AfterEach
    void tearDown() {
        System.setOut(out);
    }

    @Test
    void initNullParams() {
        final String name = RandomStringUtils.randomAlphabetic(5);
        assertThatThrownBy(() -> eventProducer.init(name, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void initParamsInvalidCarrier() {
        final String name = RandomStringUtils.randomAlphabetic(5);
        parameters.setCarrierTechnologyParameters(new SuperDooperCarrierTechnologyParameters());
        assertThatThrownBy(() -> eventProducer.init(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void initParamsWithStandardError() {
        final String name = RandomStringUtils.randomAlphabetic(5);
        technologyParameters.setStandardError(true);

        parameters.setCarrierTechnologyParameters(technologyParameters);

        assertThatCode(() -> eventProducer.init(name, parameters))
            .doesNotThrowAnyException();
    }

    @Test
    void initParamsWithStandardIo() {
        final String name = RandomStringUtils.randomAlphabetic(5);
        technologyParameters.setStandardIo(true);

        parameters.setCarrierTechnologyParameters(technologyParameters);
        assertThatCode(() -> eventProducer.init(name, parameters))
            .doesNotThrowAnyException();
    }

    @Test
    void initParamsWithFileIsDirectory() {
        final String name = RandomStringUtils.randomAlphabetic(5);
        final String fileName = System.getProperty("user.dir");

        technologyParameters.setFileName(fileName);

        parameters.setCarrierTechnologyParameters(technologyParameters);
        assertThatThrownBy(() -> eventProducer.init(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void sendEventWrongEvent() throws ApexEventException {
        final long executionId = random.nextLong();
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String producerName = RandomStringUtils.randomAlphanumeric(5);
        final Object event = new Object();

        technologyParameters.setStandardIo(true);
        parameters = new EventHandlerParameters();
        parameters.setCarrierTechnologyParameters(technologyParameters);

        ApexFileEventProducer apexFileEventProducer = new ApexFileEventProducer();
        apexFileEventProducer.init(producerName, parameters);
        assertThatThrownBy(() -> apexFileEventProducer.sendEvent(executionId, null, eventName, event))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void sendEvent() throws ApexEventException {
        // Prepare output stream to read data
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Prepare producer
        final String producerName = RandomStringUtils.randomAlphanumeric(5);
        technologyParameters.setStandardIo(true);
        parameters = new EventHandlerParameters();
        parameters.setCarrierTechnologyParameters(technologyParameters);
        eventProducer.init(producerName, parameters);

        // Prepare for sending
        final long executionId = random.nextLong();
        final String eventName = RandomStringUtils.randomAlphanumeric(4);
        final String event = RandomStringUtils.randomAlphabetic(6);
        eventProducer.sendEvent(executionId, null, eventName, event);

        // Check results
        assertThat(outContent.toString()).contains(event);
    }

}