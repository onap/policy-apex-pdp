/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2025 Nordix Foundation.
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

package org.onap.policy.apex.plugins.context.distribution.infinispan;

import java.util.Map;
import lombok.AccessLevel;
import lombok.Setter;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.impl.distribution.AbstractDistributor;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This context distributor distributes context across threads in multiple JVMs on multiple hosts. It uses Infinispan to
 * distribute maps.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class InfinispanContextDistributor extends AbstractDistributor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(InfinispanContextDistributor.class);

    // The infinispan manager for distributing context for this JVM
    @Setter(AccessLevel.PRIVATE)
    private static InfinispanManager infinispanManager = null;

    /**
     * Create an instance of an Infinispan Context Distributor.
     *
     * @throws ContextException On errors creating the context distributor
     */
    public InfinispanContextDistributor() throws ContextException {
        LOGGER.entry("InfinispanContextDistributor()");

        LOGGER.exit("InfinispanContextDistributor()");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final AxArtifactKey key) throws ContextException {
        LOGGER.entry("init(" + key + ")");

        super.init(key);

        // Create the infinispan manager if it does not already exist
        if (infinispanManager == null) {
            // Get the parameters from the parameter service
            final InfinispanDistributorParameters parameters = ParameterService
                            .get(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);

            LOGGER.debug("initiating Infinispan with the parameters: {}", parameters);

            // Create the manager
            setInfinispanManager(new InfinispanManager(parameters));
        }

        LOGGER.exit("init(" + key + ")");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Object> getContextAlbumMap(final AxArtifactKey contextAlbumKey) {
        LOGGER.info("InfinispanContextDistributor: create album: {}", contextAlbumKey.getId());

        // Get the Cache from Infinispan
        return infinispanManager.getCacheManager().getCache(contextAlbumKey.getId().replace(':', '_'));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void shutdown() {
        // Shut down the infinispan manager
        if (infinispanManager != null) {
            infinispanManager.shutdown();
        }
        setInfinispanManager(null);
    }
}
