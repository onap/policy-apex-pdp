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
package org.onap.policy.apex.plugins.context.distribution.infinispan;

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.test.distribution.ContextAlbumUpdate;
import org.onap.policy.apex.context.test.distribution.ContextInstantiation;
import org.onap.policy.apex.context.test.distribution.ContextUpdate;
import org.onap.policy.apex.context.test.distribution.SequentialContextInstantiation;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class InfinispanContextDistributorTest {
    private static final XLogger logger = XLoggerFactory.getXLogger(InfinispanContextDistributorTest.class);

    private static final String PLUGIN_CLASS = InfinispanContextDistributor.class.getCanonicalName();

    @Test
    public void testContextAlbumUpdateInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextAlbumUpdateInfinispan test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(PLUGIN_CLASS);
        new InfinispanDistributorParameters();

        new ContextAlbumUpdate().testContextAlbumUpdate();

        logger.debug("Ran testContextAlbumUpdateInfinispan test");
    }

    @Test
    public void testContextInstantiationInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextInstantiationInfinispan test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(PLUGIN_CLASS);
        new InfinispanDistributorParameters();

        new ContextInstantiation().testContextInstantiation();

        logger.debug("Ran testContextInstantiationInfinispan test");
    }

    @Test
    public void testContextUpdateInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateInfinispan test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(PLUGIN_CLASS);
        new InfinispanDistributorParameters();

        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateInfinispan test");
    }

    @Test
    public void testSequentialContextInstantiationInfinispan() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testSequentialContextInstantiationInfinispan test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(PLUGIN_CLASS);
        new InfinispanDistributorParameters();

        new SequentialContextInstantiation().testSequentialContextInstantiation();

        logger.debug("Ran testSequentialContextInstantiationInfinispan test");
    }


}
