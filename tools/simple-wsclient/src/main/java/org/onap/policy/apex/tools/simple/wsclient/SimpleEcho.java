/*-
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

package org.onap.policy.apex.tools.simple.wsclient;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.Validate;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Simple WS client as an echo.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class SimpleEcho extends WebSocketClient {

    /** Application name, used as prompt. */
    private final String appName;

    // Output and error streams
    private PrintStream outStream;
    private PrintStream errStream;

    /**
     * Creates a new simple echo object.
     *
     * @param server the name of the server as either IP address or fully qualified host name, must not be blank
     * @param port the port to be used, must not be blank
     * @param appName the application name, used as prompt, must not be blank
     * @param outStream the stream for message output
     * @param errStream the stream for error messages
     * @throws URISyntaxException is URI could not be created from server/port settings
     * @throws RuntimeException if server or port where blank
     */
    public SimpleEcho(final String server, final String port, final String appName, PrintStream outStream,
                    PrintStream errStream) throws URISyntaxException {
        super(new URI("ws://" + server + ":" + port));
        Validate.notBlank(appName, "SimpleEcho: given application name was blank");
 
        this.appName = appName;
        this.outStream = outStream;
        this.errStream = errStream;
    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {
        outStream.println(this.appName + ": Connection closed by " + (remote ? "APEX" : "me"));
        outStream.print(" ==-->> ");
        switch (code) {
            case CloseFrame.NORMAL:
                outStream.println("normal");
                break;
            case CloseFrame.GOING_AWAY:
                outStream.println("APEX going away");
                break;
            case CloseFrame.PROTOCOL_ERROR:
                outStream.println("some protocol error");
                break;
            case CloseFrame.REFUSE:
                outStream.println("received unacceptable type of data");
                break;
            case CloseFrame.NO_UTF8:
                outStream.println("expected UTF-8, found something else");
                break;
            case CloseFrame.TOOBIG:
                outStream.println("message too big");
                break;
            case CloseFrame.UNEXPECTED_CONDITION:
                outStream.println("unexpected server condition");
                break;
            default:
                outStream.println("unkown close frame code");
                break;
        }
        outStream.print(" ==-->> " + reason);
    }

    @Override
    public void onError(final Exception ex) {
        errStream.println(this.appName + ": " + ex.getMessage());
    }

    @Override
    public void onMessage(final String message) {
        outStream.println(this.appName + ": received");
        outStream.println("---------------------------------");
        outStream.println(message);
        outStream.println("=================================");
        outStream.println();
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        outStream.println(this.appName + ": opened connection to APEX (" + handshakedata.getHttpStatusMessage() + ")");
    }

}
