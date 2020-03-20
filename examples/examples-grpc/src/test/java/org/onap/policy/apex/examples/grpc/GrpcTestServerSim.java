/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.examples.grpc;

import java.io.IOException;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;

/**
 * The Class GrpcTestServerSim that manages test servers for REST and gRPC requests for the test.
 */
public class GrpcTestServerSim {
    private static final String HOST = "localhost";
    private HttpServletServer restServer;
    private GrpcTestDummyGrpcServer grpcServer;

    /**
     * Instantiates a new REST simulator for DMaaP requests.
     *
     * @throws InterruptedException interrupted exception
     * @throws IOException io exception
     */
    public GrpcTestServerSim() throws InterruptedException, IOException {
        int restServerPort = 54321;
        restServer = HttpServletServerFactoryInstance.getServerFactory().build("GrpcTestRestSimEndpoint", false, HOST,
            restServerPort, "/GrpcTestRestSim", false, false);
        restServer.addServletClass(null, GrpcTestRestSimEndpoint.class.getName());
        restServer.setSerializationProvider(GsonMessageBodyHandler.class.getName());
        restServer.start();
        if (!NetworkUtil.isTcpPortOpen(HOST, restServerPort, 2000, 1L)) {
            throw new IllegalStateException("port " + restServerPort + " is still not in use");
        }

        int grpcServerPort = 54322;
        grpcServer = new GrpcTestDummyGrpcServer(HOST, grpcServerPort);
        grpcServer.start();
        if (!NetworkUtil.isTcpPortOpen(HOST, grpcServerPort, 2000, 1L)) {
            throw new IllegalStateException("port " + grpcServerPort + " is still not in use");
        }
    }

    /**
     * Tear down.
     */
    public void tearDown() {
        if (restServer != null) {
            restServer.stop();
        }
        if (grpcServer != null) {
            grpcServer.stop();
        }
    }
}
