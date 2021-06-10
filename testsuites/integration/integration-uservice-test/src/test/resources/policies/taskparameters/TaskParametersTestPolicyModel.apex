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

model create name=TaskParametersTestPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String

event create name=CLTriggerEvent nameSpace=org.onap.policy.apex.domains.test source=Junit target=APEX
event parameter create name=CLTriggerEvent parName=event schemaName=SimpleStringType

event create name=ServiceInfoEvent nameSpace=org.onap.policy.apex.domains.test source=Decide target=OutSide
event parameter create name=ServiceInfoEvent parName=serviceDetails schemaName=SimpleStringType

event create name=CLOutputEvent nameSpace=org.onap.policy.apex.domains.test source=Decide target=OutSide
event parameter create name=CLOutputEvent parName=status schemaName=SimpleStringType

task create name=SetServiceIdForTest version=1.0.0
task logic create name=SetServiceIdForTest logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/taskparameters/SetServiceIdForTest.js"
LE

task create name=SetControlLoopNameForTest version=1.0.0
task logic create name=SetControlLoopNameForTest logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/taskparameters/SetControlLoopNameForTest.js"
LE


policy create name=Policy1 template=freestyle firstState=Junit

policy state create name=Policy1 stateName=Junit triggerName=CLTriggerEvent defaultTaskName=SetServiceIdForTest
policy state output create name=Policy1 stateName=Junit  outputName=ServiceInfo eventName=ServiceInfoEvent nextState=NULL
policy state taskref create name=Policy1 stateName=Junit taskLocalName=SetServiceInfo taskName=SetServiceIdForTest outputType=DIRECT outputName=ServiceInfo

policy create name=Policy2 template=freestyle firstState=Decide

policy state create name=Policy2 stateName=Decide triggerName=ServiceInfoEvent defaultTaskName=SetControlLoopNameForTest
policy state output create name=Policy2 stateName=Decide  outputName=CLOut eventName=CLOutputEvent nextState=NULL
policy state taskref create name=Policy2 stateName=Decide taskLocalName=SetControlLoopName taskName=SetControlLoopNameForTest outputType=DIRECT outputName=CLOut
validate

