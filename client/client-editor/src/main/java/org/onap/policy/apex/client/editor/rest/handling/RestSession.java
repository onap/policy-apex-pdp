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

import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;

/**
 * This class represents an ongoing editor session in the Apex editor and holds the information for the session.
 *
 */
public class RestSession {
    // The ID of the session
    private int sessionId;
    
    // The Apex policy model of the session
    private ApexModel apexModel;

    // The Apex policy model being edited
    private ApexModel apexModelEdited;

    public RestSession(final int sessionId) {
        this.sessionId = sessionId;
        this.apexModel = new ApexModelFactory().createApexModel(null, true);
    }

    /**
     * Commence making changes to the Apex model.
     * @return the result of the edit commencement operation
     */
    public synchronized ApexApiResult editModel() {
        if (apexModelEdited != null) {
            return new ApexApiResult(Result.FAILED, "model is already being edited");
        }
        
        apexModelEdited = apexModel.clone();
        return new ApexApiResult();
    }
    
    /**
     * Commit the changes to the Apex model.
     * @return the result of the commit operation
     */
    public synchronized ApexApiResult commitChanges() {
        if (apexModelEdited == null) {
            return new ApexApiResult(Result.FAILED, "model is not being edited");
        }
        
        apexModel = apexModelEdited;
        apexModelEdited = null;
        return new ApexApiResult();
    }
    
    /**
     * Discard the changes to the Apex model.
     * @return the result of the discard operation
     */
    public synchronized ApexApiResult discardChanges() {
        if (apexModelEdited == null) {
            return new ApexApiResult(Result.FAILED, "model is not being edited");
        }
        
        apexModelEdited = null;
        return new ApexApiResult();
    }
    
    /**
     * Get the session ID of the session.
     * @return the sessionId
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Get the Apex model of the session.
     * @return the apexModel
     */
    public ApexModel getApexModel() {
        return apexModel;
    }

    /**
     * Get the edited Apex model of the session.
     * @return the apexModel
     */
    public ApexModel getApexModelEdited() {
        return apexModelEdited;
    }
}
