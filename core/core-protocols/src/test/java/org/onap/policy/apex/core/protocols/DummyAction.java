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

package org.onap.policy.apex.core.protocols;

import org.onap.policy.apex.core.protocols.Action;

/**
 * Dummy action for testing.
 */
public class DummyAction implements Action {
    private static final long serialVersionUID = 9178856761163651594L;
    
    private String actionString =  "An Action String";

    public DummyAction(final String actionString) {
        this.actionString = actionString;
    }

    /* (non-Javadoc)
     * @see org.onap.policy.apex.core.protocols.Action#getActionString()
     */
    @Override
    public String getActionString() {
        return actionString;
    }

    public void setActionString(final String actionString) {
        this.actionString = actionString;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actionString == null) ? 0 : actionString.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        DummyAction other = (DummyAction) obj;
        if (actionString == null) {
            if (other.actionString != null) {
                return false;
            }
        } else if (!actionString.equals(other.actionString)) {
            return false;
        }
        return true;
    }
}
