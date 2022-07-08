#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2022 Nordix Foundation.
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

model create name=APEXgRPCPolicy

##
## SCHEMAS
##

schema create name=SimpleStringType flavour=Java schema=java.lang.String
schema create name=UUIDType         flavour=Java schema=java.util.UUID
schema create name=SimpleIntType    flavour=Java schema=java.lang.Integer

schema create name=CDSRequestCommonHeaderType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSRequestCommonHeaderType.avsc"
LE

schema create name=CDSResponseCommonHeaderType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSResponseCommonHeaderType.avsc"
LE

schema create name=CDSActionIdentifiersType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSActionIdentifiersType.avsc"
LE

schema create name=CDSResponseStatusType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSResponseStatusType.avsc"
LE

schema create name=CDSCreateSubscriptionPayloadType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSCreateSubscriptionPayloadType.avsc"
LE

schema create name=CDSDeleteSubscriptionPayloadType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSDeleteSubscriptionPayloadType.avsc"
LE

schema create name=CDSResponsePayloadType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/CDSResponsePayloadType.avsc"
LE

schema create name=SubscriptionStatusType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/SubscriptionStatusType.avsc"
LE

schema create name=PMSubscriptionType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/PMSubscriptionType.avsc"
LE

schema create name=SubscriptionType flavour=Avro schema=LS
#MACROFILE:"src/main/resources/schemas/SubscriptionType.avsc"
LE

##
## EVENTS
##

album create name=PMSubscriptionAlbum scope=policy writable=true schemaName=PMSubscriptionType

# Incoming event
event create name=testPolicyB version=0.0.1 nameSpace=org.onap.policy.apex.onap.pmcontrol source=DCAE target=APEX toscaPolicyState=ENTRY
event parameter create name=testPolicyB parName=nfName schemaName=SimpleStringType
event parameter create name=testPolicyB parName=policyName schemaName=SimpleStringType
event parameter create name=testPolicyB parName=changeType schemaName=SimpleStringType
event parameter create name=testPolicyB parName=closedLoopControlName schemaName=SimpleStringType
event parameter create name=testPolicyB parName=subscription schemaName=SubscriptionType

event create name=PMSubscriptionOutputEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=APEX
event parameter create name=PMSubscriptionOutputEvent parName=albumID     schemaName=UUIDType

event create name=CreateSubscriptionPayloadEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=APEX
event parameter create name=CreateSubscriptionPayloadEvent parName=payload schemaName=CDSCreateSubscriptionPayloadType
event parameter create name=CreateSubscriptionPayloadEvent parName=albumID     schemaName=UUIDType

event create name=DeleteSubscriptionPayloadEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=APEX
event parameter create name=DeleteSubscriptionPayloadEvent parName=payload schemaName=CDSDeleteSubscriptionPayloadType
event parameter create name=DeleteSubscriptionPayloadEvent parName=albumID     schemaName=UUIDType

event create name=CreateSubscriptionRequestEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=APEX
event parameter create name=CreateSubscriptionRequestEvent parName=commonHeader schemaName=CDSRequestCommonHeaderType
event parameter create name=CreateSubscriptionRequestEvent parName=actionIdentifiers schemaName=CDSActionIdentifiersType
event parameter create name=CreateSubscriptionRequestEvent parName=payload schemaName=CDSCreateSubscriptionPayloadType

event create name=DeleteSubscriptionRequestEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=APEX
event parameter create name=DeleteSubscriptionRequestEvent parName=commonHeader schemaName=CDSRequestCommonHeaderType
event parameter create name=DeleteSubscriptionRequestEvent parName=actionIdentifiers schemaName=CDSActionIdentifiersType
event parameter create name=DeleteSubscriptionRequestEvent parName=payload schemaName=CDSDeleteSubscriptionPayloadType

event create name=CDSResponseEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=CDS target=APEX
event parameter create name=CDSResponseEvent parName=commonHeader schemaName=CDSResponseCommonHeaderType
event parameter create name=CDSResponseEvent parName=actionIdentifiers schemaName=CDSActionIdentifiersType
event parameter create name=CDSResponseEvent parName=status schemaName=CDSResponseStatusType
event parameter create name=CDSResponseEvent parName=payload schemaName=CDSResponsePayloadType

event create name=CDSResponseStatusEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=DCAE toscaPolicyState=EXIT_SUCCESS
event parameter create name=CDSResponseStatusEvent parName=status schemaName=SubscriptionStatusType

event create name=LogEvent nameSpace=org.onap.policy.apex.onap.pmcontrol source=APEX target=DCAE
event parameter create name=LogEvent parName=final_status schemaName=SimpleStringType
event parameter create name=LogEvent parName=message schemaName=SimpleStringType

##
## TASKS
##


task create name=ReceivePMSubscriptionTask
task contextref create name=ReceivePMSubscriptionTask albumName=PMSubscriptionAlbum
task logic create name=ReceivePMSubscriptionTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ReceivePMSubscriptionTask.js"
LE

task create name=CreateSubscriptionPayloadTask
task contextref create name=CreateSubscriptionPayloadTask albumName=PMSubscriptionAlbum
task logic create name=CreateSubscriptionPayloadTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/CreateSubscriptionPayloadTask.js"
LE

