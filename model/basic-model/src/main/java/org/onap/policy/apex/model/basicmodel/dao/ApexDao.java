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

import java.util.Collection;
import java.util.List;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;

/**
 * The Interface ApexDao describes the DAO interface for reading and writing Apex {@link AxConcept} concepts to and from
 * databases using JDBC.
 *
 * @author Sergey Sachkov
 * @author liam.fallon@ericsson.com
 */
public interface ApexDao {

    /**
     * Initialize the Apex DAO with the given parameters.
     *
     * @param daoParameters parameters to use to access the database
     * @throws ApexException on initialization errors
     */
    void init(DaoParameters daoParameters) throws ApexException;

    /**
     * Close the Apex DAO.
     */
    void close();

    /**
     * Creates an Apex concept on the database.
     *
     * @param <T> the type of the object to create, a subclass of {@link AxConcept}
     * @param obj the object to create
     */
    <T extends AxConcept> void create(T obj);

    /**
     * Create a collection of objects in the database.
     *
     * @param <T> the type of the object to create, a subclass of {@link AxConcept}
     * @param objs the objects to create
     */
    <T extends AxConcept> void create(Collection<T> objs);

    /**
     * Delete an Apex concept on the database.
     *
     * @param <T> the type of the object to delete, a subclass of {@link AxConcept}
     * @param obj the object to delete
     */
    <T extends AxConcept> void delete(T obj);

    /**
     * Delete an Apex concept on the database.
     *
     * @param <T> the type of the object to delete, a subclass of {@link AxConcept}
     * @param someClass the class of the object to delete, a subclass of {@link AxConcept}
     * @param key the key of the object to delete
     */
    <T extends AxConcept> void delete(Class<T> someClass, AxArtifactKey key);

    /**
     * Delete an Apex concept on the database.
     *
     * @param <T> the type of the object to delete, a subclass of {@link AxConcept}
     * @param someClass the class of the object to delete, a subclass of {@link AxConcept}
     * @param key the key of the object to delete
     */
    <T extends AxConcept> void delete(Class<T> someClass, AxReferenceKey key);

    /**
     * Delete a collection of objects in the database.
     *
     * @param <T> the type of the objects to delete, a subclass of {@link AxConcept}
     * @param objs the objects to delete
     */
    <T extends AxConcept> void delete(Collection<T> objs);

    /**
     * Delete a collection of objects in the database referred to by artifact key.
     *
     * @param <T> the type of the objects to delete, a subclass of {@link AxConcept}
     * @param someClass the class of the objects to delete, a subclass of {@link AxConcept}
     * @param keys the keys of the objects to delete
     * @return the number of objects deleted
     */
    <T extends AxConcept> int deleteByArtifactKey(Class<T> someClass, Collection<AxArtifactKey> keys);

    /**
     * Delete a collection of objects in the database referred to by reference key.
     *
     * @param <T> the type of the objects to delete, a subclass of {@link AxConcept}
     * @param someClass the class of the objects to delete, a subclass of {@link AxConcept}
     * @param keys the keys of the objects to delete
     * @return the number of objects deleted
     */
    <T extends AxConcept> int deleteByReferenceKey(Class<T> someClass, Collection<AxReferenceKey> keys);

    /**
     * Delete all objects of a given class in the database.
     *
     * @param <T> the type of the objects to delete, a subclass of {@link AxConcept}
     * @param someClass the class of the objects to delete, a subclass of {@link AxConcept}
     */
    <T extends AxConcept> void deleteAll(Class<T> someClass);

    /**
     * Get an object from the database, referred to by artifact key.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param someClass the class of the object to get, a subclass of {@link AxConcept}
     * @param key the key of the object to get
     * @return the object that was retrieved from the database
     */
    <T extends AxConcept> T get(Class<T> someClass, AxArtifactKey key);

    /**
     * Get an object from the database, referred to by reference key.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param someClass the class of the object to get, a subclass of {@link AxConcept}
     * @param key the key of the object to get
     * @return the object that was retrieved from the database or null if the object was not retrieved
     */
    <T extends AxConcept> T get(Class<T> someClass, AxReferenceKey key);

    /**
     * Get all the objects in the database of a given type.
     *
     * @param <T> the type of the objects to get, a subclass of {@link AxConcept}
     * @param someClass the class of the objects to get, a subclass of {@link AxConcept}
     * @return the objects or null if no objects were retrieved
     */
    <T extends AxConcept> List<T> getAll(Class<T> someClass);

    /**
     * Get all the objects in the database of the given type with the given parent artifact key.
     *
     * @param <T> the type of the objects to get, a subclass of {@link AxConcept}
     * @param someClass the class of the objects to get, a subclass of {@link AxConcept}
     * @param parentKey the parent key of the concepts to get
     * @return the all
     */
    <T extends AxConcept> List<T> getAll(Class<T> someClass, AxArtifactKey parentKey);

    /**
     * Get a concept from the database with the given artifact key.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param someClass the class of the object to get, a subclass of {@link AxConcept}
     * @param artifactId the artifact key of the concept to get
     * @return the concept that matches the key or null if the concept is not retrieved
     */
    <T extends AxConcept> T getArtifact(Class<T> someClass, AxArtifactKey artifactId);

    /**
     * Get a concept from the database with the given reference key.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param someClass the class of the object to get, a subclass of {@link AxConcept}
     * @param artifactId the artifact key of the concept to get
     * @return the concept that matches the key or null if the concept is not retrieved
     */
    <T extends AxConcept> T getArtifact(Class<T> someClass, AxReferenceKey artifactId);

    /**
     * Get the number of instances of a concept that exist in the database.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param someClass the class of the object to get, a subclass of {@link AxConcept}
     * @return the number of instances of the concept in the database
     */
    <T extends AxConcept> long size(Class<T> someClass);

    /**
     * Update a concept in the database.
     *
     * @param <T> the type of the object to get, a subclass of {@link AxConcept}
     * @param obj the object to update
     * @return the updated object
     */
    <T extends AxConcept> T update(T obj);
}
