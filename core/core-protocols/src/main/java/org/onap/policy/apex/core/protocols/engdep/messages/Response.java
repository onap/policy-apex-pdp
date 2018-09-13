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
    private static final int HASH_PRIME = 31;

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

    /**
     * Compare this message to another Response message.
     *
     * @param otherMessage the other message
     * @return true, if successful
     */
    public boolean equals(final Response otherMessage) {
        return super.equals(otherMessage) && successful == otherMessage.successful
                && responseTo.equals(otherMessage.responseTo);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.protocols.Message#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        final Response response = (Response) object;

        if (successful != response.successful) {
            return false;
        }
        return !(responseTo != null ? !responseTo.equals(response.responseTo) : response.responseTo != null);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.protocols.Message#hashCode()
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HASH_PRIME * result + (successful ? 1 : 0);
        result = HASH_PRIME * result + (responseTo != null ? responseTo.hashCode() : 0);
        return result;
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
