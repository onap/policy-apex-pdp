/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019,2022 Nordix Foundation.
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
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class acts as a facade for operations towards a policy model for context schema operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextSchemaFacade {
    private static final String CONCEPT = "concept ";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";
    private static final String ALREADY_EXISTS = " already exists";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    /**
     * Constructor to create the context schema facade for the Model API.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     */
    public ContextSchemaFacade(final ApexModel apexModel, final Properties apexProperties) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties);
    }

    /**
     * Create a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to use the default version
     * @param schemaFlavour a string identifying the flavour of this context schema
     * @param schemaDefinition a string containing the definition of this context schema
     * @param uuid context schema UUID, set to null to generate a UUID
     * @param description context schema description, set to null to generate a description
     * @return result of the operation
     */
    public ApexApiResult createContextSchema(final String name, final String version, final String schemaFlavour,
        final String schemaDefinition, final String uuid, final String description) {
        try {
            Assertions.argumentNotNull(schemaFlavour, "schemaFlavour may not be null");

            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            if (apexModel.getPolicyModel().getSchemas().getSchemasMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, CONCEPT + key.getId() + ALREADY_EXISTS);
            }

            apexModel.getPolicyModel().getSchemas().getSchemasMap().put(key,
                new AxContextSchema(key, schemaFlavour, schemaDefinition));

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
            } else {
                return keyInformationFacade.createKeyInformation(name, version, uuid, description);
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to update the latest version
     * @param schemaFlavour a string identifying the flavour of this context schema
     * @param schemaDefinition a string containing the definition of this context schema
     * @param uuid context schema UUID, set to null to not update
     * @param description context schema description, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updateContextSchema(final String name, final String version, final String schemaFlavour,
        final String schemaDefinition, final String uuid, final String description) {
        try {
            final AxContextSchema schema = apexModel.getPolicyModel().getSchemas().get(name, version);
            if (schema == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (schemaFlavour != null) {
                schema.setSchemaFlavour(schemaFlavour);
            }

            if (schemaDefinition != null) {
                schema.setSchema(schemaDefinition);
            }

            return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List context schemas.
     *
     * @param name name of the context schema, set to null to list all
     * @param version starting version of the context schema, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult listContextSchemas(final String name, final String version) {
        try {
            final Set<AxContextSchema> schemaSet = apexModel.getPolicyModel().getSchemas().getAll(name, version);
            if (name != null && schemaSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextSchema schema : schemaSet) {
                result.addMessage(
                    new ApexModelStringWriter<AxContextSchema>(false).writeString(schema, AxContextSchema.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to delete all versions
     * @return result of the operation
     */
    public ApexApiResult deleteContextSchema(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxContextSchema removedSchema =
                    apexModel.getPolicyModel().getSchemas().getSchemasMap().remove(key);
                if (removedSchema != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxContextSchema>(false).writeString(removedSchema,
                            AxContextSchema.class));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxContextSchema> schemaSet = apexModel.getPolicyModel().getSchemas().getAll(name, version);
            if (schemaSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextSchema schema : schemaSet) {
                result.addMessage(
                    new ApexModelStringWriter<AxContextSchema>(false).writeString(schema, AxContextSchema.class));
                apexModel.getPolicyModel().getSchemas().getSchemasMap().remove(schema.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate context schemas.
     *
     * @param name name of the context schema, set to null to list all
     * @param version starting version of the context schema, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult validateContextSchemas(final String name, final String version) {
        try {
            final Set<AxContextSchema> schemaSet = apexModel.getPolicyModel().getSchemas().getAll(name, version);
            if (schemaSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextSchema schema : schemaSet) {
                final AxValidationResult validationResult = schema.validate(new AxValidationResult());
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(schema.getKey(), AxArtifactKey.class));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
}
