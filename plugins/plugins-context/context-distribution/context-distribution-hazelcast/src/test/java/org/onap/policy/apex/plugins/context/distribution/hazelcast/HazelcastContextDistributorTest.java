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

package org.onap.policy.apex.plugins.context.distribution.hazelcast;

import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.test.distribution.ContextAlbumUpdate;
import org.onap.policy.apex.context.test.distribution.ContextInstantiation;
import org.onap.policy.apex.context.test.distribution.ContextUpdate;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class HazelcastContextDistributorTest {
    private static final String HAZEL_CAST_PLUGIN_CLASS = HazelcastContextDistributor.class.getCanonicalName();
    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(HazelcastContextDistributorTest.class);


    @Test
    public void testContextAlbumUpdateHazelcast() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextAlbumUpdateHazelcast test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(HAZEL_CAST_PLUGIN_CLASS);
        new ContextAlbumUpdate().testContextAlbumUpdate();

        logger.debug("Ran testContextAlbumUpdateHazelcast test");
    }

    @Test
    public void testContextInstantiationHazelcast() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextInstantiationHazelcast test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(HAZEL_CAST_PLUGIN_CLASS);
        new ContextInstantiation().testContextInstantiation();

        logger.debug("Ran testContextInstantiationHazelcast test");
    }

    @Test
    public void testContextUpdateHazelcast() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testContextUpdateHazelcast test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(HAZEL_CAST_PLUGIN_CLASS);
        new ContextUpdate().testContextUpdate();

        logger.debug("Ran testContextUpdateHazelcast test");
    }

}
