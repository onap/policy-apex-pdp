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

function editPolicyForm_createPolicy(formParent) {
    editPolicyForm_editPolicy_inner(formParent, null, "CREATE");
}

function editPolicyForm_deletePolicy(parent, name, version) {
    var message = "Are you sure you want to delete Policy \"" + name + ":" + version + "\"?";
    if (apexUtils_areYouSure(message)) {
        var requestURL = restRootURL + "/Policy/Delete?name=" + name + "&version=" + version;
        ajax_delete(requestURL, function(data) {
            apexUtils_removeElement("editPolicyFormDiv");
            policyTab_reset();
            keyInformationTab_reset()
        });
    }
}

function editPolicyForm_viewPolicy(formParent, name, version) {
    // get the policy
    var requestURL = restRootURL + "/Policy/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexPolicy", function(policy) {
        editPolicyForm_editPolicy_inner(formParent, policy, "VIEW");
    }, "policyKey");
}

function editPolicyForm_editPolicy(formParent, name, version) {
    // get the policy
    var requestURL = restRootURL + "/Policy/Get?name=" + name + "&version=" + version;
    ajax_getWithKeyInfo(requestURL, "apexPolicy", function(policy) {
        editPolicyForm_editPolicy_inner(formParent, policy, "EDIT");
    }, "policyKey");
}

function editPolicyForm_editPolicy_inner(formParent, policy, viewOrEdit) {
    // Get all contextSchemas too
    requestURL = restRootURL + "/ContextSchema/Get?name=&version=";
    var contextSchemas = new Array();
    ajax_get(requestURL, function(data2) {
        for (var i = 0; i < data2.messages.message.length; i++) {
            var contextSchema = JSON.parse(data2.messages.message[i]).apexContextSchema;
            contextSchemas.push({
                "name" : contextSchema.key.name,
                "version" : contextSchema.key.version,
                "displaytext" : contextSchema.key.name + ":" + contextSchema.key.version,
                "contextSchema" : contextSchema
            });
        }
        // Get all tasks
        requestURL = restRootURL + "/Task/Get?name=&version=";
        var tasks = new Array();
        ajax_get(requestURL, function(data3) {
            for (var j = 0; j < data3.messages.message.length; j++) {
                var task = JSON.parse(data3.messages.message[j]).apexTask;
                tasks.push({
                    "name" : task.key.name,
                    "version" : task.key.version,
                    "displaytext" : task.key.name + ":" + task.key.version,
                    "task" : task
                });
            }
            // Get all ContextAlbums
            requestURL = restRootURL + "/ContextAlbum/Get?name=&version=";
            var albums = new Array();
            ajax_get(requestURL, function(data4) {
                for (var k = 0; k < data4.messages.message.length; k++) {
                    var album = JSON.parse(data4.messages.message[k]).apexContextAlbum;
                    albums.push({
                        "name" : album.key.name,
                        "version" : album.key.version,
                        "displaytext" : album.key.name + ":" + album.key.version,
                        "album" : album
                    });
                }
                // Get all Events
                requestURL = restRootURL + "/Event/Get?name=&version=";
                var events = new Array();
                ajax_get(requestURL, function(data5) {
                    for (var m = 0; m < data5.messages.message.length; m++) {
                        var event = JSON.parse(data5.messages.message[m]).apexEvent;
                        events.push({
                            "name" : event.key.name,
                            "version" : event.key.version,
                            "displaytext" : event.key.name + ":" + event.key.version,
                            "event" : event
                        });
                    }
                    editPolicyForm_activate(formParent, viewOrEdit, policy, tasks, events, albums, contextSchemas);
                });
            });
        });
    });
}

