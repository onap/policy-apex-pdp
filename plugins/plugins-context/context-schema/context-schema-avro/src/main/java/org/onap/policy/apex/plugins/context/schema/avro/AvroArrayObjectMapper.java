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
import org.apache.avro.generic.GenericData.Array;

/**
 * Object mapper for arrays, uses default behaviour except for a specific default constructor
 * implementation.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroArrayObjectMapper extends AvroDirectObjectMapper {
    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#createNewinstance(org.
     * apache.avro.Schema)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        return new Array(0, avroSchema);
    }
}
