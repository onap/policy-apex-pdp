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

function taskTab_reset() {
    taskTab_deactivate();
    taskTab_activate();
}

function taskTab_activate() {
    taskTab_create();
    
    var requestURL = restRootURL + "/Task/Get?name=&version=";

    ajax_get(requestURL, function(data) {
        $("#taskTableBody").find("tr:gt(0)").remove();
        
        for (var i = 0; i < data.messages.message.length; i++) {
            var task = JSON.parse(data.messages.message[i]).apexTask;
            
            var taskRow_tr = document.createElement("tr");
            var taskid = task.key.name + ":"  + task.key.version;

            var taskTableRow = 
                "<td>"                              +
                task.key.name + ":"  + task.key.version +
                "</td>"                                 +
                "<td>"                                  +
                task.taskLogic.logicFlavour             +
                "</td>";

            taskTableRow += "<td><table class='ebTable'><thead><tr class='headerRow'><th>Field Name</th><th>Field Type/Schema</th><th>Optional</th></tr></thead><tbody>";
            for (var f = 0; f < task.inputFields.entry.length; f++) {
                var fieldEntry = task.inputFields.entry[f];

                taskTableRow +=
                    "<tr><td>"                        +
                    fieldEntry.key                    +
                    "</td>"                           +
                    "<td>"                            +
                    fieldEntry.value.fieldSchemaKey.name + ":"  + fieldEntry.value.fieldSchemaKey.version +
                    "<td>"                            +
                    fieldEntry.value.optional           +
                    "</td></tr>";
            }
            taskTableRow += "</tbody></table></td>";
            
            taskTableRow += "<td><table class='ebTable'><thead><tr class='headerRow'><th>Field Name</th><th>Field Type/Schema</th><th>Optional</th></tr></thead><tbody>";
            for (var f = 0; f < task.outputFields.entry.length; f++) {
                var fieldEntry = task.outputFields.entry[f];

                taskTableRow +=
                    "<tr><td>"                        +
                    fieldEntry.key                    +
                    "</td>"                           +
                    "<td>"                            +
                    fieldEntry.value.fieldSchemaKey.name + ":"  + fieldEntry.value.fieldSchemaKey.version +
                    "<td>"                            +
                    fieldEntry.value.optional           +
                    "</td></tr>";
            }
            taskTableRow += "</tbody></table></td>";

            taskTableRow += "<td><table class='ebTable'><thead><tr class='headerRow'><th>Parameter Name</th><th>Default Value</th></tr></thead><tbody>";
            for (var p = 0; p < task.taskParameters.entry.length; p++) {
                var parameterEntry = task.taskParameters.entry[p];

                taskTableRow +=
                    "<tr><td>"                        +
                    parameterEntry.key                +
                    "</td>"                           +
                    "<td>"                            +
                    parameterEntry.value.defaultValue +
                    "</td>";
            }
            taskTableRow += "</tbody></table></td>";
            
            taskTableRow += "<td><table class='ebTable'><tbody>";
            for (var c = 0; c < task.contextAlbumReference.length; c++) {
                var contextAlbumReference = task.contextAlbumReference[c];

                taskTableRow +=
                    "<tr><td>"                            +
                    contextAlbumReference.name + ":" + contextAlbumReference.version  +
                    "</td></tr>";
            }
            taskTableRow += "</tbody></table></td>";

            taskRow_tr.innerHTML = taskTableRow; 
            taskRow_tr.addEventListener('contextmenu', rightClickMenu_scopePreserver("taskTabContent", "Task", task.key.name, task.key.version));  

            $("#taskTableBody").append(taskRow_tr);

        }
    });
}

function taskTab_deactivate() {
    apexUtils_removeElement("taskTabContent");
}

function taskTab_create() {
    var taskTab = document.getElementById("tasksTab");

    var taskTabContent = document.getElementById("taskTabContent");
    if (taskTabContent != null) {
        return
    }

    var taskTabContent = document.createElement("taskTabContent");
    taskTab.appendChild(taskTabContent);
    taskTabContent.setAttribute("id", "taskTabContent");
    taskTabContent.addEventListener('contextmenu', rightClickMenu_scopePreserver("taskTabContent", "Task", null, null));  

    var taskTable = createTable("taskTable");
    taskTabContent.appendChild(taskTable);

    var taskTableHeader = document.createElement("thead");
    taskTable.appendChild(taskTableHeader);
    taskTableHeader.setAttribute("id", "taskTableHeader");

    var taskTableHeaderRow = document.createElement("tr");
    taskTableHeader.appendChild(taskTableHeaderRow);
    taskTableHeaderRow.setAttribute("id", "taskTableHeaderRow");

    var taskTableKeyHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableKeyHeader);
    taskTableKeyHeader.setAttribute("id", "taskTableKeyHeader");
    taskTableKeyHeader.appendChild(document.createTextNode("Task"));

    var taskTableLogicFlavourHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableLogicFlavourHeader);
    taskTableLogicFlavourHeader.setAttribute("id", "taskTableLogicFlavourHeader");
    taskTableLogicFlavourHeader.appendChild(document.createTextNode("Logic Flavour"));

    var taskTableInputFieldHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableInputFieldHeader);
    taskTableInputFieldHeader.setAttribute("id", "taskTableInputFieldHeader");
    taskTableInputFieldHeader.appendChild(document.createTextNode("Input Fields"));

    var taskTableOutputFieldHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableOutputFieldHeader);
    taskTableOutputFieldHeader.setAttribute("id", "taskTableOutputFieldHeader");
    taskTableOutputFieldHeader.appendChild(document.createTextNode("Output Fields"));

    var taskTableParameterHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableParameterHeader);
    taskTableParameterHeader.setAttribute("id", "taskTableParameterHeader");
    taskTableParameterHeader.appendChild(document.createTextNode("Parameters"));

    var taskTableContextReferenceHeader = document.createElement("th");
    taskTableHeaderRow.appendChild(taskTableContextReferenceHeader);
    taskTableContextReferenceHeader.setAttribute("id", "taskTableContextReferenceHeader");
    taskTableContextReferenceHeader.appendChild(document.createTextNode("Context Album References"));

    var taskTableBody = document.createElement("tbody");
    taskTable.appendChild(taskTableBody);
    taskTable.setAttribute("id", "taskTableBody");
}