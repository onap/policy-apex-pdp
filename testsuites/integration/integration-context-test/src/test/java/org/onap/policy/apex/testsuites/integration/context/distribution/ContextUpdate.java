/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.context.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.APEX_DISTRIBUTOR;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.DATE_CONTEXT_ALBUM;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.LONG_CONTEXT_ALBUM;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.MAP_CONTEXT_ALBUM;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.TIME_ZONE;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.VERSION;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.getAxArtifactKeyArray;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextDateItem;
import org.onap.policy.apex.context.test.concepts.TestContextDateLocaleItem;
import org.onap.policy.apex.context.test.concepts.TestContextTreeMapItem;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.testsuites.integration.context.factory.TestContextAlbumFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestContextUpdate checks context updates.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class ContextUpdate {
    private static final String ZERO = "zero";
    private static final String NUMBER_ZERO = "0";
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextUpdate.class);

    /**
     * Test context update.
     *
     * @throws ApexException the apex exception
     */
    public void testContextUpdate() throws ApexException {
        LOGGER.debug("Running TestContextUpdate test . . .");

        final Distributor contextDistributor = getDistributor();

        final ContextAlbum longContextAlbum = getContextAlbum(LONG_CONTEXT_ALBUM, contextDistributor);
        final ContextAlbum dateContextAlbum = getContextAlbum(DATE_CONTEXT_ALBUM, contextDistributor);
        final ContextAlbum mapContextAlbum = getContextAlbum(MAP_CONTEXT_ALBUM, contextDistributor);

        final TestContextDateLocaleItem tciA = getTestContextDateLocaleItem();
        final TestContextTreeMapItem tciC = getTestContextTreeMapItem();

        longContextAlbum.put(NUMBER_ZERO, (long) 0);
        longContextAlbum.put(NUMBER_ZERO, 0);
        longContextAlbum.put(NUMBER_ZERO, NUMBER_ZERO);

        assertThatThrownBy(() -> longContextAlbum.put(NUMBER_ZERO, ZERO))
            .hasMessage("Failed to set context value for key \"0\" in album \"LongContextAlbum:0.0.1\":"
                + " LongContextAlbum:0.0.1: object \"zero\" of class \"java.lang.String\" not compatible with"
                + " class \"java.lang.Long\"");
        assertThatThrownBy(() -> longContextAlbum.put(NUMBER_ZERO, ""))
            .hasMessage("Failed to set context value for key \"0\" in album \"LongContextAlbum:0.0.1\": "
                + "LongContextAlbum:0.0.1: object \"\" of class \"java.lang.String\" not "
                + "compatible with class \"java.lang.Long\"");
        assertThatThrownBy(() -> longContextAlbum.put(NUMBER_ZERO, null))
            .hasMessage("album \"LongContextAlbum:0.0.1\" null values are illegal on key \"0\" for put()");
        assertThatThrownBy(() -> longContextAlbum.put(null, null))
            .hasMessage("album \"LongContextAlbum:0.0.1\" null keys are illegal on keys for put()");

        assertNull(dateContextAlbum.put("date0", tciA));
        assertThat(dateContextAlbum.put("date0", tciA)).isEqualTo(tciA);

        assertNull(mapContextAlbum.put("map0", tciC));
        assertThat(mapContextAlbum.put("map0", tciC)).isEqualTo(tciC);

        contextDistributor.clear();
    }

    private TestContextTreeMapItem getTestContextTreeMapItem() {
        final Map<String, String> testHashMap = new HashMap<>();
        testHashMap.put(NUMBER_ZERO, ZERO);
        testHashMap.put("1", "one");
        testHashMap.put("2", "two");
        testHashMap.put("3", "three");
        testHashMap.put("4", "four");

        return new TestContextTreeMapItem(testHashMap);
    }

    private TestContextDateLocaleItem getTestContextDateLocaleItem() {
        final TestContextDateLocaleItem tciA = new TestContextDateLocaleItem();
        tciA.setDateValue(new TestContextDateItem(new Date()));
        tciA.setTzValue(TIME_ZONE.getDisplayName());
        tciA.setDst(true);
        tciA.setUtcOffset(-600);
        tciA.setLocale(Locale.ENGLISH);
        return tciA;
    }

    private ContextAlbum getContextAlbum(final String albumKey, final Distributor contextDistributor)
        throws ContextException {
        final ContextAlbum longContextAlbum = contextDistributor
            .createContextAlbum(new AxArtifactKey(albumKey, VERSION));
        assertNotNull(longContextAlbum);
        longContextAlbum.setUserArtifactStack(getAxArtifactKeyArray());
        return longContextAlbum;
    }

    private Distributor getDistributor() throws ContextException {
        final AxArtifactKey distributorKey = new AxArtifactKey(APEX_DISTRIBUTOR, VERSION);
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        final AxContextModel multiModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
        contextDistributor.registerModel(multiModel);
        return contextDistributor;
    }
}
