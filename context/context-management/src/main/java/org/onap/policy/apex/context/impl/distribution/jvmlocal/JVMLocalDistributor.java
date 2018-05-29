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

package org.onap.policy.apex.context.impl.distribution.jvmlocal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.apex.context.impl.distribution.AbstractDistributor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This context distributor distributes context across threads in a single JVM. It holds context in memory in a map.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JVMLocalDistributor extends AbstractDistributor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JVMLocalDistributor.class);

    /**
     * Create an instance of a JVM Local Context Distributor.
     */
    public JVMLocalDistributor() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.context.impl.distribution.AbstractDistributor#getContextAlbumMap(org.onap.policy.apex.model.
     * basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public Map<String, Object> getContextAlbumMap(final AxArtifactKey contextMapKey) {
        LOGGER.debug("create map: " + contextMapKey.getID());
        return Collections.synchronizedMap(new HashMap<String, Object>());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.impl.distribution.AbstractDistributor#shutdown()
     */
    @Override
    public void shutdown() {
        // No specific shutdown for the JVMLocalContextDistributor
    }
}
