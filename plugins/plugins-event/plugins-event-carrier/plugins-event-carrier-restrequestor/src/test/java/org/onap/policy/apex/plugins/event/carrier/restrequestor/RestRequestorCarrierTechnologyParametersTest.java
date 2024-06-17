/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test REST Requestor carrier technology parameters.
 */
class RestRequestorCarrierTechnologyParametersTest {

    @Test
    void testRestRequestorCarrierTechnologyParametersBadList() {
        verifyException("src/test/resources/prodcons/RESTRequestorWithHTTPHeaderBadList.json",
            "item \"entry 2\" value \"null\" INVALID, is null");
    }

    @Test
    void testRestRequestorCarrierTechnologyParametersNotKvPairs() {
        verifyException("src/test/resources/prodcons/RESTRequestorWithHTTPHeaderNotKvPairs.json",
            "item \"entry 0\" value \"[aaa, bbb, ccc]\" INVALID, must have one key");
    }

    @Test
    void testRestRequestorCarrierTechnologyParametersNulls() {
        verifyException("src/test/resources/prodcons/RESTRequestorWithHTTPHeaderNulls.json",
            "\"key\"");
    }

    private void verifyException(String fileName, String expectedMsg) {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath(fileName);
        arguments.setRelativeFileRoot(".");

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments)).describedAs(fileName)
            .hasMessageContaining(expectedMsg);
    }

    @Test
    void testRestRequestorCarrierTechnologyParametersOk() throws ParameterException {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTRequestorWithHTTPHeaderOK.json");
        arguments.setRelativeFileRoot(".");

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        RestRequestorCarrierTechnologyParameters rrctp0 = (RestRequestorCarrierTechnologyParameters) parameters
            .getEventInputParameters().get("RestRequestorConsumer0").getCarrierTechnologyParameters();
        assertEquals(0, rrctp0.getHttpHeaders().length);

        RestRequestorCarrierTechnologyParameters rrctp1 = (RestRequestorCarrierTechnologyParameters) parameters
            .getEventInputParameters().get("RestRequestorConsumer1").getCarrierTechnologyParameters();
        assertEquals(3, rrctp1.getHttpHeaders().length);
        assertEquals("bbb", rrctp1.getHttpHeadersAsMultivaluedMap().get("aaa").get(0));
        assertEquals("ddd", rrctp1.getHttpHeadersAsMultivaluedMap().get("ccc").get(0));
        assertEquals("fff", rrctp1.getHttpHeadersAsMultivaluedMap().get("eee").get(0));
    }

    @Test
    void testRestClientCarrierTechnologyParameterFilterInvalid() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPFilterInvalid.json");
        arguments.setRelativeFileRoot(".");

        assertThatThrownBy(() -> {
            new ApexParameterHandler().getParameters(arguments);
            ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            parameters.getEventInputParameters().get("RestRequestorConsumer0").getCarrierTechnologyParameters();
        }).hasMessageContaining(
            "Invalid HTTP code filter, the filter must be specified as a three digit regular expression: ");
    }

    @Test
    void testGettersAndSetters() {
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();

        rrctp.setHttpHeaders(null);
        assertNull(rrctp.getHttpHeadersAsMultivaluedMap());

        rrctp.setUrl("http://some.where");
        assertEquals("http://some.where", rrctp.getUrl());

        rrctp.setHttpCodeFilter("[1-5][0][0-5]");
        assertEquals("[1-5][0][0-5]", rrctp.getHttpCodeFilter());

        String[][] httpHeaders = new String[2][2];
        httpHeaders[0][0] = "aaa";
        httpHeaders[0][1] = "bbb";
        httpHeaders[1][0] = "ccc";
        httpHeaders[1][1] = "ddd";

        rrctp.setHttpHeaders(httpHeaders);
        assertEquals("aaa", rrctp.getHttpHeaders()[0][0]);
        assertEquals("bbb", rrctp.getHttpHeaders()[0][1]);
        assertEquals("ccc", rrctp.getHttpHeaders()[1][0]);
        assertEquals("ddd", rrctp.getHttpHeaders()[1][1]);

        rrctp.setHttpHeaders(null);
        assertFalse(rrctp.checkHttpHeadersSet());

        String[][] httpHeadersZeroLength = new String[0][0];
        rrctp.setHttpHeaders(httpHeadersZeroLength);
        assertFalse(rrctp.checkHttpHeadersSet());

        rrctp.setHttpHeaders(httpHeaders);
        assertTrue(rrctp.checkHttpHeadersSet());

        rrctp.setHttpMethod(RestRequestorCarrierTechnologyParameters.HttpMethod.DELETE);
        assertEquals(RestRequestorCarrierTechnologyParameters.HttpMethod.DELETE, rrctp.getHttpMethod());

        assertEquals("RESTREQUESTORCarrierTechnologyParameters "
            + "[url=http://some.where, httpMethod=DELETE, httpHeaders=[[aaa, bbb], [ccc, ddd]],"
            + " httpCodeFilter=[1-5][0][0-5]]", rrctp.toString());
    }

    @Test
    void testUrlValidation() {
        RestRequestorCarrierTechnologyParameters rrctp = new RestRequestorCarrierTechnologyParameters();

        rrctp.setUrl("http://some.where.no.tag.in.url");
        assertEquals("http://some.where.no.tag.in.url", rrctp.getUrl());

        String[][] httpHeaders = new String[2][2];
        httpHeaders[0][0] = "aaa";
        httpHeaders[0][1] = "bbb";
        httpHeaders[1][0] = "ccc";
        httpHeaders[1][1] = "ddd";

        rrctp.setHttpHeaders(httpHeaders);
        assertEquals("aaa", rrctp.getHttpHeaders()[0][0]);
        assertEquals("bbb", rrctp.getHttpHeaders()[0][1]);
        assertEquals("ccc", rrctp.getHttpHeaders()[1][0]);
        assertEquals("ddd", rrctp.getHttpHeaders()[1][1]);

        assertTrue(rrctp.validate().isValid());

        rrctp.setUrl("http://{place}.{that}/is{that}.{one}");
        assertTrue(rrctp.validate().isValid());

        Set<String> keymap = rrctp.getKeysFromUrl();
        assertTrue(keymap.contains("place") && keymap.contains("that") && keymap.contains("one"));

        rrctp.setUrl("http://{place.{that}/{is}.{not}/{what}.{exist}");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://{place}.{that}/{is}.{not}/{what}.{exist");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://place.that/is.not/what.{exist");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://place}.{that}/{is}.{not}/{what}.{exist}");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://{place}.{that}/is}.{not}/{what}.{exist}");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://{place}.{that}/{}.{not}/{what}.{exist}");
        assertFalse(rrctp.validate().isValid());
        rrctp.setUrl("http://{place}.{that}/{ }.{not}/{what}.{exist}");
        assertFalse(rrctp.validate().isValid());
    }

}
