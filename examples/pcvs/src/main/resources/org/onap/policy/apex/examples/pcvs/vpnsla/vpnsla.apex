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

model create name=PCVS-VpnSla version=1.0.0 description="Policies-Controlled Video Streaming, VPN SLA Policy Model"



schema create name=reportDecl version=1.0.0 description="Report of activities of a policy/task" flavour=Java schema=java.lang.String
event create name=ReportOut version=1.0.0 description="Report of a policy (issued by a task)" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="APEX" target="CtxtManagement"
event parameter create name=ReportOut version=1.0.0 parName=report schemaName=reportDecl schemaVersion=1.0.0

schema create name=timestampDecl version=1.0.0 description="Timestamp" flavour=Java schema=java.lang.Long



schema create name=ctxtEdgeNameDecl version=1.0.0 description="Topology Edges: edge (link) name" flavour=Java schema=java.lang.String
schema create name=ctxtEdgeStartDecl version=1.0.0 description="Topology Edges: edge endpoint (start)" flavour=Java schema=java.lang.String
schema create name=ctxtEdgeEndDecl version=1.0.0 description="Topology Edges: edge endpoint (end)" flavour=Java schema=java.lang.String
schema create name=ctxtEdgeStatusDecl version=1.0.0 description="Topology Edges: edge status as up (true) or down (false)" flavour=Java schema=java.lang.Boolean

schema create name=ctxtTopologyEdgesDecl version=1.0.0 description="Topology Edges Context Map" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/topology-edges.avsc"
LE
album create name=albumTopoEdges scope=global writable=true schemaName=ctxtTopologyEdgesDecl


task create name=EdgeContextTask version=1.0.0 description="This task adds event context to edge context"
task inputfield create name=EdgeContextTask version=1.0.0 fieldName=edgeName schemaName=ctxtEdgeNameDecl schemaVersion=1.0.0
task inputfield create name=EdgeContextTask version=1.0.0 fieldName=start schemaName=ctxtEdgeStartDecl schemaVersion=1.0.0
task inputfield create name=EdgeContextTask version=1.0.0 fieldName=end schemaName=ctxtEdgeEndDecl schemaVersion=1.0.0
task inputfield create name=EdgeContextTask version=1.0.0 fieldName=status schemaName=ctxtEdgeStatusDecl schemaVersion=1.0.0
task outputfield create name=EdgeContextTask version=1.0.0 fieldName=report schemaName=reportDecl schemaVersion=1.0.0
task contextref create name=EdgeContextTask albumName=albumTopoEdges
task logic create name=EdgeContextTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/ctxt-edges.js"
LE

event create name=EdgeContextEventIn version=1.0.0 description="Event to add an Edge to engine Context" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="CtxtManagement" target="APEX"
event parameter create name=EdgeContextEventIn version=1.0.0 parName=edgeName schemaName=ctxtEdgeNameDecl schemaVersion=1.0.0
event parameter create name=EdgeContextEventIn version=1.0.0 parName=start schemaName=ctxtEdgeStartDecl schemaVersion=1.0.0
event parameter create name=EdgeContextEventIn version=1.0.0 parName=end schemaName=ctxtEdgeEndDecl schemaVersion=1.0.0
event parameter create name=EdgeContextEventIn version=1.0.0 parName=status schemaName=ctxtEdgeStatusDecl schemaVersion=1.0.0

policy create name=EdgeContextPolicy version=1.0.0 description="Policy that adds an edge to context" template=FREEFORM firstState=EdgeContextState
policy state create name=EdgeContextPolicy version=1.0.0 stateName=EdgeContextState triggerName=EdgeContextEventIn triggerVersion=1.0.0 defaultTaskName=EdgeContextTask defaultTaskVersion=1.0.0
policy state output create name=EdgeContextPolicy version=1.0.0 stateName=EdgeContextState outputName=EdgeContextState_Output_Direct eventName=ReportOut eventVersion=1.0.0 nextState=NULL
policy state taskref create name=EdgeContextPolicy version=1.0.0 stateName=EdgeContextState taskLocalName=doContext taskName=EdgeContextTask taskVersion=1.0.0 outputType=DIRECT outputName=EdgeContextState_Output_Direct



