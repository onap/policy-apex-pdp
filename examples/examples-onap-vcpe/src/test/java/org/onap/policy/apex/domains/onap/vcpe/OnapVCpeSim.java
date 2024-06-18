/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2023-2024 Nordix Foundation.
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

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;

/**
 * The Class OnapVCpeSim.
 */
public class OnapVCpeSim {
    private static final int MAX_LOOPS = 100000;
    private static HttpServletServer server;

    /**
     * Instantiates a new aai and guard sim.
     */
    public OnapVCpeSim(final String[] args) throws Exception {
        server = HttpServletServerFactoryInstance.getServerFactory().build(
            "OnapVCpeSimEndpoint", false, args[0], Integer.parseInt(args[1]), false, "/OnapVCpeSim", false,
            false);

        server.addServletClass(null, OnapVCpeSimEndpoint.class.getName());
        server.setSerializationProvider(GsonMessageBodyHandler.class.getName());

        server.start();

        if (!NetworkUtil.isTcpPortOpen(args[0], Integer.parseInt(args[1]), 2000, 1L)) {
            throw new IllegalStateException("port " + args[1] + " is still not in use");
        }
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {
        final OnapVCpeSim sim = new OnapVCpeSim(args);

        for (int index = 0; index < MAX_LOOPS; index++) {
            ThreadUtilities.sleep(100);
        }

        sim.tearDown();
    }
}
