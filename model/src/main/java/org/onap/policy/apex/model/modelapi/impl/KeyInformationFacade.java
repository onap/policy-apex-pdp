/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;

/**
 * This class acts as a facade for operations towards a policy model for key information operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@AllArgsConstructor
public class KeyInformationFacade {
    private static final String CONCEPT = "concept ";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";
    private static final String ALREADY_EXISTS = " already exists";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    /**
     * Create key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to use the default
     *        version
     * @param uuid key information UUID, set to null to generate a UUID
     * @param description key information description, set to null to generate a description
     * @return result of the operation
     */
    public ApexApiResult createKeyInformation(final String name, final String version, final String uuid,
        final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, CONCEPT + key.getId() + ALREADY_EXISTS);
            }

            final AxKeyInfo keyInfo = new AxKeyInfo(key);
            if (description != null) {
                keyInfo.setDescription(description);
            }
            if (uuid != null) {
                keyInfo.setUuid(UUID.fromString(uuid));
            } else {
                // generate a reproducible UUID
                keyInfo.setUuid(AxKeyInfo.generateReproducibleUuid(keyInfo.getId() + keyInfo.getDescription()));
            }
            apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().put(key, keyInfo);
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to update the
     *        latest version
     * @param uuid key information UUID, set to null to not update
     * @param description key information description, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updateKeyInformation(final String name, final String version, final String uuid,
        final String description) {
        try {
            final AxKeyInfo keyInfo = apexModel.getPolicyModel().getKeyInformation().get(name, version);
            if (keyInfo == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ":" + version + DOES_NOT_EXIST);
            }

            if (description != null) {
                keyInfo.setDescription(description);
            }

            if (uuid != null) {
                keyInfo.setUuid(UUID.fromString(uuid));
            } else {
                // generate a reproducible UUID
                keyInfo.setUuid(AxKeyInfo.generateReproducibleUuid(keyInfo.getId() + keyInfo.getDescription()));
            }

            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List key information.
     *
     * @param name name of the concept for the key information, set to null to list all
     * @param version starting version of the concept for the key information, set to null to list
     *        all versions
     * @return result of the operation
     */
    public ApexApiResult listKeyInformation(final String name, final String version) {
        try {
            final Set<AxKeyInfo> keyInfoSet = apexModel.getPolicyModel().getKeyInformation().getAll(name, version);
            if (name != null && keyInfoSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxKeyInfo keyInfo : keyInfoSet) {
                result.addMessage(new ApexModelStringWriter<AxKeyInfo>(false).writeString(keyInfo, AxKeyInfo.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to delete all
     *        versions
     * @return result of the operation
     */
    public ApexApiResult deleteKeyInformation(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxKeyInfo removedKeyInfo =
                    apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().remove(key);
                if (removedKeyInfo != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxKeyInfo>(false).writeString(removedKeyInfo, AxKeyInfo.class));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxKeyInfo> keyInfoSet = apexModel.getPolicyModel().getKeyInformation().getAll(name, version);
            if (keyInfoSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxKeyInfo keyInfo : keyInfoSet) {
                result.addMessage(new ApexModelStringWriter<AxKeyInfo>(false).writeString(keyInfo, AxKeyInfo.class));
                apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().remove(keyInfo.getKey());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to validate all
     *        versions
     * @return result of the operation
     */
    public ApexApiResult validateKeyInformation(final String name, final String version) {
        try {
            final Set<AxKeyInfo> keyInfoSet = apexModel.getPolicyModel().getKeyInformation().getAll(name, version);
            if (keyInfoSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxKeyInfo keyInfo : keyInfoSet) {
                final AxValidationResult validationResult = keyInfo.validate(new AxValidationResult());
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyInfo.getKey(), AxArtifactKey.class));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

}
