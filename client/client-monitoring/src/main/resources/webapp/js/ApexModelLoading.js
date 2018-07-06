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
 * Create the div for uploading Apex models
 */
function createModelLoadingDiv() {
	var fileLoader = document.createElement("input");
	fileLoader.setAttribute("type", "file");
	fileLoader.setAttribute("name", "apexModelFile");
	fileLoader.setAttribute("label", "Load Apex Model XML file");
	$('.modelLoading').append(fileLoader);
	
	var ignoreConflictsCheckbox = document.createElement("input");
	ignoreConflictsCheckbox.setAttribute("type", "checkbox");
	ignoreConflictsCheckbox.setAttribute("name", "ignoreContextConflicts");
	$('.modelLoading').append(ignoreConflictsCheckbox);
	
	ignoreConflictsLabel = document.createElement("label");
	ignoreConflictsLabel.setAttribute("class", "ignoreConflictsLabel");
	ignoreConflictsLabel.innerHTML = "Ignore Context Conflicts";
	$('.modelLoading').append(ignoreConflictsLabel);
	
	var forceUpdateCheckbox = document.createElement("input");
	forceUpdateCheckbox.setAttribute("type", "checkbox");
	forceUpdateCheckbox.setAttribute("name", "forceUpdate");
	$('.modelLoading').append(forceUpdateCheckbox);
	
	forceUpdateLabel = document.createElement("label");
	forceUpdateLabel.setAttribute("class", "ignoreConflictsLabel");
	forceUpdateLabel.innerHTML = "Force Update";
	$('.modelLoading').append(forceUpdateLabel);
	
	var submitButton = document.createElement("button");
	submitButton.setAttribute("class", "ebBtn");
	submitButton.innerHTML = "Load Apex Model XML file";
	$(submitButton).click(function() {
		var file = fileLoader.files[0];
		var ignoreConflicts = $(ignoreConflictsCheckbox).is(":checked");
		var forceUpdate = $(forceUpdateCheckbox).is(":checked");
		ajax_upload(restRootURL + "modelupload/", uploadCallback, this.engineURL.hostname, this.engineURL.port, file, ignoreConflicts, forceUpdate);
	}.bind(this));
	$('.modelLoading').append(submitButton);
}