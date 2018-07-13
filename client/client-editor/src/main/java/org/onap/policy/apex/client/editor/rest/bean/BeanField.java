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

package org.onap.policy.apex.client.editor.rest.bean;

import javax.xml.bind.annotation.XmlType;

/**
 * The Field Bean.
 */
@XmlType
public class BeanField extends BeanKeyRef {

    private boolean optional = true;
    private String localName = null;

    /**
     * Gets the local name for this field.
     *
     * @return the local name for this field.
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Gets the optional flag.
     *
     * @return the optional flag
     */
    public boolean getOptional() {
        return optional;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.client.editor.rest.bean.Bean_KeyRef#toString()
     */
    @Override
    public String toString() {
        return "Field [localName=" + getLocalName() + ", name=" + getName() + ", version=" + getVersion()
                + ", optional=" + getOptional() + "]";
    }

}
