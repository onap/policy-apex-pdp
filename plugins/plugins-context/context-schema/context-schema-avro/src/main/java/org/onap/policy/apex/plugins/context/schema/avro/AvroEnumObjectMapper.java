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

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.EnumSymbol;

/**
 * Object mapper for enums, uses default behaviour except for a specific default constructor
 * implementation.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroEnumObjectMapper extends AvroDirectObjectMapper {
    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.plugins.context.schema.avro.AvroObjectMapper#createNewinstance(org.
     * apache.avro.Schema)
     */
    @Override
    public Object createNewInstance(final Schema avroSchema) {
        // Initialize the ENUM to the first ENUM symbol on the list
        final List<String> enumSymbols = avroSchema.getEnumSymbols();

        // Check if any ENUM symbols have been defined
        if (enumSymbols == null || enumSymbols.isEmpty()) {
            return null;
        }

        return new EnumSymbol(avroSchema, enumSymbols.get(0));
    }
}
