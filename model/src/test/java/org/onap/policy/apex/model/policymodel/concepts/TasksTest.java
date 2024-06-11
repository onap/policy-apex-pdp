/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2022 Nordix Foundation.
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

package org.onap.policy.apex.model.policymodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;

/**
 * Test policy tasks.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TasksTest {

    @Test
    public void testTasks() {
        final TreeMap<String, AxTaskParameter> tpEmptyMap = new TreeMap<>();
        final TreeSet<AxArtifactKey> ctxtEmptySet = new TreeSet<>();

        final TreeMap<String, AxTaskParameter> tpMap = new TreeMap<>();
        final TreeSet<AxArtifactKey> ctxtSet = new TreeSet<>();

        assertNotNull(new AxTask());
        assertNotNull(new AxTask(new AxArtifactKey()));
        assertNotNull(new AxTask(new AxArtifactKey(), tpMap, ctxtSet, new AxTaskLogic()));

        final AxTask task = new AxTask();

        final AxArtifactKey taskKey = new AxArtifactKey("TaskName", "0.0.1");
        task.setKey(taskKey);
        assertEquals("TaskName:0.0.1", task.getKey().getId());
        assertEquals("TaskName:0.0.1", task.getKeys().get(0).getId());

        final AxTaskParameter tp0 = new AxTaskParameter(new AxReferenceKey(taskKey, "TP0"), "DefaultValue");
        final AxArtifactKey cr0 = new AxArtifactKey("ContextReference", "0.0.1");
        final AxTaskLogic tl = new AxTaskLogic(taskKey, "LogicName", "LogicFlavour", "Logic");

        tpMap.put(tp0.getKey().getLocalName(), tp0);
        ctxtSet.add(cr0);

        task.setInputEvent(new AxEvent());
        task.setOutputEvents(Map.of("Event", new AxEvent()));

        task.setTaskParameters(tpMap);
        assertEquals(tpMap, task.getTaskParameters());

        task.setContextAlbumReferences(ctxtSet);
        assertEquals(ctxtSet, task.getContextAlbumReferences());

        task.setTaskLogic(tl);
        assertEquals(tl, task.getTaskLogic());

        task.setKey(taskKey);
        assertEquals("TaskName:0.0.1", task.getKey().getId());
        assertEquals("TaskName:0.0.1", task.getKeys().get(0).getId());

        task.buildReferences();
        assertEquals(1, task.getTaskParameters().size());

        AxValidationResult result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        task.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        task.setKey(taskKey);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Empty task parameter map is OK
        task.setTaskParameters(tpEmptyMap);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        task.setTaskParameters(tpMap);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        tpMap.put("NullField", null);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tpMap.remove("NullField");
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Empty context reference set is OK
        task.setContextAlbumReferences(ctxtEmptySet);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        task.setContextAlbumReferences(ctxtSet);
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        ctxtSet.add(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        ctxtSet.remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = task.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        task.clean();

        final AxTask clonedTask = new AxTask(task);
        assertEquals("AxTask:(key=AxArtifactKey:(name=TaskName", clonedTask.toString().substring(0, 40));

        assertNotEquals(0, task.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(task, task); // NOSONAR
        assertEquals(task, clonedTask);
        assertNotNull(task);

        Object helloObj = "Hello";
        assertNotEquals(task, helloObj);
        assertNotEquals(task, new AxTask(new AxArtifactKey(), tpMap, ctxtSet, tl));
        assertEquals(task, new AxTask(taskKey, tpMap, ctxtSet, tl));
        assertNotEquals(task, new AxTask(taskKey, tpEmptyMap, ctxtSet, tl));
        assertNotEquals(task, new AxTask(taskKey, tpMap, ctxtEmptySet, tl));
        assertNotEquals(task, new AxTask(taskKey, tpMap, ctxtSet, new AxTaskLogic()));
        assertEquals(task, new AxTask(taskKey, tpMap, ctxtSet, tl));

        assertEquals(0, task.compareTo(task));
        assertEquals(0, task.compareTo(clonedTask));
        assertNotEquals(0, task.compareTo(new AxArtifactKey()));
        assertNotEquals(0, task.compareTo(null));
        assertNotEquals(0, task.compareTo(new AxTask(new AxArtifactKey(), tpMap, ctxtSet, tl)));
        assertEquals(0, task.compareTo(new AxTask(taskKey, tpMap, ctxtSet, tl)));
        assertNotEquals(0, task.compareTo(new AxTask(taskKey, tpEmptyMap, ctxtSet, tl)));
        assertNotEquals(0, task.compareTo(new AxTask(taskKey, tpMap, ctxtEmptySet, tl)));
        assertNotEquals(0, task.compareTo(new AxTask(taskKey, tpMap, ctxtSet, new AxTaskLogic())));
        assertEquals(0, task.compareTo(new AxTask(taskKey, tpMap, ctxtSet, tl)));

        assertNotNull(task.getKeys());

        final AxTasks tasks = new AxTasks();
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Invalid, no tasks in task map
        tasks.setKey(new AxArtifactKey("TasksKey", "0.0.1"));
        assertEquals("TasksKey:0.0.1", tasks.getKey().getId());

        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tasks.getTaskMap().put(taskKey, task);
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        tasks.getTaskMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tasks.getTaskMap().remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        tasks.getTaskMap().put(new AxArtifactKey("NullValueKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tasks.getTaskMap().remove(new AxArtifactKey("NullValueKey", "0.0.1"));
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        tasks.getTaskMap().put(new AxArtifactKey("BadTaskKey", "0.0.1"), task);
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tasks.getTaskMap().remove(new AxArtifactKey("BadTaskKey", "0.0.1"));
        result = new AxValidationResult();
        result = tasks.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        tasks.clean();

        final AxTasks clonedTasks = new AxTasks(tasks);
        assertEquals("AxTasks:(key=AxArtifactKey:(name=TasksKey,version=0.0.1),tas",
                        clonedTasks.toString().substring(0, 60));

        assertNotEquals(0, tasks.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(tasks, tasks); // NOSONAR
        assertEquals(tasks, clonedTasks);
        assertNotNull(tasks);
        assertNotEquals(tasks, helloObj);
        assertNotEquals(tasks, new AxTasks(new AxArtifactKey()));

        assertEquals(0, tasks.compareTo(tasks));
        assertEquals(0, tasks.compareTo(clonedTasks));
        assertNotEquals(0, tasks.compareTo(null));
        assertNotEquals(0, tasks.compareTo(new AxArtifactKey()));
        assertNotEquals(0, tasks.compareTo(new AxTasks(new AxArtifactKey())));

        clonedTasks.get(taskKey).getTaskLogic().setLogic("SomeChangedLogic");
        assertNotEquals(0, tasks.compareTo(clonedTasks));

        assertEquals(tasks.getKey(), tasks.getKeys().get(0));

        assertEquals("TaskName", tasks.get("TaskName").getKey().getName());
        assertEquals("TaskName", tasks.get("TaskName", "0.0.1").getKey().getName());
        assertEquals(1, tasks.getAll("TaskName", "0.0.1").size());
        assertEquals(0, tasks.getAll("NonExistantTaskName").size());
    }
}
