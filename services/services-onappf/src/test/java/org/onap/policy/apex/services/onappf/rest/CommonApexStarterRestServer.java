/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.onap.policy.apex.services.onappf.ApexStarterActivator;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.ApexStarterMain;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.parameters.CommonTestData;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.coder.Coder;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.services.Registry;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to perform unit test of {@link ApexStarterRestServer}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class CommonApexStarterRestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApexStarterRestServer.class);

    private static String KEYSTORE = System.getProperty("user.dir") + "/src/test/resources/ssl/policy-keystore";

    private static Coder coder = new StandardCoder();

    public static final String NOT_ALIVE = "not alive";
    public static final String ALIVE = "alive";
    public static final String SELF = "self";
    public static final String NAME = "Policy PDP-A";
    public static final String ENDPOINT_PREFIX = "policy/apex-pdp/v1/";

    private static int port;
    protected static String httpsPrefix;

    private static ApexStarterMain main;

    private boolean activatorWasAlive;

    /**
     * Allocates a port for the server, writes a config file, and then starts Main.
     *
     * @throws Exception if an error occurs
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        port = NetworkUtil.allocPort();

        httpsPrefix = "https://localhost:" + port + "/";

        makeConfigFile();

        startMain();
    }

    /**
     * Stops Main.
     */
    @AfterClass
    public static void teardownAfterClass() {
        try {
            stopMain();

        } catch (final ApexStarterException exp) {
            LOGGER.error("cannot stop main", exp);
        }
    }

    /**
     * Set up.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setUp() throws Exception {
        // restart, if not currently running
        if (main == null) {
            startMain();
        }

        activatorWasAlive =
                Registry.get(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class).isAlive();
    }

    /**
     * Restores the activator's "alive" state.
     */
    @After
    public void tearDown() {
        markActivator(activatorWasAlive);
    }

    /**
     * Verifies that an endpoint appears within the swagger response.
     *
     * @param endpoint the endpoint of interest
     * @throws Exception if an error occurs
     */
    protected void testSwagger(final String endpoint) throws Exception {
        final Invocation.Builder invocationBuilder = sendFqeRequest(httpsPrefix + "swagger.yaml", true);
        final String resp = invocationBuilder.get(String.class);

        assertTrue(resp.contains(ENDPOINT_PREFIX + endpoint + ":"));
    }

    /**
     * Makes a parameter configuration file.
     *
     * @throws Exception if an error occurs
     */
    private static void makeConfigFile() throws Exception {
        final Map<String, Object> config =
                new CommonTestData().getApexStarterParameterGroupMap("ApexStarterParameterGroup");

        @SuppressWarnings("unchecked")
        final Map<String, Object> restParams = (Map<String, Object>) config.get("restServerParameters");
        restParams.put("port", port);

        final File file = new File("src/test/resources/TestConfigParams.json");
        file.deleteOnExit();

        coder.encode(file, config);
    }

    /**
     * Starts the "Main".
     *
     * @throws Exception if an error occurs
     */
    private static void startMain() throws Exception {
        Registry.newRegistry();

        // make sure port is available
        if (NetworkUtil.isTcpPortOpen("localhost", port, 1, 1L)) {
            throw new IllegalStateException("port " + port + " is still in use");
        }

        final Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.keyStore", KEYSTORE);
        systemProps.put("javax.net.ssl.keyStorePassword", "Pol1cy_0nap");
        System.setProperties(systemProps);

        // @formatter:off
        final String[] apexStarterConfigParameters = {
            "-c", "src/test/resources/TestConfigParams.json",
            "-p", "src/test/resources/topic.properties"
        };
        // @formatter:on

        main = new ApexStarterMain(apexStarterConfigParameters);

        if (!NetworkUtil.isTcpPortOpen("localhost", port, 6, 10000L)) {
            throw new IllegalStateException("server is not listening on port " + port);
        }
    }

    /**
     * Stops the "Main".
     *
     * @throws Exception if an error occurs
     */
    private static void stopMain() throws ApexStarterException {
        if (main != null) {
            final ApexStarterMain main2 = main;
            main = null;

            main2.shutdown();
        }
    }

    private void markActivator(final boolean wasAlive) {
        final Object manager = Whitebox.getInternalState(
                Registry.get(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class), "manager");
        Whitebox.setInternalState(manager, "running", wasAlive);
    }

    /**
     * Verifies that unauthorized requests fail.
     *
     * @param endpoint the target end point
     * @param sender function that sends the requests to the target
     * @throws Exception if an error occurs
     */
    protected void checkUnauthRequest(final String endpoint, final Function<Invocation.Builder, Response> sender)
            throws Exception {
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                sender.apply(sendNoAuthRequest(endpoint)).getStatus());
    }

    /**
     * Sends a request to an endpoint.
     *
     * @param endpoint the target endpoint
     * @return a request builder
     * @throws Exception if an error occurs
     */
    protected Invocation.Builder sendRequest(final String endpoint) throws Exception {
        return sendFqeRequest(httpsPrefix + ENDPOINT_PREFIX + endpoint, true);
    }

    /**
     * Sends a request to an endpoint, without any authorization header.
     *
     * @param endpoint the target endpoint
     * @return a request builder
     * @throws Exception if an error occurs
     */
    protected Invocation.Builder sendNoAuthRequest(final String endpoint) throws Exception {
        return sendFqeRequest(httpsPrefix + ENDPOINT_PREFIX + endpoint, false);
    }

    /**
     * Sends a request to a fully qualified endpoint.
     *
     * @param fullyQualifiedEndpoint the fully qualified target endpoint
     * @param includeAuth if authorization header should be included
     * @return a request builder
     * @throws Exception if an error occurs
     */
    protected Invocation.Builder sendFqeRequest(final String fullyQualifiedEndpoint, final boolean includeAuth)
            throws Exception {
        final SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, NetworkUtil.getAlwaysTrustingManager(), new SecureRandom());
        final ClientBuilder clientBuilder =
                ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier((host, session) -> true);
        final Client client = clientBuilder.build();

        client.property(ClientProperties.METAINF_SERVICES_LOOKUP_DISABLE, "true");
        client.register(GsonMessageBodyHandler.class);

        if (includeAuth) {
            final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
            client.register(feature);
        }

        final WebTarget webTarget = client.target(fullyQualifiedEndpoint);

        return webTarget.request(MediaType.APPLICATION_JSON);
    }
}
