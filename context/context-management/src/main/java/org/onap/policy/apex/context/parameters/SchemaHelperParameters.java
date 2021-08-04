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

package org.onap.policy.apex.context.parameters;

import lombok.Getter;
import lombok.Setter;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * An empty schema helper parameter class that may be specialized by context schema helper plugins that require plugin
 * specific parameters.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NotNull
@Getter
@Setter
public class SchemaHelperParameters extends ParameterGroupImpl {
    private @ClassName String schemaHelperPluginClass;

    @Override
    public String toString() {
        return "SchemaHelperParameters [name=" + getName() + ", schemaHelperPluginClass=" + schemaHelperPluginClass
                        + "]";
    }
}
