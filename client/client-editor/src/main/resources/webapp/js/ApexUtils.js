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

function apexUtils_areYouSure(message) {
    return confirm(message);
}

function apexUtils_emptyElement(elementname) {
    var element = document.getElementById(elementname);
    if (element != null) {
        while (element.firstChild) {
            element.removeChild(element.firstChild);
        }
    }
}

function apexUtils_removeElement(elementname) {
    var element = document.getElementById(elementname);
    if (element != null) {
        element.parentNode.removeChild(element);
    }
}

var _entityMap = {
    '&' : '&amp;',
    '<' : '&lt;',
    '>' : '&gt;',
    '"' : '&quot;',
    "'" : '&#39;',
    '/' : '&#x2F;',
    '`' : '&#x60;',
    '=' : '&#x3D;',
    '\n' : '<br>',
    '\t' : '&nbsp;&nbsp;&nbsp;&nbsp;',
    ' ' : '&nbsp;',
};

function apexUtils_escapeHtml(string) {
    return String(string).replace(/[&<>"'\/\t\n ]/g, function(s) {
        return _entityMap[s];
    });
}

function apexUtils_deleteTableRow(tablename, tablerowindex) {
    document.getElementById(tablename).deleteRow(tablerowindex)
}

function createAddFormButton(_text) {
    var text = _text ? _text : "Add";
    var element = document.createElement("div");
    element.setAttribute("class", "add-field")
    var addIcon = document.createElement("i");
    addIcon.setAttribute("class", "form-add-icon ebIcon ebIcon_add");
    var addText = document.createElement("span");
    addText.setAttribute("class", "form-add-text");
    addText.innerHTML = text;
    element.appendChild(addIcon);
    element.appendChild(addText);
    return element;
}

function createEditArea(id, options, callback) {
    options = options ? options : {};
    var syntax = options.syntax ? options.syntax : "java";
    var start_highlight = options.start_highlight ? options.start_highlight : true;
    var min_height = options.min_height ? options.min_height : 400;
    var font_size = options.font_size ? options.font_size : 12
    var is_editable = options.hasOwnProperty("is_editable") ? options.is_editable : true;
    var toolbar = options.toolbar ? options.toolbar : "select_font, |, highlight, reset_highlight";

    setTimeout(function() {
        editAreaLoader.init({
            id : id,
            is_editable : is_editable,
            syntax : syntax,
            start_highlight : start_highlight,
            min_height : min_height,
            font_size : font_size,
            toolbar : toolbar,
            change_callback : "onEditAreaChange"
        });
    }, 100);

}

function onEditAreaChange(id) {
    $("#" + id).val(editAreaLoader.getValue(id));
}

function isFirefox() {
    return (navigator.userAgent.indexOf("Firefox") != -1);
}

function scrollToTop(element) {
    element = element ? element : "html, body";
    $(element).animate({
        scrollTop : 0
    }, 'fast');
}

function getHomepageURL() {
    var homepageURL = location.protocol
            + "//"
            + window.location.hostname
            + (location.port ? ':' + location.port : '')
            + (location.pathname.endsWith("/editor/") ? location.pathname.substring(0, location.pathname
                    .indexOf("editor/")) : location.pathname);
    location.href = homepageURL;
}