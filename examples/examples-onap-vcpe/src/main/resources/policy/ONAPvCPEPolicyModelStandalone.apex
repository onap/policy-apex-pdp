#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2020 Nordix Foundation.
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

model create name=ONAPvCPEPolicyModelStandalone

schema create name=SimpleStringType  flavour=Java schema=java.lang.String
schema create name=SimpleLongType    flavour=Java schema=java.lang.Long
schema create name=SimpleBooleanType flavour=Java schema=java.lang.Boolean
schema create name=UUIDType          flavour=Java schema=java.util.UUID

schema create name=VirtualControlLoopEventType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/standalone/VirtualControlLoopEventType.avsc"
LE

schema create name=VirtualControlLoopNotificationType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/standalone/VirtualControlLoopNotificationType.avsc"
LE

schema create name=ControllerRequestType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/standalone/ControllerRequestType.avsc"
LE

schema create name=ControllerResponseType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/standalone/ControllerResponseType.avsc"
LE

schema create name=ControlLoopStatusType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/standalone/ControlLoopStatusType.avsc"
LE

schema create name=ActionEventListType flavour=Java schema=org.onap.policy.apex.service.engine.event.ApexEventList

album create name=ControlLoopStatusAlbum scope=policy writable=true schemaName=ControlLoopStatusType

album create name=RequestIDVNFIDAlbum scope=policy writable=true schemaName=UUIDType

album create name=VnfIdWhiteBlackListAlbum scope=policy writable=true schemaName=SimpleBooleanType
album create name=ServiceIdWhiteBlackListAlbum scope=policy writable=true schemaName=SimpleBooleanType
album create name=VServerIdWhiteBlackListAlbum scope=policy writable=true schemaName=SimpleBooleanType

event create name=VirtualControlLoopEvent version=1.0.2 nameSpace=org.onap.policy.apex.onap.vcpe source=Analytics target=APEX
event parameter create name=VirtualControlLoopEvent parName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

event create name=EntityStateUpdatedEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=EntityStateUpdatedEvent parName=requestID schemaName=UUIDType
event parameter create name=EntityStateUpdatedEvent parName=vnfID     schemaName=UUIDType
event parameter create name=EntityStateUpdatedEvent parName=vnfName   schemaName=SimpleStringType optional=true

event create name=ActionEventListEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ActionEventListEvent parName=ActionEventList schemaName=ActionEventListType

event create name=ActionEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ActionEvent parName=action   schemaName=SimpleStringType
event parameter create name=ActionEvent parName=requestID schemaName=UUIDType
event parameter create name=ActionEvent parName=vnfID     schemaName=UUIDType
event parameter create name=ActionEvent parName=vnfName   schemaName=SimpleStringType optional=true

event create name=ControllerRequestEvent version="2.0.0" nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=Controller
event parameter create name=ControllerRequestEvent parName=ControllerRequest schemaName=ControllerRequestType

event create name=ControllerResponseEvent version="2.0.0" nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APPC
event parameter create name=ControllerResponseEvent parName=ControllerResponse schemaName=ControllerResponseType

event create name=ControlLoopLogEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ControlLoopLogEvent parName=VirtualControlLoopNotification schemaName=VirtualControlLoopNotificationType

event create name=ConfigureBlackWhiteListEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ConfigureBlackWhiteListEvent parName=targetType schemaName=SimpleStringType
event parameter create name=ConfigureBlackWhiteListEvent parName=target schemaName=SimpleStringType
event parameter create name=ConfigureBlackWhiteListEvent parName=black schemaName=SimpleBooleanType

event create name=ConfigureBlackWhiteListResultEvent nameSpace=org.onap.policy.apex.onap.vcpe source=APEX target=APEX
event parameter create name=ConfigureBlackWhiteListResultEvent parName=targetType schemaName=SimpleStringType
event parameter create name=ConfigureBlackWhiteListResultEvent parName=target schemaName=SimpleStringType
event parameter create name=ConfigureBlackWhiteListResultEvent parName=black schemaName=SimpleBooleanType

