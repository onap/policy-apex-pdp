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
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;

/**
 * This interface is used to allow mapping of Avro object to and from Java objects.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface AvroObjectMapper {
    /**
     * Get the Java class produced and consumed by this mapper.
     *
     * @return the Java class
     */
    Class<?> getJavaClass();

    /**
     * Initialize the mapper is working with.
     *
     * @param userKey the user key
     * @param avroType the avro type
     */
    void init(AxKey userKey, Type avroType);

    /**
     * Create a new instance of the java object the Avro schema maps to.
     *
     * @param avroSchema the Avro schema to use to create the new instance
     * @return a new instance of the object
     */
    Object createNewInstance(Schema avroSchema);

    /**
     * Set the Avro type the mapper is working with.
     *
     * @return the avro type
     */
    Type getAvroType();

    /**
     * Map the Avro object to an object Apex can handler.
     *
     * @param avroObject the Avro object to map
     * @return the Apex-compatible object
     */
    Object mapFromAvro(Object avroObject);

    /**
     * Map the Apex object to an Avro object.
     *
     * @param object the Apex-compatible object
     * @return the Avro object
     */
    Object mapToAvro(Object object);
}
