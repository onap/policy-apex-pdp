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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.events.syncasync;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public class SyncEventSisoTest extends BaseEventTest {
    private final String[] outFilePaths = {
        "target/examples/events/SampleDomain/EventsOutSingle.json"
    };
    
    /**
     * Delete output files.
     */
    @After
    public void deleteOutputFiles() {
        for (String filePath : outFilePaths) {
            new File(filePath).delete();
        }
    }

    @Test
    public void testJsonFileAsyncSiso() throws Exception {
        final String[] args = {
            "-rfr",
            "target",
            "-c",
            "target/examples/config/SampleDomain/File2FileJsonEventSyncSISO.json"};

        testFileEvents(args, outFilePaths, 100);
    }
}
