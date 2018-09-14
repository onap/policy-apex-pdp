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

/**
 * The Class AaiAndGuardSim.
 */
public class AaiAndGuardSim {
    private static final String BASE_URI = "http://localhost:54321/AAIAndGuardSim";
    private static final int MAX_LOOPS = 100000;
    private HttpServer server;

    /**
     * Instantiates a new aai and guard sim.
     */
    public AaiAndGuardSim() {
        final ResourceConfig rc = new ResourceConfig(AaiAndGuardSimEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        while (!server.isStarted()) {
            ThreadUtilities.sleep(50);
        }
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {
        final AaiAndGuardSim sim = new AaiAndGuardSim();

        for (int index = 0; index < MAX_LOOPS; index++) {
            ThreadUtilities.sleep(100);
        }
        
        sim.tearDown();
    }
}
