#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2019 Huawei. All rights reserved.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#-------------------------------------------------------------------------------

#========> create Policy model 
model create name=NomadicONTPolicyModel version=1.0.0


#========> Define Schema and Data types 
schema create name=SimpleStringType         flavour=Java schema=java.lang.String
schema create name=SimpleLongType           flavour=Java schema=java.lang.Long
schema create name=SimpleBooleanType        flavour=Java schema=java.lang.Boolean
schema create name=UUIDType                 flavour=Java schema=java.util.UUID

schema create name=VirtualControlLoopEventType flavour=Java schema=org.onap.policy.controlloop.VirtualControlLoopEvent

schema create name=NomadicONTContextType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/NomadicONTContextType.avsc"
LE

#=======================> Create Albums
album create name=NomadicONTContextAlbum scope=policy writable=true schemaName=NomadicONTContextType


#===================================> Define Events
#=============>Nomadic ONT
event create name=Nomadic_ONT version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=DCAE target=APEX
event parameter create name=Nomadic_ONT parName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

event create name=AAI_SERVICE_ASSIGNED version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=APEX
event parameter create name=AAI_SERVICE_ASSIGNED parName=requestID schemaName=UUIDType
event parameter create name=AAI_SERVICE_ASSIGNED parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=AAI_SERVICE_ASSIGNED parName=serviceInstanceId   schemaName=SimpleStringType optional=true

event create name=SDNC_RESOURCE_UPDATE version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=APEX
event parameter create name=SDNC_RESOURCE_UPDATE parName=requestID schemaName=UUIDType
event parameter create name=SDNC_RESOURCE_UPDATE parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=SDNC_RESOURCE_UPDATE parName=serviceInstanceId   schemaName=SimpleStringType optional=true

event create name=AAI_SERVICE_CREATE version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=APEX
event parameter create name=AAI_SERVICE_CREATE parName=requestID schemaName=UUIDType
event parameter create name=AAI_SERVICE_CREATE parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=AAI_SERVICE_CREATE parName=serviceInstanceId   schemaName=SimpleStringType optional=true

event create name=ERROR_LOG version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=APEX
event parameter create name=ERROR_LOG parName=errorLogParam schemaName=SimpleStringType

event create name=END_CONTROL_LOOP version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=APEX
event parameter create name=END_CONTROL_LOOP parName=result schemaName=SimpleStringType
event parameter create name=END_CONTROL_LOOP parName=requestID schemaName=UUIDType
event parameter create name=END_CONTROL_LOOP parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=END_CONTROL_LOOP parName=serviceInstanceId   schemaName=SimpleStringType optional=true


event create name=END_EVENT version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=DCAE
event parameter create name=END_EVENT parName=result schemaName=SimpleStringType optional=true
event parameter create name=END_EVENT parName=requestID schemaName=UUIDType
event parameter create name=END_EVENT parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=END_EVENT parName=serviceInstanceId   schemaName=SimpleStringType optional=true

#=============>CPE Authentication
event create name=CPE_Authentication version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=DCAE target=APEX
event parameter create name=CPE_Authentication parName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

event create name=AAI_CPE_AUTH_UPDATE version=1.0.0 nameSpace=org.onap.policy.apex.onap.bbs source=APEX target=DCAE
event parameter create name=AAI_CPE_AUTH_UPDATE parName=result schemaName=SimpleStringType
event parameter create name=AAI_CPE_AUTH_UPDATE parName=requestID schemaName=UUIDType
event parameter create name=AAI_CPE_AUTH_UPDATE parName=attachmentPoint     schemaName=SimpleStringType
event parameter create name=AAI_CPE_AUTH_UPDATE parName=serviceInstanceId   schemaName=SimpleStringType optional=true


#========> Define Tasks (1)
task create name=GetBBSCloseLoopEventTask

task inputfield create name=GetBBSCloseLoopEventTask fieldName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType
task outputfield create name=GetBBSCloseLoopEventTask fieldName=requestID schemaName=UUIDType
task outputfield create name=GetBBSCloseLoopEventTask fieldName=attachmentPoint     schemaName=SimpleStringType
task outputfield create name=GetBBSCloseLoopEventTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task contextref create name=GetBBSCloseLoopEventTask albumName=NomadicONTContextAlbum

task logic create name=GetBBSCloseLoopEventTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/GetBBSCloseLoopEventTask.js"
LE

#========> Define Tasks (2)
task create name=AAIServiceAssignedTask
task inputfield create name=AAIServiceAssignedTask fieldName=requestID schemaName=UUIDType
task inputfield create name=AAIServiceAssignedTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=AAIServiceAssignedTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=AAIServiceAssignedTask fieldName=requestID schemaName=UUIDType
task outputfield create name=AAIServiceAssignedTask fieldName=attachmentPoint     schemaName=SimpleStringType
task outputfield create name=AAIServiceAssignedTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true