task create name=GetEntityStateTask

task inputfield create name=GetEntityStateTask fieldName=VirtualControlLoopEvent schemaName=VirtualControlLoopEventType

task outputfield create name=GetEntityStateTask fieldName=requestID schemaName=UUIDType
task outputfield create name=GetEntityStateTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=GetEntityStateTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=GetEntityStateTask albumName=ControlLoopStatusAlbum
task contextref create name=GetEntityStateTask albumName=RequestIDVNFIDAlbum

task logic create name=GetEntityStateTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/GetEntityStateTask.js"
LE

task create name=CheckVNFIdTask

task inputfield create name=CheckVNFIdTask fieldName=requestID schemaName=UUIDType
task inputfield create name=CheckVNFIdTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=CheckVNFIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=CheckVNFIdTask fieldName=requestID schemaName=UUIDType
task outputfield create name=CheckVNFIdTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=CheckVNFIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=CheckVNFIdTask albumName=ControlLoopStatusAlbum
task contextref create name=CheckVNFIdTask albumName=VnfIdWhiteBlackListAlbum

task logic create name=CheckVNFIdTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/CheckVNFIdTask.js"
LE

task create name=CheckServiceIdTask

task inputfield create name=CheckServiceIdTask fieldName=requestID schemaName=UUIDType
task inputfield create name=CheckServiceIdTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=CheckServiceIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=CheckServiceIdTask fieldName=requestID schemaName=UUIDType
task outputfield create name=CheckServiceIdTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=CheckServiceIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=CheckServiceIdTask albumName=ControlLoopStatusAlbum
task contextref create name=CheckServiceIdTask albumName=ServiceIdWhiteBlackListAlbum

task logic create name=CheckServiceIdTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/CheckServiceIdTask.js"
LE

task create name=CheckVServerIdTask

task inputfield create name=CheckVServerIdTask fieldName=requestID schemaName=UUIDType
task inputfield create name=CheckVServerIdTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=CheckVServerIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=CheckVServerIdTask fieldName=requestID schemaName=UUIDType
task outputfield create name=CheckVServerIdTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=CheckVServerIdTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=CheckVServerIdTask albumName=ControlLoopStatusAlbum
task contextref create name=CheckVServerIdTask albumName=VServerIdWhiteBlackListAlbum

task logic create name=CheckVServerIdTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/CheckVServerIdTask.js"
LE

task create name=StopAndLogTask

task inputfield create name=StopAndLogTask fieldName=requestID schemaName=UUIDType
task inputfield create name=StopAndLogTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=StopAndLogTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=StopAndLogTask fieldName=requestID schemaName=UUIDType
task outputfield create name=StopAndLogTask fieldName=vnfID     schemaName=UUIDType
task outputfield create name=StopAndLogTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task contextref create name=StopAndLogTask albumName=ControlLoopStatusAlbum

task logic create name=StopAndLogTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/StopAndLogTask.js"
LE

task create name=InitiateActionsTask

task inputfield create name=InitiateActionsTask fieldName=requestID schemaName=UUIDType
task inputfield create name=InitiateActionsTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=InitiateActionsTask fieldName=ActionEventList schemaName=ActionEventListType

task contextref create name=InitiateActionsTask albumName=ControlLoopStatusAlbum

task logic create name=InitiateActionsTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/InitiateActionsTask.js"
LE

task create name=DoControllerRequestActionTask

task inputfield create name=DoControllerRequestActionTask fieldName=action   schemaName=SimpleStringType
task inputfield create name=DoControllerRequestActionTask fieldName=requestID schemaName=UUIDType
task inputfield create name=DoControllerRequestActionTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=DoControllerRequestActionTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=DoControllerRequestActionTask fieldName=requestID schemaName=UUIDType
task outputfield create name=DoControllerRequestActionTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=DoControllerRequestActionTask albumName=ControlLoopStatusAlbum

