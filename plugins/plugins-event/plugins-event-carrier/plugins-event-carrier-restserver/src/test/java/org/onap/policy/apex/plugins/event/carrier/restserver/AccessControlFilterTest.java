/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AccessControlFilterTest {

    private AccessControlFilter acf;

    @Mock
    private ContainerRequestContext requestContext;
    @Mock
    private ContainerResponseContext responseContext;

    @BeforeEach
    public void beforeEach() {
        acf = new AccessControlFilter();
    }

    @Test
    void filterAddToExisting() throws IOException {
        // prepare mocks
        final String origin = RandomStringUtils.randomAlphanumeric(14, 16);
        final MultivaluedHashMap<String, Object> map = new MultivaluedHashMap<>();
        Mockito.when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        Mockito.when(responseContext.getHeaders()).thenReturn(map);

        // prepare expected
        final MultivaluedMap<String, Object> expected = new MultivaluedHashMap<>();
        expected.add("Access-Control-Allow-Origin", origin);
        expected.add("Access-Control-Expose-Headers", "Content-Type, Accept, Allow");
        expected.add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        expected.add("Access-Control-Allow-Credentials", "true");
        expected.add("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT");


        // test
        acf.filter(requestContext, responseContext);
        final MultivaluedMap<String, Object> actual = responseContext.getHeaders();

        assertEquals(expected, actual);
    }
}