task create name=DeleteSubscriptionPayloadTask
task contextref create name=DeleteSubscriptionPayloadTask albumName=PMSubscriptionAlbum
task logic create name=DeleteSubscriptionPayloadTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/DeleteSubscriptionPayloadTask.js"
LE

task create name=CreateSubscriptionRequestTask
task contextref create name=CreateSubscriptionRequestTask albumName=PMSubscriptionAlbum
task logic create name=CreateSubscriptionRequestTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/CreateSubscriptionRequestTask.js"
LE

task create name=DeleteSubscriptionRequestTask
task contextref create name=DeleteSubscriptionRequestTask albumName=PMSubscriptionAlbum
task logic create name=DeleteSubscriptionRequestTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/DeleteSubscriptionRequestTask.js"
LE

task create name=CDSResponseTask
task contextref create name=CDSResponseTask albumName=PMSubscriptionAlbum
task logic create name=CDSResponseTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ResponseTask.js"
LE


##
## POLICIES
##


# Policy ReceiveEventPolicy
policy create name=ReceiveEventPolicy template=Freestyle firstState=ReceiveSubscriptionState

# State CreateSubscription
policy state create name=ReceiveEventPolicy stateName=CreateSubscription triggerName=CreateSubscriptionPayloadEvent defaultTaskName=CreateSubscriptionRequestTask
policy state output create name=ReceiveEventPolicy stateName=CreateSubscription outputName=IssueCreateSubscriptionRequestOutput eventName=CreateSubscriptionRequestEvent nextState=NULL
policy state taskref create name=ReceiveEventPolicy stateName=CreateSubscription taskName=CreateSubscriptionRequestTask outputType=DIRECT outputName=IssueCreateSubscriptionRequestOutput
policy state contextref create name=ReceiveEventPolicy stateName=CreateSubscription albumName=PMSubscriptionAlbum

# State DeleteSubscription
policy state create name=ReceiveEventPolicy stateName=DeleteSubscription triggerName=DeleteSubscriptionPayloadEvent defaultTaskName=DeleteSubscriptionRequestTask
policy state output create name=ReceiveEventPolicy stateName=DeleteSubscription outputName=IssueDeleteSubscriptionRequestOutput eventName=DeleteSubscriptionRequestEvent nextState=NULL
policy state taskref create name=ReceiveEventPolicy stateName=DeleteSubscription taskName=DeleteSubscriptionRequestTask outputType=DIRECT outputName=IssueDeleteSubscriptionRequestOutput
policy state contextref create name=ReceiveEventPolicy stateName=DeleteSubscription albumName=PMSubscriptionAlbum

# State CreateOrDeleteState
policy state create name=ReceiveEventPolicy stateName=CreateOrDeleteState triggerName=PMSubscriptionOutputEvent defaultTaskName=CreateSubscriptionPayloadTask

policy state output create name=ReceiveEventPolicy stateName=CreateOrDeleteState outputName=CreateSubscriptionPayload eventName=CreateSubscriptionPayloadEvent nextState=CreateSubscription
policy state taskref create name=ReceiveEventPolicy stateName=CreateOrDeleteState taskName=CreateSubscriptionPayloadTask outputType=DIRECT outputName=CreateSubscriptionPayload

policy state output create name=ReceiveEventPolicy stateName=CreateOrDeleteState outputName=DeleteSubscriptionPayload eventName=DeleteSubscriptionPayloadEvent nextState=DeleteSubscription
policy state taskref create name=ReceiveEventPolicy stateName=CreateOrDeleteState taskName=DeleteSubscriptionPayloadTask outputType=DIRECT outputName=DeleteSubscriptionPayload

policy state contextref create name=ReceiveEventPolicy stateName=CreateOrDeleteState albumName=PMSubscriptionAlbum
policy state selecttasklogic create name=ReceiveEventPolicy stateName=CreateOrDeleteState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/CreateOrDeleteStateTSL.js"
LE

# State ReceiveSubscriptionState
policy state create name=ReceiveEventPolicy stateName=ReceiveSubscriptionState triggerName=testPolicyB defaultTaskName=ReceivePMSubscriptionTask
policy state output create name=ReceiveEventPolicy stateName=ReceiveSubscriptionState outputName=ReceivePMSubscriptionOutput eventName=PMSubscriptionOutputEvent nextState=CreateOrDeleteState
policy state taskref create name=ReceiveEventPolicy stateName=ReceiveSubscriptionState taskName=ReceivePMSubscriptionTask outputType=DIRECT outputName=ReceivePMSubscriptionOutput
policy state contextref create name=ReceiveEventPolicy stateName=ReceiveSubscriptionState albumName=PMSubscriptionAlbum


# Policy CDSResponsePolicy
policy create name=CDSResponsePolicy template=Freestyle firstState=CDSResponseState

# State CDSResponseState
policy state create name=CDSResponsePolicy stateName=CDSResponseState triggerName=CDSResponseEvent defaultTaskName=CDSResponseTask
policy state output create name=CDSResponsePolicy stateName=CDSResponseState outputName=ResponseOutput eventName=CDSResponseStatusEvent
policy state output create name=CDSResponsePolicy stateName=CDSResponseState outputName=ResponseOutput eventName=LogEvent
policy state taskref create name=CDSResponsePolicy stateName=CDSResponseState taskName=CDSResponseTask outputType=DIRECT outputName=ResponseOutput

validate