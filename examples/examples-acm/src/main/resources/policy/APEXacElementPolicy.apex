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

model create name=APEXacElementPolicy

##
## SCHEMAS
##

schema create name=SimpleStringType flavour=Java schema=java.lang.String
schema create name=UUIDType         flavour=Java schema=java.util.UUID
schema create name=SimpleIntType    flavour=Java schema=java.lang.Integer

schema create name=ACEventType flavour=Json schema=LS
#MACROFILE:"src/main/resources/schemas/ACEventType.json"
LE

##
## EVENTS
##

album create name=ACElementAlbum scope=policy writable=true schemaName=ACEventType

# Incoming event
event create name=AcElementEvent version=0.0.1 nameSpace=org.onap.policy.apex.ac.element source=Dmaap target=APEX toscaPolicyState=ENTRY
event parameter create name=AcElementEvent parName=DmaapResponseEvent schemaName=ACEventType

# Forwarding event
event create name=DmaapResponseStatusEvent nameSpace=org.onap.policy.apex.ac.element source=APEX target=Dmaap
event parameter create name=DmaapResponseStatusEvent parName=DmaapResponseStatusEvent schemaName=ACEventType

# Log event
event create name=LogEvent nameSpace=org.onap.policy.apex.ac.element source=APEX target=file
event parameter create name=LogEvent parName=final_status schemaName=SimpleStringType
event parameter create name=LogEvent parName=message schemaName=SimpleStringType

##
## TASKS
##

task create name=ForwardPayloadTask
task contextref create name=ForwardPayloadTask albumName=ACElementAlbum
task inputfield create name=ForwardPayloadTask fieldName=DmaapResponseEvent schemaName=ACEventType
task logic create name=ForwardPayloadTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/ForwardPayloadTask.js"
LE


##
## POLICIES
##

# Policy ReceiveEventPolicy
policy create name=ReceiveEventPolicy template=Freestyle firstState=DecideForwardingState


# State DecideForwardingState
policy state create name=ReceiveEventPolicy stateName=DecideForwardingState triggerName=AcElementEvent defaultTaskName=ForwardPayloadTask
policy state output create name=ReceiveEventPolicy stateName=DecideForwardingState outputName=CreateForwardPayload eventName=DmaapResponseStatusEvent nextState=NULL
policy state taskref create name=ReceiveEventPolicy stateName=DecideForwardingState taskName=ForwardPayloadTask outputType=DIRECT outputName=CreateForwardPayload


validate
