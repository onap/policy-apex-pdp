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

model create name=MyFirstPolicyModel version=0.0.1 uuid=540226fb-55ee-4f0e-a444-983a0494818e description="This is my first Apex Policy Model."

schema create name=assistant_ID_type version=0.0.1 uuid=36df4c71-9616-4206-8b53-976a5cd4bd87 description="A type for 'assistant_ID' values" flavour=Java schema=java.lang.Long

schema create name=authorised_type version=0.0.1 uuid=d48b619e-d00d-4008-b884-02d76ea4350b description="A type for 'authorised' values" flavour=Java schema=java.lang.Boolean

schema create name=branch_ID_type version=0.0.1 uuid=6468845f-4122-4128-8e49-0f52c26078b5 description="A type for 'branch_ID' values" flavour=Java schema=java.lang.Long

schema create name=item_ID_type version=0.0.1 uuid=4f227ff1-aee0-453a-b6b6-9a4b2e0da932 description="A type for 'item_ID' values" flavour=Java schema=java.lang.Long

schema create name=message_type version=0.0.1 uuid=ad1431bb-3155-4e73-b5a3-b89bee498749 description="A type for 'message' values" flavour=Java schema=java.lang.String

schema create name=notes_type version=0.0.1 uuid=eecfde90-896c-4343-8f9c-2603ced94e2d description="A type for 'notes' values" flavour=Java schema=java.lang.String

schema create name=price_type version=0.0.1 uuid=52c2fc45-fd8c-463c-bd6f-d91b0554aea7 description="A type for 'amount'/'price' values" flavour=Java schema=java.lang.Long

schema create name=quantity_type version=0.0.1 uuid=ac3d9842-80af-4a98-951c-bd79a431c613 description="A type for 'quantity' values" flavour=Java schema=java.lang.Integer

schema create name=sale_ID_type version=0.0.1 uuid=cca47d74-7754-4a61-b163-ca31f66b157b description="A type for 'sale_ID' values" flavour=Java schema=java.lang.Long

schema create name=timestamp_type version=0.0.1 uuid=fd594e88-411d-4a94-b2be-697b3a0d7adf description="A type for 'time' values" flavour=Java schema=java.lang.Long

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
/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

logger.info("Task Execution: '"+subject.id+"'. Input Fields: '"+inFields+"'");

outFields.put("amount"      , inFields.get("amount"));
outFields.put("assistant_ID", inFields.get("assistant_ID"));
outFields.put("notes"       , inFields.get("notes"));
outFields.put("quantity"    , inFields.get("quantity"));
outFields.put("branch_ID"   , inFields.get("branch_ID"));
outFields.put("item_ID"     , inFields.get("item_ID"));
outFields.put("time"        , inFields.get("time"));
outFields.put("sale_ID"     , inFields.get("sale_ID"));

item_id = inFields.get("item_ID");

//The events used later to test this task use GMT timezone!
gmt = TimeZone.getTimeZone("GMT");
timenow = Calendar.getInstance(gmt);
df = new SimpleDateFormat("HH:mm:ss z");
df.setTimeZone(gmt);
timenow.setTimeInMillis(inFields.get("time"));

midnight = timenow.clone();
midnight.set(
    timenow.get(Calendar.YEAR),timenow.get(Calendar.MONTH),
    timenow.get(Calendar.DATE),0,0,0);
eleven30 = timenow.clone();
eleven30.set(
    timenow.get(Calendar.YEAR),timenow.get(Calendar.MONTH),
    timenow.get(Calendar.DATE),11,30,0);

itemisalcohol = false;
if(item_id != null && item_id >=1000 && item_id < 2000)
    itemisalcohol = true;

if( itemisalcohol
    && timenow.after(midnight) && timenow.before(eleven30)){
  outFields.put("authorised", false);
  outFields.put("message", "Sale not authorised by policy task "+subject.taskName+
    " for time "+df.format(timenow.getTime())+
    ". Alcohol can not be sold between "+df.format(midnight.getTime())+
    " and "+df.format(eleven30.getTime()));
  return true;
}
else{
  outFields.put("authorised", true);
  outFields.put("message", "Sale authorised by policy task "+subject.taskName+
    " for time "+df.format(timenow.getTime()));
  return true;
}

/*
This task checks if a sale request is for an item that is an alcoholic drink.
If the local time is between 00:00:00 GMT and 11:30:00 GMT then the sale is not
authorised. Otherwise the sale is authorised. 
In this implementation we assume that items with item_ID value between 1000 and 
2000 are all alcoholic drinks :-)
*/
LE

task create name=MorningBoozeCheckAlt1 version=0.0.1 uuid=bc6d90c9-c902-4686-afd3-925b30e39990 description=LS
This task checks if a sale request is for an item that is an alcoholic drink. 
If the local time is between 00:00:00 CET and 13:00:00 CET then the sale is not authorised. 
Also alcohol sales are not allowed on Sundays. Otherwise the sale is authorised. 
In this implementation we assume that items with item_ID between 1000 and 2000 are all alcoholic drinks
LE
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task inputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=sale_ID schemaName=sale_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=amount schemaName=price_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=assistant_ID schemaName=assistant_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=notes schemaName=notes_type schemaVersion=0.0.1 optional=true
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=quantity schemaName=quantity_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=branch_ID schemaName=branch_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=item_ID schemaName=item_ID_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=authorised schemaName=authorised_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=time schemaName=timestamp_type schemaVersion=0.0.1
task outputfield create name=MorningBoozeCheckAlt1 version=0.0.1 fieldName=message schemaName=message_type schemaVersion=0.0.1 optional=true
task logic create name=MorningBoozeCheckAlt1 version=0.0.1 logicFlavour=MVEL logic=LS
/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

