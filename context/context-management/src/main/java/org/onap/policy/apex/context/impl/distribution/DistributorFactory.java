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

package org.onap.policy.apex.context.impl.distribution;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class returns a context distributor for the particular type of distribution mechanism configured for use.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class DistributorFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DistributorFactory.class);

    /**
     * Get a context distributor for a given context set key.
     *
     * @param key The key for the distributor
     * @return a context distributor
     * @throws ContextException on context distributor creation errors
     */
    public Distributor getDistributor(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("Distributor factory, key=" + key);

        Assertions.argumentNotNull(key, ContextException.class, "Parameter \"key\" may not be null");

        // Get the class for the distributor using reflection
        final DistributorParameters distributorParameters = ParameterService.getParameters(DistributorParameters.class);
        final String pluginClass = distributorParameters.getPluginClass();
        Object contextDistributorObject = null;
        try {
            contextDistributorObject = Class.forName(pluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error(
                    "Apex context distributor class not found for context distributor plugin \"" + pluginClass + "\"",
                    e);
            throw new ContextException(
                    "Apex context distributor class not found for context distributor plugin \"" + pluginClass + "\"",
                    e);
        }

        // Check the class is a distributor
        if (!(contextDistributorObject instanceof Distributor)) {
            final String returnString = "Specified Apex context distributor plugin class \"" + pluginClass
                    + "\" does not implement the ContextDistributor interface";
            LOGGER.error(returnString);
            throw new ContextException(returnString);
        }

        // The context Distributor to return
        final Distributor contextDistributor = (Distributor) contextDistributorObject;

        // Lock and load the context distributor
        contextDistributor.init(key);

        LOGGER.exit(
                "Distributor factory, key=" + key + ", selected distributor of class " + contextDistributor.getClass());
        return contextDistributor;
    }
}
