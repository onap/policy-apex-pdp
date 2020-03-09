/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import lombok.Getter;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This class implements an Apex gRPC consumer. The consumer is used by it's peer gRPC producer to consume response
 * events.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexGrpcConsumer extends ApexPluginsEventConsumer {

    // The event receiver that will receive events from this consumer
    @Getter
    private ApexEventReceiver eventReceiver;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
        final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the gRPC Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof GrpcCarrierTechnologyParameters)) {
            final String errorMessage =
                "specified consumer properties are not applicable to the gRPC consumer (" + this.name + ")";
            throw new ApexEventException(errorMessage);
        }

        GrpcCarrierTechnologyParameters grpcConsumerParameters =
            (GrpcCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();
        grpcConsumerParameters.validateGrpcParameters(false);
        // Check if we are in peered mode
        if (!consumerParameters.isPeeredMode(EventHandlerPeeredMode.REQUESTOR)) {
            final String errorMessage =
                "gRPC consumer (" + this.name + ") must run in peered requestor mode with a gRPC producer";
            throw new ApexEventException(errorMessage);
        }
    }

    @Override
    public void stop() {
        // For gRPC requests, all the implementation is in the producer
    }

    @Override
    public void run() {
        // For gRPC requests, all the implementation is in the producer
    }

}
