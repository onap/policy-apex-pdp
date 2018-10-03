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

package org.onap.policy.apex.core.engine.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the Apex engine internal context class.
 */
public class ApexInternalContextTest {

    private AxPolicyModel policyModel;
    private AxPolicyModel newVersionPolicyModel;
    private AxPolicyModel newPolicyModel;
    private AxContextAlbum album;
    private AxContextAlbum newAlbum;
    private AxPolicyModel incompatiblePolicyModel;

    /**
     * Initialize parameters.
     */
    @Before
    public void registerParameters() {
        ParameterService.register(new SchemaParameters());
        ParameterService.register(new DistributorParameters());
        ParameterService.register(new LockManagerParameters());
        ParameterService.register(new PersistorParameters());
    }

    /**
     * Create policy model.
     */
    @Before
    public void createPolicyModels() {
        AxArtifactKey modelKey = new AxArtifactKey("PolicyModel:0.0.1");
        policyModel = new AxPolicyModel(modelKey);

        AxArtifactKey schemaKey = new AxArtifactKey("Schema:0.0.1");
        AxContextSchema schema = new AxContextSchema(schemaKey, "Java", "java.lang.String");
        policyModel.getSchemas().getSchemasMap().put(schemaKey, schema);

        AxArtifactKey albumKey = new AxArtifactKey("Album:0.0.1");
        album = new AxContextAlbum(albumKey, "Policy", true, schemaKey);

        policyModel.getAlbums().getAlbumsMap().put(albumKey, album);

        AxArtifactKey newVersionModelKey = new AxArtifactKey("PolicyModel:0.0.2");
        newVersionPolicyModel = new AxPolicyModel(newVersionModelKey);

        newVersionPolicyModel.getSchemas().getSchemasMap().put(schemaKey, schema);
        AxContextAlbum compatibleAlbum = new AxContextAlbum(albumKey, "Global", true, schemaKey);
        newVersionPolicyModel.getAlbums().getAlbumsMap().put(albumKey, compatibleAlbum);

        AxArtifactKey anotherAlbumKey = new AxArtifactKey("AnotherAlbum:0.0.1");
        AxContextAlbum anotherAlbum = new AxContextAlbum(anotherAlbumKey, "Policy", true, schemaKey);

        newVersionPolicyModel.getAlbums().getAlbumsMap().put(anotherAlbumKey, anotherAlbum);

        AxArtifactKey incompatibleModelKey = new AxArtifactKey("IncompatiblePolicyModel:0.0.2");
        incompatiblePolicyModel = new AxPolicyModel(incompatibleModelKey);

        AxArtifactKey incompatibleSchemaKey = new AxArtifactKey("IncompatibleSchema:0.0.1");
        AxContextSchema incompatibleSchema = new AxContextSchema(incompatibleSchemaKey, "Java", "java.lang.Integer");
        incompatiblePolicyModel.getSchemas().getSchemasMap().put(incompatibleSchemaKey, incompatibleSchema);

        AxContextAlbum incompatibleAlbum = new AxContextAlbum(albumKey, "Policy", true, incompatibleSchemaKey);
        incompatiblePolicyModel.getAlbums().getAlbumsMap().put(albumKey, incompatibleAlbum);

        AxArtifactKey newModelKey = new AxArtifactKey("NewPolicyModel:0.0.1");
        newPolicyModel = new AxPolicyModel(newModelKey);

        AxArtifactKey newSchemaKey = new AxArtifactKey("NewSchema:0.0.1");
        AxContextSchema newSchema = new AxContextSchema(newSchemaKey, "Java", "java.lang.Integer");
        newPolicyModel.getSchemas().getSchemasMap().put(newSchemaKey, newSchema);

        AxArtifactKey newAlbumKey = new AxArtifactKey("NewAlbum:0.0.1");
        newAlbum = new AxContextAlbum(newAlbumKey, "Policy", true, newSchemaKey);

        newPolicyModel.getAlbums().getAlbumsMap().put(newAlbumKey, newAlbum);
    }

    /**
     * Deregister parameters.
     */
    @After
    public void deregisterParameters() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
    }

    @Test
    public void testAlbumInit() throws ContextException {
        try {
            new ApexInternalContext(null);
            fail("test should throw an exception");
        } catch (ContextException ce) {
            assertEquals("internal context update failed, supplied model is null", ce.getMessage());
        }

        ApexInternalContext context = new ApexInternalContext(policyModel);

        assertEquals(policyModel.getKey(), context.getKey());
        assertEquals(1, context.getContextAlbums().size());

        AxArtifactKey albumKey = new AxArtifactKey("Album:0.0.1");
        assertEquals(album.getId(), context.get(albumKey).getKey().getId());
        assertEquals(album.getId(), context.get(albumKey.getName()).getKey().getId());
        assertEquals(album.getId(), context.get(albumKey.getName(), albumKey.getVersion()).getKey().getId());
        assertEquals(album.getId(), context.getAll(albumKey.getName()).iterator().next().getKey().getId());
        assertEquals(album.getId(),
                        context.getAll(albumKey.getName(), albumKey.getVersion()).iterator().next().getKey().getId());

        context.clear();
        assertEquals(1, context.getContextAlbums().size());

        assertEquals("ApexInternalContext [contextAlbums={AxArtifactKey:(name=Album,version=0.0.1)",
                        context.toString().substring(0, 76));
    }

    @Test
    public void testAlbumUpdate() throws ContextException {
        ApexInternalContext context = new ApexInternalContext(policyModel);

        try {
            context.update(null);
            fail("test should throw an exception");
        } catch (ContextException ce) {
            assertEquals("internal context update failed, supplied model is null", ce.getMessage());
        }

        assertEquals(policyModel.getKey().getId(), context.getKey().getId());
        assertEquals(1, context.getContextAlbums().size());

        try {
            context.update(incompatiblePolicyModel);
            fail("test should throw an exception here");
        } catch (ContextException ce) {
            assertEquals("internal context update failed on context album \"Album:0.0.1\" "
                            + "in model \"PolicyModel:0.0.1\", "
                            + "schema \"Schema:0.0.1\" on existing context model does not equal "
                            + "schema \"IncompatibleSchema:0.0.1\" on incoming model", ce.getMessage());
        }

        assertEquals(policyModel.getKey().getId(), context.getKey().getId());

        context.update(newVersionPolicyModel);
        assertEquals(newVersionPolicyModel.getKey().getId(), context.getKey().getId());

        context.update(newPolicyModel);
        assertEquals(newPolicyModel.getKey().getId(), context.getKey().getId());
    }
}
