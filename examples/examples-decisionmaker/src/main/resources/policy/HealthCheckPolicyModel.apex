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

model create name=HealthCheckPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String
schema create name=SimpleIntegerType flavour=Java schema=java.lang.Integer
schema create name=SimpleBooleanType flavour=Java schema=java.lang.Boolean

event create name=HealthCheckEvent nameSpace=org.onap.policy.apex.domains.HealthCheck source=Distribution target=Apex
event parameter create name=HealthCheckEvent parName=name schemaName=SimpleStringType
event parameter create name=HealthCheckEvent parName=url schemaName=SimpleStringType
event parameter create name=HealthCheckEvent parName=healthy schemaName=SimpleBooleanType
event parameter create name=HealthCheckEvent parName=code schemaName=SimpleIntegerType
event parameter create name=HealthCheckEvent parName=message schemaName=SimpleStringType

task create name=HealthCheckTask
task inputfield create name=HealthCheckTask fieldName=name schemaName=SimpleStringType
task inputfield create name=HealthCheckTask fieldName=url schemaName=SimpleStringType
task inputfield create name=HealthCheckTask fieldName=healthy schemaName=SimpleBooleanType
task inputfield create name=HealthCheckTask fieldName=code schemaName=SimpleIntegerType
task inputfield create name=HealthCheckTask fieldName=message schemaName=SimpleStringType

task outputfield create name=HealthCheckTask fieldName=name schemaName=SimpleStringType
task outputfield create name=HealthCheckTask fieldName=url schemaName=SimpleStringType
task outputfield create name=HealthCheckTask fieldName=healthy schemaName=SimpleBooleanType
task outputfield create name=HealthCheckTask fieldName=code schemaName=SimpleIntegerType
task outputfield create name=HealthCheckTask fieldName=message schemaName=SimpleStringType

task logic create name=HealthCheckTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/HealthCheckTask.js"
LE

policy create name=HealthCheckPolicy template=freestyle firstState=HealthCheckState

policy state create name=HealthCheckPolicy stateName=HealthCheckState triggerName=HealthCheckEvent defaultTaskName=HealthCheckTask
policy state output create name=HealthCheckPolicy stateName=HealthCheckState outputName=HealthCheckOutput eventName=HealthCheckEvent
policy state taskref create name=HealthCheckPolicy stateName=HealthCheckState taskName=HealthCheckTask outputType=DIRECT outputName=HealthCheckOutput

validate