schema create name=ctxtNodeNameDecl version=1.0.0 description="Topology Nodes: node name" flavour=Java schema=java.lang.String
schema create name=ctxtNodeMininetNameDecl version=1.0.0 description="Topology Nodes: node name in Mininet" flavour=Java schema=java.lang.String

schema create name=ctxtTopologyNodesDecl version=1.0.0 description="Topology Nodes Context Map" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/topology-nodes.avsc"
LE
album create name=albumTopoNodes scope=global writable=true schemaName=ctxtTopologyNodesDecl

task create name=NodeContextTask version=1.0.0 description="This task adds event context to node context"
task inputfield create name=NodeContextTask version=1.0.0 fieldName=nodeName schemaName=ctxtNodeNameDecl schemaVersion=1.0.0
task inputfield create name=NodeContextTask version=1.0.0 fieldName=mininetName schemaName=ctxtNodeMininetNameDecl schemaVersion=1.0.0
task outputfield create name=NodeContextTask version=1.0.0 fieldName=report schemaName=reportDecl schemaVersion=1.0.0
task contextref create name=NodeContextTask albumName=albumTopoNodes
task logic create name=NodeContextTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/ctxt-nodes.js"
LE

event create name=NodeContextEventIn version=1.0.0 description="Event to add Node to engine Context" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="CtxtManagement" target="APEX"
event parameter create name=NodeContextEventIn version=1.0.0 parName=nodeName schemaName=ctxtNodeNameDecl schemaVersion=1.0.0
event parameter create name=NodeContextEventIn version=1.0.0 parName=mininetName schemaName=ctxtNodeMininetNameDecl schemaVersion=1.0.0

policy create name=NodeContextPolicy version=1.0.0 description="Policy that adds an node to context" template=FREEFORM firstState=NodeContextState
policy state create name=NodeContextPolicy version=1.0.0 stateName=NodeContextState triggerName=NodeContextEventIn triggerVersion=1.0.0 defaultTaskName=NodeContextTask defaultTaskVersion=1.0.0
policy state output create name=NodeContextPolicy version=1.0.0 stateName=NodeContextState outputName=NodeContextState_Output_Direct eventName=ReportOut eventVersion=1.0.0 nextState=NULL
policy state taskref create name=NodeContextPolicy version=1.0.0 stateName=NodeContextState taskLocalName=doContext taskName=NodeContextTask taskVersion=1.0.0 outputType=DIRECT outputName=NodeContextState_Output_Direct




schema create name=ctxtCustomerNameDecl version=1.0.0 description="Customer Context: customer name" flavour=Java schema=java.lang.String
schema create name=ctxtCustomerPriorityDecl version=1.0.0 description="Customer Context: priority flag" flavour=Java schema=java.lang.Boolean
schema create name=ctxtCustomerSatisfactionDecl version=1.0.0 description="Customer Context: satisfaction in percent" flavour=Java schema=java.lang.Integer
schema create name=ctxtCustomerDowntimeSLADecl version=1.0.0 description="Customer Context: contracted downtime as per SLA" flavour=Java schema=java.lang.Integer
schema create name=ctxtCustomerDowntimeYTDDecl version=1.0.0 description="Customer Context: year-to-date downtime experienced" flavour=Java schema=java.lang.Integer
schema create name=ctxtCustomerLinksDecl version=1.0.0 description="Customer Context: links a customer uses (for events/task)" flavour=Java schema=java.lang.String

schema create name=ctxtCustomerMapDecl version=1.0.0 description="Map of customers with all known information" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/customers.avsc"
LE
album create name=albumCustomerMap scope=global writable=true schemaName=ctxtCustomerMapDecl

task create name=CustomerContextTask version=1.0.0 description="This task adds event context to customer context"
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=customerName schemaName=ctxtCustomerNameDecl schemaVersion=1.0.0
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=priority schemaName=ctxtCustomerPriorityDecl schemaVersion=1.0.0
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=satisfaction schemaName=ctxtCustomerSatisfactionDecl schemaVersion=1.0.0
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=dtSLA schemaName=ctxtCustomerDowntimeSLADecl schemaVersion=1.0.0
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=dtYTD schemaName=ctxtCustomerDowntimeYTDDecl schemaVersion=1.0.0
task inputfield create name=CustomerContextTask version=1.0.0 fieldName=links schemaName=ctxtCustomerLinksDecl schemaVersion=1.0.0
task outputfield create name=CustomerContextTask version=1.0.0 fieldName=report schemaName=reportDecl schemaVersion=1.0.0
task contextref create name=CustomerContextTask albumName=albumCustomerMap
task contextref create name=CustomerContextTask albumName=albumTopoEdges
task logic create name=CustomerContextTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/ctxt-customer.js"
LE

