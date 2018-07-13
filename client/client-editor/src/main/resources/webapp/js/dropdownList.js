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

function dropdownList_option_select_scopePreserver(optionDiv, selectedOption, onselect) {
    return function(event) {
        dropdownList_option_select(optionDiv, selectedOption, onselect);
    }
}
function dropdownList_filter_scopePreserver(optionDiv, optionUl) {
    return function(event) {
        dropdownList_filter(optionDiv, optionUl);
    }
}
function dropdownList_option_select(divname, selectedOption, onselect) {
    document.getElementById(divname + "_display").innerHTML = selectedOption.displaytext;
    document.getElementById(divname)["selectedOption"] = selectedOption;
    dropdownList_display_hide(divname);
    if (onselect) {
        onselect(selectedOption);
    }
}
function dropdownList_display_click(divname, options, selected, disabled, getoptions) {
    if (!document.getElementById(divname + "_options").classList.contains("dropdownList_show")) {
        if (getoptions != null) {
            var new_options = getoptions();
            dropdownList_ChangeOptions(document.getElementById(divname), new_options, selected, disabled, getoptions);
        }
        document.getElementById(divname + "_options").classList
                .add("dropdownList_show", "dropdownList_display_clicked");
    } else {
        document.getElementById(divname + "_options").classList.remove("dropdownList_show",
                "dropdownList_display_clicked");
    }
}
function dropdownList_display_hide(optionDiv) {
    document.getElementById(optionDiv + "_options").classList.remove("dropdownList_show");
}
function dropdownList_filter(optionDiv, optionUl) {
    var input, filter, ul;
    var input = document.getElementById(optionDiv + "_search");
    var filter = input.value.toUpperCase();
    var ul = document.getElementById(optionDiv + "_options_list_ul");
    var lis = ul.querySelectorAll("#" + ul.id + " > li"); // get li direct
                                                            // child elements
    for (var i = 0; i < lis.length; i++) {
        if (lis[i].innerHTML.toUpperCase().indexOf(filter) > -1) {
            lis[i].style.display = "";
        } else {
            lis[i].style.display = "none";
        }
    }
}

/*
 * Create a dropdown list, with a search function.
 * 
 * Each dropdownList must have a unique "id_prefix" value as a unique identifier
 * prefix "options" is an array of objects for options, where each object has a
 * field called name. options[i].displaytext is a displayed text for the option
 * "selected" is one of the options contained in list in the "options"
 * parameter. "selected" must contain at least one field: selected.displaytext,
 * where the value of selected.displaytext should be the same as
 * options[i].displaytext for one of the values in options. "disabled" is a
 * boolean, whether the drop down is enabled or disabled "onselect" is a
 * function that is called when an option is selected, with a parameter that is
 * the selected option from the passed "options" "getoptions" is a function that
 * is called to dynamically retrieve options. It is called when the drop-down
 * list is checked. If this is set the "options" parameter is ignored.
 * 
 * Returns a DIV representing the dropdown list. The returned DIV will have a
 * new field "selectedOption" containing one of the options in the "options"
 * array, or null if no option is selected/ The returned DIV will have an id
 * with value "id_prefix+'_dropdownList'", which should be unique.
 */
function dropdownList(id_prefix, options, selected, disabled, onselect, getoptions) {
    var retdiv = document.createElement("div");
    var divname = id_prefix + "_dropdownList";
    retdiv.setAttribute("id", divname);
    retdiv.setAttribute("class", "dropdownList");
    retdiv["_isDropDownList"] = true;
    retdiv["_dropDownList_listener"] = onselect;
    return dropdownList_ChangeOptions(retdiv, options, selected, disabled, getoptions);
}

