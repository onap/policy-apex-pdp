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

package org.onap.apex.model.basicmodel.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxConcept;
import org.onap.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.apex.model.basicmodel.dao.ApexDao;
import org.onap.apex.model.basicmodel.dao.DAOParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class DefaultApexDao is an JPA implementation of the {@link ApexDao} class for Apex concepts ({@link AxConcept}).
 * It uses the default JPA implementation in the javax {@link Persistence} class.
 *
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class DefaultApexDao implements ApexDao {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DefaultApexDao.class);

    private static final String SELECT_C_FROM = "SELECT c FROM ";
    private static final String AND_C_KEY_LOCAL_NAME = "' AND c.key.localName='";
    private static final String AND_C_KEY_PARENT_KEY_VERSION = "' AND c.key.parentKeyVersion='";
    private static final String C_WHERE_C_KEY_PARENT_KEY_NAME = " c WHERE c.key.parentKeyName='";
    private static final String AND_C_KEY_VERSION = "' AND c.key.version='";
    private static final String C_WHERE_C_KEY_NAME = " c WHERE c.key.name='";
    private static final String DELETE_FROM = "DELETE FROM ";

    // Entity manager for JPA
    private EntityManagerFactory emf = null;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#init(org.onap.apex.model.basicmodel.dao.DAOParameters)
     */
    @Override
    public void init(final DAOParameters daoParameters) throws ApexException {
        if (daoParameters == null || daoParameters.getPersistenceUnit() == null) {
            LOGGER.error("Apex persistence unit parameter not set");
            throw new ApexException("Apex persistence unit parameter not set");
        }

        LOGGER.debug("Creating Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" . . .");
        try {
            emf = Persistence.createEntityManagerFactory(daoParameters.getPersistenceUnit(),
                    daoParameters.getJdbcProperties());
        } catch (final Exception e) {
            LOGGER.warn("Creation of Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" failed", e);
            throw new ApexException(
                    "Creation of Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\" failed", e);
        }
        LOGGER.debug("Created Apex persistence unit \"" + daoParameters.getPersistenceUnit() + "\"");
    }

    /**
     * Gets the entity manager for this DAO.
     *
     * @return the entity manager
     */
    protected final synchronized EntityManager getEntityManager() {
        if (emf == null) {
            LOGGER.warn("Apex DAO has not been initialized");
            throw new ApexRuntimeException("Apex DAO has not been initialized");
        }

        return emf.createEntityManager();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#close()
     */
    @Override
    public final void close() {
        if (emf != null) {
            emf.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#create(org.onap.apex.model.basicmodel.concepts.AxConcept)
     */
    @Override
    public <T extends AxConcept> void create(final T obj) {
        if (obj == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.merge(obj);
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#delete(org.onap.apex.model.basicmodel.concepts.AxConcept)
     */
    @Override
    public <T extends AxConcept> void delete(final T obj) {
        if (obj == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.remove(mg.contains(obj) ? obj : mg.merge(obj));
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#delete(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public <T extends AxConcept> void delete(final Class<T> aClass, final AxArtifactKey key) {
        if (key == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_NAME + key.getName() + AND_C_KEY_VERSION
                    + key.getVersion() + "'", aClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#delete(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxReferenceKey)
     */
    @Override
    public <T extends AxConcept> void delete(final Class<T> aClass, final AxReferenceKey key) {
        if (key == null) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME + key.getParentKeyName()
                    + AND_C_KEY_PARENT_KEY_VERSION + key.getParentKeyVersion() + AND_C_KEY_LOCAL_NAME
                    + key.getLocalName() + "'", aClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#create(java.util.Collection)
     */
    @Override
    public <T extends AxConcept> void create(final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final T t : objs) {
                mg.merge(t);
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#delete(java.util.Collection)
     */
    @Override
    public <T extends AxConcept> void delete(final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return;
        }
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final T t : objs) {
                mg.remove(mg.contains(t) ? t : mg.merge(t));
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#deleteByArtifactKey(java.lang.Class, java.util.Collection)
     */
    @Override
    public <T extends AxConcept> int deleteByArtifactKey(final Class<T> aClass, final Collection<AxArtifactKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        int deletedCount = 0;
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final AxArtifactKey key : keys) {
                deletedCount += mg.createQuery(DELETE_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_NAME + key.getName()
                        + AND_C_KEY_VERSION + key.getVersion() + "'", aClass).executeUpdate();
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return deletedCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#deleteByReferenceKey(java.lang.Class, java.util.Collection)
     */
    @Override
    public <T extends AxConcept> int deleteByReferenceKey(final Class<T> aClass,
            final Collection<AxReferenceKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        int deletedCount = 0;
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            for (final AxReferenceKey key : keys) {
                deletedCount +=
                        mg.createQuery(
                                DELETE_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                                        + key.getParentKeyName() + AND_C_KEY_PARENT_KEY_VERSION
                                        + key.getParentKeyVersion() + AND_C_KEY_LOCAL_NAME + key.getLocalName() + "'",
                                aClass).executeUpdate();
            }
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return deletedCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#deleteAll(java.lang.Class)
     */
    @Override
    public <T extends AxConcept> void deleteAll(final Class<T> aClass) {
        final EntityManager mg = getEntityManager();
        try {
            mg.getTransaction().begin();
            mg.createQuery(DELETE_FROM + aClass.getSimpleName() + " c ", aClass).executeUpdate();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#get(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public <T extends AxConcept> T get(final Class<T> aClass, final AxArtifactKey key) {
        if (aClass == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        try {
            final T t = mg.find(aClass, key);
            if (t != null) {
                // This clone is created to force the JPA DAO to recurse down through the object
                try {
                    final T clonedT = aClass.newInstance();
                    t.copyTo(clonedT);
                    return clonedT;
                } catch (final Exception e) {
                    LOGGER.warn("Could not clone object of class \"" + aClass.getCanonicalName() + "\"", e);
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#get(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxReferenceKey)
     */
    @Override
    public <T extends AxConcept> T get(final Class<T> aClass, final AxReferenceKey key) {
        if (aClass == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        try {
            final T t = mg.find(aClass, key);
            if (t != null) {
                try {
                    final T clonedT = aClass.newInstance();
                    t.copyTo(clonedT);
                    return clonedT;
                } catch (final Exception e) {
                    LOGGER.warn("Could not clone object of class \"" + aClass.getCanonicalName() + "\"", e);
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#getAll(java.lang.Class)
     */
    @Override
    public <T extends AxConcept> List<T> getAll(final Class<T> aClass) {
        if (aClass == null) {
            return Collections.emptyList();
        }
        final EntityManager mg = getEntityManager();
        try {
            return mg.createQuery(SELECT_C_FROM + aClass.getSimpleName() + " c", aClass).getResultList();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#getAll(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public <T extends AxConcept> List<T> getAll(final Class<T> aClass, final AxArtifactKey parentKey) {
        if (aClass == null) {
            return Collections.emptyList();
        }
        final EntityManager mg = getEntityManager();
        try {
            return mg
                    .createQuery(SELECT_C_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                            + parentKey.getName() + AND_C_KEY_PARENT_KEY_VERSION + parentKey.getVersion() + "'", aClass)
                    .getResultList();
        } finally {
            mg.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#getArtifact(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public <T extends AxConcept> T getArtifact(final Class<T> aClass, final AxArtifactKey key) {
        if (aClass == null || key == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        List<T> ret;
        try {
            ret = mg.createQuery(SELECT_C_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_NAME + key.getName()
                    + AND_C_KEY_VERSION + key.getVersion() + "'", aClass).getResultList();
        } finally {
            mg.close();
        }
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        if (ret.size() > 1) {
            throw new IllegalArgumentException("More than one result was returned for search for " + aClass
                    + " with key " + key.getID() + ": " + ret);
        }
        return ret.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#getArtifact(java.lang.Class,
     * org.onap.apex.model.basicmodel.concepts.AxReferenceKey)
     */
    @Override
    public <T extends AxConcept> T getArtifact(final Class<T> aClass, final AxReferenceKey key) {
        if (aClass == null || key == null) {
            return null;
        }
        final EntityManager mg = getEntityManager();
        List<T> ret;
        try {
            ret = mg.createQuery(SELECT_C_FROM + aClass.getSimpleName() + C_WHERE_C_KEY_PARENT_KEY_NAME
                    + key.getParentKeyName() + AND_C_KEY_PARENT_KEY_VERSION + key.getParentKeyVersion()
                    + AND_C_KEY_LOCAL_NAME + key.getLocalName() + "'", aClass).getResultList();
        } finally {
            mg.close();
        }
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        if (ret.size() > 1) {
            throw new IllegalArgumentException("More than one result was returned for search for " + aClass
                    + " with key " + key.getID() + ": " + ret);
        }
        return ret.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#update(org.onap.apex.model.basicmodel.concepts.AxConcept)
     */
    @Override
    public <T extends AxConcept> T update(final T obj) {
        final EntityManager mg = getEntityManager();
        T ret;
        try {
            mg.getTransaction().begin();
            ret = mg.merge(obj);
            mg.flush();
            mg.getTransaction().commit();
        } finally {
            mg.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.dao.ApexDao#size(java.lang.Class)
     */
    @Override
    public <T extends AxConcept> long size(final Class<T> aClass) {
        if (aClass == null) {
            return 0;
        }
        final EntityManager mg = getEntityManager();
        long size = 0;
        try {
            size = mg.createQuery("SELECT COUNT(c) FROM " + aClass.getSimpleName() + " c", Long.class)
                    .getSingleResult();
        } finally {
            mg.close();
        }
        return size;
    }
}
