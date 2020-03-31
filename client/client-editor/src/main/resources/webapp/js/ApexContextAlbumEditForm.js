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

function editContextAlbumForm_createContextAlbum(formParent) {
    // Get all contextSchemas too for album item schema
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
        editContextAlbumForm_activate(formParent, "CREATE", null, contextSchemas);
    });
}

function editContextAlbumForm_deleteContextAlbum(parent, name, version) {
    var message = "Are you sure you want to delete ContextAlbum \"" + name + ":" + version + "\"?";
    if (apexUtils_areYouSure(message)) {
        var requestURL = restRootURL + "/ContextAlbum/Delete?name=" + name + "&version=" + version;
        ajax_delete(requestURL, function(data) {
            apexUtils_removeElement("editContextAlbumFormDiv");
            contextAlbumTab_reset();
            keyInformationTab_reset()
        });
    }
}

function editContextAlbumForm_viewContextAlbum(parent, name, version) {
    var requestURL = restRootURL + "/ContextAlbum/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexContextAlbum", function(contextAlbum) {
        // Get all contextSchemas too for album item schema
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
            editContextAlbumForm_activate(parent, "VIEW", contextAlbum, contextSchemas);
        });
    });
}

function editContextAlbumForm_editContextAlbum(formParent, name, version) {
    var requestURL = restRootURL + "/ContextAlbum/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexContextAlbum", function(contextAlbum) {
        // Get all contextSchemas too for album item schema
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
            editContextAlbumForm_activate(formParent, "EDIT", contextAlbum, contextSchemas);
        });
    });
}

