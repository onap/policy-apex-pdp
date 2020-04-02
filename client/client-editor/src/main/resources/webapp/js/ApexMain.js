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

var restRootURL = null;
var modelFileName = null;

$("#menu").fileMenu({
    slideSpeed : 200
});

$(document).ready(function() {
    pageControl_noModelMode();
    main_getRestRootURL();
});

$("#menu li").not(".emptyMessage").click(function() {
    switch (this.id) {
    case "menuFileNew":
        newModelForm_activate(document.getElementById("mainArea"));
        break;

    case "menuFileOpen":
        files_fileOpen();
        break;

    case "menuFileDownload":
        files_fileDownload();
        break;

    case "menuFileClear":
        if (confirm("Clear the current model?")) {
            var requestURL = restRootURL + "/Model/Delete";

            ajax_delete(requestURL, function(data) {
                localStorage.removeItem("apex_model_loaded");
                localStorage.removeItem("apex_tab_index");
                $("#mainTabs").tabs("option", "active", 0);
                pageControl_noModelMode();
            });
        }
        break;

    case "menuFileNewSession":
        clearLocalStorage();
        location.reload();
        break;

    case "menuFileClose":
        if (confirm("Close Apex Editor?")) {
            clearLocalStorage();
            window.location.href = window.location.href + "close.html";
        }
        break;

    case "menuModelAnalyse":
        modelHandling_analyse();
        break;

    case "menuModelValidate":
        modelHandling_validate();
        break;

    case "menuConceptsContextSchemas":
        $("#mainTabs").tabs("option", "active", 0);
        break;
    case "menuConceptsEvents":
        $("#mainTabs").tabs("option", "active", 1);
        break;
    case "menuConceptsContextAlbums":
        $("#mainTabs").tabs("option", "active", 2);
        break;
    case "menuConceptsTasks":
        $("#mainTabs").tabs("option", "active", 3);
        break;
    case "menuConceptsPolicies":
        $("#mainTabs").tabs("option", "active", 4);
        break;
    case "menuConceptsKeyInformation":
        $("#mainTabs").tabs("option", "active", 5);
        break;

    default:
        break;
    }
});

function main_getRestRootURL() {
    var href = location.protocol
            + "//"
            + window.location.hostname
            + (location.port ? ':' + location.port : '')
            + (location.pathname.endsWith("/editor/") ? location.pathname.substring(0, location.pathname
                    .indexOf("editor/")) : location.pathname);
    var restContext = "apexservices/editor/";
    if (localStorage.getItem("apex_session")) {
        restRootURL = href + restContext + localStorage.getItem("apex_session");
        var requestURL = restRootURL + "/Model/GetKey";
        ajax_get(requestURL, function(data) {
            $("#statusMessageTable").append("<tr><td> REST root URL set to: " + restRootURL + "</td></tr>");
            if (localStorage.getItem("apex_model_loaded")) {
                var modelKey = JSON.parse(data.messages.message[0]).apexArtifactKey;
                pageControl_modelMode(modelKey.name, modelKey.version, modelFileName);
                if (localStorage.getItem("apex_tab_index")) {
                    $("#mainTabs").tabs({
                      active: localStorage.getItem("apex_tab_index")
                    });
                }
            }
        });
    } else {
        var createSessionURL = href + restContext + "-1/Session/Create";

        ajax_get(createSessionURL, function(data) {
            localStorage.setItem("apex_session", data.messages.message[0]);
            restRootURL = href + restContext + localStorage.getItem("apex_session");
            $("#statusMessageTable").append("<tr><td> REST root URL set to: " + restRootURL + "</td></tr>");
        });
    }
}

function clearLocalStorage() {
    localStorage.removeItem("apex_session");
    localStorage.removeItem("apex_model_loaded");
    localStorage.removeItem("apex_tab_index");
}

/* Inline Message */
var ebInlineMessageHeight = $(".ebInlineMessage").height();

$(".ebInlineMessage").mouseenter(function(e) {
    e.stopPropagation();
    $(this).stop();
    var contentHeight = $(this).children('.ebInlineMessage-contentHolder').height();
    if (contentHeight > ebInlineMessageHeight) {
        $(".ebInlineMessage").animate({
            height : contentHeight + 12
        }, 200);
    }
});

$(".ebInlineMessage").mouseleave(function(e) {
    e.stopPropagation();
    $(this).stop();
    $(".ebInlineMessage").animate({
        height : ebInlineMessageHeight
    }, 200);
});

$(document).ready(function() {
    $(".content").fadeIn();
});
