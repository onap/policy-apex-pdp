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

package org.onap.policy.apex.context.parameters;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;

/**
 * An empty schema helper parameter class that may be specialized by context schema helper plugins that require plugin
 * specific parameters.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SchemaHelperParameters implements ParameterGroup {
    private String name;
    private String schemaHelperPluginClass;

    /**
     * Constructor to create a schema helper parameters instance and register the instance with the parameter service.
     */
    public SchemaHelperParameters() {
        super();
    }

    /**
     * Gets the schema helper plugin class.
     *
     * @return the schema helper plugin class
     */
    public String getSchemaHelperPluginClass() {
        return schemaHelperPluginClass;
    }

    /**
     * Sets the schema helper plugin class.
     *
     * @param pluginClass the schema helper plugin class
     */
    public void setSchemaHelperPluginClass(final String pluginClass) {
        schemaHelperPluginClass = pluginClass;
    }
    
    @Override
    public String toString() {
        return "SchemaHelperParameters [name=" + name + ", schemaHelperPluginClass=" + schemaHelperPluginClass + "]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public GroupValidationResult validate() {
        return new GroupValidationResult(this);
    }
}
