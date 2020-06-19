/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.core.engine.executor.context;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;

/**
 * Dummy context album for testing.
 */
public class DummyContextAlbum implements ContextAlbum {
    private final AxArtifactKey key;

    /**
     * Constructor.
     * @param key the key
     */
    public DummyContextAlbum(AxArtifactKey key) {
        super();
        this.key = key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clear() {
        throw new NotImplementedException("Not implemented on dummy class");
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean containsKey(Object key) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean containsValue(Object value) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object get(Object key) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isEmpty() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<String> keySet() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object put(String key, Object value) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Object remove(Object key) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int size() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<Object> values() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxContextAlbum getAlbumDefinition() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public SchemaHelper getSchemaHelper() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void lockForReading(String key) throws ContextException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void lockForWriting(String key) throws ContextException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForReading(String key) throws ContextException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void unlockForWriting(String key) throws ContextException {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept[] getUserArtifactStack() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setUserArtifactStack(AxConcept[] userArtifactStack) {
        // Do nothing
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void flush() throws ContextException {
        throw new NotImplementedException("Not implemented on dummy class");
    }
}
