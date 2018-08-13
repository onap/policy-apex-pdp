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
 * The ContextSchema Bean.
 */
@XmlType
public class BeanContextSchema extends BeanBase {
    private String name = null;
    private String version = null;
    private String schemaFlavour = null;
    private String schemaDefinition = null;
    private String uuid = null;
    private String description = null;

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
     * Gets the schema flavour.
     *
     * @return the schema flavour
     */
    public String getSchemaFlavour() {
        return schemaFlavour;
    }

    /**
     * Gets the schema definition.
     *
     * @return the schema definition
     */
    public String getSchemaDefinition() {
        return schemaDefinition;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ContextSchema [name=" + name + ", version=" + version + ", schemaFlavour=" + schemaFlavour
                + ", schemaDefinition=" + schemaDefinition + ", uuid=" + uuid + ", description=" + description + "]";
    }
}
