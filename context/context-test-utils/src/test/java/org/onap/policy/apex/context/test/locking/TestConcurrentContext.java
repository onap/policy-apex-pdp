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

package org.onap.policy.apex.context.test.locking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.onap.policy.apex.context.test.lock.modifier.LockType.WRITE_LOCK_SINGLE_VALUE_UPDATE;
import static org.onap.policy.apex.context.test.utils.Constants.TEST_VALUE;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.context.test.utils.ConfigrationProviderImpl;
import org.onap.policy.apex.context.test.utils.Constants;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestConcurrentContext tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestConcurrentContext {

    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestConcurrentContext.class);

    // Test parameters
    private static final int ALBUM_SIZE = 16;
    private static final int TEST_JVM_COUNT_SINGLE_JVM = 1;
    private static final int TEST_JVM_COUNT_MULTI_JVM = 3;
    private static final int TEST_THREAD_COUNT_SINGLE_JVM = 64;
    private static final int TEST_THREAD_COUNT_MULTI_JVM = 20;
    private static final int TEST_THREAD_LOOPS = 100;

    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;

    /**
     * Set up context for tests.
     */
    @Before
    public void beforeTest() {
        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());
        
        schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    /**
     * Clear down context for tests.
     */
    @After
    public void afterTest() {
        ParameterService.deregister(schemaParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);
    }

    @Test
    public void testConcurrentContextJvmLocalVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalVarSet test . . .");

        contextParameters.getLockManagerParameters().setPluginClass(JvmLocalLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        assertFalse(result.isEmpty());
        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());


        logger.debug("Ran testConcurrentContextJVMLocalVarSet test");
    }

    @Test
    public void testConcurrentContextJvmLocalNoVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalNoVarSet test . . .");

        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalNoVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        assertFalse(result.isEmpty());
        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        logger.debug("Ran testConcurrentContextJVMLocalNoVarSet test");
    }

    @Test
    public void testConcurrentContextMultiJvmNoLock() throws Exception {
        logger.debug("Running testConcurrentContextMultiJVMNoLock test . . .");

        contextParameters.getDistributorParameters().setPluginClass(JvmLocalDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(JvmLocalLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("testConcurrentContextMultiJVMNoLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        // No concurrent map so result will be zero
        assertFalse(result.isEmpty());
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(0, actual.getLongValue());

        logger.debug("Ran testConcurrentContextMultiJVMNoLock test");
    }

    private ConfigrationProvider getConfigrationProvider(final String testType, final int jvmCount,
            final int threadCount, final int threadLoops) {
        return new ConfigrationProviderImpl(testType, jvmCount, threadCount, threadLoops, ALBUM_SIZE,
                WRITE_LOCK_SINGLE_VALUE_UPDATE.getValue()) {
            @Override
            public Map<String, Object> getContextAlbumInitValues() {
                final Map<String, Object> initValues = super.getContextAlbumInitValues();
                initValues.put(Constants.TEST_VALUE, new TestContextLongItem(0L));
                return initValues;
            }
        };
    }
}
