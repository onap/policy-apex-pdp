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

function editContextSchemaForm_createContextSchema(formParent) {
    return editContextSchemaForm_activate(formParent, "CREATE", null);
}

function editContextSchemaForm_deleteContextSchema(parent, name, version) {
    var message = "Are you sure you want to delete ContextSchema \"" + name + ":" + version + "\"?";
    if (apexUtils_areYouSure(message)) {
        var requestURL = restRootURL + "/ContextSchema/Delete?name=" + name + "&version=" + version;
        ajax_delete(requestURL, function(data) {
            apexUtils_removeElement("editContextSchemaFormDiv");
            contextSchemaTab_reset();
            keyInformationTab_reset()
        });
    }
}

function editContextSchemaForm_viewContextSchema(parent, name, version) {
    var requestURL = restRootURL + "/ContextSchema/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexContextSchema", function(contextSchema) {
        editContextSchemaForm_activate(parent, "VIEW", contextSchema);
    });
}

function editContextSchemaForm_editContextSchema(formParent, name, version) {
    var requestURL = restRootURL + "/ContextSchema/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexContextSchema", function(contextSchema) {
        editContextSchemaForm_activate(formParent, "EDIT", contextSchema);
    });
}

function editContextSchemaForm_activate(parent, operation, contextSchema) {
    apexUtils_removeElement("editContextSchemaFormDiv");
    var formParent = document.getElementById(parent);
    apexUtils_emptyElement(parent);

    var isedit = false;
    var createEditOrView = "";

    if (!operation) {
        console.warn("No operation specified for ContextSchemaForm form")
    } else {
        createEditOrView = operation.toUpperCase();
    }

    if (createEditOrView == "CREATE") {
        isedit = true;
    } else if (createEditOrView == "EDIT" || createEditOrView == "VIEW") {
        if (createEditOrView == "EDIT") {
            isedit = true;
        }

        if (!contextSchema) {
            console.warn("Invalid value (\"" + contextSchema
                    + "\") passed as a value for \"contextSchema\" for ContextSchemaForm form.");
        } else {
            if (!contextSchema.key || !contextSchema.key.name || contextSchema.key.name == "") {
                console.warn("Invalid value (\"" + contextSchema.key.name
                        + "\") passed as a value for \"name\" for ContextSchemaForm form.");
            }
            if (!contextSchema.key || !contextSchema.key.version || contextSchema.key.version == "") {
                console.warn("Invalid value (\"" + contextSchema.key.version
                        + "\") passed as a value for \"version\" for ContextSchemaForm form.");
            }
            if (!contextSchema.uuid || contextSchema.uuid == "") {
                console.warn("Invalid value (\"" + contextSchema.uuid
                        + "\") passed as a value for \"uuid\" for ContextSchemaForm form.");
            }
            if (createEditOrView == "VIEW") {
                if (!contextSchema.description) {
                    console.warn("Invalid value (\"" + contextSchema.description
                            + "\") passed as a value for \"description\" for ContextSchemaForm form.");
                }
                if (!contextSchema.schemaFlavour || contextSchema.schemaFlavour == "") {
                    console.warn("Invalid value (\"" + contextSchema.schemaFlavour
                            + "\") passed as a value for \"schemaFlavour\" for ContextSchemaForm form.");
                }
                if (!contextSchema.schemaDefinition || contextSchema.schemaDefinition == "") {
                    console.warn("Invalid value (\"" + contextSchema.schemaDefinition
                            + "\") passed as a value for \"schemaDefinition\" for ContextSchemaForm form.");
                }
            }
        }
    } else {
        console
                .warn("Invalid operation (\""
                        + operation
                        + "\") specified for ContextSchemaForm form. Only \"Create\", \"Edit\" and \"View\" operations are supported");
    }

    var contentelement = document.createElement("editContextSchemaFormDiv");
    var formDiv = document.createElement("div");
    contentelement.appendChild(formDiv);
    formDiv.setAttribute("id", "editContextSchemaFormDiv");
    formDiv.setAttribute("class", "editContextSchemaFormDiv");

    var headingSpan = document.createElement("h2");
    headingSpan.innerHTML = "Context Item Schema Editor";
    formDiv.appendChild(headingSpan);

    var form = document.createElement("editContextSchemaForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "editContextSchemaForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");
    form.setAttribute("createEditOrView", createEditOrView);

    var formul = document.createElement("ul");
    form.appendChild(formul);

    var nameLI = document.createElement("li");
    formul.appendChild(nameLI);
    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);
    nameLabel.setAttribute("for", "editContextSchemaFormNameInput");
    nameLabel.innerHTML = "Name: ";
    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);
    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";
    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);
    nameInput.setAttribute("id", "editContextSchemaFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editContextSchemaFormameInput");
    nameInput.setAttribute("class", "field ebInput");
    nameInput.setAttribute("placeholder", "name");
    if (contextSchema && contextSchema.key && contextSchema.key.name) {
        nameInput.value = contextSchema.key.name;
    }
    if (createEditOrView != "CREATE") {
        nameInput.readOnly = true;
    }

    var versionLI = document.createElement("li");
    formul.appendChild(versionLI);
    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);
    versionLabel.setAttribute("for", "editContextSchemaFormVersionInput");
    versionLabel.innerHTML = "Version: ";
    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);
    versionInput.setAttribute("id", "editContextSchemaFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "editContextSchemaFormVersionInput");
    versionInput.setAttribute("class", "field ebInput");
    versionInput.setAttribute("placeholder", "0.0.1");
    if (contextSchema && contextSchema.key && contextSchema.key.version) {
        versionInput.value = contextSchema.key.version;
    }
    if (createEditOrView != "CREATE") {
        versionInput.readOnly = true;
    }

    var uuidLI = document.createElement("li");
    formul.appendChild(uuidLI);
    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);
    uuidLabel.setAttribute("for", "editContextSchemaFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";
    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);
    uuidInput.setAttribute("id", "editContextSchemaFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "editContextSchemaFormUuidInput");
    uuidInput.setAttribute("class", "field-long ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    if (contextSchema && contextSchema.uuid) {
        uuidInput.value = contextSchema.uuid;
    }
    if (createEditOrView != "CREATE") {
        uuidInput.readOnly = true;
    }

    var descriptionLI = document.createElement("li");
    formul.appendChild(descriptionLI);
    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "editContextSchemaFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";
    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);
    descriptionTextArea.setAttribute("id", "editContextSchemaFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "editContextSchemaFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full");
    if (contextSchema && contextSchema.description) {
        descriptionTextArea.value = contextSchema.description;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        descriptionTextArea.readOnly = true;
    }

    var schemaFlavourLI = document.createElement("li");
    formul.appendChild(schemaFlavourLI);
    var schemaFlavourLabel = document.createElement("label");
    schemaFlavourLI.appendChild(schemaFlavourLabel);
    schemaFlavourLabel.setAttribute("for", "editContextSchemaFormSchemaFlavourInput");
    schemaFlavourLabel.innerHTML = "Schema Flavour: ";
    var schemaFlavourInput = document.createElement("input");
    schemaFlavourLI.appendChild(schemaFlavourInput);
    schemaFlavourInput.setAttribute("id", "editContextSchemaFormSchemaFlavourInput");
    schemaFlavourInput.setAttribute("type", "text");
    schemaFlavourInput.setAttribute("name", "editContextSchemaFormSchemaFlavourInput");
    schemaFlavourInput.setAttribute("class", "field-long ebInput ebInput_width_full");
    schemaFlavourInput.setAttribute("placeholder", "Java");
    if (contextSchema && contextSchema.schemaFlavour) {
        schemaFlavourInput.value = contextSchema.schemaFlavour;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        schemaFlavourInput.readOnly = true;
    }

    var schemaDefinitionLI = document.createElement("li");
    formul.appendChild(schemaDefinitionLI);
    var schemaDefinitionLabel = document.createElement("label");
    schemaDefinitionLI.appendChild(schemaDefinitionLabel);
    schemaDefinitionLabel.setAttribute("for", "editContextSchemaFormSchemaDefinitionInput");
    schemaDefinitionLabel.innerHTML = "Schema Definition: ";
    var schemaDefinitionInput = document.createElement("textarea");
    schemaDefinitionLI.appendChild(schemaDefinitionInput);
    schemaDefinitionInput.setAttribute("id", "editContextSchemaFormSchemaDefinitionInput");
    schemaDefinitionInput.setAttribute("type", "text");
    schemaDefinitionInput.setAttribute("name", "editContextSchemaFormSchemaDefinitionInput");
    schemaDefinitionInput.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full");
    schemaDefinitionInput.setAttribute("placeholder", "java.lang.String");
    if (contextSchema && contextSchema.schemaDefinition) {
        schemaDefinitionInput.value = contextSchema.schemaDefinition;
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        schemaDefinitionInput.readOnly = true;
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
        generateUUIDInput.onclick = editContextSchemaForm_generateUUIDPressed;
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
        generateDescriptionInput.onclick = editContextSchemaForm_generateDescriptionPressed;
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
    cancelInput.onclick = editContextSchemaForm_cancelPressed;
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
        submitInput.onclick = editContextSchemaForm_submitPressed;
        submitInput.innerHTML = submitInput.getAttribute("value");
    }

    formParent.appendChild(contentelement);
    scrollToTop();
}

