/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation. All rights reserved.
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
executor.logger.info("finalizer execution: " + executor.subject.id);

if (executor.fields.get("authorised") == true) {
  executor.logger.info("finalizer if block ");
  executor.setSelectedStateOutputName("MorningBoozeCheck_Output_Approved");
} else {
  executor.logger.info("finalizer else block ");
  executor.setSelectedStateOutputName("MorningBoozeCheck_Output_Denied");
}

executor.logger.info("finalizer outputs ##2##: " + executor.stateOutputNames);
executor.logger.info(
    "finalizer outputs:##2##: ##" + executor.selectedStateOutputName + "##");

true;

/*
State Finaliser logic to decide the next state on the basis if the sale is approved or denied.
If the sale is approved, MorningBoozeCheck_Output_Approved is set as the state output.
If the sale is rejected, MorningBoozeCheck_Output_Denied is set as the state output.
*/
