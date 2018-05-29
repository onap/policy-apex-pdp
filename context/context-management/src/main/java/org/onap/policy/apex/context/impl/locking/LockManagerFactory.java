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

package org.onap.policy.apex.context.impl.locking;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.LockManager;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class returns a {@link LockManager} for the particular type of locking mechanism that has been configured for
 * use.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class LockManagerFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(LockManagerFactory.class);

    /**
     * Return a {@link LockManager} for the particular type of locking mechanism configured for use.
     *
     * @param key The key for the lock manager
     * @return a lock manager that can generate locks using some underlying mechanism
     * @throws ContextException on errors in getting a lock manager
     */
    public LockManager createLockManager(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("Lock Manager factory, key=" + key);

        final LockManagerParameters lockManagerParameters = ParameterService.getParameters(LockManagerParameters.class);

        // Get the class for the lock manager using reflection
        Object lockManagerObject = null;
        final String pluginClass = lockManagerParameters.getPluginClass();
        try {
            lockManagerObject = Class.forName(pluginClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error(
                    "Apex context lock manager class not found for context lock manager plugin \"" + pluginClass + "\"",
                    e);
            throw new ContextException(
                    "Apex context lock manager class not found for context lock manager plugin \"" + pluginClass + "\"",
                    e);
        }

        // Check the class is a lock manager
        if (!(lockManagerObject instanceof LockManager)) {
            LOGGER.error("Specified Apex context lock manager plugin class \"" + pluginClass
                    + "\" does not implement the LockManager interface");
            throw new ContextException("Specified Apex context lock manager plugin class \"" + pluginClass
                    + "\" does not implement the LockManager interface");
        }

        // The context lock manager to return
        final LockManager lockManager = (LockManager) lockManagerObject;

        // Lock and load (OK sorry!!!) the lock manager
        lockManager.init(key);

        LOGGER.exit("Lock manager factory, key=" + key + ", selected lock manager of class " + lockManager.getClass());
        return lockManager;
    }
}