event create name=CustomerContextEventIn version=1.0.0 description="Event to add Customers to engine Context" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="CtxtManagement" target="APEX"
event parameter create name=CustomerContextEventIn version=1.0.0 parName=customerName schemaName=ctxtCustomerNameDecl schemaVersion=1.0.0
event parameter create name=CustomerContextEventIn version=1.0.0 parName=priority schemaName=ctxtCustomerPriorityDecl schemaVersion=1.0.0
event parameter create name=CustomerContextEventIn version=1.0.0 parName=satisfaction schemaName=ctxtCustomerSatisfactionDecl schemaVersion=1.0.0
event parameter create name=CustomerContextEventIn version=1.0.0 parName=dtSLA schemaName=ctxtCustomerDowntimeSLADecl schemaVersion=1.0.0
event parameter create name=CustomerContextEventIn version=1.0.0 parName=dtYTD schemaName=ctxtCustomerDowntimeYTDDecl schemaVersion=1.0.0
event parameter create name=CustomerContextEventIn version=1.0.0 parName=links schemaName=ctxtCustomerLinksDecl schemaVersion=1.0.0

policy create name=CustomerContextPolicy version=1.0.0 description="Policy that adds Customer information to engine context" template=FREEFORM firstState=CustomerContextState
policy state create name=CustomerContextPolicy version=1.0.0 stateName=CustomerContextState triggerName=CustomerContextEventIn triggerVersion=1.0.0 defaultTaskName=CustomerContextTask defaultTaskVersion=1.0.0
policy state output create name=CustomerContextPolicy version=1.0.0 stateName=CustomerContextState outputName=CustomerContextState_Output_Direct eventName=ReportOut eventVersion=1.0.0 nextState=NULL
policy state taskref create name=CustomerContextPolicy version=1.0.0 stateName=CustomerContextState taskLocalName=doContext taskName=CustomerContextTask taskVersion=1.0.0 outputType=DIRECT outputName=CustomerContextState_Output_Direct




schema create name=edgeNameDecl version=1.0.0 description="Edge name" flavour=Java schema=java.lang.String
schema create name=edgeStatusDecl version=1.0.0 description="Statuf of the edge (UP, DOWN)" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/link-status.avsc"
LE
schema create name=edgeChangedDecl version=1.0.0 description="Status Change (true:change, false:no change)" flavour=Java schema=java.lang.Boolean

task create name=VpnSlaPolicyMatchTask version=1.0.0 description="Pre-process an edge event"
task inputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=status schemaName=edgeStatusDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=status schemaName=edgeStatusDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=hasChanged schemaName=edgeChangedDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyMatchTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyMatchTask albumName=albumTopoEdges
task logic create name=VpnSlaPolicyMatchTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-match.js"
LE




schema create name=problemMapDecl version=1.0.0 description="Map of problems with all known Information" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/problems.avsc"
LE
album create name=albumProblemMap scope=global writable=true schemaName=problemMapDecl

schema create name=establishSituationDecl version=1.0.0 description="Establish: the situation that was established" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/situation.avsc"
LE

task create name=VpnSlaPolicyEstablishTask version=1.0.0 description="Task taking a match event and establishing a situation"
task inputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=status schemaName=edgeStatusDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=hasChanged schemaName=edgeChangedDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyEstablishTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyEstablishTask albumName=albumProblemMap
task contextref create name=VpnSlaPolicyEstablishTask albumName=albumCustomerMap
task logic create name=VpnSlaPolicyEstablishTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-establish.js"
LE




schema create name=decideDecisionDecl version=1.0.0 description="Decide: the taken decision" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/avro/decision.avsc"
LE

