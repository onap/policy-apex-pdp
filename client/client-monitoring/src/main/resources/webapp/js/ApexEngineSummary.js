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
function createEngineSummaryTable() {
	var tableId = config.engineSummary.tableId;
	var headers = config.engineSummary.headers;
	
	// Create a wrapper div for both the table and the charts
	var wrapper = document.createElement("div");
	wrapper.setAttribute("id",  "engineSummary_wrapper");
	wrapper.setAttribute("class", "wrapper_borderless");
	$("." + config.engineSummary.parent).append(wrapper);
	
	var table = createEngineTable($(wrapper), tableId, headers.map(function(a) {
		return a.title;
	}));
	var tableRow = document.createElement("tr");
	var tableData = "";
	for (var h in headers) {
		tableData += "<td id=" + tableId + "_" + headers[h].id + "></td>";
	}
	tableRow.innerHTML = tableData;
	
	$(table).children("#engineTableBody").append(tableRow);
	
}

function setEngineSummaryData(data) {
	this._setEngineSummaryData(getLatestTimestamp(data), getUptimeOfOldestEngine(data), getSumOfPolicyExecutions(data), getAvgPolicyDuration(data));
}

/*
 * Check for any changes in the Engine Summary Table data and update only where necessary 
 */
function _setEngineSummaryData(timestamp, upTime, policyExecutions, avgPolicyDuration) {
	
	var tableId = config.engineSummary.tableId;
	var headers = config.engineSummary.headers.map(function(a) {
		return a.id;
	});
	var data = [timestamp, upTime, policyExecutions];
	
	var engineSummaryTable = $("#engineSummaryTable");
	
	for(var h in headers) {
		var td = engineSummaryTable.find("#" + tableId + "_" + headers[h]);
		if(td.html() !== data[h]) {
			engineSummaryTable.find("#" + tableId + "_" + headers[h]).html(data[h]);
		}
	}
	
	// Update charts
	var wrapper = engineSummaryTable.parent();
	var chartConfig = this.config.engineSummary.chart.avgPolicyDurationChart;
	var avgPolicyDurationChart = wrapper.find("#"+chartConfig.parent)[0];
	if(avgPolicyDurationChart) {
		updateChart(avgPolicyDurationChart, avgPolicyDuration, chartConfig.nodeColour);
	} else {
		var avgPolicyDurationDiv = document.createElement("div");
		avgPolicyDurationDiv.setAttribute("id", chartConfig.parent);
		avgPolicyDurationDiv.setAttribute("class", "apexChart_inline");
		createChart(avgPolicyDuration, avgPolicyDurationDiv, chartConfig.title, chartConfig.unit, chartConfig.lineStroke, chartConfig.nodeColour);
		$(wrapper).append(avgPolicyDurationDiv);
	}
}

function getLatestTimestamp(data) {
	var latestTimestamp = {
		displayText: "-1",
		value: -1
	};
	for(var d in data) {
		var currentDateInMillis = new Date(data[d].timestamp.replace(/-/g, '/').split('.')[0]).getTime();
		if(currentDateInMillis > latestTimestamp.value) {
			latestTimestamp.displayText = data[d].timestamp;
			latestTimestamp.value = currentDateInMillis;
		}
	}
	return latestTimestamp.displayText;
}

function getUptimeOfOldestEngine(data) {
	var oldestUpTime = -1;
	for(var d in data) {
		if(data[d].up_time > oldestUpTime) {
			oldestUpTime = data[d].up_time; 
		}
	}
	return oldestUpTime;
}

function getSumOfPolicyExecutions(data) {
	var totalPolicyExecutions = 0;
	for(var d in data) {
		totalPolicyExecutions += data[d].policy_executions;
	}
	return totalPolicyExecutions;
}

function getAvgPolicyDuration(data) {
	var chartData = [];
	var avgPolicyDurations = [];
	for(var d in data) {
		var avgPolicyDuration = JSON.parse(data[d].average_policy_duration);
		avgPolicyDurations.push(avgPolicyDuration);
	}
	
	if(avgPolicyDurations.length > 0) {
		chartData = avgPolicyDurations[0];
		for(var i = 1; i < avgPolicyDurations.length; i++) {
			var engineData = avgPolicyDurations[i];
			for(var c in chartData) {
				chartData[c].value += engineData[c].value;
			}
		}
	}
	
	for(var c2 in chartData) {
		chartData[c2].value = Math.round(chartData[c2].value/data.length);
	}
	
	return chartData;
}