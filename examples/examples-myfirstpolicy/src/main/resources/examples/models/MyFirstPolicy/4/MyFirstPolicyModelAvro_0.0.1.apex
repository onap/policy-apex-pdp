#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2020 Nordix Foundation. All rights reserved.
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

model create name=MyFirstPolicyModel version=0.0.1 uuid=540226fb-55ee-4f0e-a444-983a0494818e description="This is my first Apex Policy Model."

schema create name=assistant_ID_type version=0.0.1 uuid=36df4c71-9616-4206-8b53-976a5cd4bd87 description="A type for 'assistant_ID' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/assistantIDType.avsc"
LE

schema create name=authorised_type version=0.0.1 uuid=d48b619e-d00d-4008-b884-02d76ea4350b description="A type for 'authorised' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/authorisedType.avsc"
LE

schema create name=branch_ID_type version=0.0.1 uuid=6468845f-4122-4128-8e49-0f52c26078b5 description="A type for 'branch_ID' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/branchIDType.avsc"
LE

schema create name=item_ID_type version=0.0.1 uuid=4f227ff1-aee0-453a-b6b6-9a4b2e0da932 description="A type for 'item_ID' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/itemIDType.avsc"
LE

schema create name=message_type version=0.0.1 uuid=ad1431bb-3155-4e73-b5a3-b89bee498749 description="A type for 'message' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/messageType.avsc"
LE

schema create name=notes_type version=0.0.1 uuid=eecfde90-896c-4343-8f9c-2603ced94e2d description="A type for 'notes' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/notesType.avsc"
LE

schema create name=price_type version=0.0.1 uuid=52c2fc45-fd8c-463c-bd6f-d91b0554aea7 description="A type for 'amount'/'price' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/assistantIDType.avsc"
LE

schema create name=quantity_type version=0.0.1 uuid=ac3d9842-80af-4a98-951c-bd79a431c613 description="A type for 'quantity' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/quantityType.avsc"
LE

schema create name=sale_ID_type version=0.0.1 uuid=cca47d74-7754-4a61-b163-ca31f66b157b description="A type for 'sale_ID' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/saleIDType.avsc"
LE

schema create name=timestamp_type version=0.0.1 uuid=fd594e88-411d-4a94-b2be-697b3a0d7adf description="A type for 'time' values" flavour=Avro schema=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/4/avsc/timestampType.avsc"
LE

task create name=MorningBoozeCheck version=0.0.1 uuid=3351b0f4-cf06-4fa2-8823-edf67bd30223 description=LS
This task checks if the sales request is for an item that contains alcohol.
If the local time is between 00:00:00 and 11:30:00 then the sale is not authorised. Otherwise the sale is authorised.
In this implementation we assume that all items with item_ID values between 1000 and 2000 contain alcohol :-)
LE
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheck version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=authorised schemaName=authorised_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheck version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true

task logic create name=MorningBoozeCheck version=0.0.1 logicFlavour=MVEL logic=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/3/MorningBoozeCheck.mvel"
LE

task create name=BoozeConcessionCheck version=0.0.1 description=LS
This task checks if the approved sales item is eligible for concession
In this implementation we assume that all items with quantity greater than 2 is eligible for concession :-)
LE
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=authorised schemaName=authorised_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task inputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true
task outputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task outputfield create name=BoozeConcessionCheck version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true

task logic create name=BoozeConcessionCheck version=0.0.1 logicFlavour=MVEL logic=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/3/BoozeConcessionCheck.mvel"
LE

task create name=AlternateRecommendationCreate version=0.0.1 description=LS
This task checks if the approved sales item is rejected for sale
In this implementation we assume that all denied customers will be recommended to buy non alcoholic drinks :-)
LE
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=authorised schemaName=authorised_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task inputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true
task outputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task outputfield create name=AlternateRecommendationCreate version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true

task logic create name=AlternateRecommendationCreate version=0.0.1 logicFlavour=MVEL logic=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/3/AlternateRecommendationCreate.mvel"
LE

event create name=SALE_AUTH version=0.0.1 uuid=c4500941-3f98-4080-a9cc-5b9753ed050b description="An event emitted by the Policy to indicate whether the sale of an item has been authorised" nameSpace=com.hyperm source="APEX" target="POS"
event parameter create name=SALE_AUTH version=0.0.1 parName=amount schemaName=price_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=authorised schemaName=authorised_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=message schemaName=message_type schemaVersion=0.0.1 optional=true
event parameter create name=SALE_AUTH version=0.0.1 parName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
event parameter create name=SALE_AUTH version=0.0.1 parName=quantity schemaName=quantity_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
event parameter create name=SALE_AUTH version=0.0.1 parName=time schemaName=timestamp_type schemaVersion=0.0.1

