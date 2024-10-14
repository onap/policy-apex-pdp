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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

class AvroObjMapperFactoryTest {
    @Test
    void testObjMapperFactory() {
        var objMapperFactory = new AvroObjectMapperFactory();
        var key = new AxArtifactKey("test", "1.0.1");
        var schema1 = Schema.createUnion(List.of(Schema.create(Schema.Type.NULL)));
        assertThatThrownBy(() -> objMapperFactory.get(key, schema1))
                .isInstanceOf(ContextRuntimeException.class)
                .hasMessageContaining("Apex currently only supports UNION schemas with 2 options, "
                        + "one must be NULL");

        var schema2 = mock(Schema.class);
        when(schema2.getType()).thenReturn(Schema.Type.UNION);
        var nullSchema = Schema.create(Schema.Type.NULL);
        when(schema2.getTypes()).thenReturn(List.of(nullSchema, nullSchema));
        assertThatThrownBy(() -> objMapperFactory.get(key, schema2))
                .isInstanceOf(ContextRuntimeException.class)
                .hasMessageContaining("Apex currently only supports UNION schema2 with 2 options, "
                        + "only one can be NULL, and the other cannot be another UNION");

        var fixedSchema = Schema.createFixed("test1", "doc", "test", 2);
        when(schema2.getTypes()).thenReturn(List.of(fixedSchema, nullSchema));
        assertDoesNotThrow(() -> objMapperFactory.get(key, schema2));

    }

}
