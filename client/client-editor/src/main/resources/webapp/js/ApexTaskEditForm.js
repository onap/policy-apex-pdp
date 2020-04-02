/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

function editTaskForm_createTask(formParent) {
    // Get all contextSchemas too for task input/outputfields
    var requestURL = restRootURL + "/ContextSchema/Get?name=&version=";
    var contextSchemas = new Array();
    ajax_get(requestURL, function(data2) {
        for (var i = 0; i < data2.messages.message.length; i++) {
            var contextSchema = JSON.parse(data2.messages.message[i]).apexContextSchema;
            var dt = {
                "name" : contextSchema.key.name,
                "version" : contextSchema.key.version,
                "displaytext" : contextSchema.key.name + ":" + contextSchema.key.version,
                "contextSchema" : contextSchema
            };
            contextSchemas.push(dt);
        }
        // Get all contextAlbums too for task context album references
        var requestURL = restRootURL + "/ContextAlbum/Get?name=&version=";
        var contextAlbums = new Array();
        ajax_get(requestURL, function(data3) {
            for (var i = 0; i < data3.messages.message.length; i++) {
                var contextAlbum = JSON.parse(data3.messages.message[i]).apexContextAlbum;
                var ca = {
                    "name" : contextAlbum.key.name,
                    "version" : contextAlbum.key.version,
                    "displaytext" : contextAlbum.key.name + ":" + contextAlbum.key.version,
                    "contextAlbum" : contextAlbum
                };
                contextAlbums.push(ca);
            }
            editTaskForm_activate(formParent, "CREATE", null, contextSchemas, contextAlbums);
        });
    });
}

function editTaskForm_deleteTask(parent, name, version) {
    var message = "Are you sure you want to delete Task \"" + name + ":" + version + "\"?";
    if (apexUtils_areYouSure(message)) {
        var requestURL = restRootURL + "/Task/Delete?name=" + name + "&version=" + version;
        ajax_delete(requestURL, function(data) {
            apexUtils_removeElement("editTaskFormDiv");
            taskTab_reset();
            keyInformationTab_reset()
        });
    }
}

function editTaskForm_viewTask(formParent, name, version) {
    editTaskForm_editTask_inner(formParent, name, version, "VIEW");
}

function editTaskForm_editTask(formParent, name, version) {
    editTaskForm_editTask_inner(formParent, name, version, "EDIT");
}

function editTaskForm_editTask_inner(formParent, name, version, viewOrEdit) {
    var requestURL = restRootURL + "/Task/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexTask", function(task) {
        // Get all contextSchemas too for task inputfields
        var requestURL = restRootURL + "/ContextSchema/Get?name=&version=";
        var contextSchemas = new Array();
        ajax_get(requestURL, function(data2) {
            for (var i = 0; i < data2.messages.message.length; i++) {
                var contextSchema = JSON.parse(data2.messages.message[i]).apexContextSchema;
                contextSchemas.push({
                    "name" : contextSchema.key.name,
                    "version" : contextSchema.key.version,
                    "displaytext" : contextSchema.key.name + ":" + contextSchema.key.version,
                    "contextSchema" : contextSchema
                });
            }
            // Get all contextAlbums too for task context album references
            var requestURL = restRootURL + "/ContextAlbum/Get?name=&version=";
            var contextAlbums = new Array();
            ajax_get(requestURL, function(data3) {
                for (var i = 0; i < data3.messages.message.length; i++) {
                    var contextAlbum = JSON.parse(data3.messages.message[i]).apexContextAlbum;
                    var ca = {
                        "name" : contextAlbum.key.name,
                        "version" : contextAlbum.key.version,
                        "displaytext" : contextAlbum.key.name + ":" + contextAlbum.key.version,
                        "contextAlbum" : contextAlbum
                    };
                    contextAlbums.push(ca);
                }
                editTaskForm_activate(formParent, viewOrEdit, task, contextSchemas, contextAlbums);
            });
        });
    });
}

