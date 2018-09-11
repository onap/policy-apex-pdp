/*
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

package org.onap.policy.apex.model.modelapi;

import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The Interface ApexModelAPI provides functional methods that allow Apex models to be managed.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexModel extends ApexEditorApi {
    /**
     * Make a deep copy of the Model.
     *
     * @return the result of the operation
     */
    ApexModel clone();

    /**
     * Load an Apex model from a string.
     *
     * @param modelString the string with the model
     * @return the result of the operation
     */
    ApexApiResult loadFromString(String modelString);

    /**
     * Load an Apex model from a file.
     *
     * @param fileName the file name of the file with the model
     * @return the result of the operation
     */
    ApexApiResult loadFromFile(String fileName);

    /**
     * Save an Apex model to a file.
     *
     * @param fileName the file name
     * @param xmlFlag if true, save the file in XML format, otherwise save the file in the default JSON format
     * @return the result of the operation
     */
    ApexApiResult saveToFile(String fileName, boolean xmlFlag);

    /**
     * Load an Apex model from a database.
     *
     * @param modelName the name of the model to load
     * @param modelVersion the version of the model to load, loads the policy model from the database with this name, if
     *        more than one exist, an exception is thrown
     * @param daoParameters the parameters to use to access the database over JDBC
     * @return the result of the operation
     */
    ApexApiResult loadFromDatabase(String modelName, String modelVersion, DaoParameters daoParameters);

    /**
     * Save an Apex model to a database.
     *
     * @param daoParameters the parameters to use to access the database over JDBC
     * @return the result of the operation
     */
    ApexApiResult saveToDatabase(DaoParameters daoParameters);

    /**
     * Read an APEX model from a location identified by a URL.
     *
     * @param urlString the url string
     * @return the result of the operation
     */
    ApexApiResult readFromUrl(String urlString);

    /**
     * Write an APEX model to a location identified by a URL.
     *
     * @param urlString the URL to read the model from
     * @param xmlFlag if true, save the file in XML format, otherwise save the file in the default JSON format
     * @return the result of the operation
     */
    ApexApiResult writeToUrl(String urlString, boolean xmlFlag);

    /**
     * Analyse an Apex model that shows the concept usage references of a policy model.
     *
     * @return the result of the operation
     */
    ApexApiResult analyse();

    /**
     * Validate an Apex model, checking all concepts and references in the model.
     *
     * @return the result of the operation
     */
    ApexApiResult validate();

    /**
     * Compare to Apex models, returning the differences between the models.
     *
     * @param otherModelFileName the file name of the other model
     * @param diffsOnly only returns differences between the model when set
     * @param keysOnly only returns the keys that are different when set, when not set values are also returned
     * @return the result of the operation
     */
    ApexApiResult compare(String otherModelFileName, boolean diffsOnly, boolean keysOnly);

    /**
     * Compare two Apex models, returning the differences between the models.
     *
     * @param otherModelString the other model as a string
     * @param diffsOnly only returns differences between the model when set
     * @param keysOnly only returns the keys that are different when set, when not set values are also returned
     * @return the result of the operation
     */
    ApexApiResult compareWithString(String otherModelString, boolean diffsOnly, boolean keysOnly);

    /**
     * Split out a sub model from an Apex model that contains a given subset of the policies in the original model.
     *
     * @param targetModelName the file name of the target model in which to store the model split out from the original
     *        model
     * @param splitOutPolicies the policies form the original model to include in the split out model, specified as a
     *        comma delimited list of policy names
     * @return the result of the operation
     */
    ApexApiResult split(String targetModelName, String splitOutPolicies);

    /**
     * Split out a sub model from an Apex model that contains a given subset of the policies in the original model,
     * return the split model in the result as a string.
     *
     * @param splitOutPolicies the policies form the original model to include in the split out model, specified as a
     *        comma delimited list of policy names
     * @return the result of the operation
     */
    ApexApiResult split(String splitOutPolicies);

    /**
     * Merge two Apex models together.
     *
     * @param mergeInModelName the file name of the model to merge into the current model
     * @param keepOriginal if this flag is set to true, if a concept exists in both models, the original model copy of
     *        that concept is kept, if the flag is set to false, then the copy of the concept from the mergeInModel
     *        overwrites the concept in the original model
     * @return the result of the operation
     */
    ApexApiResult merge(String mergeInModelName, boolean keepOriginal);

    /**
     * Merge two Apex models together.
     *
     * @param otherModelString the model to merge as a string
     * @param keepOriginal if this flag is set to true, if a concept exists in both models, the original model copy of
     *        that concept is kept, if the flag is set to false, then the copy of the concept from the mergeInModel
     *        overwrites the concept in the original model
     * @return the result of the operation
     */
    ApexApiResult mergeWithString(String otherModelString, boolean keepOriginal);

    /**
     * Get the raw policy model being used by this model.
     *
     * @return the policy model
     */
    AxPolicyModel getPolicyModel();

    /**
     * Set the raw policy model being used by this model.
     *
     * @param policyModel the policy model
     */
    void setPolicyModel(AxPolicyModel policyModel);

    /**
     * Builds the raw policy model being used by this model.
     *
     * @return the policy model
     */
    AxPolicyModel build();
}
