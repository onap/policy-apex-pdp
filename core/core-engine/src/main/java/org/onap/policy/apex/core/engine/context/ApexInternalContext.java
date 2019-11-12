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

package org.onap.policy.apex.core.engine.context;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetterImpl;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.handling.ContextComparer;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * This class manages the internal context for an Apex engine. This class is not thread safe and need not be because
 * each Context object is owned by one and only one ApexEngine, which runs in a single thread and only runs one policy
 * at a time. Therefore there is only ever one policy using a Context object at a time. The currentPolicyContextAlbum is
 * set on the Context object by the StateMachineExecutor each time a policy is triggered.
 *
 * @author Liam Fallon
 */
public class ApexInternalContext implements AxConceptGetter<ContextAlbum> {
    // The key of the currently running Apex model
    private AxArtifactKey key;

    // The context albums being used in this engine
    private final NavigableMap<AxArtifactKey, ContextAlbum> contextAlbums =
            Maps.synchronizedNavigableMap(new TreeMap<AxArtifactKey, ContextAlbum>());

    // The internal context uses a context distributor to handle distribution of context across multiple instances
    private Distributor contextDistributor = null;

    // The key of the current policy, used to return the correct policy context album to the user
    private AxArtifactKey currentPolicyKey = null;

    /**
     * Constructor, instantiate the context object from the Apex model.
     *
     * @param apexPolicyModel the apex model
     * @throws ContextException On errors on context setting
     */
    public ApexInternalContext(final AxPolicyModel apexPolicyModel) throws ContextException {
        if (apexPolicyModel == null) {
            throw new ContextException("internal context update failed, supplied model is null");
        }
        apexPolicyModel.register();

        // The context distributor used to distribute context across policy engine instances
        contextDistributor = new DistributorFactory().getDistributor(apexPolicyModel.getKey());

        // Set up the context albums for this engine
        for (final AxArtifactKey contextAlbumKey : ModelService.getModel(AxContextAlbums.class).getAlbumsMap()
                .keySet()) {
            contextAlbums.put(contextAlbumKey, contextDistributor.createContextAlbum(contextAlbumKey));
        }

        // Record the key of the current model
        key = apexPolicyModel.getKey();
    }

    /**
     * Get the key of the internal context, which is the same as the key of the engine.
     *
     * @return the key
     */
    public AxArtifactKey getKey() {
        return key;
    }

    /**
     * Get the context albums of the engine.
     *
     * @return the context albums
     */
    public Map<AxArtifactKey, ContextAlbum> getContextAlbums() {
        return contextAlbums;
    }

    /**
     * Update the current context so that it aligns with this incoming model, transferring context values if they exist
     * in the new model.
     *
     * @param newPolicyModel The new incoming Apex model to use for context
     * @param isSubsequentInstance if the current worker instance being updated is not the first one
     * @throws ContextException On errors on context setting
     */
    public void update(final AxPolicyModel newPolicyModel, boolean isSubsequentInstance) throws ContextException {
        if (newPolicyModel == null) {
            throw new ContextException("internal context update failed, supplied model is null");
        }
        // context is shared between all the engine instances
        // during model update context album only needs to be updated for the first instance.
        // remaining engine instances can just copy the context
        if (isSubsequentInstance) {
            contextAlbums.clear();
            for (AxArtifactKey contextAlbumKey : ModelService.getModel(AxContextAlbums.class).getAlbumsMap().keySet()) {
                contextAlbums.put(contextAlbumKey, contextDistributor.createContextAlbum(contextAlbumKey));
            }
            key = newPolicyModel.getKey();
            return;
        }
        // Get the differences between the existing context and the new context
        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> contextDifference =
                new ContextComparer().compare(ModelService.getModel(AxContextAlbums.class), newPolicyModel.getAlbums());


        // Handle the updated maps
        for (final Entry<AxArtifactKey, List<AxContextAlbum>> contextAlbumEntry : contextDifference.getDifferentValues()
                .entrySet()) {
            // Compare the updated maps
            final AxContextAlbum currentContextAlbum = contextAlbumEntry.getValue().get(0);
            final AxContextAlbum newContextAlbum = contextAlbumEntry.getValue().get(1);

            // Check that the schemas are the same on the old and new context albums
            if (!currentContextAlbum.getItemSchema().equals(newContextAlbum.getItemSchema())) {
                // The schema is different, throw an exception because the schema should not change if the key of the
                // album has not changed
                throw new ContextException("internal context update failed on context album \""
                        + contextAlbumEntry.getKey().getId() + "\" in model \"" + key.getId() + "\", schema \""
                        + currentContextAlbum.getItemSchema().getId()
                        + "\" on existing context model does not equal schema \""
                        + newContextAlbum.getItemSchema().getId() + "\" on incoming model");
            }
        }

        // Remove maps that are no longer used
        for (final Entry<AxArtifactKey, AxContextAlbum> removedContextAlbumEntry : contextDifference.getLeftOnly()
                .entrySet()) {
            contextDistributor.removeContextAlbum(removedContextAlbumEntry.getKey());
            contextAlbums.remove(removedContextAlbumEntry.getKey());
        }

        // We switch over to the new Apex model
        newPolicyModel.register();

        // Set up the new context albums
        for (final AxArtifactKey contextAlbumKey : contextDifference.getRightOnly().keySet()) {
            // In case if a context album is part of previous and current model, but needs to be cleared
            // for example, due to a major version change
            if (contextAlbums.containsKey(contextAlbumKey)) {
                contextDistributor.removeContextAlbum(contextAlbumKey);
            }
            contextAlbums.put(contextAlbumKey, contextDistributor.createContextAlbum(contextAlbumKey));
        }

        // Record the key of the current model
        key = newPolicyModel.getKey();
    }

    /**
     * Clear the internal context.
     *
     * @throws ContextException on clearing errors
     */
    public void clear() throws ContextException {
        // Clear all context in the distributor
        contextDistributor.clear();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "ApexInternalContext [contextAlbums=" + contextAlbums + ", contextDistributor=" + contextDistributor
                + ", currentPolicyKey=" + currentPolicyKey + "]";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ContextAlbum get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>(contextAlbums).get(conceptKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ContextAlbum get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>(contextAlbums).get(conceptKeyName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ContextAlbum get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>(contextAlbums).get(conceptKeyName, conceptKeyVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<ContextAlbum> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>(contextAlbums).getAll(conceptKeyName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<ContextAlbum> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>(contextAlbums).getAll(conceptKeyName, conceptKeyVersion);
    }
}
