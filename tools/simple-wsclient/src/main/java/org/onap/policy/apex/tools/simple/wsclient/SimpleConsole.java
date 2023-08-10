/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2023 Nordix Foundation.
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

package org.onap.policy.apex.tools.simple.wsclient;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import org.apache.commons.lang3.Validate;

/**
 * Simple WS client with a console for events.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
@ClientEndpoint
public class SimpleConsole {

    /** Application name, used as prompt. */
    private final String appName;

    // Output and error streams
    private PrintStream outStream;
    private PrintStream errStream;
    // The Websocket Session
    private Session userSession;

    /**
     * Creates a new simple echo object.
     *
     * @param server the name of the server as either IP address or fully qualified host name, must not be blank
     * @param port the port to be used, must not be blank
     * @param appName the application name, used as prompt, must not be blank
     * @param outStream the stream for message output
     * @param errStream the stream for error messages
     * @throws URISyntaxException is URI could not be created from server/port settings
     * @throws IOException on IO exceptions
     * @throws DeploymentException on deployment exceptions
     * @throws RuntimeException if server or port where blank
     */
    public SimpleConsole(final String server, final String port, final String appName, PrintStream outStream,
                         PrintStream errStream) throws URISyntaxException, DeploymentException, IOException {
        Validate.notBlank(appName, "SimpleConsole: given application name was blank");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI("ws://" + server + ":" + port));

        this.appName = appName;
        this.outStream = outStream;
        this.errStream = errStream;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        outStream.println(this.appName + ": Connection closed");

        outStream.print(" ==-->> ");
        CloseCodes reasonCloseCode = CloseCodes.valueOf(reason.getCloseCode().toString());
        switch (reasonCloseCode) {
            case NORMAL_CLOSURE:
                outStream.println("normal");
                break;
            case GOING_AWAY:
                outStream.println("APEX going away");
                break;
            case PROTOCOL_ERROR:
                outStream.println("some protocol error");
                break;
            case CANNOT_ACCEPT:
                outStream.println("received unacceptable type of data");
                break;
            case CLOSED_ABNORMALLY:
                outStream.println("expected UTF-8, found something else");
                break;
            case TOO_BIG:
                outStream.println("message too big");
                break;
            case UNEXPECTED_CONDITION:
                outStream.println("unexpected server condition");
                break;
            default:
                outStream.println("unkown close frame code");
                break;
        }
        outStream.print(" ==-->> " + reason);
    }


    @OnError
    public void onError(final Exception ex) {
        errStream.println(this.appName + ": " + ex.getMessage());
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        // this client does not expect messages
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        outStream.println(this.appName + ": opened connection to APEX (" + userSession.getRequestURI() + ")");

        this.userSession = userSession;
    }

    /**
     * Runs the console client. In particular, it starts a new thread for the Websocket connection and then reads from
     * standard input.
     *
     * @throws NotYetConnectedException if not connected to server when sending events
     * @throws IOException on an IO problem on standard in
     */
    public void runClient() throws IOException {
        final var in = new BufferedReader(new InputStreamReader(System.in));
        var event = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            if ("exit".equals(line)) {
                break;
            }

            final String current = line.trim();
            if ("".equals(current)) {
                this.userSession.getBasicRemote().sendText(event.toString());
                event = new StringBuilder();
            } else {
                event.append(current);
            }
        }

        this.userSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Normal Closure"));
    }


}
