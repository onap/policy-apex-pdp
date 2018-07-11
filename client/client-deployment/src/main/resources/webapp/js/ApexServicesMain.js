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
		} ]
	}
}

/*
 * Callback for showing model info
 */
function servicesCallback(data) {
	// If engine url in cookie has not been cleared
	if (localStorage.getItem("apex-monitor-services")) {
		setEngineServiceData(data.engine_id, data.model_id, data.server,
				data.port, data.periodic_events);

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
	// Clear each div
	$('#content > div').each(function() {
		$(this).empty();
	});

	// Set up content div's
	createEngineServiceTable();
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
							+ (location.pathname.endsWith("/deployment/") ? location.pathname
									.substring(0, location.pathname
											.indexOf("deployment/"))
									: location.pathname)
							+ "apexservices/deployment/";

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