task create name=VpnSlaPolicyDecideNoneTask version=1.0.0 description="Decide task for a 'none' problem"
task inputfield create name=VpnSlaPolicyDecideNoneTask version=1.0.0 fieldName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyDecideNoneTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideNoneTask version=1.0.0 fieldName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideNoneTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task logic create name=VpnSlaPolicyDecideNoneTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-decide-none.js"
LE


task create name=VpnSlaPolicyDecideSlaTask version=1.0.0 description="Decide task solving the problem by balancing SLAs"
task inputfield create name=VpnSlaPolicyDecideSlaTask version=1.0.0 fieldName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyDecideSlaTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideSlaTask version=1.0.0 fieldName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideSlaTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyDecideSlaTask albumName=albumCustomerMap
task contextref create name=VpnSlaPolicyDecideSlaTask albumName=albumProblemMap
task logic create name=VpnSlaPolicyDecideSlaTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-decide-sla.js"
LE


task create name=VpnSlaPolicyDecidePriorityTask version=1.0.0 description="Decide task solving the problem by using customer priorities"
task inputfield create name=VpnSlaPolicyDecidePriorityTask version=1.0.0 fieldName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyDecidePriorityTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecidePriorityTask version=1.0.0 fieldName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecidePriorityTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyDecidePriorityTask albumName=albumCustomerMap
task contextref create name=VpnSlaPolicyDecidePriorityTask albumName=albumProblemMap
task logic create name=VpnSlaPolicyDecidePriorityTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-decide-priority.js"
LE


task create name=VpnSlaPolicyDecideSolvedTask version=1.0.0 description="Decide task solving the problem by using customer priorities"
task inputfield create name=VpnSlaPolicyDecideSolvedTask version=1.0.0 fieldName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyDecideSolvedTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideSolvedTask version=1.0.0 fieldName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyDecideSolvedTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyDecideSolvedTask albumName=albumProblemMap
task logic create name=VpnSlaPolicyDecideSolvedTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-decide-solved.js"
LE




schema create name=actionDecl version=1.0.0 description="An action for the actioning system" flavour=Java schema=java.lang.String

task create name=VpnSlaPolicyActTask version=1.0.0 description="Task issueing an action for taken decision"
task inputfield create name=VpnSlaPolicyActTask version=1.0.0 fieldName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
task inputfield create name=VpnSlaPolicyActTask version=1.0.0 fieldName=matchStart schemaName=timestampDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyActTask version=1.0.0 fieldName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
task outputfield create name=VpnSlaPolicyActTask version=1.0.0 fieldName=action schemaName=actionDecl schemaVersion=1.0.0
task contextref create name=VpnSlaPolicyActTask albumName=albumCustomerMap
task contextref create name=VpnSlaPolicyActTask albumName=albumProblemMap
task logic create name=VpnSlaPolicyActTask logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/task-act.js"
LE







event create name=VpnSlaTrigger version=1.0.0 description="Event triggering the VPN SLA policy" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="TriggerSys" target="VpnSlaMatch"
event parameter create name=VpnSlaTrigger version=1.0.0 parName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
event parameter create name=VpnSlaTrigger version=1.0.0 parName=status schemaName=edgeStatusDecl schemaVersion=1.0.0

event create name=VpnSlaMatchOut version=1.0.0 description="Event with matched trigger for the VPN SLA policy" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="VpnSlaMatch" target="VpnSlaEstablish"
event parameter create name=VpnSlaMatchOut version=1.0.0 parName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
event parameter create name=VpnSlaMatchOut version=1.0.0 parName=status schemaName=edgeStatusDecl schemaVersion=1.0.0
event parameter create name=VpnSlaMatchOut version=1.0.0 parName=hasChanged schemaName=edgeChangedDecl schemaVersion=1.0.0
event parameter create name=VpnSlaMatchOut version=1.0.0 parName=matchStart schemaName=timestampDecl schemaVersion=1.0.0

event create name=VpnSlaEstablishOut version=1.0.0 description="Event with situation for the SLA policy" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="SlaEstablish" target="SlaDecide"
event parameter create name=VpnSlaEstablishOut version=1.0.0 parName=situation schemaName=establishSituationDecl schemaVersion=1.0.0
event parameter create name=VpnSlaEstablishOut version=1.0.0 parName=matchStart schemaName=timestampDecl schemaVersion=1.0.0