function editPolicyForm_activate(parent, operation, policy, tasks, events, contextAlbums, contextItemSchemas) {
    apexUtils_removeElement("editPolicyFormDiv");
    var formParent = document.getElementById(parent);
    apexUtils_emptyElement(parent);

    var isedit = false;
    var createEditOrView = "";

    if (!operation) {
        console.warn("No operation specified for PolicyForm form")
    } else {
        createEditOrView = operation.toUpperCase();
    }

    if (createEditOrView == "CREATE") {
        isedit = true;
    } else if (createEditOrView == "EDIT" || createEditOrView == "VIEW") {
        if (createEditOrView == "EDIT") {
            isedit = true;
        }

        if (!policy) {
            console.warn("Invalid value (\"" + policy + "\") passed as a value for \"policy\" for PolicyForm form.");
        } else {
            if (!policy.policyKey || !policy.policyKey.name || policy.policyKey.name == "") {
                console.warn("Invalid value (\"" + policy.policyKey.name
                        + "\") passed as a value for \"name\" for PolicyForm form.");
            }
            if (!policy.policyKey || !policy.policyKey.version || policy.policyKey.version == "") {
                console.warn("Invalid value (\"" + policy.policyKey.version
                        + "\") passed as a value for \"version\" for PolicyForm form.");
            }
            if (!policy.uuid || policy.uuid == "") {
                console.warn("Invalid value (\"" + policy.uuid
                        + "\") passed as a value for \"uuid\" for PolicyForm form.");
            }
        }
    } else {
        console.warn("Invalid operation (\"" + operation
                + "\") specified for PolicyForm form. Only \"Create\", \"Edit\" and \"View\" operations are supported");
    }

    var contentelement = document.createElement("editPolicyFormDiv");
    var formDiv = document.createElement("div");
    contentelement.appendChild(formDiv);
    formDiv.setAttribute("id", "editPolicyFormDiv");
    formDiv.setAttribute("class", "editPolicyFormDiv");

    var headingSpan = document.createElement("h2");
    formDiv.appendChild(headingSpan);
    headingSpan.innerHTML = "Policy Editor";

    var form = document.createElement("editPolicyForm");
    formDiv.appendChild(form);

    form.setAttribute("id", "editPolicyForm");
    form.setAttribute("class", "form-style-1");
    form.setAttribute("method", "post");
    form.setAttribute("createEditOrView", createEditOrView);

    var formul = document.createElement("ul");
    form.appendChild(formul);

    // name
    var nameLI = document.createElement("li");
    formul.appendChild(nameLI);
    var nameLabel = document.createElement("label");
    nameLI.appendChild(nameLabel);
    nameLabel.setAttribute("for", "editPolicyFormNameInput");
    nameLabel.innerHTML = "Name: ";
    var nameLabelSpan = document.createElement("span");
    nameLabel.appendChild(nameLabelSpan);
    nameLabelSpan.setAttribute("class", "required");
    nameLabelSpan.innerHTML = "*";
    var nameInput = document.createElement("input");
    nameLI.appendChild(nameInput);
    nameInput.setAttribute("id", "editPolicyFormNameInput");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "editPolicyFormNameInput");
    nameInput.setAttribute("class", "field ebInput");
    nameInput.setAttribute("placeholder", "name");
    if (policy != null && policy.policyKey != null && policy.policyKey.name != null) {
        nameInput.value = policy.policyKey.name;
    }
    if (createEditOrView != "CREATE") {
        nameInput.readOnly = true;
    }

    // version
    var versionLI = document.createElement("li");
    formul.appendChild(versionLI);
    var versionLabel = document.createElement("label");
    versionLI.appendChild(versionLabel);
    versionLabel.setAttribute("for", "editPolicyFormVersionInput");
    versionLabel.innerHTML = "Version: ";
    var versionInput = document.createElement("input");
    versionLI.appendChild(versionInput);
    versionInput.setAttribute("id", "editPolicyFormVersionInput");
    versionInput.setAttribute("type", "text");
    versionInput.setAttribute("name", "editPolicyFormVersionInput");
    versionInput.setAttribute("class", "field ebInput");
    versionInput.setAttribute("placeholder", "0.0.1");
    if (policy != null && policy.policyKey != null && policy.policyKey.version != null) {
        versionInput.value = policy.policyKey.version;
    }
    if (createEditOrView != "CREATE") {
        versionInput.readOnly = true;
    }

    // uuid
    var uuidLI = document.createElement("li");
    formul.appendChild(uuidLI);
    var uuidLabel = document.createElement("label");
    uuidLI.appendChild(uuidLabel);
    uuidLabel.setAttribute("for", "editPolicyFormUuidInput");
    uuidLabel.innerHTML = "UUID: ";
    var uuidInput = document.createElement("input");
    uuidLI.appendChild(uuidInput);
    uuidInput.setAttribute("id", "editPolicyFormUuidInput");
    uuidInput.setAttribute("type", "text");
    uuidInput.setAttribute("name", "editPolicyFormUuidInput");
    uuidInput.setAttribute("class", "field-long ebInput ebInput_width_full");
    uuidInput.setAttribute("placeholder", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    if (policy != null && policy.uuid != null) {
        uuidInput.value = policy.uuid;
    }
    if (createEditOrView != "CREATE") {
        uuidInput.readOnly = true;
    }

    var edit_disabled = (createEditOrView != "CREATE" && createEditOrView != "EDIT");

    // description
    var descriptionLI = document.createElement("li");
    formul.appendChild(descriptionLI);
    var descriptionLabel = document.createElement("label");
    descriptionLI.appendChild(descriptionLabel);
    descriptionLabel.setAttribute("for", "editPolicyFormDescriptionTextArea");
    descriptionLabel.innerHTML = "Description: ";
    var descriptionTextArea = document.createElement("textarea");
    descriptionLI.appendChild(descriptionTextArea);
    descriptionTextArea.setAttribute("id", "editPolicyFormDescriptionTextArea");
    descriptionTextArea.setAttribute("name", "editPolicyFormDescriptionTextArea");
    descriptionTextArea.setAttribute("class", "field-long ebTextArea ebTextArea_width_full");
    if (policy != null && policy.description != null) {
        descriptionTextArea.value = policy.description;
    }
    descriptionTextArea.readOnly = edit_disabled;

    // template type
    var templateTypeLI = document.createElement("li");
    formul.appendChild(templateTypeLI);
    var templateTypeLabel = document.createElement("label");
    templateTypeLI.appendChild(templateTypeLabel);
    templateTypeLabel.setAttribute("for", "editEventFormTemplateTypeInput");
    templateTypeLabel.innerHTML = "Policy Type / Flavour: ";
    var templateTypeInput = document.createElement("input");
    templateTypeLI.appendChild(templateTypeInput);
    templateTypeInput.setAttribute("id", "editEventFormTemplateTypeInput");
    templateTypeInput.setAttribute("type", "text");
    templateTypeInput.setAttribute("name", "editEventFormTemplateTypeInput");
    templateTypeInput.setAttribute("class", "field-policyTemplateType ebInput");
    templateTypeInput.setAttribute("placeholder", "FREEFORM");
    if (policy != null && policy.template != null) {
        templateTypeInput.value = policy.template;
    } else {
        templateTypeInput.value = "FREEFORM";
    }
    if (createEditOrView != "CREATE" && createEditOrView != "EDIT") {
        templateTypeInput.readOnly = true;
    }

    // first state
    var firstStateLI = document.createElement("li");
    formul.appendChild(firstStateLI);
    var firstStateLabel = document.createElement("label");
    firstStateLI.appendChild(firstStateLabel);
    firstStateLabel.setAttribute("for", "editEventFormSelectFirstState_dropdownList");
    firstStateLabel.innerHTML = "First State: ";
    var firststateoptions = new Array();
    var firststateselected = null;
    if (policy != null && policy.state != null) {
        for (i = 0; i < policy.state.entry.length; i++) {
            if (policy.state.entry[i] != null && policy.state.entry[i].key != null) {
                var statename = policy.state.entry[i].key;
                firststateoptions.push({
                    "name" : statename,
                    "displaytext" : statename
                });
            }
        }
    }
    if (policy != null && policy.firstState != null && policy.firstState != "") {
        firststateselected = {
            "name" : policy.firstState,
            "displaytext" : policy.firstState
        }
    }
    var firstStateSelectDiv = dropdownList("editEventFormSelectFirstState", firststateoptions, firststateselected,
            (createEditOrView != "CREATE" && createEditOrView != "EDIT"), function() {
                return editPolicyForm_updateTriggerEventOptions(events);
            }, function() {
                return editPolicyForm_getStateOptions();
            });
    firstStateLI.appendChild(firstStateSelectDiv);

    // Trigger event
    var triggerLI = document.createElement("li");
    formul.appendChild(triggerLI);
    var triggerLabel = document.createElement("label");
    triggerLI.appendChild(triggerLabel);
    triggerLabel.setAttribute("for", "editEventFormSelectTrigger_dropdownList");
    triggerLabel.innerHTML = "Policy Trigger Event: ";
    var triggerevent = null;
    if (policy != null && policy.firstState != null && policy.firstState != "" && policy.state != null) {
        for (i = 0; i < policy.state.entry.length; i++) {
            if (policy.state.entry[i] != null && policy.state.entry[i].key != null) {
                var statename = policy.state.entry[i].key;
                var state = policy.state.entry[i].value;
                if (statename != null && statename == policy.firstState) {
                    triggerevent = {
                        "name" : state.trigger.name,
                        "version" : state.trigger.version,
                        "displaytext" : state.trigger.name + ":" + state.trigger.version
                    };
                }
            }
        }
    }
    // var triggerSelectDiv = dropdownList("editEventFormSelectTrigger", events,
    // triggerevent, true, null); // ewatkmi: replaced dropdown with label
    var triggerSelectDiv = document.createElement("label");
    triggerSelectDiv.setAttribute("class", "policy-trigger-event");
    triggerSelectDiv.setAttribute("id", "editEventFormSelectTrigger_dropdownList");
    triggerSelectDiv.innerHTML = triggerevent ? triggerevent.displaytext : "No Event Selected";
    triggerLI.appendChild(triggerSelectDiv);
    var triggerPeriodicEventCheckbox = document.createElement("input");
    triggerPeriodicEventCheckbox.setAttribute("type", "checkbox");
    triggerPeriodicEventCheckbox.setAttribute("id", "periodicEventsCheckbox");
    triggerPeriodicEventCheckbox.setAttribute("class", "field-checkbox-center periodic-events-checkbox");
    triggerPeriodicEventCheckbox.disabled = createEditOrView != "EDIT";
    triggerPeriodicEventCheckbox.onclick = function() {
        var firstState = document.getElementById("editEventFormSelectFirstState_dropdownList_display").innerHTML;
        var firstStateDropdown = document.getElementById("editPolicyFormTrigger_" + firstState + "_dropdownList");
        if ($(triggerPeriodicEventCheckbox).is(":checked")) {
            var periodicEvent = undefined;
            var tmpEvents = $.merge([], events);
            for ( var e in events) {
                if (events[e].name.indexOf("PeriodicEvent") !== -1) {
                    periodicEvent = events[e];
                    break;
                }
            }
            if (!periodicEvent) {
                periodicEvent = {
                    name : "PeriodicEvent",
                    version : "0.0.1",
                    displaytext : "PeriodicEvent:0.0.1",
                    event : {
                        description : "",
                        key : {
                            name : "PeriodicEvent",
                            version : "0.0.1"
                        },
                        nameSpace : "org.onap.policy.apex.domains.aadm.events",
                        parameter : {
                            entry : [ {
                                key : "PERIODIC_EVENT_COUNT",
                                value : {
                                    key : "PERIODIC_EVENT_COUNT",
                                    optional : false,
                                    fieldSchemaKey : {
                                        name : "PeriodicEventCount",
                                        version : "0.0.1"
                                    }
                                }
                            } ]
                        },
                        source : "System",
                        target : "Apex",
                        uuid : "44236da1-3d47-4988-8033-b6fee9d6a0f4"
                    },
                };
                tmpEvents.push(periodicEvent);
            }

            dropdownList_ChangeOptions(firstStateDropdown, tmpEvents, periodicEvent, false);
            editPolicyForm_updateTriggerEventOptions(tmpEvents);
        } else {
            dropdownList_ChangeOptions(firstStateDropdown, events, events[0], false);
            editPolicyForm_updateTriggerEventOptions(events);
        }
    }

    triggerLI.appendChild(triggerPeriodicEventCheckbox);

    triggerPeriodicEventLabel = document.createElement("label");
    triggerPeriodicEventLabel.setAttribute("class", "periodic-events-label");
    triggerPeriodicEventLabel.innerHTML = "is Periodic Event";
    triggerLI.appendChild(triggerPeriodicEventLabel);

    // states
    var statesLI = document.createElement("li");
    formul.appendChild(statesLI);
    var statesLabel = document.createElement("label");
    statesLI.appendChild(statesLabel);
    statesLabel.setAttribute("for", "editEventFormStates");
    statesLabel.innerHTML = "States: ";
    var statesUL = document.createElement("ul");
    statesUL.setAttribute("id", "editEventFormStates");
    statesLI.appendChild(statesUL);
    if (policy && policy.state) {
        var states = policy.state.entry;
        for ( var s in states) {
            var state = states[s];
            if (state.key == policy.firstState) {
                states.splice(s, 1);
                states.unshift(state);
                break;
            }
        }
        for (i = 0; i < policy.state.entry.length; i++) {
            stateEntry = policy.state.entry[i];
            var statename = stateEntry.key;
            var state = stateEntry.value;
            var stateLI = editPolicyForm_addState(statename, state, createEditOrView, policy, tasks, events,
                    contextAlbums, contextItemSchemas);
            statesUL.appendChild(stateLI);
        }
    }

    // add new state
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var newStateLI = document.createElement("li");
        statesUL.appendChild(newStateLI);
        var newStateLabel = document.createElement("label");
        newStateLI.appendChild(newStateLabel);
        newStateLabel.setAttribute("for", "editPolicyFormStateDiv_");
        newStateLabel.innerHTML = "Add a new State: ";
        var newStDiv = document.createElement("div");
        newStateLI.appendChild(newStDiv);
        newStDiv.setAttribute("id", "editPolicyFormStateDiv_");
        newStDiv.setAttribute("class", "editPolicyFormStateDiv");
        var newStateInput = document.createElement("input");
        newStDiv.appendChild(newStateInput);
        newStateInput.setAttribute("id", "editEventFormNewStateInput");
        newStateInput.setAttribute("type", "text");
        newStateInput.setAttribute("name", "editEventFormTemplateTypeInput");
        newStateInput.setAttribute("class", "field-policyTemplateType ebInput ebInput_width_xLong");
        newStateInput.setAttribute("placeholder", "Name for new State ... ");
        var addState = document.createElement("button");
        newStDiv.appendChild(addState);
        addState.setAttribute("id", "addStateButton");
        addState.setAttribute("class", "ebBtn");
        addState.setAttribute("type", "submit");
        addState.setAttribute("value", "Add a new State");
        addState.style["margin-left"] = "10px";
        addState.onclick = function() {
            return editPolicyForm_addNewState(statesUL, createEditOrView, policy, tasks, events, contextAlbums,
                    contextItemSchemas);
        };
        addState.innerHTML = addState.getAttribute("value");
    }

    // buttons
    var inputLI = document.createElement("li");
    formul.appendChild(inputLI);
    if (createEditOrView == "CREATE") {
        var generateUUIDInput = document.createElement("button");
        inputLI.appendChild(generateUUIDInput);
        generateUUIDInput.setAttribute("id", "generateUUID");
        generateUUIDInput.setAttribute("class", "ebBtn ebBtn_large");
        generateUUIDInput.setAttribute("type", "submit");
        generateUUIDInput.setAttribute("value", "Generate UUID");
        generateUUIDInput.onclick = editPolicyForm_generateUUIDPressed;
        generateUUIDInput.innerHTML = generateUUIDInput.getAttribute("value");

        var inputSpan0 = document.createElement("span");
        inputLI.appendChild(inputSpan0);
        inputSpan0.setAttribute("class", "required");
        inputSpan0.innerHTML = " ";

        var generateDescriptionInput = document.createElement("button");
        inputLI.appendChild(generateDescriptionInput);
        generateDescriptionInput.setAttribute("id", "generateDescription");
        generateDescriptionInput.setAttribute("class", "ebBtn ebBtn_large");
        generateDescriptionInput.setAttribute("type", "submit");
        generateDescriptionInput.setAttribute("value", "Generate Description");
        generateDescriptionInput.onclick = editPolicyForm_generateDescriptionPressed;
        generateDescriptionInput.innerHTML = generateDescriptionInput.getAttribute("value");

        var inputSpan1 = document.createElement("span");
        inputLI.appendChild(inputSpan1);
        inputSpan1.setAttribute("class", "required");
        inputSpan1.innerHTML = " ";
    }

    var cancelInput = document.createElement("button");
    inputLI.appendChild(cancelInput);
    cancelInput.setAttribute("id", "cancel");
    cancelInput.setAttribute("class", "ebBtn ebBtn_large");
    cancelInput.setAttribute("type", "submit");
    cancelInput.setAttribute("value", "Cancel");
    cancelInput.onclick = editPolicyForm_cancelPressed;
    cancelInput.innerHTML = cancelInput.getAttribute("value");

    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        var inputSpan2 = document.createElement("span");
        inputLI.appendChild(inputSpan2);
        inputSpan2.setAttribute("class", "required");
        inputSpan2.innerHTML = " ";
        var submitInput = document.createElement("button");
        inputLI.appendChild(submitInput);
        submitInput.setAttribute("id", "submit");
        submitInput.setAttribute("class", "ebBtn ebBtn_large");
        submitInput.setAttribute("type", "submit");
        submitInput.setAttribute("value", "Submit");
        submitInput.onclick = editPolicyForm_submitPressed;
        submitInput.innerHTML = submitInput.getAttribute("value");
    }

    formParent.appendChild(contentelement);
    scrollToTop();
}

