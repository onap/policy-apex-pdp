#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
#  Modifications Copyright (C) 2019-2020,2024 Nordix Foundation.
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

model create name=ONAPvCPEPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String
schema create name=SimpleLongType    flavour=Java schema=java.lang.Long
schema create name=SimpleBooleanType flavour=Java schema=java.lang.Boolean
schema create name=UUIDType          flavour=Java schema=java.util.UUID

schema create name=VirtualControlLoopEventType flavour=Java schema=org.onap.policy.controlloop.VirtualControlLoopEvent
schema create name=VirtualControlLoopNotificationType flavour=Java schema=org.onap.policy.controlloop.VirtualControlLoopNotification

schema create name=APPCLCMRequestType flavour=Java schema=org.onap.policy.appclcm.AppcLcmMessageWrapper
schema create name=APPCLCMResponseType flavour=Java schema=org.onap.policy.appclcm.AppcLcmMessageWrapper

schema create name=GuardDecisionAttributesType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/definitive/GuardDecisionAttributesType.avsc"
LE

schema create name=VCPEClosedLoopStatusType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/definitive/VCPEClosedLoopStatusType.avsc"
LE

album create name=VCPEClosedLoopStatusAlbum scope=policy writable=true schemaName=VCPEClosedLoopStatusType

album create name=ControlLoopExecutionIDAlbum scope=policy writable=true schemaName=UUIDType

album create name=RequestIDVNFIDAlbum scope=policy writable=true schemaName=UUIDType

event create name=ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e version=1.0.2 nameSpace=org.onap.policy.apex.onap.vcpe source=DCAE target=APEX
event parameter create name=ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e parName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

event create name=VCPEStateUpdatedEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=VCPEStateUpdatedEvent parName=requestID schemaName=UUIDType
event parameter create name=VCPEStateUpdatedEvent parName=vnfID     schemaName=UUIDType
event parameter create name=VCPEStateUpdatedEvent parName=vnfName   schemaName=SimpleStringType optional=true

event create name=GuardRequestEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX

event parameter create name=GuardRequestEvent parName=decisionAttributes schemaName=GuardDecisionAttributesType
event parameter create name=GuardRequestEvent parName=onapName           schemaName=SimpleStringType

event create name=GuardResponseEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX

event parameter create name=GuardResponseEvent parName=decision  schemaName=SimpleStringType
event parameter create name=GuardResponseEvent parName=details   schemaName=SimpleStringType

event create name=APPCRestartVNFRequestEvent version="2.0.0" nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APPC
event parameter create name=APPCRestartVNFRequestEvent parName=APPCLCMRequestEvent schemaName=APPCLCMRequestType

event create name=APPCRestartVNFResponseEvent version="2.0.0" nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APPC
event parameter create name=APPCRestartVNFResponseEvent parName=APPCLCMResponseEvent schemaName=APPCLCMResponseType

event create name=ControlLoopLogEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ControlLoopLogEvent parName=VirtualControlLoopNotification schemaName=VirtualControlLoopNotificationType

task create name=GetVCPEStateTask

task inputfield create name=GetVCPEStateTask fieldName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

task outputfield create name=GetVCPEStateTask fieldName=requestID schemaName=UUIDType
task outputfield create name=GetVCPEStateTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=GetVCPEStateTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=GetVCPEStateTask albumName=VCPEClosedLoopStatusAlbum
task contextref create name=GetVCPEStateTask albumName=RequestIDVNFIDAlbum

task logic create name=GetVCPEStateTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/GetVCPEStateTask.js"
LE

task create name=AbatedTask

task inputfield create name=AbatedTask fieldName=requestID schemaName=UUIDType
task inputfield create name=AbatedTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=AbatedTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=AbatedTask fieldName=requestID schemaName=UUIDType
task outputfield create name=AbatedTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=AbatedTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=AbatedTask albumName=VCPEClosedLoopStatusAlbum

task logic create name=AbatedTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/AbatedTask.js"
LE

task create name=GuardRequestTask

task inputfield create name=GuardRequestTask fieldName=requestID schemaName=UUIDType
task inputfield create name=GuardRequestTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=GuardRequestTask fieldName=decisionAttributes schemaName=GuardDecisionAttributesType
task outputfield create name=GuardRequestTask fieldName=onapName           schemaName=SimpleStringType

task contextref create name=GuardRequestTask albumName=VCPEClosedLoopStatusAlbum
task contextref create name=GuardRequestTask albumName=ControlLoopExecutionIDAlbum

task logic create name=GuardRequestTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/GuardRequestTask.js"
LE

task create name=GuardResponseTask

