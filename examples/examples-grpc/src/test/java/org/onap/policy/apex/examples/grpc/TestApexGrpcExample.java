/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2022 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * Test class to run an example policy for APEX-CDS interaction over gRPC. Event received on
 * unauthenticated.DCAE_CL_OUTPUT DMaaP topic (dummy REST Endpoint here) triggers the policy Based on the event, a
 * create/delete subscription gRPC request is triggered to the CDS (a dummy gRPC server here). Response received from
 * CDS is used to send a final output Log event on POLICY_CL_MGT topic.
 */
class TestApexGrpcExample {

    @Test
    void testGrpcExample() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/main/resources/policy/APEXgRPCPolicy.apex",
            "-l",
            "target/APEXgRPCPolicyModel.log",
            "-ac",
            "src/main/resources/examples/config/APEXgRPC/ApexConfig.json",
            "-t",
            "src/main/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXgRPCPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] apexArgs = {
            "-rfr",
            "target/classes",
            "-p",
            "target/classes/APEXgRPCPolicy.json"
        };
        // @formatter:on

        final GrpcTestServerSim sim = new GrpcTestServerSim();

        final Client client = ClientBuilder.newClient();
        final ApexMain apexMain = new ApexMain(apexArgs);

        await().atMost(5000, TimeUnit.MILLISECONDS).until(apexMain::isAlive);

        String getLoggedEventUrl = "http://localhost:54321/GrpcTestRestSim/sim/event/getLoggedEvent";
        // wait for success response code to be received, until a timeout
        await().atMost(50000, TimeUnit.MILLISECONDS)
            .pollInterval(10000, TimeUnit.MILLISECONDS)
            .until(() -> 200 == client.target(getLoggedEventUrl).request("application/json").get().getStatus());

        apexMain.shutdown();

        Response response = client.target(getLoggedEventUrl).request("application/json").get();
        sim.tearDown();

        String responseEntity = response.readEntity(String.class);
        var logFileJson = "src/main/resources/examples/events/APEXgRPC/LogEvent.json";
        String expectedLoggedOutputEvent = Files.readString(Paths.get(logFileJson)).replaceAll("\r", "");

        var cdsResponseJson = "src/main/resources/examples/events/APEXgRPC/CDSResponseStatusEvent.json";
        String expectedStatusEvent = Files.readString(Paths.get(cdsResponseJson)).replaceAll("\r", "");

        // Both LogEvent and CDSResponseStatusEvent are generated from the final state in the policy
        assertThat(responseEntity).contains(expectedStatusEvent).contains(expectedLoggedOutputEvent);
        client.close();
    }
}