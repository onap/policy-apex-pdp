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

	formDiv.setAttribute("id",    "apexDialogDiv");
	formDiv.setAttribute("class", "apexDialogDiv");
	
	var headingSpan = document.createElement("span");
	formDiv.appendChild(headingSpan);

	headingSpan.setAttribute("class", "headingSpan");
	headingSpan.innerHTML = "Apex Engine Configuration";

	var form = document.createElement("apexDialog");
	formDiv.appendChild(form);

	form.setAttribute("id",     "apexDialog");
	form.setAttribute("class",  "form-style-1");
	form.setAttribute("method", "post");

	if(message) {
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
	urlInput.value = (engineUrl && engineUrl !== "null") ? JSON.parse(engineUrl).hostname + ":" + JSON.parse(engineUrl).port : "";
	urlLI.appendChild(urlInput);
	
	var inputLI = document.createElement("li");
	form.appendChild(inputLI);
	
	var submitInput = document.createElement("input");
	submitInput.setAttribute("id",    "submit");
	submitInput.setAttribute("class", "button ebBtn");
	submitInput.setAttribute("type",  "submit");
	submitInput.setAttribute("value", "Submit");
	submitInput.onclick = apexDialogForm_submitPressed;
	inputLI.appendChild(submitInput);
	
	// Enter key press triggers submit
	$(urlInput).keyup(function(event){
	    if(event.keyCode == 13){
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

	formDiv.setAttribute("id",    "apexErrorDialogDiv");
	formDiv.setAttribute("class", "apexDialogDiv apexErrorDialogDiv");
	
	var headingSpan = document.createElement("span");
	formDiv.appendChild(headingSpan);

	headingSpan.setAttribute("class", "headingSpan");
	headingSpan.innerHTML = title;

	var form = document.createElement("div");
	formDiv.appendChild(form);

	form.setAttribute("id",     "apexDialog");
	form.setAttribute("class",  "form-style-1");
	form.setAttribute("method", "post");

	if(message) {
		var messageLI = document.createElement("li");
		messageLI.setAttribute("class", "dialogMessage");
		messageLI.innerHTML = message;
		form.appendChild(messageLI);
	}

	var inputLI = document.createElement("li");
	form.appendChild(inputLI);
	
	var cancelInput = document.createElement("input");
	cancelInput.setAttribute("class", "button ebBtn");
	cancelInput.setAttribute("type",  "submit");
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
	if(url && url.length > 0) {
		var engineConfig = {
			hostname: url.split(":")[0],
			port: url.split(":")[1]
		};
		localStorage.setItem("apex-monitor-services_old", JSON.stringify(engineConfig));
		localStorage.setItem("apex-monitor-services", JSON.stringify(engineConfig));
		apexUtils_removeElement("apexDialogDivBackground");
		getEngineURL();
	}
}

/*
 * Remove an element from the page
 */
function apexUtils_removeElement(elementname){
	var element = document.getElementById(elementname);
	if (element != null) {
		element.parentNode.removeChild(element);
	}
}

/*
 * Compare two objects
 */
function deepCompare() {
	var i, l, leftChain, rightChain;

	function compare2Objects(x, y) {
		var p;

		// remember that NaN === NaN returns false
		// and isNaN(undefined) returns true
		if (isNaN(x) && isNaN(y) && typeof x === 'number'
				&& typeof y === 'number') {
			return true;
		}

		// Compare primitives and functions.     
		// Check if both arguments link to the same object.
		// Especially useful on the step where we compare prototypes
		if (x === y) {
			return true;
		}

		// Works in case when functions are created in constructor.
		// Comparing dates is a common scenario. Another built-ins?
		// We can even handle functions passed across iframes
		if ((typeof x === 'function' && typeof y === 'function')
				|| (x instanceof Date && y instanceof Date)
				|| (x instanceof RegExp && y instanceof RegExp)
				|| (x instanceof String && y instanceof String)
				|| (x instanceof Number && y instanceof Number)) {
			return x.toString() === y.toString();
		}

		// At last checking prototypes as good as we can
		if (!(x instanceof Object && y instanceof Object)) {
			return false;
		}

		if (x.isPrototypeOf(y) || y.isPrototypeOf(x)) {
			return false;
		}

		if (x.constructor !== y.constructor) {
			return false;
		}

		if (x.prototype !== y.prototype) {
			return false;
		}

		// Check for infinitive linking loops
		if (leftChain.indexOf(x) > -1 || rightChain.indexOf(y) > -1) {
			return false;
		}

		// Quick checking of one object being a subset of another.
		// todo: cache the structure of arguments[0] for performance
		for (p in y) {
			if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
				return false;
			} else if (typeof y[p] !== typeof x[p]) {
				return false;
			}
		}

		for (p in x) {
			if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
				return false;
			} else if (typeof y[p] !== typeof x[p]) {
				return false;
			}

			switch (typeof (x[p])) {
			case 'object':
			case 'function':

				leftChain.push(x);
				rightChain.push(y);

				if (!compare2Objects(x[p], y[p])) {
					return false;
				}

				leftChain.pop();
				rightChain.pop();
				break;

			default:
				if (x[p] !== y[p]) {
					return false;
				}
				break;
			}
		}

		return true;
	}

	if (arguments.length < 1) {
		return true;
	}

	for (i = 1, l = arguments.length; i < l; i++) {

		leftChain = []; //Todo: this can be cached
		rightChain = [];

		if (!compare2Objects(arguments[0], arguments[i])) {
			return false;
		}
	}

	return true;
}

function getHomepageURL() {
	var homepageURL = location.protocol + "//" + window.location.hostname + (location.port ? ':'+location.port : '') + (location.pathname.endsWith("/monitoring/") ? location.pathname.substring( 0, location.pathname.indexOf("monitoring/")) : location.pathname);
	location.href = homepageURL;
}