task logic create name=DoControllerRequestActionTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/DoControllerRequestActionTask.js"
LE

task create name=DoLogActionTask

task inputfield create name=DoLogActionTask fieldName=action   schemaName=SimpleStringType
task inputfield create name=DoLogActionTask fieldName=requestID schemaName=UUIDType
task inputfield create name=DoLogActionTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=DoLogActionTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=DoLogActionTask fieldName=requestID schemaName=UUIDType
task outputfield create name=DoLogActionTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=DoLogActionTask albumName=ControlLoopStatusAlbum

task logic create name=DoLogActionTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/DoLogActionTask.js"
LE

task create name=ControllerRequestTask

task inputfield create name=ControllerRequestTask fieldName=action    schemaName=SimpleStringType
task inputfield create name=ControllerRequestTask fieldName=requestID schemaName=UUIDType
task inputfield create name=ControllerRequestTask fieldName=vnfID     schemaName=UUIDType
task inputfield create name=ControllerRequestTask fieldName=vnfName   schemaName=SimpleStringType optional=true

task outputfield create name=ControllerRequestTask fieldName=ControllerRequest schemaName=ControllerRequestType

task contextref create name=ControllerRequestTask albumName=ControlLoopStatusAlbum
task contextref create name=ControllerRequestTask albumName=RequestIDVNFIDAlbum

task logic create name=ControllerRequestTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ControllerRequestTask.js"
LE

task create name=ControllerResponseTask

task inputfield create name=ControllerResponseTask fieldName=ControllerResponse schemaName=ControllerResponseType

task outputfield create name=ControllerResponseTask fieldName=requestID schemaName=UUIDType
task outputfield create name=ControllerResponseTask fieldName=vnfID     schemaName=UUIDType

task contextref create name=ControllerResponseTask albumName=ControlLoopStatusAlbum
task contextref create name=ControllerResponseTask albumName=RequestIDVNFIDAlbum

task logic create name=ControllerResponseTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ControllerResponseTask.js"
LE

task create name=ControlLoopLogTask

task inputfield create name=ControlLoopLogTask fieldName=requestID schemaName=UUIDType
task inputfield create name=ControlLoopLogTask fieldName=vnfID     schemaName=UUIDType

task outputfield create name=ControlLoopLogTask fieldName=VirtualControlLoopNotification schemaName=VirtualControlLoopNotificationType

task contextref create name=ControlLoopLogTask albumName=ControlLoopStatusAlbum

task logic create name=ControlLoopLogTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ControlLoopLogTask.js"
LE

task create name=ConfigureBlackWhiteListTask

task inputfield create name=ConfigureBlackWhiteListTask fieldName=targetType schemaName=SimpleStringType
task inputfield create name=ConfigureBlackWhiteListTask fieldName=black schemaName=SimpleBooleanType
task inputfield create name=ConfigureBlackWhiteListTask fieldName=target schemaName=SimpleStringType

task outputfield create name=ConfigureBlackWhiteListTask fieldName=targetType schemaName=SimpleStringType
task outputfield create name=ConfigureBlackWhiteListTask fieldName=black schemaName=SimpleBooleanType
task outputfield create name=ConfigureBlackWhiteListTask fieldName=target schemaName=SimpleStringType

task contextref create name=ConfigureBlackWhiteListTask albumName=VnfIdWhiteBlackListAlbum
task contextref create name=ConfigureBlackWhiteListTask albumName=ServiceIdWhiteBlackListAlbum
task contextref create name=ConfigureBlackWhiteListTask albumName=VServerIdWhiteBlackListAlbum

task logic create name=ConfigureBlackWhiteListTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ConfigureBlackWhiteListTask.js"
LE

#
# ReceiveEventPolicy
#
policy create name=ReceiveEventPolicy template=Freestyle firstState=GetEntityState

