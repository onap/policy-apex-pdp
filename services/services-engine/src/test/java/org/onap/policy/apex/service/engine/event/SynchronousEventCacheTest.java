/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.service.engine.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.eventrequestor.EventRequestorConsumer;
import org.onap.policy.apex.service.engine.event.impl.eventrequestor.EventRequestorProducer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class SynchronousEventCacheTest {

    private final Random random = new Random();
    private ApexEventConsumer consumer;
    private ApexEventProducer producer;

    @BeforeEach
    void setUp() {
        consumer = new EventRequestorConsumer();
        producer = new EventRequestorProducer();
    }

    @Test
    void removedCachedFromApexNotExists() {
        int timeout = random.nextInt(100);
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);

        final Object actual = cache.removeCachedEventFromApexIfExists(executionId);
        assertThat(actual).isNull();
    }

    @Test
    void removeCachedFromApex() {
        int timeout = random.nextInt(100);
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);
        final Object event = new Object();
        cache.cacheSynchronizedEventFromApex(executionId, event);

        final Object actual = cache.removeCachedEventFromApexIfExists(executionId);
        assertThat(actual).isSameAs(event);
    }

    @Test
    void removedCachedToApexNotExists() {
        int timeout = random.nextInt(100);
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);

        final Object actual = cache.removeCachedEventToApexIfExists(executionId);
        assertThat(actual).isNull();
    }

    @Test
    void removeCachedToApex() {
        int timeout = random.nextInt(100);
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);
        final Object event = new Object();
        cache.cacheSynchronizedEventToApex(executionId, event);

        final Object actual = cache.removeCachedEventToApexIfExists(executionId);
        assertThat(actual).isSameAs(event);
    }

    @Test
    void apexExistsFromApexNo() {
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 0);

        final boolean actual = cache.existsEventFromApex(executionId);
        assertThat(actual).isFalse();
    }

    @Test
    void apexExistsFromApexYes() {
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 0);
        cache.cacheSynchronizedEventFromApex(executionId, new Object());

        final boolean actual = cache.existsEventFromApex(executionId);
        assertThat(actual).isTrue();
    }

    @Test
    void apexExistsToApexNo() {
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 0);

        final boolean actual = cache.existsEventToApex(executionId);
        assertThat(actual).isFalse();
    }

    @Test
    void apexExistsToApexYes() {
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 0);
        cache.cacheSynchronizedEventToApex(executionId, new Object());

        final boolean actual = cache.existsEventToApex(executionId);
        assertThat(actual).isTrue();
    }

    @Test
    void addEventsFromApexDuplicatedExecutionId() {
        int timeout = random.nextInt(100);
        int executionId = random.nextInt();
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);

        final var obj1 = new Object();
        cache.cacheSynchronizedEventFromApex(executionId, obj1);

        final var obj2 = new Object();
        assertThatCode(() -> cache.cacheSynchronizedEventFromApex(executionId, obj2))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void stop() {
        int timeout = random.nextInt(100);
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, timeout);
        assertThatCode(cache::stop)
            .doesNotThrowAnyException();
    }

    @Test
    void stopNotEmpty() {
        final SynchronousEventCache cache =
            new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS, consumer, producer, 2000);
        assertThatCode(() -> {
            cache.cacheSynchronizedEventToApex(random.nextInt(), new Object());
            cache.stop();
        })
            .doesNotThrowAnyException();
    }
}
