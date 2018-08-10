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

package org.onap.policy.apex.model.contextmodel.handling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.handling.ContextComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * TestContextComparison.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextComparison {
    private AxContextModel emptyModel;
    private AxContextModel fullModel;
    private AxContextModel noGlobalContextModel;
    private AxContextModel shellModel;
    private AxContextModel singleEntryModel;

    @Before
    public void getContext() {
        final TestContextComparisonFactory factory = new TestContextComparisonFactory();
        emptyModel           = factory.getEmptyModel();
        fullModel            = factory.getFullModel();
        noGlobalContextModel = factory.getNoGlobalContextModel();
        shellModel           = factory.getShellModel();
        singleEntryModel     = factory.getSingleEntryModel();
    }
    
    @Test
    public void testEmptyEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(emptyModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(emptyModel.getSchemas().getSchemasMap().equals(schemaResult.getIdenticalValues()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(emptyModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(emptyModel.getAlbums().getAlbumsMap().equals(albumResult.getIdenticalValues()));
    }
    
    @Test
    public void testEmptyFull() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(emptyModel.getSchemas(), fullModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(fullModel.getSchemas().getSchemasMap().equals(schemaResult.getRightOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(emptyModel.getAlbums(), fullModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(fullModel.getAlbums().getAlbumsMap().equals(albumResult.getRightOnly()));
    }
    
    @Test
    public void testFullEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(fullModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(fullModel.getSchemas().getSchemasMap().equals(schemaResult.getLeftOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(fullModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(fullModel.getAlbums().getAlbumsMap().equals(albumResult.getLeftOnly()));
    }
    
    @Test
    public void testEmptyNoGlobalContext() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(emptyModel.getSchemas(), noGlobalContextModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(noGlobalContextModel.getSchemas().getSchemasMap().equals(schemaResult.getRightOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(emptyModel.getAlbums(), noGlobalContextModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(noGlobalContextModel.getAlbums().getAlbumsMap().equals(albumResult.getRightOnly()));
    }
    
    @Test
    public void testNoGlobalContextEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(noGlobalContextModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(noGlobalContextModel.getSchemas().getSchemasMap().equals(schemaResult.getLeftOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(noGlobalContextModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(noGlobalContextModel.getAlbums().getAlbumsMap().equals(albumResult.getLeftOnly()));
    }
    
    @Test
    public void testEmptyShell() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(emptyModel.getSchemas(), shellModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(shellModel.getSchemas().getSchemasMap().equals(schemaResult.getRightOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(emptyModel.getAlbums(), shellModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(shellModel.getAlbums().getAlbumsMap().equals(albumResult.getRightOnly()));
    }
    
    @Test
    public void testShellEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(shellModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(shellModel.getSchemas().getSchemasMap().equals(schemaResult.getLeftOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(shellModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(shellModel.getAlbums().getAlbumsMap().equals(albumResult.getLeftOnly()));
    }
    
    @Test
    public void testEmptySingleEntry() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(emptyModel.getSchemas(), singleEntryModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(singleEntryModel.getSchemas().getSchemasMap().equals(schemaResult.getRightOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(emptyModel.getAlbums(), singleEntryModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(singleEntryModel.getAlbums().getAlbumsMap().equals(albumResult.getRightOnly()));
    }
    
    @Test
    public void testSingleEntryEmpty() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(singleEntryModel.getSchemas(), emptyModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(singleEntryModel.getSchemas().getSchemasMap().equals(schemaResult.getLeftOnly()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(singleEntryModel.getAlbums(), emptyModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(singleEntryModel.getAlbums().getAlbumsMap().equals(albumResult.getLeftOnly()));
    }
    
    @Test
    public void testFullFull() {
        final KeyedMapDifference<AxArtifactKey, AxContextSchema> schemaResult = 
                new ContextComparer().compare(fullModel.getSchemas(), fullModel.getSchemas());
        assertNotNull(schemaResult);
        assertTrue(fullModel.getSchemas().getSchemasMap().equals(schemaResult.getIdenticalValues()));

        final KeyedMapDifference<AxArtifactKey, AxContextAlbum> albumResult = 
                new ContextComparer().compare(fullModel.getAlbums(), fullModel.getAlbums());
        assertNotNull(albumResult);
        assertTrue(fullModel.getAlbums().getAlbumsMap().equals(albumResult.getIdenticalValues()));
    }
}