policy state create name=ReceiveEventPolicy stateName=LogAndStopState triggerName=EntityStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=ReceiveEventPolicy stateName=LogAndStopState outputName=AbatedOutput eventName=ControlLoopLogEvent
policy state taskref create name=ReceiveEventPolicy stateName=LogAndStopState taskName=ControlLoopLogTask outputType=DIRECT outputName=AbatedOutput

policy state create name=ReceiveEventPolicy stateName=ExecuteOrLogState triggerName=EntityStateUpdatedEvent defaultTaskName=StopAndLogTask
policy state output create name=ReceiveEventPolicy stateName=ExecuteOrLogState outputName=ExecuteOutput eventName=ActionEventListEvent
policy state taskref create name=ReceiveEventPolicy stateName=ExecuteOrLogState taskName=InitiateActionsTask outputType=DIRECT outputName=ExecuteOutput
policy state output create name=ReceiveEventPolicy stateName=ExecuteOrLogState outputName=StopAndLogOutput eventName=EntityStateUpdatedEvent nextState=LogAndStopState
policy state taskref create name=ReceiveEventPolicy stateName=ExecuteOrLogState taskName=StopAndLogTask outputType=DIRECT outputName=StopAndLogOutput

policy state contextref create name=ReceiveEventPolicy stateName=ExecuteOrLogState albumName=ControlLoopStatusAlbum

policy state selecttasklogic create name=ReceiveEventPolicy stateName=ExecuteOrLogState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ReceiveEventPolicyExecuteOrLogStateTSL.js"
LE

policy state create name=ReceiveEventPolicy stateName=CheckVNFIdState triggerName=EntityStateUpdatedEvent defaultTaskName=CheckVNFIdTask
policy state output create name=ReceiveEventPolicy stateName=CheckVNFIdState outputName=CheckVNFIdOutput eventName=EntityStateUpdatedEvent nextState=ExecuteOrLogState
policy state taskref create name=ReceiveEventPolicy stateName=CheckVNFIdState taskName=CheckVNFIdTask outputType=DIRECT outputName=CheckVNFIdOutput

policy state create name=ReceiveEventPolicy stateName=CheckVServerIdState triggerName=EntityStateUpdatedEvent defaultTaskName=CheckVServerIdTask
policy state output create name=ReceiveEventPolicy stateName=CheckVServerIdState outputName=CheckVServerIdOutput eventName=EntityStateUpdatedEvent nextState=CheckVNFIdState
policy state taskref create name=ReceiveEventPolicy stateName=CheckVServerIdState taskName=CheckVServerIdTask outputType=DIRECT outputName=CheckVServerIdOutput

policy state create name=ReceiveEventPolicy stateName=CheckServiceIdState triggerName=EntityStateUpdatedEvent defaultTaskName=CheckServiceIdTask
policy state output create name=ReceiveEventPolicy stateName=CheckServiceIdState outputName=CheckServiceIdOutput eventName=EntityStateUpdatedEvent nextState=CheckVServerIdState
policy state taskref create name=ReceiveEventPolicy stateName=CheckServiceIdState taskName=CheckServiceIdTask outputType=DIRECT outputName=CheckServiceIdOutput

policy state create name=ReceiveEventPolicy stateName=GetEntityState triggerName=VirtualControlLoopEvent defaultTaskName=GetEntityStateTask
policy state output create name=ReceiveEventPolicy stateName=GetEntityState outputName=GetEntityStateOutput eventName=EntityStateUpdatedEvent nextState=CheckServiceIdState
policy state taskref create name=ReceiveEventPolicy stateName=GetEntityState taskName=GetEntityStateTask outputType=DIRECT outputName=GetEntityStateOutput

policy state contextref create name=ReceiveEventPolicy stateName=GetEntityState albumName=ControlLoopStatusAlbum

#
# ExecuteActionsPolicy
#
policy create name=ExecuteActionsPolicy template=Freestyle firstState=ActionState

