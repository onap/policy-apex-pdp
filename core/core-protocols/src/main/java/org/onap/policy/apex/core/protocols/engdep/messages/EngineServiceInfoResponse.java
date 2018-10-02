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

import java.util.Arrays;
import java.util.Collection;

import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class Response is a message that holds the response by an Apex engine to another Actino message sent to that
 * engine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineServiceInfoResponse extends Response {
    private static final long serialVersionUID = -7895025789667402067L;

    // The engine service key
    private AxArtifactKey engineServiceKey;

    // The engines under the control of this engine service
    private AxArtifactKey[] engineKeyArray;

    // The engine service key
    private AxArtifactKey apexModelKey;

    /**
     * Instantiates a new EngineServiceInfoResponse message.
     *
     * @param targetKey the target key of the entity that asked for the action that triggered this response message
     * @param successful the successful if the action in the triggering message worked
     * @param responseTo the message to which this message is a response
     */
    public EngineServiceInfoResponse(final AxArtifactKey targetKey, final boolean successful,
            final Message responseTo) {
        super(targetKey, successful, null, responseTo);
    }

    /**
     * Instantiates a new EngineServiceInfoResponse message.
     *
     * @param targetKey the target key of the entity that asked for the action that triggered this response message
     * @param successful the successful if the action in the triggering message worked
     * @param messageData the message data which may indicate specific conditions for the response
     * @param responseTo the message to which this message is a response
     */
    public EngineServiceInfoResponse(final AxArtifactKey targetKey, final boolean successful, final String messageData,
            final Message responseTo) {
        super(targetKey, successful, messageData, responseTo);
    }

    /**
     * Gets the engine service key.
     *
     * @return the engine service key
     */
    public AxArtifactKey getEngineServiceKey() {
        return engineServiceKey;
    }

    /**
     * Sets the engine service key.
     *
     * @param engineServiceKey the engine service key
     */
    public void setEngineServiceKey(final AxArtifactKey engineServiceKey) {
        this.engineServiceKey = engineServiceKey;
    }

    /**
     * Gets the engine key array.
     *
     * @return the engine key array
     */
    public AxArtifactKey[] getEngineKeyArray() {
        return engineKeyArray;
    }

    /**
     * Sets the engine key array.
     *
     * @param engineKeyCollection the engine key array
     */
    public void setEngineKeyArray(final Collection<AxArtifactKey> engineKeyCollection) {
        if (engineKeyCollection != null) {
            engineKeyArray = engineKeyCollection.toArray(new AxArtifactKey[engineKeyCollection.size()]);
        }
        else {
            engineKeyArray = null;
        }
    }

    /**
     * Gets the apex model key.
     *
     * @return the apex model key
     */
    public AxArtifactKey getApexModelKey() {
        return apexModelKey;
    }

    /**
     * Sets the apex model key.
     *
     * @param apexModelKey the apex model key
     */
    public void setApexModelKey(final AxArtifactKey apexModelKey) {
        this.apexModelKey = apexModelKey;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((apexModelKey == null) ? 0 : apexModelKey.hashCode());
        result = prime * result + Arrays.hashCode(engineKeyArray);
        result = prime * result + ((engineServiceKey == null) ? 0 : engineServiceKey.hashCode());
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

        EngineServiceInfoResponse other = (EngineServiceInfoResponse) obj;
        if (apexModelKey == null) {
            if (other.apexModelKey != null) {
                return false;
            }
        } else if (!apexModelKey.equals(other.apexModelKey)) {
            return false;
        }
        if (!Arrays.equals(engineKeyArray, other.engineKeyArray)) {
            return false;
        }
        if (engineServiceKey == null) {
            if (other.engineServiceKey != null) {
                return false;
            }
        } else if (!engineServiceKey.equals(other.engineServiceKey)) {
            return false;
        }
        return true;
    }
}
