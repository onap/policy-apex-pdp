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

package org.onap.policy.apex.client.editor.rest.handling.bean;

import java.util.Map;

import javax.xml.bind.annotation.XmlType;

/**
 * The Event Bean.
 */
@XmlType
public class BeanEvent extends BeanBase {
    private String name = null;
    private String version = null;
    private String nameSpace = null;
    private String source = null;
    private String target = null;
    private String uuid = null;
    private String description = null;
    private Map<String, BeanField> parameters = null;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the name space.
     *
     * @return the name space
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, BeanField> getParameters() {
        return parameters;
    }

    /**
     * Gets the parameter.
     *
     * @param ps the parameter string
     * @return the parameter
     */
    public BeanField getParameter(final String ps) {
        if (parameters != null) {
            return parameters.get(ps);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Event [name=" + name + ", version=" + version + ", nameSpace=" + nameSpace + ", source=" + source
                + ", target=" + target + ", uuid=" + uuid + ", description=" + description + ", parameters="
                + getParameters() + "]";
    }

}
