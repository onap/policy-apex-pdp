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

package org.onap.policy.apex.context.test.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Cover uncovered code in concepts.
 *
 */
public class ConceptUncoveredTest {

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testInt() {
        TestContextIntItem intItem = new TestContextIntItem(123);
        assertEquals(123, intItem.getIntValue());

        assertFalse(intItem.equals(null));
        assertTrue(intItem.equals(intItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(intItem.equals(booleanItem));

        TestContextIntItem otherIntItem = new TestContextIntItem(intItem);
        assertTrue(intItem.equals(otherIntItem));
        otherIntItem.setIntValue(321);
        assertFalse(intItem.equals(otherIntItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testByte() {
        TestContextByteItem byteItem = new TestContextByteItem((byte) 123);
        assertEquals(123, byteItem.getByteValue());

        assertFalse(byteItem.equals(null));
        assertTrue(byteItem.equals(byteItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(byteItem.equals(booleanItem));

        TestContextByteItem otherByteItem = new TestContextByteItem((byte) 123);
        assertTrue(byteItem.equals(otherByteItem));
        otherByteItem.setByteValue((byte) 321);
        assertFalse(byteItem.equals(otherByteItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testLong() {
        TestContextLongItem longItem = new TestContextLongItem((long) 123);
        assertEquals(123, longItem.getLongValue());

        assertFalse(longItem.equals(null));
        assertTrue(longItem.equals(longItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(longItem.equals(booleanItem));

        TestContextLongItem otherLongItem = new TestContextLongItem((long) 123);
        assertTrue(longItem.equals(otherLongItem));
        otherLongItem.setLongValue((long) 321);
        assertFalse(longItem.equals(otherLongItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testFloat() {
        TestContextFloatItem floatItem = new TestContextFloatItem((float) 123);
        assertEquals(new Float("123"), (Float) floatItem.getFloatValue());

        assertFalse(floatItem.equals(null));
        assertTrue(floatItem.equals(floatItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(floatItem.equals(booleanItem));

        TestContextFloatItem otherFloatItem = new TestContextFloatItem((float) 123);
        assertTrue(floatItem.equals(otherFloatItem));
        otherFloatItem.setFloatValue((float) 321);
        assertFalse(floatItem.equals(otherFloatItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testDouble() {
        TestContextDoubleItem doubleItem = new TestContextDoubleItem((double) 123);
        assertEquals(new Double("123"), (Double) doubleItem.getDoubleValue());

        assertFalse(doubleItem.equals(null));
        assertTrue(doubleItem.equals(doubleItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(doubleItem.equals(booleanItem));

        TestContextDoubleItem otherDoubleItem = new TestContextDoubleItem((double) 123);
        assertTrue(doubleItem.equals(otherDoubleItem));
        otherDoubleItem.setDoubleValue((double) 321);
        assertFalse(doubleItem.equals(otherDoubleItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testBoolean() {
        TestContextBooleanItem booleanItem = new TestContextBooleanItem(true);
        assertEquals(true, booleanItem.getFlag());

        assertFalse(booleanItem.equals(null));
        assertTrue(booleanItem.equals(booleanItem));

        TestContextDoubleItem doubleItem = new TestContextDoubleItem();
        assertFalse(booleanItem.equals(doubleItem));

        TestContextBooleanItem otherBooleanItem = new TestContextBooleanItem(true);
        assertTrue(booleanItem.equals(otherBooleanItem));
        otherBooleanItem.setFlag(false);
        assertFalse(booleanItem.equals(otherBooleanItem));

        assertEquals(1262, booleanItem.hashCode());
        assertEquals(1268, otherBooleanItem.hashCode());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testString() {
        TestContextStringItem stringItem = new TestContextStringItem("A String");
        assertEquals("A String", stringItem.getStringValue());

        assertFalse(stringItem.equals(null));
        assertTrue(stringItem.equals(stringItem));

        TestContextDoubleItem doubleItem = new TestContextDoubleItem();
        assertFalse(stringItem.equals(doubleItem));

        TestContextStringItem otherStringItem = new TestContextStringItem("A String");
        assertTrue(stringItem.equals(otherStringItem));
        otherStringItem.setStringValue("Some Other String Value");
        assertFalse(stringItem.equals(otherStringItem));

        otherStringItem.setStringValue(null);
        assertEquals(-1859249905, stringItem.hashCode());
        assertEquals(31, otherStringItem.hashCode());

        assertFalse(otherStringItem.equals(stringItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testLongObject() {
        TestContextLongObjectItem longItem = new TestContextLongObjectItem((long) 123);
        assertEquals((Long) 123L, longItem.getLongValue());

        assertFalse(longItem.equals(null));
        assertTrue(longItem.equals(longItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(longItem.equals(booleanItem));

        TestContextLongObjectItem otherLongItem = new TestContextLongObjectItem((long) 123);
        assertTrue(longItem.equals(otherLongItem));
        otherLongItem.setLongValue((long) 321);
        assertFalse(longItem.equals(otherLongItem));

        otherLongItem.setLongValue(null);
        assertEquals(154, longItem.hashCode());
        assertEquals(31, otherLongItem.hashCode());
        assertFalse(otherLongItem.equals(longItem));
    }

    @SuppressWarnings(
        { "unlikely-arg-type", "rawtypes", "unchecked" })
    @Test
    public void testTreeMap() {
        TestContextTreeMapItem treeMapItem = new TestContextTreeMapItem();
        assertEquals(new TreeMap(), treeMapItem.getMapValue());

        assertFalse(treeMapItem.equals(null));
        assertTrue(treeMapItem.equals(treeMapItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(treeMapItem.equals(booleanItem));

        TestContextTreeMapItem otherTreeMapItem = new TestContextTreeMapItem();
        assertTrue(treeMapItem.equals(otherTreeMapItem));
        otherTreeMapItem.getMapValue().put("Key", "Value");
        assertFalse(treeMapItem.equals(otherTreeMapItem));

        treeMapItem.setMapValue(new TreeMap());
        otherTreeMapItem.setMapValue(null);
        assertNotNull(otherTreeMapItem.getMapValue());
        otherTreeMapItem.setMapValue(null);
        assertEquals(31, treeMapItem.hashCode());
        assertEquals(31, otherTreeMapItem.hashCode());
        assertFalse(otherTreeMapItem.equals(treeMapItem));
        treeMapItem.setMapValue(null);
        assertTrue(otherTreeMapItem.equals(treeMapItem));
    }

    @SuppressWarnings(
        { "unlikely-arg-type", "rawtypes", "unchecked" })
    @Test
    public void testTreeSet() {
        TestContextTreeSetItem treeSetItem = new TestContextTreeSetItem();
        assertEquals(new TreeSet(), treeSetItem.getSetValue());

        assertFalse(treeSetItem.equals(null));
        assertTrue(treeSetItem.equals(treeSetItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(treeSetItem.equals(booleanItem));

        TestContextTreeSetItem otherTreeSetItem = new TestContextTreeSetItem();
        assertTrue(treeSetItem.equals(otherTreeSetItem));
        otherTreeSetItem.getSetValue().add("Value");
        assertFalse(treeSetItem.equals(otherTreeSetItem));

        treeSetItem.setSetValue(new TreeSet());
        otherTreeSetItem.setSetValue(null);
        assertNotNull(otherTreeSetItem.getSetValue());
        otherTreeSetItem.setSetValue(null);
        assertEquals(31, treeSetItem.hashCode());
        assertEquals(31, otherTreeSetItem.hashCode());
        assertFalse(otherTreeSetItem.equals(treeSetItem));
        treeSetItem.setSetValue(null);
        assertTrue(otherTreeSetItem.equals(treeSetItem));

        String[] stringArray =
            { "hello", "goodbye" };
        TestContextTreeSetItem treeSetItemFromArray = new TestContextTreeSetItem(stringArray);
        assertTrue(treeSetItemFromArray.getSetValue().contains("hello"));
        assertTrue(treeSetItemFromArray.getSetValue().contains("goodbye"));
    }

    @SuppressWarnings(
        { "unlikely-arg-type" })
    @Test
    public void testDate() {
        TestContextDateItem dateItem = new TestContextDateItem();
        assertTrue(new Date().getTime() >= dateItem.getDateValue().getTime());

        assertFalse(dateItem.equals(null));
        assertTrue(dateItem.equals(dateItem));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(dateItem.equals(booleanItem));

        TestContextDateItem otherDateItem = new TestContextDateItem(dateItem.getDateValue());
        assertTrue(dateItem.equals(otherDateItem));
        otherDateItem.setDateValue(Date.from(Instant.now()));
        assertFalse(dateItem.equals(otherDateItem));

        dateItem.setDateValue(new Date());
        otherDateItem.setDateValue(null);
        assertNotNull(otherDateItem.getDateValue());
        otherDateItem.setDateValue(new Date(12345678));
        assertEquals(939444071, otherDateItem.hashCode());
        assertFalse(otherDateItem.equals(dateItem));
        dateItem = new TestContextDateItem(null);
        otherDateItem = new TestContextDateItem(null);
        assertTrue(otherDateItem.equals(dateItem));

        dateItem = new TestContextDateItem(new Date(1538394566123L));
        assertEquals(2018, dateItem.getYear());
        assertEquals(9, dateItem.getMonth());
        assertEquals(1, dateItem.getDay());
        assertEquals(11, dateItem.getHour());
        assertEquals(49, dateItem.getMinute());
        assertEquals(26, dateItem.getSecond());
        assertEquals(123, dateItem.getMilliSecond());

        dateItem = new TestContextDateItem(new Date(0L));
        otherDateItem = new TestContextDateItem(new Date(1L));
        assertFalse(dateItem.equals(otherDateItem));

        otherDateItem = new TestContextDateItem(new Date(1000L));
        assertFalse(dateItem.equals(otherDateItem));

        otherDateItem = new TestContextDateItem(new Date(60000L));
        assertFalse(dateItem.equals(otherDateItem));

        otherDateItem = new TestContextDateItem(new Date(3600000L));
        assertFalse(dateItem.equals(otherDateItem));

        otherDateItem = new TestContextDateItem(new Date(86400000L));
        assertFalse(dateItem.equals(otherDateItem));

        otherDateItem = new TestContextDateItem(new Date(2678400000L));
        assertFalse(dateItem.equals(otherDateItem));

        dateItem = new TestContextDateItem(123L);
        assertEquals(123, dateItem.getTime());

        assertEquals("TestContextItem008 [time=123, year=1970, month=0, day=1, "
                        + "hour=0, minute=0, second=0, milliSecond=123]", dateItem.toString());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testDateTz() {
        TestContextDateItem dateItem = new TestContextDateItem(new Date(0L));

        TestContextDateTzItem dateTzItem = new TestContextDateTzItem(dateItem, "UTC", true);
        assertEquals("Coordinated Universal Time", dateTzItem.getTzValue());
        assertEquals(true, dateTzItem.getDst());

        assertFalse(dateTzItem.equals(null));
        assertTrue(dateTzItem.equals(dateTzItem));

        TestContextDoubleItem doubleItem = new TestContextDoubleItem();
        assertFalse(dateTzItem.equals(doubleItem));

        TestContextDateTzItem otherDateTzItem = new TestContextDateTzItem(dateItem, "UTC", true);
        assertTrue(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setDst(false);
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setDst(true);
        otherDateTzItem.setTzValue("IST");
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setTzValue(null);
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setTzValue("UTC");
        assertTrue(otherDateTzItem.equals(dateTzItem));

        dateTzItem.setDateValue(null);
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setDateValue(null);
        assertTrue(otherDateTzItem.equals(dateTzItem));

        TestContextDateItem otherDateItem = new TestContextDateItem(new Date(1L));
        dateTzItem.setDateValue(dateItem);
        otherDateTzItem.setDateValue(otherDateItem);
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setDateValue(dateItem);

        dateTzItem.setTzValue(null);
        assertFalse(dateTzItem.equals(otherDateTzItem));
        otherDateTzItem.setTzValue(null);
        assertTrue(otherDateTzItem.equals(dateTzItem));

        dateTzItem.setTzValue("UTC");
        otherDateTzItem.setTzValue("IST");
        assertFalse(dateTzItem.equals(otherDateTzItem));

        dateTzItem.setDateValue(null);
        dateTzItem.setTzValue(null);
        dateTzItem.setDst(true);
        assertEquals(67952, dateTzItem.hashCode());

        dateTzItem.setDst(false);
        assertEquals(68138, dateTzItem.hashCode());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testDateLocale() {
        TestContextDateItem dateItem = new TestContextDateItem(new Date(0L));

        TestContextDateLocaleItem dateLocaleItem = new TestContextDateLocaleItem(dateItem, "UTC", true, 1, "EN", "IE");
        assertEquals("Coordinated Universal Time", dateLocaleItem.getTzValue());
        assertEquals(true, dateLocaleItem.getDst());

        assertFalse(dateLocaleItem.equals(null));
        assertTrue(dateLocaleItem.equals(dateLocaleItem));

        TestContextDoubleItem doubleItem = new TestContextDoubleItem();
        assertFalse(dateLocaleItem.equals(doubleItem));

        TestContextDateLocaleItem otherDateLocaleItem = new TestContextDateLocaleItem(dateItem, "UTC", true, 1, "EN",
                        "IE");
        assertTrue(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setDst(false);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setDst(true);
        otherDateLocaleItem.setTzValue("IST");
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setTzValue(null);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setTzValue("UTC");
        assertTrue(otherDateLocaleItem.equals(dateLocaleItem));

        dateLocaleItem.setDateValue(null);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setDateValue(null);
        assertTrue(otherDateLocaleItem.equals(dateLocaleItem));

        TestContextDateItem otherDateItem = new TestContextDateItem(new Date(1L));
        dateLocaleItem.setDateValue(dateItem);
        otherDateLocaleItem.setDateValue(otherDateItem);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setDateValue(dateItem);

        dateLocaleItem.setTzValue(null);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setTzValue(null);
        assertTrue(otherDateLocaleItem.equals(dateLocaleItem));

        dateLocaleItem.setTzValue("UTC");
        otherDateLocaleItem.setTzValue("IST");
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));

        dateLocaleItem.setDateValue(null);
        dateLocaleItem.setTzValue(null);
        dateLocaleItem.setDst(true);
        dateLocaleItem.setLocale(new Locale("EN", "IE"));
        assertEquals(-1567427636, dateLocaleItem.hashCode());

        dateLocaleItem.setDst(false);
        assertEquals(-1567248890, dateLocaleItem.hashCode());

        dateLocaleItem.setLocale(null);
        assertEquals(65480619, dateLocaleItem.hashCode());

        dateLocaleItem.setLocale(new Locale("EN", "IE"));
        assertEquals(new Locale("EN", "IE"), dateLocaleItem.getLocale());
        assertEquals(1, dateLocaleItem.getUtcOffset());

        dateLocaleItem = new TestContextDateLocaleItem(dateItem, "UTC", true, 1, "EN", "IE");
        otherDateLocaleItem = new TestContextDateLocaleItem(dateItem, "UTC", true, 1, "EN", "IE");
        dateLocaleItem.setLocale(null);
        assertFalse(dateLocaleItem.equals(otherDateLocaleItem));
        otherDateLocaleItem.setLocale(null);
        assertTrue(otherDateLocaleItem.equals(dateLocaleItem));

        dateLocaleItem.setUtcOffset(0);
        assertFalse(otherDateLocaleItem.equals(dateLocaleItem));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testPolicyContextItem() {
        TestPolicyContextItem item0 = new TestPolicyContextItem();
        TestPolicyContextItem item1 = new TestPolicyContextItem();

        assertTrue(item0.equals(item0));
        assertTrue(item0.equals(item1));
        assertFalse(item0.equals(null));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(item0.equals(booleanItem));

        assertEquals(887503681, item0.hashCode());
        item0.setTestPolicyContextItem000(new TestContextStringItem());
        item0.setTestPolicyContextItem001(new TestContextLongItem());
        item0.setTestPolicyContextItem002(new TestContextDoubleItem());
        item0.setTestPolicyContextItem003(new TestContextBooleanItem());
        item0.setTestPolicyContextItem004(new TestContextLongItem());
        item0.setTestPolicyContextItem005(new TestContextTreeMapItem());
        assertEquals(1805779574, item0.hashCode());

        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem000(new TestContextStringItem("Hello"));
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem000(item0.getTestPolicyContextItem000());
        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem001(new TestContextLongItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem001(item0.getTestPolicyContextItem001());
        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem002(new TestContextDoubleItem(123.45));
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem002(item0.getTestPolicyContextItem002());
        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem003(new TestContextBooleanItem(true));
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem003(item0.getTestPolicyContextItem003());
        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem004(new TestContextLongItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem004(item0.getTestPolicyContextItem004());
        assertFalse(item1.equals(item0));

        item1.setTestPolicyContextItem005(new TestContextTreeMapItem());
        item1.getTestPolicyContextItem005().getMapValue().put("Key", "Value");
        assertFalse(item1.equals(item0));
        item1.setTestPolicyContextItem005(item0.getTestPolicyContextItem005());
        assertTrue(item1.equals(item0));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testExternalContextItem() {
        TestExternalContextItem item0 = new TestExternalContextItem();
        TestExternalContextItem item1 = new TestExternalContextItem();

        assertTrue(item0.equals(item0));
        assertTrue(item0.equals(item1));
        assertFalse(item0.equals(null));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(item0.equals(booleanItem));

        assertEquals(-505558625, item0.hashCode());
        item0.setTestExternalContextItem000(new TestContextBooleanItem());
        item0.setTestExternalContextItem001(new TestContextByteItem());
        item0.setTestExternalContextItem002(new TestContextIntItem());
        item0.setTestExternalContextItem003(new TestContextLongItem());
        item0.setTestExternalContextItem004(new TestContextFloatItem());
        item0.setTestExternalContextItem005(new TestContextDoubleItem());
        item0.setTestExternalContextItem006(new TestContextStringItem());
        item0.setTestExternalContextItem007(new TestContextLongObjectItem());
        item0.setTestExternalContextItem008(new TestContextDateItem());
        item0.setTestExternalContextItem009(new TestContextDateTzItem());
        item0.setTestExternalContextItem00A(new TestContextDateLocaleItem());
        item0.setTestExternalContextItem00B(new TestContextTreeSetItem());
        item0.setTestExternalContextItem00C(new TestContextTreeMapItem());
        assertTrue(item0.hashCode() != 0);

        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem000(new TestContextBooleanItem(true));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem000(item0.getTestExternalContextItem000());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem001(new TestContextByteItem((byte) 123));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem001(item0.getTestExternalContextItem001());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem002(new TestContextIntItem(123));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem002(item0.getTestExternalContextItem002());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem003(new TestContextLongItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem003(item0.getTestExternalContextItem003());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem004(new TestContextFloatItem((float) 123.45));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem004(item0.getTestExternalContextItem004());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem005(new TestContextDoubleItem(123.45));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem005(item0.getTestExternalContextItem005());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem006(new TestContextStringItem("Hello"));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem006(item0.getTestExternalContextItem006());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem007(new TestContextLongObjectItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem007(item0.getTestExternalContextItem007());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem008(new TestContextDateItem(new Date(124)));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem008(item0.getTestExternalContextItem008());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem009(
                        new TestContextDateTzItem(new TestContextDateItem(new Date(124)), "UTC", true));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem009(item0.getTestExternalContextItem009());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem00A(new TestContextDateLocaleItem(new TestContextDateItem(new Date(124)), "UTC",
                        true, 1, "EN", "IE"));
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem00A(item0.getTestExternalContextItem00A());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem00B(new TestContextTreeSetItem());
        item1.getTestExternalContextItem00B().getSetValue().add("Hello");
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem00B(item0.getTestExternalContextItem00B());
        assertFalse(item1.equals(item0));

        item1.setTestExternalContextItem00C(new TestContextTreeMapItem());
        item1.getTestExternalContextItem00C().getMapValue().put("Key", "Value");
        assertFalse(item1.equals(item0));
        item1.setTestExternalContextItem00C(item0.getTestExternalContextItem00C());
        assertTrue(item1.equals(item0));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testGlobalContextItem() {
        TestGlobalContextItem item0 = new TestGlobalContextItem();
        TestGlobalContextItem item1 = new TestGlobalContextItem();

        assertTrue(item0.equals(item0));
        assertTrue(item0.equals(item1));
        assertFalse(item0.equals(null));

        TestContextBooleanItem booleanItem = new TestContextBooleanItem();
        assertFalse(item0.equals(booleanItem));

        assertEquals(-505558625, item0.hashCode());
        item0.setTestGlobalContextItem000(new TestContextBooleanItem());
        item0.setTestGlobalContextItem001(new TestContextByteItem());
        item0.setTestGlobalContextItem002(new TestContextIntItem());
        item0.setTestGlobalContextItem003(new TestContextLongItem());
        item0.setTestGlobalContextItem004(new TestContextFloatItem());
        item0.setTestGlobalContextItem005(new TestContextDoubleItem());
        item0.setTestGlobalContextItem006(new TestContextStringItem());
        item0.setTestGlobalContextItem007(new TestContextLongObjectItem());
        item0.setTestGlobalContextItem008(new TestContextDateItem());
        item0.setTestGlobalContextItem009(new TestContextDateTzItem());
        item0.setTestGlobalContextItem00A(new TestContextDateLocaleItem());
        item0.setTestGlobalContextItem00B(new TestContextTreeSetItem());
        item0.setTestGlobalContextItem00C(new TestContextTreeMapItem());
        assertTrue(item0.hashCode() != 0);

        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem000(new TestContextBooleanItem(true));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem000(item0.getTestGlobalContextItem000());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem001(new TestContextByteItem((byte) 123));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem001(item0.getTestGlobalContextItem001());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem002(new TestContextIntItem(123));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem002(item0.getTestGlobalContextItem002());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem003(new TestContextLongItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem003(item0.getTestGlobalContextItem003());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem004(new TestContextFloatItem((float) 123.45));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem004(item0.getTestGlobalContextItem004());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem005(new TestContextDoubleItem(123.45));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem005(item0.getTestGlobalContextItem005());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem006(new TestContextStringItem("Hello"));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem006(item0.getTestGlobalContextItem006());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem007(new TestContextLongObjectItem(123L));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem007(item0.getTestGlobalContextItem007());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem008(new TestContextDateItem(new Date(124)));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem008(item0.getTestGlobalContextItem008());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem009(
                        new TestContextDateTzItem(new TestContextDateItem(new Date(124)), "UTC", true));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem009(item0.getTestGlobalContextItem009());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem00A(new TestContextDateLocaleItem(new TestContextDateItem(new Date(124)), "UTC",
                        true, 1, "EN", "IE"));
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem00A(item0.getTestGlobalContextItem00A());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem00B(new TestContextTreeSetItem());
        item1.getTestGlobalContextItem00B().getSetValue().add("Hello");
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem00B(item0.getTestGlobalContextItem00B());
        assertFalse(item1.equals(item0));

        item1.setTestGlobalContextItem00C(new TestContextTreeMapItem());
        item1.getTestGlobalContextItem00C().getMapValue().put("Key", "Value");
        assertFalse(item1.equals(item0));
        item1.setTestGlobalContextItem00C(item0.getTestGlobalContextItem00C());
        assertTrue(item1.equals(item0));
    }
}
