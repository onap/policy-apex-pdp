/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

function ajax_get(requestURL, callback) {
    $.ajax({
        type : 'GET',
        url : requestURL,
        dataType : "json", // data type of response
        success : function(data, textStatus, jqXHR) {
            pageControl_successStatus(data);
            callback(data);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
        }
    });
}

function ajax_getWithKeyInfo(requestURL, objectType, callback, keyName) {
    var keyName = keyName || "key";
    var keyInfoURL = restRootURL + "/KeyInformation/Get?name=&version=";
    ajax_get(keyInfoURL, function(dataKeyInfos) {
        ajax_get(requestURL, function(data) {
            var keyInfos = [];
            for ( var i = 0; i < dataKeyInfos.messages.message.length; i++) {
                var ki = JSON.parse(dataKeyInfos.messages.message[i]).apexKeyInfo;
                keyInfos.push(ki);
            }
            var object = JSON.parse(data.messages.message[0])[objectType];
            var keyInfo = keyInfos.filter(function(ki) {
                return ki.key.name === object[keyName].name
                    && ki.key.version === object[keyName].version;
            });
            if (keyInfo.length > 0) {
                object.uuid = keyInfo[0].UUID;
                object.description = keyInfo[0].description;
            }
            callback(object);
        });
    });
}

function ajax_getOKOrFail(requestURL, callback) {
    $.ajax({
        type : 'GET',
        url : requestURL,
        dataType : "json", // data type of response
        success : function(data, textStatus, jqXHR) {
            pageControl_status(data);
            callback(data);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
        }
    });
}

function ajax_put(requestURL, requestData, callback) {
    $.ajax({
        type : 'PUT',
        contentType : 'application/json',
        url : requestURL,
        dataType : "json",
        data : requestData,
        success : function(responseData, textStatus, jqXHR) {
            pageControl_successStatus(responseData);
            callback(responseData);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
        }
    });
}

function ajax_post(requestURL, requestData, callback) {
    $.ajax({
        type : 'POST',
        contentType : 'application/json',
        url : requestURL,
        dataType : "json",
        data : requestData,
        success : function(responseData, textStatus, jqXHR) {
            pageControl_successStatus(responseData);
            callback(responseData);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
        }
    });
}

function ajax_delete(requestURL, callback) {
    $.ajax({
        type : 'DELETE',
        url : requestURL,
        dataType : "json", // data type of response
        success : function(data, textStatus, jqXHR) {
            pageControl_successStatus(data);
            callback(data);
        },
        error : function(jqXHR, textStatus, errorThrown) {
            pageControl_restError(requestURL, jqXHR, textStatus, errorThrown);
        }
    });
}
