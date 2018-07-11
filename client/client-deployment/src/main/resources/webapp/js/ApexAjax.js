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
 * Send a GET request
 */
function ajax_get(requestURL, callback, hostName, port, params, errorCallback) {
	var data = {
		hostName : hostName,
		port : port
	};
	for ( var p in params) {
		data[p] = params[p];
	}
	return $
			.ajax({
				type : 'GET',
				url : requestURL,
				dataType : "json",
				data : data,
				success : function(data, textStatus, jqXHR) {
					if (callback) {
						callback(data);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.status == 500 || jqXHR.status == 404) {
						if (jqXHR.responseText
								.indexOf("cound not handshake with server") !== -1
								|| jqXHR.status == 404) {
							clearEngineURL();
							getEngineURL(jqXHR.responseText);
						} else {
							apexErrorDialog_activate(document.body,
									jqXHR.responseText);
						}
					}
					if (errorCallback) {
						errorCallback(jqXHR, textStatus, errorThrown);
					}
				}
			});
}

/*
 * Send a POST request and add a file to its payload
 */
function ajax_upload(requestURL, callback, hostName, port, fileUrl,
		ignoreConflicts, forceUpdate) {
	var formData = new FormData();
	formData.append("hostName", hostName);
	formData.append("port", port);
	formData.append("file", fileUrl);
	formData.append("ignoreConflicts", ignoreConflicts);
	formData.append("forceUpdate", forceUpdate);
	return $.ajax({
		url : requestURL,
		type : "POST",
		contentType : false,
		dataType : "text",
		processData : false,
		data : formData,
		success : function(data, textStatus, jqXHR) {
			callback(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.status == 500) {
				apexErrorDialog_activate(document.body, jqXHR.responseText);
			}
		}
	});
}
