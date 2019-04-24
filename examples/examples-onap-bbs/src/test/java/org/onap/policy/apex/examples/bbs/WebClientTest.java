/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 huawei. All rights reserved.
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

package org.onap.policy.apex.examples.bbs;

import com.sun.jersey.api.client.WebResource;
import org.glassfish.jersey.client.ClientResponse;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebClientTest {

    @Test
    public void httpsRequest() {
        WebClient cl = new WebClient();
        WebResource webResource = mock(WebResource.class);
        WebResource.Builder webResourceBuilder = mock(WebResource.Builder.class);
        ClientResponse clientResponse = mock(ClientResponse.class);
        try {
            Mockito.doNothing().when(webResourceBuilder).method("GET");
            when(webResource.accept(anyString())).thenReturn(webResourceBuilder);

            cl.httpRequest("https://127.0.0.1", "GET", null,
                    "admin", "admin", "application/json",true);
        } catch (Exception e) {
            //
        }
    }

    @Test
    public void toPrettyString() {
        String xmlSample = "<input xmlns=\"org:onap:sdnc:northbound:generic-resource\">"
                + "<sdnc-request-header> <svc-action>update</svc-action> </sdnc-request-header></input>";
        WebClient cl = new WebClient();
        cl.toPrettyString(xmlSample, 4);
    }
}