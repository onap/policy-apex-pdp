/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.model.contextmodel.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * Test context comparisons.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ContextComparisonTest {
    private AxContextModel emptyModel;
    private AxContextModel fullModel;
    private AxContextModel noGlobalContextModel;
    private AxContextModel shellModel;
    private AxContextModel singleEntryModel;

    /**
     * Set up tests.
     */
    @BeforeEach
    void getContext() {
        final TestContextComparisonFactory factory = new TestContextComparisonFactory();
        emptyModel = factory.getEmptyModel();
        fullModel = factory.getFullModel();
        noGlobalContextModel = factory.getNoGlobalContextModel();
        shellModel = factory.getShellModel();
        singleEntryModel = factory.getSingleEntryModel();
    }

    @Test
    void testEmptyEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(emptyModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(emptyModel.getSchemas().getSchemasMap(), schemaResult.getIdenticalValues());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(emptyModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(emptyModel.getAlbums().getAlbumsMap(), albumResult.getIdenticalValues());
    }

    @Test
    void testEmptyFull() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(emptyModel.getSchemas(), fullModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(fullModel.getSchemas().getSchemasMap(), schemaResult.getRightOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(emptyModel.getAlbums(), fullModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(fullModel.getAlbums().getAlbumsMap(), albumResult.getRightOnly());
    }

    @Test
    void testFullEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(fullModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(fullModel.getSchemas().getSchemasMap(), schemaResult.getLeftOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(fullModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(fullModel.getAlbums().getAlbumsMap(), albumResult.getLeftOnly());
    }

    @Test
    void testEmptyNoGlobalContext() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(emptyModel.getSchemas(), noGlobalContextModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(noGlobalContextModel.getSchemas().getSchemasMap(), schemaResult.getRightOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(emptyModel.getAlbums(), noGlobalContextModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(noGlobalContextModel.getAlbums().getAlbumsMap(), albumResult.getRightOnly());
    }

    @Test
    void testNoGlobalContextEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(noGlobalContextModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(noGlobalContextModel.getSchemas().getSchemasMap(), schemaResult.getLeftOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(noGlobalContextModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(noGlobalContextModel.getAlbums().getAlbumsMap(), albumResult.getLeftOnly());
    }

    @Test
    void testEmptyShell() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(emptyModel.getSchemas(), shellModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(shellModel.getSchemas().getSchemasMap(), schemaResult.getRightOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(emptyModel.getAlbums(), shellModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(shellModel.getAlbums().getAlbumsMap(), albumResult.getRightOnly());
    }

    @Test
    void testShellEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(shellModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(shellModel.getSchemas().getSchemasMap(), schemaResult.getLeftOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(shellModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(shellModel.getAlbums().getAlbumsMap(), albumResult.getLeftOnly());
    }

    @Test
    void testEmptySingleEntry() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(emptyModel.getSchemas(), singleEntryModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(singleEntryModel.getSchemas().getSchemasMap(), schemaResult.getRightOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(emptyModel.getAlbums(), singleEntryModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(singleEntryModel.getAlbums().getAlbumsMap(), albumResult.getRightOnly());
    }

    @Test
    void testSingleEntryEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(singleEntryModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(singleEntryModel.getSchemas().getSchemasMap(), schemaResult.getLeftOnly());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(singleEntryModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(singleEntryModel.getAlbums().getAlbumsMap(), albumResult.getLeftOnly());
    }

    @Test
    void testFullFull() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult =
            new ContextComparer().compare(fullModel.getSchemas(), fullModel.getSchemas());
        assertNotNull(schemaResult);
        assertEquals(fullModel.getSchemas().getSchemasMap(), schemaResult.getIdenticalValues());

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult =
            new ContextComparer().compare(fullModel.getAlbums(), fullModel.getAlbums());
        assertNotNull(albumResult);
        assertEquals(fullModel.getAlbums().getAlbumsMap(), albumResult.getIdenticalValues());
    }
}
