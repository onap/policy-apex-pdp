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

package org.onap.policy.apex.core.protocols.engdep.messages;

import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.EngDepAction;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class Response is a message that holds the response by an Apex engine to another Actino message sent to that
 * engine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class Response extends Message {
    private static final long serialVersionUID = -4162385039044294476L;

    private boolean successful = false;
    private Message responseTo = null;

    /**
     * Instantiates a new Response message.
     *
     * @param targetKey the target key of the entity that asked for the action that triggered this response message
     * @param successful the successful if the action in the triggering message worked
     * @param responseTo the message to which this message is a response
     */
    public Response(final AxArtifactKey targetKey, final boolean successful, final Message responseTo) {
        this(targetKey, successful, null, responseTo);
    }

    /**
     * Instantiates a new Response message.
     *
     * @param targetKey the target key of the entity that asked for the action that triggered this response message
     * @param successful the successful if the action in the triggering message worked
     * @param messageData the message data which may indicate specific conditions for the response
     * @param responseTo the message to which this message is a response
     */
    public Response(final AxArtifactKey targetKey, final boolean successful, final String messageData,
            final Message responseTo) {
        super(EngDepAction.RESPONSE, targetKey, messageData);
        this.successful = successful;
        this.responseTo = responseTo;
    }

    /**
     * Checks if the action to which this is a response was successful.
     *
     * @return true, if is successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the message to which this message is a response to.
     *
     * @return the the message to which this message is a response to
     */
    public Message getResponseTo() {
        return responseTo;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((responseTo == null) ? 0 : responseTo.hashCode());
        result = prime * result + (successful ? 1231 : 1237);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }

        Response other = (Response) obj;
        if (responseTo == null) {
            if (other.responseTo != null) {
                return false;
            }
        } else if (!responseTo.equals(other.responseTo)) {
            return false;
        }
        return successful == other.successful;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.model.protocols.Message#toString()
     */
    @Override
    public String toString() {
        return "Response {" + super.toString() + "}[successful=" + successful + "]";
    }
}
