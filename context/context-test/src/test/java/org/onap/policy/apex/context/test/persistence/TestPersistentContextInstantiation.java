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

package org.onap.policy.apex.context.test.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;

/**
 * The Class TestContextInstantiation.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class TestPersistentContextInstantiation {
    // Logger for this class
    // private static final XLogger logger = XLoggerFactory.getXLogger(TestPersistentContextInstantiation.class);

    private Connection connection;

    @Before
    public void setup() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");
    }

    @After
    public void teardown() throws Exception {
        connection.close();
        new File("derby.log").delete();
    }

    @After
    public void afterTest() throws IOException {}

    @Test
    @Ignore
    public void testContextPersistentInstantiation() throws ApexModelException, IOException, ApexException {
        /*
         * ContextParameters contextParameters = new ContextParameters(); contextParameters.setPersistencePluginClass(
         * "org.onap.policy.apex.context.impl.persistence.ephemeral.EphemeralPersistor");
         *
         * AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1"); ContextDistributor
         * contextDistributor = new ContextDistributorFactory().getDistributor(distributorKey, contextParameters);
         *
         * AxArtifactKey[] usedArtifactStackArray = { new AxArtifactKey("testC-top", "0.0.1"), new
         * AxArtifactKey("testC-next", "0.0.1"), new AxArtifactKey("testC-bot", "0.0.1") };
         *
         * AxContextModel someContextModel = TestContextMapFactory.createLargeContextModel(); AxContextMap
         * someContextMap = someContextModel.getContext().getContextMaps().get(new AxArtifactKey("LargeContextMap",
         * "0.0.1"));
         *
         * ApexDao apexDao = new ApexDaoFactory().createApexDao(contextParameters); apexDao.init(distributorProperties);
         *
         * apexDao.create(someContextMap);
         *
         * contextDistributor.registerModel(someContextModel); ContextMap someContextMap0 =
         * contextDistributor.createContextMap(someContextMap.getKey()); assertNotNull(someContextMap0);
         * someContextMap0.setUserArtifactStack(usedArtifactStackArray);
         *
         *
         * final Float testFloat = new Float(3.14159265359); final Date testDate = new Date();
         *
         * TestContextItem009 tciG9 = new
         * TestContextItem009((AxContextItem)someContextMap0.get("TestGlobalContextItem009"));
         * tciG9.setDateValue(testDate); tciG9.setTZValue(TimeZone.getTimeZone("Europe/Dublin")); tciG9.setDST(true);
         *
         * TestContextItem009 tciE9 = new
         * TestContextItem009((AxContextItem)someContextMap0.get("TestExternalContextItem009"));
         * tciE9.setDateValue(testDate); tciE9.setTZValue(TimeZone.getTimeZone("Europe/Dublin")); tciE9.setDST(true);
         *
         * TestContextItem00A tciGA = new
         * TestContextItem00A((AxContextItem)someContextMap0.get("TestGlobalContextItem00A"));
         * tciGA.setDateValue(testDate); tciGA.setTZValue(TimeZone.getTimeZone("Europe/Dublin")); tciGA.setDST(true);
         * tciGA.setUTCOffset(-600); tciGA.setLocale(Locale.ENGLISH);
         *
         * TestContextItem00A tciEA = new
         * TestContextItem00A((AxContextItem)someContextMap0.get("TestExternalContextItem00A"));
         * tciEA.setDateValue(testDate); tciEA.setTZValue(TimeZone.getTimeZone("Europe/Dublin")); tciEA.setDST(true);
         * tciEA.setUTCOffset(-600); tciEA.setLocale(Locale.ENGLISH);
         *
         * final Set<String> testHashSet = new HashSet<String>(); testHashSet.add("one hundred");
         * testHashSet.add("one hundred and one"); testHashSet.add("one hundred and two");
         * testHashSet.add("one hundred and three"); testHashSet.add("one hundred and four");
         *
         * final Map<String, Object> testHashMap = new HashMap<String, Object>(); testHashMap.put("0", "zero");
         * testHashMap.put("1", "one"); testHashMap.put("2", "two"); testHashMap.put("3", "three"); testHashMap.put("4",
         * "four");
         *
         * final Map<String, Object> valueMap0 = new HashMap<String, Object>();
         * valueMap0.put("TestPolicyContextItem000", "This is a policy context string");
         * valueMap0.put("TestPolicyContextItem001", 0xFFFFFFFFFFFFFFFFL); valueMap0.put("TestPolicyContextItem002",
         * Math.PI); valueMap0.put("TestPolicyContextItem003", true); valueMap0.put("TestPolicyContextItem004",
         * testDate.getTime()); valueMap0.put("TestPolicyContextItem005", testHashMap);
         *
         * someContextMap0.putAll(valueMap0);
         *
         * assertEquals(((TestContextItem006)someContextMap0.get("TestPolicyContextItem000")).getStringValue(),
         * "This is a policy context string");
         * assertEquals(((TestContextItem003)someContextMap0.get("TestPolicyContextItem001")).getLongValue(),
         * 0xFFFFFFFFFFFFFFFFL);
         * assertTrue(((TestContextItem005)someContextMap0.get("TestPolicyContextItem002")).getDoubleValue() ==
         * Math.PI); assertEquals(((TestContextItem000)someContextMap0.get("TestPolicyContextItem003")).getFlag(),
         * true); assertTrue(((TestContextItem003)someContextMap0.get("TestPolicyContextItem004")).getLongValue() ==
         * testDate.getTime());
         * assertEquals(((TestContextItem00C)someContextMap0.get("TestPolicyContextItem005")).getMapValue(),
         * testHashMap);
         *
         * someContextMap0.flush();
         *
         * final Map<String, Object> valueMap1 = new HashMap<String, Object>();
         * valueMap1.put("TestGlobalContextItem000", false); valueMap1.put("TestGlobalContextItem001", (byte) 0xFF);
         * valueMap1.put("TestGlobalContextItem002", 0xFFFFFFFF); valueMap1.put("TestGlobalContextItem003",
         * 0xFFFFFFFFFFFFFFFFL); valueMap1.put("TestGlobalContextItem004", testFloat);
         * valueMap1.put("TestGlobalContextItem005", Math.PI); valueMap1.put("TestGlobalContextItem006",
         * "This is a global context string"); valueMap1.put("TestGlobalContextItem007", testDate.getTime());
         * valueMap1.put("TestGlobalContextItem008", testDate); valueMap1.put("TestGlobalContextItem009", tciG9);
         * valueMap1.put("TestGlobalContextItem00A", tciGA); valueMap1.put("TestGlobalContextItem00B", testHashSet);
         * valueMap1.put("TestGlobalContextItem00C", testHashMap);
         *
         * ContextMap someContextMap1 = contextDistributor.createContextMap(someContextMap.getKey());
         * assertNotNull(someContextMap1); someContextMap1.setUserArtifactStack(usedArtifactStackArray);
         *
         * someContextMap1.putAll(valueMap1); logger.debug(someContextMap1.toString());
         *
         * assertEquals(((TestContextItem000)someContextMap1.get("TestGlobalContextItem000")).getFlag(), false);
         * assertEquals(((TestContextItem001)someContextMap1.get("TestGlobalContextItem001")).getByteValue(), (byte)
         * 0xFF); assertEquals(((TestContextItem002)someContextMap1.get("TestGlobalContextItem002")).getIntValue(),
         * 0xFFFFFFFF);
         * assertEquals(((TestContextItem003)someContextMap1.get("TestGlobalContextItem003")).getLongValue(),
         * 0xFFFFFFFFFFFFFFFFL);
         * assertTrue(((TestContextItem004)someContextMap1.get("TestGlobalContextItem004")).getFloatValue() ==
         * testFloat); assertTrue(((TestContextItem005)someContextMap1.get("TestGlobalContextItem005")).getDoubleValue()
         * == Math.PI);
         * assertEquals(((TestContextItem006)someContextMap1.get("TestGlobalContextItem006")).getStringValue(),
         * "This is a global context string");
         * assertTrue(((TestContextItem007)someContextMap1.get("TestGlobalContextItem007")).getLongValue() ==
         * testDate.getTime());
         * assertEquals(((TestContextItem008)someContextMap1.get("TestGlobalContextItem008")).getDateValue(), testDate);
         * assertEquals(((TestContextItem009)someContextMap1.get("TestGlobalContextItem009")), tciG9);
         * assertEquals(((TestContextItem00A)someContextMap1.get("TestGlobalContextItem00A")), tciGA);
         * assertEquals(((TestContextItem00B)someContextMap1.get("TestGlobalContextItem00B")).getSetValue(),
         * testHashSet);
         * assertEquals(((TestContextItem00C)someContextMap1.get("TestGlobalContextItem00C")).getMapValue(),
         * testHashMap);
         *
         * someContextMap1.flush();
         *
         * final Map<String, Object> valueMap2 = new HashMap<String, Object>();
         * valueMap2.put("TestExternalContextItem000", false); valueMap2.put("TestExternalContextItem001", (byte) 0xFF);
         * valueMap2.put("TestExternalContextItem002", 0xFFFFFFFF); valueMap2.put("TestExternalContextItem003",
         * 0xFFFFFFFFFFFFFFFFL); valueMap2.put("TestExternalContextItem004", testFloat);
         * valueMap2.put("TestExternalContextItem005", Math.PI); valueMap2.put("TestExternalContextItem006",
         * "This is an external context string"); valueMap2.put("TestExternalContextItem007", testDate.getTime());
         * valueMap2.put("TestExternalContextItem008", testDate); valueMap2.put("TestExternalContextItem009", tciE9);
         * valueMap2.put("TestExternalContextItem00A", tciEA); valueMap2.put("TestExternalContextItem00B", testHashSet);
         * valueMap2.put("TestExternalContextItem00C", testHashMap);
         *
         * ContextMap someContextMap2 = contextDistributor.createContextMap(someContextMap.getKey());
         * assertNotNull(someContextMap2); someContextMap2.setUserArtifactStack(usedArtifactStackArray);
         *
         * someContextMap2.putAll(valueMap2); logger.debug(someContextMap2.toString());
         *
         * assertEquals(((TestContextItem000)someContextMap2.get("TestExternalContextItem000")).getFlag(), false);
         * assertEquals(((TestContextItem001)someContextMap2.get("TestExternalContextItem001")).getByteValue(), (byte)
         * 0xFF); assertEquals(((TestContextItem002)someContextMap2.get("TestExternalContextItem002")).getIntValue(),
         * 0xFFFFFFFF);
         * assertEquals(((TestContextItem003)someContextMap2.get("TestExternalContextItem003")).getLongValue(),
         * 0xFFFFFFFFFFFFFFFFL);
         * assertTrue(((TestContextItem004)someContextMap2.get("TestExternalContextItem004")).getFloatValue() ==
         * testFloat);
         * assertTrue(((TestContextItem005)someContextMap2.get("TestExternalContextItem005")).getDoubleValue() ==
         * Math.PI);
         * assertEquals(((TestContextItem006)someContextMap2.get("TestExternalContextItem006")).getStringValue(),
         * "This is an external context string");
         * assertTrue(((TestContextItem007)someContextMap2.get("TestExternalContextItem007")).getLongValue() ==
         * testDate.getTime());
         * assertEquals(((TestContextItem008)someContextMap2.get("TestExternalContextItem008")).getDateValue(),
         * testDate); assertEquals(((TestContextItem009)someContextMap2.get("TestExternalContextItem009")), tciE9);
         * assertEquals(((TestContextItem00A)someContextMap2.get("TestExternalContextItem00A")), tciEA);
         * assertEquals(((TestContextItem00B)someContextMap2.get("TestExternalContextItem00B")).getSetValue(),
         * testHashSet);
         * assertEquals(((TestContextItem00C)someContextMap2.get("TestExternalContextItem00C")).getMapValue(),
         * testHashMap);
         *
         * Collection<Object> mapValues = someContextMap2.values();
         * assertTrue(someContextMap2.values().containsAll(mapValues));
         *
         * // Check that clearing does not work someContextMap2.clear(); assertEquals(someContextMap2.size(), 44);
         *
         * try { someContextMap2.containsKey(null); } catch (ContextRuntimeException e) {
         * assertEquals("null values are illegal on method parameter \"key\"", e.getMessage()); }
         *
         * assertTrue(someContextMap2.containsKey("TestExternalContextItem00B"));
         * assertFalse(someContextMap2.containsKey("TestExternalContextItem099"));
         *
         * assertTrue(someContextMap2.containsValue(tciE9)); assertFalse(someContextMap2.containsValue(new
         * String("Hello")));
         *
         * Set<Entry<String, Object>> entrySet = someContextMap2.entrySet(); assertEquals(entrySet.size(), 44);
         *
         * try { someContextMap2.get(null); } catch (ContextRuntimeException e) {
         * assertEquals("null values are illegal on method parameter \"key\"", e.getMessage()); }
         *
         * Object aObject = someContextMap2.get("TestExternalContextItem00A"); assertEquals(aObject, tciEA);
         *
         * // Null keys are illegal //put null keys should fail (return null) try { someContextMap2.put(null, null); }
         * catch (ContextRuntimeException e) { assertEquals("null keys are illegal on method parameter \"key\"",
         * e.getMessage()); }
         *
         * // Put null ContextItem should work (return null) try { // Put returns the previous value
         * assertEquals(((TestContextItem00B)someContextMap2.put("TestExternalContextItem00B", null)).getSetValue(),
         * testHashSet);
         *
         * // Should return null assertEquals(someContextMap2.get("TestExternalContextItem00B"), null); } catch
         * (Exception e) { logger.debug(e.getMessage());
         * assertTrue(e.getMessage().equals("null values are illegal on method parameter TestExternalContextItem00B"));
         * }
         *
         * someContextMap2.put("TestExternalContextItem00B", testHashSet);
         * assertTrue(someContextMap2.values().containsAll(mapValues));
         *
         * // Put should return the previous contextItem
         * assertNotEquals(someContextMap2.put("TestExternalContextItem008", testDate), null);
         * assertEquals(((TestContextItem008)someContextMap2.get("TestExternalContextItem008")).getDateValue(),
         * testDate);
         *
         * try { someContextMap2.put("TestExternalContextItem00A", null);
         * assertEquals(someContextMap2.get("TestExternalContextItem00A"), null); } catch (Exception e) {
         * logger.debug(e.getMessage());
         * assertTrue(e.getMessage().equals("null values are illegal on method parameter TestExternalContextItem00A"));
         * }
         *
         * someContextMap2.put("TestExternalContextItem00A", tciEA);
         * assertEquals(someContextMap2.get("TestExternalContextItem00A"), tciEA);
         *
         * // Should print warning try { someContextMap2.put("TestExternalContextItemFFF", null); } catch
         * (ContextRuntimeException e) {
         * assertEquals("null values are illegal on method parameter TestExternalContextItemFFF", e.getMessage()); }
         * assertEquals(someContextMap2.size(), 44);
         *
         * // Should ignore remove someContextMap2.remove("TestExternalContextItem017");
         * assertEquals(someContextMap2.size(), 44);
         *
         * assertEquals(someContextMap2.values().size(), 44);
         * assertTrue(someContextMap2.values().containsAll(mapValues));
         *
         * someContextMap2.flush(); contextDistributor.clear();
         */
    }
}
