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

function files_fileOpen() {
    $('<input type="file">').on('change', function() {
        var reader = new FileReader();
        modelFileName = this.files[0].name;
        reader.readAsText(this.files[0]);

        reader.onload = function(event) {
            var requestURL = restRootURL + "/Model/Load";
            ajax_put(requestURL, event.target.result, function(resultData) {
                localStorage.setItem("apex_model_loaded", true);
                var requestURL = restRootURL + "/Model/GetKey";
                ajax_get(requestURL, function(data) {
                    var modelKey = JSON.parse(data.messages.message[0]).apexArtifactKey;
                    pageControl_modelMode(modelKey.name, modelKey.version, modelFileName);
                });
            });
        };
    }).click();
}

function files_fileDownload() {
    var requestURL = restRootURL + "/Model/Download";

    var downloadLink = document.createElement("a");
    document.body.appendChild(downloadLink);
    downloadLink.download = modelFileName;
    downloadLink.href = requestURL;
    downloadLink.click();
    document.body.removeChild(downloadLink);
}
