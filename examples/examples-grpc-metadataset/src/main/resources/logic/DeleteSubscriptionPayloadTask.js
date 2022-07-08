/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix. All rights reserved.
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

var pmSubscriptionInfo = executor.getContextAlbum("PMSubscriptionAlbum").get(executor.inFields.get("albumID").toString())

var payloadProperties = executor.subject.getOutFieldSchemaHelper("payload").createNewSubInstance("delete_DasH_subscription_DasH_properties_record");

payloadProperties.put("nfName",  pmSubscriptionInfo.get("nfName"))
payloadProperties.put("subscriptionName",  pmSubscriptionInfo.get("subscription").get("subscriptionName"))
payloadProperties.put("administrativeState", pmSubscriptionInfo.get("subscription").get("administrativeState"))
payloadProperties.put("fileBasedGP",  pmSubscriptionInfo.get("subscription").get("fileBasedGP").toString())
payloadProperties.put("fileLocation", pmSubscriptionInfo.get("subscription").get("fileLocation"))
payloadProperties.put("measurementGroups", pmSubscriptionInfo.get("subscription").get("measurementGroups"))

var payloadEntry = executor.subject.getOutFieldSchemaHelper("payload").createNewSubInstance("CDSRequestPayloadEntry");
payloadEntry.put("delete_DasH_subscription_DasH_properties", payloadProperties)

var payload = executor.subject.getOutFieldSchemaHelper("payload").createNewInstance();
payload.put("delete_DasH_subscription_DasH_request", payloadEntry);

executor.outFields.put("albumID", executor.inFields.get("albumID"))
executor.outFields.put("payload", payload);

executor.logger.info("Sending delete Subscription Event to CDS")

true;
