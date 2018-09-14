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

package org.onap.policy.apex.testsuites.performance.benchmark.engine.main;

import org.junit.Test;

public class TestAsyncEventSimo extends BaseTest {

    @Test
    public void testJsonFileAsyncSimo() throws Exception {
        final String[] args = {
            "-c",
            "src/test/resources/parameters/File2FileJsonEventAsyncSIMO.json"
        };
        
        final String[] outFilePaths = {
            "src/test/resources/events/EventsOutMulti0.json",
            "src/test/resources/events/EventsOutMulti1.json",
            "src/test/resources/events/EventsOutMulti2.json"
        };

        testFileEvents(args, outFilePaths, 48956);
    }

}
