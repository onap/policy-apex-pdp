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

package org.onap.policy.apex.service.engine.engdep;

import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.core.protocols.Action;

/**
 * Bad action class.
 */
public class BadAction implements Action {
    private static final long serialVersionUID = -6562765120898697138L;
    
    private String actionString;

    public BadAction(final String actionString) {
        this.actionString = actionString;
    }
    
    @Override
    public String getActionString() {
        if (actionString == "throw exception") {
            throw new NotImplementedException("dummy IO excepton");
        }
        return actionString;
    }
}
