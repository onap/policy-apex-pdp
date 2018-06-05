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

package org.onap.policy.apex.plugins.context.test.distribution;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.test.distribution.ContextUpdate;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanDistributorParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TestContextUpdate {
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestContextUpdate.class);

    @Before
    public void beforeTest() {}

    @After
    public void afterTest() {}

    @Test
    public void testContextUpdateJVMLocalVarSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters()
                .setPluginClass(DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateJVMLocalVarSet test");
    }

    @Test
    public void testContextUpdateHazelcast() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateHazelcast test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(
                "org.onap.policy.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor");
        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateHazelcast test");
    }

    @Test
    public void testContextUpdateInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateInfinispan test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(
                "org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanContextDistributor");
        new InfinispanDistributorParameters();

        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateInfinispan test");
    }

    @Test
    public void testContextUpdateJVMLocalVarNotSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateJVMLocalVarNotSet test . . .");

        new ContextParameters();
        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateJVMLocalVarNotSet test");
    }
}