function dropdownList_ChangeOptions(dropdownListDIV, options, selected, disabled, getoptions) {
    var retdiv = dropdownListDIV;
    var divname = retdiv.getAttribute("id");
    if (!retdiv["_isDropDownList"]) {
        console.error("Cannot provision dropdown list " + divname + " DIV because it is not a dropdown list");
        return null;
    }
    if (options == null && getoptions == null) {
        console.error("Cannot provision dropdown list " + divname
                + " DIV because it has no options and no function to get options");
        return null;
    }
    if (disabled && !selected) {
        console.warn("Provisioning dropdown list " + divname + " that is disabled, but there is no value selected!")
    }
    var subdivs = retdiv.querySelectorAll("#" + divname + " > div, button"); // get
                                                                                // direct
                                                                                // children
                                                                                // or
                                                                                // retdiv
                                                                                // that
                                                                                // are
                                                                                // divs
    for (var d = 0; d < subdivs.length; d++) {
        retdiv.removeChild(subdivs[d]);
    }
    var onselect = null;
    if (retdiv["_dropDownList_listener"]) {
        onselect = retdiv["_dropDownList_listener"];
    }
    var display = document.createElement("div");
    retdiv.appendChild(display);
    display.setAttribute("id", divname + "_display");
    retdiv["selectedOption"] = null;

    var button = document.createElement("button");
    button.setAttribute("class", "ebCombobox-helper");
    var iconHolder = document.createElement("span");
    iconHolder.setAttribute("class", "ebCombobox-iconHolder");
    var icon = document.createElement("i");
    var iconStyle = "ebIcon ebIcon_small ebIcon_downArrow_10px eb_noVertAlign";
    if (disabled) {
        iconStyle += " ebIcon_disabled";
    }
    icon.setAttribute("class", iconStyle);
    iconHolder.appendChild(icon);
    button.appendChild(iconHolder);
    retdiv.appendChild(button);

    if (disabled) {
        display.setAttribute("class", "dropdownList_display_disabled ebInput_width_xLong");
    } else {
        display.setAttribute("class", "dropdownList_display ebInput_width_xLong");
        var onClickFunction = function(event) {
            dropdownList_display_click(divname, options, selected, disabled, getoptions);
        };
        display.onclick = onClickFunction;
        button.onclick = onClickFunction;
    }
    var optionsDIV = document.createElement("div");
    retdiv.appendChild(optionsDIV);
    optionsDIV.setAttribute("id", divname + "_options");
    optionsDIV.setAttribute("class", "dropdownList_options");
    var optionsSearch = document.createElement("input");
    optionsDIV.appendChild(optionsSearch);
    optionsSearch.setAttribute("id", divname + "_search");
    optionsSearch.setAttribute("type", "input");
    optionsSearch.setAttribute("placeholder", "Search..");
    optionsSearch.setAttribute("class", "ebInput ebInput_width_full");
    optionsSearch.onkeyup = dropdownList_filter_scopePreserver(divname, divname + "_options_list");
    var optionsUL = document.createElement("ul");
    optionsUL.setAttribute("class", "dropdownList_options_body");
    optionsSearch.setAttribute("id", divname + "_search");
    optionsDIV.appendChild(optionsUL);
    optionsUL.setAttribute("id", divname + "_options_list_ul");
    if (options) {
        for (var i = 0; i < options.length; i++) {
            var option = document.createElement("li");
            optionsUL.appendChild(option);
            option.onclick = dropdownList_option_select_scopePreserver(divname, options[i], onselect);
            option.innerHTML = options[i].displaytext;
            if (selected && selected.displaytext && selected.displaytext == options[i].displaytext) {
                retdiv["selectedOption"] = options[i];
            }
        }
    } else if (getoptions != null && selected != null) {
        retdiv["selectedOption"] = selected;
    }

    if (retdiv["selectedOption"] != null) {
        display.innerHTML = retdiv["selectedOption"].displaytext;
        display.title = display.innerHTML;
    } else if (retdiv["selectedOption"] == null && !disabled) {
        display.innerHTML = "Select an Option";
    } else if (retdiv["selectedOption"] == null && disabled) {
        display.innerHTML = "No Option Selected";
    }
    return retdiv;
}
