/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Samsung Electronics Co., Ltd.
 *  Modifications Copyright (C) 2019,2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;

/**
 * This class acts as a facade for operations towards a policy model for context album operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextAlbumFacade {
    private static final String CONCEPT = "concept ";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    /**
     * Constructor that creates a context album facade for the Apex Model API.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     */
    public ContextAlbumFacade(final ApexModel apexModel, final Properties apexProperties) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties);
    }

    /**
     * Create a context album.
     *
     * @param builder the builder for the context album parameters
     * @return result of the operation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexApiResult createContextAlbum(ContextAlbum builder) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(builder.getName());
            if (builder.getVersion() != null) {
                key.setVersion(builder.getVersion());
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            if (apexModel.getPolicyModel().getAlbums().getAlbumsMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                    CONCEPT + key.getId() + " already exists");
            }

            final AxContextSchema schema = apexModel.getPolicyModel().getSchemas().get(builder.getContextSchemaName(),
                builder.getContextSchemaVersion());
            if (schema == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, CONCEPT
                    + builder.getContextSchemaName() + ':' + builder.getContextSchemaVersion() + DOES_NOT_EXIST);
            }

            final AxContextAlbum contextAlbum = new AxContextAlbum(key);
            contextAlbum.setScope(builder.getScope());
            contextAlbum.setItemSchema(schema.getKey());

            contextAlbum
                .setWritable(builder.getWritable() != null && ("true".equalsIgnoreCase(builder.getWritable().trim())
                    || "t".equalsIgnoreCase(builder.getWritable().trim())));

            apexModel.getPolicyModel().getAlbums().getAlbumsMap().put(key, contextAlbum);

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(builder.getName(), builder.getVersion(),
                    builder.getUuid(), builder.getDescription());
            } else {
                return keyInformationFacade.createKeyInformation(builder.getName(), builder.getVersion(),
                    builder.getUuid(), builder.getDescription());
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * Update a context album.
     *
     * @param builder the builder for the context album parameters
     * @return result of the operation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexApiResult updateContextAlbum(ContextAlbum builder) {
        try {
            final AxContextAlbum contextAlbum =
                apexModel.getPolicyModel().getAlbums().get(builder.getName(), builder.getVersion());
            if (contextAlbum == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + builder.getName() + ':' + builder.getVersion() + DOES_NOT_EXIST);
            }

            if (builder.getScope() != null) {
                contextAlbum.setScope(builder.getScope());
            }

            contextAlbum
                .setWritable(builder.getWritable() != null && ("true".equalsIgnoreCase(builder.getWritable().trim())
                    || "t".equalsIgnoreCase(builder.getWritable().trim())));

            if (builder.getContextSchemaName() != null) {
                final AxContextSchema schema = apexModel.getPolicyModel().getSchemas()
                    .get(builder.getContextSchemaName(), builder.getContextSchemaVersion());
                if (schema == null) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, CONCEPT
                        + builder.getContextSchemaName() + ':' + builder.getContextSchemaVersion() + DOES_NOT_EXIST);
                }
                contextAlbum.setItemSchema(schema.getKey());
            }

            return keyInformationFacade.updateKeyInformation(builder.getName(), builder.getVersion(), builder.getUuid(),
                builder.getDescription());
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * List context albums.
     *
     * @param name name of the context album, set to null to list all
     * @param version starting version of the context album, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult listContextAlbum(final String name, final String version) {
        try {
            final Set<AxContextAlbum> contextAlbumSet = apexModel.getPolicyModel().getAlbums().getAll(name, version);
            if (name != null && contextAlbumSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextAlbum contextAlbum : contextAlbumSet) {
                result.addMessage(
                    new ApexModelStringWriter<AxContextAlbum>(false).writeString(contextAlbum, AxContextAlbum.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a context album.
     *
     * @param name name of the context album
     * @param version version of the context album, set to null to delete versions
     * @return result of the operation
     */
    public ApexApiResult deleteContextAlbum(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                if (apexModel.getPolicyModel().getAlbums().getAlbumsMap().remove(key) != null) {
                    return new ApexApiResult();
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxContextAlbum> contextAlbumSet = apexModel.getPolicyModel().getAlbums().getAll(name, version);
            if (contextAlbumSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextAlbum contextAlbum : contextAlbumSet) {
                result.addMessage(
                    new ApexModelStringWriter<AxContextAlbum>(false).writeString(contextAlbum, AxContextAlbum.class));
                apexModel.getPolicyModel().getAlbums().getAlbumsMap().remove(contextAlbum.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate context albums.
     *
     * @param name name of the context album, set to null to list all
     * @param version starting version of the context album, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult validateContextAlbum(final String name, final String version) {
        try {
            final Set<AxContextAlbum> contextAlbumSet = apexModel.getPolicyModel().getAlbums().getAll(name, version);
            if (contextAlbumSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxContextAlbum contextAlbum : contextAlbumSet) {
                final AxValidationResult validationResult = contextAlbum.validate(new AxValidationResult());
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(contextAlbum.getKey(),
                    AxArtifactKey.class));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
}