function editContextAlbumForm_activate(parent, operation, contextAlbum, contextSchemas) {
    apexUtils_removeElement("editContextAlbumFormDiv");
    var formParent = document.getElementById(parent);
    apexUtils_emptyElement(parent);

    var isedit = false;
    var createEditOrView = "";

    if (!operation) {
        console.warn("No operation specified for ContextAlbumForm form")
    } else {
        createEditOrView = operation.toUpperCase();
    }

    if (createEditOrView == "CREATE") {
        isedit = true;
    } else if (createEditOrView == "EDIT" || createEditOrView == "VIEW") {
        if (createEditOrView == "EDIT") {
            isedit = true;
        }

        if (!contextAlbum) {
            console.warn("Invalid value (\"" + contextAlbum
                    + "\") passed as a value for \"contextAlbum\" for ContextAlbumForm form.");
        } else {
            if (!contextAlbum.key || !contextAlbum.key.name || contextAlbum.key.name == "") {
                console.warn("Invalid value (\"" + contextAlbum.key.name
                        + "\") passed as a value for \"name\" for ContextAlbumForm form.");
            }
            if (!contextAlbum.key || !contextAlbum.key.version || contextAlbum.key.version == "") {
                console.warn("Invalid value (\"" + contextAlbum.key.version
                        + "\") passed as a value for \"version\" for ContextAlbumForm form.");
            }
            if (!contextAlbum.uuid || contextAlbum.uuid == "") {
                console.warn("Invalid value (\"" + contextAlbum.uuid
                        + "\") passed as a value for \"uuid\" for ContextAlbumForm form.");
            }
            if (createEditOrView == "VIEW") {
                if (!contextAlbum.description) {
                    console.warn("Invalid value (\"" + contextAlbum.description
                            + "\") passed as a value for \"description\" for ContextAlbumForm form.");
                }
                if (!contextAlbum.scope || contextAlbum.scope == "") {
                    console.warn("Invalid value (\"" + contextAlbum.scope
                            + "\") passed as a value for \"scope\" for ContextAlbumForm form.");
                }
                if (!contextAlbum.itemSchema || !contextAlbum.itemSchema.name || contextAlbum.itemSchema.name == ""
                        || !contextAlbum.itemSchema.version || contextAlbum.itemSchema.version == "") {
                    console.warn("Invalid value (\"" + contextAlbum.itemSchema
                            + "\") passed as a value for \"itemSchema\" for ContextAlbumForm form.");
                }
                if (!contextAlbum.isWritable || contextAlbum.isWritable == "") {
                    console.warn("Invalid value (\"" + contextAlbum.isWritable
                            + "\") passed as a value for \"isWritable\" for ContextAlbumForm form.");
                }
            }
        }
    } else {
        console
                .warn("Invalid operation (\""
                        + operation
                        + "\") specified for ContextAlbumForm form. Only \"Create\", \"Edit\" and \"View\" operations are supported");
    }

    var contentelement = document.createElement("editContextAlbumFormDiv");
    var formDiv = document.createElement("div");
    contentelement.appendChild(formDiv);
    formDiv.setAttribute("id", "editContextAlbumFormDiv");
    formDiv.setAttribute("class", "editContextAlbumFormDiv");

    var headingSpan = document.createElement("h2");
    formDiv.appendChild(headingSpan);
    headingSpan.innerHTML = "Context Item Album Editor";

    var form = document.createElement("editContextAlbumForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "editContextAlbumForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");
    form.setAttribute("createEditOrView", createEditOrView);

    var formul = document.createElement("ul");
    form.appendChild(formul);

    var nameLI = document.createElement("li");
    formul.appendChild(nameLI);
    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);
    nameLabel.setAttribute("for", "editContextAlbumFormNameInput");
    nameLabel.innerHTML = "Name: ";
    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);
    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";
    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);
    nameInput.setAttribute("id", "editContextAlbumFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editContextAlbumFormameInput");
    nameInput.setAttribute("class", "field ebInput");
    nameInput.setAttribute("placeholder", "name");
    if (contextAlbum && contextAlbum.key && contextAlbum.key.name) {
        nameInput.value = contextAlbum.key.name;
    }
    if (createEditOrView != "CREATE") {
        nameInput.readOnly = true;
    }

    var versionLI = document.createElement("li");
    formul.appendChild(versionLI);
    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);
    versionLabel.setAttribute("for", "editContextAlbumFormVersionInput");
    versionLabel.innerHTML = "Version: ";
    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);
    versionInput.setAttribute("id", "editContextAlbumFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "editContextAlbumFormVersionInput");
    versionInput.setAttribute("class", "field ebInput");
    versionInput.setAttribute("placeholder", "0.0.1");
    if (contextAlbum && contextAlbum.key && contextAlbum.key.version) {
        versionInput.value = contextAlbum.key.version;
    }
    if (createEditOrView != "CREATE") {
        versionInput.readOnly = true;
    }

    var uuidLI = document.createElement("li");
    formul.appendChild(uuidLI);
    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);
    uuidLabel.setAttribute("for", "editContextAlbumFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";
    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);
    uuidInput.setAttribute("id", "editContextAlbumFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "editContextAlbumFormUuidInput");
    uuidInput.setAttribute("class", "field-long  ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    if (contextAlbum && contextAlbum.uuid) {
        uuidInput.value = contextAlbum.uuid;
    }
    if (createEditOrView != "CREATE") {
        uuidInput.readOnly = true;
    }

    var disabled = (createEditOrView != "CREATE" && createEditOrView != "EDIT");

    var descriptionLI = document.createElement("li");
    formul.appendChild(descriptionLI);
    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "editContextAlbumFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";
    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);
    descriptionTextArea.setAttribute("id", "editContextAlbumFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "editContextAlbumFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long field-textarea  ebTextArea ebTextArea_width_full");
    if (contextAlbum && contextAlbum.description) {
        descriptionTextArea.value = contextAlbum.description;
    }
    descriptionTextArea.readOnly = disabled;

    var albumScopeLI = document.createElement("li");
    formul.appendChild(albumScopeLI);
    var albumScopeLabel = document.createElement("label");
    albumScopeLI.appendChild(albumScopeLabel);
    albumScopeLabel.setAttribute("for", "editContextAlbumFormAlbumScopeInput");
    albumScopeLabel.innerHTML = "Context Album Scope: ";
    var albumScopeInput = document.createElement("input");
    albumScopeLI.appendChild(albumScopeInput);
    albumScopeInput.setAttribute("id", "editContextAlbumFormAlbumScopeInput");
    albumScopeInput.setAttribute("type", "text");
    albumScopeInput.setAttribute("name", "editContextAlbumFormAlbumScopeInput");
    albumScopeInput.setAttribute("class", "field-albumScope ebInput");
    albumScopeInput.setAttribute("placeholder", "Global");
    if (contextAlbum && contextAlbum.scope) {
        albumScopeInput.value = contextAlbum.scope;
    }
    albumScopeInput.readOnly = disabled;

    var albumReadOnlyLI = document.createElement("li");
    formul.appendChild(albumReadOnlyLI);
    var albumReadOnlyLabel = document.createElement("label");
    albumReadOnlyLI.appendChild(albumReadOnlyLabel);
    albumReadOnlyLabel.setAttribute("for", "editContextAlbumFormAlbumReadOnlyInput");
    albumReadOnlyLabel.innerHTML = "Read Only? ";
    var albumReadOnlyInput = document.createElement("input");
    albumReadOnlyInput.setAttribute("type", "checkbox");
    albumReadOnlyLI.appendChild(albumReadOnlyInput);
    albumReadOnlyInput.setAttribute("id", "editContextAlbumFormAlbumReadOnlyCheckbox");
    albumReadOnlyInput.setAttribute("name", "editContextAlbumFormAlbumReadOnlyCheckbox");
    albumReadOnlyInput.setAttribute("class", "field-albumReadonlyCheckbox");
    if (contextAlbum && contextAlbum.isWritable != null && contextAlbum.isWritable === false) {
        albumReadOnlyInput.checked = true;
    } else {
        albumReadOnlyInput.checked = false;
    }
    albumReadOnlyInput.disabled = disabled;

    var albumItemSchemaLI = document.createElement("li");
    formul.appendChild(albumItemSchemaLI);
    var albumItemSchemaLabel = document.createElement("label");
    albumItemSchemaLI.appendChild(albumItemSchemaLabel);
    albumItemSchemaLabel.setAttribute("for", "editContextAlbumFormAlbumItemSchema");
    albumItemSchemaLabel.innerHTML = "Schema/Type of Album items: ";
    var selected = null;
    if (contextAlbum && contextAlbum.itemSchema != null && contextAlbum.itemSchema.name != null
            && contextAlbum.itemSchema.version != null && contextAlbum.itemSchema.name != ""
            && contextAlbum.itemSchema.version != "") {
        selected = {
            "name" : contextAlbum.itemSchema.name,
            "version" : contextAlbum.itemSchema.version,
            "displaytext" : contextAlbum.itemSchema.name + ":" + contextAlbum.itemSchema.version,
            "contextAlbum" : contextAlbum
        };
    }
    var selectDiv = dropdownList("editContextAlbumFormAlbumItemSchema", contextSchemas, selected, disabled, null);
    if (selectDiv == null) {
        console.error("Cannot create a Context Album because there are no contextSchemas to add to it");
        alert("Cannot create a Context Album because there are no contextSchemas to add to it");
        editContextAlbumForm_cancelPressed();
    } else {
        albumItemSchemaLI.appendChild(selectDiv);
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
        generateUUIDInput.onclick = editContextAlbumForm_generateUUIDPressed;
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
        generateDescriptionInput.onclick = editContextAlbumForm_generateDescriptionPressed;
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
    cancelInput.onclick = editContextAlbumForm_cancelPressed;
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
        submitInput.onclick = editContextAlbumForm_submitPressed;
        submitInput.innerHTML = submitInput.getAttribute("value");
    }

    formParent.appendChild(contentelement);
    scrollToTop();
}

