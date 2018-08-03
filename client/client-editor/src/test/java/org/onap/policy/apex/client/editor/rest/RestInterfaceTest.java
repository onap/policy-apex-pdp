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

package org.onap.policy.apex.client.editor.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.client.editor.rest.ApexEditorMain.EditorState;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * The RestInterface Test.
 */
public class RestInterfaceTest {
    // CHECKSTYLE:OFF: MagicNumber

    private static final String TESTMODELFILE = "models/SamplePolicyModelMVEL.json";
    private static final String TESTPORTNUM = "18989";
    private static final long MAX_WAIT = 15000; // 15 sec
    private static final InputStream SYSIN = System.in;
    private static final String[] EDITOR_MAIN_ARGS = new String[] { "-p", TESTPORTNUM };

    private static ApexEditorMain editorMain;
    private static WebTarget target;

    private static AxPolicyModel localmodel = null;
    private static String localmodelString = null;

    /**
     * Sets up the tests.
     *
     * @throws Exception if an error happens
     */
    @BeforeClass
    public static void setUp() throws Exception {
        // Start the editor
        editorMain = new ApexEditorMain(EDITOR_MAIN_ARGS, System.out);
        // prevent a stray stdin value from killing the editor
        final ByteArrayInputStream input = new ByteArrayInputStream("".getBytes());
        System.setIn(input);
        // Init the editor in a separate thread
        final Runnable testThread = new Runnable() {
            @Override
            public void run() {
                editorMain.init();
            }
        };
        new Thread(testThread).start();
        // wait until editorMain is in state RUNNING
        final long startwait = System.currentTimeMillis();
        while (editorMain.getState().equals(EditorState.STOPPED) || editorMain.getState().equals(EditorState.READY)
                || editorMain.getState().equals(EditorState.INITIALIZING)) {
            if (editorMain.getState().equals(EditorState.STOPPED)) {
                Assert.fail("Rest endpoint (" + editorMain + ") shut down before it could be used");
            }
            if (System.currentTimeMillis() - startwait > MAX_WAIT) {
                Assert.fail("Rest endpoint (" + editorMain + ") for test failed to start fast enough");
            }
            Thread.sleep(100);
        }

        // create the client
        final Client c = ClientBuilder.newClient();
        // Create the web target
        target = c.target(new ApexEditorParameters().getBaseURI());

        // load a test model locally
        localmodel = new ApexModelReader<>(AxPolicyModel.class, false)
                .read(ResourceUtils.getResourceAsStream(TESTMODELFILE));
        localmodelString =
                new ApexModelStringWriter<AxPolicyModel>(false).writeJSONString(localmodel, AxPolicyModel.class);

        // initialize a session ID
        createNewSession();
    }

    /**
     * Clean up streams.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    @AfterClass
    public static void cleanUpStreams() throws IOException, InterruptedException {
        editorMain.shutdown();
        // wait until editorMain is in state STOPPED
        final long startwait = System.currentTimeMillis();
        while (!editorMain.getState().equals(EditorState.STOPPED)) {
            if (System.currentTimeMillis() - startwait > MAX_WAIT) {
                Assert.fail("Rest endpoint (" + editorMain + ") for test failed to shutdown fast enough");
            }
            Thread.sleep(50);
        }
        System.setIn(SYSIN);
    }

    /**
     * Test to see that the message create Model with model id -1 .
     */
    @Test
    public void createSession() {
        createNewSession();
    }

    /**
     * Creates a new session.
     *
     * @return the session ID
     */
    private static int createNewSession() {
        final ApexAPIResult responseMsg = target.path("editor/-1/Session/Create").request().get(ApexAPIResult.class);
        assertEquals(responseMsg.getResult(), ApexAPIResult.RESULT.SUCCESS);
        assertTrue(responseMsg.getMessages().size() == 1);
        return Integer.parseInt(responseMsg.getMessages().get(0));
    }

    /**
     * Upload policy.
     *
     * @param sessionID the session ID
     * @param modelAsJsonString the model as json string
     */
    private void uploadPolicy(final int sessionID, final String modelAsJsonString) {
        final Builder requestbuilder = target.path("editor/" + sessionID + "/Model/Load").request();
        final ApexAPIResult responseMsg = requestbuilder.put(Entity.json(modelAsJsonString), ApexAPIResult.class);
        assertTrue(responseMsg.isOK());
    }

    /**
     * Create a new session, Upload a test policy model, then get a policy, parse it, and compare it to the same policy
     * in the original model.
     *
     * @throws ApexException if there is an Apex Error
     * @throws JAXBException if there is a JaxB Error
     **/
    @Test
    public void testUploadThenGet() throws ApexException, JAXBException {

        final int sessionID = createNewSession();

        uploadPolicy(sessionID, localmodelString);

        final ApexAPIResult responseMsg = target.path("editor/" + sessionID + "/Policy/Get")
                .queryParam("name", "Policy0").queryParam("version", "0.0.1").request().get(ApexAPIResult.class);
        assertTrue(responseMsg.isOK());

        // The string in responseMsg.Messages[0] is a JSON representation of a AxPolicy object. Lets parse it
        final String returnedPolicyAsString = responseMsg.getMessages().get(0);
        ApexModelReader<AxPolicy> apexPolicyReader = new ApexModelReader<>(AxPolicy.class, false);
        final AxPolicy returnedpolicy = apexPolicyReader.read(returnedPolicyAsString);
        // AxPolicy returnedpolicy = RestUtils.getConceptFromJSON(returnedPolicyAsString, AxPolicy.class);

        // Extract the local copy of that policy from the local Apex Policy Model
        final AxPolicy localpolicy = localmodel.getPolicies().get("Policy0", "0.0.1");

        // Write that local copy of the AxPolicy object to a Json String, ten parse it again
        final ApexModelStringWriter<AxPolicy> apexModelWriter = new ApexModelStringWriter<>(false);
        final String localPolicyString = apexModelWriter.writeJSONString(localpolicy, AxPolicy.class);
        apexPolicyReader = new ApexModelReader<>(AxPolicy.class, false);
        final AxPolicy localpolicyReparsed = apexPolicyReader.read(localPolicyString);
        // AxPolicy localpolicy_reparsed = RestUtils.getConceptFromJSON(returnedPolicyAsString, AxPolicy.class);

        assertNotNull(returnedpolicy);
        assertNotNull(localpolicy);
        assertNotNull(localpolicyReparsed);
        assertEquals(localpolicy, localpolicyReparsed);
        assertEquals(localpolicy, returnedpolicy);
    }

    // TODO Full unit testing of REST interface

}
