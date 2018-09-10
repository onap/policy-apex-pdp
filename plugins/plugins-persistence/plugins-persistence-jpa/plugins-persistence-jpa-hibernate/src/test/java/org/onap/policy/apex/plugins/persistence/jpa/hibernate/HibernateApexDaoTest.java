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

package org.onap.policy.apex.plugins.persistence.jpa.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.test.entities.ArtifactKeyTestEntity;
import org.onap.policy.apex.context.test.entities.ReferenceKeyTestEntity;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;

/**
 * Junit test for class HibernateApexDao.
 * 
 * @author Dinh Danh Le (dinh.danh.le@ericsson.com)
 *
 */

public class HibernateApexDaoTest {
    private static final List<AxArtifactKey> TEST_ARTIKEYS = Arrays.asList(new AxArtifactKey[] {
        new AxArtifactKey("ABC", "0.0.1"),
        new AxArtifactKey("DEF", "0.1.1"), new AxArtifactKey("XYZ", "1.1.1")
    });

    private final DaoParameters daoParameters = new DaoParameters();

    private HibernateApexDao hibernateApexDao = null;

    /**
     * Set up tests.
     * 
     * @throws ApexException on test setup errors
     */
    @Before
    public void setupDao() throws ApexException {
        daoParameters.setPluginClass(HibernateApexDao.class.getCanonicalName());
        daoParameters.setPersistenceUnit("DAOTest");
        hibernateApexDao = new HibernateApexDao();
        hibernateApexDao.init(daoParameters);
    }

    /**
     * Cleardown tests.
     */
    @After
    public void teardownDao() {
        hibernateApexDao.close();
    }

    @Test
    public void test_NullArguments() {
        final AxArtifactKey nullArtifactKey = null;
        final AxArtifactKey nullRefernceKey = null;
        final List<Object> emptyList = Collections.emptyList();

        assertNull(hibernateApexDao.getArtifact(null, nullArtifactKey));
        assertNull(hibernateApexDao.getArtifact(ArtifactKeyTestEntity.class, nullArtifactKey));

        assertNull(hibernateApexDao.getArtifact(null, nullRefernceKey));
        assertNull(hibernateApexDao.getArtifact(ReferenceKeyTestEntity.class, nullRefernceKey));

        assertNotNull(hibernateApexDao.getAll(null));
        assertTrue(hibernateApexDao.getAll(null).equals(emptyList));
        assertNotNull(hibernateApexDao.getAll(ReferenceKeyTestEntity.class));
    }

    @Test
    public void test_createObject() throws ApexException {
        // create 3 more entities from testArtiKeys
        for (final AxArtifactKey akey : TEST_ARTIKEYS) {
            hibernateApexDao.create(new ReferenceKeyTestEntity(new AxReferenceKey(akey), Math.random() + 100.0));
        }
        assertEquals(3, hibernateApexDao.getAll(ReferenceKeyTestEntity.class).size());
    }

    @Test
    public void test_getAll() {
        // create a list of three entities from testArtiKeys
        final double[] genDoubleVals = new double[TEST_ARTIKEYS.size()];

        for (int i = 0; i < TEST_ARTIKEYS.size(); i++) {
            final AxArtifactKey akey = TEST_ARTIKEYS.get(i);
            genDoubleVals[i] = Math.random();
            hibernateApexDao.create(new ReferenceKeyTestEntity(new AxReferenceKey(akey), genDoubleVals[i]));
        }

        final List<ReferenceKeyTestEntity> ret = hibernateApexDao.getAll(ReferenceKeyTestEntity.class);
        assertEquals(TEST_ARTIKEYS.size(), ret.size());

        for (int i = 0; i < TEST_ARTIKEYS.size(); i++) {
            final ReferenceKeyTestEntity e = ret.get(i);
            assertEquals(TEST_ARTIKEYS.get(i), e.getKey().getParentArtifactKey());
            assertEquals(genDoubleVals[i], e.getDoubleValue(), 0.0);
        }
    }

    @Test
    public void test_getArtifactByReferenceKey() {
        final AxArtifactKey artifactKey = new AxArtifactKey("XXX", "0.0.1");
        final AxReferenceKey referenceKey = new AxReferenceKey(artifactKey, "Entity1");

        // assert null if Entity Class is null
        assertNull(hibernateApexDao.getArtifact(null, referenceKey));

        // create PersistenceContext with an entity
        hibernateApexDao.create(new ReferenceKeyTestEntity(referenceKey, 1.0));
        // assert null when trying to find an entity with an unknown key
        final AxArtifactKey anotherArtifactKey = new AxArtifactKey("YYY", "0.0.2");
        final AxReferenceKey anotherReferenceKey = new AxReferenceKey(anotherArtifactKey);

        assertNull(hibernateApexDao.getArtifact(ReferenceKeyTestEntity.class, anotherReferenceKey));

        // assert return only one entity when finding an entity with correct key
        final ReferenceKeyTestEntity retEntity = hibernateApexDao.getArtifact(ReferenceKeyTestEntity.class,
                        referenceKey);
        assertEquals(referenceKey, retEntity.getKey());
    }