task inputfield create name=GuardResponseTask fieldName=decision  schemaName=SimpleStringType
task inputfield create name=GuardResponseTask fieldName=details   schemaName=SimpleStringType

task outputfield create name=GuardResponseTask fieldName=requestID schemaName=UUIDType
task outputfield create name=GuardResponseTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=GuardResponseTask albumName=VCPEClosedLoopStatusAlbum
task contextref create name=GuardResponseTask albumName=ControlLoopExecutionIDAlbum

task logic create name=GuardResponseTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/GuardResponseTask.js"
LE

task create name=DeniedTask

task inputfield create name=DeniedTask fieldName=requestID schemaName=UUIDType
task inputfield create name=DeniedTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=DeniedTask fieldName=requestID schemaName=UUIDType
task outputfield create name=DeniedTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=DeniedTask albumName=VCPEClosedLoopStatusAlbum

task logic create name=DeniedTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/DeniedTask.js"
LE

task create name=APPCRestartVNFRequestTask

task inputfield create name=APPCRestartVNFRequestTask fieldName=requestID schemaName=UUIDType
task inputfield create name=APPCRestartVNFRequestTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=APPCRestartVNFRequestTask fieldName=APPCLCMRequestEvent schemaName=APPCLCMRequestType

task contextref create name=APPCRestartVNFRequestTask albumName=VCPEClosedLoopStatusAlbum
task contextref create name=APPCRestartVNFRequestTask albumName=RequestIDVNFIDAlbum

task logic create name=APPCRestartVNFRequestTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/APPCRestartVNFRequestTask.js"
LE

task create name=APPCRestartVNFResponseTask

task inputfield create name=APPCRestartVNFResponseTask fieldName=APPCLCMResponseEvent schemaName=APPCLCMResponseType

task outputfield create name=APPCRestartVNFResponseTask fieldName=requestID schemaName=UUIDType
task outputfield create name=APPCRestartVNFResponseTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=APPCRestartVNFResponseTask albumName=VCPEClosedLoopStatusAlbum
task contextref create name=APPCRestartVNFResponseTask albumName=RequestIDVNFIDAlbum

task logic create name=APPCRestartVNFResponseTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/APPCRestartVNFResponseTask.js"
LE

task create name=ControlLoopLogTask

task inputfield create name=ControlLoopLogTask fieldName=requestID schemaName=UUIDType
task inputfield create name=ControlLoopLogTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=ControlLoopLogTask fieldName=VirtualControlLoopNotification schemaName=VirtualControlLoopNotificationType

task contextref create name=ControlLoopLogTask albumName=VCPEClosedLoopStatusAlbum

task logic create name=ControlLoopLogTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/ControlLoopLogTask.js"
LE

task create name=AAILookupTask

task inputfield create name=AAILookupTask fieldName=requestID schemaName=UUIDType
task inputfield create name=AAILookupTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=AAILookupTask fieldName=requestID schemaName=UUIDType
task outputfield create name=AAILookupTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=AAILookupTask albumName=VCPEClosedLoopStatusAlbum

task logic create name=AAILookupTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/AAILookupTask.js"
LE

task create name=NoAAILookupTask

task inputfield create name=NoAAILookupTask fieldName=requestID schemaName=UUIDType
task inputfield create name=NoAAILookupTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=NoAAILookupTask fieldName=requestID schemaName=UUIDType
task outputfield create name=NoAAILookupTask fieldName=vnfID     schemaName=UUIDType

task logic create name=NoAAILookupTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/NoAAILookupTask.js"
LE

#
# ReceiveEventPolicy
#
policy create name=ReceiveEventPolicy template=Freestyle firstState=GetVCPEState

policy state create name=ReceiveEventPolicy stateName=AbatedState triggerName=VCPEStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=ReceiveEventPolicy stateName=AbatedState outputName=AbatedOutput eventName=ControlLoopLogEvent
policy state taskref create name=ReceiveEventPolicy stateName=AbatedState taskName=ControlLoopLogTask outputType=DIRECT outputName=AbatedOutput

policy state create name=ReceiveEventPolicy stateName=OnsetOrAbatedState triggerName=VCPEStateUpdatedEvent defaultTaskName=AbatedTask
policy state output create name=ReceiveEventPolicy stateName=OnsetOrAbatedState outputName=OnsetOutput eventName=GuardRequestEvent
policy state taskref create name=ReceiveEventPolicy stateName=OnsetOrAbatedState taskName=GuardRequestTask outputType=DIRECT outputName=OnsetOutput
policy state output create name=ReceiveEventPolicy stateName=OnsetOrAbatedState outputName=AbatedOutput eventName=VCPEStateUpdatedEvent nextState=AbatedState
policy state taskref create name=ReceiveEventPolicy stateName=OnsetOrAbatedState taskName=AbatedTask outputType=DIRECT outputName=AbatedOutput

