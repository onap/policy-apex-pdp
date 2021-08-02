/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.executionproperties;

import lombok.Data;
import org.onap.policy.common.utils.coder.Coder;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;

/**
 * Test event fgor execution properties.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
@Data
public class RunTestEvent {
    private static final Coder coder = new StandardCoder();

    private String testToRun;
    private String propertyFileName;

    public String toJson() throws CoderException {
        return coder.encode(this);
    }

    /**
     * Set fields of this event from a JSON string.
     *
     * @param jsonString the JSON string
     * @throws CoderException on JSON exceptions
     */
    public void fromJson(final String jsonString) throws CoderException {
        RunTestEvent jsonTestEvent = coder.decode(jsonString, RunTestEvent.class);
        this.testToRun = jsonTestEvent.testToRun;
        this.propertyFileName = jsonTestEvent.propertyFileName;
    }
}
