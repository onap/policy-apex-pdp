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

import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;

/**
 * An empty schema helper parameter class that may be specialized by context schema helper plugins that require plugin
 * specific parameters.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SchemaHelperParameters extends AbstractParameters {
    // Schema helper plugin class for the schema
    private String schemaHelperPluginClass;

    /**
     * Constructor to create a schema helper parameters instance and register the instance with the parameter service.
     */
    public SchemaHelperParameters() {
        super(SchemaHelperParameters.class.getCanonicalName());
        ParameterService.registerParameters(SchemaHelperParameters.class, this);
    }

    /**
     * Constructor to create a schema helper parameters instance with the name of a sub class of this class and register
     * the instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public SchemaHelperParameters(final String parameterClassName) {
        super(parameterClassName);
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "SchemaHelperParameters [schemaHelperPluginClass=" + schemaHelperPluginClass + "]";
    }
}
