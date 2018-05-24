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

package org.onap.apex.model.contextmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.apex.model.contextmodel.concepts.AxContextAlbums;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextAlbums {

    @Test
    public void testContextAlbums() {
        assertNotNull(new AxContextAlbum());
        assertNotNull(new AxContextAlbum(new AxArtifactKey()));
        assertNotNull(new AxContextAlbum(new AxArtifactKey(), "AlbumScope", false, new AxArtifactKey()));

        final AxArtifactKey albumKey = new AxArtifactKey("AlbumName", "0.0.1");
        final AxArtifactKey albumSchemaKey = new AxArtifactKey("AlbumSchemaName", "0.0.1");

        final AxContextAlbum album = new AxContextAlbum(albumKey, "AlbumScope", false, albumSchemaKey);
        assertNotNull(album);

        final AxArtifactKey newKey = new AxArtifactKey("NewAlbumName", "0.0.1");
        album.setKey(newKey);
        assertEquals("NewAlbumName:0.0.1", album.getKey().getID());
        assertEquals("NewAlbumName:0.0.1", album.getKeys().get(0).getID());
        album.setKey(albumKey);

        try {
            album.setScope("");
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("parameter \"scope\": value \"\", does not match regular expression \"[A-Za-z0-9\\-_]+\"",
                    e.getMessage());
        }

        album.setScope("NewAlbumScope");
        assertEquals("NewAlbumScope", album.getScope());

        assertEquals(false, album.isWritable());
        album.setWritable(true);
        assertEquals(true, album.isWritable());

        final AxArtifactKey newSchemaKey = new AxArtifactKey("NewAlbumSchemaName", "0.0.1");
        album.setItemSchema(newSchemaKey);
        assertEquals("NewAlbumSchemaName:0.0.1", album.getItemSchema().getID());
        album.setItemSchema(albumSchemaKey);

        AxValidationResult result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        album.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

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

        album.setItemSchema(albumSchemaKey);
        result = new AxValidationResult();
        result = album.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        album.clean();

        final AxContextAlbum clonedAlbum = new AxContextAlbum(album);
        assertEquals(
                "AxContextAlbum:(key=AxArtifactKey:(name=NewAlbumName,version=0.0.1),scope=NewAlbumScope,isWritable=true,itemSchema=AxArtifactKey:(name=AlbumSchemaName,version=0.0.1))",
                clonedAlbum.toString());

        assertFalse(album.hashCode() == 0);

        assertTrue(album.equals(album));
        assertTrue(album.equals(clonedAlbum));
        assertFalse(album.equals(null));
        assertFalse(album.equals("Hello"));
        assertFalse(album.equals(new AxContextAlbum(new AxArtifactKey(), "Scope", false, AxArtifactKey.getNullKey())));
        assertFalse(album.equals(new AxContextAlbum(newKey, "Scope", false, AxArtifactKey.getNullKey())));
        assertFalse(album.equals(new AxContextAlbum(newKey, "NewAlbumScope", false, AxArtifactKey.getNullKey())));
        assertFalse(album.equals(new AxContextAlbum(newKey, "NewAlbumScope", true, AxArtifactKey.getNullKey())));
        assertTrue(album.equals(new AxContextAlbum(newKey, "NewAlbumScope", true, albumSchemaKey)));

        assertEquals(0, album.compareTo(album));
        assertEquals(0, album.compareTo(clonedAlbum));
        assertNotEquals(0, album.compareTo(null));
        assertNotEquals(0, album.compareTo(new AxArtifactKey()));
        assertNotEquals(0,
                album.compareTo(new AxContextAlbum(new AxArtifactKey(), "Scope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0, album.compareTo(new AxContextAlbum(newKey, "Scope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0,
                album.compareTo(new AxContextAlbum(newKey, "NewAlbumScope", false, AxArtifactKey.getNullKey())));
        assertNotEquals(0,
                album.compareTo(new AxContextAlbum(newKey, "NewAlbumScope", true, AxArtifactKey.getNullKey())));
        assertEquals(0, album.compareTo(new AxContextAlbum(newKey, "NewAlbumScope", true, albumSchemaKey)));

        final AxContextAlbums albums = new AxContextAlbums();
        result = new AxValidationResult();
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

        albums.clean();

        final AxContextAlbums clonedAlbums = new AxContextAlbums(albums);
        assertTrue(clonedAlbums.toString()
                .startsWith("AxContextAlbums:(AxContextAlbums:(key=AxArtifactKey:(name=AlbumsKey,version=0.0.1)"));

        assertFalse(albums.hashCode() == 0);

        assertTrue(albums.equals(albums));
        assertTrue(albums.equals(clonedAlbums));
        assertFalse(albums.equals(null));
        assertFalse(albums.equals("Hello"));
        assertFalse(albums.equals(new AxContextAlbums(new AxArtifactKey())));

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
        assertEquals(0, albums.getAll("NonExistantAlbumName").size());
    }
}
