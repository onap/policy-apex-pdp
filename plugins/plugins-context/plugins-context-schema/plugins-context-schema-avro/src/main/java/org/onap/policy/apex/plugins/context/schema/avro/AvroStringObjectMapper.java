/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import lombok.Getter;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.util.Utf8;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class does string mapping from the Avro Utf8 class to the Java String class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroStringObjectMapper implements AvroObjectMapper {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AvroStringObjectMapper.class);

    // The user keyAvro type for direct mapping
    private AxKey userKey;
    @Getter
    private Type avroType;

    // The Apex compatible class
    private static final Class<String> schemaClass = String.class;

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
    public void init(final AxKey initUserKey, final Type initAvroType) {
        this.userKey = initUserKey;
        this.avroType = initAvroType;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        // By default, we do not create an instance, normal Java object creation for strings is
        // sufficient
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object mapFromAvro(final Object avroObject) {
        // The Avro object should be a Utf8 object
        if (!(avroObject instanceof Utf8)) {
            final var returnString = userKey.getId() + ": object \"" + avroObject + "\" of class \""
                            + avroObject.getClass() + "\" cannot be decoded to an object of class \""
                            + schemaClass.getName() + "\"";
            LOGGER.warn(returnString);
            throw new ContextRuntimeException(returnString);
        }

        return avroObject.toString();
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

        return object;
    }
}