function editContextAlbumForm_generateUUIDPressed() {
    document.getElementById("editContextAlbumFormUuidInput").value = formUtils_generateUUID();
}

function editContextAlbumForm_generateDescriptionPressed() {
    document.getElementById("editContextAlbumFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("editContextAlbumFormNameInput").value, document
            .getElementById("editContextAlbumFormVersionInput").value, document
            .getElementById("editContextAlbumFormUuidInput").value);
}

function editContextAlbumForm_cancelPressed() {
    apexUtils_removeElement("editContextAlbumFormDiv");
    contextAlbumTab_reset();
}

function editContextAlbumForm_submitPressed() {
    var createEditOrView = $('#editContextAlbumForm').attr("createEditOrView");
    if (!createEditOrView || createEditOrView == "" || (createEditOrView != "CREATE" && createEditOrView != "EDIT")) {
        console.error("Invalid operation \"" + createEditOrView
                + "\" passed to editContextAlbumForm_submitPressed function. Edit failed");
        apexUtils_removeElement("editContextAlbumFormDiv");
        contextAlbumTab_reset();
        return;
    }

    var name = $('#editContextAlbumFormNameInput').val();
    var version = $('#editContextAlbumFormVersionInput').val();

    var selectedschema = document.getElementById("editContextAlbumFormAlbumItemSchema_dropdownList").selectedOption;
    if (selectedschema == null) {
        alert("Context Album \"" + name + "\" has no selected Album Item Schema/Type");
        return false;
    }
    var itemschema = {
        "name" : selectedschema.name,
        "version" : selectedschema.version
    };

    var scope = $('#editContextAlbumFormAlbumScopeInput').val().toUpperCase();
    if (scope != "APPLICATION" && scope != "EXTERNAL" && scope != "GLOBAL") {
        var message = "Are you sure you want to set the scope ContextAlbum to \"" + scope
                + "\"? Currently the only scope supported are: \"APPLICATION\", \"EXTERNAL\" and \"GLOBAL\"";
        if (!apexUtils_areYouSure(message)) {
            return false;
        }
    }
    var readonly = $('#editContextAlbumFormAlbumReadOnlyCheckbox').prop('checked');
    if (readonly && scope != "EXTERNAL") {
        var message = "Are you sure you want to set the scope ContextAlbum to \""
                + scope
                + "\" while it is set as Read Only. It only makes sense to use readonly for \"EXTERNAL\" Context Albums. If this Album is readonly it cannot be set!";
        if (!apexUtils_areYouSure(message)) {
            return false;
        }
    }
    if (!readonly && scope == "EXTERNAL") {
        var message = "Are you sure you want to set the scope ContextAlbum to \""
                + scope
                + "\" while it is not set as Read Only. It only makes sense to use readonly for \"EXTERNAL\" Context Albums since EXTERNAL context cannot be set in policies.";
        if (!apexUtils_areYouSure(message)) {
            return false;
        }
    }

    var jsonString = JSON.stringify({
        "name" : name,
        "version" : version,
        "uuid" : $('#editContextAlbumFormUuidInput').val(),
        "description" : $('#editContextAlbumFormDescriptionTextArea').val(),
        "itemSchema" : itemschema,
        "scope" : scope,
        "writeable" : !readonly
    });

    if (createEditOrView == "CREATE") {
        var requestURL = restRootURL + "/ContextAlbum/Create";
        ajax_post(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editContextAlbumFormDiv");
            contextAlbumTab_reset();
            keyInformationTab_reset()
        });
    } else if (createEditOrView == "EDIT") {
        var requestURL = restRootURL + "/ContextAlbum/Update";
        ajax_put(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editContextAlbumFormDiv");
            contextAlbumTab_reset();
            keyInformationTab_reset()
        });
    }

}
