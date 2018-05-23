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

package org.onap.apex.model.basicmodel.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.onap.apex.model.basicmodel.concepts.AxReferenceKey;

/**
 * This class manages marshaling and unmarshaling of Apex {@link AxReferenceKey} concepts using JAXB. The local name in reference keys must have specific
 * handling.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(namespace = "http://www.ericsson.com/apex")
public class AxReferenceKeyAdapter extends XmlAdapter<String, AxReferenceKey> implements Serializable {

    private static final long serialVersionUID = -3480405083900107029L;

    /*
     * (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public final String marshal(final AxReferenceKey key) throws Exception {
        return key.getLocalName();
    }

    /*
     * (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public final AxReferenceKey unmarshal(final String key) throws Exception {
        final AxReferenceKey axReferenceKey = new AxReferenceKey();
        axReferenceKey.setLocalName(key);
        return axReferenceKey;
    }
}
