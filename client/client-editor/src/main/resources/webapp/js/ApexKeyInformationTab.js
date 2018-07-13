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

function keyInformationTab_reset() {
    keyInformationTab_deactivate();
    keyInformationTab_activate();
}

function keyInformationTab_activate() {
    keyInformationTab_create();
    
    var requestURL = restRootURL + "/KeyInformation/Get?name=&version=";

    ajax_get(requestURL, function(data) {
        $("#keyInformationTableBody").find("tr:gt(0)").remove();
        
        for (var i = 0; i < data.messages.message.length; i++) {
            var keyInfo = JSON.parse(data.messages.message[i]).apexKeyInfo;
            
            var keyInfoRow_tr = document.createElement("tr");
            var keyInfoRow =    
                    "<td>"                                               +
                    keyInfo.key.name + ":"  + keyInfo.key.version     +
                    "</td>"                                           +
                    "<td><uuid>"                                    +
                    apexUtils_escapeHtml(keyInfo.UUID)                 +
                    "</uuid></td>"                                     +
                    "<td><desc>"                                    +
                    apexUtils_escapeHtml(keyInfo.description)          +
                    "</desc></td>";
            keyInfoRow_tr.innerHTML = keyInfoRow; 
            // keyInfoRow_tr.addEventListener('contextmenu',
            // rightClickMenu_scopePreserver("keyInformationTabContent",
            // "KeyInformation", keyInfo.key.name, keyInfo.key.version));
            $("#keyInformationTableBody").append(keyInfoRow_tr);

        }
    });
}

function keyInformationTab_deactivate() {
    apexUtils_removeElement("keyInformationTabContent");
}

function keyInformationTab_create() {
    var keyInformationTab = document.getElementById("keyInformationTab");

    var keyInformationTabContent = document.getElementById("keyInformationTabContent");
    if (keyInformationTabContent != null) {
        return
    }

    var keyInformationTabContent = document.createElement("keyInformationTabContent");
    keyInformationTab.appendChild(keyInformationTabContent);
    keyInformationTabContent.setAttribute("id", "keyInformationTabContent");

    var keyInformationTable = createTable("keyInformationTable");
    keyInformationTable.oncontextmenu = new Function("return false;")
    keyInformationTabContent.appendChild(keyInformationTable);

    var keyInformationTableHeader = document.createElement("thead");
    keyInformationTable.appendChild(keyInformationTableHeader);
    keyInformationTableHeader.setAttribute("id", "keyInformationTableHeader");

    var keyInformationTableHeaderRow = document.createElement("tr");
    keyInformationTableHeader.appendChild(keyInformationTableHeaderRow);
    keyInformationTableHeaderRow.setAttribute("id", "keyInformationTableHeaderRow");

    var keyInformationTableKeyHeader = document.createElement("th");
    keyInformationTableHeaderRow.appendChild(keyInformationTableKeyHeader);
    keyInformationTableKeyHeader.setAttribute("id", "keyInformationTableKeyHeader");
    keyInformationTableKeyHeader.appendChild(document.createTextNode("Key Information"));

    var keyInformationTableUUIDHeader = document.createElement("th");
    keyInformationTableHeaderRow.appendChild(keyInformationTableUUIDHeader);
    keyInformationTableUUIDHeader.setAttribute("id", "keyInformationTableUUIDHeader");
    keyInformationTableUUIDHeader.appendChild(document.createTextNode("UUID"));

    var keyInformationTableDescriptionHeader = document.createElement("th");
    keyInformationTableHeaderRow.appendChild(keyInformationTableDescriptionHeader);
    keyInformationTableDescriptionHeader.setAttribute("id", "keyInformationTableDescriptionHeader");
    keyInformationTableDescriptionHeader.appendChild(document.createTextNode("Description"));

    var keyInformationTableBody = document.createElement("tbody");
    keyInformationTable.appendChild(keyInformationTableBody);
    keyInformationTable.setAttribute("id", "keyInformationTableBody");
}
