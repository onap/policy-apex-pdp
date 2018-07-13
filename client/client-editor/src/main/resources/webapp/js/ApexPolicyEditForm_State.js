/*
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

function editPolicyForm_State_generateStateDiv(createEditOrView, policy, statename, state, tasks, events,
        contextAlbums, contextItemSchemas) {
    var retDiv = document.createElement("div");
    retDiv.setAttribute("id", "editPolicyFormStateDiv_" + statename);
    retDiv.setAttribute("class", "editPolicyFormStateDiv");
    var divUL = document.createElement("ul");
    retDiv.appendChild(divUL);

    // input event
    var triggerLI = document.createElement("li");
    divUL.appendChild(triggerLI);
    var triggerLabel = document.createElement("label");
    triggerLI.appendChild(triggerLabel);
    triggerLabel.setAttribute("for", "editPolicyFormTrigger" + "_" + statename);
    triggerLabel.innerHTML = "Input Event for State: ";
    var eventselected = null;
    if (state != null && state.trigger != null) {
        eventselected = {
            "name" : state.trigger.name,
            "version" : state.trigger.version,
            "displaytext" : state.trigger.name + ":" + state.trigger.version
        };
    }
    var ineventSelectDiv = dropdownList("editPolicyFormTrigger" + "_" + statename, events, eventselected,
            (createEditOrView == "VIEW"), function() {
                return editPolicyForm_updateTriggerEventOptions(events)
            });
    triggerLI.appendChild(ineventSelectDiv);

    // Context Albums references
    var contextsLI = document.createElement("li");
    divUL.appendChild(contextsLI);
    var contextsLabel = document.createElement("label");
    contextsLI.appendChild(contextsLabel);
    contextsLabel.setAttribute("for", "editPolicyFormContextsTable" + "_" + statename);
    contextsLabel.innerHTML = "Context Albums used in Task Selection Logic / State Output Logic: ";
    var contextstable = document.createElement("table");
    contextstable.setAttribute("id", "editPolicyFormContextsTable" + "_" + statename);
    contextstable.setAttribute("name", "editPolicyFormContextsTable" + "_" + statename);
    contextstable.setAttribute("class", "table-policycontext");
    contextsLI.appendChild(contextstable);
    // var contextstable_head = document.createElement("thead");
    // contextstable.appendChild(contextstable_head);
    // var contextstable_head_tr = document.createElement("tr");
    // contextstable_head.appendChild(contextstable_head_tr);
    // contextstable_head_tr.appendChild(document.createElement("th")); //empty,
    // for delete button
    // var contextstable_head_th = document.createElement("th");
    // contextstable_head_tr.appendChild(contextstable_head_th);
    // contextstable_head_th.innerHTML = "Context Album: ";
    // contextstable_head_th.setAttribute("class",
    // "table-policycontext-heading");
    var contextstable_body = document.createElement("tbody");
    contextstable.appendChild(contextstable_body);
    // Add the contexts
    if (state && state.contextAlbumReference && $.isArray(state.contextAlbumReference)) {
        for (var p = 0; p < state.contextAlbumReference.length; p++) {
            var contextEntry = state.contextAlbumReference[p];
            var contextName = contextEntry.name + ":" + contextEntry.version;
            var ce = {
                "name" : contextEntry.name,
                "version" : contextEntry.version,
                "displaytext" : contextName
            };
            editPolicyForm_State_addPolicyContext(contextstable_body, (createEditOrView == "VIEW"), statename,
                    contextName, ce, contextAlbums);
        }
    }
    // add the Policy Context button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var contextTR = document.createElement("tr");
        contextTR.setAttribute("class", "field-policycontext-tr.new");
        contextstable_body.appendChild(contextTR);

        var contextTD = document.createElement("td");
        contextTD.setAttribute("colspan", "2");
        contextTR.appendChild(contextTD);
        var addContextInput = createAddFormButton("Add New Policy Logic Context");
        contextTD.appendChild(addContextInput);
        // addContextInput.setAttribute("id", "addContextButton");
        // addContextInput.setAttribute("class", "ebBtn ebBtn_large");
        // addContextInput.setAttribute("type", "submit");
        // addContextInput.setAttribute("value", "Add New Policy Logic
        // Context");
        // addContextInput.innerHTML = addContextInput.getAttribute("value");
        addContextInput.onclick = function() {
            return editPolicyForm_State_addPolicyContext(contextstable_body, false, statename, null, null,
                    contextAlbums);
        };
    }

    // Task references
    var tasksLI = document.createElement("li");
    divUL.appendChild(tasksLI);
    var tasksLabel = document.createElement("label");
    tasksLI.appendChild(tasksLabel);
    tasksLabel.setAttribute("for", "editPolicyFormTasksTable" + "_" + statename);
    tasksLabel.innerHTML = "State Tasks:";
    var taskstable = document.createElement("table");
    taskstable.setAttribute("id", "editPolicyFormTasksTable" + "_" + statename);
    taskstable.setAttribute("name", "editPolicyFormTasksTable" + "_" + statename);
    taskstable.setAttribute("class", "table-policytask");
    tasksLI.appendChild(taskstable);
    var taskstable_head = document.createElement("thead");
    taskstable.appendChild(taskstable_head);
    var taskstable_head_tr = document.createElement("tr");
    taskstable_head.appendChild(taskstable_head_tr);
    taskstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                    // for
                                                                    // delete
                                                                    // button
    var taskstable_isdefault_head_th = document.createElement("th");
    taskstable_head_tr.appendChild(taskstable_isdefault_head_th);
    taskstable_isdefault_head_th.innerHTML = "Default Task? ";
    taskstable_isdefault_head_th.setAttribute("class", "table-policytask-heading form-heading");
    var taskstable_localname_head_th = document.createElement("th");
    taskstable_head_tr.appendChild(taskstable_localname_head_th);
    taskstable_localname_head_th.innerHTML = "Local Name for Task: ";
    taskstable_localname_head_th.setAttribute("class", "table-policytask-heading form-heading");
    var taskstable_head_th = document.createElement("th");
    taskstable_head_tr.appendChild(taskstable_head_th);
    taskstable_head_th.innerHTML = "Task: ";
    taskstable_head_th.setAttribute("class", "table-policytask-heading form-heading");
    var taskstable_outputtype_head_th = document.createElement("th");
    taskstable_head_tr.appendChild(taskstable_outputtype_head_th);
    taskstable_outputtype_head_th.innerHTML = "Output Mapping type: ";
    taskstable_outputtype_head_th.setAttribute("class", "table-policytask-heading form-heading");
    taskstable_outputtype_head_th.setAttribute("colspan", "2");
    var taskstable_outputsel_head_th = document.createElement("th");
    taskstable_head_tr.appendChild(taskstable_outputsel_head_th);
    taskstable_outputsel_head_th.innerHTML = "Output Mapping: ";
    taskstable_outputsel_head_th.setAttribute("class", "table-policytask-heading form-heading");
    var taskstable_body = document.createElement("tbody");
    taskstable.appendChild(taskstable_body);
    var defaulttask = null;
    if (state && state.defaultTask && state.defaultTask.name && state.defaultTask.version) {
        defaulttask = state.defaultTask.name + ":" + state.defaultTask.version;
    }
    // Add the tasks
    if (state && state.taskReferences && $.isArray(state.taskReferences.entry)) {
        for (var p = 0; p < state.taskReferences.entry.length; p++) {
            var taskEntry = state.taskReferences.entry[p];
            var taskName = taskEntry.key.name + ":" + taskEntry.key.version;
            var taskselected = {
                "name" : taskEntry.key.name,
                "version" : taskEntry.key.version,
                "displaytext" : taskName
            };
            var taskreference = taskEntry.value;
            editPolicyForm_State_addPolicyTask(taskstable_body, (createEditOrView == "VIEW"),
                    (defaulttask == taskName), state, statename, taskreference, taskselected, tasks);
        }
    }
    // add the Policy Task button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var taskTR = document.createElement("tr");
        taskTR.setAttribute("class", "field-policytask-tr.new");
        taskstable_body.appendChild(taskTR);
        var taskTD = document.createElement("td");
        taskTD.setAttribute("colspan", "4");
        taskTR.appendChild(taskTD);
        var addTaskInput = createAddFormButton("Add New Task");
        taskTD.appendChild(addTaskInput);
        // addTaskInput.setAttribute("id", "addTaskButton");
        // addTaskInput.setAttribute("class", "ebBtn ebBtn_large");
        // addTaskInput.setAttribute("type", "submit");
        // addTaskInput.setAttribute("value", "Add New Task");
        // addTaskInput.innerHTML = addTaskInput.getAttribute("value");
        addTaskInput.onclick = function() {
            return editPolicyForm_State_addPolicyTask(taskstable_body, false, false, state, statename, null, null,
                    tasks);
        };
    }

    // tasksellogic
    var tasksellogicLI = document.createElement("li");
    divUL.appendChild(tasksellogicLI);
    var tasksellogicLabel = document.createElement("label");
    tasksellogicLI.appendChild(tasksellogicLabel);
    tasksellogicLabel.setAttribute("for", "editEventFormTaskSelLogicDiv" + "_" + statename);
    tasksellogicLabel.innerHTML = "Task Selection Logic: ";
    var tasksellogicdiv = document.createElement("div");
    tasksellogicdiv.setAttribute("id", "editEventFormTaskSelLogicDiv" + "_" + statename);

    var logic = "";
    if (state && state.taskSelectionLogic && state.taskSelectionLogic.logic && state.taskSelectionLogic.logic != "") {
        logic = state.taskSelectionLogic.logic;
    }
    var edit_disabled = false;
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        edit_disabled = true;
    }
    var textarea = showHideTextarea("editEventFormTaskSelLogicInput" + "_" + statename, logic, false, !edit_disabled,
            false);

    tasksellogicLI.appendChild(textarea);

    // tasksellogic type
    var tasksellogicTypeLabel = document.createElement("label");
    tasksellogicdiv.appendChild(tasksellogicTypeLabel);
    tasksellogicTypeLabel.setAttribute("for", "editPolicyFormTaskSelLogicTypeInput" + "_" + statename);
    tasksellogicTypeLabel.innerHTML = "Task Selection Logic Type / Flavour: ";
    var tasksellogicTypeInput = document.createElement("input");
    tasksellogicdiv.appendChild(tasksellogicTypeInput);
    tasksellogicTypeInput.setAttribute("id", "editPolicyFormTaskSelLogicTypeInput" + "_" + statename);
    tasksellogicTypeInput.setAttribute("type", "text");
    tasksellogicTypeInput.setAttribute("name", "editPolicyFormTaskSelLogicTypeInput" + "_" + statename);
    tasksellogicTypeInput.setAttribute("class", "field-taskSelLogicType ebInput ebInput_width_xLong");
    tasksellogicTypeInput.setAttribute("placeholder", "MVEL");
    if (state && state.taskSelectionLogic && state.taskSelectionLogic.logicFlavour != null
            && state.taskSelectionLogic.logicFlavour != ""
            && state.taskSelectionLogic.logicFlavour.toUpperCase() != "UNDEFINED") {
        tasksellogicTypeInput.value = state.taskSelectionLogic.logicFlavour;
    }
    tasksellogicTypeInput.readOnly = (createEditOrView != "CREATE" && createEditOrView != "EDIT");

    divUL.appendChild(tasksellogicdiv);

    // Output mappings
    var outputsLI = document.createElement("li");
    divUL.appendChild(outputsLI);
    var outputsLabel = document.createElement("label");
    outputsLI.appendChild(outputsLabel);
    outputsLabel.innerHTML = "State Output Mappings: ";
    outputsLabel.setAttribute("for", "editPolicyFormOutputsUL" + "_" + statename);
    var outsUL = document.createElement("ul");
    outsUL.setAttribute("id", "editPolicyFormOutputsUL" + "_" + statename);
    outputsLI.appendChild(outsUL);
    // Direct Output Mappings
    var dir_outputsLI = document.createElement("li");
    outsUL.appendChild(dir_outputsLI);
    var dir_outputsLabel = document.createElement("label");
    dir_outputsLI.appendChild(dir_outputsLabel);
    dir_outputsLI.innerHTML = "Direct State Output Mappings: ";
    dir_outputsLabel.setAttribute("for", "editPolicyFormDirOutputsTable" + "_" + statename);
    var dir_outputstable = document.createElement("table");
    dir_outputstable.setAttribute("id", "editPolicyFormDirOutputsTable" + "_" + statename);
    dir_outputstable.setAttribute("name", "editPolicyFormDirOutputsTable" + "_" + statename);
    dir_outputstable.setAttribute("class", "table-policyoutput");
    dir_outputsLI.appendChild(dir_outputstable);
    var dir_outputstable_head = document.createElement("thead");
    dir_outputstable.appendChild(dir_outputstable_head);
    var dir_outputstable_head_tr = document.createElement("tr");
    dir_outputstable_head.appendChild(dir_outputstable_head_tr);
    dir_outputstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                        // for
                                                                        // delete
                                                                        // button
    var dir_outputstable_name_head_th = document.createElement("th");
    dir_outputstable_head_tr.appendChild(dir_outputstable_name_head_th);
    dir_outputstable_name_head_th.innerHTML = "Local Name for Output Mapping: ";
    dir_outputstable_name_head_th.setAttribute("class", "table-policyoutput-heading form-heading");
    var dir_outputstable_nextstate_head_th = document.createElement("th");
    dir_outputstable_head_tr.appendChild(dir_outputstable_nextstate_head_th);
    dir_outputstable_nextstate_head_th.innerHTML = "Next State: ";
    dir_outputstable_nextstate_head_th.setAttribute("class", "table-policyoutput-heading form-heading");
    var dir_outputstable_event_head_th = document.createElement("th");
    dir_outputstable_head_tr.appendChild(dir_outputstable_event_head_th);
    dir_outputstable_event_head_th.innerHTML = "State Output Event: ";
    dir_outputstable_event_head_th.setAttribute("class", "table-policyoutput-heading form-heading");
    var dir_outputstable_body = document.createElement("tbody");
    dir_outputstable.appendChild(dir_outputstable_body);
    var stateNextStateOptions = new Array();
    stateNextStateOptions.push({
        "name" : "NULL",
        "displaytext" : "None",
        "state" : null
    });
    if (policy && policy.state && policy.state.entry && $.isArray(policy.state.entry)) {
        for (var s = 0; s < policy.state.entry.length; s++) {
            var st = policy.state.entry[s];
            if (statename != st.key) { // state cannot have itself as nextstate
                stateNextStateOptions.push({
                    "name" : st.key,
                    "displaytext" : st.key,
                    "state" : st.value
                });
            }
        }
    }
    if (state && state.stateOutputs && $.isArray(state.stateOutputs.entry)) {
        for (var p = 0; p < state.stateOutputs.entry.length; p++) {
            var outputEntry = state.stateOutputs.entry[p];
            var outputName = outputEntry.key;
            var nextState = null;
            var nxtst = outputEntry.value.nextState.localName;
            if (nxtst != null && nxtst.toUpperCase() != "NULL") {
                nextState = {
                    "name" : nxtst,
                    "displaytext" : nxtst
                };
            }
            if (nxtst != null && nxtst.toUpperCase() == "NULL") {
                nextState = {
                    "name" : "NULL",
                    "displaytext" : "None"
                };
            }
            var outEvent = null;
            var oute = outputEntry.value.outgoingEvent;
            if (oute != null && oute.name != null && oute.version != null) {
                outEvent = {
                    "name" : oute.name,
                    "version" : oute.version,
                    "displaytext" : oute.name + ":" + oute.version
                };
            }
            editPolicyForm_State_addStateDirectOutput(dir_outputstable_body, (createEditOrView == "VIEW"), statename,
                    state, outputName, nextState, outEvent, stateNextStateOptions, events);
        }
    }
    // add the new Direct output button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var dir_outputTR = document.createElement("tr");
        dir_outputTR.setAttribute("class", "field-policyoutput-tr.new");
        dir_outputstable_body.appendChild(dir_outputTR);
        var dir_outputTD = document.createElement("td");
        dir_outputTD.setAttribute("colspan", "4");
        dir_outputTR.appendChild(dir_outputTD);
        var addStateDirOutput = createAddFormButton("Add New Direct State Output Mapping")
        dir_outputTD.appendChild(addStateDirOutput);
        // addStateDirOutput.setAttribute("id",
        // "editPolicyFormDirOutputsAddOutput"+"_"+statename);
        // addStateDirOutput.setAttribute("class", "ebBtn ebBtn_large");
        // addStateDirOutput.setAttribute("type", "submit");
        // addStateDirOutput.setAttribute("value", Add New Direct State Output
        // Mapping);
        // addStateDirOutput.innerHTML =
        // addStateDirOutput.getAttribute("value");
        addStateDirOutput.onclick = function() {
            return editPolicyForm_State_addStateDirectOutput(dir_outputstable_body, false, statename, state, null,
                    null, null, stateNextStateOptions, events);
        };
    }
    // Logic-based Output Mappings
    var logic_outputsLI = document.createElement("li");
    outsUL.appendChild(logic_outputsLI);
    var logic_outputsLabel = document.createElement("label");
    logic_outputsLI.appendChild(dir_outputsLabel);
    logic_outputsLI.innerHTML = "Logic-based State Output Mappings / Finalizers: ";
    logic_outputsLabel.setAttribute("for", "editPolicyFormLogicOutputsTable" + "_" + statename);
    var logic_outputstable = document.createElement("table");
    logic_outputstable.setAttribute("id", "editPolicyFormLogicOutputsTable" + "_" + statename);
    logic_outputstable.setAttribute("name", "editPolicyFormLogicOutputsTable" + "_" + statename);
    logic_outputstable.setAttribute("class", "table-policyoutput");
    logic_outputsLI.appendChild(logic_outputstable);
    var logic_outputstable_head = document.createElement("thead");
    logic_outputstable.appendChild(logic_outputstable_head);
    var logic_outputstable_head_tr = document.createElement("tr");
    logic_outputstable_head.appendChild(logic_outputstable_head_tr);
    logic_outputstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                            // for
                                                                            // delete
                                                                            // button
    var logic_outputstable_name_head_th = document.createElement("th");
    logic_outputstable_head_tr.appendChild(logic_outputstable_name_head_th);
    logic_outputstable_name_head_th.innerHTML = "Local Name for Output Mapping: ";
    logic_outputstable_name_head_th.setAttribute("class", "table-policyoutput-heading form-heading");
    var logic_outputstablelogic_head_th = document.createElement("th");
    logic_outputstable_head_tr.appendChild(logic_outputstablelogic_head_th);
    logic_outputstablelogic_head_th.style.textAlign = "left";
    logic_outputstablelogic_head_th.innerHTML = "Output Mapping / Finalizer Logic: ";
    logic_outputstablelogic_head_th.setAttribute("class", "table-policyoutput-heading form-heading");
    var logic_outputstable_body = document.createElement("tbody");
    logic_outputstable.appendChild(logic_outputstable_body);
    if (state && state.stateFinalizerLogicMap && $.isArray(state.stateFinalizerLogicMap.entry)) {
        for (var p = 0; p < state.stateFinalizerLogicMap.entry.length; p++) {
            var outputEntry = state.stateFinalizerLogicMap.entry[p];
            var outputName = outputEntry.key;
            var logic = null;
            if (outputEntry.value != null && outputEntry.value.logic != null) {
                logic = outputEntry.value.logic;
            }
            var flavour = null;
            if (outputEntry.value != null && outputEntry.value.logicFlavour != null) {
                flavour = outputEntry.value.logicFlavour;
            }
            editPolicyForm_State_addStateLogicOutput(logic_outputstable_body, (createEditOrView == "VIEW"), statename,
                    state, outputName, logic, flavour);
        }
    }
    // add the new Logic output button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var logic_outputTR = document.createElement("tr");
        logic_outputTR.setAttribute("class", "field-policyoutput-tr.new");
        logic_outputstable_body.appendChild(logic_outputTR);
        var logic_outputTD = document.createElement("td");
        logic_outputTD.setAttribute("colspan", "3");
        logic_outputTR.appendChild(logic_outputTD);
        var addStateLogicOutput = createAddFormButton("Add New Logic-based State Output Mapping / Finalizer");
        logic_outputTD.appendChild(addStateLogicOutput);
        // addStateLogicOutput.setAttribute("id",
        // "editPolicyFormLogicOutputsAddOutput"+"_"+statename);
        // addStateLogicOutput.setAttribute("class", "ebBtn ebBtn_large");
        // addStateLogicOutput.setAttribute("type", "submit");
        // addStateLogicOutput.setAttribute("value", "Add New Logic-based State
        // Output Mapping / Finalizer");
        // addStateLogicOutput.innerHTML =
        // addStateLogicOutput.getAttribute("value");
        addStateLogicOutput.onclick = function() {
            return editPolicyForm_State_addStateLogicOutput(logic_outputstable_body, false, statename, state, null,
                    null, null);
        };
    }
    return retDiv;
}
function editPolicyForm_State_addStateLogicOutput(parentTBody, disabled, statename, state, outputName, logic, flavour) {
    var random_suffix = formUtils_generateUUID();
    var outputTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    outputTR.style.verticalAlign = "top";
    outputTR.style.textAlign = "left";
    outputTR.setAttribute("finalizer_id", random_suffix);
    outputTR.setAttribute("class", "field-policyoutput-tr");
    if (outputName == null && !disabled) {
        outputTR.setAttribute("class", "field-policyoutput-tr.new");
    }
    // delete
    var deleteTD = document.createElement("td");
    outputTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(outputTR).hide("fast", function() {
                outputTR.parentNode.removeChild(outputTR);
            });
        };
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    // name
    var nameTD = document.createElement("td");
    nameTD.style.whiteSpace = "nowrap";
    outputTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editPolicyFormLogicOutputNameValue" + "_" + statename + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editPolicyFormLogicOutputNameValue" + "_" + statename + "_" + random_suffix);
    nameInput.setAttribute("class", "field-policy-outputname");
    if (outputName == null && logic == null && flavour == null && !disabled) {
        nameInput.setAttribute("class", "field-policy-outputname.new ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "A name for this Output mapper");
    if (outputName) {
        nameInput.value = outputName;
    }
    nameInput.readOnly = disabled;
    // logic
    var logicTD = document.createElement("td");
    logicTD.style.width = "99%";
    logicTD.style.whiteSpace = "nowrap";
    outputTR.appendChild(logicTD);
    var finalizerlogicdiv = document.createElement("div");
    logicTD.appendChild(finalizerlogicdiv);
    finalizerlogicdiv.setAttribute("id", "editEventFormFinalizerLogicDiv" + "_" + statename + "_" + random_suffix);
    var finalizerlogicLabel = document.createElement("label");
    finalizerlogicdiv.appendChild(finalizerlogicLabel);
    finalizerlogicLabel.setAttribute("for", "editEventFormfinalizerLogicInput" + "_" + statename + "_" + random_suffix);
    finalizerlogicLabel.innerHTML = "Logic: ";

    var logicString = "";
    if (logic != null) {
        logicString = logic;
    }
    var edit_readOnly = disabled;
    var textarea = showHideTextarea("editEventFormfinalizerLogicInput" + "_" + statename + "_" + random_suffix, logic,
            false, !edit_readOnly, false);
    finalizerlogicdiv.appendChild(textarea);

    // finalizerlogic type
    var finalizerlogicTypeLabel = document.createElement("label");
    finalizerlogicdiv.appendChild(finalizerlogicTypeLabel);
    finalizerlogicTypeLabel.setAttribute("for", "editPolicyFormFinalizerLogicTypeInput" + "_" + statename + "_"
            + random_suffix);
    finalizerlogicTypeLabel.innerHTML = "Type / Flavour: ";
    var finalizerlogicTypeInput = document.createElement("input");
    finalizerlogicdiv.appendChild(finalizerlogicTypeInput);
    finalizerlogicTypeInput.setAttribute("id", "editPolicyFormFinalizerLogicTypeInput" + "_" + statename + "_"
            + random_suffix);
    finalizerlogicTypeInput.setAttribute("type", "text");
    finalizerlogicTypeInput.setAttribute("name", "editPolicyFormFinalizerLogicTypeInput" + "_" + statename + "_"
            + random_suffix);
    finalizerlogicTypeInput.setAttribute("class", "field-finalizerLogicType ebInput ebInput_width_xLong");
    finalizerlogicTypeInput.setAttribute("placeholder", "MVEL");
    if (flavour != null) {
        finalizerlogicTypeInput.value = flavour;
    }
    finalizerlogicTypeInput.readOnly = disabled;

    logicTD.appendChild(finalizerlogicdiv);
}

function editPolicyForm_State_addStateDirectOutput(parentTBody, disabled, stateName, state, outputName, nextState,
        outEvent, stateNextStateOptions, events) {
    var random_suffix = formUtils_generateUUID();
    var outputTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    outputTR.setAttribute("output_id", random_suffix);
    outputTR.setAttribute("class", "field-policyoutput-tr");
    if (outputName == null && nextState == null && nextState == null && !disabled) {
        outputTR.setAttribute("class", "field-policyoutput-tr.new");
    }
    // delete
    var deleteTD = document.createElement("td");
    outputTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(outputTR).hide("fast", function() {
                outputTR.parentNode.removeChild(outputTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    // name
    var nameTD = document.createElement("td");
    outputTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editPolicyFormDirectOutputNameValue" + "_" + stateName + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editPolicyFormDirectOutputNameValue" + "_" + stateName + "_" + random_suffix);
    nameInput.setAttribute("class", "field-policy-outputname ebInput ebInput_width_xLong");
    if (outputName == null && nextState == null && nextState == null && !disabled) {
        nameInput.setAttribute("class", "field-policy-outputname.new ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "A name for this Output mapper");
    if (outputName) {
        nameInput.value = outputName;
    }
    nameInput.readOnly = disabled;
    // outputEvent
    var outeventTD = document.createElement("td");
    var outeventSelectDiv = dropdownList("editPolicyFormOutputEventValue" + "_" + stateName + "_" + random_suffix,
            events, outEvent, disabled, null);
    outeventTD.appendChild(outeventSelectDiv);
    // nextstate
    var nextstateTD = document.createElement("td");
    var nextstateSelectDiv = dropdownList("editPolicyFormOutputNextStateValue" + "_" + stateName + "_" + random_suffix,
            stateNextStateOptions, nextState, disabled, null, function() {
                return editPolicyForm_getNextStateOptions();
            });
    nextstateTD.appendChild(nextstateSelectDiv);

    outputTR.appendChild(nextstateTD);
    outputTR.appendChild(outeventTD);

}

function editPolicyForm_State_addPolicyContext(parentTBody, disabled, stateName, contextName, contextreference,
        contextAlbums) {
    var random_suffix = formUtils_generateUUID();
    var contextTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    contextTR.setAttribute("context_id", random_suffix);
    contextTR.setAttribute("class", "field-policycontext-tr");
    if (contextName == null && contextreference == null && !disabled) {
        contextTR.setAttribute("class", "field-policycontext-tr.new");
    }
    // delete
    var deleteTD = document.createElement("td");
    contextTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(contextTR).hide("fast", function() {
                contextTR.parentNode.removeChild(contextTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    // context
    var valueTD = document.createElement("td");
    contextTR.appendChild(valueTD);
    var selectDiv = dropdownList("editPolicyFormContextValue" + "_" + stateName + "_" + random_suffix, contextAlbums,
            contextreference, disabled, null);
    valueTD.appendChild(selectDiv);
}

function editPolicyForm_State_addPolicyTask(parentTBody, disabled, isdefault, state, stateName, taskreference,
        taskSelected, taskOptions) {
    var random_suffix = formUtils_generateUUID();
    var taskTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    taskTR.setAttribute("task_id", random_suffix);
    taskTR.setAttribute("class", "field-policytask-tr");
    if (taskreference && taskSelected == null && !disabled) {
        taskTR.setAttribute("class", "field-policytask-tr.new");
    }
    // delete
    var deleteTD = document.createElement("td");
    taskTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(taskTR).hide("fast", function() {
                taskTR.parentNode.removeChild(taskTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    // default
    var defaulttaskTD = document.createElement("td");
    taskTR.appendChild(defaulttaskTD);
    var defaulttaskInput = document.createElement("input");
    defaulttaskTD.appendChild(defaulttaskInput);
    defaulttaskInput.setAttribute("id", "editPolicyFormTaskIsDefault" + "_" + stateName + "_" + random_suffix);
    defaulttaskInput.setAttribute("value", "editPolicyFormTaskIsDefault" + "_" + stateName + "_" + random_suffix);
    defaulttaskInput.setAttribute("type", "radio");
    defaulttaskInput.setAttribute("name", "editPolicyFormTaskIsDefault" + "_" + stateName); // group
                                                                                            // name
    defaulttaskInput.setAttribute("class", "field-policy-taskisdefault");
    if (taskreference == null && taskSelected == null && !disabled) {
        defaulttaskInput.setAttribute("class", "field-policy-taskisdefault.new");
    }
    defaulttaskInput.checked = isdefault;
    defaulttaskInput.readOnly = disabled;
    // localname
    var localnameTD = document.createElement("td");
    taskTR.appendChild(localnameTD);
    var localnameInput = document.createElement("input");
    localnameTD.appendChild(localnameInput);
    localnameInput.setAttribute("id", "editPolicyFormTaskLocalNameValue" + "_" + stateName + "_" + random_suffix);
    localnameInput.setAttribute("type", "text");
    localnameInput.setAttribute("name", "editPolicyFormTaskLocalNameValue" + "_" + stateName + "_" + random_suffix);
    localnameInput.setAttribute("class", "field-policy-tasklocalname ebInput ebInput_width_xLong");
    if (taskreference == null && taskSelected == null && !disabled) {
        localnameInput.setAttribute("class", "field-policy-tasklocalname.new ebInput ebInput_width_xLong");
    }
    localnameInput.setAttribute("placeholder", "Task's Local Name");
    if (taskreference != null && taskreference.key != null && taskreference.key.localName != null) {
        localnameInput.value = taskreference.key.localName;
    }
    localnameInput.readOnly = disabled;
    // task
    var valueTD = document.createElement("td");
    taskTR.appendChild(valueTD);
    var selectDiv = dropdownList("editPolicyFormTaskValue" + "_" + stateName + "_" + random_suffix, taskOptions,
            taskSelected, disabled, null);
    valueTD.appendChild(selectDiv);
    // output type
    var outputTypeDirectTD = document.createElement("td");
    taskTR.appendChild(outputTypeDirectTD);
    var outputTypeDirectLabel = document.createElement("label");
    outputTypeDirectTD.appendChild(outputTypeDirectLabel);
    outputTypeDirectLabel.setAttribute("for", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix
            + "_DIRECT");
    outputTypeDirectLabel.setAttribute("class", "label-policy-taskoutputtype");
    outputTypeDirectLabel.innerHTML = " DIRECT";
    var outputTypeDirectInput = document.createElement("input");
    outputTypeDirectLabel.appendChild(outputTypeDirectInput);
    outputTypeDirectInput.setAttribute("id", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix
            + "_DIRECT");
    outputTypeDirectInput.setAttribute("value", "DIRECT");
    outputTypeDirectInput.setAttribute("type", "radio");
    outputTypeDirectInput.setAttribute("name", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix); // group
                                                                                                                        // name
    outputTypeDirectInput.setAttribute("class", "field-policy-taskoutputtype");
    if (taskreference == null && taskSelected == null && !disabled) {
        outputTypeDirectInput.setAttribute("class", "field-policy-taskoutputtype.new");
    }
    if (taskreference != null && taskreference.outputType != null && taskreference.outputType.toUpperCase() == "DIRECT") {
        outputTypeDirectInput.checked = true;
    }
    outputTypeDirectInput.readOnly = disabled;
    var outputTypeLogicTD = document.createElement("td");
    taskTR.appendChild(outputTypeLogicTD);
    var outputTypeLogicLabel = document.createElement("label");
    outputTypeLogicTD.appendChild(outputTypeLogicLabel);
    outputTypeLogicLabel.setAttribute("for", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix
            + "_LOGIC");
    outputTypeLogicLabel.setAttribute("class", "label-policy-taskoutputtype");
    outputTypeLogicLabel.innerHTML = " LOGIC";
    var outputTypeLogicInput = document.createElement("input");
    outputTypeLogicLabel.appendChild(outputTypeLogicInput);
    outputTypeLogicInput.setAttribute("id", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix
            + "_LOGIC");
    outputTypeLogicInput.setAttribute("value", "LOGIC");
    outputTypeLogicInput.setAttribute("type", "radio");
    outputTypeLogicInput.setAttribute("name", "editPolicyFormTaskOutputType" + "_" + stateName + "_" + random_suffix); // group
                                                                                                                        // name
    outputTypeLogicInput.setAttribute("class", "field-policy-taskoutputtype");
    if (taskreference && taskSelected == null && !disabled) {
        outputTypeLogicInput.setAttribute("class", "field-policy-taskoutputtype.new");
    }
    if (taskreference != null && taskreference.outputType != null && taskreference.outputType.toUpperCase() == "LOGIC") {
        outputTypeLogicInput.checked = true;
    }
    outputTypeLogicInput.readOnly = disabled;
    // output selected
    var outputSelectionTD = document.createElement("td");
    taskTR.appendChild(outputSelectionTD);
    var dir_outputselected = null;
    var logic_outputselected = null;
    if (taskreference != null && taskreference.output != null && taskreference.output.localName != null
            && taskreference.outputType != null && taskreference.outputType.toUpperCase() == "DIRECT") {
        dir_outputselected = {
            "name" : taskreference.output.localName,
            "displaytext" : taskreference.output.localName
        };
    } else if (taskreference != null && taskreference.output != null && taskreference.output.localName != null
            && taskreference.outputType != null && taskreference.outputType.toUpperCase() == "LOGIC") {
        logic_outputselected = {
            "name" : taskreference.output.localName,
            "displaytext" : taskreference.output.localName
        };
    }
    var dir_outputOptions = new Array();
    if (state != null && state.stateOutputs != null && $.isArray(state.stateOutputs.entry)) {
        for (var p = 0; p < state.stateOutputs.entry.length; p++) {
            var outputEntry = state.stateOutputs.entry[p].key;
            dir_outputOptions.push({
                "name" : outputEntry,
                "displaytext" : outputEntry
            });
        }
    }
    var logic_outputOptions = new Array();
    if (state != null && state.stateFinalizerLogicMap != null && $.isArray(state.stateFinalizerLogicMap.entry)) {
        for (var p = 0; p < state.stateFinalizerLogicMap.entry.length; p++) {
            var outputEntry = state.stateFinalizerLogicMap.entry[p].key;
            logic_outputOptions.push({
                "name" : outputEntry,
                "displaytext" : outputEntry
            });
        }
    }
    var dir_selectDiv = document.createElement("div");
    dir_selectDiv.appendChild(new dropdownList("editPolicyFormTaskDirectOutputSelection" + "_" + stateName + "_"
            + random_suffix, dir_outputOptions, dir_outputselected, disabled, null, function() {
        return editPolicyForm_State_getDirectOutputMappingOptions(stateName);
    }));
    outputSelectionTD.appendChild(dir_selectDiv);
    var logic_selectDiv = document.createElement("div");
    logic_selectDiv.appendChild(dropdownList("editPolicyFormTaskLogicOutputSelection" + "_" + stateName + "_"
            + random_suffix, logic_outputOptions, logic_outputselected, disabled, null, function() {
        return editPolicyForm_State_getLogicOutputMappingOptions(stateName);
    }));
    outputSelectionTD.appendChild(logic_selectDiv);
    if (outputTypeLogicInput.checked) {
        dir_selectDiv.style.display = "none";
        logic_selectDiv.style.display = "inline";
    } else if (outputTypeDirectInput.checked) {
        dir_selectDiv.style.display = "inline";
        logic_selectDiv.style.display = "none";
    } else {
        dir_selectDiv.style.display = "none";
        logic_selectDiv.style.display = "none";
    }
    outputTypeDirectInput.onclick = function() {
        if (this.checked) {
            dir_selectDiv.style.display = "inline";
            logic_selectDiv.style.display = "none";
        } else {
            dir_selectDiv.style.display = "none";
            logic_selectDiv.style.display = "inline";
        }
    };
    outputTypeLogicInput.onclick = function() {
        if (this.checked) {
            dir_selectDiv.style.display = "none";
            logic_selectDiv.style.display = "inline";
        } else {
            dir_selectDiv.style.display = "inline";
            logic_selectDiv.style.display = "none";
        }
    };
}

function editPolicyForm_State_getLogicOutputMappingOptions(statename) {
    var outputoptions = new Array();
    var finalizerstablerows = document.getElementById("editPolicyFormLogicOutputsTable_" + statename).rows;
    if (finalizerstablerows && finalizerstablerows.length > 2) { // has head
                                                                    // so just
                                                                    // ignore
                                                                    // (2) top
                                                                    // row and
                                                                    // bottom
                                                                    // row
        for (var i = 1; i < finalizerstablerows.length - 1; i++) {
            var finalizerTR = finalizerstablerows[i];
            if (finalizerTR && finalizerTR.getAttribute("finalizer_id")) {
                var finalizer_id = finalizerTR.getAttribute("finalizer_id");
                var finalizerlocalname = document.getElementById("editPolicyFormLogicOutputNameValue_" + statename
                        + "_" + finalizer_id).value;
                if (finalizerlocalname != null && finalizerlocalname != "") {
                    outputoptions.push({
                        "name" : finalizerlocalname,
                        "displaytext" : finalizerlocalname
                    });
                }
            }
        }
    }
    return outputoptions;
}

function editPolicyForm_State_getDirectOutputMappingOptions(statename) {
    var outputoptions = new Array();
    var outputstablerows = document.getElementById("editPolicyFormDirOutputsTable_" + statename).rows;
    if (outputstablerows && outputstablerows.length > 2) { // has head so just
                                                            // ignore (2) top
                                                            // row and bottom
                                                            // row
        for (var i = 1; i < outputstablerows.length - 1; i++) {
            var outputTR = outputstablerows[i];
            if (outputTR && outputTR.getAttribute("output_id")) {
                var output_id = outputTR.getAttribute("output_id");
                var outputlocalname = document.getElementById("editPolicyFormDirectOutputNameValue_" + statename + "_"
                        + output_id).value;
                if (outputlocalname != null && outputlocalname != "") {
                    outputoptions.push({
                        "name" : outputlocalname,
                        "displaytext" : outputlocalname
                    });
                }
            }
        }
    }
    return outputoptions;
}

function editPolicyForm_State_getStateBean(statename) {
    if (statename == null || statename == "") {
        console.error("Request for '" + statename + "' state!");
        alert("Request for '" + statename + "' state!");
        return null;
    }
    var div = document.getElementById("editPolicyFormStateDiv_" + statename);
    if (div == null) {
        console.error("State information requested for state " + statename + ", but that state does not exist!")
        alert("State information requested for state " + statename + ", but that state does not exist!");
        return null;
    }
    var ret = new Object();
    // name
    ret["name"] = statename;
    // trigger
    var triggervalue = document.getElementById("editPolicyFormTrigger_" + statename + "_dropdownList").selectedOption;
    ret["trigger"] = null;
    if (triggervalue != null && triggervalue.event != null) {
        ret.trigger = {
            "name" : triggervalue.event.key.name,
            "version" : triggervalue.event.key.version
        };
    }
    // context
    var statebean_context = new Array();
    var contextstablerows = document.getElementById("editPolicyFormContextsTable_" + statename).rows;
    if (contextstablerows && contextstablerows.length > 1) { // no head so
                                                                // just ignore
                                                                // (1) bottom
                                                                // row
        for (var i = 0; i < contextstablerows.length - 1; i++) {
            var contextTR = contextstablerows[i];
            if (contextTR && contextTR.getAttribute("context_id")) {
                var context_id = contextTR.getAttribute("context_id");
                var contextvalue = document.getElementById("editPolicyFormContextValue_" + statename + "_" + context_id
                        + "_dropdownList").selectedOption;
                if (contextvalue != null && contextvalue.album != null) {
                    statebean_context.push({
                        "name" : contextvalue.album.key.name,
                        "version" : contextvalue.album.key.version
                    });
                }
            }
        }
    }
    ret["contexts"] = statebean_context;
    // outputs
    var statebean_outputs = new Object();
    var outputstablerows = document.getElementById("editPolicyFormDirOutputsTable_" + statename).rows;
    if (outputstablerows && outputstablerows.length > 2) { // has head so just
                                                            // ignore (2) top
                                                            // row and bottom
                                                            // row
        for (var i = 1; i < outputstablerows.length - 1; i++) {
            var outputTR = outputstablerows[i];
            if (outputTR && outputTR.getAttribute("output_id")) {
                var output_id = outputTR.getAttribute("output_id");
                var outputlocalname = document.getElementById("editPolicyFormDirectOutputNameValue_" + statename + "_"
                        + output_id).value;
                if (outputlocalname == null || outputlocalname == "") {
                    console.error("No Local Name entered for Direct Output Mapping #" + i + " for state " + statename);
                    alert("No Local Name entered for Direct Output Mapping #" + i + " for state " + statename);
                    return null;
                }
                var nextstatevalue = document.getElementById("editPolicyFormOutputNextStateValue_" + statename + "_"
                        + output_id + "_dropdownList").selectedOption;
                var nextstatename;
                if (nextstatevalue == null) {
                    console.error("An option must be selected for Next State for Direct Output Mapping "
                            + outputlocalname + " for state " + statename);
                    alert("An option must be selected for Next State for Direct Output Mapping " + outputlocalname
                            + " for state " + statename);
                    return null;
                } else if (nextstatevalue.name.toUpperCase() == "NULL") {
                    nextstatename = null;
                } else {
                    nextstatename = nextstatevalue.name;
                }

                var nexteventvalue = document.getElementById("editPolicyFormOutputEventValue_" + statename + "_"
                        + output_id + "_dropdownList").selectedOption;
                if (nexteventvalue == null || nexteventvalue.event == null || nexteventvalue.event.key == null) {
                    console.error("No Output Event selected for Direct Output Mapping " + outputlocalname
                            + " for state " + statename);
                    alert("No Output Event selected for Direct Output Mapping " + outputlocalname + " for state "
                            + statename);
                    return null;
                }
                var nextevent = {
                    "name" : nexteventvalue.event.key.name,
                    "version" : nexteventvalue.event.key.version
                };
                statebean_outputs[outputlocalname] = {
                    "event" : nextevent,
                    "nextState" : nextstatename
                };
            }
        }
    }
    ret["stateOutputs"] = statebean_outputs;
    // finalizers
    var statebean_finalizers = new Object();
    var finalizerstablerows = document.getElementById("editPolicyFormLogicOutputsTable_" + statename).rows;
    if (finalizerstablerows && finalizerstablerows.length > 2) { // has head
                                                                    // so just
                                                                    // ignore
                                                                    // (2) top
                                                                    // row and
                                                                    // bottom
                                                                    // row
        for (var i = 1; i < finalizerstablerows.length - 1; i++) {
            var finalizerTR = finalizerstablerows[i];
            if (finalizerTR && finalizerTR.getAttribute("finalizer_id")) {
                var finalizer_id = finalizerTR.getAttribute("finalizer_id");
                var finalizerlocalname = document.getElementById("editPolicyFormLogicOutputNameValue_" + statename
                        + "_" + finalizer_id).value;
                if (finalizerlocalname == null || finalizerlocalname == "") {
                    console.error("No Local Name entered for Logic-based Output Mapping #" + i + " for state "
                            + statename);
                    alert("No Local Name entered for Logic-based Output Mapping #" + i + " for state " + statename);
                    return null;
                }

                var finalizerlogicvalue = document.getElementById("editEventFormfinalizerLogicInput_" + statename + "_"
                        + finalizer_id + "_textarea").value;
                if (finalizerlogicvalue == null || finalizerlogicvalue == "") {
                    console.error("No Logic is specified for Logic-based Output Mapping " + finalizerlocalname
                            + " for state " + statename);
                    alert("No Logic is specified for Logic-based Output Mapping " + finalizerlocalname + " for state "
                            + statename);
                    return null;
                }

                var finalizerlogictypevalue = document.getElementById("editPolicyFormFinalizerLogicTypeInput_"
                        + statename + "_" + finalizer_id).value;
                if (finalizerlogictypevalue == null || finalizerlogictypevalue == "") {
                    console.error("No Logic Type is specified for Logic-based Output Mapping " + finalizerlocalname
                            + " for state " + statename);
                    alert("No Logic Type is specified for Logic-based Output Mapping " + finalizerlocalname
                            + " for state " + statename);
                    return null;
                }
                statebean_finalizers[finalizerlocalname] = {
                    "logic" : finalizerlogicvalue,
                    "logicFlavour" : finalizerlogictypevalue
                };
            }
        }
    }
    ret["finalizers"] = statebean_finalizers;
    // tasks & defaulttask
    var statebean_tasks = new Object();
    var statebean_defaultTask = null;
    var taskstablerows = document.getElementById("editPolicyFormTasksTable_" + statename).rows;
    if (taskstablerows == null || taskstablerows.length <= 2) {
        alert("No tasks selected for state " + statename);
        console.error("No tasks selected for state " + statename);
        return null;
    } else { // has head so just ignore (2) top row and bottom row
        for (var i = 1; i < taskstablerows.length - 1; i++) {
            var taskTR = taskstablerows[i];
            if (taskTR && taskTR.getAttribute("task_id")) {
                var task_id = taskTR.getAttribute("task_id");
                var tasklocalname = document.getElementById("editPolicyFormTaskLocalNameValue_" + statename + "_"
                        + task_id).value;
                if (tasklocalname == "") {
                    console.error("No Local Name entered for task #" + i + " for state " + statename);
                    alert("No Local Name entered for task #" + i + " for state " + statename);
                    return null;
                }
                if (statebean_tasks[tasklocalname] != null) {
                    console.error("There cannot be more than one task called " + tasklocalname + " for state "
                            + statename);
                    alert("There cannot be more than one task called " + tasklocalname + " for state " + statename);
                    return null;
                }
                var taskvalue = document.getElementById("editPolicyFormTaskValue_" + statename + "_" + task_id
                        + "_dropdownList").selectedOption;
                if (taskvalue == null || taskvalue.task == null) {
                    console.error("No Task selected for task " + tasklocalname + " for state " + statename);
                    alert("No Task selected for task " + tasklocalname + " for state " + statename);
                    return null;
                }
                var task = taskvalue.task;
                var r = document.querySelector('input[name="editPolicyFormTaskOutputType_' + statename + '_' + task_id
                        + '"]:checked');
                if (r == null) {
                    console.error("No Output Mapping type selected for task " + tasklocalname + " for state "
                            + statename);
                    alert("No Output Mapping type selected for task " + tasklocalname + " for state " + statename);
                    return null;
                }
                var outputtype = r.value;
                var outputname = null;
                if (outputtype == "DIRECT") {
                    var diroutput = document.getElementById("editPolicyFormTaskDirectOutputSelection_" + statename
                            + "_" + task_id + "_dropdownList").selectedOption;
                    if (diroutput == null) {// } || diroutput.output == null){
                        console.error("No DIRECT Output Mapping selected for task " + tasklocalname + " for state "
                                + statename);
                        alert("No DIRECT Output Mapping selected for task " + tasklocalname + " for state " + statename);
                    }
                    outputname = diroutput.name;
                } else if (outputtype == "LOGIC") {
                    var logoutput = document.getElementById("editPolicyFormTaskLogicOutputSelection_" + statename + "_"
                            + task_id + "_dropdownList").selectedOption;
                    if (logoutput == null || logoutput.name == null) {
                        console.error("No LOGIC Output Mapping selected for task " + tasklocalname + " for state "
                                + statename);
                        alert("No LOGIC Output Mapping selected for task " + tasklocalname + " for state " + statename);
                    }
                    outputname = logoutput.name;
                } else {
                    console.error("Unknown Output Mapping type ('" + outputtype + "') selected for task "
                            + tasklocalname + " for state " + statename);
                    alert("Unknown Output Mapping type ('" + outputtype + "') selected for task " + tasklocalname
                            + " for state " + statename);
                    return null;
                }
                statebean_tasks[tasklocalname] = {
                    "task" : {
                        "name" : task.key.name,
                        "version" : task.key.version
                    },
                    "outputType" : outputtype,
                    "outputName" : outputname
                };

                var r2 = document.getElementById("editPolicyFormTaskIsDefault_" + statename + "_" + task_id);
                if (taskstablerows.length <= 3 || (r2 != null && r2.checked == true)) { // default
                                                                                        // is
                                                                                        // checked
                                                                                        // or
                                                                                        // there
                                                                                        // is
                                                                                        // only
                                                                                        // one
                                                                                        // task
                    statebean_defaultTask = {
                        "name" : task.key.name,
                        "version" : task.key.version
                    };
                }

            }
        }
    }
    ret["tasks"] = statebean_tasks;
    ret["defaultTask"] = statebean_defaultTask;
    // tasksellogic
    var tsl = document.getElementById("editEventFormTaskSelLogicInput_" + statename + "_textarea").value;
    var tsl_type = document.getElementById("editPolicyFormTaskSelLogicTypeInput_" + statename).value;
    if (tsl == null || tsl == "" || tsl_type == null || tsl_type == "") {
        if (statebean_tasks != null && taskstablerows.length > 3) { // there is
                                                                    // more than
                                                                    // 1 task
            console
                    .error("State "
                            + statename
                            + " has more than one task reference so Task Selection Logic and Task Selection Logic type must be specified");
            alert("State "
                    + statename
                    + " has more than one task reference so Task Selection Logic and Task Selection Logic type must be specified");
            return null;
        }
    } else if ((tsl == null || tsl == "") && (tsl_type == null || tsl_type == "")) {
        ret["taskSelectionLogic"] = null;
    } else {
        ret["taskSelectionLogic"] = {
            "logic" : tsl,
            "logicFlavour" : tsl_type
        };
    }

    return ret;

}
