/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.onap.policy.apex.core.engine.executor.TaskExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;

/**
 * Test Task Execution Context.
 */
@ExtendWith(MockitoExtension.class)
class TaskExecutionContextTest {
    @Mock
    private TaskExecutor taskExecutorMock;

    @Mock
    private TaskExecutor parentExecutorMock;

    @Mock
    private AxTask axTaskMock;

    @Mock
    private ApexInternalContext internalContextMock;

    /**
     * Set up mocking.
     */
    @BeforeEach
    void startMocking() {

        Set<AxArtifactKey> contextAlbumReferences = new LinkedHashSet<>();
        contextAlbumReferences.add(new AxArtifactKey(("AlbumKey0:0.0.1")));
        contextAlbumReferences.add(new AxArtifactKey(("AlbumKey1:0.0.1")));

        Mockito.doReturn(contextAlbumReferences).when(axTaskMock).getContextAlbumReferences();

        Map<String, AxTaskParameter> taskParameters = new HashMap<>();
        taskParameters.put("parameterKey1", new AxTaskParameter(new AxReferenceKey(), "parameterValue1"));
        taskParameters.put("parameterKey2", new AxTaskParameter(new AxReferenceKey(), "parameterValue2"));
        Mockito.doReturn(taskParameters).when(axTaskMock).getTaskParameters();

        Map<AxArtifactKey, ContextAlbum> contextAlbumMap = new LinkedHashMap<>();
        AxArtifactKey album0Key = new AxArtifactKey("AlbumKey0:0.0.1");
        AxArtifactKey album1Key = new AxArtifactKey("AlbumKey1:0.0.1");

        contextAlbumMap.put(album0Key, new DummyContextAlbum(album0Key));
        contextAlbumMap.put(album1Key, new DummyContextAlbum(album1Key));

        Mockito.doReturn(contextAlbumMap).when(internalContextMock).getContextAlbums();

        Mockito.doReturn(parentExecutorMock).when(taskExecutorMock).getParent();
        Mockito.doReturn(new AxArtifactKey("Parent:0.0.1")).when(parentExecutorMock).getKey();
    }

    @Test
    void test() {
        final Map<String, Object> inFields = new LinkedHashMap<>();
        final List<Map<String, Object>> outFieldsList = new LinkedList<>();

        TaskExecutionContext tec = new TaskExecutionContext(taskExecutorMock, 0, null, axTaskMock, inFields,
            outFieldsList, internalContextMock);

        assertNotNull(tec);
        tec.setMessage("TEC Message");
        assertEquals("TEC Message", tec.getMessage());

        ContextAlbum contextAlbum = tec.getContextAlbum("AlbumKey0");
        assertEquals("AlbumKey0:0.0.1", contextAlbum.getKey().getId());

        Map<String, String> parameters = tec.getParameters();
        assertEquals("parameterValue1", parameters.get("parameterKey1"));
        assertEquals("parameterValue2", parameters.get("parameterKey2"));

        assertThatThrownBy(() -> tec.getContextAlbum("AlbumKeyNonExistent"))
            .hasMessageContaining("cannot find definition of context album \"AlbumKeyNonExistent\" on task \"null\"");
    }
}
