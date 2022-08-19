/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var msgResponse = executor.inFields.get('DmaapResponseEvent');
executor.logger.info('Task in progress with mesages: ' + msgResponse);

var elementId = msgResponse.get('elementId').get('name');

if (msgResponse.get('messageType') == 'STATUS' &&
    (elementId == 'onap.policy.clamp.ac.startertobridge'
    || elementId == 'onap.policy.clamp.ac.bridgetosink')) {

    var receiverId = '';
    if (elementId == 'onap.policy.clamp.ac.startertobridge') {
        receiverId = 'onap.policy.clamp.ac.bridge';
    } else {
        receiverId = 'onap.policy.clamp.ac.sink';
    }

    var elementIdResponse = new java.util.HashMap();
    elementIdResponse.put('name', receiverId);
    elementIdResponse.put('version', msgResponse.get('elementId').get('version'));

    var dmaapResponse = new java.util.HashMap();
    dmaapResponse.put('elementId', elementIdResponse);

    var message = msgResponse.get('message') + ' trace added from policy';
    dmaapResponse.put('message', message);
    dmaapResponse.put('messageType', 'STATUS');
    dmaapResponse.put('messageId', msgResponse.get('messageId'));
    dmaapResponse.put('timestamp', msgResponse.get('timestamp'));

    executor.logger.info('Sending forwarding Event to Ac element: ' + dmaapResponse);

    executor.outFields.put('DmaapResponseStatusEvent', dmaapResponse);
}

true;
