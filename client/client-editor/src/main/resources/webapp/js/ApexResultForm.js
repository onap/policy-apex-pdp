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

function resultForm_activate(formParent, formHeading, formText) {
    apexUtils_removeElement("newModelDivBackground");

    var contentelement = document.createElement("resultFormDiv");
    var formDiv = document.createElement("div");
    var backgroundDiv = document.createElement("div");
    backgroundDiv.setAttribute("id", "newModelDivBackground");
    backgroundDiv.setAttribute("class", "newModelDivBackground");

    backgroundDiv.appendChild(formDiv);
    contentelement.appendChild(backgroundDiv);
    formParent.appendChild(contentelement);

    formDiv.setAttribute("id", "resultFormDiv");
    formDiv.setAttribute("class", "resultFormDiv");

    var headingSpan = document.createElement("span");
    formDiv.appendChild(headingSpan);

    headingSpan.setAttribute("class", "headingSpan");
    headingSpan.innerHTML = formHeading;

    var form = document.createElement("resultForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "resultForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");

    var ul = document.createElement("ul");
    form.appendChild(ul);

    var resultTextLI = document.createElement("li");
    form.appendChild(resultTextLI);

    var resultTextLabel = document.createElement("label");
    resultTextLI.appendChild(resultTextLabel);
    resultTextLabel.setAttribute("for", "resultFormTextArea");
    resultTextLabel.innerHTML = "resultText: ";

    var resultTextArea = document.createElement("textarea");
    resultTextLI.appendChild(resultTextArea);

    resultTextArea.setAttribute("id", "resultFormTextArea");
    resultTextArea.setAttribute("resultText", "resultFormTextArea");
    resultTextArea.setAttribute("class", "field-long field-textarea ebTextArea ebTextArea_width_full eb_scrollbar");
    resultTextArea.setAttribute("readonly", "readonly");
    resultTextArea.style.cursor = "text";

    resultTextArea.value = formText;

    var inputLI = document.createElement("li");
    form.appendChild(inputLI);

    var okInput = document.createElement("input");
    inputLI.appendChild(okInput);

    okInput.setAttribute("id", "generateDescription");
    okInput.setAttribute("class", "okButton ebBtn");
    okInput.setAttribute("type", "submit");
    okInput.setAttribute("value", "OK");

    okInput.onclick = resultForm_okPressed;
}

function resultForm_okPressed() {
    apexUtils_removeElement("newModelDivBackground");
}
