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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onap.policy.common.parameters.ParameterRuntimeException;

class CarrierTechnologyParametersJsonAdapterTest {

    CarrierTechnologyParametersJsonAdapter adapter;
    RestPluginCarrierTechnologyParameters parameters;
    JsonDeserializationContext deserializationContext;

    @BeforeEach
    void setUp() {
        adapter = new CarrierTechnologyParametersJsonAdapter();
        parameters = new RestPluginCarrierTechnologyParameters();
        deserializationContext = Mockito.mock(JsonDeserializationContext.class);
    }

    @Test
    void testSerialize() {
        Type typeOf = mock(Type.class);
        JsonSerializationContext context = mock(JsonSerializationContext.class);
        assertThatThrownBy(() -> adapter.serialize(parameters, typeOf, context))
            .isInstanceOf(ParameterRuntimeException.class)
            .hasMessageContaining("serialization of Apex carrier technology parameters to Json is not supported");

    }

    @Test
    void testDeserialize() {
        String jsonString = """
            {
                "parameters": {}
            }
            """;
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        CarrierTechnologyParameters result = adapter.deserialize(jsonElement, null, deserializationContext);
        assertNull(result);

        jsonString = """
                {
                    "carrierTechnology": "UNKNOWN_TECH",
                    "parameters": {}
                }
                """;
        JsonElement finalJsonElement =  JsonParser.parseString(jsonString);
        assertThatThrownBy(() -> adapter.deserialize(finalJsonElement, null, deserializationContext))
            .isInstanceOf(ParameterRuntimeException.class)
                .hasMessageContaining("carrier technology \"UNKNOWN_TECH\"");

        jsonString = """
                {
                    "carrierTechnology": "",
                    "parameters": {}
                }
                """;
        JsonElement finalJsonElement1 = JsonParser.parseString(jsonString);
        assertThatThrownBy(() -> adapter.deserialize(finalJsonElement1, null, deserializationContext)).isInstanceOf(
            ParameterRuntimeException.class);

        jsonString = """
                {
                    "carrierTechnology": "null",
                    "parameters": {}
                }
                """;
        JsonElement finalJsonElement2 = JsonParser.parseString(jsonString);
        assertThatThrownBy(() -> adapter.deserialize(finalJsonElement2, null, deserializationContext)).isInstanceOf(
            ParameterRuntimeException.class);

    }

    @Test
    void testDeserializeWithInvalidParameterClass() {
        String jsonString = """
                {
                    "carrierTechnology": "FILE",
                    "parameterClassName": "InvalidClassName",
                    "parameters": {}
                }
                """;
        JsonElement jsonElement = JsonParser.parseString(jsonString);

        assertThatThrownBy(() -> adapter.deserialize(jsonElement, null, deserializationContext))
            .isInstanceOf(ParameterRuntimeException.class)
            .hasMessageContaining("could not find class");
    }

}
