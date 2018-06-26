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

package org.onap.policy.apex.common.test.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.onap.policy.apex.common.test.distribution.Constants.APEX_DISTRIBUTOR;
import static org.onap.policy.apex.common.test.distribution.Constants.BYTE_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.DATE_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.EXCEPTION_MESSAGE;
import static org.onap.policy.apex.common.test.distribution.Constants.FLOAT_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.INT_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.LONG_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.PI_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.STRING_GLOBAL_VAL;
import static org.onap.policy.apex.common.test.distribution.Constants.TIME_ZONE;
import static org.onap.policy.apex.common.test.distribution.Constants.USED_ARTIFACT_STACK_ARRAY;
import static org.onap.policy.apex.common.test.distribution.Constants.VERSION;
import static org.onap.policy.apex.common.test.factory.TestContextAlbumFactory.createMultiAlbumsContextModel;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.onap.policy.apex.common.test.concepts.TestContextBooleanItem;
import org.onap.policy.apex.common.test.concepts.TestContextByteItem;
import org.onap.policy.apex.common.test.concepts.TestContextDateItem;
import org.onap.policy.apex.common.test.concepts.TestContextDateLocaleItem;
import org.onap.policy.apex.common.test.concepts.TestContextDateTzItem;
import org.onap.policy.apex.common.test.concepts.TestContextDoubleItem;
import org.onap.policy.apex.common.test.concepts.TestContextFloatItem;
import org.onap.policy.apex.common.test.concepts.TestContextIntItem;
import org.onap.policy.apex.common.test.concepts.TestContextLongItem;
import org.onap.policy.apex.common.test.concepts.TestContextLongObjectItem;
import org.onap.policy.apex.common.test.concepts.TestContextStringItem;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;



