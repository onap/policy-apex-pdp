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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebClientTest {
    HttpsURLConnection mockedHttpsURLConnection;
    String sampleString = "Response Code :200";
    /**
     * Set up the mocked REST manager.
     */
    @Before
    public void setupMockedRest() {
        mockedHttpsURLConnection   = mock(HttpsURLConnection.class);
        InputStream iStream = new ByteArrayInputStream(sampleString.getBytes());
        try {
            when(mockedHttpsURLConnection.getInputStream()).thenReturn(iStream);
            Mockito.doNothing().when(mockedHttpsURLConnection).connect();
        }catch (Exception e) {
        }

    }

    @Test
    public void httpsRequest() {
        WebClient cl = new WebClient();
        String result = cl.httpsRequest("https://some.random.url/data", "POST", null,
                "admin", "admin", "application/json",true, true);

    }

    @Test
    public void httpRequest() {
        WebClient cl = new WebClient();
        String result = cl.httpRequest("http://some.random.url/data", "GET", null,
                "admin", "admin", "application/json",true, true);

    }

    @Test
    public void toPrettyString() {
        String xmlSample = "<input xmlns=\"org:onap:sdnc:northbound:generic-resource\">" +
                "<sdnc-request-header> <svc-action>update</svc-action> </sdnc-request-header></input>";
        WebClient cl = new WebClient();
        cl.toPrettyString(xmlSample, 4);
    }
}
