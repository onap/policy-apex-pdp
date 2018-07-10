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
 * Create an Engine Status Table and its charts
 */
function createEngineStatusTable(id, startStopStatus) {
	var tableId = config.engineStatus.tableId;
	var headers = config.engineStatus.headers;

	// Create a wrapper div for both the table and the charts
	var wrapper = document.createElement("div");
	wrapper.setAttribute("id", id + "_wrapper");
	wrapper.setAttribute("class", "wrapper");
	$("." + config.engineStatus.parent).append(wrapper);

	// Create the table
	var table = createEngineTable($(wrapper), id, headers.map(function(a) {
		return a.title;
	}));
	var tableRow = document.createElement("tr");
	var tableData = "";
	for ( var h in headers) {
		tableData += "<td id=" + tableId + "_" + headers[h].id + "></td>";
	}
	tableRow.innerHTML = tableData;
	var actionTD = $(tableRow).find("#" + tableId + "_action");
	var checked = (startStopStatus === "STOPPED") ? "checked" : "";
	var chartWrapper = document.createElement("div");
	chartWrapper.setAttribute("id", "chartWrapper");
	actionTD
			.html('<label class="ebSwitcher"><input type="checkbox" '
					+ checked
					+ ' class="ebSwitcher-checkbox" /><div class="ebSwitcher-body"><div class="ebSwitcher-onLabel">Stopped</div><div class="ebSwitcher-switch"></div><div class="ebSwitcher-offLabel">Started</div></div></label>');

	var checkbox = $(actionTD).find('input:checkbox:first');
	checkbox.change(function(event) {
		var startstop;
		if (checkbox.prop('checked')) {
			startstop = "Stop";
		} else {
			startstop = "Start";
		}
		this.servicesCall.abort();
		ajax_get(restRootURL + "startstop", startStopCallback,
				this.engineURL.hostname, this.engineURL.port, {
					engineId : id.split("_")[1],
					startstop : startstop
				});
	}.bind(this));

	$(table).children("#engineTableBody").append(tableRow);

	var expand = document.createElement("i");
	expand
			.setAttribute("class",
					"ebIcon ebIcon_rowExpanded ebIcon_large ebIcon_interactive expandIcon");
	$(expand)
			.click(
					function() {
						if ($(chartWrapper).is(":visible")) {
							expand
									.setAttribute("class",
											"ebIcon ebIcon_rowCollapsed ebIcon_large ebIcon_interactive expandIcon");
						} else {
							expand
									.setAttribute("class",
											"ebIcon ebIcon_rowExpanded ebIcon_large ebIcon_interactive expandIcon");
						}
						$(chartWrapper).slideToggle();
					}.bind(this));
	$(wrapper).append(expand);
	$(wrapper).append(chartWrapper);
	return table;
}

/*
 * Check for any changes in the Engine Status Table data and its charts and
 * update only where necessary
 */
function setEngineStatusData(engineStatusData) {
	var tableId = config.engineStatus.tableId;
	var headers = config.engineStatus.headers.map(function(a) {
		return a.id;
	});
	for ( var esd in engineStatusData) {
		var id = tableId + "_" + engineStatusData[esd].id;
		var existingTable = undefined;
		for ( var est in this.engineStatusTables) {
			if (id === this.engineStatusTables[est].getAttribute("id")) {
				existingTable = this.engineStatusTables[est];
			}
		}

		var data = [ engineStatusData[esd].timestamp, id.split("_")[1],
				engineStatusData[esd].status,
				engineStatusData[esd].last_message,
				engineStatusData[esd].up_time,
				engineStatusData[esd].policy_executions ];

		var table = existingTable;
		// If no table already exists for the engine, add one
		if (!table) {
			table = createEngineStatusTable(id, engineStatusData[esd].status);
			table.setAttribute("id", id);
			table.style["margin-bottom"] = "10px";
			table.style.display = "inline-block";
			this.engineStatusTables.push(table);
		}

		// Update data in table
		for ( var h in headers) {
			var td = $(table).find("#" + tableId + "_" + headers[h]);
			if (td.html() !== data[h]) {
				$(table).find("#" + tableId + "_" + headers[h]).html(data[h]);
			}
		}

		var checked = (engineStatusData[esd].status === "STOPPED");
		var actionTD = $(table).find("#engineStatusTable_action");
		var checkbox = $(actionTD).find('input:checkbox:first');
		if (checkbox.is(":checked") !== checked) {
			checkbox.prop("checked", !checkbox.prop("checked"));
		}

		// Update charts
		var wrapper = $(table).parent();
		var chartWrapper = $(wrapper).find("#chartWrapper")

		var chartConfig = this.config.engineChart.lastPolicyDurationChart;
		var lastPolicyDurationChart = wrapper.find("#" + chartConfig.parent)[0];
		if (lastPolicyDurationChart) {
			updateChart(lastPolicyDurationChart, JSON
					.parse(engineStatusData[esd].last_policy_duration),
					chartConfig.nodeColour);
		} else {
			chartConfig = this.config.engineChart.lastPolicyDurationChart;
			var lastPolicyDurationDiv = document.createElement("div");
			lastPolicyDurationDiv.setAttribute("id", chartConfig.parent);
			lastPolicyDurationDiv.setAttribute("class", "apexChart");
			createChart(JSON.parse(engineStatusData[esd].last_policy_duration),
					lastPolicyDurationDiv, chartConfig.title, chartConfig.unit,
					chartConfig.lineStroke, chartConfig.nodeColour);
			$(chartWrapper).append(lastPolicyDurationDiv);
		}

		chartConfig = this.config.engineChart.averagePolicyDurationChart;
		var averagePolicyDurationChart = wrapper.find("#" + chartConfig.parent)[0];
		if (averagePolicyDurationChart) {
			updateChart(averagePolicyDurationChart, JSON
					.parse(engineStatusData[esd].average_policy_duration),
					chartConfig.nodeColour);
		} else {
			chartConfig = this.config.engineChart.averagePolicyDurationChart;
			var averagePolicyDurationDiv = document.createElement("div");
			averagePolicyDurationDiv.setAttribute("id", chartConfig.parent);
			averagePolicyDurationDiv.setAttribute("class", "apexChart");
			createChart(JSON
					.parse(engineStatusData[esd].average_policy_duration),
					averagePolicyDurationDiv, chartConfig.title,
					chartConfig.unit, chartConfig.lineStroke,
					chartConfig.nodeColour);
			$(chartWrapper).append(averagePolicyDurationDiv);
		}

	}
}