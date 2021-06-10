#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2019 Nordix Foundation.
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

model create name=ExecutionPropertiesTestPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String
schema create name=SimpleIntegerType flavour=Java schema=java.lang.Integer

event create name=RunTestEvent nameSpace=org.onap.policy.apex.domains.test source=JUNIT target=apex
event parameter create name=RunTestEvent parName=testToRun schemaName=SimpleStringType

task create name=ReadOnlyTask
task logic create name=ReadOnlyTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/ReadOnlyTask.js"
LE

task create name=EmptyToEmptyTask
task logic create name=EmptyToEmptyTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/EmptyToEmptyTask.js"
LE

task create name=EmptyToDefinedTask
task logic create name=EmptyToDefinedTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/EmptyToDefinedTask.js"
LE

task create name=DefinedToEmptyTask
task logic create name=DefinedToEmptyTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/DefinedToEmptyTask.js"
LE

task create name=AddPropertyTask
task logic create name=AddPropertyTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/AddPropertyTask.js"
LE

task create name=RemovePropertyTask
task logic create name=RemovePropertyTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/RemovePropertyTask.js"
LE

policy create name=ExecutionPropertiesTestPolicy template=freestyle firstState=RunTestState

policy state create name=ExecutionPropertiesTestPolicy stateName=RunTestState triggerName=RunTestEvent defaultTaskName=ReadOnlyTask
policy state output create name=ExecutionPropertiesTestPolicy stateName=RunTestState outputName=TestFinalOutput eventName=RunTestEvent
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=ReadOnlyTask outputType=DIRECT outputName=TestFinalOutput
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=EmptyToEmptyTask outputType=DIRECT outputName=TestFinalOutput
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=EmptyToDefinedTask outputType=DIRECT outputName=TestFinalOutput
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=DefinedToEmptyTask outputType=DIRECT outputName=TestFinalOutput
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=AddPropertyTask outputType=DIRECT outputName=TestFinalOutput
policy state taskref create name=ExecutionPropertiesTestPolicy stateName=RunTestState taskName=RemovePropertyTask outputType=DIRECT outputName=TestFinalOutput

policy state selecttasklogic create name=ExecutionPropertiesTestPolicy stateName=RunTestState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/RunTestStateTSL.js"
LE

validate

