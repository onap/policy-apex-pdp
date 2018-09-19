/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.client.editor.rest.handling;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class carries out session handling for Apex REST editor sessions.
 */
public class RestSessionHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(RestSessionHandler.class);

    // The next session will have this number, stating at 0
    private AtomicInteger nextSessionId = new AtomicInteger();

    // All REST editor sessions being handled by this handler
    private final Map<Integer, RestSession> sessionMap = new TreeMap<>();

    /**
     * Create a new session.
     * @param result the result of session creation
     * @return the new session object
     */
    public RestSession createSession(ApexApiResult result) {
        LOGGER.entry("creating session");
        
        // Create the session with the next session ID
        final int newSessionId = nextSessionId.getAndIncrement();
        sessionMap.put(newSessionId, new RestSession(newSessionId));

        result.addMessage(Integer.toString(newSessionId));
        
        LOGGER.exit("created session with ID: " + newSessionId);
        return sessionMap.get(newSessionId);
    }

    /**
     * Get a session for the given session ID.
     * @param sessionId the session ID of the session we require
     * @param result the result of the session get
     * @return the session
     */
    public RestSession getSession(final int sessionId, ApexApiResult result) {
        LOGGER.entry("finding session: " + sessionId);

        // Check for valid session IDs
        if (sessionId < 0) {
            result.setResult(Result.FAILED);
            result.addMessage("Session ID  \"" + sessionId + "\" is negative");
            LOGGER.exit(result.getMessage());
            return null;
        }

        // Check if session exits
        if (!sessionMap.containsKey(sessionId)) {
            result.setResult(Result.FAILED);
            result.addMessage("A session with session ID \"" + sessionId + "\" does not exist");
            LOGGER.exit(result.getMessage());
            return null;
        }
        
        RestSession session = sessionMap.get(sessionId);
        
        // Check if session is valid
        if (session == null) {
            result.setResult(Result.FAILED);
            result.addMessage("The session with session ID \"" + sessionId + "\" is corrupt");
            LOGGER.exit(result.getMessage());
            return null;
        }
        
        // Return the session
        LOGGER.exit("session found: " + sessionId);
        return session;
    }

    /*
     * This is a test method to set a corrupt session ID in the session map
     * @param corruptSessionId the ID of the corrupt session
     */
    protected void setCorruptSession(int corruptSessionId) {
        sessionMap.put(corruptSessionId, null);
    }
}
