/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.common.testclasses;

import java.io.Serializable;

import lombok.Data;

import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * The Class TestPing.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Data
public class PingTestClass implements Serializable {
    private static final long serialVersionUID = -3400711508992955886L;

    private int id = 0;
    private String name = "Rose";
    private String description = "A rose by any other name would smell as sweet";
    private long pingTime = System.currentTimeMillis();
    private long pongTime = -1;

    /**
     * Verify the class.
     *
     * @throws ApexException the apex event exception
     */
    public void verify() throws ApexException {
        if (name == null || name.length() < 4) {
            throw new ApexException("TestPing is not valid, name length null or less than 4");
        }

        if (!name.startsWith("Rose")) {
            throw new ApexException("TestPing is not valid, name does not start with \"Rose\"");
        }

        if (description == null || description.length() <= 44) {
            throw new ApexException("TestPing is not valid, description length null or less than 44");
        }

        if (!description.startsWith("A rose by any other name would smell as sweet")) {
            throw new ApexException("TestPing is not valid, description is incorrect");
        }

        if (pongTime < pingTime) {
            throw new ApexException(
                    "TestPing is not valid, pong time " + pongTime + " is less than ping time " + pingTime);
        }
    }
}