function editPolicyForm_addNewState(statesUL, createEditOrView, policy, tasks, events, contextAlbums,
        contextItemSchemas) {
    var statename = document.getElementById("editEventFormNewStateInput").value;
    if (statename == null || statename == "") {
        alert("Please enter a value for the name of the new state\"" + paramname + "\"");
        document.getElementById("editEventFormNewStateInput").focus();
        return false;
    } else if (statename.toUpperCase() == "NULL" || statename.toUpperCase() == "NONE") {
        alert("Please enter a valid value for the name of the new state\"" + paramname
                + "\". Values \"NULL\" and \"None\" are not allowed");
        document.getElementById("editEventFormNewStateInput").focus();
        return false;
    } else {
        document.getElementById("editEventFormNewStateInput").value = "";
    }
    if (policy && policy.state) {
        for (i = 0; i < policy.state.entry.length; i++) {
            if (statename.toUpperCase() == policy.state.entry[i].key.toUpperCase()) {
                alert("Policy " + policy.policyKey.name + ":" + policy.policyKey.version
                        + " already contains a state called \"" + statename + "\".");
                document.getElementById("editEventFormNewStateInput").focus();
                return false;
            }
        }
    }
    var stateLI = editPolicyForm_addState(statename, null, createEditOrView, policy, tasks, events, contextAlbums,
            contextItemSchemas);
    statesUL.insertBefore(stateLI, statesUL.lastElementChild);
    editPolicyForm_updateTriggerEventOptions(events);
}

