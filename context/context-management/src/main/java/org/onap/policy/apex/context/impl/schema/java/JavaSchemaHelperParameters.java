/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.context.parameters.SchemaHelperParameters;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;

/**
 * The Schema helper parameter class for the Java schema helper is an empty parameter class that acts as a placeholder.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
public class JavaSchemaHelperParameters extends SchemaHelperParameters {
    // Map of specific type adapters for this event
    private Map<String, @NotNull @Valid JavaSchemaHelperJsonAdapterParameters> jsonAdapters = new LinkedHashMap<>();

    /**
     * Constructor for Java schema helper parameters.
     */
    public JavaSchemaHelperParameters() {
        this.setName("Java");
        this.setSchemaHelperPluginClass(JavaSchemaHelper.class.getName());
    }

}
