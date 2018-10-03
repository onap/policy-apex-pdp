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

package org.onap.policy.apex.context.impl;

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
 * Dummy album implementation class.
 *
 */
public class DummyContextAlbumImpl implements ContextAlbum {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public Object get(Object key) {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<String> keySet() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(String key, Object value) {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public Object remove(Object key) {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#values()
     */
    @Override
    public Collection<Object> values() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#getName()
     */
    @Override
    public String getName() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#getAlbumDefinition()
     */
    @Override
    public AxContextAlbum getAlbumDefinition() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#getSchemaHelper()
     */
    @Override
    public SchemaHelper getSchemaHelper() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#lockForReading(java.lang.String)
     */
    @Override
    public void lockForReading(String key) throws ContextException {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#lockForWriting(java.lang.String)
     */
    @Override
    public void lockForWriting(String key) throws ContextException {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#unlockForReading(java.lang.String)
     */
    @Override
    public void unlockForReading(String key) throws ContextException {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#unlockForWriting(java.lang.String)
     */
    @Override
    public void unlockForWriting(String key) throws ContextException {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#getUserArtifactStack()
     */
    @Override
    public AxConcept[] getUserArtifactStack() {
        throw new NotImplementedException("Not Implemeted on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.context.ContextAlbum#setUserArtifactStack(org.onap.policy.apex.model.basicmodel.concepts.
     * AxConcept[])
     */
    @Override
    public void setUserArtifactStack(AxConcept[] userArtifactStack) {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.ContextAlbum#flush()
     */
    @Override
    public void flush() throws ContextException {
        throw new NotImplementedException("Not Implemeted on dummy class");

    }

}
