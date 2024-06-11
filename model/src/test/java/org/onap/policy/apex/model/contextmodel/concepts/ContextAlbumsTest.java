/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.apex.model.contextmodel.concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Context album tests.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ContextAlbumsTest {

    @Test
    void testNewAxContextAlbum() {
        assertNotNull(new AxContextAlbum());
        assertNotNull(new AxContextAlbum(new AxArtifactKey()));
        assertNotNull(new AxContextAlbum(new AxArtifactKey(), "AlbumScope", false, new AxArtifactKey()));
    }

    @Test
    void testContextAlbums() {
        final AxArtifactKey albumKey = new AxArtifactKey("AlbumName", "0.0.1");
        final AxArtifactKey albumSchemaKey = new AxArtifactKey("AlbumSchemaName", "0.0.1");

        final AxContextAlbum album = new AxContextAlbum(albumKey, "AlbumScope", false, albumSchemaKey);
        assertNotNull(album);

        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        album.setKey(newKey);
        assertEquals("NewAlbumName:0.0.1", album.getKey().getId());
        assertEquals("NewAlbumName:0.0.1", album.getKeys().get(0).getId());
        album.setKey(albumKey);

        assertThatThrownBy(() -> album.setScope(""))
            .hasMessage("parameter \"scope\": value \"\", does not match regular expression "
                    + "\"[A-Za-z0-9\\-_]+\"");

        album.setScope("NewAlbumScope");
        assertEquals("NewAlbumScope", album.getScope());

        assertFalse(album.isWritable());
        album.setWritable(true);
        assertTrue(album.isWritable());

        final AxArtifactKey newSchemaKey = new AxArtifactKey("NewAlbumSchemaName", "0.0.1");
        album.setItemSchema(newSchemaKey);
        assertEquals("NewAlbumSchemaName:0.0.1", album.getItemSchema().getId());
        album.setItemSchema(albumSchemaKey);
    }

    private AxContextAlbum setTestAlbum() {
        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        final AxArtifactKey newSchemaKey = new AxArtifactKey("NewAlbumSchemaName", "0.0.1");

        final AxContextAlbum album = new AxContextAlbum(newKey, "AlbumScope", false, newSchemaKey);

        album.setScope("NewAlbumScope");
        album.setWritable(true);

        return album;
    }

    @Test
    void testAxValidationAlbum() {
        final AxContextAlbum album = setTestAlbum();
        AxValidationResult result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        album.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        album.setKey(newKey);
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        album.setScope("UNDEFINED");
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        album.setScope("NewAlbumScope");
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        album.setItemSchema(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        final AxArtifactKey albumSchemaKey = new AxArtifactKey("AlbumSchemaName", "0.0.1");
        album.setItemSchema(albumSchemaKey);
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

    }

    @Test
    void testEqualsAlbum() {
        final AxContextAlbum album = setTestAlbum();
        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        final AxArtifactKey albumSchemaKey = new AxArtifactKey("AlbumSchemaName", "0.0.1");
        album.setItemSchema(albumSchemaKey);

        final AxContextAlbum clonedAlbum = new AxContextAlbum(album);
        assertEquals("AxContextAlbum(key=AxArtifactKey:(name=NewAlbumName,version=0.0.1), "
                        + "scope=NewAlbumScope, isWritable=true, itemSchema="
                        + "AxArtifactKey:(name=AlbumSchemaName,version=0.0.1))", clonedAlbum.toString());

        assertNotEquals(0, album.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(album, album); // NOSONAR
        assertEquals(album, clonedAlbum);
        assertNotNull(album);
        assertNotEquals(album, (Object) "Hello");
        assertNotEquals(album, new AxContextAlbum(new AxArtifactKey(), "Scope", false, AxArtifactKey.getNullKey()));
        assertNotEquals(album, new AxContextAlbum(newKey, "Scope", false, AxArtifactKey.getNullKey()));
        assertNotEquals(album, new AxContextAlbum(newKey, "NewAlbumScope", false, AxArtifactKey.getNullKey()));
        assertNotEquals(album, new AxContextAlbum(newKey, "NewAlbumScope", true, AxArtifactKey.getNullKey()));
        assertEquals(album, new AxContextAlbum(newKey, "NewAlbumScope", true, albumSchemaKey));

        assertEquals(0, album.compareTo(album));
        assertEquals(0, album.compareTo(clonedAlbum));
        assertNotEquals(0, album.compareTo(null));
        assertNotEquals(0, album.compareTo(new AxArtifactKey()));
        assertNotEquals(0, album.compareTo(
                        new AxContextAlbum(new AxArtifactKey(), "Scope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0, album.compareTo(new AxContextAlbum(newKey, "Scope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0, album
                        .compareTo(new AxContextAlbum(newKey, "NewAlbumScope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0,
                        album.compareTo(new AxContextAlbum(newKey, "NewAlbumScope", true, AxArtifactKey.getNullKey())));
        assertEquals(0, album.compareTo(new AxContextAlbum(newKey, "NewAlbumScope", true, albumSchemaKey)));
    }

    @Test
    void testMultipleAlbums() {
        final AxContextAlbums albums = new AxContextAlbums();
        final AxContextAlbum album = setTestAlbum();
        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        AxValidationResult result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Observation, no albums in album map
        albums.setKey(new AxArtifactKey("AlbumsKey", "0.0.1"));
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        albums.getAlbumsMap().put(newKey, album);
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        albums.getAlbumsMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        albums.getAlbumsMap().remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        albums.getAlbumsMap().put(new AxArtifactKey("NullValueKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        albums.getAlbumsMap().remove(new AxArtifactKey("NullValueKey", "0.0.1"));
        result = new AxValidationResult();
        result = albums.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

    }

    @Test
    void testClonedAlbums() {
        final AxContextAlbums albums = new AxContextAlbums();
        final AxContextAlbum album = setTestAlbum();
        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        albums.setKey(new AxArtifactKey("AlbumsKey", "0.0.1"));
        albums.getAlbumsMap().put(newKey, album);
        albums.clean();

        final AxContextAlbums clonedAlbums = new AxContextAlbums(albums);
        assertThat(clonedAlbums.toString()).startsWith(
                        "AxContextAlbums(key=AxArtifactKey:(name=AlbumsKey,version=0.0.1)");

        assertNotEquals(0, albums.hashCode());

        assertEquals(albums, clonedAlbums);
        assertNotNull(albums);
        assertNotEquals(albums, (Object) "Hello");
        assertNotEquals(albums, new AxContextAlbums(new AxArtifactKey()));

        assertEquals(0, albums.compareTo(albums));
        assertEquals(0, albums.compareTo(clonedAlbums));
        assertNotEquals(0, albums.compareTo(null));
        assertNotEquals(0, albums.compareTo(new AxArtifactKey()));
        assertNotEquals(0, albums.compareTo(new AxContextAlbums(new AxArtifactKey())));

        clonedAlbums.get(newKey).setScope("YetAnotherScope");
        assertNotEquals(0, albums.compareTo(clonedAlbums));

        assertEquals("NewAlbumName", albums.get("NewAlbumName").getKey().getName());
        assertEquals("NewAlbumName", albums.get("NewAlbumName", "0.0.1").getKey().getName());
        assertEquals(1, albums.getAll("NewAlbumName", "0.0.1").size());
        assertEquals(0, albums.getAll("NonExistentAlbumName").size());
    }
}
