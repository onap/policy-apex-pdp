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

package org.onap.policy.apex.context.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.parameters.ParameterService;

public class ContextAlbumImplTest {
    /**
     * Set ups everything for the test.
     */
    @BeforeClass
    public static void prepareForTest() {
        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters()
                        .setPluginClass("org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager");

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    /**
     * Clear down the test data.
     */
    @AfterClass
    public static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    public void testNullsOnConstructor() {
        try {
            new ContextAlbumImpl(null, null, null);
            fail("this test should throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Context album definition may not be null", e.getMessage());
        } catch (ContextException e) {
            fail("this test should throw an IllegalArgumentException");
        }

        try {
            new ContextAlbumImpl(new AxContextAlbum(), null, null);
            fail("this test should throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Distributor may not be null", e.getMessage());
        } catch (ContextException e) {
            fail("this test should throw an IllegalArgumentException");
        }

        try {
            new ContextAlbumImpl(new AxContextAlbum(), new JvmLocalDistributor(), null);
            fail("this test should throw an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Album map may not be null", e.getMessage());
        } catch (ContextException e) {
            fail("this test should throw an IllegalArgumentException");
        }

        try {
            new ContextAlbumImpl(new AxContextAlbum(), new JvmLocalDistributor(), new LinkedHashMap<String, Object>());
            fail("this test should throw an exception");
        } catch (ApexRuntimeException e) {
            assertEquals("Model for org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas "
                    + "not found in model service", e.getMessage());
        } catch (ContextException e) {
            fail("this test should throw an ApexRuntimeException");
        }
    }

    @Test
    public void testAlbumInterface() throws ContextException {
        AxContextSchemas schemas = new AxContextSchemas();
        AxContextSchema simpleStringSchema = new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"),
                        "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxContextAlbum axContextAlbum = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
                        true, AxArtifactKey.getNullKey());

        try {
            new ContextAlbumImpl(axContextAlbum, new JvmLocalDistributor(), new LinkedHashMap<String, Object>());
            fail("this test should throw an exception");
        } catch (ContextException e) {
            assertEquals("could not initiate schema management for context album AxContextAlbum",
                            e.getMessage().substring(0, 69));
        }

        axContextAlbum.setItemSchema(simpleStringSchema.getKey());
        Distributor distributor = new JvmLocalDistributor();
        distributor.init(axContextAlbum.getKey());
        ContextAlbum album = new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<String, Object>());

        AxContextAlbum axContextAlbumRo = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
                false, simpleStringSchema.getKey());
        ContextAlbum albumRo = new ContextAlbumImpl(axContextAlbumRo, distributor, new LinkedHashMap<String, Object>());

        assertEquals("TestContextAlbum", album.getName());
        assertEquals("TestContextAlbum:0.0.1", album.getKey().getId());
        assertEquals("TestContextAlbum:0.0.1", album.getAlbumDefinition().getId());
        assertEquals("SimpleStringSchema:0.0.1", album.getSchemaHelper().getSchema().getId());

        try {
            album.containsKey(null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("null values are illegal on method parameter \"key\"", e.getMessage());
        }
        assertEquals(false, album.containsKey("Key0"));

        try {
            album.containsValue(null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("null values are illegal on method parameter \"value\"", e.getMessage());
        }
        assertEquals(false, album.containsValue("some value"));

        try {
            album.get(null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" null keys are illegal on keys for get()", e.getMessage());
        }

        try {
            album.put(null, null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" null keys are illegal on keys for put()", e.getMessage());
        }

        try {
            album.put("KeyNull", null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" null values are illegal on key \"KeyNull\" for put()",
                            e.getMessage());
        }

        try {
            albumRo.put("KeyReadOnly", "A value for a Read Only Album");
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" put() not allowed on read only albums "
                            + "for key=\"KeyReadOnly\", value=\"A value for a Read Only Album", e.getMessage());
        }

        Map<String, Object> putAllData = new LinkedHashMap<>();
        putAllData.put("AllKey0", "vaue of AllKey0");
        putAllData.put("AllKey1", "vaue of AllKey1");
        putAllData.put("AllKey2", "vaue of AllKey2");

        try {
            albumRo.putAll(putAllData);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" putAll() not allowed on read only albums", e.getMessage());
        }

        try {
            albumRo.remove("AllKey0");
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals(
                    "album \"TestContextAlbum:0.0.1\" remove() not allowed on read only albums for key=\"AllKey0\"",
                    e.getMessage());
        }

        try {
            album.remove(null);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("null values are illegal on method parameter \"keyID\"", e.getMessage());
        }

        try {
            albumRo.clear();
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" clear() not allowed on read only albums", e.getMessage());
        }

        // The following locking tests pass because the locking protects access to Key0 across all
        // copies of the distributed album whether the key exists or not
        album.lockForReading("Key0");
        assertEquals(null, album.get("Key0"));
        album.unlockForReading("Key0");
        assertEquals(null, album.get("Key0"));

        album.lockForWriting("Key0");
        assertEquals(null, album.get("Key0"));
        album.unlockForWriting("Key0");
        assertEquals(null, album.get("Key0"));

        // Test write access, trivial test because Integration Test does
        // a full test of access locking over albums in different JVMs
        album.lockForWriting("Key0");
        assertEquals(null, album.get("Key0"));
        album.put("Key0", "value of Key0");
        assertEquals("value of Key0", album.get("Key0"));
        album.unlockForWriting("Key0");
        assertEquals("value of Key0", album.get("Key0"));

        // Test read access, trivial test because Integration Test does
        // a full test of access locking over albums in different JVMs
        album.lockForReading("Key0");
        assertEquals("value of Key0", album.get("Key0"));
        album.unlockForReading("Key0");

        AxArtifactKey somePolicyKey = new AxArtifactKey("MyPolicy", "0.0.1");
        AxReferenceKey somePolicyState = new AxReferenceKey(somePolicyKey, "SomeState");

        AxConcept[] userArtifactStack = { somePolicyKey, somePolicyState };
        album.setUserArtifactStack(userArtifactStack);
        assertEquals("MyPolicy:0.0.1", album.getUserArtifactStack()[0].getId());
        assertEquals("MyPolicy:0.0.1:NULL:SomeState", album.getUserArtifactStack()[1].getId());

        assertEquals(true, album.keySet().contains("Key0"));
        assertEquals(true, album.values().contains("value of Key0"));
        assertEquals(1, album.entrySet().size());

        // The flush() operation fails because the distributor is not initialized with the album which
        // is fine for unit test
        try {
            album.flush();
            fail("test should throw an exception");
        } catch (ContextException e) {
            assertEquals("map flush failed, supplied map is null", e.getMessage());
        }

        assertEquals(1, album.size());
        assertEquals(false, album.isEmpty());

        album.put("Key0", "New value of Key0");
        assertEquals("New value of Key0", album.get("Key0"));

        album.putAll(putAllData);

        putAllData.put("AllKey3", null);
        try {
            album.putAll(putAllData);
            fail("test should throw an exception");
        } catch (ContextRuntimeException e) {
            assertEquals("album \"TestContextAlbum:0.0.1\" null values are illegal on key \"AllKey3\" for put()",
                            e.getMessage());
        }

        assertEquals("New value of Key0", album.remove("Key0"));
        
        album.clear();
        assertTrue(album.isEmpty());
        
        ModelService.clear();
    }
}
