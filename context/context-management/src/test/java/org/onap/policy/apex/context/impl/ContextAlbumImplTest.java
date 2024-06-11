/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.common.parameters.ParameterService;

class ContextAlbumImplTest {
    /**
     * Set-ups everything for the test.
     */
    @BeforeAll
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
    @AfterAll
    public static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    void testNullsOnConstructor() {
        assertThatThrownBy(() -> new ContextAlbumImpl(null, null, null))
            .hasMessage("Context album definition may not be null");

        assertThatThrownBy(() -> new ContextAlbumImpl(new AxContextAlbum(), null, null))
            .hasMessage("Distributor may not be null");

        assertThatThrownBy(() -> new ContextAlbumImpl(new AxContextAlbum(), new JvmLocalDistributor(), null))
            .hasMessage("Album map may not be null");

        assertThatThrownBy(() -> new ContextAlbumImpl(new AxContextAlbum(), new JvmLocalDistributor(),
            new LinkedHashMap<>()))
            .hasMessage("Model for org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas "
                + "not found in model service");
    }

    @Test
    void testAlbumInterface() throws ContextException {
        AxContextSchemas schemas = new AxContextSchemas();
        AxContextSchema simpleStringSchema = new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"),
            "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxContextAlbum axContextAlbum = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
            true, AxArtifactKey.getNullKey());

        assertThatThrownBy(
            () -> new ContextAlbumImpl(axContextAlbum, new JvmLocalDistributor(), new LinkedHashMap<>()))
            .hasMessageContaining("could not initiate schema management for context album AxContextAlbum");

