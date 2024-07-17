/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 huawei. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2023-2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebClientTest {

    private HttpsURLConnection mockedHttpsUrlConnection;
    private URL url;
    private String sampleString = "Response Code :200";

    /**
     * Set up the mocked REST manager.
     *
     * @throws IOException on I/O errors
     */
    @BeforeEach
    void setupMockedRest() throws IOException {
        mockedHttpsUrlConnection = mock(HttpsURLConnection.class);
        url = mock(URL.class);
        when(url.openConnection()).thenReturn(mockedHttpsUrlConnection);
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        when(mockedHttpsUrlConnection.getInputStream()).thenReturn(inputStream);
        when(mockedHttpsUrlConnection.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void testHttpsPostRequest() {
        WebClient cl = new WebClient();
        String result = cl
            .httpRequest(url, "POST", null, "admin", "admin", "application/json");
        assertNotNull(result);
    }

    @Test
    void testHttpsGetRequest() {
        WebClient cl = new WebClient();
        String result = cl
            .httpRequest(url, "GET", "sample output string", "admin", "admin", "application/json");
        assertNotNull(result);
    }

    @Test
    void testToPrettyString() {
        String xmlSample = "<input xmlns=\"org:onap:sdnc:northbound:generic-resource\">"
            + "<sdnc-request-header> <svc-action>update</svc-action> </sdnc-request-header></input>";
        WebClient cl = new WebClient();
        assertNotNull(cl.toPrettyString(xmlSample, 4));
    }
}
