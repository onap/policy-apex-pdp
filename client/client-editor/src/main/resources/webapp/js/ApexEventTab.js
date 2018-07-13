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

function eventTab_reset() {
    eventTab_deactivate();
    eventTab_activate();
}

function eventTab_activate() {
    eventTab_create();
    
    var requestURL = restRootURL + "/Event/Get?name=&version=";

    ajax_get(requestURL, function(data) {
        $("#eventTableBody").find("tr:gt(0)").remove();


        for (var i = 0; i < data.messages.message.length; i++) {
            var event = JSON.parse(data.messages.message[i]).apexEvent;

            var eventTableRow_tr = document.createElement("tr");
            var eventid = event.key.name + ":"  + event.key.version;
            
            var eventTableRow = 
                "<td>"                      +
                eventid                        +
                "</td>"                     +
                "<td>"                      +
                event.nameSpace             +
                "</td>"                     +
                "<td>"                      +
                event.source                +
                "</td>"                     +
                "<td>"                      +
                event.target                +
                "</td>";

            eventTableRow += "<td><table class='ebTable'><thead><tr><th>Parameter</th><th>Parameter Type/Schema</th><th>Optional</th></tr></thead><tbody>";

            for (var p = 0; p < event.parameter.entry.length; p++) {
                var fieldEntry = event.parameter.entry[p];

                eventTableRow +=
                    "<tr><td>"                        +
                    fieldEntry.key                    +
                    "</td>"                           +
                    "<td>"                            +
                    fieldEntry.value.fieldSchemaKey.name + ":"  + fieldEntry.value.fieldSchemaKey.version +
                    "<td>"                            +
                    fieldEntry.value.optional
                    "</td></tr>";
            }

            eventTableRow += "</tbody></table></td>";
            
            eventTableRow_tr.innerHTML = eventTableRow; 

            eventTableRow_tr.addEventListener('contextmenu', rightClickMenu_scopePreserver("eventTabContent", "Event", event.key.name, event.key.version));  
            
            $("#eventTableBody").append(eventTableRow_tr);
        }
    });
}

function eventTab_deactivate() {
    apexUtils_removeElement("eventTabContent");
}

function eventTab_create() {
    var eventTab = document.getElementById("eventsTab");

    var eventTabContent = document.getElementById("eventTabContent");
    if (eventTabContent != null) {
        return
    }

    var eventTabContent = document.createElement("eventTabContent");
    eventTab.appendChild(eventTabContent);
    eventTabContent.setAttribute("id", "eventTabContent");
    eventTabContent.addEventListener('contextmenu', rightClickMenu_scopePreserver("eventTabContent", "Event", null, null));  

    var eventTable = createTable("eventTable");
    eventTabContent.appendChild(eventTable);

    var eventTableHeader = document.createElement("thead");
    eventTable.appendChild(eventTableHeader);
    eventTableHeader.setAttribute("id", "eventTableHeader");

    var eventTableHeaderRow = document.createElement("tr");
    eventTableHeader.appendChild(eventTableHeaderRow);
    eventTableHeaderRow.setAttribute("id", "eventTableHeaderRow");

    var eventTableKeyHeader = document.createElement("th");
    eventTableHeaderRow.appendChild(eventTableKeyHeader);
    eventTableKeyHeader.setAttribute("id", "eventTableKeyHeader");
    eventTableKeyHeader.appendChild(document.createTextNode("Event"));

    var eventTableNamespaceHeader = document.createElement("th");
    eventTableHeaderRow.appendChild(eventTableNamespaceHeader);
    eventTableNamespaceHeader.setAttribute("id", "eventTableNamespaceHeader");
    eventTableNamespaceHeader.appendChild(document.createTextNode("Name Space"));

    var eventTableSourceHeader = document.createElement("th");
    eventTableHeaderRow.appendChild(eventTableSourceHeader);
    eventTableSourceHeader.setAttribute("id", "eventTableSourceHeader");
    eventTableSourceHeader.appendChild(document.createTextNode("Source"));

    var eventTableTargetHeader = document.createElement("th");
    eventTableHeaderRow.appendChild(eventTableTargetHeader);
    eventTableTargetHeader.setAttribute("id", "eventTableTargetHeader");
    eventTableTargetHeader.appendChild(document.createTextNode("Target"));

    var eventTableParameterHeader = document.createElement("th");
    eventTableHeaderRow.appendChild(eventTableParameterHeader);
    eventTableParameterHeader.setAttribute("id", "eventTableParameterHeader");
    eventTableParameterHeader.appendChild(document.createTextNode("Parameters"));

    var eventTableBody = document.createElement("tbody");
    eventTable.appendChild(eventTableBody);
    eventTable.setAttribute("id", "eventTableBody");
}