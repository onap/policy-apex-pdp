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

function newModelForm_activate(formParent) {
    apexUtils_removeElement("newModelFormDiv");

    var contentelement = document.createElement("newModelFormDiv");
    var formDiv = document.createElement("div");
    var backgroundDiv = document.createElement("div");
    backgroundDiv.setAttribute("id", "newModelDivBackground");
    backgroundDiv.setAttribute("class", "newModelDivBackground");

    backgroundDiv.appendChild(formDiv);
    contentelement.appendChild(backgroundDiv);
    formParent.appendChild(contentelement);

    formDiv.setAttribute("id", "newModelFormDiv");
    formDiv.setAttribute("class", "newModelFormDiv");

    var headingSpan = document.createElement("span");
    formDiv.appendChild(headingSpan);

    headingSpan.setAttribute("class", "headingSpan");
    headingSpan.innerHTML = "Model Details";

    var form = document.createElement("newModelForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "newModelForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");

    var ul = document.createElement("ul");
    form.appendChild(ul);

    var nameLI = document.createElement("li");
    form.appendChild(nameLI);

    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);

    nameLabel.setAttribute("for", "newModelFormNameInput");
    nameLabel.innerHTML = "Name: ";

    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);

    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";

    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);

    nameInput.setAttribute("id", "newModelFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "newModelFormameInput");
    nameInput.setAttribute("class", "field ebInput ebInput_width_xLong");
    nameInput.setAttribute("placeholder", "name");

    var versionLI = document.createElement("li");
    form.appendChild(versionLI);

    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);

    versionLabel.setAttribute("for", "newModelFormVersionInput");
    versionLabel.innerHTML = "Version: ";

    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);

    versionInput.setAttribute("id", "newModelFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "newModelFormVersionInput");
    versionInput.setAttribute("class", "field ebInput ebInput_width_xLong");
    versionInput.setAttribute("placeholder", "0.0.1");

    var uuidLI = document.createElement("li");
    form.appendChild(uuidLI);

    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);

    uuidLabel.setAttribute("for", "newModelFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";

    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);

    uuidInput.setAttribute("id", "newModelFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "newModelFormUuidInput");
    uuidInput.setAttribute("class", "field-long  ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");

    var descriptionLI = document.createElement("li");
    form.appendChild(descriptionLI);

    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "newModelFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";

    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);

    descriptionTextArea.setAttribute("id", "newModelFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "newModelFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full");

    var inputLI = document.createElement("li");
    form.appendChild(inputLI);

    var generateUUIDInput = document.createElement("input");
    inputLI.appendChild(generateUUIDInput);

    generateUUIDInput.setAttribute("id", "generateUUID");
    generateUUIDInput.setAttribute("class", "button ebBtn");
    generateUUIDInput.setAttribute("type", "submit");
    generateUUIDInput.setAttribute("value", "Generate UUID");

    generateUUIDInput.onclick = newModelForm_generateUUIDPressed;

    var inputSpan0 = document.createElement("span");
    inputLI.appendChild(inputSpan0);

    inputSpan0.setAttribute("class", "required");
    inputSpan0.innerHTML = " ";

    var generateDescriptionInput = document.createElement("input");
    inputLI.appendChild(generateDescriptionInput);

    generateDescriptionInput.setAttribute("id", "generateDescription");
    generateDescriptionInput.setAttribute("class", "button ebBtn");
    generateDescriptionInput.setAttribute("type", "submit");
    generateDescriptionInput.setAttribute("value", "Generate Description");

    generateDescriptionInput.onclick = newModelForm_generateDescriptionPressed;

    var inputSpan1 = document.createElement("span");
    inputLI.appendChild(inputSpan1);

    inputSpan1.setAttribute("class", "required");
    inputSpan1.innerHTML = " ";

    var cancelInput = document.createElement("input");
    inputLI.appendChild(cancelInput);

    cancelInput.setAttribute("id", "generateDescription");
    cancelInput.setAttribute("class", "button ebBtn");
    cancelInput.setAttribute("type", "submit");
    cancelInput.setAttribute("value", "Cancel");

    cancelInput.onclick = newModelForm_cancelPressed;

    var inputSpan2 = document.createElement("span");
    inputLI.appendChild(inputSpan2);

    inputSpan2.setAttribute("class", "required");
    inputSpan2.innerHTML = " ";

    var submitInput = document.createElement("input");
    inputLI.appendChild(submitInput);

    submitInput.setAttribute("id", "submit");
    submitInput.setAttribute("class", "button ebBtn");
    submitInput.setAttribute("type", "submit");
    submitInput.setAttribute("value", "Submit");

    submitInput.onclick = newModelForm_submitPressed;
}

function newModelForm_generateUUIDPressed() {
    document.getElementById("newModelFormUuidInput").value = formUtils_generateUUID();
}

function newModelForm_generateDescriptionPressed() {
    document.getElementById("newModelFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("newModelFormNameInput").value, document.getElementById("newModelFormVersionInput").value,
            document.getElementById("newModelFormUuidInput").value);
}

function newModelForm_cancelPressed() {
    apexUtils_removeElement("newModelDivBackground");
}

function newModelForm_submitPressed() {
    jsonString = JSON.stringify({
        "name" : $('#newModelFormNameInput').val(),
        "version" : $('#newModelFormVersionInput').val(),
        "uuid" : $('#newModelFormUuidInput').val(),
        "description" : $('#newModelFormDescriptionTextArea').val()
    });

    var requestURL = restRootURL + "/Model/Create";

    ajax_post(requestURL, jsonString, function(resultData) {
        apexUtils_removeElement("newModelDivBackground");

        var requestURL = restRootURL + "/Model/GetKey";

        ajax_get(requestURL, function(data) {
            var modelKey = JSON.parse(data.messages.message[0]).apexArtifactKey;
            modelFileName = modelKey.name + ".json";
            pageControl_modelMode(modelKey.name, modelKey.version, modelFileName);
        });
        keyInformationTab_reset()
    });
}
