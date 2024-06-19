/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021, 2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.events.syncasync;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AsyncEventSisoTest extends TestEventBase {
    private final String[] outFilePaths = {
        "target/examples/events/SampleDomain/EventsOutSingle.json"
    };

    /**
     * Delete output files.
     */
    @AfterEach
    void deleteOutputFiles() {
        for (String filePath : outFilePaths) {
            assertTrue(new File(filePath).delete());
        }
    }

    @Test
    void testJsonFileAsyncSiso() throws Exception {
        final String[] args = {
            "-rfr",
            "target",
            "-p",
            "target/examples/config/SampleDomain/File2FileJsonEventAsyncSISO.json"};

        testFileEvents(args, outFilePaths, 100);
    }
}