function editTaskForm_activate(parent, operation, task, contextSchemas, contextAlbums) {
    apexUtils_removeElement("editTaskFormDiv");
    var formParent = document.getElementById(parent);
    apexUtils_emptyElement(parent);

    var isedit = false;
    var createEditOrView = "";

    if (!operation) {
        console.warn("No operation specified for TaskForm form")
    } else {
        createEditOrView = operation.toUpperCase();
    }

    if (createEditOrView == "CREATE") {
        isedit = true;
    } else if (createEditOrView == "EDIT" || createEditOrView == "VIEW") {
        if (createEditOrView == "EDIT") {
            isedit = true;
        }

        if (!task) {
            console.warn("Invalid value (\"" + task + "\") passed as a value for \"task\" for TaskForm form.");
        } else {
            if (!task.key || !task.key.name || task.key.name == "") {
                console.warn("Invalid value (\"" + task.key.name
                        + "\") passed as a value for \"name\" for TaskForm form.");
            }
            if (!task.key || !task.key.version || task.key.version == "") {
                console.warn("Invalid value (\"" + task.key.version
                        + "\") passed as a value for \"version\" for TaskForm form.");
            }
            if (!task.uuid || task.uuid == "") {
                console.warn("Invalid value (\"" + task.uuid + "\") passed as a value for \"uuid\" for TaskForm form.");
            }
        }
    } else {
        console.warn("Invalid operation (\"" + operation
                + "\") specified for TaskForm form. Only \"Create\", \"Edit\" and \"View\" operations are supported");
    }

    var contentelement = document.createElement("editTaskFormDiv");
    var formDiv = document.createElement("div");
    contentelement.appendChild(formDiv);
    formDiv.setAttribute("id", "editTaskFormDiv");
    formDiv.setAttribute("class", "editTaskFormDiv");

    var headingSpan = document.createElement("h2");
    formDiv.appendChild(headingSpan);
    headingSpan.innerHTML = "Task Editor";

    var form = document.createElement("editTaskForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "editTaskForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");
    form.setAttribute("createEditOrView", createEditOrView);

    var formul = document.createElement("ul");
    form.appendChild(formul);

    var nameLI = document.createElement("li");
    formul.appendChild(nameLI);
    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);
    nameLabel.setAttribute("for", "editTaskFormNameInput");
    nameLabel.innerHTML = "Name: ";
    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);
    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";
    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);
    nameInput.setAttribute("id", "editTaskFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editTaskFormNameInput");
    nameInput.setAttribute("class", "field ebInput");
    nameInput.setAttribute("placeholder", "name");
    if (task && task.key && task.key.name) {
        nameInput.value = task.key.name;
    }
    if (createEditOrView != "CREATE") {
        nameInput.readOnly = true;
    }

    var versionLI = document.createElement("li");
    formul.appendChild(versionLI);
    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);
    versionLabel.setAttribute("for", "editTaskFormVersionInput");
    versionLabel.innerHTML = "Version: ";
    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);
    versionInput.setAttribute("id", "editTaskFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "editTaskFormVersionInput");
    versionInput.setAttribute("class", "field ebInput");
    versionInput.setAttribute("placeholder", "0.0.1");
    if (task && task.key && task.key.version) {
        versionInput.value = task.key.version;
    }
    if (createEditOrView != "CREATE") {
        versionInput.readOnly = true;
    }

    var uuidLI = document.createElement("li");
    formul.appendChild(uuidLI);
    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);
    uuidLabel.setAttribute("for", "editTaskFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";
    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);
    uuidInput.setAttribute("id", "editTaskFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "editTaskFormUuidInput");
    uuidInput.setAttribute("class", "field-long ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    if (task && task.uuid) {
        uuidInput.value = task.uuid;
    }
    if (createEditOrView != "CREATE") {
        uuidInput.readOnly = true;
    }

    var edit_disabled = (createEditOrView != "CREATE" && createEditOrView != "EDIT");

    // description
    var descriptionLI = document.createElement("li");
    formul.appendChild(descriptionLI);
    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "editTaskFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";
    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);
    descriptionTextArea.setAttribute("id", "editTaskFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "editTaskFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full");
    if (task && task.description) {
        descriptionTextArea.value = task.description;
    }
    descriptionTextArea.readOnly = edit_disabled;

    // input fields
    var inputfieldsLI = document.createElement("li");
    formul.appendChild(inputfieldsLI);
    var inputfieldsLabel = document.createElement("label");
    inputfieldsLI.appendChild(inputfieldsLabel);
    inputfieldsLabel.setAttribute("for", "editTaskFormInputFieldsTable");
    inputfieldsLabel.innerHTML = "Task Input Fields: ";
    var inputfieldstable = document.createElement("table");
    inputfieldstable.setAttribute("id", "editTaskFormInputFieldsTable");
    inputfieldstable.setAttribute("name", "editTaskFormInputFieldsTable");
    inputfieldstable.setAttribute("class", "table-taskinputfield");
    inputfieldsLI.appendChild(inputfieldstable);
    var inputfieldstable_head = document.createElement("thead");
    inputfieldstable.appendChild(inputfieldstable_head);
    var inputfieldstable_head_tr = document.createElement("tr");
    inputfieldstable_head.appendChild(inputfieldstable_head_tr);
    inputfieldstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                        // for
                                                                        // delete
                                                                        // button
    var inputfieldstable_head_th = document.createElement("th");
    inputfieldstable_head_tr.appendChild(inputfieldstable_head_th);
    inputfieldstable_head_th.innerHTML = "Input Field Name: ";
    inputfieldstable_head_th.setAttribute("class", "table-taskinputfield-heading form-heading");
    inputfieldstable_head_th = document.createElement("th");
    inputfieldstable_head_tr.appendChild(inputfieldstable_head_th);
    inputfieldstable_head_th.innerHTML = "Input Field Type/Schema: ";
    inputfieldstable_head_th.setAttribute("class", "table-taskinputfield-heading form-heading");
    inputfieldstable_head_th = document.createElement("th");
    inputfieldstable_head_tr.appendChild(inputfieldstable_head_th);
    inputfieldstable_head_th.innerHTML = "Optional: ";
    inputfieldstable_head_th.setAttribute("class", "table-eventparam-heading form-heading");
    var inputfieldstable_body = document.createElement("tbody");
    inputfieldstable.appendChild(inputfieldstable_body);
    // Add the inputfields
    if (task && task.inputFields && task.inputFields.entry) {
        for (var p = 0; p < task.inputFields.entry.length; p++) {
            var inputfieldEntry = task.inputFields.entry[p];
            var contextSchema = inputfieldEntry.value.fieldSchemaKey;
            contextSchema["displaytext"] = contextSchema.name + ":" + contextSchema.version;
            editTaskForm_addTaskInputField(inputfieldstable_body, (createEditOrView == "VIEW"), inputfieldEntry.key,
                    inputfieldEntry.value.optional, contextSchema, contextSchemas);
        }
    }
    // add the New Input Field button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var inputfieldTR = document.createElement("tr");
        inputfieldTR.setAttribute("class", "field-taskinputfield-tr.new");
        inputfieldstable_body.appendChild(inputfieldTR);
        var inputfieldTD = document.createElement("td");
        inputfieldTD.setAttribute("colspan", "3");
        inputfieldTR.appendChild(inputfieldTD);
        var addInputFieldInput = createAddFormButton();
        inputfieldTD.appendChild(addInputFieldInput);
        addInputFieldInput.onclick = function() {
            editTaskForm_addTaskInputField(inputfieldstable_body, false, null, false, null, contextSchemas);
        };
    }

    // output fields
    var outputfieldsLI = document.createElement("li");
    formul.appendChild(outputfieldsLI);
    var outputfieldsLabel = document.createElement("label");
    outputfieldsLI.appendChild(outputfieldsLabel);
    outputfieldsLabel.setAttribute("for", "editTaskFormOutputFieldsTable");
    outputfieldsLabel.innerHTML = "Task Output Fields: ";
    var outputfieldstable = document.createElement("table");
    outputfieldstable.setAttribute("id", "editTaskFormOutputFieldsTable");
    outputfieldstable.setAttribute("name", "editTaskFormOutputFieldsTable");
    outputfieldstable.setAttribute("class", "table-taskoutputfield");
    outputfieldsLI.appendChild(outputfieldstable);
    var outputfieldstable_head = document.createElement("thead");
    outputfieldstable.appendChild(outputfieldstable_head);
    var outputfieldstable_head_tr = document.createElement("tr");
    outputfieldstable_head.appendChild(outputfieldstable_head_tr);
    outputfieldstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                            // for
                                                                            // delete
                                                                            // button
    var outputfieldstable_head_th = document.createElement("th");
    outputfieldstable_head_tr.appendChild(outputfieldstable_head_th);
    outputfieldstable_head_th.innerHTML = "Output Field Name: ";
    outputfieldstable_head_th.setAttribute("class", "table-taskoutputfield-heading form-heading");
    outputfieldstable_head_th = document.createElement("th");
    outputfieldstable_head_tr.appendChild(outputfieldstable_head_th);
    outputfieldstable_head_th.innerHTML = "Output Field Type/Schema: ";
    outputfieldstable_head_th.setAttribute("class", "table-taskoutputfield-heading form-heading");
    outputfieldstable_head_th = document.createElement("th");
    outputfieldstable_head_tr.appendChild(outputfieldstable_head_th);
    outputfieldstable_head_th.innerHTML = "Optional: ";
    outputfieldstable_head_th.setAttribute("class", "table-eventparam-heading form-heading");
    var outputfieldstable_body = document.createElement("tbody");
    outputfieldstable.appendChild(outputfieldstable_body);
    // Add the outputfields
    if (task && task.outputFields && task.outputFields.entry) {
        for (var p = 0; p < task.outputFields.entry.length; p++) {
            var outputfieldEntry = task.outputFields.entry[p];
            var contextSchema = outputfieldEntry.value.fieldSchemaKey;
            contextSchema["displaytext"] = contextSchema.name + ":" + contextSchema.version;
            editTaskForm_addTaskOutputField(outputfieldstable_body, (createEditOrView == "VIEW"), outputfieldEntry.key,
                    outputfieldEntry.value.optional, contextSchema, contextSchemas);
        }
    }
    // add the New Output Field button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var outputfieldTR = document.createElement("tr");
        outputfieldTR.setAttribute("class", "field-taskoutputfield-tr.new");
        outputfieldstable_body.appendChild(outputfieldTR);
        var outputfieldTD = document.createElement("td");
        outputfieldTD.setAttribute("colspan", "3");
        outputfieldTR.appendChild(outputfieldTD);
        var addOutputFieldInput = createAddFormButton();
        outputfieldTD.appendChild(addOutputFieldInput);
        addOutputFieldInput.onclick = function() {
            editTaskForm_addTaskOutputField(outputfieldstable_body, false, null, false, null, contextSchemas);
        };
    }

    // tasklogic
    var tasklogicLI = document.createElement("li");
    formul.appendChild(tasklogicLI);
    var tasklogicLabel = document.createElement("label");
    tasklogicLI.appendChild(tasklogicLabel);
    tasklogicLabel.setAttribute("for", "editTaskFormTaskLogicTextArea");
    tasklogicLabel.innerHTML = "Task Logic: ";
    var tlogic = null;
    if (task && task.taskLogic && task.taskLogic.logic) {
        tlogic = task.taskLogic.logic;
    }
    // showHideTextarea(id_prefix, content, initialshow, editable, disabled)
    var textarea = showHideTextarea("editTaskFormTaskLogicTextArea", tlogic, false, !edit_disabled, false);

    tasklogicLI.appendChild(textarea);

    // tasklogic type
    var taskLogicTypeLI = document.createElement("li");
    formul.appendChild(taskLogicTypeLI);
    var taskLogicTypeLabel = document.createElement("label");
    taskLogicTypeLI.appendChild(taskLogicTypeLabel);
    taskLogicTypeLabel.setAttribute("for", "editTaskFormTaskLogicTypeInput");
    taskLogicTypeLabel.innerHTML = "Task Logic Type / Flavour: ";
    var taskLogicTypeInput = document.createElement("input");
    taskLogicTypeLI.appendChild(taskLogicTypeInput);
    taskLogicTypeInput.setAttribute("id", "editTaskFormTaskLogicTypeInput");
    taskLogicTypeInput.setAttribute("type", "text");
    taskLogicTypeInput.setAttribute("name", "editTaskFormTaskLogicTypeInput");
    taskLogicTypeInput.setAttribute("class", "field-taskLogicType ebInput");
    taskLogicTypeInput.setAttribute("placeholder", "MVEL");
    if (task && task.taskLogic && task.taskLogic.logicFlavour) {
        taskLogicTypeInput.value = task.taskLogic.logicFlavour;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        taskLogicTypeInput.readOnly = true;
    }

    // parameters
    var paramsLI = document.createElement("li");
    formul.appendChild(paramsLI);
    var paramsLabel = document.createElement("label");
    paramsLI.appendChild(paramsLabel);
    paramsLabel.setAttribute("for", "editTaskFormParamsTable");
    paramsLabel.innerHTML = "Task Parameters: ";
    var paramstable = document.createElement("table");
    paramstable.setAttribute("id", "editTaskFormParamsTable");
    paramstable.setAttribute("name", "editTaskFormParamsTable");
    paramstable.setAttribute("class", "table-taskparam");
    paramsLI.appendChild(paramstable);
    var paramstable_head = document.createElement("thead");
    paramstable.appendChild(paramstable_head);
    var paramstable_head_tr = document.createElement("tr");
    paramstable_head.appendChild(paramstable_head_tr);
    paramstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                    // for
                                                                    // delete
                                                                    // button
    var paramstable_head_th = document.createElement("th");
    paramstable_head_tr.appendChild(paramstable_head_th);
    paramstable_head_th.innerHTML = "Task Parameter Name: ";
    paramstable_head_th.setAttribute("class", "table-taskparam-heading form-heading");
    paramstable_head_th = document.createElement("th");
    paramstable_head_tr.appendChild(paramstable_head_th);
    paramstable_head_th.innerHTML = "Task Parameter Value: ";
    paramstable_head_th.setAttribute("class", "table-taskparam-heading form-heading");
    var paramstable_body = document.createElement("tbody");
    paramstable.appendChild(paramstable_body);
    // Add the params
    if (task && task.taskParameters && task.taskParameters.entry) {
        for (var p = 0; p < task.taskParameters.entry.length; p++) {
            var paramEntry = task.taskParameters.entry[p];
            var paramName = paramEntry.key;
            var paramValue = paramEntry.value.defaultValue;
            editTaskForm_addTaskParameter(paramstable_body, (createEditOrView == "VIEW"), paramName, paramValue);
        }
    }
    // add the Task Parameter button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var paramTR = document.createElement("tr");
        paramTR.setAttribute("class", "field-taskparam-tr.new");
        paramstable_body.appendChild(paramTR);
        var paramTD = document.createElement("td");
        paramTD.setAttribute("colspan", "3");
        paramTR.appendChild(paramTD);
        var addParamInput = createAddFormButton();
        paramTD.appendChild(addParamInput);
        addParamInput.onclick = function() {
            editTaskForm_addTaskParameter(paramstable_body, false, null, null);
        };
    }

    // Context Albums references
    var contextsLI = document.createElement("li");
    formul.appendChild(contextsLI);
    var contextsLabel = document.createElement("label");
    contextsLI.appendChild(contextsLabel);
    contextsLabel.setAttribute("for", "editTaskFormContextsTable");
    contextsLabel.innerHTML = "Context Albums used in Task Logic: ";
    var contextstable = document.createElement("table");
    contextstable.setAttribute("id", "editTaskFormContextsTable");
    contextstable.setAttribute("name", "editTaskFormContextsTable");
    contextstable.setAttribute("class", "table-taskcontext");
    contextsLI.appendChild(contextstable);
    var contextstable_head = document.createElement("thead");
    contextstable.appendChild(contextstable_head);
    var contextstable_head_tr = document.createElement("tr");
    contextstable_head.appendChild(contextstable_head_tr);
    contextstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                        // for
                                                                        // delete
                                                                        // button
    var contextstable_head_th = document.createElement("th");
    contextstable_head_tr.appendChild(contextstable_head_th);
    contextstable_head_th.innerHTML = "Context Album: ";
    contextstable_head_th.setAttribute("class", "table-taskcontext-heading form-heading");
    var contextstable_body = document.createElement("tbody");
    contextstable.appendChild(contextstable_body);
    // Add the contexts
    if (task && task.contextAlbumReference && $.isArray(task.contextAlbumReference)) {
        for (var p = 0; p < task.contextAlbumReference.length; p++) {
            var contextEntry = task.contextAlbumReference[p];
            var contextName = contextEntry.name + ":" + contextEntry.version;
            var ce = {
                "name" : contextEntry.name,
                "version" : contextEntry.version,
                "displaytext" : contextName
            };
            editTaskForm_addTaskContext(contextstable_body, (createEditOrView == "VIEW"), contextName, ce,
                    contextAlbums);
        }
    }
    // add the Task Context button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var contextTR = document.createElement("tr");
        contextTR.setAttribute("class", "field-taskcontext-tr.new");
        contextstable_body.appendChild(contextTR);
        var contextTD = document.createElement("td");
        contextTD.setAttribute("colspan", "2");
        contextTR.appendChild(contextTD);
        var addContextInput = createAddFormButton();
        contextTD.appendChild(addContextInput);
        addContextInput.onclick = function() {
            editTaskForm_addTaskContext(contextstable_body, false, null, null, contextAlbums);
        };
    }

    // buttons
    var inputLI = document.createElement("li");
    formul.appendChild(inputLI);
    if (createEditOrView == "CREATE") {
        var generateUUIDInput = document.createElement("button");
        inputLI.appendChild(generateUUIDInput);
        generateUUIDInput.setAttribute("id", "generateUUID");
        generateUUIDInput.setAttribute("class", "ebBtn ebBtn_large");
        generateUUIDInput.setAttribute("type", "submit");
        generateUUIDInput.setAttribute("value", "Generate UUID");
        generateUUIDInput.onclick = editTaskForm_generateUUIDPressed;
        generateUUIDInput.innerHTML = generateUUIDInput.getAttribute("value");
        var inputSpan0 = document.createElement("span");
        inputLI.appendChild(inputSpan0);
        inputSpan0.setAttribute("class", "required");
        inputSpan0.innerHTML = " ";

        var generateDescriptionInput = document.createElement("button");
        inputLI.appendChild(generateDescriptionInput);
        generateDescriptionInput.setAttribute("id", "generateDescription");
        generateDescriptionInput.setAttribute("class", "ebBtn ebBtn_large");
        generateDescriptionInput.setAttribute("type", "submit");
        generateDescriptionInput.setAttribute("value", "Generate Description");
        generateDescriptionInput.onclick = editTaskForm_generateDescriptionPressed;
        generateDescriptionInput.innerHTML = generateDescriptionInput.getAttribute("value");
        var inputSpan1 = document.createElement("span");
        inputLI.appendChild(inputSpan1);
        inputSpan1.setAttribute("class", "required");
        inputSpan1.innerHTML = " ";
    }

    var cancelInput = document.createElement("button");
    inputLI.appendChild(cancelInput);
    cancelInput.setAttribute("id", "cancel");
    cancelInput.setAttribute("class", "ebBtn ebBtn_large");
    cancelInput.setAttribute("type", "submit");
    cancelInput.setAttribute("value", "Cancel");
    cancelInput.onclick = editTaskForm_cancelPressed;
    cancelInput.innerHTML = cancelInput.getAttribute("value");

    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var inputSpan2 = document.createElement("span");
        inputLI.appendChild(inputSpan2);
        inputSpan2.setAttribute("class", "required");
        inputSpan2.innerHTML = " ";
        var submitInput = document.createElement("button");
        inputLI.appendChild(submitInput);
        submitInput.setAttribute("id", "submit");
        submitInput.setAttribute("class", "ebBtn ebBtn_large");
        submitInput.setAttribute("type", "submit");
        submitInput.setAttribute("value", "Submit");
        submitInput.onclick = editTaskForm_submitPressed;
        submitInput.innerHTML = submitInput.getAttribute("value");
    }

    formParent.appendChild(contentelement);
    scrollToTop();
}