function editPolicyForm_getStateOptions() {
    var stateoptions = new Array();
    var stateslis = document.getElementById("editEventFormStates").querySelectorAll(
            "#editEventFormStates > li[stateName]"); // get li direct child
                                                        // elements with an
                                                        // attribute "stateName"
    for (var i = 0; i < stateslis.length; i++) {
        if (stateslis != null && stateslis[i] != null && stateslis[i].getAttribute("stateName") != null) {
            stateoptions.push({
                "name" : stateslis[i].getAttribute("stateName"),
                "displaytext" : stateslis[i].getAttribute("stateName")
            });
        }
    }
    return stateoptions;
}

function editPolicyForm_getNextStateOptions() {
    var stateoptions = editPolicyForm_getStateOptions();
    stateoptions.push({
        "name" : "NULL",
        "displaytext" : "None"
    });
    return stateoptions;
}

function editPolicyForm_updateTriggerEventOptions(events) {
    var stateevent = null;
    var triggerSelectDiv = document.getElementById("editEventFormSelectTrigger_dropdownList");
    var firstStateSelectDiv = document.getElementById("editEventFormSelectFirstState_dropdownList");
    var firststate = firstStateSelectDiv.selectedOption;
    var createEditOrView = document.getElementById("editPolicyForm").getAttribute("createEditOrView");
    if (firststate != null && firststate.name != null) {
        var statename = firststate.name;
        var stateeventselect = document.getElementById("editPolicyFormTrigger_" + statename + "_dropdownList");
        if (stateeventselect != null && stateeventselect.selectedOption) {
            stateevent = stateeventselect.selectedOption;
        }
        if (createEditOrView == "CREATE") {
            var periodicCheckbox = document.getElementById("periodicEventsCheckbox");
            if (periodicCheckbox.hasAttribute("disabled")) {
                periodicCheckbox.removeAttribute("disabled");
            }
        }
    } else {
        var triggerSelectDiv = document.getElementById("editEventFormSelectTrigger_dropdownList");
        triggerSelectDiv.innerHTML = "No Event Selected";
        var periodicEventsCheckbox = $("#periodicEventsCheckbox");
        if (periodicEventsCheckbox.is(":checked")) {
            periodicEventsCheckbox.attr("checked", false);
        }
        if (createEditOrView == "CREATE") {
            var periodicCheckbox = document.getElementById("periodicEventsCheckbox");
            if (!periodicCheckbox.hasAttribute("disabled")) {
                periodicCheckbox.disabled = true;
            }
        }
    }
    if (stateevent) {
        triggerSelectDiv.innerHTML = stateevent.displaytext;
        if (stateevent.displaytext.indexOf("PeriodicEvent") == -1) {
            var periodicEventsCheckbox = $("#periodicEventsCheckbox");
            if (periodicEventsCheckbox.is(":checked")) {
                periodicEventsCheckbox.attr("checked", false);
            }
        }
    }
}