    @Test
    public void test_getArtifactByArtifactKey() {
        final AxArtifactKey artifactKey = new AxArtifactKey("XXX", "0.0.1");
        // assert null if either Entity Class is null
        assertNull(hibernateApexDao.getArtifact(null, artifactKey));
        // create an entity
        hibernateApexDao.create(new ArtifactKeyTestEntity(artifactKey, 1.0));

        // assert null when trying to find an entity with an unknown key
        final AxArtifactKey otherArtifactKey = new AxArtifactKey("YYY", "0.0.2");
        assertNull(hibernateApexDao.getArtifact(ArtifactKeyTestEntity.class, otherArtifactKey));

        // assert return only one entity when finding an entity with correct key
        final ArtifactKeyTestEntity retEntity = hibernateApexDao.getArtifact(ArtifactKeyTestEntity.class, artifactKey);
        assertNotNull(retEntity);
        assertEquals(artifactKey, retEntity.getKey());
    }

    @Test
    public void test_deleteByArtifactKey() {
        // initialize a list of (3) entities corresponding to the list of testArtiKeys
        for (final AxArtifactKey akey : TEST_ARTIKEYS) {
            hibernateApexDao.create(new ArtifactKeyTestEntity(akey, Math.random()));
        }
        // create one more entity
        final ArtifactKeyTestEntity entity = new ArtifactKeyTestEntity(new AxArtifactKey("XYZ", "100.0.0"), 100.0);
        hibernateApexDao.create(entity);

        assertEquals(3, hibernateApexDao.deleteByArtifactKey(ArtifactKeyTestEntity.class, TEST_ARTIKEYS));

        // after deleteByArtifactKey()--> getAll().size() == 1
        final List<ArtifactKeyTestEntity> remainingEntities = hibernateApexDao.getAll(ArtifactKeyTestEntity.class);
        assertEquals(1, remainingEntities.size());
        // more details about the remainingEntities
        assertEquals(100.0, remainingEntities.get(0).getDoubleValue(), 0.0);
    }

    @Test
    public void test_deleteByReferenceKey() {
        // prepare 2 AxArtifactKeys
        final AxArtifactKey owner0Key = new AxArtifactKey("Owner0", "0.0.1");
        final AxArtifactKey owner1Key = new AxArtifactKey("Owner1", "0.0.1");

        // prepare a list of (3) AxReferenceKeys corresponding to owner0Key
        final List<AxReferenceKey> refKey0s = Arrays.asList(new AxReferenceKey[] {
            new AxReferenceKey(owner0Key, "Entity01"), new AxReferenceKey(owner0Key, "Entity02"),
            new AxReferenceKey(owner0Key, "Entity03")
        });
        // prepare 2 more AxReferenceKeys corresponding to owner1Key
        final AxReferenceKey refKey11 = new AxReferenceKey(owner1Key, "Entity11");
        final AxReferenceKey refKey12 = new AxReferenceKey(owner1Key, "Entity12");

        // create a list of 5 entities
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey0s.get(0), 101.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey0s.get(1), 102.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey0s.get(2), 103.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey11, 104.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey12, 105.0));

        // assert 3 entities are deleted by this deletion
        assertEquals(3, hibernateApexDao.deleteByReferenceKey(ReferenceKeyTestEntity.class, refKey0s));
        // after deletion, make sure getAll().size() == 2
        assertEquals(2, hibernateApexDao.getAll(ReferenceKeyTestEntity.class).size());
    }

    @Test
    public void test_deleteAll() {
        // initialize a list of (3) entities and add to the PersistenceContext
        for (final AxArtifactKey akey : TEST_ARTIKEYS) {
            hibernateApexDao.create(new ReferenceKeyTestEntity(new AxReferenceKey(akey), Math.random()));
        }
        // before deleteAll()--> getAll().size() == 3
        assertEquals(3, hibernateApexDao.getAll(ReferenceKeyTestEntity.class).size());
        hibernateApexDao.deleteAll(ReferenceKeyTestEntity.class);
        // after deleteAll()--> getAll().size() == 0
        assertEquals(0, hibernateApexDao.getAll(ReferenceKeyTestEntity.class).size());
    }

    @Test
    public void test_getAllByArtifactKey() {

        final AxArtifactKey artiKey0 = new AxArtifactKey("XYZA", "0.1.2");
        final AxArtifactKey artiKey1 = new AxArtifactKey("ONAP", "0.0.1");

        final AxReferenceKey refKey0 = new AxReferenceKey(artiKey0, "Entity0");
        final AxReferenceKey refKey1 = new AxReferenceKey(artiKey1, "Entity1");

        // test with null class with known key --> return an empty list
        assertNotNull(hibernateApexDao.getAll(null, artiKey1));
        assertTrue(hibernateApexDao.getAll(null, artiKey1).equals(Collections.emptyList()));

        // test with (not_null) ArtifactKeyTestEntity class
        assertEquals(0, hibernateApexDao.getAll(ReferenceKeyTestEntity.class, artiKey0).size());
        // create 2 entities
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey0, 100.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey0, 200.0));
        hibernateApexDao.create(new ReferenceKeyTestEntity(refKey1, 100.0));

        final List<ReferenceKeyTestEntity> ret = hibernateApexDao.getAll(ReferenceKeyTestEntity.class, artiKey0);
        assertEquals(1, ret.size());
        final ReferenceKeyTestEntity retEntity = ret.get(0);
        assertEquals(200.0, retEntity.getDoubleValue(), 0);
    }

}
