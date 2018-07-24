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

package org.onap.policy.apex.domains.onap.vcpe;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

public class AAIAndGuardSim {
    private static final String BASE_URI = "http://192.168.144.235:54321/AAIAndGuardSim";
    private HttpServer server;

    public AAIAndGuardSim() {
        final ResourceConfig rc = new ResourceConfig(AAIAndGuardSimEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        while (!server.isStarted()) {
            ThreadUtilities.sleep(50);
        }
    }

    public void tearDown() throws Exception {
        server.shutdown();
    }

    public static void main(final String[] args) throws Exception {
        final AAIAndGuardSim sim = new AAIAndGuardSim();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                break;
            }
        }
        sim.tearDown();
    }
}
