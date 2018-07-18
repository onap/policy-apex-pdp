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

package org.onap.policy.apex.plugins.context.schema.avro;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;

import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;

/**
 * The Class AvroNullableMapper handles Avro null mappings to Java null values.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class AvroNullableMapper extends AvroDirectObjectMapper {
    // The wrapped mapper for nullables
    private final AvroObjectMapper wrappedMapper;

    /**
     * The Constructor.
     *
     * @param wrappedMapper the wrapped mapper
     */
    public AvroNullableMapper(final AvroObjectMapper wrappedMapper) {
        this.wrappedMapper = wrappedMapper;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#getJavaClass()
     */
    @Override
    public Class<?> getJavaClass() {
        return wrappedMapper.getJavaClass();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#init(org.onap.policy.
     * apex. model.basicmodel.concepts.AxKey, org.apache.avro.Schema.Type)
     */
    @Override
    public void init(final AxKey userKey, final Type avroType) {
        wrappedMapper.init(userKey, avroType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#createNewInstance(
     * org. apache.avro.Schema)
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        return wrappedMapper.createNewInstance(avroSchema);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#getAvroType()
     */
    @Override
    public Type getAvroType() {
        return Schema.Type.UNION;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#mapFromAvro(java.
     * lang. Object)
     */
    @Override
    public Object mapFromAvro(final Object avroObject) {
        if (avroObject == null) {
            return null;
        } else {
            return wrappedMapper.mapFromAvro(avroObject);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroDirectObjectMapper#mapToAvro(java.lang.
     * Object)
     */
    @Override
    public Object mapToAvro(final Object object) {
        if (object == null) {
            return null;
        } else {
            throw new ApexRuntimeException("Unions/Nullable is not supported in output event ... Coming soon!");
        }

    }

}
