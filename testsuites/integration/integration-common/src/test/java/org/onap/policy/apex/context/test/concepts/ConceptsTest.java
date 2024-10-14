/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.Map;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class ConceptsTest {

    @Test
    void testConceptBooleanItem() {
        assertDoesNotThrow(() -> new TestContextBooleanItem());
        assertDoesNotThrow(() -> new TestContextBooleanItem(true));
        var item = new TestContextBooleanItem();
        item.setFlag(false);
        assertFalse(item.isFlag());
    }

    @Test
    void testContextByteItem() {
        assertDoesNotThrow(() -> new TestContextByteItem());
        assertDoesNotThrow(() -> new TestContextByteItem((byte) 1));
        var byteItem = new TestContextByteItem();
        byteItem.setByteValue((byte) 0);
        assertEquals((byte) 0, byteItem.getByteValue());
        assertEquals((byte) 1, byteItem.getIncrementedByteValue());
    }

    @Test
    void testContextDateItem() {
        assertDoesNotThrow(() -> new TestContextDateItem());
        assertDoesNotThrow(() -> new TestContextDateItem(new Date()));
        assertDoesNotThrow(() -> new TestContextDateItem(1726611256L));
        var date = new TestContextDateItem();
        date.setDateValue(new Date());
        assertThat(date.getDateValue()).isInstanceOf(Date.class);
        var date2 = new TestContextDateItem();
        assertThat(date2.getMonth()).isInstanceOf(Integer.class);
        assertThat(date2.getYear()).isInstanceOf(Integer.class);
        assertThat(date2.getDay()).isInstanceOf(Integer.class);
        assertThat(date2.getTime()).isInstanceOf(Long.class);
        assertThat(date2.getMilliSecond()).isInstanceOf(Integer.class);
        assertThat(date2.getMinute()).isInstanceOf(Integer.class);
        assertThat(date2.getHour()).isInstanceOf(Integer.class);
        assertThat(date2.getSecond()).isInstanceOf(Integer.class);
        var date3 = new TestContextDateItem(null);
        assertNotNull(date3.getDateValue());
    }

    @Test
    void testContextDateLocaleItem() {
        assertDoesNotThrow(() -> new TestContextDateLocaleItem());
        assertDoesNotThrow(() -> new TestContextDateLocaleItem(new TestContextDateLocaleItem()));
        assertDoesNotThrow(() -> new TestContextDateLocaleItem(new TestContextDateItem(), "UTC", true,
                1, "english", "Ireland"));
        var localeItem = new TestContextDateLocaleItem();
        localeItem.setTzValue("utc");
        assertNotNull(localeItem.getTzValue());
    }

    @Test
    void testContextDateTzItem() {
        assertDoesNotThrow(() -> new TestContextDateTzItem());
        assertDoesNotThrow(() -> new TestContextDateTzItem(new TestContextDateTzItem()));
        assertDoesNotThrow(() -> new TestContextDateTzItem(new TestContextDateItem(), "utc", false));
        var tzItem = new TestContextDateTzItem();
        tzItem.setTzValue("utc");
        assertNotNull(tzItem.getTzValue());
        assertThat(tzItem.getTzValue()).isInstanceOf(String.class);
    }

    @Test
    void testContextDoubleItem() {
        assertDoesNotThrow(() -> new TestContextDoubleItem());
        assertDoesNotThrow(() -> new TestContextDoubleItem(0));
        var doubleItem = new TestContextDoubleItem();
        doubleItem.setDoubleValue(0);
        assertEquals(1, doubleItem.getIncrementedDoubleValue());
    }

    @Test
    void testContextFloatItem() {
        assertDoesNotThrow(() -> new TestContextFloatItem());
        assertDoesNotThrow(() -> new TestContextFloatItem(0));
        var floatItem = new TestContextFloatItem();
        floatItem.setFloatValue(0);
        assertEquals(1, floatItem.getIncrementedFloatValue());
    }

    @Test
    void testContextIntItem() {
        assertDoesNotThrow(() -> new TestContextIntItem());
        assertDoesNotThrow(() -> new TestContextIntItem(0));
        var intItem = new TestContextIntItem();
        intItem.setIntValue(0);
        assertEquals(1, intItem.getIncrementedIntValue());
    }

    @Test
    void testContextLongItem() {
        assertDoesNotThrow(() -> new TestContextLongItem());
        assertDoesNotThrow(() -> new TestContextLongItem(0));
        var longItem = new TestContextLongItem();
        longItem.setLongValue(0);
        assertEquals(1, longItem.getIncrementedLongValue());
    }

    @Test
    void testContextLongObjectItem() {
        assertDoesNotThrow(() -> new TestContextLongObjectItem());
        assertDoesNotThrow(() -> new TestContextLongObjectItem(0L));
        var longObjItem = new TestContextLongObjectItem();
        longObjItem.setLongValue(0L);
        assertEquals(1, longObjItem.getIncrementedLongValue());
    }

    @Test
    void testContextStringItem() {
        assertDoesNotThrow(() -> new TestContextStringItem());
        assertDoesNotThrow(() -> new TestContextStringItem("test"));
        var strItem = new TestContextStringItem();
        strItem.setStringValue("test");
        assertEquals("test", strItem.getStringValue());
    }

    @Test
    void testContextTreeMapItem() {
        assertDoesNotThrow(() -> new TestContextTreeMapItem());
        assertDoesNotThrow(() -> new TestContextTreeMapItem(Map.of("", "")));
        var treeMapItem = new TestContextTreeMapItem();
        treeMapItem.setMapValue(Map.of("test", "testVal"));
        assertEquals("testVal", treeMapItem.getMapValue().get("test"));
    }

    @Test
    void testContextTreeSetItem() {
        assertDoesNotThrow(() -> new TestContextTreeSetItem());
        var items = new TreeSet<String>();
        assertDoesNotThrow(() -> new TestContextTreeSetItem(items));
    }



}
