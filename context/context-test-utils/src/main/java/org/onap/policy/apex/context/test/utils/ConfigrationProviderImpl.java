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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.context.test.lock.modifier.AlbumModifier;
import org.onap.policy.apex.context.test.lock.modifier.LockType;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * The Class ConfigrationProviderImpl provides configuration information for a context test back to the caller.
 */
public class ConfigrationProviderImpl implements ConfigrationProvider {

    private final String testType;
    private final int jvmCount;
    private final int threadCount;
    private final int loopSize;
    private final int albumSize;
    private final LockType lockType;

    /**
     * The parameterized ConfigrationProviderImpl constructor.
     * 
     * @param testType the test type
     * @param jvmCount the JVM count
     * @param threadCount the thread count
     * @param loopSize the size of loop
     * @param albumSize the size of album
     * @param lockType the lock type
     */
    public ConfigrationProviderImpl(final String testType, final int jvmCount, final int threadCount,
                    final int loopSize, final int albumSize, final int lockType) {
        this.testType = testType;
        this.jvmCount = jvmCount;
        this.threadCount = threadCount;
        this.loopSize = loopSize;
        this.albumSize = albumSize;
        this.lockType = LockType.getLockType(lockType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getTestName()
     */
    @Override
    public String getTestName() {
        return testType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getLoopSize()
     */
    @Override
    public int getLoopSize() {
        return loopSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getThreadCount()
     */
    @Override
    public int getThreadCount() {
        return threadCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getJvmCount()
     */
    @Override
    public int getJvmCount() {
        return jvmCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getAlbumSize()
     */
    @Override
    public int getAlbumSize() {
        return albumSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getExecutorService()
     */
    @Override
    public ExecutorService getExecutorService() {
        final String name = getThreadFactoryName(jvmCount, testType);
        final IntegrationThreadFactory threadFactory = new IntegrationThreadFactory(name);
        return Executors.newFixedThreadPool(threadCount, threadFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getExecutorService(java.lang.String, int)
     */
    @Override
    public ExecutorService getExecutorService(final String threadFactoryName, final int threadPoolSize) {
        final IntegrationThreadFactory threadFactory = new IntegrationThreadFactory(threadFactoryName);
        return Executors.newFixedThreadPool(threadPoolSize, threadFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.context.test.utils.ConfigrationProvider#getDistributor(org.onap.policy.apex.model.basicmodel
     * .concepts.AxArtifactKey)
     */
    @Override
    public Distributor getDistributor(final AxArtifactKey key) {
        try {
            return new DistributorFactory().getDistributor(key);
        } catch (ContextException e) {
            throw new ContextRuntimeException("Unable to create Distributor", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getDistributor()
     */
    @Override
    public Distributor getDistributor() {
        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        return getDistributor(distributorKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getContextAlbum(org.onap.policy.apex.context.
     * Distributor)
     */
    @Override
    public ContextAlbum getContextAlbum(final Distributor distributor) {
        return getContextAlbum(distributor, Constants.L_TYPE_CONTEXT_ALBUM, Constants.getAxArtifactKeyArray());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getContextAlbum(org.onap.policy.apex.context.
     * Distributor, org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey,
     * org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey[])
     */
    @Override
    public ContextAlbum getContextAlbum(final Distributor distributor, final AxArtifactKey axContextAlbumKey,
                    final AxArtifactKey[] artifactKeys) {
        final AxContextModel axContextModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
        try {
            distributor.registerModel(axContextModel);
            final ContextAlbum contextAlbum = distributor.createContextAlbum(axContextAlbumKey);
            Assertions.argumentNotNull(contextAlbum, "ContextAlbum should not be null");
            contextAlbum.setUserArtifactStack(artifactKeys);
            return contextAlbum;
        } catch (ContextException e) {
            throw new ContextRuntimeException("Unable to create ContextAlbum", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getContextAlbumInitValues()
     */
    @Override
    public Map<String, Object> getContextAlbumInitValues() {
        final Map<String, Object> values = new HashMap<>();
        for (int i = 0; i < albumSize; i++) {
            values.put(Integer.toString(i), new TestContextLongItem(0L));
        }
        return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getAlbumModifier()
     */
    @Override
    public AlbumModifier getAlbumModifier() {
        return lockType.getAlbumModifier();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.context.test.utils.ConfigrationProvider#getLockType()
     */
    @Override
    public LockType getLockType() {
        return lockType;
    }

    /**
     * Gets the thread factory name.
     *
     * @param jvmCount the jvm count
     * @param testType the test type
     * @return the thread factory name
     */
    private String getThreadFactoryName(final int jvmCount, final String testType) {
        return jvmCount == 1 ? testType + ":TestConcurrentContextThread_0_"
                        : testType + ":TestConcurrentContextJVMThread_";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConfigrationProviderImpl [testType=" + testType + ", jvmCount=" + jvmCount + ", threadCount="
                        + threadCount + ", loopSize=" + loopSize + ", albumSize=" + albumSize + ", lockType=" + lockType
                        + "]";
    }

}
