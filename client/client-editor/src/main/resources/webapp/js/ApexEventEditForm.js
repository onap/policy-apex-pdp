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

function editEventForm_deleteEvent(parent, name, version) {
    var message = "Are you sure you want to delete Event \"" + name + ":" + version + "\"?";
    if (apexUtils_areYouSure(message)) {
        var requestURL = restRootURL + "/Event/Delete?name=" + name + "&version=" + version;
        ajax_delete(requestURL, function(data) {
            apexUtils_removeElement("editEventFormDiv");
            eventTab_reset();
            keyInformationTab_reset()
        });
    }
}

function editEventForm_viewEvent(formParent, name, version) {
    editEventForm_editEvent_inner(formParent, name, version, "VIEW");
}

function editEventForm_editEvent(formParent, name, version) {
    editEventForm_editEvent_inner(formParent, name, version, "EDIT");
}

function editEventForm_createEvent(formParent) {
    // Get all contextSchemas too for event params
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
        editEventForm_activate(formParent, "CREATE", null, contextSchemas);
    });
}

function editEventForm_editEvent_inner(formParent, name, version, viewOrEdit) {
    var requestURL = restRootURL + "/Event/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexEvent", function(event) {
        // Get all contextSchemas too for event params
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
            editEventForm_activate(formParent, viewOrEdit, event, contextSchemas);
        });
    });
}