function editPolicyForm_addState(statename, state, createEditOrView, policy, tasks, events, contextAlbums,
        contextItemSchemas) {
    var stateLI = document.createElement("li");
    stateLI.setAttribute("stateName", statename);
    var deleteDiv = document.createElement("div");
    if (createEditOrView == "CREATE" || createEditOrView == "EDIT") {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete");
        deleteDiv.onclick = function(event) {
            $(stateLI)
                    .hide(
                            "fast",
                            function() {
                                stateLI.parentNode.removeChild(stateLI);
                                var firstState = document
                                        .getElementById("editEventFormSelectFirstState_dropdownList_display").innerHTML;
                                var selected = (statename !== firstState) ? {
                                    name : firstState,
                                    displaytext : firstState
                                } : undefined;
                                dropdownList_ChangeOptions(document
                                        .getElementById("editEventFormSelectFirstState_dropdownList"),
                                        editPolicyForm_getStateOptions(), selected, false,
                                        editPolicyForm_getStateOptions);
                                editPolicyForm_updateTriggerEventOptions();
                            });
        }
    } else {
        deleteDiv.setAttribute("class", "ebIcon ebIcon_interactive ebIcon_delete ebIcon_disabled");
    }
    deleteDiv.style["vertical-align"] = "baseline";
    stateLI.appendChild(deleteDiv);
    var stateLabel = document.createElement("label");
    stateLabel.style.display = "inline-block";
    stateLabel.style["min-width"] = "120px";
    stateLabel.style["padding-left"] = "10px";
    stateLabel.style["vertical-align"] = "middle";
    stateLabel.innerHTML = statename;
    stateLI.appendChild(stateLabel);
    var stateDiv = editPolicyForm_State_generateStateDiv(createEditOrView, policy, statename, state, tasks, events,
            contextAlbums, contextItemSchemas);
    var showhideDIV = showHideElement("editEventFormStates_" + statename, stateDiv, true, "Show " + statename + " ...",
            "Hide " + statename + " ...", "showhide_div_show", "showhide_div_hide", "showhide_button_show",
            "showhide_button_hide");
    showhideDIV.style.display = "inline-block";
    showhideDIV.style["vertical-align"] = "middle";
    stateLabel.setAttribute("for", stateDiv.id);
    stateLI.appendChild(showhideDIV);
    stateLI.appendChild(stateDiv);
    return stateLI;
}

