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

package org.onap.policy.apex.common.test.factory;

import static org.onap.policy.apex.common.test.distribution.Constants.DATE_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.EXTERNAL_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.GLOBAL_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.LONG_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.MAP_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.POLICY_CONTEXT_ALBUM;
import static org.onap.policy.apex.common.test.distribution.Constants.VERSION;

import org.onap.policy.apex.common.test.concepts.TestContextDateLocaleItem;
import org.onap.policy.apex.common.test.concepts.TestContextLongItem;
import org.onap.policy.apex.common.test.concepts.TestContextTreeMapItem;
import org.onap.policy.apex.common.test.concepts.TestExternalContextItem;
import org.onap.policy.apex.common.test.concepts.TestGlobalContextItem;
import org.onap.policy.apex.common.test.concepts.TestPolicyContextItem;
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

    private static final String APPLICATION = "APPLICATION";
    private static final String JAVA_LONG = Long.class.getCanonicalName();
    private static final String JAVA_FLAVOUR = "Java";

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
        final AxContextSchema policySchema = new AxContextSchema(new AxArtifactKey("PolicySchema", VERSION),
                JAVA_FLAVOUR, TestPolicyContextItem.class.getCanonicalName());
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey(POLICY_CONTEXT_ALBUM, VERSION),
                APPLICATION, true, policySchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", VERSION));
        schemas.getSchemasMap().put(policySchema.getKey(), policySchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", VERSION));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", VERSION));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("PolicyContextModel", VERSION), schemas, albums, keyInformation);
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
        final AxContextSchema globalSchema = new AxContextSchema(new AxArtifactKey("GlobalSchema", VERSION),
                JAVA_FLAVOUR, TestGlobalContextItem.class.getCanonicalName());
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey(GLOBAL_CONTEXT_ALBUM, VERSION),
                "GLOBAL", true, globalSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", VERSION));
        schemas.getSchemasMap().put(globalSchema.getKey(), globalSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", VERSION));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", VERSION));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("GlobalContextModel", VERSION), schemas, albums, keyInformation);
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
        final AxContextSchema externalSchema = new AxContextSchema(new AxArtifactKey("ExternalSchema", VERSION),
                JAVA_FLAVOUR, TestExternalContextItem.class.getCanonicalName());
        final AxContextAlbum albumDefinition = new AxContextAlbum(new AxArtifactKey(EXTERNAL_CONTEXT_ALBUM, VERSION),
                "EXTERNAL", true, externalSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", VERSION));
        schemas.getSchemasMap().put(externalSchema.getKey(), externalSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", VERSION));
        albums.getAlbumsMap().put(albumDefinition.getKey(), albumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", VERSION));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("ExternalContextModel", VERSION), schemas, albums, keyInformation);
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
        final AxArtifactKey longSchemaKey = new AxArtifactKey("LongSchema", VERSION);
        final AxContextSchema longSchema = new AxContextSchema(longSchemaKey, JAVA_FLAVOUR, JAVA_LONG);

        final AxArtifactKey longContextAlbumKey = new AxArtifactKey("LongContextAlbum1", VERSION);
        final AxContextAlbum albumDefinition1 =
                new AxContextAlbum(longContextAlbumKey, APPLICATION, true, longSchema.getKey());

        final AxArtifactKey longContextAlbumKey2 = new AxArtifactKey("LongContextAlbum2", VERSION);
        final AxContextAlbum albumDefinition2 =
                new AxContextAlbum(longContextAlbumKey2, APPLICATION, true, longSchema.getKey());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", VERSION));
        schemas.getSchemasMap().put(longSchema.getKey(), longSchema);
        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", VERSION));
        albums.getAlbumsMap().put(albumDefinition1.getKey(), albumDefinition1);
        albums.getAlbumsMap().put(albumDefinition2.getKey(), albumDefinition2);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", VERSION));
        final AxContextModel contextModel =
                new AxContextModel(new AxArtifactKey("LongContextModel", VERSION), schemas, albums, keyInformation);
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
                new AxContextSchema(new AxArtifactKey("LongSchema", VERSION), JAVA_FLAVOUR, JAVA_LONG);
        final AxContextSchema lTypeSchema = new AxContextSchema(new AxArtifactKey("LTypeSchema", VERSION), JAVA_FLAVOUR,
                TestContextLongItem.class.getCanonicalName());
        final AxContextSchema dateSchema = new AxContextSchema(new AxArtifactKey("DateSchema", VERSION), JAVA_FLAVOUR,
                TestContextDateLocaleItem.class.getCanonicalName());
        final AxContextSchema mapSchema = new AxContextSchema(new AxArtifactKey("MapSchema", VERSION), JAVA_FLAVOUR,
                TestContextTreeMapItem.class.getCanonicalName());

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("Schemas", VERSION));
        schemas.getSchemasMap().put(longSchema.getKey(), longSchema);
        schemas.getSchemasMap().put(lTypeSchema.getKey(), lTypeSchema);
        schemas.getSchemasMap().put(dateSchema.getKey(), dateSchema);
        schemas.getSchemasMap().put(mapSchema.getKey(), mapSchema);

        final AxContextAlbum longAlbumDefinition = new AxContextAlbum(new AxArtifactKey(LONG_CONTEXT_ALBUM, VERSION),
                APPLICATION, true, longSchema.getKey());
        final AxContextAlbum lTypeAlbumDefinition = new AxContextAlbum(new AxArtifactKey("LTypeContextAlbum", VERSION),
                APPLICATION, true, lTypeSchema.getKey());
        final AxContextAlbum dateAlbumDefinition = new AxContextAlbum(new AxArtifactKey(DATE_CONTEXT_ALBUM, VERSION),
                APPLICATION, true, dateSchema.getKey());
        final AxContextAlbum mapAlbumDefinition = new AxContextAlbum(new AxArtifactKey(MAP_CONTEXT_ALBUM, VERSION),
                APPLICATION, true, mapSchema.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", VERSION));
        albums.getAlbumsMap().put(longAlbumDefinition.getKey(), longAlbumDefinition);
        albums.getAlbumsMap().put(lTypeAlbumDefinition.getKey(), lTypeAlbumDefinition);
        albums.getAlbumsMap().put(dateAlbumDefinition.getKey(), dateAlbumDefinition);
        albums.getAlbumsMap().put(mapAlbumDefinition.getKey(), mapAlbumDefinition);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", VERSION));
        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("MultiAlbumsContextModel", VERSION),
                schemas, albums, keyInformation);
        contextModel.setKeyInformation(keyInformation);
        keyInformation.generateKeyInfo(contextModel);

        return contextModel;
    }

}