function editEventForm_activate(parent, operation, event, contextSchemas) {
    apexUtils_removeElement("editEventFormDiv");
    var formParent = document.getElementById(parent);
    apexUtils_emptyElement(parent);

    var isedit = false;
    var createEditOrView = "";
    if (!operation) {
        console.warn("No operation specified for EventForm form")
    } else {
        createEditOrView = operation.toUpperCase()
    }
    if (createEditOrView == "CREATE") {
        isedit = true;
    } else if (createEditOrView == "EDIT" || createEditOrView == "VIEW") {
        if (createEditOrView == "EDIT") {
            isedit = true;
        }

        if (!event) {
            console.warn("Invalid value (\"" + event + "\") passed as a value for \"event\" for EventForm form.");
        } else {
            if (!event.key || !event.key.name || event.key.name == "") {
                console.warn("Invalid value (\"" + event.key.name
                        + "\") passed as a value for \"name\" for EventForm form.");
            }
            if (!event.key || !event.key.version || event.key.version == "") {
                console.warn("Invalid value (\"" + event.key.version
                        + "\") passed as a value for \"version\" for EventForm form.");
            }
            if (!event.uuid || event.uuid == "") {
                console.warn("Invalid value (\"" + event.uuid
                        + "\") passed as a value for \"uuid\" for EventForm form.");
            }
        }
    } else {
        console.warn("Invalid operation (\"" + operation
                + "\") specified for EventForm form. Only \"Create\", \"Edit\" and \"View\" operations are supported");
    }

    var contentelement = document.createElement("editEventFormDiv");
    var formDiv = document.createElement("div");
    contentelement.appendChild(formDiv);
    formDiv.setAttribute("id", "editEventFormDiv");
    formDiv.setAttribute("class", "editEventFormDiv");

    var headingSpan = document.createElement("h2");
    formDiv.appendChild(headingSpan);

    headingSpan.innerHTML = "Event Editor";

    var form = document.createElement("editEventForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "editEventForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");
    form.setAttribute("createEditOrView", createEditOrView);

    var formul = document.createElement("ul");
    form.appendChild(formul);

    var nameLI = document.createElement("li");
    formul.appendChild(nameLI);
    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);
    nameLabel.setAttribute("for", "editEventFormNameInput");
    nameLabel.innerHTML = "Name: ";
    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);
    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";
    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);
    nameInput.setAttribute("id", "editEventFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editEventFormNameInput");
    nameInput.setAttribute("class", "field ebInput");
    nameInput.setAttribute("placeholder", "name");
    if (event && event.key && event.key.name) {
        nameInput.value = event.key.name;
    }
    if (createEditOrView != "CREATE") {
        nameInput.readOnly = true;
    }

    var versionLI = document.createElement("li");
    formul.appendChild(versionLI);
    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);
    versionLabel.setAttribute("for", "editEventFormVersionInput");
    versionLabel.innerHTML = "Version: ";
    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);
    versionInput.setAttribute("id", "editEventFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "editEventFormVersionInput");
    versionInput.setAttribute("class", "field ebInput");
    versionInput.setAttribute("placeholder", "0.0.1");
    if (event && event.key && event.key.version) {
        versionInput.value = event.key.version;
    }
    if (createEditOrView != "CREATE") {
        versionInput.readOnly = true;
    }

    var uuidLI = document.createElement("li");
    formul.appendChild(uuidLI);
    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);
    uuidLabel.setAttribute("for", "editEventFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";
    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);
    uuidInput.setAttribute("id", "editEventFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "editEventFormUuidInput");
    uuidInput.setAttribute("class", "field-long ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    if (event && event.uuid) {
        uuidInput.value = event.uuid;
    }
    if (createEditOrView != "CREATE") {
        uuidInput.readOnly = true;
    }

    var descriptionLI = document.createElement("li");
    formul.appendChild(descriptionLI);
    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "editEventFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";
    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);
    descriptionTextArea.setAttribute("id", "editEventFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "editEventFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full");
    if (event && event.description) {
        descriptionTextArea.value = event.description;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        descriptionTextArea.readOnly = true;
    }

    var namespaceLI = document.createElement("li");
    formul.appendChild(namespaceLI);
    var namespaceLabel = document.createElement("label");
    namespaceLI.appendChild(namespaceLabel);
    namespaceLabel.setAttribute("for", "editEventFormNamespaceInput");
    namespaceLabel.innerHTML = "Namespace: ";
    var namespaceInput = document.createElement("input");
    namespaceLI.appendChild(namespaceInput);
    namespaceInput.setAttribute("id", "editEventFormNamespaceInput");
    namespaceInput.setAttribute("type", "text");
    namespaceInput.setAttribute("name", "editEventFormNamespaceInput");
    namespaceInput.setAttribute("class", "field-namespace ebInput  ebInput ebInput_width_full");
    namespaceInput.setAttribute("placeholder", "");
    if (event && event.nameSpace) {
        namespaceInput.value = event.nameSpace;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        namespaceInput.readOnly = true;
    }

    var sourceLI = document.createElement("li");
    formul.appendChild(sourceLI);
    var sourceLabel = document.createElement("label");
    sourceLI.appendChild(sourceLabel);
    sourceLabel.setAttribute("for", "editEventFormSourceInput");
    sourceLabel.innerHTML = "Source: ";
    var sourceInput = document.createElement("input");
    sourceLI.appendChild(sourceInput);
    sourceInput.setAttribute("id", "editEventFormSourceInput");
    sourceInput.setAttribute("type", "text");
    sourceInput.setAttribute("name", "editEventFormSourceInput");
    sourceInput.setAttribute("class", "field-source ebInput");
    sourceInput.setAttribute("placeholder", "");
    if (event && event.source) {
        sourceInput.value = event.source;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        sourceInput.readOnly = true;
    }

    var targetLI = document.createElement("li");
    formul.appendChild(targetLI);
    var targetLabel = document.createElement("label");
    targetLI.appendChild(targetLabel);
    targetLabel.setAttribute("for", "editEventFormTargetInput");
    targetLabel.innerHTML = "Target: ";
    var targetInput = document.createElement("input");
    targetLI.appendChild(targetInput);
    targetInput.setAttribute("id", "editEventFormTargetInput");
    targetInput.setAttribute("type", "text");
    targetInput.setAttribute("name", "editEventFormTargetInput");
    targetInput.setAttribute("class", "field-target ebInput");
    targetInput.setAttribute("placeholder", "");
    if (event && event.target) {
        targetInput.value = event.target;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        targetInput.readOnly = true;
    }

    var parametersLI = document.createElement("li");
    formul.appendChild(parametersLI);
    var parametersLabel = document.createElement("label");
    parametersLI.appendChild(parametersLabel);
    parametersLabel.setAttribute("for", "editEventFormParametersTable");
    parametersLabel.innerHTML = "Event Parameters: ";
    var paramstable = document.createElement("table");
    paramstable.setAttribute("id", "editEventFormParametersTable");
    paramstable.setAttribute("name", "editEventFormParametersTable");
    paramstable.setAttribute("class", "table-eventparam");
    parametersLI.appendChild(paramstable);
    var paramstable_head = document.createElement("thead");
    paramstable.appendChild(paramstable_head);
    var paramstable_head_tr = document.createElement("tr");
    paramstable_head.appendChild(paramstable_head_tr);
    paramstable_head_tr.appendChild(document.createElement("th")); // empty,
                                                                    // for
                                                                    // delete
                                                                    // button
    paramstable_head_th = document.createElement("th");
    paramstable_head_tr.appendChild(paramstable_head_th);
    paramstable_head_th.innerHTML = "Parameter Name: ";
    paramstable_head_th.setAttribute("class", "table-eventparam-heading form-heading");
    paramstable_head_th = document.createElement("th");
    paramstable_head_tr.appendChild(paramstable_head_th);
    paramstable_head_th.innerHTML = "Parameter Type/Schema: ";
    paramstable_head_th.setAttribute("class", "table-eventparam-heading form-heading");
    paramstable_head_th = document.createElement("th");
    paramstable_head_tr.appendChild(paramstable_head_th);
    paramstable_head_th.innerHTML = "Optional: ";
    paramstable_head_th.setAttribute("class", "table-eventparam-heading form-heading");
    var paramstable_body = document.createElement("tbody");
    paramstable.appendChild(paramstable_body);
    // Add the parameters
    if (event && event.parameter && event.parameter.entry) {
        for (var p = 0; p < event.parameter.entry.length; p++) {
            var fieldEntry = event.parameter.entry[p];
            var contextSchema = fieldEntry.value.fieldSchemaKey;
            var optional = fieldEntry.value.optional;
            contextSchema["displaytext"] = contextSchema.name + ":" + contextSchema.version;
            editEventForm_addEventParam(paramstable_body, (createEditOrView == "VIEW"), fieldEntry.key, optional,
                    contextSchema, contextSchemas);
        }
    }
    // add the New Parameter button
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var paramTR = document.createElement("tr");
        paramTR.setAttribute("class", "field-eventparam-tr.new");
        paramstable_body.appendChild(paramTR);
        var paramTD = document.createElement("td");
        paramTD.setAttribute("colspan", "4");
        paramTR.appendChild(paramTD);
        var addParamInput = createAddFormButton();
        paramTD.appendChild(addParamInput);
        addParamInput.onclick = function() {
            editEventForm_addEventParam(paramstable_body, false, null, false, null, contextSchemas);
        };
    }

    var inputLI = document.createElement("li");
    formul.appendChild(inputLI);
    if (createEditOrView == "CREATE") {
        var generateUUIDInput = document.createElement("button");
        inputLI.appendChild(generateUUIDInput);
        generateUUIDInput.setAttribute("id", "generateUUID");
        generateUUIDInput.setAttribute("class", "ebBtn ebBtn_large");
        generateUUIDInput.setAttribute("type", "submit");
        generateUUIDInput.setAttribute("value", "Generate UUID");
        generateUUIDInput.onclick = editEventForm_generateUUIDPressed;
        generateUUIDInput.innerHTML = generateUUIDInput.getAttribute("value");

        var inputSpan0 = document.createElement("span");
        inputLI.appendChild(inputSpan0);
        inputSpan0.setAttribute("class", "required");
        inputSpan0.innerHTML = " ";
    }
    if (createEditOrView == "CREATE") {
        var generateDescriptionInput = document.createElement("button");
        inputLI.appendChild(generateDescriptionInput);
        generateDescriptionInput.setAttribute("id", "generateDescription");
        generateDescriptionInput.setAttribute("class", "ebBtn ebBtn_large");
        generateDescriptionInput.setAttribute("type", "submit");
        generateDescriptionInput.setAttribute("value", "Generate Description");
        generateDescriptionInput.onclick = editEventForm_generateDescriptionPressed;
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
    cancelInput.onclick = editEventForm_cancelPressed;
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

        submitInput.onclick = editEventForm_submitPressed;
        submitInput.innerHTML = submitInput.getAttribute("value")
    }

    formParent.appendChild(contentelement);
    scrollToTop();
}

