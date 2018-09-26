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

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var genericDataRecordType = Java.type("org.apache.avro.generic.GenericData.Record");

var vcpeClosedLoopStatus = executor.getContextAlbum("VCPEClosedLoopStatusAlbum").get(
        executor.inFields.get("vnfID").toString());

var appcBodyRecord = executor.subject.getOutFieldSchemaHelper("body").createNewInstance();
var appcBodyRecordSchema = appcBodyRecord.getSchema();

var inputRecord = new genericDataRecordType(appcBodyRecordSchema.getField("input").schema());
var inputRecordRecordSchema = inputRecord.getSchema();

var actionIndentifiersRecord = new genericDataRecordType(inputRecordRecordSchema.getField("action_DasH_identifiers")
        .schema());

var commonHeaderRecord = new genericDataRecordType(inputRecordRecordSchema.getField("common_DasH_header").schema());
var commonHeaderRecordSchema = commonHeaderRecord.getSchema();

var commonHeaderFlagsRecord = new genericDataRecordType(commonHeaderRecordSchema.getField("flags").schema());

appcBodyRecord.put("input", inputRecord);
inputRecord.put("action_DasH_identifiers", actionIndentifiersRecord);
inputRecord.put("common_DasH_header", commonHeaderRecord);
commonHeaderRecord.put("flags", commonHeaderFlagsRecord);

inputRecord.put("action", "Restart");
inputRecord.put("payload", "{}");

actionIndentifiersRecord.put("vnf_DasH_id", executor.inFields.get("vnfID").toString());

commonHeaderRecord.put("timestamp", new Date().toISOString());
commonHeaderRecord.put("api_DasH_ver", "2.00");
commonHeaderRecord.put("originator_DasH_id", executor.inFields.get("requestID").toString());
commonHeaderRecord.put("request_DasH_id", executor.inFields.get("requestID").toString());
commonHeaderRecord.put("sub_DasH_request_DasH_id", "1");

commonHeaderFlagsRecord.put("ttl", "10000");
commonHeaderFlagsRecord.put("force", "TRUE");
commonHeaderFlagsRecord.put("mode", "EXCLUSIVE");

executor.outFields.put("version", "2.0");
executor.outFields.put("rpc-name", "restart");
executor.outFields.put("correlation-id", executor.inFields.get("vnfID"));
executor.outFields.put("type", "request");
executor.outFields.put("body", appcBodyRecord);

executor.getContextAlbum("RequestIDVNFIDAlbum").put(executor.inFields.get("requestID").toString(),
        executor.inFields.get("vnfID"));

vcpeClosedLoopStatus.put("notification", "OPERATION: RESTART REQUESTED");

executor.logger.info(executor.outFields);

var returnValue = executor.isTrue;
