/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2017 - 2019 Bell Canada.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.plugins.event.carrier.cds;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import org.onap.ccsdk.cds.controllerblueprints.common.api.ActionIdentifiers;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.BluePrintProcessingServiceGrpc;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.BluePrintProcessingServiceGrpc.BluePrintProcessingServiceStub;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CdsProcessingHandler {

    private static final Logger log = LoggerFactory.getLogger(CdsProcessingHandler.class);

    private CdsProcessingListener listener;

    CdsProcessingHandler(final CdsProcessingListener listener) {
        this.listener = listener;
    }

    CountDownLatch process(ExecutionServiceInput request, ManagedChannel channel) {

        ActionIdentifiers header = request.getActionIdentifiers();

        log.info("Processing blueprint({}:{}) for action({})", header.getBlueprintVersion(), header.getBlueprintName(),
                header.getBlueprintVersion());

        final CountDownLatch finishLatch = new CountDownLatch(1);

        final BluePrintProcessingServiceStub asyncStub = BluePrintProcessingServiceGrpc.newStub(channel);

        final StreamObserver<ExecutionServiceOutput> responseObserver = new StreamObserver<ExecutionServiceOutput>() {
            @Override
            public void onNext(ExecutionServiceOutput output) {
                listener.onMessage(output);
            }

            @Override
            public void onError(Throwable throwable) {
                listener.onError(throwable);
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Completed blueprint({}:{}) for action({})", header.getBlueprintVersion(),
                        header.getBlueprintName(), header.getBlueprintVersion());
                finishLatch.countDown();
            }
        };

        final StreamObserver<ExecutionServiceInput> requestObserver = asyncStub.process(responseObserver);

        try {
            // Send our message to CDS backend for processing
            requestObserver.onNext(request);
            // Mark the end of requests
            requestObserver.onCompleted();
        } catch (RuntimeException e) {
            requestObserver.onError(e);
        }
        return finishLatch;
    }
}
