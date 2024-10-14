/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation. All rights reserved.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

class AvroNullableMapperTest {

    @Test
    void avroNullableMapperTests() {
        var avroObjectMapper = new AvroDirectObjectMapper();
        var avroNullableMapper = new AvroNullableMapper(avroObjectMapper);
        assertDoesNotThrow(() -> avroNullableMapper.init(new AxArtifactKey("test", "1.0.1"),
                Schema.Type.BOOLEAN));

        assertEquals("class java.lang.Boolean", avroNullableMapper.getJavaClass().toString());
        assertThat(avroNullableMapper.getAvroType()).isEqualByComparingTo(Schema.Type.UNION);

        var avroObjMapper = new AvroDirectObjectMapper();
        avroObjMapper.init(new AxArtifactKey("test2", "1.1.2"), Schema.Type.BOOLEAN);
        assertThrows(ContextRuntimeException.class, () -> avroNullableMapper.mapFromAvro(avroObjMapper));
        assertNull(avroNullableMapper.mapFromAvro(null));

        assertNull(avroNullableMapper.mapToAvro(null));
        assertThrows(ApexRuntimeException.class, () -> avroNullableMapper.mapToAvro(avroObjMapper));

        assertDoesNotThrow(() -> avroNullableMapper.createNewInstance(mock(Schema.class)));
    }
}
