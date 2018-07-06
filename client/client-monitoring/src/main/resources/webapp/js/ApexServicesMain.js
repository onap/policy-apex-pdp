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

var restRootURL;

// Configuration used for page layout and charts
var config = {
	refresh : 5000,
	engineService : {
		parent : "engineService",
		tableId : "engineServicesTable",
		headers : [ {
			title : "Engine Service ID",
			id : "engine_id"
		}, {
			title : "server:port",
			id : "server_port"
		}, {
			title : "Model ID",
			id : "model_id"
		}, {
			title : "Periodic Events",
			id : "periodic_events"
		} ]
	},
	engineSummary : {
		parent : "engineSummary",
		tableId : "engineSummaryTable",
		headers : [ {
			title : "Timestamp",
			id : "timestamp"
		}, {
			title : "Up Time of oldest engine (ms)",
			id : "up_time"
		}, {
			title : "Sum of policy executions",
			id : "policy_executions"
		} ],
		chart : {
			avgPolicyDurationChart : {
				parent : "avgPolicyDuration",
				title : "Average Policy Duration (ms)",
				unit : "ms",
				lineStroke : "#5FBADD",
				nodeColour : "#00A9D4"
			},
		}
	},
	engineStatus : {
		parent : "engineStatus",
		tableId : "engineStatusTable",
		headers : [ {
			title : "Timestamp",
			id : "timestamp"
		}, {
			title : "Engine ID",
			id : "engine_id"
		}, {
			title : "Engine Status",
			id : "engine_status"
		}, {
			title : "Last Message",
			id : "last_message"
		}, {
			title : "Up Time (ms)",
			id : "up_time"
		}, {
			title : "Policy Executions",
			id : "policy_executions"
		}, {
			title : "Action",
			id : "action"
		} ]
	},
	engineContext : {
		parent : "context",
		tableId : "engineContextTable",
		headers : [ {
			title : "Name",
			id : "name"
		}, {
			title : "Version",
			id : "version"
		}, {
			title : "Info",
			id : "schema"
		} ]
	},
	engineChart : {
		lastPolicyDurationChart : {
			parent : "lastPolicyDurationChart",
			title : "Last Policy Duration (ms)",
			unit : "ms",
			lineStroke : "#F5A241",
			nodeColour : "#F08A00"
		},
		averagePolicyDurationChart : {
			parent : "averagePolicyDurationChart",
			title : "Average Policy Duration (ms)",
			unit : "ms",
			lineStroke : "#00625F",
			nodeColour : "#007B78"
		}
	}
};

function servicesCallback(data) {
	// If engine url in cookie has not been cleared
	if (localStorage.getItem("apex-monitor-services")) {
		setEngineServiceData(data.engine_id, data.model_id, data.server,
				data.port, data.periodic_events);
		setEngineSummaryData(data.status);
		setEngineStatusData(data.status);
		setEngineContextData(data.context);

		// Make content visible after data has been returned for the first time
		if (!$(".content").is(':visible')) {
			$(".content").fadeIn();
		}

		// Repeat the same request
		setTimeout(function() {
			this.servicesCall = ajax_get(restRootURL, servicesCallback,
					this.engineURL.hostname, this.engineURL.port);
		}, config.refresh);
	}
}

/*
 * Callback for starting/stopping an engine/events
 */
function startStopCallback() {
	this.servicesCall = ajax_get(restRootURL, servicesCallback,
			this.engineURL.hostname, this.engineURL.port);
}

/*
 * Callback for uploading a model
 */
function uploadCallback(response) {
	// Open a dialog showing the response
	apexSuccessDialog_activate(document.body, response);
}

/*
 * Clears and resets all content on the page
 */
function setUpPage() {
	// Hide all content
	$('#content').hide();

	// Clear each div
	$('#content > div').each(function() {
		$(this).empty();
	});

	// Reset trackers for tables
	this.engineStatusTables = [];
	this.engineContextTables = [];

	// Set up content div's
	createEngineServiceTable();
	createEngineSummaryTable();
	createModelLoadingDiv();
}

/*
 * Retrieves the engine URL from the cookie. If it has not been set yet, then a
 * dialog is shown asking for it
 */
function getEngineURL(message) {
	// The engine URL is stored in a cookie using the key
	// "apex-monitor-services"
	var engineURL = localStorage.getItem("apex-monitor-services");

	// This url is used to store the last known engine URL so that the user
	// doesn't have to retype it every time
	var oldEngineURL = localStorage.getItem("apex-monitor-services_old");

	// If an engine URL is stored in the cookie
	if (engineURL) {
		// Parse the engine URL
		this.engineURL = JSON.parse(engineURL);

		// Send a request with that engine URL
		this.servicesCall = ajax_get(restRootURL, servicesCallback,
				this.engineURL.hostname, this.engineURL.port);
	} else {
		// Prompt for engine URL
		apexDialogForm_activate(document.body, message);
	}
}

/*
 * Clears the cookie and reset the page
 */
function clearEngineURL() {
	// Remove engine URL from cookie
	localStorage.removeItem("apex-monitor-services");

	// Reset the page
	setUpPage();
}

/*
 * Called after the DOM is ready
 */
$(document)
		.ready(
				function() {
					restRootURL = location.protocol
							+ "//"
							+ window.location.hostname
							+ (location.port ? ':' + location.port : '')
							+ (location.pathname.endsWith("/monitoring/") ? location.pathname
									.substring(0, location.pathname
											.indexOf("monitoring/"))
									: location.pathname)
							+ "apexservices/monitoring/";
					// Initialize tooltip for the charts
					initTooltip();

					// Set up the structure of the page
					setUpPage();

					// Check cookies for engine URL
					getEngineURL();

					// Add click event to config icon for clearing engine URL
					$(".ebSystemBar-config").click(function() {
						// Clear the engine URL
						clearEngineURL();

						// Request the engine URL
						getEngineURL();
					});
				});