policy state create name=ExecuteActionsPolicy stateName=ControllerRequestActionState triggerName=ActionEvent defaultTaskName=ControllerRequestTask
policy state output create name=ExecuteActionsPolicy stateName=ControllerRequestActionState outputName=ControllerRequestOutput eventName=ControllerRequestEvent
policy state taskref create name=ExecuteActionsPolicy stateName=ControllerRequestActionState taskName=ControllerRequestTask outputType=DIRECT outputName=ControllerRequestOutput

policy state create name=ExecuteActionsPolicy stateName=LogActionState triggerName=EntityStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=ExecuteActionsPolicy stateName=LogActionState outputName=ResponseLogOutput eventName=ControlLoopLogEvent
policy state taskref create name=ExecuteActionsPolicy stateName=LogActionState taskName=ControlLoopLogTask outputType=DIRECT outputName=ResponseLogOutput

policy state create name=ExecuteActionsPolicy stateName=ActionState triggerName=ActionEvent defaultTaskName=DoLogActionTask
policy state output create name=ExecuteActionsPolicy stateName=ActionState outputName=LogActionOutput eventName=EntityStateUpdatedEvent nextState=LogActionState
policy state taskref create name=ExecuteActionsPolicy stateName=ActionState taskName=DoLogActionTask outputType=DIRECT outputName=LogActionOutput
policy state output create name=ExecuteActionsPolicy stateName=ActionState outputName=ControllerRequestActionOutput eventName=ActionEvent nextState=ControllerRequestActionState
policy state taskref create name=ExecuteActionsPolicy stateName=ActionState taskName=DoControllerRequestActionTask outputType=DIRECT outputName=ControllerRequestActionOutput

policy state contextref create name=ExecuteActionsPolicy stateName=ActionState albumName=ControlLoopStatusAlbum

policy state selecttasklogic create name=ExecuteActionsPolicy stateName=ActionState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/standalone/ExecuteActionsPolicyActionStateTSL.js"
LE

#
# ControllerResponsePolicy
#
policy create name=ControllerResponsePolicy template=Freestyle firstState=ControllerResponseState

policy state create name=ControllerResponsePolicy stateName=ResponseLogState triggerName=EntityStateUpdatedEvent defaultTaskName=ControlLoopLogTask
policy state output create name=ControllerResponsePolicy stateName=ResponseLogState outputName=ResponseLogOutput eventName=ControlLoopLogEvent
policy state taskref create name=ControllerResponsePolicy stateName=ResponseLogState taskName=ControlLoopLogTask outputType=DIRECT outputName=ResponseLogOutput

policy state create name=ControllerResponsePolicy stateName=ControllerResponseState triggerName=ControllerResponseEvent defaultTaskName=ControllerResponseTask
policy state output create name=ControllerResponsePolicy stateName=ControllerResponseState outputName=ControllerResponseOutput eventName=EntityStateUpdatedEvent nextState=ResponseLogState
policy state taskref create name=ControllerResponsePolicy stateName=ControllerResponseState taskName=ControllerResponseTask outputType=DIRECT outputName=ControllerResponseOutput

#
# ConfigureBlackWhiteListPolicy
#
policy create name=ConfigureBlackWhiteListPolicy template=Freestyle firstState=ConfigureBlackWhiteListState

policy state create name=ConfigureBlackWhiteListPolicy stateName=ConfigureBlackWhiteListState triggerName=ConfigureBlackWhiteListEvent defaultTaskName=ConfigureBlackWhiteListTask
policy state output create name=ConfigureBlackWhiteListPolicy stateName=ConfigureBlackWhiteListState outputName=ConfigureBlackWhiteListOutput eventName=ConfigureBlackWhiteListResultEvent
policy state taskref create name=ConfigureBlackWhiteListPolicy stateName=ConfigureBlackWhiteListState taskName=ConfigureBlackWhiteListTask outputType=DIRECT outputName=ConfigureBlackWhiteListOutput

validate
