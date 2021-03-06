/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.schema.avro;

import org.onap.policy.apex.context.parameters.SchemaHelperParameters;

/**
 * Schema helper parameter class for the Avro schema helper.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class AvroSchemaHelperParameters extends SchemaHelperParameters {
    /**
     * The Default Constructor sets the {@link AvroSchemaHelper} as the schema helper class for Avro schemas.
     */
    public AvroSchemaHelperParameters() {
        this.setSchemaHelperPluginClass(AvroSchemaHelper.class.getName());
    }
}
