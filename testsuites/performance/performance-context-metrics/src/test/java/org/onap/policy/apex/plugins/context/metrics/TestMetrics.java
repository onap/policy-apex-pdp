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

package org.onap.policy.apex.plugins.context.metrics;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMetrics {
    // Zookeeper test server
    TestingServer zkTestServer;

    @Before
    public void beforeTest() throws Exception {
        zkTestServer = new TestingServer(62181);
    }

    @After
    public void afterTest() throws IOException {
        zkTestServer.stop();
    }

    @Test
    public void getSingleJVMMetrics() {
        final String[] args = {"singleJVMTestNL", "1", "32", "1000", "65536", "0", "localhost:62181", "false"};

        try {
            ConcurrentContextMetrics.main(args);
        } catch (final Exception e) {
            fail("Metrics test failed");
            e.printStackTrace();
        }
    }
}
