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

// Load compatibility script for imports etc 
load("nashorn:mozilla_compat.js");
importPackage(java.text);
importClass(java.text.SimpleDateFormat);

executor.logger.info("Task Execution: '"+executor.subject.id+"'. Input Fields: '"+executor.inFields+"'");

executor.outFields.put("amount"      , executor.inFields.get("amount"));
executor.outFields.put("assistant_ID", executor.inFields.get("assistant_ID"));
executor.outFields.put("notes"       , executor.inFields.get("notes"));
executor.outFields.put("quantity"    , executor.inFields.get("quantity"));
executor.outFields.put("branch_ID"   , executor.inFields.get("branch_ID"));
executor.outFields.put("item_ID"     , executor.inFields.get("item_ID"));
executor.outFields.put("time"        , executor.inFields.get("time"));
executor.outFields.put("sale_ID"     , executor.inFields.get("sale_ID"));

item_id = executor.inFields.get("item_ID");

//All times in this script are in GMT/UTC since the policy and events assume time is in GMT. 
var timenow_gmt =  new Date(Number(executor.inFields.get("time")));

var midnight_gmt = new Date(Number(executor.inFields.get("time")));
midnight_gmt.setUTCHours(0,0,0,0);

var eleven30_gmt = new Date(Number(executor.inFields.get("time")));
eleven30_gmt.setUTCHours(11,30,0,0);

var timeformatter = new java.text.SimpleDateFormat("HH:mm:ss z");

var itemisalcohol = false;
if(item_id != null && item_id >=1000 && item_id < 2000)
    itemisalcohol = true;
    
if( itemisalcohol
    && timenow_gmt.getTime() >= midnight_gmt.getTime()
    && timenow_gmt.getTime() <  eleven30_gmt.getTime()) {

  executor.outFields.put("authorised", false);
  executor.outFields.put("message", "Sale not authorised by policy task " +
    executor.subject.taskName+ " for time " + timeformatter.format(timenow_gmt.getTime()) +
    ". Alcohol can not be sold between " + timeformatter.format(midnight_gmt.getTime()) +
    " and " + timeformatter.format(eleven30_gmt.getTime()));
}
else{
  executor.outFields.put("authorised", true);
  executor.outFields.put("message", "Sale authorised by policy task " + 
    executor.subject.taskName + " for time "+timeformatter.format(timenow_gmt.getTime()));
}

/*
This task checks if a sale request is for an item that is an alcoholic drink.
If the local time is between 00:00:00 GMT and 11:30:00 GMT then the sale is not
authorised. Otherwise the sale is authorised. 
In this implementation we assume that items with item_ID value between 1000 and 
2000 are all alcoholic drinks :-)
*/
