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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.onap.policy.apex.context.test.factory.TestContextAlbumFactory.createPolicyContextModel;
import static org.onap.policy.apex.context.test.utils.Constants.BYTE_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.EXCEPTION_MESSAGE;
import static org.onap.policy.apex.context.test.utils.Constants.EXTERNAL_CONTEXT;
import static org.onap.policy.apex.context.test.utils.Constants.EXTERNAL_CONTEXT_ALBUM;
import static org.onap.policy.apex.context.test.utils.Constants.FLOAT_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.GLOBAL_CONTEXT_ALBUM;
import static org.onap.policy.apex.context.test.utils.Constants.GLOBAL_CONTEXT_KEY;
import static org.onap.policy.apex.context.test.utils.Constants.INT_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.INT_VAL_2;
import static org.onap.policy.apex.context.test.utils.Constants.INT_VAL_3;
import static org.onap.policy.apex.context.test.utils.Constants.LONG_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.PI_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.POLICY_CONTEXT_ALBUM;
import static org.onap.policy.apex.context.test.utils.Constants.STRING_EXT_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.STRING_GLOBAL_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.STRING_VAL;
import static org.onap.policy.apex.context.test.utils.Constants.TEST_POLICY_CONTEXT_ITEM;
import static org.onap.policy.apex.context.test.utils.Constants.TIME_ZONE;
import static org.onap.policy.apex.context.test.utils.Constants.VERSION;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextBooleanItem;
import org.onap.policy.apex.context.test.concepts.TestContextByteItem;
import org.onap.policy.apex.context.test.concepts.TestContextDateItem;
import org.onap.policy.apex.context.test.concepts.TestContextDateLocaleItem;
import org.onap.policy.apex.context.test.concepts.TestContextDateTzItem;
import org.onap.policy.apex.context.test.concepts.TestContextDoubleItem;
import org.onap.policy.apex.context.test.concepts.TestContextFloatItem;
import org.onap.policy.apex.context.test.concepts.TestContextIntItem;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.concepts.TestContextLongObjectItem;
import org.onap.policy.apex.context.test.concepts.TestContextStringItem;
import org.onap.policy.apex.context.test.concepts.TestContextTreeMapItem;
import org.onap.policy.apex.context.test.concepts.TestContextTreeSetItem;
import org.onap.policy.apex.context.test.concepts.TestExternalContextItem;
import org.onap.policy.apex.context.test.concepts.TestGlobalContextItem;
import org.onap.policy.apex.context.test.concepts.TestPolicyContextItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.context.test.utils.Constants;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestContextInstantiation is used to test Apex context insitiation is correct.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class ContextInstantiation {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextInstantiation.class);

    // Recurring string constants
    private static final String TEST_POLICY_CONTEXT_ITEM000 = "TestPolicyContextItem000";
    private static final String TEST_POLICY_CONTEXT_ITEM005 = "TestPolicyContextItem005";
    private static final String TEST_POLICY_CONTEXT_ITEM004 = "TestPolicyContextItem004";
    private static final String TEST_POLICY_CONTEXT_ITEM003 = "TestPolicyContextItem003";
    private static final String TEST_POLICY_CONTEXT_ITEM002 = "TestPolicyContextItem002";
    private static final String TEST_POLICY_CONTEXT_ITEM001 = "TestPolicyContextItem001";
    private static final String NORMAL_TEST_EXCEPTION = "normal test exception";
    private static final String NULL_VALUES_ILLEGAL_TAG =
                    "album \"ExternalContextAlbum:0.0.1\" null values are illegal on key ";

    private static final TreeSet<String> TEST_TREE_SET = new TreeSet<>();
    private static final Map<String, String> TEST_HASH_MAP = new HashMap<>();

    static {
        TEST_TREE_SET.add("one hundred");
        TEST_TREE_SET.add("one hundred and one");
        TEST_TREE_SET.add("one hundred and two");
        TEST_TREE_SET.add("one hundred and three");
        TEST_TREE_SET.add("one hundred and four");

        TEST_HASH_MAP.put("0", "zero");
        TEST_HASH_MAP.put("1", "one");
        TEST_HASH_MAP.put("2", "two");
        TEST_HASH_MAP.put("3", "three");
        TEST_HASH_MAP.put("4", "four");

    }

    /**
     * Test context instantiation.
     *
     * @throws ContextException the context exception
     */
    public void testContextInstantiation() throws ContextException {
        LOGGER.debug("Running TestContextInstantiation test . . .");

        final Distributor contextDistributor = getDistributor();

        final ContextAlbum policyContextAlbum = contextDistributor
                        .createContextAlbum(new AxArtifactKey(POLICY_CONTEXT_ALBUM, VERSION));

        assertNotNull(policyContextAlbum);
        policyContextAlbum.setUserArtifactStack(Constants.getAxArtifactKeyArray());

        final Date testDate = new Date();

        final TestContextDateTzItem tci9 = getTestContextDateTzItem(testDate);
        final TestContextDateLocaleItem tciA = getTestContextDateLocaleItem(testDate);

        final TestPolicyContextItem policyContext = getTestPolicyContextItem(policyContextAlbum, testDate);

        final Map<String, Object> valueMap0 = new HashMap<>();
        valueMap0.put(TEST_POLICY_CONTEXT_ITEM, policyContext);

        policyContextAlbum.putAll(valueMap0);

        final TestPolicyContextItem contextItem = (TestPolicyContextItem) policyContextAlbum
                        .get(TEST_POLICY_CONTEXT_ITEM);
        assertEquals(STRING_VAL, contextItem.getTestPolicyContextItem000().getStringValue());

        assertEquals(LONG_VAL, contextItem.getTestPolicyContextItem001().getLongValue());
        assertDouble(contextItem.getTestPolicyContextItem002().getDoubleValue(), PI_VAL);
        assertTrue(contextItem.getTestPolicyContextItem003().getFlag());
        assertEquals(contextItem.getTestPolicyContextItem004().getLongValue(), testDate.getTime());
        assertEquals(TEST_HASH_MAP, contextItem.getTestPolicyContextItem005().getMapValue());

        final TestGlobalContextItem globalContext = getTestGlobalContextItem(contextDistributor, testDate, tci9,
                        tciA);

        final Map<String, Object> valueMap1 = new HashMap<>();
        valueMap1.put(GLOBAL_CONTEXT_KEY, globalContext);

        final ContextAlbum globalContextAlbum = getContextAlbum(contextDistributor);

        globalContextAlbum.putAll(valueMap1);

        final TestGlobalContextItem globalContextItem = (TestGlobalContextItem) globalContextAlbum
                        .get(GLOBAL_CONTEXT_KEY);

        assertFalse(globalContextItem.getTestGlobalContextItem000().getFlag());

        assertEquals(BYTE_VAL, globalContextItem.getTestGlobalContextItem001().getByteValue());

        assertEquals(INT_VAL, globalContextItem.getTestGlobalContextItem002().getIntValue());
        assertEquals(LONG_VAL, globalContextItem.getTestGlobalContextItem003().getLongValue());
        assertFloat(FLOAT_VAL, globalContextItem.getTestGlobalContextItem004().getFloatValue());

        assertDouble(PI_VAL, globalContextItem.getTestGlobalContextItem005().getDoubleValue());
        assertEquals(STRING_GLOBAL_VAL, globalContextItem.getTestGlobalContextItem006().getStringValue());

        assertEquals((Long) testDate.getTime(), globalContextItem.getTestGlobalContextItem007().getLongValue());
        assertEquals(testDate, globalContextItem.getTestGlobalContextItem008().getDateValue());
        assertEquals(tci9.getDateValue().getTime(),
                        globalContextItem.getTestGlobalContextItem009().getDateValue().getTime());

        assertEquals(tciA.getDateValue().getTime(),
                        globalContextItem.getTestGlobalContextItem00A().getDateValue().getTime());

        assertEquals(TEST_TREE_SET, globalContextItem.getTestGlobalContextItem00B().getSetValue());
        assertEquals(TEST_HASH_MAP, globalContextItem.getTestGlobalContextItem00C().getMapValue());

        final AxContextModel externalContextModel = TestContextAlbumFactory.createExternalContextModel();

        final TestContextDateTzItem tci9A = new TestContextDateTzItem(tci9);
        final TestContextDateLocaleItem tciAa = new TestContextDateLocaleItem(tciA);
        final TestExternalContextItem externalContext = getTestExternalContextItem(testDate, tci9A, tciAa);

        final Map<String, Object> valueMap2 = new HashMap<>();
        valueMap2.put(EXTERNAL_CONTEXT, externalContext);

        contextDistributor.clear();
        contextDistributor.init(new AxArtifactKey("ClearedandInittedDistributor", VERSION));
        contextDistributor.registerModel(externalContextModel);

        final AxArtifactKey axContextAlbumKey = new AxArtifactKey(EXTERNAL_CONTEXT_ALBUM, VERSION);
        final ContextAlbum externalContextAlbum = contextDistributor.createContextAlbum(axContextAlbumKey);
        assertNotNull(externalContextAlbum);
        externalContextAlbum.setUserArtifactStack(Constants.getAxArtifactKeyArray());

        externalContextAlbum.putAll(valueMap2);
        externalContextAlbum.getAlbumDefinition().setWritable(false);

        TestExternalContextItem externalContextItem = (TestExternalContextItem) externalContextAlbum
                        .get(EXTERNAL_CONTEXT);

        assertFalse(externalContextItem.getTestExternalContextItem000().getFlag());
        assertEquals(BYTE_VAL, externalContextItem.getTestExternalContextItem001().getByteValue());
        assertEquals(INT_VAL, externalContextItem.getTestExternalContextItem002().getIntValue());

        assertFloat(LONG_VAL, externalContextItem.getTestExternalContextItem003().getLongValue());
        assertFloat(FLOAT_VAL, externalContextItem.getTestExternalContextItem004().getFloatValue());

        assertDouble(PI_VAL, externalContextItem.getTestExternalContextItem005().getDoubleValue());
        assertEquals(STRING_EXT_VAL, externalContextItem.getTestExternalContextItem006().getStringValue());
        assertEquals((Long) testDate.getTime(), externalContextItem.getTestExternalContextItem007().getLongValue());
        assertEquals(testDate, externalContextItem.getTestExternalContextItem008().getDateValue());
        assertEquals(tci9A.getDateValue().getTime(),
                        externalContextItem.getTestExternalContextItem009().getDateValue().getTime());

        assertEquals(tciAa.getDateValue().getTime(),
                        externalContextItem.getTestExternalContextItem00A().getDateValue().getTime());
        assertEquals(TEST_TREE_SET, externalContextItem.getTestExternalContextItem00B().getSetValue());
        assertEquals(TEST_HASH_MAP, externalContextItem.getTestExternalContextItem00C().getMapValue());

        final Collection<Object> mapValues = externalContextAlbum.values();
        assertTrue(externalContextAlbum.values().containsAll(mapValues));

        // Check that clearing does not work
        try {
            externalContextAlbum.clear();
            fail(EXCEPTION_MESSAGE);
        } catch (final ContextRuntimeException e) {
            assertEquals("album \"ExternalContextAlbum:0.0.1\" clear() not allowed on read only albums",
                            e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        assertEquals(1, externalContextAlbum.size());

        assertContextAlbumContains(externalContext, externalContextAlbum);

        final Set<Entry<String, Object>> entrySet = externalContextAlbum.entrySet();
        assertEquals(1, entrySet.size());

        try {
            externalContextAlbum.get(null);
        } catch (final ContextRuntimeException e) {
            assertEquals("album \"ExternalContextAlbum:0.0.1\" null keys are illegal on keys for get()",
                            e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        final Object aObject = externalContextAlbum.get(EXTERNAL_CONTEXT);
        assertEquals(aObject, externalContext);

        // put null keys should fail, throws a runtime exception
        try {
            externalContextAlbum.put(null, null);
        } catch (final ContextRuntimeException e) {
            assertEquals("album \"ExternalContextAlbum:0.0.1\" null keys are illegal on keys for put()",
                            e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        try {
            externalContextAlbum.put("TestExternalContextItem00A", null);
        } catch (final ContextRuntimeException e) {
            assertEquals(NULL_VALUES_ILLEGAL_TAG + "\"TestExternalContextItem00A\" for put()", e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }
        assertEquals(tciAa, externalContextItem.getTestExternalContextItem00A());

        // Should return the hash set
        assertEquals(TEST_TREE_SET, externalContextItem.getTestExternalContextItem00B().getSetValue());

        assertTrue(externalContextAlbum.values().containsAll(mapValues));

        // Set the write flag back as it should be
        externalContextAlbum.getAlbumDefinition().setWritable(true);

        // Put should return the previous contextItem
        final TestExternalContextItem externalContextOther = new TestExternalContextItem();
        externalContextOther.setTestExternalContextItem002(new TestContextIntItem());
        externalContextOther.getTestExternalContextItem002().setIntValue(INT_VAL_2);

        assertTrue(externalContextAlbum.put(EXTERNAL_CONTEXT, externalContextOther).equals(externalContext));
        externalContextItem = (TestExternalContextItem) externalContextAlbum.get(EXTERNAL_CONTEXT);
        assertEquals(INT_VAL_2, externalContextItem.getTestExternalContextItem002().getIntValue());
        assertTrue(externalContextAlbum.put(EXTERNAL_CONTEXT, externalContext).equals(externalContextOther));
        externalContextItem = (TestExternalContextItem) externalContextAlbum.get(EXTERNAL_CONTEXT);
        assertEquals(INT_VAL_3, externalContextItem.getTestExternalContextItem002().getIntValue());

        try {
            externalContextAlbum.put("TestExternalContextItem00A", null);
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage().equals(NULL_VALUES_ILLEGAL_TAG + "\"TestExternalContextItem00A\" for put()"));
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }
        assertTrue(externalContextAlbum.get(EXTERNAL_CONTEXT).equals(externalContext));

        try {
            externalContextAlbum.put("TestExternalContextItemFFF", null);
        } catch (final ContextRuntimeException e) {
            assert (e.getMessage().equals(NULL_VALUES_ILLEGAL_TAG + "\"TestExternalContextItemFFF\" for put()"));
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }
        assertEquals(1, externalContextAlbum.size());

        try {
            externalContextAlbum.put("TestExternalContextItemFFF", null);
        } catch (final ContextRuntimeException e) {
            assertEquals(NULL_VALUES_ILLEGAL_TAG + "\"TestExternalContextItemFFF\" for put()", e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }
        assertEquals(1, externalContextAlbum.size());

        // Should ignore remove
        externalContextAlbum.remove("TestExternalContextItem017");
        assertEquals(1, externalContextAlbum.size());
        assertEquals(1, externalContextAlbum.values().size());
        assertTrue(externalContextAlbum.values().containsAll(mapValues));

        contextDistributor.clear();
    }

    private void assertContextAlbumContains(final TestExternalContextItem externalContext,
                    final ContextAlbum externalContextAlbum) {
        try {
            externalContextAlbum.containsKey(null);
        } catch (final ContextRuntimeException e) {
            assertEquals("null values are illegal on method parameter \"key\"", e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        assertTrue(externalContextAlbum.containsKey(EXTERNAL_CONTEXT));
        assertTrue(!externalContextAlbum.containsKey(GLOBAL_CONTEXT_KEY));

        try {
            externalContextAlbum.containsValue(null);
        } catch (final ContextRuntimeException e) {
            assertEquals("null values are illegal on method parameter \"value\"", e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        assertTrue(externalContextAlbum.containsValue(externalContext));
        assertFalse(externalContextAlbum.containsValue("Hello"));
    }

    private ContextAlbum getContextAlbum(final Distributor contextDistributor) throws ContextException {
        final ContextAlbum globalContextAlbum = contextDistributor
                        .createContextAlbum(new AxArtifactKey(GLOBAL_CONTEXT_ALBUM, VERSION));
        assertNotNull(globalContextAlbum);
        globalContextAlbum.setUserArtifactStack(Constants.getAxArtifactKeyArray());
        return globalContextAlbum;
    }

    private TestGlobalContextItem getTestGlobalContextItem(final Distributor contextDistributor, final Date testDate,
                    final TestContextDateTzItem tci9, final TestContextDateLocaleItem tciA) throws ContextException {
        final AxContextModel globalContextModel = TestContextAlbumFactory.createGlobalContextModel();
        final TestGlobalContextItem globalContext = getTestGlobalContextItem(testDate, tci9, tciA);
        contextDistributor.registerModel(globalContextModel);
        return globalContext;
    }

    private TestGlobalContextItem getTestGlobalContextItem(final Date testDate, final TestContextDateTzItem tci9,
                    final TestContextDateLocaleItem tciA) {
        final TestGlobalContextItem globalContext = new TestGlobalContextItem();

        final TestContextBooleanItem testGlobalContextItem000 = new TestContextBooleanItem(false);
        final TestContextByteItem testGlobalContextItem001 = new TestContextByteItem(BYTE_VAL);
        final TestContextIntItem testGlobalContextItem002 = new TestContextIntItem(INT_VAL);
        final TestContextLongItem testGlobalContextItem003 = new TestContextLongItem(LONG_VAL);
        final TestContextFloatItem testGlobalContextItem004 = new TestContextFloatItem(FLOAT_VAL);
        final TestContextDoubleItem testGlobalContextItem005 = new TestContextDoubleItem(PI_VAL);
        final TestContextStringItem testGlobalContextItem006 = new TestContextStringItem(STRING_GLOBAL_VAL);
        final TestContextLongObjectItem testGlobalContextItem007 = new TestContextLongObjectItem(testDate.getTime());

        final TestContextDateItem testGlobalContextItem008 = new TestContextDateItem(testDate);
        final TestContextTreeSetItem testGlobalContextItem00B = new TestContextTreeSetItem(TEST_TREE_SET);
        final TestContextTreeMapItem testGlobalContextItem00C = new TestContextTreeMapItem(TEST_HASH_MAP);

        globalContext.setTestGlobalContextItem000(testGlobalContextItem000);
        globalContext.setTestGlobalContextItem001(testGlobalContextItem001);
        globalContext.setTestGlobalContextItem002(testGlobalContextItem002);
        globalContext.setTestGlobalContextItem003(testGlobalContextItem003);
        globalContext.setTestGlobalContextItem004(testGlobalContextItem004);
        globalContext.setTestGlobalContextItem005(testGlobalContextItem005);
        globalContext.setTestGlobalContextItem006(testGlobalContextItem006);
        globalContext.setTestGlobalContextItem007(testGlobalContextItem007);
        globalContext.setTestGlobalContextItem008(testGlobalContextItem008);
        globalContext.setTestGlobalContextItem009(tci9);
        globalContext.setTestGlobalContextItem00A(tciA);
        globalContext.setTestGlobalContextItem00B(testGlobalContextItem00B);
        globalContext.setTestGlobalContextItem00C(testGlobalContextItem00C);
        return globalContext;
    }

    private TestPolicyContextItem getTestPolicyContextItem(final ContextAlbum policyContextAlbum, final Date testDate) {
        final TestContextStringItem contextStringItem = new TestContextStringItem(STRING_VAL);
        final TestContextLongItem contextLongItem = new TestContextLongItem(LONG_VAL);
        final TestContextDoubleItem contextDoubleItem = new TestContextDoubleItem(PI_VAL);
        final TestContextBooleanItem contextBooleanItem = new TestContextBooleanItem(true);
        final TestContextLongItem contextLongItem2 = new TestContextLongItem(testDate.getTime());
        final TestContextTreeMapItem contextTreeMapItem = new TestContextTreeMapItem(TEST_HASH_MAP);

        final Map<String, Object> valueMapA = new LinkedHashMap<>();
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM001, contextLongItem);
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM002, contextDoubleItem);
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM003, contextBooleanItem);
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM004, contextLongItem2);
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM005, contextTreeMapItem);
        valueMapA.put(TEST_POLICY_CONTEXT_ITEM000, contextStringItem);

        assertPutMethods(policyContextAlbum, contextStringItem, valueMapA);

        final TestPolicyContextItem policyContext = new TestPolicyContextItem();

        LOGGER.debug(policyContextAlbum.toString());

        policyContext.setTestPolicyContextItem000(contextStringItem);
        policyContext.setTestPolicyContextItem001(contextLongItem);
        policyContext.setTestPolicyContextItem002(contextDoubleItem);
        policyContext.setTestPolicyContextItem003(contextBooleanItem);
        policyContext.setTestPolicyContextItem004(contextLongItem2);
        policyContext.setTestPolicyContextItem005(contextTreeMapItem);
        return policyContext;
    }

    private void assertPutMethods(final ContextAlbum policyContextAlbum, final TestContextStringItem contextStringItem,
                    final Map<String, Object> valueMapA) {
        try {
            policyContextAlbum.put(TEST_POLICY_CONTEXT_ITEM000, contextStringItem);
            fail(EXCEPTION_MESSAGE);
        } catch (final ContextRuntimeException e) {
            assertEquals(getMessage(TEST_POLICY_CONTEXT_ITEM000, "TestContextItem006",
                            TestContextStringItem.class.getCanonicalName(), "stringValue=" + STRING_VAL),
                            e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }

        try {
            policyContextAlbum.putAll(valueMapA);
            fail(EXCEPTION_MESSAGE);
        } catch (final ContextRuntimeException e) {
            assertEquals(getMessage(TEST_POLICY_CONTEXT_ITEM001, "TestContextItem003",
                            TestContextLongItem.class.getCanonicalName(), "longValue=" + INT_VAL_3), e.getMessage());
            LOGGER.trace(NORMAL_TEST_EXCEPTION, e);
        }
    }

    private AxContextModel getAxContextModel() {
        final AxContextModel policyContextModel = createPolicyContextModel();
        final AxValidationResult result = new AxValidationResult();
        policyContextModel.validate(result);
        LOGGER.debug(result.toString());

        assertTrue(result.isValid());
        return policyContextModel;
    }

    private TestContextDateLocaleItem getTestContextDateLocaleItem(final Date testDate) {
        final TestContextDateLocaleItem tciA = new TestContextDateLocaleItem();
        tciA.setDateValue(new TestContextDateItem(testDate));
        tciA.setTzValue(TIME_ZONE.getDisplayName());
        tciA.setDst(true);
        tciA.setUtcOffset(-600);
        tciA.setLocale(Locale.ENGLISH);
        return tciA;
    }

    private TestContextDateTzItem getTestContextDateTzItem(final Date testDate) {
        final TestContextDateTzItem tci9 = new TestContextDateTzItem();
        tci9.setDateValue(new TestContextDateItem(testDate));
        tci9.setTzValue(TIME_ZONE.getDisplayName());
        tci9.setDst(true);
        return tci9;
    }

    private TestExternalContextItem getTestExternalContextItem(final Date testDate, final TestContextDateTzItem tci9A,
                    final TestContextDateLocaleItem tciAa) {
        final TestExternalContextItem externalContext = new TestExternalContextItem();

        final TestContextBooleanItem testExternalContextItem000 = new TestContextBooleanItem(false);
        final TestContextByteItem testExternalContextItem001 = new TestContextByteItem(BYTE_VAL);
        final TestContextIntItem testExternalContextItem002 = new TestContextIntItem(INT_VAL);
        final TestContextLongItem testExternalContextItem003 = new TestContextLongItem(LONG_VAL);
        final TestContextFloatItem testExternalContextItem004 = new TestContextFloatItem(new Float(3.14159265359));
        final TestContextDoubleItem testExternalContextItem005 = new TestContextDoubleItem(PI_VAL);
        final TestContextStringItem testExternalContextItem006 = new TestContextStringItem(STRING_EXT_VAL);
        final TestContextLongObjectItem testExternalContextItem007 = new TestContextLongObjectItem(testDate.getTime());
        final TestContextDateItem testExternalContextItem008 = new TestContextDateItem(testDate);
        final TestContextTreeSetItem testExternalContextItem00B = new TestContextTreeSetItem(TEST_TREE_SET);
        final TestContextTreeMapItem testExternalContextItem00C = new TestContextTreeMapItem(TEST_HASH_MAP);

        externalContext.setTestExternalContextItem000(testExternalContextItem000);
        externalContext.setTestExternalContextItem001(testExternalContextItem001);
        externalContext.setTestExternalContextItem002(testExternalContextItem002);
        externalContext.setTestExternalContextItem003(testExternalContextItem003);
        externalContext.setTestExternalContextItem004(testExternalContextItem004);
        externalContext.setTestExternalContextItem005(testExternalContextItem005);
        externalContext.setTestExternalContextItem006(testExternalContextItem006);
        externalContext.setTestExternalContextItem007(testExternalContextItem007);
        externalContext.setTestExternalContextItem008(testExternalContextItem008);
        externalContext.setTestExternalContextItem009(tci9A);
        externalContext.setTestExternalContextItem00A(tciAa);
        externalContext.setTestExternalContextItem00B(testExternalContextItem00B);
        externalContext.setTestExternalContextItem00C(testExternalContextItem00C);
        return externalContext;
    }

    private String getMessage(final String key, final String objName, final String clazzName, final String valString) {
        return getMessage(key, objName, clazzName, valString, TestPolicyContextItem.class.getCanonicalName());
    }

    private String getMessage(final String key, final String objName, final String clazzName, final String valString,
                    final String compatibleClazzName) {
        return "Failed to set context value for key \"" + key + "\" in album \"PolicyContextAlbum:0.0.1\": "
                        + "PolicyContextAlbum:0.0.1: object \"" + objName + " [" + valString + "]\" " + "of class \""
                        + clazzName + "\"" + " not compatible with class \"" + compatibleClazzName + "\"";
    }

    private void assertFloat(final float actual, final float expected) {
        assertTrue(Float.compare(actual, expected) == 0);
    }

    private void assertDouble(final double actual, final double expected) {
        assertTrue(Double.compare(actual, expected) == 0);
    }

    private Distributor getDistributor() throws ContextException {
        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributorInit", VERSION);
        final Distributor distributor = new DistributorFactory().getDistributor(distributorKey);
        final AxContextModel policyContextModel = getAxContextModel();
        distributor.registerModel(policyContextModel);
        return distributor;
    }
}
