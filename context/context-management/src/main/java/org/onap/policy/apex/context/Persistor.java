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

package org.onap.policy.apex.context;

import java.util.Set;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;

/**
 * This interface is implemented by plugin classes that persist Context Albums in Apex.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface Persistor {

    /**
     * Initialize the persistor with its properties.
     *
     * @param key The key that identifies this persistor
     * @throws ContextException On errors initializing the persistor
     */
    void init(AxArtifactKey key) throws ContextException;

    /**
     * Get the key of the persistor.
     *
     * @return the contextSetKey
     */
    AxArtifactKey getKey();

    /**
     * Read a context item from the persistence mechanism.
     *
     * @param key the key of the context item
     * @param contextItemClassName the name of the context item class, a subclass of {@link AxContextSchema}
     * @return the context item that has been read
     * @throws ContextException on persistence read errors
     */
    AxContextSchema readContextItem(AxReferenceKey key, Class<?> contextItemClassName) throws ContextException;

    /**
     * Read all the values of a particular type from persistence.
     *
     * @param ownerKey the owner key
     * @param contextItemClassName The class name of the objects to return
     * @return the set of context item values read from persistence or null if none were found
     * @throws ContextException On read errors
     */
    Set<AxContextSchema> readContextItems(AxArtifactKey ownerKey, Class<?> contextItemClassName)
            throws ContextException;

    /**
     * Write a context item value to the persistence mechanism.
     *
     * @param contextItem the context item
     * @return the context item that has been written
     */
    Object writeContextItem(Object contextItem);
}
