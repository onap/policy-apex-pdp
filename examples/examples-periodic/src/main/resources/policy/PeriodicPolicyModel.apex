#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

model create name=PeriodicPolicyModel

schema create name=SimpleLongType   flavour=Java schema=java.lang.Long
schema create name=SimpleStringType flavour=Java schema=java.lang.String
schema create name=EventListType  flavour=Java schema=org.onap.policy.apex.service.engine.event.ApexEventList

event create name=PERIODIC_EVENT nameSpace=org.onap.policy.apex.periodic source=APEX target=APEX
event parameter create name=PERIODIC_EVENT parName=PERIODIC_DELAY        schemaName=SimpleLongType
event parameter create name=PERIODIC_EVENT parName=PERIODIC_FIRST_TIME   schemaName=SimpleLongType
event parameter create name=PERIODIC_EVENT parName=PERIODIC_LAST_TIME    schemaName=SimpleLongType
event parameter create name=PERIODIC_EVENT parName=PERIODIC_CURRENT_TIME schemaName=SimpleLongType
event parameter create name=PERIODIC_EVENT parName=PERIODIC_EVENT_COUNT  schemaName=SimpleLongType

event create name=EventListEvent nameSpace=org.onap.policy.apex.periodic source=APEX target=APEX
event parameter create name=EventListEvent parName=EventList schemaName=EventListType

event create name=InputEvent nameSpace=org.onap.policy.apex.periodic source=APEX target=APEX
event parameter create name=InputEvent parName=Par0 schemaName=SimpleStringType
event parameter create name=InputEvent parName=Par1 schemaName=SimpleStringType

event create name=OutputEvent nameSpace=org.onap.policy.apex.periodic source=APEX target=APEX
event parameter create name=OutputEvent parName=Par0 schemaName=SimpleStringType
event parameter create name=OutputEvent parName=Par1 schemaName=SimpleStringType

task create name=PeriodicTask
task inputfield create name=PeriodicTask fieldName=PERIODIC_DELAY        schemaName=SimpleLongType
task inputfield create name=PeriodicTask fieldName=PERIODIC_FIRST_TIME   schemaName=SimpleLongType
task inputfield create name=PeriodicTask fieldName=PERIODIC_LAST_TIME    schemaName=SimpleLongType
task inputfield create name=PeriodicTask fieldName=PERIODIC_CURRENT_TIME schemaName=SimpleLongType
task inputfield create name=PeriodicTask fieldName=PERIODIC_EVENT_COUNT  schemaName=SimpleLongType

task outputfield create name=PeriodicTask fieldName=EventList schemaName=EventListType

task logic create name=PeriodicTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/PeriodicTask.js"
LE

task create name=OutputTask
task inputfield create name=OutputTask fieldName=Par0 schemaName=SimpleStringType
task inputfield create name=OutputTask fieldName=Par1 schemaName=SimpleStringType

task outputfield create name=OutputTask fieldName=Par0 schemaName=SimpleStringType
task outputfield create name=OutputTask fieldName=Par1 schemaName=SimpleStringType

task logic create name=OutputTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/OutputTask.js"
LE

policy create name=PeriodicPolicy template=freestyle firstState=PeriodicState

policy state create name=PeriodicPolicy stateName=PeriodicState triggerName=PERIODIC_EVENT defaultTaskName=PeriodicTask
policy state output create name=PeriodicPolicy stateName=PeriodicState outputName=PeriodicOutput eventName=EventListEvent
policy state taskref create name=PeriodicPolicy stateName=PeriodicState taskName=PeriodicTask outputType=DIRECT outputName=PeriodicOutput


policy create name=OutputPolicy template=freestyle firstState=OutputState

policy state create name=OutputPolicy stateName=OutputState triggerName=InputEvent defaultTaskName=OutputTask
policy state output create name=OutputPolicy stateName=OutputState outputName=OutputOutput eventName=OutputEvent
policy state taskref create name=OutputPolicy stateName=OutputState taskName=OutputTask outputType=DIRECT outputName=OutputOutput

validate

