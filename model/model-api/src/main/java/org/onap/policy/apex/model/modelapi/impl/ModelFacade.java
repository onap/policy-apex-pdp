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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Properties;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class acts as a facade for operations towards a policy model.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ModelFacade {
    private static final String CONCEPT = "concept ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String ALREADY_CREATED = " already created";
    private static final String NO_VERSION_SPECIFIED = ", no version specified";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    // JSON output on list/delete if set
    private final boolean jsonMode;

    /**
     * Constructor to create a model facade for the Apex model.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public ModelFacade(final ApexModel apexModel, final Properties apexProperties, final boolean jsonMode) {
        Assertions.argumentNotNull(apexModel, "apexModel may not be null");
        Assertions.argumentNotNull(apexProperties, "apexProperties may not be null");

        this.apexModel = apexModel;
        this.apexProperties = apexProperties;
        this.jsonMode = jsonMode;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties, jsonMode);
    }

    /**
     * Create model.
     *
     * @param name name of the model
     * @param version version of the model, set to null to use the default version
     * @param uuid model UUID, set to null to generate a UUID
     * @param description model description, set to null to generate a description
     * @return result of the operation
     */
    public ApexAPIResult createModel(final String name, final String version, final String uuid,
            final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                final String defaultVersion = apexProperties.getProperty("DEFAULT_CONCEPT_VERSION");
                if (defaultVersion != null) {
                    key.setVersion(defaultVersion);
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, CONCEPT + name + NO_VERSION_SPECIFIED);
                }
            }

            if (!apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                        CONCEPT + apexModel.getPolicyModel().getKey().getId() + ALREADY_CREATED);
            }

            apexModel.setPolicyModel(new AxPolicyModel(key));

            ApexAPIResult result;

            result = keyInformationFacade.createKeyInformation(name, version, uuid, description);
            if (result.getResult().equals(ApexAPIResult.RESULT.SUCCESS)) {
                apexModel.getPolicyModel().getKeyInformation().generateKeyInfo(apexModel.getPolicyModel());
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Update model.
     *
     * @param name name of the model
     * @param version version of the model, set to null to update the latest version
     * @param uuid key information UUID, set to null to not update
     * @param description policy description, set to null to not update
     * @return result of the operation
     */
    public ApexAPIResult updateModel(final String name, final String version, final String uuid,
            final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                final String defaultVersion = apexProperties.getProperty("DEFAULT_CONCEPT_VERSION");
                if (defaultVersion != null) {
                    key.setVersion(defaultVersion);
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                            CONCEPT + apexModel.getPolicyModel().getKey().getId() + NO_VERSION_SPECIFIED);
                }
            }

            if (apexModel.getPolicyModel().getKey().equals(AxArtifactKey.getNullKey())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + apexModel.getPolicyModel().getKey().getId() + DOES_NOT_EXIST);
            }

            return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Get the key of an Apex model.
     *
     * @return the result of the operation
     */
    public ApexAPIResult getModelKey() {
        try {
            final ApexAPIResult result = new ApexAPIResult();
            final AxArtifactKey modelkey = apexModel.getPolicyModel().getKey();
            result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(modelkey, AxArtifactKey.class,
                    jsonMode));
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List an Apex model.
     *
     * @return the result of the operation
     */
    public ApexAPIResult listModel() {
        try {
            final ApexAPIResult result = new ApexAPIResult();
            result.addMessage(new ApexModelStringWriter<AxPolicyModel>(false).writeString(apexModel.getPolicyModel(),
                    AxPolicyModel.class, jsonMode));
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete an Apex model, clear all the concepts in the model.
     *
     * @return the result of the operation
     */
    public ApexAPIResult deleteModel() {
        // @formatter:off
        apexModel.getPolicyModel().getSchemas()       .getSchemasMap() .clear();
        apexModel.getPolicyModel().getEvents()        .getEventMap()   .clear();
        apexModel.getPolicyModel().getAlbums()        .getAlbumsMap()  .clear();
        apexModel.getPolicyModel().getTasks()         .getTaskMap()    .clear();
        apexModel.getPolicyModel().getPolicies()      .getPolicyMap()  .clear();
        apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap() .clear();
        // @formatter:on

        apexModel.setPolicyModel(new AxPolicyModel());

        return new ApexAPIResult();
    }
}