event create name=VpnSlaDecideOut version=1.0.0 description="Event with a decision for the SLA policy" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="SlaDecide" target="SlaAct"
event parameter create name=VpnSlaDecideOut version=1.0.0 parName=decision schemaName=decideDecisionDecl schemaVersion=1.0.0
event parameter create name=VpnSlaDecideOut version=1.0.0 parName=matchStart schemaName=timestampDecl schemaVersion=1.0.0

event create name=VpnSlaActOut version=1.0.0 description="Event action" nameSpace=org.onap.policy.apex.examples.pcvs.vpnsla source="SlaAct" target="ActioningSystem"
event parameter create name=VpnSlaActOut version=1.0.0 parName=edgeName schemaName=edgeNameDecl schemaVersion=1.0.0
event parameter create name=VpnSlaActOut version=1.0.0 parName=action schemaName=actionDecl schemaVersion=1.0.0


policy create name=VpnSlaPolicy version=1.0.0 description="Policy deciding customer treatment based on SLAs as MEDA policy" template=FREEFORM firstState=VpnSlaPolicyMatchState

policy state create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyActState triggerName=VpnSlaDecideOut triggerVersion=1.0.0 defaultTaskName=VpnSlaPolicyActTask defaultTaskVersion=1.0.0
policy state output create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyActState outputName=SlaPolicyAct_Output_Direct eventName=VpnSlaActOut eventVersion=1.0.0 nextState=NULL
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyActState taskLocalName=act taskName=VpnSlaPolicyActTask taskVersion=1.0.0 outputType=DIRECT outputName=SlaPolicyAct_Output_Direct

policy state create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState triggerName=VpnSlaEstablishOut triggerVersion=1.0.0 defaultTaskName=VpnSlaPolicyDecideSlaTask defaultTaskVersion=1.0.0
policy state contextref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState albumName=albumProblemMap
policy state selecttasklogic create name=VpnSlaPolicy stateName=VpnSlaPolicyDecideState logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/org/onap/policy/apex/examples/pcvs/vpnsla/logic/tsl-decide.js"
LE
policy state output create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState outputName=VpnSlaPolicyDecide_Output_Direct eventName=VpnSlaDecideOut eventVersion=1.0.0 nextState=VpnSlaPolicyActState
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState taskLocalName=decideNone taskName=VpnSlaPolicyDecideNoneTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyDecide_Output_Direct
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState taskLocalName=decideNone taskName=VpnSlaPolicyDecideSolvedTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyDecide_Output_Direct
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState taskLocalName=decideSla taskName=VpnSlaPolicyDecideSlaTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyDecide_Output_Direct
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyDecideState taskLocalName=decidePriority taskName=VpnSlaPolicyDecidePriorityTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyDecide_Output_Direct

policy state create name=VpnSlaPolicy version=1.0.0 stateName=VpmSlaPolicyEstablishState triggerName=VpnSlaMatchOut triggerVersion=1.0.0 defaultTaskName=VpnSlaPolicyEstablishTask defaultTaskVersion=1.0.0
policy state output create name=VpnSlaPolicy version=1.0.0 stateName=VpmSlaPolicyEstablishState outputName=VpnSlaPolicyEstablish_Output_Direct eventName=VpnSlaEstablishOut eventVersion=1.0.0 nextState=VpnSlaPolicyDecideState
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpmSlaPolicyEstablishState taskLocalName=establish taskName=VpnSlaPolicyEstablishTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyEstablish_Output_Direct

policy state create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyMatchState triggerName=VpnSlaTrigger triggerVersion=1.0.0 defaultTaskName=VpnSlaPolicyMatchTask defaultTaskVersion=1.0.0
policy state output create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyMatchState outputName=VpnSlaPolicyMatch_Output_Direct eventName=VpnSlaMatchOut eventVersion=1.0.0 nextState=VpmSlaPolicyEstablishState
policy state taskref create name=VpnSlaPolicy version=1.0.0 stateName=VpnSlaPolicyMatchState taskLocalName=match taskName=VpnSlaPolicyMatchTask taskVersion=1.0.0 outputType=DIRECT outputName=VpnSlaPolicyMatch_Output_Direct



validate
quit

