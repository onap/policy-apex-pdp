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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * The Class CDATAConditioner converts a CDATA String to and from database format by removing spaces at the ends of lines and
 * platform-specific new line endings.
 *
 * @author John Keeney (John.Keeney@ericsson.com)
 */
@Converter
public class CDATAConditioner extends XmlAdapter<String, String> implements AttributeConverter<String, String> {

    private static final String NL = "\n";

    /*
     * (non-Javadoc)
     *
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(final String raw) {
        return clean(raw);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public String convertToEntityAttribute(final String db) {
        return clean(db);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter
     */
    @Override
    public String unmarshal(final String v) throws Exception {
        return this.convertToEntityAttribute(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.bind.annotation.adapters.XmlAdapter
     */
    @Override
    public String marshal(final String v) throws Exception {
        return this.convertToDatabaseColumn(v);
    }

    /**
     * Clean.
     *
     * @param in the in
     * @return the string
     */
    public static final String clean(final String in) {
        if (in == null) {
            return null;
        }
        else {
            return in.replaceAll("\\s+$", "").replaceAll("\\r?\\n", NL);
        }
    }
}
