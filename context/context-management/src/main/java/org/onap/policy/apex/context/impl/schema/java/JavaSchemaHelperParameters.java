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

package org.onap.policy.apex.context.impl.schema.java;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.apex.context.parameters.SchemaHelperParameters;
import org.onap.policy.common.parameters.GroupValidationResult;

/**
 * The Schema helper parameter class for the Java schema helper is an empty parameter class that acts as a placeholder.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavaSchemaHelperParameters extends SchemaHelperParameters {
    // Map of specific type adapters for this event
    private Map<String, JavaSchemaHelperJsonAdapterParameters> jsonAdapters = new LinkedHashMap<>();

    /**
     * Constructor for Java schema helper parameters.
     */
    public JavaSchemaHelperParameters() {
        this.setName("Java");
        this.setSchemaHelperPluginClass(JavaSchemaHelper.class.getCanonicalName());
    }
    
    /**
     * Get the JSON adapters.
     * 
     * @return the JSON adapters
     */
    public Map<String, JavaSchemaHelperJsonAdapterParameters> getJsonAdapters() {
        return jsonAdapters;
    }

    /**
     * Set JSON adapters for the schema helper.
     * 
     * @param jsonAdapters the JSON adapters
     */
    public void setJsonAdapters(Map<String, JavaSchemaHelperJsonAdapterParameters> jsonAdapters) {
        this.jsonAdapters = jsonAdapters;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = new GroupValidationResult(this);

        for (Entry<String, JavaSchemaHelperJsonAdapterParameters> typeAdapterEntry : jsonAdapters.entrySet()) {
            result.setResult("jsonAdapters", typeAdapterEntry.getKey(), typeAdapterEntry.getValue().validate());
        }
        return result;
    }

}
