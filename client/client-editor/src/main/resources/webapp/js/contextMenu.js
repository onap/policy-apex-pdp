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

//hide the context menu, if it's shown, anytime the user clicks anywhere. 
$(document).on("click", function(event) {
    if (document.getElementById("rightClickMenu"))
        document.getElementById("rightClickMenu").className = "ebContextMenu-body_visible_false";
});

// Hack: need to maintain each passed parameter in a way it can be referenced by
// the onContextMenu handler function for
// each iteration inside a loop, after the passed parameter goes out of scope
// and changes.
// see
// http://stackoverflow.com/questions/750486/javascript-closure-inside-loops-simple-practical-example
function rightClickMenu_scopePreserver(parent, type, name, version) {
    return function(clickevent) {
        rightClickMenu(clickevent, parent, type, name, version);
    }
}

// Context menu when user right-clicks inside one of the display tabs
function rightClickMenu(event, parent, type, name, version) {
    var rclickdiv = document.getElementById('rightClickMenu');
    if (rclickdiv == null) {
        rclickdiv = document.createElement("div");
        document.body.appendChild(rclickdiv);
    }
    // clear the div
    apexUtils_emptyElement('rightClickMenu');
    rclickdiv.setAttribute("id", "rightClickMenu");
    rclickdiv.setAttribute("title", type + " menu");
    rclickdiv.addEventListener("contextmenu", function(e) {
        e.preventDefault();
    }, false);

    var ul = document.createElement("div");
    ul.setAttribute("class", "ebComponentList")
    rclickdiv.appendChild(ul);

    var li1 = document.createElement("div");
    li1.setAttribute("class", "ebComponentList-item")
    li1.onclick = function() {
        return rightClickMenuCreate(parent, type);
    };
    li1.innerHTML = "Create new <type>" + type + "<type>";
    if (type.toUpperCase() == "KEYINFORMATION") {
        li1.setAttribute("class", "ebComponentList-item ebComponentList-item_disabled");
    }
    ul.appendChild(li1);

    if (name) {
        var value = name + ":" + version;

        var li4 = document.createElement("div");
        li4.setAttribute("class", "ebComponentList-item")
        li4.onclick = function() {
            return rightClickMenuView(parent, type, name, version);
        };
        li4.innerHTML = "View <type>" + type + "</type> <value>" + value + "<value>";
        ul.appendChild(li4);

        var li2 = document.createElement("div");
        li2.setAttribute("class", "ebComponentList-item")
        li2.onclick = function() {
            return rightClickMenuEdit(parent, type, name, version);
        };
        li2.innerHTML = "Edit <type>" + type + "</type> <value>" + value + "<value>";
        ul.appendChild(li2);

        var li3 = document.createElement("div");
        li3.setAttribute("class", "ebComponentList-item")
        li3.onclick = function() {
            return rightClickMenuDelete(parent, type, name, version);
        };
        li3.innerHTML = "Delete <type>" + type + "</type> <value>" + value + "<value>";
        ul.appendChild(li3);
        if (type.toUpperCase() == "KEYINFORMATION") {
            li3.setAttribute("class", "ebComponentList-item ebComponentList-item_disabled");
        }
    }

    // rclickdiv.setAttribute("class", "contextmenu_show"); ewatkmi: swapped
    // with ebContextMenu
    rclickdiv.setAttribute("class",
            "ebContextMenu-body ebContextMenu-body_corner_default ebContextMenu-body_visible_true");
    rclickdiv.style.position = "absolute";
    rclickdiv.style.top = mouseY(event);
    rclickdiv.style.left = mouseX(event);

    // prevent event bubbling up to parent elements for their on-click
    event.stopPropagation();
    // prevent default context menu
    event.returnValue = false;
    event.preventDefault();
    return false;
}
function rightClickMenuCreate(parent, type) {
    document.getElementById("rightClickMenu").className = "ebContextMenu-body_visible_false";
    if (type.toUpperCase() == "CONTEXTSCHEMA") {
        editContextSchemaForm_createContextSchema(parent);
    } else if (type.toUpperCase() == "EVENT") {
        editEventForm_createEvent(parent);
    } else if (type.toUpperCase() == "TASK") {
        editTaskForm_createTask(parent);
    } else if (type.toUpperCase() == "POLICY") {
        editPolicyForm_createPolicy(parent);
    } else if (type.toUpperCase() == "CONTEXTALBUM") {
        editContextAlbumForm_createContextAlbum(parent);
    } else {
        alert("So you want to create a new " + type);
    }
}
function rightClickMenuView(parent, type, value_name, value_version) {
    document.getElementById("rightClickMenu").className = "ebContextMenu-body_visible_false";
    if (type.toUpperCase() == "CONTEXTSCHEMA") {
        editContextSchemaForm_viewContextSchema(parent, value_name, value_version);
    } else if (type.toUpperCase() == "EVENT") {
        editEventForm_viewEvent(parent, value_name, value_version);
    } else if (type.toUpperCase() == "TASK") {
        editTaskForm_viewTask(parent, value_name, value_version);
    } else if (type.toUpperCase() == "POLICY") {
        editPolicyForm_viewPolicy(parent, value_name, value_version);
    } else if (type.toUpperCase() == "CONTEXTALBUM") {
        editContextAlbumForm_viewContextAlbum(parent, value_name, value_version);
    } else {
        alert("So you want to view " + type + " " + value_name + ":" + value_version);
    }
}
function rightClickMenuEdit(parent, type, value_name, value_version) {
    document.getElementById("rightClickMenu").className = "ebContextMenu-body_visible_false";
    if (type.toUpperCase() == "CONTEXTSCHEMA") {
        editContextSchemaForm_editContextSchema(parent, value_name, value_version);
    } else if (type.toUpperCase() == "EVENT") {
        editEventForm_editEvent(parent, value_name, value_version);
    } else if (type.toUpperCase() == "TASK") {
        editTaskForm_editTask(parent, value_name, value_version);
    } else if (type.toUpperCase() == "POLICY") {
        editPolicyForm_editPolicy(parent, value_name, value_version);
    } else if (type.toUpperCase() == "CONTEXTALBUM") {
        editContextAlbumForm_editContextAlbum(parent, value_name, value_version);
    } else {
        alert("So you want to edit " + type + " " + value_name + ":" + value_version);
    }
}
function rightClickMenuDelete(parent, type, value_name, value_version) {
    document.getElementById("rightClickMenu").className = "ebContextMenu-body_visible_false";
    if (type.toUpperCase() == "CONTEXTSCHEMA") {
        editContextSchemaForm_deleteContextSchema(parent, value_name, value_version);
    } else if (type.toUpperCase() == "EVENT") {
        editEventForm_deleteEvent(parent, value_name, value_version);
    } else if (type.toUpperCase() == "TASK") {
        editTaskForm_deleteTask(parent, value_name, value_version);
    } else if (type.toUpperCase() == "POLICY") {
        editPolicyForm_deletePolicy(parent, value_name, value_version);
    } else if (type.toUpperCase() == "CONTEXTALBUM") {
        editContextAlbumForm_deleteContextAlbum(parent, value_name, value_version);
    } else {
        alert("So you want to delete " + type + " " + value_name + ":" + value_version);
    }
}
function mouseX(evt) {
    if (evt.pageX) {
        return evt.pageX;
    } else if (evt.clientX) {
        return evt.clientX
                + (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft);
    } else {
        return null;
    }
}
function mouseY(evt) {
    if (evt.pageY) {
        return evt.pageY;
    } else if (evt.clientY) {
        return evt.clientY
                + (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop);
    } else {
        return null;
    }
}
