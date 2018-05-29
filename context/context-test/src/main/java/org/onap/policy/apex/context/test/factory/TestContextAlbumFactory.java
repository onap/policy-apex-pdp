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

package org.onap.policy.apex.context.test.factory;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * The Class TestContextAlbumFactory creates test context albums.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class TestContextAlbumFactory {
    /**
     * Default constructor to prevent sub-classing.
     */
    private TestContextAlbumFactory() {}

    /**
     * Creates the policy context model.
     *
     * @return the ax context model
     */
    public static AxContextModel createPolicyContextModel() {
        final AxContextSchema policySchema = new AxContextSchema(new AxArtifactKey("PolicySchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestPolicyContextItem");
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey("PolicyContextAlbum", "0.0.1"),
                "APPLICATION", true, policySchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(policySchema.getKey(), policySchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("PolicyContextModel", "0.0.1"), schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }

    /**
     * Creates the global context model.
     *
     * @return the ax context model
     */
    public static AxContextModel createGlobalContextModel() {
        final AxContextSchema globalSchema = new AxContextSchema(new AxArtifactKey("GlobalSchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestGlobalContextItem");
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey("GlobalContextAlbum", "0.0.1"),
                "GLOBAL", true, globalSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(globalSchema.getKey(), globalSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("GlobalContextModel", "0.0.1"), schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }

    /**
     * Creates the external context model.
     *
     * @return the ax context model
     */
    public static AxContextModel createExternalContextModel() {
        final AxContextSchema externalSchema = new AxContextSchema(new AxArtifactKey("ExternalSchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestExternalContextItem");
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey("ExternalContextAlbum", "0.0.1"),
                "EXTERNAL", true, externalSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(externalSchema.getKey(), externalSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("ExternalContextModel", "0.0.1"), schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }

    /**
     * Creates the long context model.
     *
     * @return the ax context model
     */
    public static AxContextModel createLongContextModel() {
        final AxContextSchema longSchema =
                new AxContextSchema(new AxArtifactKey("LongSchema", "0.0.1"), "Java", "java.lang.Long");
        final AxContextAlbum albumDefinition1 = new AxContextAlbum(new AxArtifactKey("LongContextAlbum1", "0.0.1"),
                "APPLICATION", true, longSchema.getKey());
        final AxContextAlbum albumDefinition2 = new AxContextAlbum(new AxArtifactKey("LongContextAlbum2", "0.0.1"),
                "APPLICATION", true, longSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(longSchema.getKey(), longSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(albumDefinition1.getKey(), albumDefinition1);
        albums.getAlbumsMap().put(albumDefinition2.getKey(), albumDefinition2);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("LongContextModel", "0.0.1"), schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }

    /**
     * Creates the multi albums context model.
     *
     * @return the ax context model
     */
    public static AxContextModel createMultiAlbumsContextModel() {
        final AxContextSchema longSchema =
                new AxContextSchema(new AxArtifactKey("LongSchema", "0.0.1"), "Java", "java.lang.Long");
        final AxContextSchema lTypeSchema = new AxContextSchema(new AxArtifactKey("LTypeSchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestContextItem003");
        final AxContextSchema dateSchema = new AxContextSchema(new AxArtifactKey("DateSchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestContextItem00A");
        final AxContextSchema mapSchema = new AxContextSchema(new AxArtifactKey("MapSchema", "0.0.1"), "Java",
                "org.onap.policy.apex.context.test.concepts.TestContextItem00C");

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", "0.0.1"));
        schemas.getSchemasMap().put(longSchema.getKey(), longSchema);
        schemas.getSchemasMap().put(lTypeSchema.getKey(), lTypeSchema);
        schemas.getSchemasMap().put(dateSchema.getKey(), dateSchema);
        schemas.getSchemasMap().put(mapSchema.getKey(), mapSchema);

        final AxContextAlbum longAlbumDefinition = new AxContextAlbum(new AxArtifactKey("LongContextAlbum", "0.0.1"),
                "APPLICATION", true, longSchema.getKey());
        final AxContextAlbum lTypeAlbumDefinition = new AxContextAlbum(new AxArtifactKey("LTypeContextAlbum", "0.0.1"),
                "APPLICATION", true, lTypeSchema.getKey());
        final AxContextAlbum dateAlbumDefinition = new AxContextAlbum(new AxArtifactKey("DateContextAlbum", "0.0.1"),
                "APPLICATION", true, dateSchema.getKey());
        final AxContextAlbum mapAlbumDefinition = new AxContextAlbum(new AxArtifactKey("MapContextAlbum", "0.0.1"),
                "APPLICATION", true, mapSchema.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(longAlbumDefinition.getKey(), longAlbumDefinition);
        albums.getAlbumsMap().put(lTypeAlbumDefinition.getKey(), lTypeAlbumDefinition);
        albums.getAlbumsMap().put(dateAlbumDefinition.getKey(), dateAlbumDefinition);
        albums.getAlbumsMap().put(mapAlbumDefinition.getKey(), mapAlbumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("MultiAlbumsContextModel", "0.0.1"),
                schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }
}