        axContextAlbum.setItemSchema(simpleStringSchema.getKey());
        Distributor distributor = new JvmLocalDistributor();
        distributor.init(axContextAlbum.getKey());
        ContextAlbum album = new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<>());

        assertGetValuesFromContextAlbum(album);

        assertThatThrownBy(() -> album.containsKey(null))
            .hasMessage("null values are illegal on method parameter \"key\"");
        assertFalse(album.containsKey("Key0"));

        assertUsingNullValues_RaiseExceptions(album);

        AxContextAlbum axContextAlbumRo = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
            false, simpleStringSchema.getKey());
        ContextAlbum albumRo = new ContextAlbumImpl(axContextAlbumRo, distributor, new LinkedHashMap<>());

        assertThatThrownBy(() -> albumRo.put("KeyReadOnly", "A value for a Read Only Album"))
            .hasMessage("album \"TestContextAlbum:0.0.1\" put() not allowed on read only albums "
                + "for key=\"KeyReadOnly\", value=\"A value for a Read Only Album");

        Map<String, Object> putAllData = new LinkedHashMap<>();
        putAllData.put("AllKey0", "vaue of AllKey0");
        putAllData.put("AllKey1", "vaue of AllKey1");
        putAllData.put("AllKey2", "vaue of AllKey2");

        assertThatThrownBy(() -> albumRo.putAll(putAllData))
            .hasMessage("album \"TestContextAlbum:0.0.1\" putAll() not allowed on read only albums");

        assertThatThrownBy(() -> albumRo.remove("AllKey0")).hasMessage(
            "album \"TestContextAlbum:0.0.1\" remove() not allowed " + "on read only albums for key=\"AllKey0\"");

        assertThatThrownBy(() -> album.remove(null))
            .hasMessage("null values are illegal on method parameter \"keyID\"");

        assertThatThrownBy(albumRo::clear)
            .hasMessage("album \"TestContextAlbum:0.0.1\" clear() not allowed on read only albums");

        assertLockingUnlocking(album);

        AxArtifactKey somePolicyKey = new AxArtifactKey("MyPolicy", "0.0.1");
        AxReferenceKey somePolicyState = new AxReferenceKey(somePolicyKey, "SomeState");

        AxConcept[] userArtifactStack = {somePolicyKey, somePolicyState};
        album.setUserArtifactStack(userArtifactStack);
        assertEquals("MyPolicy:0.0.1", album.getUserArtifactStack()[0].getId());
        assertEquals("MyPolicy:0.0.1:NULL:SomeState", album.getUserArtifactStack()[1].getId());

        assertTrue(album.containsKey("Key0"));
        assertTrue(album.containsValue("value of Key0"));
        assertEquals(1, album.entrySet().size());

        // The flush() operation fails because the distributor is not initialized with
        // the album which is fine for unit test
        assertThatThrownBy(album::flush).hasMessage("map flush failed, supplied map is null");
        assertEquals(1, album.size());
        assertFalse(album.isEmpty());

        album.put("Key0", "New value of Key0");
        assertEquals("New value of Key0", album.get("Key0"));

        album.putAll(putAllData);

        putAllData.put("AllKey3", null);
        assertThatThrownBy(() -> album.putAll(putAllData))
            .hasMessage("album \"TestContextAlbum:0.0.1\" null values are illegal on key " + "\"AllKey3\" for put()");
        assertEquals("New value of Key0", album.remove("Key0"));

        album.clear();

        ModelService.clear();
    }

    private static void assertUsingNullValues_RaiseExceptions(ContextAlbum album) {
        assertThatThrownBy(() -> album.containsValue(null))
            .hasMessage("null values are illegal on method parameter \"value\"");
        assertFalse(album.containsValue("some value"));

        assertThatThrownBy(() -> album.get(null))
            .hasMessage("album \"TestContextAlbum:0.0.1\" null keys are illegal on keys for get()");

        assertThatThrownBy(() -> album.put(null, null))
            .hasMessage("album \"TestContextAlbum:0.0.1\" null keys are illegal on keys for put()");

        assertThatThrownBy(() -> album.put("KeyNull", null))
            .hasMessage("album \"TestContextAlbum:0.0.1\" null values are illegal on key \"KeyNull\"" + " for put()");
    }

    private static void assertGetValuesFromContextAlbum(ContextAlbum album) {
        assertEquals("TestContextAlbum", album.getName());
        assertEquals("TestContextAlbum:0.0.1", album.getKey().getId());
        assertEquals("TestContextAlbum:0.0.1", album.getAlbumDefinition().getId());
        assertEquals("SimpleStringSchema:0.0.1", album.getSchemaHelper().getSchema().getId());
    }

    private static void assertLockingUnlocking(ContextAlbum album) throws ContextException {
        // The following locking tests pass because the locking protects access to Key0
        // across all
        // copies of the distributed album whether the key exists or not
        album.lockForReading("Key0");
        assertNull(album.get("Key0"));
        album.unlockForReading("Key0");
        assertNull(album.get("Key0"));

        album.lockForWriting("Key0");
        assertNull(album.get("Key0"));
        album.unlockForWriting("Key0");
        assertNull(album.get("Key0"));

        // Test write access, trivial test because Integration Test does
        // a full test of access locking over albums in different JVMs
        album.lockForWriting("Key0");
        assertNull(album.get("Key0"));
        album.put("Key0", "value of Key0");
        assertEquals("value of Key0", album.get("Key0"));
        album.unlockForWriting("Key0");
        assertEquals("value of Key0", album.get("Key0"));

        // Test read access, trivial test because Integration Test does
        // a full test of access locking over albums in different JVMs
        album.lockForReading("Key0");
        assertEquals("value of Key0", album.get("Key0"));
        album.unlockForReading("Key0");
    }

    @Test
    void testCompareToEqualsHash() throws ContextException {
        AxContextSchemas schemas = new AxContextSchemas();
        AxContextSchema simpleIntSchema = new AxContextSchema(new AxArtifactKey("SimpleIntSchema", "0.0.1"), "JAVA",
            "java.lang.Integer");
        schemas.getSchemasMap().put(simpleIntSchema.getKey(), simpleIntSchema);
        AxContextSchema simpleStringSchema = new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"),
            "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxContextAlbum axContextAlbum = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
            true, AxArtifactKey.getNullKey());

        axContextAlbum.setItemSchema(simpleIntSchema.getKey());
        Distributor distributor = new JvmLocalDistributor();
        distributor.init(axContextAlbum.getKey());
        ContextAlbumImpl album = new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<>());

        assertNotEquals(0, album.hashCode());

        assertEquals(1, album.compareTo(null));

        assertNotEquals(album, new DummyContextAlbumImpl());

        ContextAlbumImpl otherAlbum = new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<>());
        assertEquals(album, otherAlbum);

        otherAlbum.put("Key", 123);
        assertNotEquals(album, otherAlbum);

        assertThatThrownBy(() -> {
            ContextAlbumImpl otherAlbumBad = new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<>());
            otherAlbumBad.put("Key", "BadValue");
        }).hasMessage("Failed to set context value for key \"Key\" in album \"TestContextAlbum:0.0.1\": "
            + "TestContextAlbum:0.0.1: object \"BadValue\" of class \"java.lang.String\" "
            + "not compatible with class \"java.lang.Integer\"");
        AxContextAlbum otherAxContextAlbum = new AxContextAlbum(new AxArtifactKey("OtherTestContextAlbum", "0.0.1"),
            "Policy", true, AxArtifactKey.getNullKey());

        otherAxContextAlbum.setItemSchema(simpleStringSchema.getKey());
        otherAlbum = new ContextAlbumImpl(otherAxContextAlbum, distributor, new LinkedHashMap<>());
        assertNotEquals(album, otherAlbum);

        assertThatThrownBy(album::flush).hasMessage("map flush failed, supplied map is null");
        AxContextAlbums albums = new AxContextAlbums();
        ModelService.registerModel(AxContextAlbums.class, albums);
        albums.getAlbumsMap().put(axContextAlbum.getKey(), axContextAlbum);
        distributor.createContextAlbum(album.getKey());

        album.flush();

        ModelService.clear();
    }
}
