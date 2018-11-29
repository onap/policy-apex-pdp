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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.onap.policy.apex.context.test.utils.Constants.APEX_DISTRIBUTOR;
import static org.onap.policy.apex.context.test.utils.Constants.VERSION;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ContextAlbumUpdate is used to test Context Album updates.
 */
public class ContextAlbumUpdate {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextAlbumUpdate.class);

    /**
     * Test context album update.
     *
     * @throws ApexModelException the apex model exception
     * @throws ApexException the apex exception
     */
    public void testContextAlbumUpdate() throws ApexException {
        LOGGER.debug("Running TestContextAlbumUpdate test . . .");

        final AxArtifactKey distributorKey = new AxArtifactKey(APEX_DISTRIBUTOR, VERSION);
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        final AxContextModel longModel = TestContextAlbumFactory.createLongContextModel();
        contextDistributor.registerModel(longModel);

        final AxContextAlbum longAlbum1Def = longModel.getAlbums().get(new AxArtifactKey("LongContextAlbum1", VERSION));
        final ContextAlbum longAlbum1 = contextDistributor.createContextAlbum(longAlbum1Def.getKey());

        assertNotNull(longAlbum1);

        final AxContextAlbum longAlbum2Def = longModel.getAlbums().get(new AxArtifactKey("LongContextAlbum2", VERSION));
        final ContextAlbum longAlbum2 = contextDistributor.createContextAlbum(longAlbum2Def.getKey());

        assertNotNull(longAlbum2);

        longAlbum1.put("0", (long) 0);
        longAlbum1.put("1", (long) 1);
        longAlbum1.put("2", (long) 2);
        longAlbum1.put("3", (long) 3);

        final KeyedMapDifference<String, Object> result0 =
                new KeyedMapComparer<String, Object>().compareMaps(longAlbum1, longAlbum2);

        assertEquals(0, result0.getDifferentValues().size());
        assertEquals(0, result0.getIdenticalValues().size());
        assertEquals(0, result0.getRightOnly().size());
        assertEquals(4, result0.getLeftOnly().size());

        longAlbum2.putAll(longAlbum1);

        final KeyedMapDifference<String, Object> result1 =
                new KeyedMapComparer<String, Object>().compareMaps(longAlbum1, longAlbum2);


        assertEquals(0, result1.getDifferentValues().size());
        assertEquals(4, result1.getIdenticalValues().size());
        assertEquals(0, result1.getRightOnly().size());
        assertEquals(0, result1.getLeftOnly().size());

        longAlbum1.put("4", (long) 4);
        longAlbum2.put("5", (long) 5);
        longAlbum1.put("67", (long) 6);
        longAlbum2.put("67", (long) 7);

        final KeyedMapDifference<String, Object> result2 =
                new KeyedMapComparer<String, Object>().compareMaps(longAlbum1, longAlbum2);

        assertEquals(1, result2.getDifferentValues().size());
        assertEquals(4, result2.getIdenticalValues().size());
        assertEquals(1, result2.getRightOnly().size());
        assertEquals(1, result2.getLeftOnly().size());

        longAlbum1.remove("0");
        longAlbum2.remove("3");

        final KeyedMapDifference<String, Object> result3 =
                new KeyedMapComparer<String, Object>().compareMaps(longAlbum1, longAlbum2);

        assertEquals(1, result3.getDifferentValues().size());
        assertEquals(2, result3.getIdenticalValues().size());
        assertEquals(2, result3.getRightOnly().size());
        assertEquals(2, result3.getLeftOnly().size());
        contextDistributor.clear();
    }
}