/**
 * The Class SequentialContextInstantiation checks sequential initiation of context.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class SequentialContextInstantiation {
    private static final String DV1 = "dv1";
    private static final String DV0 = "dv0";
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(SequentialContextInstantiation.class);

    /**
     * Test sequential context instantiation.
     *
     * @throws ContextException the context exception
     */
    public void testSequentialContextInstantiation() throws ContextException {
        LOGGER.debug("Running TestContextInstantiation test . . .");

        final Distributor contextDistributor = getDistributor();

        try {
            final ContextAlbum dateAlbum = getContextAlbum(DATE_CONTEXT_ALBUM, contextDistributor);
            final Date testDate = new Date();
            final TestContextDateLocaleItem tciA00 = getTestContextDateLocaleItem();

            dateAlbum.put(DV0, tciA00);
            assertEquals(tciA00, dateAlbum.get(DV0));

            dateAlbum.put(DV1, tciA00);
            assertEquals(tciA00, dateAlbum.get(DV1));

            final TestContextDateTzItem tci9 = getTestContextDateTzItem(testDate);

            try {
                dateAlbum.put("tci9", tci9);
            } catch (final Exception e) {
                final String message = "class \"" + TestContextDateTzItem.class.getCanonicalName()
                        + "\" not compatible with class \"" + TestContextDateLocaleItem.class.getCanonicalName() + "\"";
                assertTrue(e.getMessage().contains(message));
            }

            final TestContextDateLocaleItem tciA01 = new TestContextDateLocaleItem(tciA00);
            final TestContextDateLocaleItem tciA02 = new TestContextDateLocaleItem(tciA00);
            final TestContextDateLocaleItem tciA03 = new TestContextDateLocaleItem(tciA00);

            final Map<String, Object> valueMap0 = new HashMap<>();
            valueMap0.put("Item01", tciA01);
            valueMap0.put("Item02", tciA02);
            valueMap0.put("Item03", tciA03);

            dateAlbum.putAll(valueMap0);
            assertEquals(5, dateAlbum.size());
            assertEquals(tciA01, dateAlbum.get("Item01"));
            assertEquals(tciA02, dateAlbum.get("Item02"));
            assertEquals(tciA03, dateAlbum.get("Item03"));

            final Map<String, Object> valueMap1 = getMap(testDate, tciA00, tci9);

            // Get another reference to the album
            final ContextAlbum dateAlbum1 = getContextAlbum(DATE_CONTEXT_ALBUM, contextDistributor);

            try {
                dateAlbum1.putAll(valueMap1);
                fail(EXCEPTION_MESSAGE);
            } catch (final Exception e) {
                assertTrue(e.getMessage().endsWith(
                        "not compatible with class \"" + TestContextDateLocaleItem.class.getCanonicalName() + "\""));
            }
            assertEquals(5, dateAlbum1.size());

            valueMap1.clear();
            valueMap1.put("Item00_2", tciA00);
            dateAlbum1.putAll(valueMap1);
            assertEquals(6, dateAlbum1.size());

            assertEquals(tciA00, dateAlbum1.get("Item00_2"));
            dateAlbum.remove("Item00_2");
            assertEquals(5, dateAlbum1.size());

            final ContextAlbum dateAlbumCopy = getContextAlbum(DATE_CONTEXT_ALBUM, contextDistributor);

            final Map<String, Object> valueMap2 = new HashMap<>();
            valueMap2.put("Item04", tciA01);
            valueMap2.put("Item05", tciA02);
            valueMap2.put("Item06", tciA03);

            dateAlbumCopy.putAll(valueMap2);
            assertEquals(8, dateAlbumCopy.size());

            assertEquals(tciA03, dateAlbumCopy.get("Item06"));

            final Collection<Object> mapValues = dateAlbum.values();
            assertTrue(dateAlbumCopy.values().containsAll(mapValues));

            // Check that clearing works
            dateAlbum1.clear();
            assertTrue(dateAlbum1.isEmpty());


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

            assertContains(dateAlbum, tciA01);

            final Set<Entry<String, Object>> entrySet = dateAlbum.entrySet();
            assertEquals(7, entrySet.size());

            assertAlbumGetAndPutMethods(dateAlbum, tciA03, tciA00);

            // Should do removes
            dateAlbum.remove("TestAA");
            dateAlbum.remove("TestAB");
            dateAlbum.remove("ItemNull");
            assertEquals(7, entrySet.size());
            assertTrue(dateAlbumCopy.values().containsAll(mapValues));
            // CHECKSTYLE:ON: checkstyle:magicNumber
        } finally {
            contextDistributor.clear();
        }
    }

    private void assertContains(final ContextAlbum dateAlbum, final TestContextDateLocaleItem tciA01) {
        try {
            dateAlbum.containsKey(null);
            fail(EXCEPTION_MESSAGE);
        } catch (final Exception e) {
            assertEquals("null values are illegal on method parameter \"key\"", e.getMessage());
        }

        assertTrue(dateAlbum.containsKey("Item05"));
        assertTrue(!dateAlbum.containsKey("Item07"));

        try {
            dateAlbum.containsValue(null);
        } catch (final Exception e) {
            assertEquals("null values are illegal on method parameter \"value\"", e.getMessage());

        }

        assertTrue(dateAlbum.containsValue(tciA01));
        assertTrue(!dateAlbum.containsValue("Hello"));
    }

    private void assertAlbumGetAndPutMethods(final ContextAlbum dateAlbum, final TestContextDateLocaleItem tciA03,
            final TestContextDateLocaleItem tciA00) {
        try {
            dateAlbum.get(null);
            fail(EXCEPTION_MESSAGE);
        } catch (final Exception e) {
            assertEquals("album \"DateContextAlbum:0.0.1\" null keys are illegal on keys for get()", e.getMessage());
        }

        final Object aObject = dateAlbum.get("Item03");
        assertEquals(tciA03, aObject);
        try {
            dateAlbum.put(null, null);
            fail(EXCEPTION_MESSAGE);
        } catch (final Exception e) {
            assertEquals("album \"DateContextAlbum:0.0.1\" null keys are illegal on keys for put()", e.getMessage());
        }

        // Put null ContextItem should work (return null)
        try {
            dateAlbum.put("ItemNull", null);
        } catch (final Exception e) {
            assertEquals("album \"DateContextAlbum:0.0.1\" null values are illegal on key \"ItemNull\" for put()",
                    e.getMessage());
        }

        // Should return null
        assertNull(dateAlbum.get("ItemNull"));
        // Put should return the previous contextItem
        tciA00.setDST(false);
        final TestContextDateLocaleItem tciA03_clone = new TestContextDateLocaleItem(tciA03);
        tciA03_clone.setDST(true);
        TestContextDateLocaleItem retItem = (TestContextDateLocaleItem) dateAlbum.put("Item03", tciA03_clone);
        assertEquals(tciA03, retItem);
        retItem = (TestContextDateLocaleItem) dateAlbum.put("Item03", tciA03);
        assertEquals(tciA03_clone, retItem);

        try {
            dateAlbum.put("ItemNull", null);
            fail(EXCEPTION_MESSAGE);

        } catch (final Exception e) {
            assert (e.getMessage()
                    .equals("album \"DateContextAlbum:0.0.1\" null values are illegal on key \"ItemNull\" for put()"));
        }

        dateAlbum.put("TestAA", tciA00);
        assertEquals(tciA00, dateAlbum.get("TestAA"));

        // Should print warning
        try {
            dateAlbum.put("TestAA", null);
            fail(EXCEPTION_MESSAGE);
        } catch (final Exception e) {
            assertEquals("album \"DateContextAlbum:0.0.1\" null values are illegal on key \"TestAA\" for put()",
                    e.getMessage());
        }
        assertEquals(8, dateAlbum.size());
        try {
            dateAlbum.put("TestAB", null);
            fail(EXCEPTION_MESSAGE);
        } catch (final Exception e) {
            assertEquals("album \"DateContextAlbum:0.0.1\" null values are illegal on key \"TestAB\" for put()",
                    e.getMessage());
        }
        assertEquals(8, dateAlbum.size());
    }

    private Map<String, Object> getMap(final Date testDate, final TestContextDateLocaleItem tciA00,
            final TestContextDateTzItem tci9) {
        final TestContextBooleanItem testBadItem000 = new TestContextBooleanItem();
        final TestContextByteItem testBadItem001 = new TestContextByteItem();
        final TestContextIntItem testBadItem002 = new TestContextIntItem();
        final TestContextLongItem testBadItem003 = new TestContextLongItem();
        final TestContextFloatItem testBadItem004 = new TestContextFloatItem();
        final TestContextDoubleItem testBadItem005 = new TestContextDoubleItem();
        final TestContextStringItem testBadItem006 = new TestContextStringItem();
        final TestContextLongObjectItem testBadItem007 = new TestContextLongObjectItem();
        final TestContextDateItem testBadItem008 = new TestContextDateItem();

        testBadItem000.setFlag(false);
        testBadItem001.setByteValue(BYTE_VAL);
        testBadItem002.setIntValue(INT_VAL);
        testBadItem003.setLongValue(LONG_VAL);
        testBadItem004.setFloatValue(new Float(FLOAT_VAL));
        testBadItem005.setDoubleValue(PI_VAL);
        testBadItem006.setStringValue(STRING_GLOBAL_VAL);
        testBadItem007.setLongValue(testDate.getTime());
        testBadItem008.setDateValue(testDate);

        final Map<String, Object> values = new HashMap<>();
        values.put("TestBadItem000", testBadItem000);
        values.put("TestBadItem001", testBadItem001);
        values.put("TestBadItem002", testBadItem002);
        values.put("TestBadItem003", testBadItem003);
        values.put("TestBadItem004", testBadItem004);
        values.put("TestBadItem005", testBadItem005);
        values.put("TestBadItem006", testBadItem006);
        values.put("TestBadItem007", testBadItem007);
        values.put("TestBadItem008", testBadItem008);
        values.put("TestBadItem009", tci9);
        values.put("Item00_2", tciA00);
        return values;
    }

    private TestContextDateTzItem getTestContextDateTzItem(final Date testDate) {
        final TestContextDateTzItem tci9 = new TestContextDateTzItem();
        tci9.setDateValue(new TestContextDateItem(testDate));
        tci9.setTZValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
        tci9.setDST(true);
        return tci9;
    }

    private TestContextDateLocaleItem getTestContextDateLocaleItem() {
        final TestContextDateLocaleItem tciA00 = new TestContextDateLocaleItem();
        tciA00.setDateValue(new TestContextDateItem(new Date()));
        tciA00.setTZValue(TIME_ZONE.getDisplayName());
        tciA00.setDST(true);
        tciA00.setUTCOffset(-600);
        tciA00.setLocale(Locale.ENGLISH);
        return tciA00;
    }

    private ContextAlbum getContextAlbum(final String albumName, final Distributor contextDistributor)
            throws ContextException {
        final ContextAlbum dateAlbum = contextDistributor.createContextAlbum(new AxArtifactKey(albumName, VERSION));
        assertNotNull(dateAlbum);
        dateAlbum.setUserArtifactStack(USED_ARTIFACT_STACK_ARRAY);
        return dateAlbum;
    }

    private Distributor getDistributor() throws ContextException {
        final AxArtifactKey distributorKey = new AxArtifactKey(APEX_DISTRIBUTOR, VERSION);
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);
        contextDistributor.registerModel(createMultiAlbumsContextModel());
        return contextDistributor;
    }
}
