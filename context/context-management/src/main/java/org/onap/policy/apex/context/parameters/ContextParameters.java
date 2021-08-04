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
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;

// @formatter:off
/**
 * Bean class to hold parameters for context handling in Apex. This class contains all the context parameters for schema
 * handling, distribution, locking, and persistence of context albums.
 *
 * <p>The following parameters are defined:
 * <ol>
 * <li>flushPeriod: Context is flushed to any persistor plugin that is defined periodically, and the period for flushing
 * is the flush period.
 * <li>distributorParameters: The parameters (a {@link DistributorParameters} instance) for the distributor plugin that
 * is being used for context album distribution
 * <li>schemaParameters: The parameters (a {@link SchemaParameters} instance) for the schema plugin that is being used
 * for context album schemas
 * <li>lockManagerParameters: The parameters (a {@link LockManagerParameters} instance) for the locking mechanism plugin
 * that is being used for context album locking
 * <li>persistorParameters: The parameters (a {@link PersistorParameters} instance) for the persistence plugin that is
 * being used for context album persistence
 * </ol>
 */
@NotNull
@Getter
@Setter
public class ContextParameters extends ParameterGroupImpl {
    private @Valid DistributorParameters distributorParameters = new DistributorParameters();
    private @Valid SchemaParameters      schemaParameters      = new SchemaParameters();
    private @Valid LockManagerParameters lockManagerParameters = new LockManagerParameters();
    private @Valid PersistorParameters   persistorParameters   = new PersistorParameters();
    // @formatter:on

    /**
     * Constructor to create a context parameters instance and register the instance with the parameter service.
     */
    public ContextParameters() {
        super(ContextParameterConstants.MAIN_GROUP_NAME);
    }

    @Override
    public String toString() {
        return "ContextParameters [name=" + getName() + ", distributorParameters=" + distributorParameters
                        + ", schemaParameters=" + schemaParameters + ", lockManagerParameters=" + lockManagerParameters
                        + ", persistorParameters=" + persistorParameters + "]";
    }
}
