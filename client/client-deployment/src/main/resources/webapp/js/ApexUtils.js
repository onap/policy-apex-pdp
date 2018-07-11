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
 * Crate a dialog with input, attach it to a given parent and show an optional message
 */
function apexDialogForm_activate(formParent, message) {
	apexUtils_removeElement("apexDialogDiv");

	var contentelement = document.createElement("apexDialogDiv");
	var formDiv = document.createElement("div");
	var backgroundDiv = document.createElement("div");
	backgroundDiv.setAttribute("id", "apexDialogDivBackground");
	backgroundDiv.setAttribute("class", "apexDialogDivBackground");

	backgroundDiv.appendChild(formDiv);
	contentelement.appendChild(backgroundDiv);
	formParent.appendChild(contentelement);

	formDiv.setAttribute("id", "apexDialogDiv");
	formDiv.setAttribute("class", "apexDialogDiv");

	var headingSpan = document.createElement("span");
	formDiv.appendChild(headingSpan);

	headingSpan.setAttribute("class", "headingSpan");
	headingSpan.innerHTML = "Apex Engine Configuration";

	var form = document.createElement("apexDialog");
	formDiv.appendChild(form);

	form.setAttribute("id", "apexDialog");
	form.setAttribute("class", "form-style-1");
	form.setAttribute("method", "post");

	if (message) {
		var messageLI = document.createElement("li");
		messageLI.setAttribute("class", "dialogMessage");
		messageLI.innerHTML = message;
		form.appendChild(messageLI);
	}

	var urlLI = document.createElement("li");
	form.appendChild(urlLI);

	var urlLabel = document.createElement("label");
	urlLI.appendChild(urlLabel);

	urlLabel.setAttribute("for", "apexDialogUrlInput");
	urlLabel.innerHTML = "Apex Engine rest URL:";

	var urlLabelSpan = document.createElement("span");
	urlLabel.appendChild(urlLabelSpan);

	urlLabelSpan.setAttribute("class", "required");
	urlLabelSpan.innerHTML = "*";

	var engineUrl = localStorage.getItem("apex-monitor-services_old");

	var urlInput = document.createElement("input");
	urlInput.setAttribute("id", "services_url_input");
	urlInput.setAttribute("placeholder", "localhost:12345");
	urlInput.value = (engineUrl && engineUrl !== "null") ? JSON
			.parse(engineUrl).hostname
			+ ":" + JSON.parse(engineUrl).port : "";
	urlLI.appendChild(urlInput);

	var inputLI = document.createElement("li");
	form.appendChild(inputLI);

	var submitInput = document.createElement("input");
	submitInput.setAttribute("id", "submit");
	submitInput.setAttribute("class", "button ebBtn");
	submitInput.setAttribute("type", "submit");
	submitInput.setAttribute("value", "Submit");
	submitInput.onclick = apexDialogForm_submitPressed;
	inputLI.appendChild(submitInput);

	// Enter key press triggers submit
	$(urlInput).keyup(function(event) {
		if (event.keyCode == 13) {
			$(submitInput).click();
		}
	});

	urlInput.focus();
}

/*
 * Create a dialog for displaying text
 */
function apexTextDialog_activate(formParent, message, title) {
	apexUtils_removeElement("apexDialogDiv");

	var contentelement = document.createElement("div");
	contentelement.setAttribute("id", "apexDialogDiv")
	var formDiv = document.createElement("div");
	var backgroundDiv = document.createElement("div");
	backgroundDiv.setAttribute("id", "apexDialogDivBackground");
	backgroundDiv.setAttribute("class", "apexDialogDivBackground");

	backgroundDiv.appendChild(formDiv);
	contentelement.appendChild(backgroundDiv);
	formParent.appendChild(contentelement);

	formDiv.setAttribute("id", "apexErrorDialogDiv");
	formDiv.setAttribute("class", "apexDialogDiv apexErrorDialogDiv");

	var headingSpan = document.createElement("span");
	formDiv.appendChild(headingSpan);

	headingSpan.setAttribute("class", "headingSpan");
	headingSpan.innerHTML = title;

	var form = document.createElement("div");
	formDiv.appendChild(form);

	form.setAttribute("id", "apexDialog");
	form.setAttribute("class", "form-style-1");
	form.setAttribute("method", "post");

	if (message) {
		var messageLI = document.createElement("li");
		messageLI.setAttribute("class", "dialogMessage");
		messageLI.innerHTML = message;
		form.appendChild(messageLI);
	}

	var inputLI = document.createElement("li");
	form.appendChild(inputLI);

	var cancelInput = document.createElement("input");
	cancelInput.setAttribute("class", "button ebBtn");
	cancelInput.setAttribute("type", "submit");
	cancelInput.setAttribute("value", "Close");
	cancelInput.onclick = newModelForm_cancelPressed;
	form.appendChild(cancelInput);
}

/*
 * Create a Success dialog
 */
function apexSuccessDialog_activate(formParent, message) {
	apexTextDialog_activate(formParent, message, "Success");
}

/*
 * Create an Error dialog
 */
function apexErrorDialog_activate(formParent, message) {
	apexTextDialog_activate(formParent, message, "Error");
}

/*
 * Dialog cancel callback
 */
function newModelForm_cancelPressed() {
	apexUtils_removeElement("apexDialogDivBackground");
}

/*
 * Dialog submit callback
 */
function apexDialogForm_submitPressed() {
	var url = $('#services_url_input').val();
	if (url && url.length > 0) {
		var engineConfig = {
			hostname : url.split(":")[0],
			port : url.split(":")[1]
		};
		localStorage.setItem("apex-monitor-services_old", JSON
				.stringify(engineConfig));
		localStorage.setItem("apex-monitor-services", JSON
				.stringify(engineConfig));
		apexUtils_removeElement("apexDialogDivBackground");
		getEngineURL();
	}
}

/*
 * Remove an element from the page
 */
function apexUtils_removeElement(elementname) {
	var element = document.getElementById(elementname);
	if (element != null) {
		element.parentNode.removeChild(element);
	}
}

function getHomepageURL() {
	var homepageURL = location.protocol
			+ "//"
			+ window.location.hostname
			+ (location.port ? ':' + location.port : '')
			+ (location.pathname.endsWith("/deployment/") ? location.pathname
					.substring(0, location.pathname.indexOf("deployment/"))
					: location.pathname);
	location.href = homepageURL;
}