function editPolicyForm_generateUUIDPressed() {
    document.getElementById("editPolicyFormUuidInput").value = formUtils_generateUUID();
}

function editPolicyForm_generateDescriptionPressed() {
    document.getElementById("editPolicyFormDescriptionTextArea").value = formUtils_generateDescription(document
            .getElementById("editPolicyFormNameInput").value,
            document.getElementById("editPolicyFormVersionInput").value, document
                    .getElementById("editPolicyFormUuidInput").value);
}

function editPolicyForm_cancelPressed() {
    apexUtils_removeElement("editPolicyFormDiv");
    policyTab_reset();
}

function editPolicyForm_submitPressed() {
    var createEditOrView = document.getElementById("editPolicyForm").getAttribute("createEditOrView");
    if (!createEditOrView || createEditOrView == "" || (createEditOrView != "CREATE" && createEditOrView != "EDIT")) {
        console.error("Invalid operation \"" + createEditOrView
                + "\" passed to editPolicyForm_submitPressed function. Edit failed");
        apexUtils_removeElement("editPolicyFormDiv");
        policyTab_reset();
        return;
    }

    var policybean = editPolicyForm_getPolicyBean();
    if (policybean == null) {
        return false;
    }
    var jsonString = JSON.stringify(policybean);

    if (createEditOrView == "CREATE") {
        var requestURL = restRootURL + "/Policy/Create";
        ajax_post(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editPolicyFormDiv");
            policyTab_reset();
            keyInformationTab_reset();
        });
    } else if (createEditOrView == "EDIT") {
        var firstStatePeriodic = $("#periodicEventsCheckbox").is(":checked")
        var requestURL = restRootURL + "/Policy/Update?firstStatePeriodic=" + firstStatePeriodic;
        ajax_put(requestURL, jsonString, function(resultData) {
            apexUtils_removeElement("editPolicyFormDiv");
            policyTab_reset();
            keyInformationTab_reset();
        });
    }

}

