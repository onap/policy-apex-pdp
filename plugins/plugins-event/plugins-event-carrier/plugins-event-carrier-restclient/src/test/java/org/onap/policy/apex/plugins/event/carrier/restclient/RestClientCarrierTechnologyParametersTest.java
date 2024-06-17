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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import static org.assertj.core.api.Assertions.assertThatCode;
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
 * Test REST client carrier technology parameters.
 */
class RestClientCarrierTechnologyParametersTest {

    @Test
    void testRestClientCarrierTechnologyParametersBadList() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderBadList.json");
        arguments.setRelativeFileRoot(".");

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("httpHeaders")
            .hasMessageContaining("item \"entry 0\" value \"null\" INVALID, is null")
            .hasMessageContaining("item \"entry 1\" value \"null\" INVALID, is null")
            .hasMessageContaining("item \"entry 2\" value \"null\" INVALID, is null");
    }

    @Test
    void testRestClientCarrierTechnologyParametersNotKvPairs() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderNotKvPairs.json");
        arguments.setRelativeFileRoot(".");

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("httpHeaders")
            .hasMessageContaining("\"entry 0\" value \"[aaa, bbb, ccc]\" INVALID, must have one key and one value")
            .hasMessageContaining("\"entry 0\" value \"[aaa]\" INVALID, must have one key and one value");
    }

    @Test
    void testRestClientCarrierTechnologyParametersNulls() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderNulls.json");
        arguments.setRelativeFileRoot(".");

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("httpHeaders", "entry 0", "entry 1")
            .hasMessageContaining("item \"key\" value \"null\" INVALID, is blank")
            .hasMessageContaining("item \"value\" value \"null\" INVALID, is blank");
    }

    @Test
    void testRestClientCarrierTechnologyParameterFilterInvalid() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPFilterInvalid.json");
        arguments.setRelativeFileRoot(".");

        assertThatCode(() -> {
            ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            parameters.getEventInputParameters().get("RestClientConsumer0").getCarrierTechnologyParameters();
        }).hasMessageContaining(
                "Invalid HTTP code filter, the filter must be specified as a three digit regular expression: ");
    }

    @Test
    void testRestClientCarrierTechnologyParametersOk() throws ParameterException {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderOK.json");
        arguments.setRelativeFileRoot(".");

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        RestClientCarrierTechnologyParameters rrctp0 = (RestClientCarrierTechnologyParameters) parameters
                .getEventInputParameters().get("RestClientConsumer0").getCarrierTechnologyParameters();
        assertEquals(0, rrctp0.getHttpHeaders().length);

        RestClientCarrierTechnologyParameters rrctp1 = (RestClientCarrierTechnologyParameters) parameters
                .getEventInputParameters().get("RestClientConsumer1").getCarrierTechnologyParameters();
        assertEquals(3, rrctp1.getHttpHeaders().length);
        assertEquals("bbb", rrctp1.getHttpHeadersAsMultivaluedMap().get("aaa").get(0));
        assertEquals("ddd", rrctp1.getHttpHeadersAsMultivaluedMap().get("ccc").get(0));
        assertEquals("fff", rrctp1.getHttpHeadersAsMultivaluedMap().get("eee").get(0));

        rrctp1.setHttpHeaders(null);
        assertNull(rrctp1.getHttpHeadersAsMultivaluedMap());
    }

    @Test
    void testRestClientCarrierTechnologyHttpCodeFilterOk() throws ParameterException {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setToscaPolicyFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderOK.json");
        arguments.setRelativeFileRoot(".");

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        RestClientCarrierTechnologyParameters rrctp1 = (RestClientCarrierTechnologyParameters) parameters
                .getEventInputParameters().get("RestClientConsumer1").getCarrierTechnologyParameters();
        assertEquals("[1-5][0][0-5]", rrctp1.getHttpCodeFilter());
    }

    @Test
    void testGettersAndSetters() {
        RestClientCarrierTechnologyParameters rrctp = new RestClientCarrierTechnologyParameters();

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

        rrctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.DELETE, rrctp.getHttpMethod());

        assertEquals("RESTCLIENTCarrierTechnologyParameters "
                + "[url=http://some.where, httpMethod=DELETE, httpHeaders=[[aaa, bbb], [ccc, ddd]], "
                + "httpCodeFilter=[1-5][0][0-5]]", rrctp.toString());
    }

    @Test
    void testUrlValidation() {
        RestClientCarrierTechnologyParameters rrctp = new RestClientCarrierTechnologyParameters();

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