function editContextSchemaForm_generateUUIDPressed() {
    document.getElementById("editContextSchemaFormUuidInput").value = formUtils_generateUUID();
}

function editContextSchemaForm_generateDescriptionPressed() {
    document.getElementById("editContextSchemaFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("editContextSchemaFormNameInput").value, document
            .getElementById("editContextSchemaFormVersionInput").value, document
            .getElementById("editContextSchemaFormUuidInput").value);
}

function editContextSchemaForm_cancelPressed() {
    apexUtils_removeElement("editContextSchemaFormDiv");
    contextSchemaTab_reset();
}

function editContextSchemaForm_submitPressed() {
    var createEditOrView = $('#editContextSchemaForm').attr("createEditOrView");
    if (!createEditOrView || createEditOrView == "" || (createEditOrView != "CREATE" && createEditOrView != "EDIT")) {
        console.error("Invalid operation \"" + createEditOrView
                + "\" passed to editContextSchemaForm_submitPressed function. Edit failed");
        apexUtils_removeElement("editContextSchemaFormDiv");
        contextSchemaTab_reset();
        return;
    }

    var name = $('#editContextSchemaFormNameInput').val();
    var version = $('#editContextSchemaFormVersionInput').val()

    var jsonString = JSON.stringify({
        "name" : name,
        "version" : version,
        "uuid" : $('#editContextSchemaFormUuidInput').val(),
        "description" : $('#editContextSchemaFormDescriptionTextArea').val(),
        "schemaFlavour" : $('#editContextSchemaFormSchemaFlavourInput').val(),
        "schemaDefinition" : $('#editContextSchemaFormSchemaDefinitionInput').val(),
    });

    if (createEditOrView == "CREATE") {
        var requestURL = restRootURL + "/ContextSchema/Create";
        ajax_post(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editContextSchemaFormDiv");
            contextSchemaTab_reset();
            keyInformationTab_reset()
        });
    } else if (createEditOrView == "EDIT") {
        var requestURL = restRootURL + "/ContextSchema/Update";
        ajax_put(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editContextSchemaFormDiv");
            contextSchemaTab_reset();
            keyInformationTab_reset()
        });
    }

}
