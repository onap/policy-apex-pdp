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

package org.onap.policy.apex.context.test.distribution;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextItem008;
import org.onap.policy.apex.context.test.concepts.TestContextItem00A;
import org.onap.policy.apex.context.test.concepts.TestContextItem00C;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestContextUpdate checks context updates.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class ContextUpdate {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextUpdate.class);

    /**
     * Test context update.
     *
     * @throws ApexModelException the apex model exception
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    public void testContextUpdate() throws ApexModelException, IOException, ApexException {
        LOGGER.debug("Running TestContextUpdate test . . .");

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        // @formatter:off
        final AxArtifactKey[] usedArtifactStackArray = {
                new AxArtifactKey("testC-top", "0.0.1"),
                new AxArtifactKey("testC-next", "0.0.1"),
                new AxArtifactKey("testC-bot", "0.0.1")
        };
        // @formatter:on

        // CHECKSTYLE:OFF: checkstyle:magicNumber

        final AxContextModel multiModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
        contextDistributor.registerModel(multiModel);

        final ContextAlbum longContextAlbum =
                contextDistributor.createContextAlbum(new AxArtifactKey("LongContextAlbum", "0.0.1"));
        assert (longContextAlbum != null);
        longContextAlbum.setUserArtifactStack(usedArtifactStackArray);

        final ContextAlbum dateContextAlbum =
                contextDistributor.createContextAlbum(new AxArtifactKey("DateContextAlbum", "0.0.1"));
        assert (dateContextAlbum != null);
        longContextAlbum.setUserArtifactStack(usedArtifactStackArray);

        final ContextAlbum mapContextAlbum =
                contextDistributor.createContextAlbum(new AxArtifactKey("MapContextAlbum", "0.0.1"));
        assert (mapContextAlbum != null);
        mapContextAlbum.setUserArtifactStack(usedArtifactStackArray);

        final TestContextItem00A tciA = new TestContextItem00A();
        tciA.setDateValue(new TestContextItem008(new Date()));
        tciA.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
        tciA.setDST(true);
        tciA.setUTCOffset(-600);
        tciA.setLocale(Locale.ENGLISH);

        final Map<String, String> testHashMap = new HashMap<>();
        testHashMap.put("0", "zero");
        testHashMap.put("1", "one");
        testHashMap.put("2", "two");
        testHashMap.put("3", "three");
        testHashMap.put("4", "four");

        final TestContextItem00C tciC = new TestContextItem00C(testHashMap);

        longContextAlbum.put("0", (long) 0);
        longContextAlbum.put("0", 0);
        longContextAlbum.put("0", "0");

        try {
            longContextAlbum.put("0", "zero");
            assert ("Test should throw an exception".equals(""));
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage().equals(
                    "Failed to set context value for key \"0\" in album \"LongContextAlbum:0.0.1\": LongContextAlbum:0.0.1: object \"zero\" of class \"java.lang.String\" not compatible with class \"java.lang.Long\""));
        }

        try {
            longContextAlbum.put("0", "");
            assert ("Test should throw an exception".equals(""));
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage().equals(
                    "Failed to set context value for key \"0\" in album \"LongContextAlbum:0.0.1\": LongContextAlbum:0.0.1: object \"\" of class \"java.lang.String\" not compatible with class \"java.lang.Long\""));
        }

        try {
            longContextAlbum.put("0", null);
            assert ("Test should throw an exception".equals(""));
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage()
                    .equals("album \"LongContextAlbum:0.0.1\" null values are illegal on key \"0\" for put()"));
        }

        try {
            longContextAlbum.put(null, null);
            assert ("Test should throw an exception".equals(""));
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage().equals("album \"LongContextAlbum:0.0.1\" null keys are illegal on keys for put()"));
        }

        assert (dateContextAlbum.put("date0", tciA) == null);
        assert (dateContextAlbum.put("date0", tciA).equals(tciA));

        assert (mapContextAlbum.put("map0", tciC) == null);
        assert (mapContextAlbum.put("map0", tciC).equals(tciC));

        contextDistributor.clear();
        // CHECKSTYLE:ON: checkstyle:magicNumber
    }
}