function editTaskForm_addTaskInputField(parentTBody, disabled, name, optional, contextSchema, contextSchemas) {
    var random_suffix = formUtils_generateUUID();

    var inputfieldTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    inputfieldTR.setAttribute("inputfield_id", random_suffix);
    inputfieldTR.setAttribute("class", "field-taskinputfield-tr");
    if (name == null && contextSchema == null && !disabled) {
        inputfieldTR.setAttribute("class", "field-taskinputfield-tr.new field-add-new");
        $(inputfieldTR).show("fast");
    }

    var deleteTD = document.createElement("td");
    inputfieldTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(inputfieldTR).hide("fast", function() {
                inputfieldTR.parentNode.removeChild(inputfieldTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    var nameTD = document.createElement("td");
    inputfieldTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editTaskFormInputFieldName" + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editTaskFormInputFieldName" + "_" + random_suffix);
    nameInput.setAttribute("class", "field-taskinputfield-name ebInput ebInput_width_xLong");
    if (name == null && contextSchema == null && !disabled) {
        nameInput.setAttribute("class", "field-taskinputfield-name.new ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "Input Field Name");
    if (name) {
        nameInput.value = name;
    }
    nameInput.readOnly = disabled;

    var contextSchemaTD = document.createElement("td");
    inputfieldTR.appendChild(contextSchemaTD);

    var selectDiv = dropdownList("editTaskFormInputFieldContextSchema" + "_" + random_suffix, contextSchemas,
            contextSchema, disabled, null)
    contextSchemaTD.appendChild(selectDiv);

    var inputOptionalTD = document.createElement("td");
    inputOptionalTD.setAttribute("class", "field-checkbox-center");
    inputfieldTR.appendChild(inputOptionalTD);
    var inputOptional = document.createElement("input");
    inputOptional.setAttribute("type", "checkbox");
    inputOptional.setAttribute("id", "editTaskFormInputFieldOptional" + "_" + random_suffix);
    inputOptional.setAttribute("name", "editTaskFormInputFieldOptional" + "_" + random_suffix);
    inputOptional.setAttribute("class", "field-eventparam-optional");
    if (name == null && contextSchema == null && !disabled) {
        inputOptional.setAttribute("class", "field-eventparam-optional.new");
    }
    if (optional == true) {
        inputOptional.checked = true;
    } else {
        inputOptional.checked = false;
    }
    inputOptional.disabled = disabled;
    inputOptionalTD.appendChild(inputOptional);
}

function editTaskForm_addTaskOutputField(parentTBody, disabled, name, optional, contextSchema, contextSchemas) {
    var random_suffix = formUtils_generateUUID();

    var outputfieldTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    outputfieldTR.setAttribute("outputfield_id", random_suffix);
    outputfieldTR.setAttribute("class", "field-taskoutputfield-tr");
    if (name == null && contextSchema == null && !disabled) {
        outputfieldTR.setAttribute("class", "field-taskoutputfield-tr.new field-add-new");
        $(outputfieldTR).show("fast");
    }

    var deleteTD = document.createElement("td");
    outputfieldTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(outputfieldTR).hide("fast", function() {
                outputfieldTR.parentNode.removeChild(outputfieldTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon ebIcon_disabled");
    }
    var nameTD = document.createElement("td");
    outputfieldTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editTaskFormOutputFieldName" + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editTaskFormOutputFieldName" + "_" + random_suffix);
    nameInput.setAttribute("class", "field-taskoutputfield-name ebInput ebInput_width_xLong");
    if (name == null && contextSchema == null && !disabled) {
        nameInput.setAttribute("class", "field-taskoutputfield-name.new ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "Output Field Name");
    if (name) {
        nameInput.value = name;
    }
    nameInput.readOnly = disabled;

    var contextSchemaTD = document.createElement("td");
    outputfieldTR.appendChild(contextSchemaTD);

    var selectDiv = dropdownList("editTaskFormOutputFieldContextSchema" + "_" + random_suffix, contextSchemas,
            contextSchema, disabled, null)
    contextSchemaTD.appendChild(selectDiv);

    var outputOptionalTD = document.createElement("td");
    outputOptionalTD.setAttribute("class", "field-checkbox-center");
    outputfieldTR.appendChild(outputOptionalTD);
    var outputOptional = document.createElement("input");
    outputOptional.setAttribute("type", "checkbox");
    outputOptional.setAttribute("id", "editTaskFormOutputFieldOptional" + "_" + random_suffix);
    outputOptional.setAttribute("name", "editTaskFormOutputFieldOptional" + "_" + random_suffix);
    outputOptional.setAttribute("class", "field-eventparam-optional");
    if (name == null && contextSchema == null && !disabled) {
        outputOptional.setAttribute("class", "field-eventparam-optional.new");
    }
    if (optional == true) {
        outputOptional.checked = true;
    } else {
        outputOptional.checked = false;
    }
    outputOptional.disabled = disabled;
    outputOptionalTD.appendChild(outputOptional);
}

function editTaskForm_addTaskParameter(parentTBody, disabled, name, value) {
    var random_suffix = formUtils_generateUUID();

    var paramTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    paramTR.setAttribute("param_id", random_suffix);
    paramTR.setAttribute("class", "field-taskparam-tr");
    if (name == null && value == null && !disabled) {
        paramTR.setAttribute("class", "field-taskparam-tr.new field-add-new");
        $(paramTR).show("fast");
    }

    var deleteTD = document.createElement("td");
    paramTR.appendChild(deleteTD);
    var deleteDiv = document.createElement("div");
    deleteTD.appendChild(deleteDiv);
    if (!disabled) {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(paramTR).hide("fast", function() {
                paramTR.parentNode.removeChild(paramTR);
            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    var nameTD = document.createElement("td");
    paramTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editTaskFormParamName" + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editTaskFormParamName" + "_" + random_suffix);
    nameInput.setAttribute("class", "field-taskparam-name ebInput ebInput_width_xLong");
    if (name == null && value == null && !disabled) {
        nameInput.setAttribute("class", "field-taskparam-name.new ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "Task Parameter Name");
    if (name) {
        nameInput.value = name;
    }
    nameInput.readOnly = disabled;

    var valueTD = document.createElement("td");
    paramTR.appendChild(valueTD);
    var paramInput = document.createElement("input");
    valueTD.appendChild(paramInput);
    paramInput.setAttribute("id", "editTaskFormParamValue" + "_" + random_suffix);
    paramInput.setAttribute("type", "text");
    paramInput.setAttribute("name", "editTaskFormParamValue" + "_" + random_suffix);
    paramInput.setAttribute("class", "field-taskparam-value  ebInput ebInput_width_xLong");
    if (name == null && value == null && !disabled) {
        paramInput.setAttribute("class", "field-taskparam-value.new ebInput ebInput_width_xLong");
    }
    paramInput.setAttribute("placeholder", "Task Parameter Value");
    if (value) {
        paramInput.value = value;
    }
    paramInput.readOnly = disabled;
}

function editTaskForm_addTaskContext(parentTBody, disabled, name, albumreference, contextAlbums) {
    var random_suffix = formUtils_generateUUID();

    var contextTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    contextTR.setAttribute("context_id", random_suffix);
    contextTR.setAttribute("class", "field-taskcontext-tr");
    if (name == null && albumreference == null && !disabled) {
        contextTR.setAttribute("class", "field-taskcontext-tr.new field-add-new");
        $(contextTR).show("fast");
    }

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
    var valueTD = document.createElement("td");
    contextTR.appendChild(valueTD);

    var selectDiv = dropdownList("editTaskFormContextValue" + "_" + random_suffix, contextAlbums, albumreference,
            disabled, null);
    valueTD.appendChild(selectDiv);
}

function editTaskForm_generateUUIDPressed() {
    document.getElementById("editTaskFormUuidInput").value = formUtils_generateUUID();
}

function editTaskForm_generateDescriptionPressed() {
    document.getElementById("editTaskFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("editTaskFormNameInput").value, document.getElementById("editTaskFormVersionInput").value,
            document.getElementById("editTaskFormUuidInput").value);
}

function editTaskForm_cancelPressed() {
    apexUtils_removeElement("editTaskFormDiv");
    taskTab_reset();
}

function editTaskForm_submitPressed() {
    var createEditOrView = document.getElementById("editTaskForm").getAttribute("createEditOrView");
    if (!createEditOrView || createEditOrView == "" || (createEditOrView != "CREATE" && createEditOrView != "EDIT")) {
        console.error("Invalid operation \"" + createEditOrView
                + "\" passed to editTaskForm_submitPressed function. Edit failed");
        apexUtils_removeElement("editTaskFormDiv");
        taskTab_reset();
        return;
    }

    var name = document.getElementById('editTaskFormNameInput').value;
    var version = document.getElementById('editTaskFormVersionInput').value;

    // get the task inputfields
    var taskbean_inputfields = null;
    var inputfieldstablerows = document.getElementById("editTaskFormInputFieldsTable").rows;
    if (inputfieldstablerows && inputfieldstablerows.length > 2) {
        taskbean_inputfields = new Object();
        for (var i = 1; i < inputfieldstablerows.length - 1; i++) {
            var inputfieldTR = inputfieldstablerows[i];
            if (inputfieldTR && inputfieldTR.getAttribute("inputfield_id")) {
                var inputfield_id = inputfieldTR.getAttribute("inputfield_id");
                var inputfieldname = document.getElementById("editTaskFormInputFieldName" + "_" + inputfield_id).value;
                var inputfield_optional = document.getElementById("editTaskFormInputFieldOptional" + "_"
                        + inputfield_id).checked;
                var inputfield_dt = document.getElementById("editTaskFormInputFieldContextSchema" + "_" + inputfield_id
                        + "_dropdownList").selectedOption;
                if (taskbean_inputfields[inputfieldname]) {
                    alert("Task \"" + name + "\" contains more than one Input Field called \"" + inputfieldname + "\"");
                    return false;
                }
                if (inputfield_dt == null) {
                    alert("Task \"" + name + "\" has no selected Context Item Schema for the Input Field called \""
                            + inputfieldname + "\"");
                    return false;
                }
                var inputfield_dt_name = inputfield_dt.name;
                var inputfield_dt_version = inputfield_dt.version;
                taskbean_inputfields[inputfieldname] = {
                    "localName" : inputfieldname,
                    "name" : inputfield_dt_name,
                    "version" : inputfield_dt_version,
                    "optional" : inputfield_optional
                };
            }
        }
    }
    // get the task outputfields
    var taskbean_outputfields = null;
    var outputfieldstablerows = document.getElementById("editTaskFormOutputFieldsTable").rows;
    if (outputfieldstablerows && outputfieldstablerows.length > 2) {
        taskbean_outputfields = new Object();
        for (var i = 1; i < outputfieldstablerows.length - 1; i++) {
            var outputfieldTR = outputfieldstablerows[i];
            if (outputfieldTR && outputfieldTR.getAttribute("outputfield_id")) {
                var outputfield_id = outputfieldTR.getAttribute("outputfield_id");
                var outputfieldname = document.getElementById("editTaskFormOutputFieldName" + "_" + outputfield_id).value;
                var outputfield_optional = document.getElementById("editTaskFormOutputFieldOptional" + "_"
                        + outputfield_id).checked;
                var outputfield_dt = document.getElementById("editTaskFormOutputFieldContextSchema" + "_"
                        + outputfield_id + "_dropdownList").selectedOption;
                if (taskbean_outputfields[outputfieldname]) {
                    alert("Task \"" + name + "\" contains more than one Output Field called \"" + outputfieldname
                            + "\"");
                    return false;
                }
                if (outputfield_dt == null) {
                    alert("Task \"" + name + "\" has no selected Context Item Schema for the Output Field called \""
                            + outputfieldname + "\"");
                    return false;
                }
                var outputfield_dt_name = outputfield_dt.name;
                var outputfield_dt_version = outputfield_dt.version;
                taskbean_outputfields[outputfieldname] = {
                    "localName" : outputfieldname,
                    "name" : outputfield_dt_name,
                    "version" : outputfield_dt_version,
                    "optional" : outputfield_optional
                };
            }
        }
    }
    // get the logic fields
    var logicfield = document.getElementById("editTaskFormTaskLogicTextArea_textarea").value;
    var logictype = document.getElementById("editTaskFormTaskLogicTypeInput").value;
    if (logictype == null || logictype == "") {
        alert("Task \"" + name + "\" has no Task Logic Type");
        return false;
    }
    var tasklogic = {
        "logic" : logicfield,
        "logicFlavour" : logictype
    };
    // get the task parameters
    var taskbean_parameters = null;
    var paramstablerows = document.getElementById("editTaskFormParamsTable").rows;
    if (paramstablerows && paramstablerows.length > 2) {
        taskbean_parameters = new Object();
        for (var i = 1; i < paramstablerows.length - 1; i++) {
            var paramTR = paramstablerows[i];
            if (paramTR && paramTR.getAttribute("param_id")) {
                var param_id = paramTR.getAttribute("param_id");
                var paramname = document.getElementById("editTaskFormParamName" + "_" + param_id).value;
                var paramvalue = document.getElementById("editTaskFormParamValue" + "_" + param_id).value;
                if (taskbean_parameters[paramname]) {
                    alert("Task \"" + name + "\" contains more than one Task Parameters called \"" + paramname + "\"");
                    return false;
                }
                taskbean_parameters[paramname] = {
                    "parameterName" : paramname,
                    "defaultValue" : paramvalue
                };
            }
        }
    }
    // get the context album references
    var taskbean_context = null;
    var contextstablerows = document.getElementById("editTaskFormContextsTable").rows;
    if (contextstablerows && contextstablerows.length > 2) {
        taskbean_context = new Array();
        for (var i = 1; i < contextstablerows.length - 1; i++) {
            var contextTR = contextstablerows[i];
            if (contextTR && contextTR.getAttribute("context_id")) {
                var context_id = contextTR.getAttribute("context_id");
                var contextalbumvalue = document.getElementById("editTaskFormContextValue" + "_" + context_id
                        + "_dropdownList").selectedOption;
                if (contextalbumvalue == null) {
                    alert("Task \"" + name + "\" has Context Album reference, but no Context Album is selected");
                    return false;
                }
                var contextalbumname = contextalbumvalue.displaytext;
                for (var j = 0; j < taskbean_context.length; j++) {
                    if (taskbean_context[j] != null && taskbean_context[j].name == contextalbumvalue.name
                            && taskbean_context[j].version == contextalbumvalue.version) {
                        alert("Task \"" + name + "\" references Context Album \"" + contextalbumname
                                + "\" more than once");
                        return false;
                    }
                }
                taskbean_context.push({
                    "name" : contextalbumvalue.name,
                    "version" : contextalbumvalue.version
                });
            }
        }
    }

    // generate an task bean to json-ify and send in rest request
    var taskbean = {
        "name" : name,
        "version" : version,
        "uuid" : document.getElementById('editTaskFormUuidInput').value,
        "description" : document.getElementById('editTaskFormDescriptionTextArea').value,
        "taskLogic" : tasklogic,
        "inputFields" : taskbean_inputfields,
        "outputFields" : taskbean_outputfields,
        "parameters" : taskbean_parameters,
        "contexts" : taskbean_context
    }
    var jsonString = JSON.stringify(taskbean);

    if (createEditOrView == "CREATE") {
        var requestURL = restRootURL + "/Task/Create";
        ajax_post(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editTaskFormDiv");
            taskTab_reset();
            keyInformationTab_reset()
        });
    } else if (createEditOrView == "EDIT") {
        var requestURL = restRootURL + "/Task/Update";
        ajax_put(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editTaskFormDiv");
            taskTab_reset();
            keyInformationTab_reset()
        });
    }

}
