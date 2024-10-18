/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.parameters.carriertechnology;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

class RestPluginCarrierTechnologyParametersTest {

    RestPluginCarrierTechnologyParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = new RestPluginCarrierTechnologyParameters();
    }

    @Test
    void testHeaders() {
        String[][] headers = {
            {"HeaderOne", "valueOne"},
            {"HeaderTwo", "valueTwo"}
        };
        parameters.setHttpHeaders(headers);
        assertTrue(parameters.checkHttpHeadersSet());

        MultivaluedMap<String, Object> multiVHeaders = parameters.getHttpHeadersAsMultivaluedMap();
        assertNotNull(multiVHeaders);

        parameters.setHttpHeaders(null);
        assertFalse(parameters.checkHttpHeadersSet());
        multiVHeaders = parameters.getHttpHeadersAsMultivaluedMap();
        assertNull(multiVHeaders);

        headers = new String[][] {};
        parameters.setHttpHeaders(headers);
        assertFalse(parameters.checkHttpHeadersSet());
    }

    @Test
    void testValidate() {
        String[][] headers = {
            {"HeaderOne", "valueOne"},
            {"HeaderTwo", "valueTwo"}
        };
        parameters.setHttpHeaders(headers);

        String url = "https://testurl.com/{something}/{else}";
        parameters.setUrl(url);

        BeanValidationResult result = parameters.validate();
        assertNotNull(result.getResult());

        parameters.setHttpHeaders(null);
        result = parameters.validate();
        assertNotNull(result.getResult());

        headers = new String[][] {
            {"HeaderOne", "valueOne"},
            null,
            {"HeaderTwo", "ValueTwo", "ValueThree"},
            {null, "2"},
            {"HeaderThree", null}
        };
        parameters.setHttpHeaders(headers);
        assertThatCode(parameters::validate).doesNotThrowAnyException();
    }

    @Test
    void testUrl() {
        String urlMissingEndTag = "http://www.blah.com/{par1/somethingelse";
        parameters.setUrl(urlMissingEndTag);
        ValidationResult result = parameters.validateUrl();
        assertEquals(ValidationStatus.INVALID, result.getStatus());

        String urlNestedTag = "http://www.blah.com/{par1/{some}thingelse";
        parameters.setUrl(urlNestedTag);
        result = parameters.validateUrl();
        assertEquals(ValidationStatus.INVALID, result.getStatus());

        String urlMissingStartTag = "http://www.blah.com/{par1}/some}thingelse";
        parameters.setUrl(urlMissingStartTag);
        result = parameters.validateUrl();
        assertEquals(ValidationStatus.INVALID, result.getStatus());

        String urlMissingStartTag2 = "http://www.blah.com/par1}/somethingelse";
        parameters.setUrl(urlMissingStartTag2);
        result = parameters.validateUrl();
        assertEquals(ValidationStatus.INVALID, result.getStatus());

        String urlEmptyTag = "http://www.blah.com/{}/somethingelse";
        parameters.setUrl(urlEmptyTag);
        result = parameters.validateUrl();
        assertEquals(ValidationStatus.INVALID, result.getStatus());

        String urlNull = null;
        parameters.setUrl(urlNull);
        result = parameters.validateUrl();
        assertNull(result);

        String urlValid = "https://testurl.com/{something}/{else}";
        parameters.setUrl(urlValid);
        result = parameters.validateUrl();
        assertNull(result);

        Set<String> keys = parameters.getKeysFromUrl();
        assertNotNull(keys);
    }

    @Test
    void testCodeFilter() {
        parameters.setHttpCodeFilter("test");
        assertNull(parameters.validateHttpCodeFilter());

        parameters.setHttpCodeFilter(null);
        assertNull(parameters.validateHttpCodeFilter());

        parameters.setHttpCodeFilter("");
        assertEquals(ValidationStatus.INVALID, parameters.validateHttpCodeFilter().getStatus());

        parameters.setHttpCodeFilter("(22M");
        assertEquals(ValidationStatus.INVALID, parameters.validateHttpCodeFilter().getStatus());
    }

    @Test
    void testToString() {
        assertThat(parameters.toString()).contains("CarrierTechnologyParameters");
    }
}
