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
