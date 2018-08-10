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
 * Bean class to hold parameters for context handling in Apex. This class contains all the context parameters for schema
 * handling, distribution, locking, and persistence of context albums.
 * 
 * <p>The following parameters are defined:
 * <ol>
 * <li>flushPeriod: Context is flushed to any persistor plugin that is defined periodically, and the period for flushing
 * is the flush period.
 * <li>distributorParameters: The parameters (a {@link distributorParameters} instance) for the distributor plugin that
 * is being used for context album distribution
 * <li>schemaParameters: The parameters (a {@link SchemaParameters} instance) for the schema plugin that is being used
 * for context album schemas
 * <li>lockManagerParameters: The parameters (a {@link LockManagerParameters} instance) for the locking mechanism plugin
 * that is being used for context album locking
 * <li>persistorParameters: The parameters (a {@link PersistorParameters} instance) for the persistence plugin that is
 * being used for context album persistence
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextParameters extends AbstractParameters {
    // @formatter:off
    // Plugin Parameters
    private DistributorParameters distributorParameters = new DistributorParameters();
    private SchemaParameters      schemaParameters      = new SchemaParameters();
    private LockManagerParameters lockManagerParameters = new LockManagerParameters();
    private PersistorParameters   persistorParameters   = new PersistorParameters();
    // @formatter:on

    /**
     * Constructor to create a context parameters instance and register the instance with the parameter service.
     */
    public ContextParameters() {
        super(ContextParameters.class.getCanonicalName());
        ParameterService.registerParameters(ContextParameters.class, this);
    }

    /**
     * Gets the distributor parameters.
     *
     * @return the distributor parameters
     */
    public DistributorParameters getDistributorParameters() {
        return distributorParameters;
    }

    /**
     * Sets the distributor parameters.
     *
     * @param distributorParameters the distributor parameters
     */
    public void setDistributorParameters(final DistributorParameters distributorParameters) {
        this.distributorParameters = distributorParameters;
    }

    /**
     * Gets the schema parameters.
     *
     * @return the schema parameters
     */
    public SchemaParameters getSchemaParameters() {
        return schemaParameters;
    }

    /**
     * Sets the schema parameters.
     *
     * @param schemaParameters the schema parameters
     */
    public void setSchemaParameters(final SchemaParameters schemaParameters) {
        this.schemaParameters = schemaParameters;
    }

    /**
     * Gets the lock manager parameters.
     *
     * @return the lock manager parameters
     */
    public LockManagerParameters getLockManagerParameters() {
        return lockManagerParameters;
    }

    /**
     * Sets the lock manager parameters.
     *
     * @param lockManagerParameters the lock manager parameters
     */
    public void setLockManagerParameters(final LockManagerParameters lockManagerParameters) {
        this.lockManagerParameters = lockManagerParameters;
    }

    /**
     * Gets the persistor parameters.
     *
     * @return the persistor parameters
     */
    public PersistorParameters getPersistorParameters() {
        return persistorParameters;
    }

    /**
     * Sets the persistor parameters.
     *
     * @param persistorParameters the persistor parameters
     */
    public void setPersistorParameters(final PersistorParameters persistorParameters) {
        this.persistorParameters = persistorParameters;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "ContextParameters [distributorParameters=" + distributorParameters + ", schemaParameters="
                + schemaParameters + ", lockManagerParameters=" + lockManagerParameters + ", persistorParameters="
                + persistorParameters + "]";
    }
}
