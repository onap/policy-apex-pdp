/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class does string mapping from the Avro BYTES type to a Java byte array.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroBytesObjectMapper implements AvroObjectMapper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AvroBytesObjectMapper.class);

    // The user keyAvro type for direct mapping
    private AxKey userKey;
    private Type avroType;

    // The Apex compatible class
    private static final Class<Byte[]> schemaClass = Byte[].class;

    /**
     * {@inheritDoc}.
     */
    @Override
    public Class<?> getJavaClass() {
        return schemaClass;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final AxKey intUserKey, final Type initAvroType) {
        this.userKey = intUserKey;
        this.avroType = initAvroType;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        // By default, we do not create an instance, normal Java object creation for byte arrays is
        // sufficient
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Type getAvroType() {
        return avroType;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object mapFromAvro(final Object avroObject) {
        // The Avro object should be a Utf8 object
        if (!(avroObject instanceof ByteBuffer)) {
            final var returnString = userKey.getId() + ": object \"" + avroObject + "\" of class \""
                            + avroObject.getClass() + "\" cannot be decoded to an object of class \""
                            + schemaClass.getName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Cast the byte buffer object so we get access to its methods
        final var byteBufferAvroObject = (ByteBuffer) avroObject;

        // read the byte buffer into a byte array
        final var byteArray = new byte[byteBufferAvroObject.remaining()];
        byteBufferAvroObject.get(byteArray);

        return byteArray;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object mapToAvro(final Object object) {
        if (object == null) {
            final var returnString = userKey.getId() + ": cannot encode a null object of class \""
                            + schemaClass.getName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // The incoming object should be a byte array
        if (!(object instanceof byte[])) {
            final var returnString = userKey.getId() + ": object \"" + object + "\" of class \"" + object.getClass()
                            + "\" cannot be decoded to an object of class \"" + schemaClass.getName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Create a ByteBuffer object to serialize the bytes
        return ByteBuffer.wrap((byte[]) object);
    }
}