policy state contextref create name=ReceiveEventPolicy stateName=OnsetOrAbatedState albumName=VCPEClosedLoopStatusAlbum

policy state selecttasklogic create name=ReceiveEventPolicy stateName=OnsetOrAbatedState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/OnsetOrAbatedStateTSL.js"
LE

policy state create name=ReceiveEventPolicy stateName=RequestAAIState triggerName=VCPEStateUpdatedEvent defaultTaskName=AAILookupTask
policy state output create name=ReceiveEventPolicy stateName=RequestAAIState outputName=RequestAAIStateOutput eventName=VCPEStateUpdatedEvent nextState=OnsetOrAbatedState
policy state taskref create name=ReceiveEventPolicy stateName=RequestAAIState taskName=AAILookupTask outputType=DIRECT outputName=RequestAAIStateOutput

policy state contextref create name=ReceiveEventPolicy stateName=RequestAAIState albumName=VCPEClosedLoopStatusAlbum

policy state create name=ReceiveEventPolicy stateName=GetVCPEState triggerName=ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e defaultTaskName=GetVCPEStateTask
policy state output create name=ReceiveEventPolicy stateName=GetVCPEState outputName=GetVCPEStateOutput eventName=VCPEStateUpdatedEvent nextState=RequestAAIState
policy state taskref create name=ReceiveEventPolicy stateName=GetVCPEState taskName=GetVCPEStateTask outputType=DIRECT outputName=GetVCPEStateOutput

#
# RestartAPPCRequestPolicy
#
policy create name=RestartAPPCRequestPolicy template=Freestyle firstState=PolicyGuardResponseState

policy state create name=RestartAPPCRequestPolicy stateName=DeniedState triggerName=VCPEStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=RestartAPPCRequestPolicy stateName=DeniedState outputName=DeniedOutput eventName=ControlLoopLogEvent
policy state taskref create name=RestartAPPCRequestPolicy stateName=DeniedState taskName=ControlLoopLogTask outputType=DIRECT outputName=DeniedOutput

policy state create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState triggerName=VCPEStateUpdatedEvent defaultTaskName=DeniedTask
policy state output create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState outputName=PermitOutput eventName=APPCRestartVNFRequestEvent
policy state taskref create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState taskName=APPCRestartVNFRequestTask outputType=DIRECT outputName=PermitOutput
policy state output create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState outputName=DenyOutput eventName=VCPEStateUpdatedEvent nextState=DeniedState
policy state taskref create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState taskName=DeniedTask outputType=DIRECT outputName=DenyOutput

policy state create name=RestartAPPCRequestPolicy stateName=PolicyGuardResponseState triggerName=GuardResponseEvent defaultTaskName=GuardResponseTask
policy state output create name=RestartAPPCRequestPolicy stateName=PolicyGuardResponseState outputName=GuardResponseOutput eventName=VCPEStateUpdatedEvent nextState=PermitOrDenyState
policy state taskref create name=RestartAPPCRequestPolicy stateName=PolicyGuardResponseState taskName=GuardResponseTask outputType=DIRECT outputName=GuardResponseOutput

policy state contextref create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState albumName=VCPEClosedLoopStatusAlbum

policy state selecttasklogic create name=RestartAPPCRequestPolicy stateName=PermitOrDenyState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/definitive/RestartAPPCRequestPolicyPermitOrDenyTSL.js"
LE

#
# RestartAPPCResponsePolicy
#
policy create name=RestartAPPCResponsePolicy template=Freestyle firstState=RestartAPPCResponseState

policy state create name=RestartAPPCResponsePolicy stateName=ResponseLogState triggerName=VCPEStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=RestartAPPCResponsePolicy stateName=ResponseLogState outputName=ResponseLogOutput eventName=ControlLoopLogEvent
policy state taskref create name=RestartAPPCResponsePolicy stateName=ResponseLogState taskName=ControlLoopLogTask outputType=DIRECT outputName=ResponseLogOutput

policy state create name=RestartAPPCResponsePolicy stateName=RestartAPPCResponseState triggerName=APPCRestartVNFResponseEvent defaultTaskName=APPCRestartVNFResponseTask
policy state output create name=RestartAPPCResponsePolicy stateName=RestartAPPCResponseState outputName=APPCRestartVNFResponseOutput eventName=VCPEStateUpdatedEvent nextState=ResponseLogState
policy state taskref create name=RestartAPPCResponsePolicy stateName=RestartAPPCResponseState taskName=APPCRestartVNFResponseTask outputType=DIRECT outputName=APPCRestartVNFResponseOutput

validate

