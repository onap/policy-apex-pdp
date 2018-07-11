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

/*
 * Create the Engine Service Table 
 */
function createEngineServiceTable() {
    var tableId = config.engineService.tableId;
    var headers = config.engineService.headers;
    var table = createEngineTable($("." + config.engineService.parent), tableId, headers.map(function(a) {
        return a.title;
    }));
    var tableRow = document.createElement("tr");
    var tableData = "";
    for ( var h in headers) {
        tableData += "<td id=" + tableId + "_" + headers[h].id + "></td>";
    }
    tableRow.innerHTML = tableData;
    var actionTD = $(tableRow).find("#" + tableId + "_periodic_events");
    actionTD
            .html('<input type="text" name="period" id="period" style="display:inline-block"><label class="ebSwitcher"><input type="checkbox" class="ebSwitcher-checkbox" /><div class="ebSwitcher-body"><div class="ebSwitcher-onLabel">Stopped</div><div class="ebSwitcher-switch"></div><div class="ebSwitcher-offLabel">Started</div></div></label>');
    var period = actionTD.find("#period");
    var switcher = actionTD.find(".ebSwitcher");
    switcher.css('display', 'inline-block');
    switcher.css('margin-left', '5px');
    switcher.css('vertical-align', 'middle');
    var checkbox = $(actionTD).find('input:checkbox:first');
    checkbox.change(function(event) {
        var startstop;
        if (checkbox.prop('checked')) {
            startstop = "Stop";
        } else {
            startstop = "Start";
        }
        this.servicesCall.abort();
        ajax_get(restRootURL + "periodiceventstartstop", startStopCallback, this.engineURL.hostname,
                this.engineURL.port, {
                    engineId : this.engineId,
                    startstop : startstop,
                    period : period.val()
                }, resetPeriodicEvents);
    }.bind(this));
    $(table).children("#engineTableBody").append(tableRow);
}

/*
 * Check for any changes in the Engine Service Table data and update only where
 * necessary
 */
function setEngineServiceData(engineId, modelId, server, port, periodicEvents) {
    this.engineId = engineId;
    var tableId = config.engineService.tableId;
    var headers = config.engineService.headers.map(function(a) {
        return a.id;
    });
    var data = [ engineId, server + ":" + port, modelId ];

    var engineServiceTable = $("#engineServicesTable");

    for ( var h in headers) {
        var td = engineServiceTable.find("#" + tableId + "_" + headers[h]);
        if (td.html() !== data[h]) {
            engineServiceTable.find("#" + tableId + "_" + headers[h]).html(data[h]);
        }
    }

    var actionTD = engineServiceTable.find("#" + tableId + "_periodic_events");
    var checkbox = $(actionTD).find('input:checkbox:first');
    if (checkbox.is(":checked") === periodicEvents) {
        checkbox.prop("checked", !checkbox.prop("checked"));
    }
}

/*
 * Resets the switcher for Periodic Events in the Engine Service Table
 */
function resetPeriodicEvents() {
    var engineServiceTable = $("#engineServicesTable");
    var periodicEventsTD = $(engineServiceTable).find("#engineServicesTable_periodic_events");
    var checkbox = $(periodicEventsTD).find('input:checkbox:first');
    if (checkbox.is(":checked")) {
        checkbox.prop("checked", false);
    }
}
