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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextItem000;
import org.onap.policy.apex.context.test.concepts.TestContextItem001;
import org.onap.policy.apex.context.test.concepts.TestContextItem002;
import org.onap.policy.apex.context.test.concepts.TestContextItem003;
import org.onap.policy.apex.context.test.concepts.TestContextItem004;
import org.onap.policy.apex.context.test.concepts.TestContextItem005;
import org.onap.policy.apex.context.test.concepts.TestContextItem006;
import org.onap.policy.apex.context.test.concepts.TestContextItem007;
import org.onap.policy.apex.context.test.concepts.TestContextItem008;
import org.onap.policy.apex.context.test.concepts.TestContextItem009;
import org.onap.policy.apex.context.test.concepts.TestContextItem00A;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class SequentialContextInstantiation checks sequential initiation of context.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class SequentialContextInstantiation {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SequentialContextInstantiation.class);

    /**
     * Test sequential context instantiation.
     *
     * @throws ContextException the context exception
     */
    // CHECKSTYLE:OFF: checkstyle:MaximumMethodLength
    public void testSequentialContextInstantiation() throws ContextException {
        // CHECKSTYLE:ON: MaximumMethodLength
        LOGGER.debug("Running TestContextInstantiation test . . .");

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        Distributor contextDistributor = null;
        try {
            contextDistributor = new DistributorFactory().getDistributor(distributorKey);
        } catch (final Exception e) {
            e.printStackTrace();
            assert ("Test has failed".equals(""));
            return;
        }

        // @formatter:off
       final AxArtifactKey[] usedArtifactStackArray = {
                new AxArtifactKey("testC-top", "0.0.1"),
                new AxArtifactKey("testC-next", "0.0.1"),
                new AxArtifactKey("testC-bot", "0.0.1")
                };
       // @formatter:on

        try {
            // CHECKSTYLE:OFF: checkstyle:magicNumber
            final AxContextModel multiModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
            contextDistributor.registerModel(multiModel);

            final ContextAlbum dateAlbum =
                    contextDistributor.createContextAlbum(new AxArtifactKey("DateContextAlbum", "0.0.1"));
            assert (dateAlbum != null);
            dateAlbum.setUserArtifactStack(usedArtifactStackArray);

            final Date testDate = new Date();

            final TestContextItem00A tciA00 = new TestContextItem00A();
            tciA00.setDateValue(new TestContextItem008(new Date()));
            tciA00.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
            tciA00.setDST(true);
            tciA00.setUTCOffset(-600);
            tciA00.setLocale(Locale.ENGLISH);

            dateAlbum.put("dv0", tciA00);
            assert (tciA00.equals(dateAlbum.get("dv0")));

            dateAlbum.put("dv1", tciA00);
            assert (tciA00.equals(dateAlbum.get("dv1")));

            final TestContextItem009 tci9 = new TestContextItem009();
            tci9.setDateValue(new TestContextItem008(testDate));
            tci9.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
            tci9.setDST(true);

            try {
                dateAlbum.put("tci9", tci9);
            } catch (final Exception e) {
                assert (e.getMessage().contains(
                        "class \"org.onap.policy.apex.context.test.concepts.TestContextItem009\" not compatible with class \"org.onap.policy.apex.context.test.concepts.TestContextItem00A\""));
            }

            final TestContextItem00A tciA01 = new TestContextItem00A(tciA00);
            final TestContextItem00A tciA02 = new TestContextItem00A(tciA00);
            final TestContextItem00A tciA03 = new TestContextItem00A(tciA00);

            final Map<String, Object> valueMap0 = new HashMap<>();
            valueMap0.put("Item01", tciA01);
            valueMap0.put("Item02", tciA02);
            valueMap0.put("Item03", tciA03);

            dateAlbum.putAll(valueMap0);
            assert (dateAlbum.size() == 5);
            assert (tciA01.equals(dateAlbum.get("Item01")));
            assert (tciA02.equals(dateAlbum.get("Item02")));
            assert (tciA03.equals(dateAlbum.get("Item03")));

            final Float testFloat = new Float(3.14159265359);

            final TestContextItem000 testBadItem000 = new TestContextItem000();
            final TestContextItem001 testBadItem001 = new TestContextItem001();
            final TestContextItem002 testBadItem002 = new TestContextItem002();
            final TestContextItem003 testBadItem003 = new TestContextItem003();
            final TestContextItem004 testBadItem004 = new TestContextItem004();
            final TestContextItem005 testBadItem005 = new TestContextItem005();
            final TestContextItem006 testBadItem006 = new TestContextItem006();
            final TestContextItem007 testBadItem007 = new TestContextItem007();
            final TestContextItem008 testBadItem008 = new TestContextItem008();

            testBadItem000.setFlag(false);
            testBadItem001.setByteValue((byte) 0xFF);
            testBadItem002.setIntValue(0xFFFFFFFF);
            testBadItem003.setLongValue(0xFFFFFFFFFFFFFFFFL);
            testBadItem004.setFloatValue(testFloat);
            testBadItem005.setDoubleValue(Math.PI);
            testBadItem006.setStringValue("This is a global context string");
            testBadItem007.setLongValue(testDate.getTime());
            testBadItem008.setDateValue(testDate);

            final Map<String, Object> valueMap1 = new HashMap<>();
            valueMap1.put("TestBadItem000", testBadItem000);
            valueMap1.put("TestBadItem001", testBadItem001);
            valueMap1.put("TestBadItem002", testBadItem002);
            valueMap1.put("TestBadItem003", testBadItem003);
            valueMap1.put("TestBadItem004", testBadItem004);
            valueMap1.put("TestBadItem005", testBadItem005);
            valueMap1.put("TestBadItem006", testBadItem006);
            valueMap1.put("TestBadItem007", testBadItem007);
            valueMap1.put("TestBadItem008", testBadItem008);
            valueMap1.put("TestBadItem009", tci9);
            valueMap1.put("Item00_2", tciA00);

            // Get another reference to the album
            final ContextAlbum dateAlbum1 =
                    contextDistributor.createContextAlbum(new AxArtifactKey("DateContextAlbum", "0.0.1"));
            assert (dateAlbum1 != null);
            dateAlbum1.setUserArtifactStack(usedArtifactStackArray);

            try {
                dateAlbum1.putAll(valueMap1);
            } catch (final Exception e) {
                assert (e.getMessage().endsWith(
                        "not compatible with class \"org.onap.policy.apex.context.test.concepts.TestContextItem00A\""));
            }
            assert (dateAlbum1.size() == 5);

            valueMap1.clear();
            valueMap1.put("Item00_2", tciA00);
            dateAlbum1.putAll(valueMap1);
            assert (dateAlbum1.size() == 6);

            assert (tciA00.equals(dateAlbum1.get("Item00_2")));
            dateAlbum.remove("Item00_2");
            assert (5 == dateAlbum1.size());

            final ContextAlbum dateAlbumCopy =
                    contextDistributor.createContextAlbum(new AxArtifactKey("DateContextAlbum", "0.0.1"));
            assert (dateAlbumCopy != null);
            dateAlbumCopy.setUserArtifactStack(usedArtifactStackArray);

            final Map<String, Object> valueMap2 = new HashMap<>();
            valueMap2.put("Item04", tciA01);
            valueMap2.put("Item05", tciA02);
            valueMap2.put("Item06", tciA03);

            dateAlbumCopy.putAll(valueMap2);
            assert (8 == dateAlbumCopy.size());
            assert (tciA03.equals(dateAlbumCopy.get("Item06")));

            final Collection<Object> mapValues = dateAlbum.values();
            assert (dateAlbumCopy.values().containsAll(mapValues));

            // Check that clearing works
            dateAlbum1.clear();
            assert (dateAlbum1.size() == 0);

            dateAlbum.put("Item00", tciA00);
            final Map<String, Object> valueMap3 = new HashMap<>();
            valueMap3.put("Item01", tciA01);
            valueMap3.put("Item02", tciA02);
            valueMap3.put("Item03", tciA03);
            dateAlbum.putAll(valueMap3);

            final Map<String, Object> valueMap4 = new HashMap<>();
            valueMap4.put("Item04", tciA01);
            valueMap4.put("Item05", tciA02);
            valueMap4.put("Item06", tciA03);

            dateAlbumCopy.putAll(valueMap4);

            try {
                dateAlbum.containsKey(null);
            } catch (final Exception e) {
                assert (e.getMessage().equals("null values are illegal on method parameter \"key\""));
            }

            for (final Entry<String, Object> entry : dateAlbum.entrySet()) {
                System.out.println(entry.getKey() + "->" + entry.getValue());
            }
            assert (dateAlbum.containsKey("Item05"));
            assert (!dateAlbum.containsKey("Item07"));

            try {
                dateAlbum.containsValue(null);
            } catch (final Exception e) {
                assert (e.getMessage().equals("null values are illegal on method parameter \"value\""));
            }

            assert (dateAlbum.containsValue(tciA01));
            assert (!dateAlbum.containsValue(new String("Hello")));

            final Set<Entry<String, Object>> entrySet = dateAlbum.entrySet();
            assert (entrySet.size() == 7);

            try {
                assert (dateAlbum.get(null) == null);
            } catch (final Exception e) {
                assert (e.getMessage()
                        .equals("album \"DateContextAlbum:0.0.1\" null keys are illegal on keys for get()"));
            }

            final Object aObject = dateAlbum.get("Item03");
            assert (aObject.equals(tciA03));

            try {
                assert (dateAlbum.put(null, null) == null);
            } catch (final Exception e) {
                assert (e.getMessage()
                        .equals("album \"DateContextAlbum:0.0.1\" null keys are illegal on keys for put()"));
            }

            // Put null ContextItem should work (return null)
            try {
                dateAlbum.put("ItemNull", null);
            } catch (final Exception e) {
                assert (e.getMessage().equals(
                        "album \"DateContextAlbum:0.0.1\" null values are illegal on key \"ItemNull\" for put()"));
            }

            // Should return null
            assert (dateAlbum.get("ItemNull") == null);

            // Put should return the previous contextItem
            tciA00.setDST(false);
            final TestContextItem00A tciA03_clone = new TestContextItem00A(tciA03);
            tciA03_clone.setDST(true);
            TestContextItem00A retItem = (TestContextItem00A) dateAlbum.put("Item03", tciA03_clone);
            assert (retItem.equals(tciA03));
            retItem = (TestContextItem00A) dateAlbum.put("Item03", tciA03);
            assert (retItem.equals(tciA03_clone));

            try {
                dateAlbum.put("ItemNull", null);
                assert (dateAlbum.get("ItemNull") == null);
            } catch (final Exception e) {
                assert (e.getMessage().equals(
                        "album \"DateContextAlbum:0.0.1\" null values are illegal on key \"ItemNull\" for put()"));
            }

            dateAlbum.put("TestAA", tciA00);
            assert (dateAlbum.get("TestAA").equals(tciA00));

            // Should print warning
            try {
                dateAlbum.put("TestAA", null);
            } catch (final Exception e) {
                assert (e.getMessage().equals(
                        "album \"DateContextAlbum:0.0.1\" null values are illegal on key \"TestAA\" for put()"));
            }
            assert (dateAlbum.size() == 8);
            try {
                dateAlbum.put("TestAB", null);
            } catch (final Exception e) {
                assert (e.getMessage().equals(
                        "album \"DateContextAlbum:0.0.1\" null values are illegal on key \"TestAB\" for put()"));
            }
            assert (dateAlbum.size() == 8);

            // Should do removes
            dateAlbum.remove("TestAA");
            dateAlbum.remove("TestAB");
            dateAlbum.remove("ItemNull");
            assert (dateAlbum.size() == 7);
            assert (dateAlbumCopy.values().containsAll(mapValues));
            // CHECKSTYLE:ON: checkstyle:magicNumber
        } catch (final Exception e) {
            e.printStackTrace();
            contextDistributor.clear();
            assert ("Test has failed".equals(""));
        }

        try {
            contextDistributor.clear();
        } catch (final Exception e) {
            e.printStackTrace();
            assert ("Test has failed".equals(""));
        }
    }
}