function editEventForm_addEventParam(parentTBody, disabled, name, optional, contextSchema, contextSchemas) {
    var random_suffix = formUtils_generateUUID();

    var paramTR = parentTBody.insertRow(parentTBody.rows.length - 1);
    paramTR.setAttribute("param_id", random_suffix);
    paramTR.setAttribute("class", "field-eventparam-tr");
    if (name == null && contextSchema == null && !disabled) {
        paramTR.setAttribute("class", "field-eventparam-tr.new field-add-new");
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
        };
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }

    var nameTD = document.createElement("td");
    paramTR.appendChild(nameTD);
    var nameInput = document.createElement("input");
    nameTD.appendChild(nameInput);
    nameInput.setAttribute("id", "editEventFormParamName" + "_" + random_suffix);
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editEventFormParamName" + "_" + random_suffix);
    nameInput.setAttribute("class", "field-eventparam-name  ebInput ebInput_width_xLong");
    if (name == null && contextSchema == null && !disabled) {
        nameInput.setAttribute("class", "field-eventparam-name.new  ebInput ebInput_width_xLong");
    }
    nameInput.setAttribute("placeholder", "Parameter Name");
    if (name) {
        nameInput.value = name;
    }
    nameInput.readOnly = disabled;

    var contextSchemaTD = document.createElement("td");
    paramTR.appendChild(contextSchemaTD);

    var selectDiv = dropdownList("editEventFormParamContextSchema" + "_" + random_suffix, contextSchemas,
            contextSchema, disabled, null)
    contextSchemaTD.appendChild(selectDiv);

    var paramOptionalTD = document.createElement("td");
    paramOptionalTD.setAttribute("class", "field-checkbox-center");
    paramTR.appendChild(paramOptionalTD);
    var paramOptionalInput = document.createElement("input");
    paramOptionalInput.setAttribute("type", "checkbox");
    paramOptionalInput.setAttribute("id", "editEventFormParamOptional" + "_" + random_suffix);
    paramOptionalInput.setAttribute("name", "editEventFormParamOptional" + "_" + random_suffix);
    paramOptionalInput.setAttribute("class", "field-eventparam-optional");
    if (name == null && contextSchema == null && !disabled) {
        paramOptionalInput.setAttribute("class", "field-eventparam-optional.new");
    }
    if (optional == true) {
        paramOptionalInput.checked = true;
    } else {
        paramOptionalInput.checked = false;
    }
    paramOptionalInput.disabled = disabled;
    paramOptionalTD.appendChild(paramOptionalInput);
}

