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

package org.onap.policy.apex.context.impl.persistence;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Persistor;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.utilities.Assertions;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class returns a persistor for the particular type of persistor mechanism that has been configured for use.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PersistorFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PersistorFactory.class);

    /**
     * Return a persistor for the persistence mechanism configured for use.
     *
     * @param key The key for the persistor
     * @return a persistor
     * @throws ContextException on invalid persistor types
     */
    public Persistor createPersistor(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("persistor factory, key=" + key);
        Assertions.argumentNotNull(key, ContextException.class, "Parameter \"key\" may not be null");

        final PersistorParameters persistorParameters = ParameterService
                        .get(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        // Get the class for the persistor using reflection
        Object persistorObject = null;
        final String pluginClass = persistorParameters.getPluginClass();
        try {
            persistorObject = Class.forName(pluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error("Apex context persistor class not found for context persistor plugin \"" + pluginClass + "\"",
                            e);
            throw new ContextException("Apex context persistor class not found for context persistor plugin \""
                            + pluginClass + "\"", e);
        }

        // Check the class is a persistor
        if (!(persistorObject instanceof Persistor)) {
            LOGGER.error("Specified Apex context persistor plugin class \"{}\" does not implement the ContextDistributor interface",
                            pluginClass);
            throw new ContextException("Specified Apex context persistor plugin class \"" + pluginClass
                            + "\" does not implement the ContextDistributor interface");
        }

        // The persistor to return
        final Persistor persistor = (Persistor) persistorObject;

        // Lock and load the persistor
        persistor.init(key);

        LOGGER.exit("Persistor factory, key=" + key + ", selected persistor of class " + persistor.getClass());
        return persistor;
    }
}
