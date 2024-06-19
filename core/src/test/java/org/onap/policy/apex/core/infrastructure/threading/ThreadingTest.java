/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.core.infrastructure.threading;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ThreadingTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ThreadingTest {

    private static final String LOCAL_NAME = "localName";
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(ThreadingTest.class);

    /**
     * Test thread factory initialization.
     */
    @Test
    void testThreadFactoryInitialization() {
        final ApplicationThreadFactory objUnderTest = new ApplicationThreadFactory(LOCAL_NAME, 0);
        assertNotNull(objUnderTest, "Failed to create ApplicationThreadFactory threadFactory0");
        logger.debug(objUnderTest.toString());
        assertTrue(objUnderTest.getName().startsWith("Apex-" + LOCAL_NAME),
            "Failed to name ApplicationThreadFactory threadFactory0");

        final ApplicationThreadFactory objUnderTest1 = new ApplicationThreadFactory(LOCAL_NAME, 0);
        assertNotNull(objUnderTest1, "Failed to create ApplicationThreadFactory threadFactory1");
        logger.debug(objUnderTest1.toString());
        assertTrue(objUnderTest1.getName().startsWith("Apex-" + LOCAL_NAME),
            "Failed to name ApplicationThreadFactory threadFactory1");

        testThreadFactory(objUnderTest);
        testThreadFactory(objUnderTest1);
    }

    /**
     * Test thread factory.
     *
     * @param threadFactory the thread factory
     */
    private void testThreadFactory(final ApplicationThreadFactory threadFactory) {
        final List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final Thread thread = threadFactory.newThread(() -> {
            });
            threadList.add(thread);
            thread.start();
        }

        for (int i = 0; i < 5; i++) {
            Thread thread = threadList.get(i);
            assertTrue(thread.getName().startsWith("Apex-" + LOCAL_NAME));
            assertTrue(thread.getName().contains(":" + i));
        }
    }
}