function editPolicyForm_getPolicyBean() {

    var name = document.getElementById('editPolicyFormNameInput').value;
    if (name == null || name == "") {
        console.error("No Name is specified for the policy");
        alert("No Name is specified for the policy");
        return null;
    }
    var version = document.getElementById('editPolicyFormVersionInput').value;
    if (version == null || version == "") {
        console.error("No Version is specified for the policy");
        alert("No Version is specified for the policy");
        return null;
    }
    var uuid = document.getElementById('editPolicyFormUuidInput').value;
    if (uuid == null || uuid == "") {
        console.error("No UUID is specified for the policy");
        alert("No UUID is specified for the policy");
        return null;
    }
    var desc = document.getElementById('editPolicyFormDescriptionTextArea').value;
    if (desc == null) {
        desc = "";
    }
    var template = document.getElementById('editEventFormTemplateTypeInput').value;
    if (template == null) {
        template = "";
    }
    var firststateselectedoption = document.getElementById("editEventFormSelectFirstState" + "_dropdownList").selectedOption;
    if (firststateselectedoption == null) {
        console.error("Please select an option for First State");
        alert("Please select an option for First State");
        return null;
    }
    var states = new Object();
    var stateslis = document.getElementById("editEventFormStates").querySelectorAll(
            "#editEventFormStates > li[stateName]"); // get li direct child
                                                        // elements with an
                                                        // attribute "stateName"
    for (var i = 0; i < stateslis.length; i++) { // ignore last li ... it has
                                                    // the new state button etc.
        if (stateslis != null && stateslis[i] != null && stateslis[i].getAttribute("stateName") != null) {
            var statename = stateslis[i].getAttribute("stateName");
            var state = editPolicyForm_State_getStateBean(statename);
            if (state == null) {
                return null;
            }
            states[statename] = state;
        }
    }

    var policybean = {
        "name" : name,
        "version" : version,
        "uuid" : uuid,
        "description" : desc,
        "template" : template,
        "firstState" : firststateselectedoption.name,
        "states" : states
    };
    return policybean;
}
