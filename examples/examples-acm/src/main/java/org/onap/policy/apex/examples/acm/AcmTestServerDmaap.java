/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2022-2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.acm;

import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;

/**
 * The Class AcmTestServerDmaap that manages test servers for REST requests for the test.
 */
public class AcmTestServerDmaap implements AutoCloseable {
    private static final String HOST = "localhost";
    private HttpServletServer restServer;
    private int restServerPort = 3904;

    /**
     * Instantiates a new REST simulator for DMaaP requests.
     */
    public AcmTestServerDmaap() {
        restServer = HttpServletServerFactoryInstance.getServerFactory().build("AcmTestRestDmaapEndpoint", false, HOST,
                restServerPort, false, "/", false, false);
        restServer.addServletClass(null, AcmTestRestDmaapEndpoint.class.getName());
        restServer.setSerializationProvider(GsonMessageBodyHandler.class.getName());
        restServer.start();
    }

    /**
     * Validate the Rest server.
     * @throws InterruptedException if is not alive
     */
    public void validate() throws InterruptedException {
        if (!NetworkUtil.isTcpPortOpen(HOST, restServerPort, 50, 200L)) {
            throw new IllegalStateException("port " + restServerPort + " is still not in use");
        }
    }

    @Override
    public void close() {
        if (restServer != null) {
            restServer.stop();
            restServer = null;
        }
    }
}
