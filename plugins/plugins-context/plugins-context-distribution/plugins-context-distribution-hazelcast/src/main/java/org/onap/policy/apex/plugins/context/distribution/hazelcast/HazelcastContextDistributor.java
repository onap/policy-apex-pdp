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

package org.onap.policy.apex.plugins.context.distribution.hazelcast;

import java.util.Map;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.distribution.AbstractDistributor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * This context distributor distributes context across threads in multiple JVMs on multiple hosts.
 * It uses hazelcast to distribute maps.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class HazelcastContextDistributor extends AbstractDistributor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(HazelcastContextDistributor.class);

    // The hazelcast instance for distributing context for this JVM
    private static HazelcastInstance hazelcastInstance = null;

    /**
     * Create an instance of a Hazelcast Context Distributor.
     *
     * @throws ContextException On errors creating the context distributor
     */
    public HazelcastContextDistributor() throws ContextException {
        super();
        LOGGER.entry("HazelcastContextDistributor()");

        LOGGER.exit("HazelcastContextDistributor()");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.impl.distribution.AbstractContextDistributor#init(org.onap.
     * policy.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("init(" + key + ")");

        super.init(key);

        // Create the hazelcast instance if it does not already exist
        if (hazelcastInstance == null) {
            hazelcastInstance = Hazelcast.newHazelcastInstance();
        }

        LOGGER.exit("init(" + key + ")");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.impl.distribution.AbstractContextDistributor#
     * getContextAlbumMap(org.onap.policy.apex.core.model.concepts.AxArtifactKey)
     */
    @Override
    public Map<String, Object> getContextAlbumMap(final AxArtifactKey contextAlbumKey) {
        // Get the map from Hazelcast
        LOGGER.info("HazelcastContextDistributor: create album: " + contextAlbumKey.getId());
        return hazelcastInstance.getMap(contextAlbumKey.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.impl.distribution.AbstractContextDistributor#shutdown()
     */
    @Override
    public void shutdown() {
        // Shut down the hazelcast instance
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
        hazelcastInstance = null;
    }
}
