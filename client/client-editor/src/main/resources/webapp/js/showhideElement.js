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

function showHideElement_display(showhide, element, _showstyle, _hidestyle, _buttonshowstyle, _buttonhidestyle) {
    var checkbox = $(showhide).find('input:checkbox:first');
    if (checkbox) {
        checkbox.change(function(event) {
            $(element).toggle("fast");
        });
    } else {
        if (_buttonshowstyle) {
            showhide.classList.remove(_buttonshowstyle);
        }
        if (_buttonhidestyle) {
            showhide.classList.add(_buttonhidestyle);
        }
        showhide.onclick = function(event) {
            $(element).toggle("fast");
        };
    }
}

function showHideElement(id_prefix, element, _initialhide, _showText, _hideText, _showstyle, _hidestyle,
        _buttonshowstyle, _buttonhidestyle) {
    var retdiv = document.createElement("div");
    var divname = id_prefix;
    retdiv.setAttribute("id", divname);
    retdiv.setAttribute("class", "showHideElement");
    var showhide = document.createElement("div");
    retdiv.appendChild(showhide);
    showhide.setAttribute("id", divname + "_showhide");
    showhide.innerHTML = '<label class="ebSwitcher"><input type="checkbox" class="ebSwitcher-checkbox" /><div class="ebSwitcher-body"><div class="ebSwitcher-onLabel">Show</div><div class="ebSwitcher-switch"></div><div class="ebSwitcher-offLabel">Hide</div></div></label>';

    retdiv.appendChild(element);
    if (_initialhide != null && _initialhide === true) {
        element.style.display = "none";
    } else {
        element.style.display = "block";
    }
    showHideElement_display(showhide, element, _showstyle, _hidestyle, undefined, undefined);
    return retdiv;
};

