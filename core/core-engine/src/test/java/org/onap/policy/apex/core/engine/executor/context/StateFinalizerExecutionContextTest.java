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

package org.onap.policy.apex.core.engine.executor.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;

/**
 * Test Task Execution Context.
 */
public class StateFinalizerExecutionContextTest {
    @Mock
    private StateFinalizerExecutor stateFinalizerExecutorMock;

    @Mock
    private StateFinalizerExecutor parentExecutorMock;

    @Mock
    private AxState axStateMock;

    @Mock
    private ApexInternalContext internalContextMock;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        Set<AxArtifactKey> contextAlbumReferences = new LinkedHashSet<>();
        contextAlbumReferences.add(new AxArtifactKey(("AlbumKey0:0.0.1")));
        contextAlbumReferences.add(new AxArtifactKey(("AlbumKey1:0.0.1")));

        Mockito.doReturn(contextAlbumReferences).when(axStateMock).getContextAlbumReferences();
        Mockito.doReturn(new AxReferenceKey("Parent:0.0.1:ParentName:StateName")).when(axStateMock).getKey();

        Map<AxArtifactKey, ContextAlbum> contextAlbumMap = new LinkedHashMap<>();
        AxArtifactKey album0Key = new AxArtifactKey("AlbumKey0:0.0.1");
        AxArtifactKey album1Key = new AxArtifactKey("AlbumKey1:0.0.1");

        contextAlbumMap.put(album0Key, new DummyContextAlbum(album0Key));
        contextAlbumMap.put(album1Key, new DummyContextAlbum(album1Key));

        Mockito.doReturn(contextAlbumMap).when(internalContextMock).getContextAlbums();

        Mockito.doReturn(parentExecutorMock).when(stateFinalizerExecutorMock).getParent();
        Mockito.doReturn(new AxReferenceKey("Parent:0.0.1:ParentName:LocalName")).when(parentExecutorMock).getKey();
    }

    @Test
    public void test() {
        final Map<String, Object> fields = new LinkedHashMap<>();
        final Set<String> stateOutputNames = new LinkedHashSet<>();

        StateFinalizerExecutionContext sfec = new StateFinalizerExecutionContext(stateFinalizerExecutorMock, 0,
                        axStateMock, fields, stateOutputNames, internalContextMock);

        assertNotNull(sfec);
        sfec.setMessage("SFEC Message");
        assertEquals("SFEC Message", sfec.getMessage());

        sfec.setSelectedStateOutputName("SomeOutput");
        assertEquals("SomeOutput", sfec.getSelectedStateOutputName());

        ContextAlbum contextAlbum = sfec.getContextAlbum("AlbumKey0");
        assertEquals("AlbumKey0:0.0.1", contextAlbum.getKey().getId());

        try {
            sfec.getContextAlbum("AlbumKeyNonExistant");
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("cannot find definition of context album \"AlbumKeyNonExistant\" "
                            + "on state \"Parent:0.0.1:ParentName:StateName\"", exc.getMessage());
        }
    }
}