event create name=SALE_INPUT version=0.0.1 uuid=4f04aa98-e917-4f4a-882a-c75ba5a99374 description="An event raised by the PoS system each time an item is scanned for purchase" nameSpace=com.hyperm source="POS" target="APEX"
event parameter create name=SALE_INPUT version=0.0.1 parName=amount schemaName=price_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
event parameter create name=SALE_INPUT version=0.0.1 parName=quantity schemaName=quantity_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
event parameter create name=SALE_INPUT version=0.0.1 parName=time schemaName=timestamp_type schemaVersion=0.0.1

event create name=SALE_CONCESSION version=0.0.1 description="An event raised by the PoS system each time an item is approved for sale" nameSpace=com.hyperm source="POS" target="APEX"
event parameter create name=SALE_CONCESSION version=0.0.1 parName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
event parameter create name=SALE_CONCESSION version=0.0.1 parName=message schemaName=message_type schemaVersion=0.0.1 optional=true

event create name=SALE_RECOMMEND version=0.0.1 description="An event raised by the PoS system each time an item is not approved for sale" nameSpace=com.hyperm source="POS" target="APEX"
event parameter create name=SALE_RECOMMEND version=0.0.1 parName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
event parameter create name=SALE_RECOMMEND version=0.0.1 parName=message schemaName=message_type schemaVersion=0.0.1 optional=true

policy create name=MyFirstPolicy version=0.0.1 description="This is my first Apex policy. It checks if a sale should be authorised or not." template=FREEFORM firstState=BoozeAuthDecide
policy state create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide triggerName=SALE_INPUT triggerVersion=0.0.1 defaultTaskName=MorningBoozeCheck defaultTaskVersion=0.0.1
policy state create name=MyFirstPolicy version=0.0.1 stateName=BoozeConcessionDecide triggerName=SALE_AUTH triggerVersion=0.0.1 defaultTaskName=BoozeConcessionCheck defaultTaskVersion=0.0.1
policy state create name=MyFirstPolicy version=0.0.1 stateName=AlternateRecommendation triggerName=SALE_AUTH triggerVersion=0.0.1 defaultTaskName=AlternateRecommendationCreate defaultTaskVersion=0.0.1
policy state output create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide outputName=MorningBoozeCheck_Output_Approved eventName=SALE_AUTH eventVersion=0.0.1 nextState=BoozeConcessionDecide
policy state output create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide outputName=MorningBoozeCheck_Output_Denied eventName=SALE_AUTH eventVersion=0.0.1 nextState=AlternateRecommendation

policy state finalizerlogic create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide finalizerLogicName=MorningBoozeCheck_Output_logic logicFlavour=JAVASCRIPT logic=LS
#MACROFILE:"src/main/resources/examples/models/MyFirstPolicy/3/MyFirstPolicy_SFL.js"
LE
policy state taskref create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide taskLocalName=MorningBoozeCheck taskName=MorningBoozeCheck taskVersion=0.0.1 outputType=LOGIC outputName=MorningBoozeCheck_Output_logic

policy state output create name=MyFirstPolicy version=0.0.1 stateName=BoozeConcessionDecide outputName=BoozeConcessionCheck_Output eventName=SALE_CONCESSION eventVersion=0.0.1 nextState=NULL
policy state taskref create name=MyFirstPolicy version=0.0.1 stateName=BoozeConcessionDecide taskLocalName=BoozeConcessionCheck taskName=BoozeConcessionCheck taskVersion=0.0.1 outputType=DIRECT outputName=BoozeConcessionCheck_Output

policy state output create name=MyFirstPolicy version=0.0.1 stateName=AlternateRecommendation outputName=AlternateRecommendationCreate_Output eventName=SALE_RECOMMEND eventVersion=0.0.1 nextState=NULL
policy state taskref create name=MyFirstPolicy version=0.0.1 stateName=AlternateRecommendation taskLocalName=AlternateRecommendationCreate taskName=AlternateRecommendationCreate taskVersion=0.0.1 outputType=DIRECT outputName=AlternateRecommendationCreate_Output

validate
quit