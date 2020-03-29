/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.examples.grpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.onap.ccsdk.cds.controllerblueprints.processing.api.BluePrintProcessingServiceGrpc.BluePrintProcessingServiceImplBase;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceOutput;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceOutput.Builder;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

/**
 * The Class GrpcTestDummyGrpcServer creates a dummy gRPC server to mimic a CDS implementation.
 */
public class GrpcTestDummyGrpcServer {
    private Server server;

    /**
     * Dummy server for gRPC.
     *
     * @param host hostname of the server
     * @param port port of the server
     */
    public GrpcTestDummyGrpcServer(String host, int port) {
        // Implement the dummy gRPC server
        BluePrintProcessingServiceImplBase testCdsBlueprintServerImpl = new BluePrintProcessingServiceImplBase() {
            @Override
            public StreamObserver<ExecutionServiceInput>
                process(final StreamObserver<ExecutionServiceOutput> responseObserver) {
                return new StreamObserver<ExecutionServiceInput>() {
                    @Override
                    public void onNext(final ExecutionServiceInput executionServiceInput) {
                        String responseString = "";
                        try {
                            responseString = Files.readString(Paths.get(
                                "src/main/resources/examples/events/APEXgRPC/CreateSubscriptionResponseEvent.json"));
                        } catch (IOException e) {
                            throw new ApexEventRuntimeException("Cannot read executionServiceOutput from file", e);
                        }
                        ExecutionServiceOutput executionServiceOutput;
                        Builder builder = ExecutionServiceOutput.newBuilder();
                        try {
                            JsonFormat.parser().ignoringUnknownFields().merge(responseString, builder);
                            executionServiceOutput = builder.build();
                            responseObserver.onNext(executionServiceOutput);
                        } catch (InvalidProtocolBufferException e) {
                            throw new ApexEventRuntimeException(
                                "Output string cannot be converted to ExecutionServiceOutput type for gRPC request."
                                    + e);
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        responseObserver.onError(throwable);
                    }

                    @Override
                    public void onCompleted() {
                        responseObserver.onCompleted();
                    }
                };
            }
        };
        server = NettyServerBuilder.forAddress(new InetSocketAddress(host, port)).addService(testCdsBlueprintServerImpl)
            .build();
    }

    public void start() throws IOException {
        server.start();
    }

    public void stop() {
        server.shutdown();
    }
}
