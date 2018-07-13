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

function modelHandling_analyse() {
    var requestURL = restRootURL + "/Model/Analyse";

    ajax_get(requestURL, function(data) {
        resultForm_activate(document.getElementById("mainArea"), "Model Analysis Result", data.messages.message[0]);
    });
}

function modelHandling_validate() {
    var requestURL = restRootURL + "/Model/Validate";

    ajax_getOKOrFail(requestURL, function(data) {
        var validationResultString = "";
        for (var i = 1; i < data.messages.message.length; i++) {
            validationResultString += (data.messages.message[i] + "\n");
        }
        resultForm_activate(document.getElementById("mainArea"), "Model Validation Result", validationResultString);
    });
}