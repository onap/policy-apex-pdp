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

package org.onap.policy.apex.model.policymodel.concepts;

import org.onap.policy.apex.model.policymodel.concepts.AxLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxLogicReader;

public class DummyLogicReader implements AxLogicReader {

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.policymodel.concepts.AxLogicReader#getLogicPackage()
     */
    @Override
    public String getLogicPackage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.policymodel.concepts.AxLogicReader#setLogicPackage(java.lang.
     * String)
     */
    @Override
    public AxLogicReader setLogicPackage(final String logicPackage) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.policymodel.concepts.AxLogicReader#getDefaultLogic()
     */
    @Override
    public String getDefaultLogic() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.policymodel.concepts.AxLogicReader#setDefaultLogic(java.lang.
     * String)
     */
    @Override
    public AxLogicReader setDefaultLogic(final String defaultLogic) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.model.policymodel.concepts.AxLogicReader#readLogic(org.onap.policy.apex.
     * model.policymodel.concepts.AxLogic)
     */
    @Override
    public String readLogic(final AxLogic axLogic) {
        return "Dummy Logic";
    }
}
