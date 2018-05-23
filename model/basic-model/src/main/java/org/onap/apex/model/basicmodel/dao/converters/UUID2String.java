/*
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

package org.onap.apex.model.basicmodel.dao.converters;

import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The Class UUIDConverter converts a UUID to and from database format.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Converter
public class UUID2String extends XmlAdapter<String, UUID> implements AttributeConverter<UUID, String> {

    /*
     * (non-Javadoc)
     *
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(final UUID uuid) {
        String returnString;
        if (uuid == null) {
            returnString = "";
        }
        else {
            returnString = uuid.toString();
        }
        return returnString;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public UUID convertToEntityAttribute(final String uuidString) {
        return UUID.fromString(uuidString);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter
     */
    @Override
    public UUID unmarshal(final String v) throws Exception {
        return this.convertToEntityAttribute(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter
     */
    @Override
    public String marshal(final UUID v) throws Exception {
        return this.convertToDatabaseColumn(v);
    }
}
