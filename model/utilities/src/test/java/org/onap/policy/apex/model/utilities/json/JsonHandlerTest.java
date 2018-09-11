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

package org.onap.policy.apex.model.utilities.json;

import static org.junit.Assert.assertEquals;

import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class JsonHandlerTest {

    private static final String VALUE = "value";

    @Test
    public void testAssertions() throws IOException {
        final OverTheMoonObject jsonObject = new OverTheMoonObject(VALUE);
        final String jsonString = new GsonBuilder().create().toJson(jsonObject);

        final byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        try (final InputStream inputStream = new ByteArrayInputStream(bytes);) {

            final JsonHandler<OverTheMoonObject> objUnderTest = new JsonHandler<>();

            final OverTheMoonObject actualObject = objUnderTest.read(OverTheMoonObject.class, inputStream);
            assertEquals(VALUE, actualObject.name);
        }

    }

    private class OverTheMoonObject {
        private final String name;

        public OverTheMoonObject(final String name) {
            this.name = name;
        }
    }
}
