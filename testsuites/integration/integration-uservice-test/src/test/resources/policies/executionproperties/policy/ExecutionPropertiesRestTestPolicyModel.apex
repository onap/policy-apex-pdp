#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2019-2020 Nordix Foundation.
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

model create name=ExecutionPropertiesRestTestPolicyModel

schema create name=SimpleStringType  flavour=Java schema=java.lang.String

event create name=Event0200 nameSpace=org.onap.policy.apex.domains.test source=Junit target=Decide
event parameter create name=Event0200 parName=testToRun schemaName=SimpleStringType

event create name=Event0201 nameSpace=org.onap.policy.apex.domains.test source=Decide target=OutSide
event parameter create name=Event0201 parName=testToRun schemaName=SimpleStringType


task create name=TaskFetchHttpCode
task logic create name=TaskFetchHttpCode logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/TaskFetchHttpCode.js"
LE

task create name=TaskTagUrl
task logic create name=TaskTagUrl logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/test/resources/policies/executionproperties/logic/TaskTagUrl.js"
LE

policy create name=Policy2 template=freestyle firstState=Junit

policy state create name=Policy2 stateName=Decide triggerName=Event0201 defaultTaskName=TaskTagUrl
policy state output create name=Policy2 stateName=Decide  outputName=TagUrlOut eventName=Event0201 nextState=NULL
policy state taskref create name=Policy2 stateName=Decide taskLocalName=tag taskName=TaskTagUrl outputType=DIRECT outputName=TagUrlOut

policy state create name=Policy2 stateName=Junit triggerName=Event0200 defaultTaskName=TaskFetchHttpCode
policy state output create name=Policy2 stateName=Junit  outputName=HttpCodeOut eventName=Event0201 nextState=Decide
policy state taskref create name=Policy2 stateName=Junit taskLocalName=check taskName=TaskFetchHttpCode outputType=DIRECT outputName=HttpCodeOut

validate
