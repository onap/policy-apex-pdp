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
    private final Class<Byte[]> schemaClass = Byte[].class;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#getJavaClass()
     */
    @Override
    public Class<?> getJavaClass() {
        return schemaClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#setAvroType(org.apache.
     * avro. Schema.Type)
     */
    @Override
    public void init(final AxKey intUserKey, final Type initAvroType) {
        this.userKey = intUserKey;
        this.avroType = initAvroType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#createNewinstance(org.
     * apache. avro.Schema)
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        // By default, we do not create an instance, normal Java object creation for byte arrays is
        // sufficient
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#getAvroType()
     */
    @Override
    public Type getAvroType() {
        return avroType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#mapFromAvro(java.lang.
     * Object)
     */
    @Override
    public Object mapFromAvro(final Object avroObject) {
        // The Avro object should be a Utf8 object
        if (!(avroObject instanceof ByteBuffer)) {
            final String returnString =
                    userKey.getID() + ": object \"" + avroObject + "\" of class \"" + avroObject.getClass()
                            + "\" cannot be decoded to an object of class \"" + schemaClass.getCanonicalName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Cast the byte buffer object so we get access to its methods
        final ByteBuffer byteBufferAvroObject = (ByteBuffer) avroObject;

        // read the byte buffer into a byte array
        final byte[] byteArray = new byte[byteBufferAvroObject.remaining()];
        byteBufferAvroObject.get(byteArray);

        return byteArray;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#mapToAvro(java.lang.Object)
     */
    @Override
    public Object mapToAvro(final Object object) {
        if (object == null) {
            final String returnString = userKey.getID() + ": cannot encode a null object of class \""
                    + schemaClass.getCanonicalName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // The incoming object should be a byte array
        if (!(object instanceof byte[])) {
            final String returnString = userKey.getID() + ": object \"" + object + "\" of class \"" + object.getClass()
                    + "\" cannot be decoded to an object of class \"" + schemaClass.getCanonicalName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        // Create a ByteBuffer object to serialize the bytes
        final ByteBuffer byteBuffer = ByteBuffer.wrap((byte[]) object);

        return byteBuffer;
    }
}