task contextref create name=AAIServiceAssignedTask albumName=NomadicONTContextAlbum

task logic create name=AAIServiceAssignedTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/AAIServiceAssignedTask.js"
LE

#========> Define Tasks (3)
task create name=ErrorAAIServiceAssignedLogTask
task inputfield create name=ErrorAAIServiceAssignedLogTask fieldName=result schemaName=SimpleStringType
task inputfield create name=ErrorAAIServiceAssignedLogTask fieldName=requestID schemaName=UUIDType
task inputfield create name=ErrorAAIServiceAssignedLogTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=ErrorAAIServiceAssignedLogTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=ErrorAAIServiceAssignedLogTask fieldName=errorLogParam schemaName=SimpleStringType

task logic create name=ErrorAAIServiceAssignedLogTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ErrorAAIServiceAssignedLogTask.js"
LE

#========> Define Tasks (4)
task create name=SdncResourceUpdateTask
task inputfield create name=SdncResourceUpdateTask fieldName=requestID schemaName=UUIDType
task inputfield create name=SdncResourceUpdateTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=SdncResourceUpdateTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=SdncResourceUpdateTask fieldName=requestID schemaName=UUIDType
task outputfield create name=SdncResourceUpdateTask fieldName=attachmentPoint     schemaName=SimpleStringType
task outputfield create name=SdncResourceUpdateTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task contextref create name=SdncResourceUpdateTask albumName=NomadicONTContextAlbum

task logic create name=SdncResourceUpdateTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/SdncResourceUpdateTask.js"
LE

#========> Define Tasks (5)
task create name=ErrorSdncResourceUpdateTaskLogTask
task inputfield create name=ErrorSdncResourceUpdateTaskLogTask fieldName=result schemaName=SimpleStringType
task inputfield create name=ErrorSdncResourceUpdateTaskLogTask fieldName=requestID schemaName=UUIDType
task inputfield create name=ErrorSdncResourceUpdateTaskLogTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=ErrorSdncResourceUpdateTaskLogTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=ErrorSdncResourceUpdateTaskLogTask fieldName=errorLogParam schemaName=SimpleStringType

task logic create name=ErrorSdncResourceUpdateTaskLogTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ErrorSdncResourceUpdateTaskLogTask.js"
LE

#========> Define Tasks (6)
task create name=AAIServiceCreateTask
task inputfield create name=AAIServiceCreateTask fieldName=requestID schemaName=UUIDType
task inputfield create name=AAIServiceCreateTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=AAIServiceCreateTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=AAIServiceCreateTask fieldName=requestID schemaName=UUIDType
task outputfield create name=AAIServiceCreateTask fieldName=attachmentPoint     schemaName=SimpleStringType
task outputfield create name=AAIServiceCreateTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=AAIServiceCreateTask fieldName=result   schemaName=SimpleStringType
task contextref create name=AAIServiceCreateTask albumName=NomadicONTContextAlbum

task logic create name=AAIServiceCreateTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/AAIServiceCreateTask.js"
LE

#========> Define Tasks (7)
task create name=NomadicEventSuccessTask
task inputfield create name=NomadicEventSuccessTask fieldName=requestID schemaName=UUIDType
task inputfield create name=NomadicEventSuccessTask fieldName=attachmentPoint     schemaName=SimpleStringType
task inputfield create name=NomadicEventSuccessTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task outputfield create name=NomadicEventSuccessTask fieldName=result schemaName=SimpleStringType optional=true
task contextref create name=NomadicEventSuccessTask albumName=NomadicONTContextAlbum

task logic create name=NomadicEventSuccessTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/NomadicEventSuccessTask.js"
LE



#========> Define Tasks (8)
task create name=ServiceUpdateStateCpeAuthTask
task inputfield create name=ServiceUpdateStateCpeAuthTask fieldName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType
task outputfield create name=ServiceUpdateStateCpeAuthTask fieldName=result schemaName=SimpleStringType
task outputfield create name=ServiceUpdateStateCpeAuthTask fieldName=requestID schemaName=UUIDType
task outputfield create name=ServiceUpdateStateCpeAuthTask fieldName=attachmentPoint     schemaName=SimpleStringType
task outputfield create name=ServiceUpdateStateCpeAuthTask fieldName=serviceInstanceId   schemaName=SimpleStringType optional=true
task contextref create name=ServiceUpdateStateCpeAuthTask albumName=NomadicONTContextAlbum

