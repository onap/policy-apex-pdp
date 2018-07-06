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

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JVMLocalLockManager;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
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
    private static final int TEST_JVM_COUNT_SINGLE_JVM = 1;
    private static final int TEST_JVM_COUNT_MULTI_JVM = 3;
    private static final int TEST_THREAD_COUNT_SINGLE_JVM = 64;
    private static final int TEST_THREAD_COUNT_MULTI_JVM = 20;
    private static final int TEST_THREAD_LOOPS = 100;

    @Test
    public void testConcurrentContextJVMLocalVarSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());
        final long result = new ConcurrentContext().testConcurrentContext("JVMLocalVarSet", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);

        logger.debug("Ran testConcurrentContextJVMLocalVarSet test");
    }

    @Test
    public void testConcurrentContextJVMLocalNoVarSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextJVMLocalNoVarSet test . . .");

        new ContextParameters();
        final long result = new ConcurrentContext().testConcurrentContext("JVMLocalNoVarSet", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);

        logger.debug("Ran testConcurrentContextJVMLocalNoVarSet test");
    }

    @Test
    public void testConcurrentContextMultiJVMNoLock() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextMultiJVMNoLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(JVMLocalDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());

        final long result = new ConcurrentContext().testConcurrentContext("testConcurrentContextMultiJVMNoLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        // No concurrent map so result will be zero
        assertEquals(0, result);

        logger.debug("Ran testConcurrentContextMultiJVMNoLock test");
    }
}