logger.info("Task Execution: '"+subject.id+"'. Input Event: '"+inFields+"'");

outFields.put("amount"      , inFields.get("amount"));
outFields.put("assistant_ID", inFields.get("assistant_ID"));
outFields.put("notes"       , inFields.get("notes"));
outFields.put("quantity"    , inFields.get("quantity"));
outFields.put("branch_ID"   , inFields.get("branch_ID"));
outFields.put("item_ID"     , inFields.get("item_ID"));
outFields.put("time"        , inFields.get("time"));
outFields.put("sale_ID"     , inFields.get("sale_ID"));

item_id = inFields.get("item_ID");

//The events used later to test this task use CET timezone!
cet = TimeZone.getTimeZone("CET");
timenow = Calendar.getInstance(cet);
df = new SimpleDateFormat("HH:mm:ss z");
df.setTimeZone(cet);
timenow.setTimeInMillis(inFields.get("time"));

midnight = timenow.clone();
midnight.set(
    timenow.get(Calendar.YEAR),timenow.get(Calendar.MONTH),
    timenow.get(Calendar.DATE),0,0,0);
onepm = timenow.clone();
onepm.set(
    timenow.get(Calendar.YEAR),timenow.get(Calendar.MONTH),
    timenow.get(Calendar.DATE),13,0,0);

itemisalcohol = false;
if(item_id != null && item_id >=1000 && item_id < 2000)
    itemisalcohol = true;

if( itemisalcohol &&
    ( (timenow.after(midnight) && timenow.before(onepm))
      ||
      (timenow.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
    )){
  outFields.put("authorised", false);
  outFields.put("message", "Sale not authorised by policy task "+subject.taskName+
    " for time "+df.format(timenow.getTime())+
    ". Alcohol can not be sold between "+df.format(midnight.getTime())+
    " and "+df.format(onepm.getTime()) +" or on Sunday");
  return true;
}
else{
  outFields.put("authorised", true);
  outFields.put("message", "Sale authorised by policy task "+subject.taskName+
    " for time "+df.format(timenow.getTime()));
  return true;
}

/*
This task checks if a sale request is for an item that is an alcoholic drink. 
If the local time is between 00:00:00 CET and 13:00:00 CET then the sale is not authorised. 
Also alcohol sales are not allowed on Sundays. Otherwise the sale is authorised. 
In this implementation we assume that items with item_ID between 1000 and 2000 are all alcoholic drinks :-)
*/
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


policy create name=MyFirstPolicy version=0.0.1 uuid=6c5e410f-489a-46ff-964e-982ce6e8b6d0 description="This is my first Apex policy. It checks if a sale should be authorised or not." template=FREEFORM firstState=BoozeAuthDecide
policy state create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide triggerName=SALE_INPUT triggerVersion=0.0.1 defaultTaskName=MorningBoozeCheck defaultTaskVersion=0.0.1
policy state output create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide outputName=MorningBoozeCheck_Output_Direct eventName=SALE_AUTH eventVersion=0.0.1 nextState=NULL
policy state taskref create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide taskLocalName=MorningBoozeCheckAlt1 taskName=MorningBoozeCheckAlt1 taskVersion=0.0.1 outputType=DIRECT outputName=MorningBoozeCheck_Output_Direct
policy state taskref create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide taskLocalName=MorningBoozeCheck taskName=MorningBoozeCheck taskVersion=0.0.1 outputType=DIRECT outputName=MorningBoozeCheck_Output_Direct
policy state selecttasklogic create name=MyFirstPolicy version=0.0.1 stateName=BoozeAuthDecide logicFlavour=JAVASCRIPT logic=LS
/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

var returnValueType = Java.type("java.lang.Boolean");
var returnValue = new returnValueType(true);

executor.logger.info("Task Selection Execution: '"+executor.subject.id+"'. Input Event: '"+executor.inFields+"'");

branchid = executor.inFields.get("branch_ID");
taskorig = executor.subject.getTaskKey("MorningBoozeCheck");
taskalt = executor.subject.getTaskKey("MorningBoozeCheckAlt1");
taskdef = executor.subject.getDefaultTaskKey();

if(branchid >=0 && branchid <1000){
  taskorig.copyTo(executor.selectedTask);
}
else if (branchid >=1000 && branchid <2000){
  taskalt.copyTo(executor.selectedTask);
}
else{
  taskdef.copyTo(executor.selectedTask);
}

/*
This task selection logic selects task "MorningBoozeCheck" for branches with 0<=branch_ID<1000 and selects task "MorningBoozeCheckAlt1" for branches with 1000<=branch_ID<2000. Otherwise the default task is selected. In this case the default task is also "MorningBoozeCheck"
*/
LE



