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

package org.onap.policy.apex.context.test.utils;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.test.lock.modifier.AlbumModifier;
import org.onap.policy.apex.context.test.lock.modifier.LockType;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Interface ConfigrationProvider provides the configuration for a context test to a context test executor.
 */
public interface ConfigrationProvider {

    /**
     * Gets the test name.
     *
     * @return the test name
     */
    String getTestName();

    /**
     * Gets the loop size.
     *
     * @return the loop size
     */
    int getLoopSize();

    /**
     * Gets the thread count.
     *
     * @return the thread count
     */
    int getThreadCount();

    /**
     * Gets the jvm count.
     *
     * @return the jvm count
     */
    int getJvmCount();

    /**
     * Gets the album size.
     *
     * @return the album size
     */
    int getAlbumSize();

    /**
     * Gets the executor service.
     *
     * @return the executor service
     */
    ExecutorService getExecutorService();

    /**
     * Gets the executor service.
     *
     * @param threadFactoryName the thread factory name
     * @param threadPoolSize the thread pool size
     * @return the executor service
     */
    ExecutorService getExecutorService(final String threadFactoryName, final int threadPoolSize);

    /**
     * Gets the distributor.
     *
     * @param key the key
     * @return the distributor
     */
    Distributor getDistributor(final AxArtifactKey key);

    /**
     * Gets the distributor.
     *
     * @return the distributor
     */
    Distributor getDistributor();

    /**
     * Gets the context album.
     *
     * @param distributor the distributor
     * @return the context album
     */
    ContextAlbum getContextAlbum(final Distributor distributor);

    /**
     * Gets the context album.
     *
     * @param distributor the distributor
     * @param axContextAlbumKey the ax context album key
     * @param artifactKeys the artifact keys
     * @return the context album
     * @throws ContextException the context exception
     */
    ContextAlbum getContextAlbum(final Distributor distributor, AxArtifactKey axContextAlbumKey,
                    AxArtifactKey[] artifactKeys) throws ContextException;

    /**
     * Gets the context album init values.
     *
     * @return the context album init values
     */
    Map<String, Object> getContextAlbumInitValues();

    /**
     * Gets the album modifier.
     *
     * @return the album modifier
     */
    AlbumModifier getAlbumModifier();

    /**
     * Gets the lock type.
     *
     * @return the lock type
     */
    LockType getLockType();

}
