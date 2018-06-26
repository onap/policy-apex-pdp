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

package com.ericsson.apex.context.test.distribution;


import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.common.test.distribution.ContextAlbumUpdate;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TestContextAlbumUpdate {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestContextAlbumUpdate.class);

    @Test
    public void testContextAlbumUpdateJVMLocalVarSet() throws Exception {
        logger.debug("Running testContextAlbumUpdateJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(JVMLocalDistributor.class.getCanonicalName());
        new ContextAlbumUpdate().testContextAlbumUpdate();

        logger.debug("Ran testContextAlbumUpdateJVMLocalVarSet test");
    }

    @Test
    public void testContextAlbumUpdateJVMLocalVarNotSet() throws Exception {
        logger.debug("Running testContextAlbumUpdateJVMLocalVarNotSet test . . .");

        new ContextParameters();
        new ContextAlbumUpdate().testContextAlbumUpdate();

        logger.debug("Ran testContextAlbumUpdateJVMLocalVarNotSet test");
    }

    /**
     * Test context update cleardown.
     */
    @After
    public void testContextAlbumUpdateCleardown() {}
}
