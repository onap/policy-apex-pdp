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

package org.onap.policy.apex.service.parameters.engineservice;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.common.parameters.ParameterRuntimeException;

class EngineServiceParametersJsonAdapterTest {

    EngineServiceParametersJsonAdapter adapter;
    Type typeOf;
    JsonSerializationContext contextSer;
    JsonDeserializationContext contextDeser;

    @BeforeEach
    void setUp() {
        adapter = new EngineServiceParametersJsonAdapter();
        typeOf = mock(Type.class);
        contextSer = mock(JsonSerializationContext.class);
        contextDeser = mock(JsonDeserializationContext.class);
    }

    @Test
    void testSerialize() {
        EngineParameters parameters = new EngineParameters();
        assertThatThrownBy(() -> adapter.serialize(parameters, typeOf, contextSer))
            .isInstanceOf(ParameterRuntimeException.class)
            .hasMessageContaining("serialization of Apex parameters to Json is not supported");
    }

    @Test
    void testGetExecutorParametersListError() {
        String jsonString = """
            {
                "taskParameters": {},
                "executorParameters": {
                    "param1": "value1",
                    "param2": "value2"
                }
            }
            """;
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        assertThatThrownBy(() -> adapter.deserialize(jsonElement, typeOf, contextDeser))
            .isInstanceOf(ParameterRuntimeException.class)
            .hasMessageContaining("entry is not a parameter JSON object");
    }

    @Test
    void testDeserialize() {
        String jsonString = """
            {
                "taskParameters": [
                    {
                        "test1": "value1",
                        "test2": "value2"
                    }
                ],
                "executorParameters": {}
            }
            """;
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        EngineParameters engineParameters = adapter.deserialize(jsonElement, typeOf, contextDeser);
        assertNotNull(engineParameters);
    }
}
