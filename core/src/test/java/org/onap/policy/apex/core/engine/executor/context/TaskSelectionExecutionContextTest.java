/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;

/**
 * Test Task Execution Context.
 */
@ExtendWith(MockitoExtension.class)
class TaskSelectionExecutionContextTest {
    @Mock
    private TaskSelectExecutor taskSelectExecutorMock;

    @Mock
    private TaskSelectExecutor parentExecutorMock;

    @Mock
    private AxState axStateMock;

    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private EnEvent incomingEventMock;

    /**
     * Set up mocking.
     */
    @BeforeEach
    void startMocking() {

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

        Mockito.doReturn(parentExecutorMock).when(taskSelectExecutorMock).getParent();
        Mockito.doReturn(new AxReferenceKey("Parent:0.0.1:ParentName:LocalName")).when(parentExecutorMock).getKey();
    }

    @Test
    void test() {
        final AxArtifactKey outgoingEventKey = new AxArtifactKey("OutEvent:0.0.1");

        TaskSelectionExecutionContext tsec = new TaskSelectionExecutionContext(taskSelectExecutorMock, 0, axStateMock,
            incomingEventMock, outgoingEventKey, internalContextMock);

        assertNotNull(tsec);
        tsec.setMessage("TSEC Message");
        assertEquals("TSEC Message", tsec.getMessage());

        ContextAlbum contextAlbum = tsec.getContextAlbum("AlbumKey0");
        assertEquals("AlbumKey0:0.0.1", contextAlbum.getKey().getId());

        assertThatThrownBy(() -> tsec.getContextAlbum("AlbumKeyNonExistent"))
            .hasMessage("cannot find definition of context album \"AlbumKeyNonExistent\" "
                + "on state \"Parent:0.0.1:ParentName:StateName\"");
    }
}
