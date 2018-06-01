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

model create name=LogicTestPolicy

schema create name=SchemaLogicTest flavour=Exotic schema=LS
{
asdmasdm'asda'sdasd
}
LE

schema create name=SchemaLogicTest flavour=Avro schema=LS
{
  "type"      : "record",
  "name"      : "VPNActEvent",
  "namespace" : "org.onap.policy.apex.domains.vpn.events",
  "fields"    : [
    {"name": "nameSpace", "type": "string"},
    {"name": "name",      "type": "string"},
    {"name": "version",   "type": "string"},
    {"name": "source",    "type": "string"},
    {"name": "target",    "type": "string"},
    {"name": "LinkMap", "type": {
        "type" : "record",
        "name" : "LinkMap",
        "namespace" : "org.onap.policy.apex.domains.vpn",
        "fields" : [ ]
      }
    },
    {"name": "CustomerMap", "type": {
        "type" : "record",
        "name" : "CustomerMap",
        "namespace" : "org.onap.policy.apex.domains.vpn",
        "fields" : [ ]
      }
    }
  ]
}
LE
 
# This creates logic as a block
task logic create name=LogicTestTask0 logicFlavour=MVEL logic=LS   			
{
	import java.util.Date;
	logger.debug("Running ConsistencyCheck");

	outFields.putAll(inFields);
	outFields["Attribute1PolicyValue"] = "POLICY_DEFINED";
	logger.debug("Finished Running ConsistencyCheck");

	return true;
}
LE

task create name=LogicTestTask0
 
# This creates logic as a block
task logic create name=LogicTestTask0 logicFlavour=MVEL logic=LS   			
{
	import java.util.Date;
	logger.debug("Running ConsistencyCheck");

	outFields.putAll(inFields);
	outFields["Attribute1PolicyValue"] = "POLICY_DEFINED";
	logger.debug("Finished Running ConsistencyCheck");

	return true;
}
LE
  
task create name=LogicTestTask1
 
# This creates logic on a single line
task logic create name=LogicTestTask1 logicFlavour=MVEL logic="import java.util.Date;"

task create name=LogicTestTask2
 
# This creates logic as a block
task logic create name=LogicTestTask2 logicFlavour=MVEL logic=LS
{
	system.out.println("{");
}LE

task create name=LogicTestTaskA

# This fails as it should
task logic create name=LogicTestTaskA logicFlavour=MVEL logic=

task create name=LogicTestTaskB

# This fails as it should, no end tag
task logic create name=LogicTestTaskB logicFlavour=MVEL logic=LS
{
