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

package org.onap.policy.apex.model.basicmodel.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.TestEntity;

/**
 * JUnit test class.
 */
public class EntityTest {
    private Connection connection;
    private ApexDao apexDao;

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

    @Test
    public void testEntityTestSanity() throws ApexException {
        final DaoParameters daoParameters = new DaoParameters();

        apexDao = new ApexDaoFactory().createApexDao(daoParameters);

        try {
            apexDao.init(null);
            fail("Test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("Apex persistence unit parameter not set", e.getMessage());
        }

        try {
            apexDao.init(daoParameters);
            fail("Test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("Apex persistence unit parameter not set", e.getMessage());
        }

        daoParameters.setPluginClass("somewhere.over.the.rainbow");
        daoParameters.setPersistenceUnit("Dorothy");
        try {
            apexDao.init(daoParameters);
            fail("Test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("Creation of Apex persistence unit \"Dorothy\" failed", e.getMessage());
        }
        try {
            apexDao.create(new AxArtifactKey());
            fail("Test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("Apex DAO has not been initialized", e.getMessage());
        }
        apexDao.close();
    }

    @Test
    public void testEntityTestAllOpsJpa() throws ApexException {
        final DaoParameters daoParameters = new DaoParameters();
        daoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        daoParameters.setPersistenceUnit("DaoTest");

        apexDao = new ApexDaoFactory().createApexDao(daoParameters);
        apexDao.init(daoParameters);

        testAllOps();
        apexDao.close();
    }

    @Test
    public void testEntityTestBadVals() throws ApexException {
        final DaoParameters daoParameters = new DaoParameters();
        daoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        daoParameters.setPersistenceUnit("DaoTest");

        apexDao = new ApexDaoFactory().createApexDao(daoParameters);
        apexDao.init(daoParameters);

        final AxArtifactKey nullKey = null;
        final AxReferenceKey nullRefKey = null;
        final List<AxArtifactKey> nullKeyList = null;
        final List<AxArtifactKey> emptyKeyList = new ArrayList<>();
        final List<AxReferenceKey> nullRKeyList = null;
        final List<AxReferenceKey> emptyRKeyList = new ArrayList<>();

        apexDao.create(nullKey);
        apexDao.createCollection(nullKeyList);
        apexDao.createCollection(emptyKeyList);

        apexDao.delete(nullKey);
        apexDao.deleteCollection(nullKeyList);
        apexDao.deleteCollection(emptyKeyList);
        apexDao.delete(AxArtifactKey.class, nullKey);
        apexDao.delete(AxReferenceKey.class, nullRefKey);
        apexDao.deleteByArtifactKey(AxArtifactKey.class, nullKeyList);
        apexDao.deleteByArtifactKey(AxArtifactKey.class, emptyKeyList);
        apexDao.deleteByReferenceKey(AxReferenceKey.class, nullRKeyList);
        apexDao.deleteByReferenceKey(AxReferenceKey.class, emptyRKeyList);

        apexDao.get(null, nullKey);
        apexDao.get(null, nullRefKey);
        apexDao.getAll(null);
        apexDao.getAll(null, nullKey);
        apexDao.getArtifact(null, nullKey);
        apexDao.getArtifact(AxArtifactKey.class, nullKey);
        apexDao.getArtifact(null, nullRefKey);
        apexDao.getArtifact(AxReferenceKey.class, nullRefKey);
        apexDao.size(null);

        apexDao.close();
    }

    private void testAllOps() {
        final AxArtifactKey aKey0 = new AxArtifactKey("A-KEY0", "0.0.1");
        final AxArtifactKey aKey1 = new AxArtifactKey("A-KEY1", "0.0.1");
        final AxArtifactKey aKey2 = new AxArtifactKey("A-KEY2", "0.0.1");
        final AxKeyInfo keyInfo0 = new AxKeyInfo(aKey0, UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        "key description 0");
        final AxKeyInfo keyInfo1 = new AxKeyInfo(aKey1, UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "key description 1");
        final AxKeyInfo keyInfo2 = new AxKeyInfo(aKey2, UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        "key description 2");

        apexDao.create(keyInfo0);

        final AxKeyInfo keyInfoBack0 = apexDao.get(AxKeyInfo.class, aKey0);
        assertTrue(keyInfo0.equals(keyInfoBack0));

        final AxKeyInfo keyInfoBackNull = apexDao.get(AxKeyInfo.class, AxArtifactKey.getNullKey());
        assertNull(keyInfoBackNull);

        final AxKeyInfo keyInfoBack1 = apexDao.getArtifact(AxKeyInfo.class, aKey0);
        assertTrue(keyInfoBack0.equals(keyInfoBack1));

        final AxKeyInfo keyInfoBack2 = apexDao.getArtifact(AxKeyInfo.class, new AxArtifactKey("A-KEY3", "0.0.1"));
        assertNull(keyInfoBack2);

        final Set<AxKeyInfo> keyInfoSetIn = new TreeSet<AxKeyInfo>();
        keyInfoSetIn.add(keyInfo1);
        keyInfoSetIn.add(keyInfo2);

        apexDao.createCollection(keyInfoSetIn);

        Set<AxKeyInfo> keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));

        keyInfoSetIn.add(keyInfo0);
        assertTrue(keyInfoSetIn.equals(keyInfoSetOut));

        apexDao.delete(keyInfo1);
        keyInfoSetIn.remove(keyInfo1);
        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertTrue(keyInfoSetIn.equals(keyInfoSetOut));

        apexDao.deleteCollection(keyInfoSetIn);
        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertEquals(0, keyInfoSetOut.size());

        keyInfoSetIn.add(keyInfo0);
        keyInfoSetIn.add(keyInfo1);
        keyInfoSetIn.add(keyInfo0);
        apexDao.createCollection(keyInfoSetIn);
        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertTrue(keyInfoSetIn.equals(keyInfoSetOut));

        apexDao.delete(AxKeyInfo.class, aKey0);
        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertEquals(2, keyInfoSetOut.size());
        assertEquals(2, apexDao.size(AxKeyInfo.class));

        final Set<AxArtifactKey> keySetIn = new TreeSet<AxArtifactKey>();
        keySetIn.add(aKey1);
        keySetIn.add(aKey2);

        final int deletedCount = apexDao.deleteByArtifactKey(AxKeyInfo.class, keySetIn);
        assertEquals(2, deletedCount);

        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertEquals(0, keyInfoSetOut.size());

        keyInfoSetIn.add(keyInfo0);
        keyInfoSetIn.add(keyInfo1);
        keyInfoSetIn.add(keyInfo0);
        apexDao.createCollection(keyInfoSetIn);
        keyInfoSetOut = new TreeSet<AxKeyInfo>(apexDao.getAll(AxKeyInfo.class));
        assertTrue(keyInfoSetIn.equals(keyInfoSetOut));

        apexDao.deleteAll(AxKeyInfo.class);
        assertEquals(0, apexDao.size(AxKeyInfo.class));

        final AxArtifactKey owner0Key = new AxArtifactKey("Owner0", "0.0.1");
        final AxArtifactKey owner1Key = new AxArtifactKey("Owner1", "0.0.1");
        final AxArtifactKey owner2Key = new AxArtifactKey("Owner2", "0.0.1");
        final AxArtifactKey owner3Key = new AxArtifactKey("Owner3", "0.0.1");
        final AxArtifactKey owner4Key = new AxArtifactKey("Owner4", "0.0.1");
        final AxArtifactKey owner5Key = new AxArtifactKey("Owner5", "0.0.1");

        apexDao.create(new TestEntity(new AxReferenceKey(owner0Key, "Entity0"), 100.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner0Key, "Entity1"), 101.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner0Key, "Entity2"), 102.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner0Key, "Entity3"), 103.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner0Key, "Entity4"), 104.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner1Key, "Entity5"), 105.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner1Key, "Entity6"), 106.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner1Key, "Entity7"), 107.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner2Key, "Entity8"), 108.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner2Key, "Entity9"), 109.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner3Key, "EntityA"), 110.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner4Key, "EntityB"), 111.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner5Key, "EntityC"), 112.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner5Key, "EntityD"), 113.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner5Key, "EntityE"), 114.0));
        apexDao.create(new TestEntity(new AxReferenceKey(owner5Key, "EntityF"), 115.0));

        TreeSet<TestEntity> testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class));
        assertEquals(16, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner0Key));
        assertEquals(5, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner1Key));
        assertEquals(3, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner2Key));
        assertEquals(2, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner3Key));
        assertEquals(1, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner4Key));
        assertEquals(1, testEntitySetOut.size());

        testEntitySetOut = new TreeSet<TestEntity>(apexDao.getAll(TestEntity.class, owner5Key));
        assertEquals(4, testEntitySetOut.size());

        assertNotNull(apexDao.get(TestEntity.class, new AxReferenceKey(owner0Key, "Entity0")));
        assertNotNull(apexDao.getArtifact(TestEntity.class, new AxReferenceKey(owner0Key, "Entity0")));
        assertNull(apexDao.get(TestEntity.class, new AxReferenceKey(owner0Key, "Entity1000")));
        assertNull(apexDao.getArtifact(TestEntity.class, new AxReferenceKey(owner0Key, "Entity1000")));
        apexDao.delete(TestEntity.class, new AxReferenceKey(owner0Key, "Entity0"));

        final Set<AxReferenceKey> rKeySetIn = new TreeSet<AxReferenceKey>();
        rKeySetIn.add(new AxReferenceKey(owner4Key, "EntityB"));
        rKeySetIn.add(new AxReferenceKey(owner5Key, "EntityD"));

        final int deletedRCount = apexDao.deleteByReferenceKey(TestEntity.class, rKeySetIn);
        assertEquals(2, deletedRCount);

        apexDao.update(new TestEntity(new AxReferenceKey(owner5Key, "EntityF"), 120.0));
    }
}