task logic create name=ServiceUpdateStateCpeAuthTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ServiceUpdateStateCpeAuthTask.js"
LE

#========> Create Policy (1)
policy create name=ONTRegistrationPolicy template=Freestyle firstState=INIT

policy state create name=ONTRegistrationPolicy stateName=NOMADIC_ONT_REG_SUCCESS triggerName=END_CONTROL_LOOP defaultTaskName=NomadicEventSuccessTask
policy state output create name=ONTRegistrationPolicy stateName=NOMADIC_ONT_REG_SUCCESS outputName=NomadicEventSuccessOutPut eventName=END_EVENT
policy state taskref create name=ONTRegistrationPolicy stateName=NOMADIC_ONT_REG_SUCCESS taskName=NomadicEventSuccessTask outputType=DIRECT outputName=NomadicEventSuccessOutPut

#========> Create State (1.4)
policy state create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE triggerName=AAI_SERVICE_CREATE defaultTaskName=AAIServiceCreateTask

policy state output create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE outputName=AAIServiceCreateTaskOutput eventName=END_CONTROL_LOOP nextState=NOMADIC_ONT_REG_SUCCESS
policy state taskref create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE taskName=AAIServiceCreateTask outputType=DIRECT outputName=AAIServiceCreateTaskOutput

policy state output create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE outputName=SdncResourceUpdateErrorLogOutput eventName=ERROR_LOG
policy state taskref create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE taskName=ErrorSdncResourceUpdateTaskLogTask outputType=DIRECT outputName=SdncResourceUpdateErrorLogOutput

policy state contextref create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE albumName=NomadicONTContextAlbum

policy state selecttasklogic create name=ONTRegistrationPolicy stateName=AAI_SERVICE_CREATE logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/SU2orInitStateSelect.js"
LE


#========> Create State (1.3)
policy state create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE triggerName=SDNC_RESOURCE_UPDATE defaultTaskName=SdncResourceUpdateTask

policy state output create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE outputName=SdncResourceUpdateOutput eventName=AAI_SERVICE_CREATE nextState=AAI_SERVICE_CREATE
policy state taskref create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE taskName=SdncResourceUpdateTask outputType=DIRECT outputName=SdncResourceUpdateOutput

policy state output create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE outputName=AAIServiceAssignedErrorLogOutput eventName=ERROR_LOG
policy state taskref create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE taskName=ErrorAAIServiceAssignedLogTask outputType=DIRECT outputName=AAIServiceAssignedErrorLogOutput

policy state contextref create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE albumName=NomadicONTContextAlbum

policy state selecttasklogic create name=ONTRegistrationPolicy stateName=SDNC_RES_UPDATE logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/RUorInitStateSelect.js"
LE



#========> Create State (1.2)
policy state create name=ONTRegistrationPolicy stateName=AAI_SERVICE_ASSIGN triggerName=AAI_SERVICE_ASSIGNED defaultTaskName=AAIServiceAssignedTask

policy state output create name=ONTRegistrationPolicy stateName=AAI_SERVICE_ASSIGN outputName=AAIServiceAssignedOutput eventName=SDNC_RESOURCE_UPDATE nextState=SDNC_RES_UPDATE
policy state taskref create name=ONTRegistrationPolicy stateName=AAI_SERVICE_ASSIGN taskName=AAIServiceAssignedTask outputType=DIRECT outputName=AAIServiceAssignedOutput





#========> Create State (1.1)
policy state create name=ONTRegistrationPolicy stateName=INIT triggerName=Nomadic_ONT defaultTaskName=GetBBSCloseLoopEventTask
policy state output create name=ONTRegistrationPolicy stateName=INIT outputName=BBSCloseLoopEventOutPut eventName=AAI_SERVICE_ASSIGNED nextState=AAI_SERVICE_ASSIGN
policy state taskref create name=ONTRegistrationPolicy stateName=INIT taskName=GetBBSCloseLoopEventTask outputType=DIRECT outputName=BBSCloseLoopEventOutPut



#========> Create Policy (2)
policy create name=CPEAuthenticationPolicy template=Freestyle firstState=INIT


#========> Create State (2.1)
policy state create name=CPEAuthenticationPolicy stateName=INIT triggerName=CPE_Authentication defaultTaskName=ServiceUpdateStateCpeAuthTask
policy state output create name=CPEAuthenticationPolicy stateName=INIT outputName=ServiceUpdateCpeAuthOutPut eventName=AAI_CPE_AUTH_UPDATE
policy state taskref create name=CPEAuthenticationPolicy stateName=INIT taskName=ServiceUpdateStateCpeAuthTask outputType=DIRECT outputName=ServiceUpdateCpeAuthOutPut

validate
