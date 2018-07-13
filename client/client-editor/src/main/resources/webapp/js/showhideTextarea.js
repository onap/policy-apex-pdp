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

function showHideTextarea_display_hide(showHideDivprefix) {
    var ta = document.getElementById(showHideDivprefix + "_textarea");
    var sh = document.getElementById(showHideDivprefix + "_showhide");
    _showHideTextarea_display_hide(ta, sh)
    if (sh["_clickable"] == true) {
        sh.onclick = function(event) {
            showHideTextarea_display_show(showHideDivprefix);
        };
    }
}
function showHideTextarea_display_show(showHideDivprefix) {
    var ta = document.getElementById(showHideDivprefix + "_textarea");
    var sh = document.getElementById(showHideDivprefix + "_showhide");
    _showHideTextarea_display_show(ta, sh)
    if (sh["_clickable"] == true) {
        sh.onclick = function(event) {
            showHideTextarea_display_hide(showHideDivprefix);
        };
    }
}

function _showHideTextarea_display_hide(txtarea, showhide) {
    txtarea.classList.remove("showHideTextarea_displayed");
    txtarea.classList.add("showHideTextarea_hidden");
    showhide.classList.remove("showHideTextarea_show");
    showhide.classList.add("showHideTextarea_hide");
    showhide.innerHTML = showhide["showText"];
}
function _showHideTextarea_display_show(txtarea, showhide) {
    txtarea.classList.add("showHideTextarea_displayed");
    txtarea.classList.remove("showHideTextarea_hidden");
    showhide.classList.add("showHideTextarea_show");
    showhide.classList.remove("showHideTextarea_hide");
    showhide.innerHTML = showhide["hideText"];
}

/*
 * Create a hideable textarea, inside a div, with some text displayed in a
 * clickable area to show the text area, and some other text displayed in the
 * clickable area to hide the text area
 * 
 * Each showHideTextarea must have a unique "id_prefix" vale as an identifier
 * "content" is the text to be put into the text area "initialshow" is a
 * boolean, if true the textarea will be shown initially, otherwise hidden
 * "editable" is a boolean, if true the textarea will be editable, otherwise
 * hidden "disabled" is a boolean, if true the textarea can be shown, otherwise
 * it canot be shown "showText" is the text that is shown to be clicked to show
 * the text area "hideText" is the text that is shown to be clicked to show the
 * text area
 * 
 * Returns a div representing the hideable textarea, with id 'id_prefix'. The
 * returned div will a textarea called "id_prefix+'_textarea'", that can be
 * queried to get the value of the textarea
 */

function showHideTextarea(id_prefix, content, _initialshow, _editable, _disabled, _showText, _hideText) {
    var initialshow = (_initialshow != null ? _initialshow : false);
    var editable = (_editable != null ? _editable : true);
    var callback;

    var retdiv = document.createElement("div");
    var divname = id_prefix;
    retdiv.setAttribute("id", divname);
    retdiv.setAttribute("class", "showHideTextarea");
    var showhide = document.createElement("div");
    retdiv.appendChild(showhide);
    showhide.setAttribute("id", divname + "_showhide");
    showhide.innerHTML = '<label class="ebSwitcher"><input type="checkbox" class="ebSwitcher-checkbox" /><div class="ebSwitcher-body"><div class="ebSwitcher-onLabel">Show</div><div class="ebSwitcher-switch"></div><div class="ebSwitcher-offLabel">Hide</div></div></label>';
    var parent = document.createElement("div");
    var textArea = document.createElement("textarea");
    parent.appendChild(textArea);
    retdiv.appendChild(parent);
    textArea.setAttribute("id", divname + "_textarea");
    textArea.setAttribute("name", divname + "_textarea");
    textArea.style.width = "100%";
    textArea.style.height = "400px";
    textArea.style.display = "none";
    if (content != null) {
        textArea.value = content;
    }

    if (!editable) {
        textArea.readOnly = (!editable);
        textArea.style.cursor = "text";
        textArea.style["border-color"] = "#E3E3E3";
        textArea.style["background-color"] = "#F0F0F0";
        textArea.style.color = "#B2B2B2";
    }

    var shown = false;
    var checkbox = $(showhide).find('input:checkbox:first');
    checkbox.change(function(event) {
        if (!shown) {
            createEditArea(textArea.getAttribute("id"), {
                is_editable : editable
            });
            shown = true;
        } else {
            $(parent).toggle("fast");
        }
    });

    return retdiv;
};

