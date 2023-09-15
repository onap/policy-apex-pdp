#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2023 Nordix Foundation. All rights reserved.
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

model create name=DecisionMakerPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String
schema create name=SimpleIntegerType flavour=Java schema=java.lang.Integer

album create name=AnswerAlbum scope=policy writable=true schemaName=SimpleStringType
album create name=LastAnswerAlbum scope=policy writable=true schemaName=SimpleIntegerType

event create name=AnswerEvent nameSpace=org.onap.policy.apex.domains.decisionmaker source=DCAE target=apex
event parameter create name=AnswerEvent parName=a0 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a1 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a2 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a3 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a4 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a5 schemaName=SimpleStringType
event parameter create name=AnswerEvent parName=a6 schemaName=SimpleStringType

task create name=AnswerInitTask
task inputfield create name=AnswerInitTask fieldName=a0 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a1 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a2 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a3 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a4 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a5 schemaName=SimpleStringType
task inputfield create name=AnswerInitTask fieldName=a6 schemaName=SimpleStringType

task outputfield create name=AnswerInitTask fieldName=a0 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a1 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a2 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a3 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a4 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a5 schemaName=SimpleStringType
task outputfield create name=AnswerInitTask fieldName=a6 schemaName=SimpleStringType

task contextref create name=AnswerInitTask albumName=AnswerAlbum
task contextref create name=AnswerInitTask albumName=LastAnswerAlbum

task logic create name=AnswerInitTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/logic/AnswerInitTask.js"
LE

policy create name=AnswerInitPolicy template=freestyle firstState=AnswerInitState

policy state create name=AnswerInitPolicy stateName=AnswerInitState triggerName=AnswerEvent defaultTaskName=AnswerInitTask
policy state output create name=AnswerInitPolicy stateName=AnswerInitState outputName=AnswerInitOutput eventName=AnswerEvent
policy state taskref create name=AnswerInitPolicy stateName=AnswerInitState taskName=AnswerInitTask outputType=DIRECT outputName=AnswerInitOutput

validate