function editEventForm_generateUUIDPressed() {
    document.getElementById("editEventFormUuidInput").value = formUtils_generateUUID();
}

function editEventForm_generateDescriptionPressed() {
    document.getElementById("editEventFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("editEventFormNameInput").value,
            document.getElementById("editEventFormVersionInput").value, document
                    .getElementById("editEventFormUuidInput").value);
}

function editEventForm_cancelPressed() {
    apexUtils_removeElement("editEventFormDiv");
    eventTab_reset();
}

function editEventForm_submitPressed() {
    var createEditOrView = document.getElementById("editEventForm").getAttribute("createEditOrView");
    if (!createEditOrView || createEditOrView == "" || (createEditOrView != "CREATE" && createEditOrView != "EDIT")) {
        console.error("Invalid operation \"" + createEditOrView
                + "\" passed to editEventForm_submitPressed function. Edit failed");
        apexUtils_removeElement("editEventFormDiv");
        eventTab_reset();
        return;
    }

    var name = document.getElementById('editEventFormNameInput').value;
    var version = document.getElementById('editEventFormVersionInput').value;

    var eventbean_params = null;
    // get the event parameters
    var paramstablerows = document.getElementById("editEventFormParametersTable").rows;
    if (paramstablerows && paramstablerows.length >= 2) {
        eventbean_params = new Object();
        for (var i = 1; i < paramstablerows.length - 1; i++) {
            var paramTR = paramstablerows[i];
            if (paramTR && paramTR.getAttribute("param_id")) {
                var param_id = paramTR.getAttribute("param_id");
                var paramname = document.getElementById("editEventFormParamName" + "_" + param_id).value;
                var paramoptional = document.getElementById("editEventFormParamOptional" + "_" + param_id).checked;
                var param_dt = document.getElementById("editEventFormParamContextSchema" + "_" + param_id
                        + "_dropdownList").selectedOption;
                if (eventbean_params[paramname]) {
                    alert("Event \"" + name + "\" contains more than one Parameter called \"" + paramname + "\"");
                    return false;
                }
                if (param_dt == null) {
                    alert("Event \"" + name + "\" has no selected Context Item Schema for the Parameter called \""
                            + paramname + "\"");
                    return false;
                }
                var param_dt_name = param_dt.name;
                var param_dt_version = param_dt.version;
                eventbean_params[paramname] = {
                    "localName" : paramname,
                    "name" : param_dt_name,
                    "version" : param_dt_version,
                    "optional" : paramoptional
                };
            }
        }
    }
    // generate an event bean to json-ify and send in rest request
    var eventbean = {
        "name" : name,
        "version" : version,
        "uuid" : document.getElementById('editEventFormUuidInput').value,
        "description" : document.getElementById('editEventFormDescriptionTextArea').value,
        "source" : document.getElementById('editEventFormSourceInput').value,
        "target" : document.getElementById('editEventFormTargetInput').value,
        "nameSpace" : document.getElementById('editEventFormNamespaceInput').value,
        "parameters" : eventbean_params
    }
    var jsonString = JSON.stringify(eventbean);

    if (createEditOrView == "CREATE") {
        var requestURL = restRootURL + "/Event/Create";
        ajax_post(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editEventFormDiv");
            eventTab_reset();
            keyInformationTab_reset()
        });
    } else if (createEditOrView == "EDIT") {
        var requestURL = restRootURL + "/Event/Update";
        ajax_put(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editEventFormDiv");
            eventTab_reset();
            keyInformationTab_reset()
        });
    }

}
