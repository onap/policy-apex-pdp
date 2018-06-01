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

model create name=AvroSchemaPolicy


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

event create name=Test_InputEvent nameSpace=org.onap.policy.apex.test source=External target=Apex

event parameter create name=Test_InputEvent parName=AAA   schemaName=SchemaLogicTest optional=false
event parameter create name=Test_InputEvent parName=BBB   schemaName=SchemaLogicTest optional=true
event parameter create name=Test_InputEvent parName=CCC   schemaName=SchemaLogicTest
