/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.domains.onap.vcpe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import org.onap.policy.appclcm.AppcLcmBody;
import org.onap.policy.appclcm.AppcLcmDmaapWrapper;
import org.onap.policy.appclcm.AppcLcmInput;
import org.onap.policy.appclcm.AppcLcmOutput;
import org.onap.policy.controlloop.util.Serialization;

/**
 * Respond to an APPC request with a given delay.
 */
public class AppcResponseCreator {
    // The request from APPC
    private final String jsonRequestString;

    // The queue for APPC responses
    private final BlockingQueue<String> appcResponseQueue;

    // The timer task for response generation
    private final Timer appcTimer;

    private static final Gson gson =
            new GsonBuilder().registerTypeAdapter(Instant.class, new Serialization.GsonInstantAdapter()).create();

    /**
     * Respond to the given APPC request after the given amount of milliseconds.
     *
     * @param appcResponseQueue the queue into which to put the APPC response
     * @param jsonRequestString the request JSON string
     * @param milliSecondsToWait the number of milliseconds to wait
     */
    public AppcResponseCreator(BlockingQueue<String> appcResponseQueue, String jsonRequestString,
            long milliSecondsToWait) {
        this.jsonRequestString = jsonRequestString;
        this.appcResponseQueue = appcResponseQueue;

        appcTimer = new Timer();
        appcTimer.schedule(new AppcTimerTask(), milliSecondsToWait);
    }

    private class AppcTimerTask extends TimerTask {
        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {

            AppcLcmDmaapWrapper requestWrapper = null;
            requestWrapper = gson.fromJson(jsonRequestString, AppcLcmDmaapWrapper.class);

            AppcLcmInput request = requestWrapper.getBody().getInput();

            AppcLcmOutput response = new AppcLcmOutput(request);
            response.getStatus().setCode(400);
            response.getStatus().setMessage("Restart Successful");
            response.setPayload("");

            AppcLcmDmaapWrapper responseWrapper = new AppcLcmDmaapWrapper();
            responseWrapper.setBody(new AppcLcmBody());
            responseWrapper.getBody().setOutput(response);

            responseWrapper.setVersion(requestWrapper.getVersion());
            responseWrapper.setRpcName(requestWrapper.getRpcName());
            responseWrapper.setCorrelationId(requestWrapper.getCorrelationId());
            responseWrapper.setType(requestWrapper.getType());

            appcResponseQueue.add(gson.toJson(responseWrapper));
        }
    }
}
