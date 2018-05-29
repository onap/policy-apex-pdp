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

package org.onap.policy.apex.context.impl.persistence.ephemeral;

import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Persistor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;

/**
 * This class acts as an "in memory" persistor for a single JVM. It just initiates stubs the Persistor interface and
 * does not persist anything.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EphemeralPersistor implements Persistor {

    // The key of this persistor
    private AxArtifactKey key;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.Persistor#init(org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public void init(final AxArtifactKey persistorKey) throws ContextException {
        this.key = persistorKey;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.context.Persistor#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.Persistor#readContextItem(org.onap.policy.apex.core.basicmodel.concepts.
     * AxReferenceKey, java.lang.Class)
     */
    @Override
    public AxContextSchema readContextItem(final AxReferenceKey itemKey, final Class<?> contextItemClass) {
        // Can't read from this persistor as nothing is persisted
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.context.Persistor#readContextItems(org.onap.policy.apex.core.basicmodel.concepts.
     * AxArtifactKey, java.lang.Class)
     */
    @Override
    public Set<AxContextSchema> readContextItems(final AxArtifactKey ownerKey, final Class<?> contextItemClass)
            throws ContextException {
        // No reading from persistence on the Ephemeral persistor, return an empty set
        return new TreeSet<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.context.Persistor#writeContextItem(org.onap.policy.apex.core.contextmodel.concepts.
     * AxContextItem)
     */
    @Override
    public Object writeContextItem(final Object contextItem) {
        // No writing to persistence on the Ephemeral persistor
        return contextItem;
    }
}
