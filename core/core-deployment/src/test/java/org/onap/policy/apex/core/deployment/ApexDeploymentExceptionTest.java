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

package org.onap.policy.apex.core.deployment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

/**
 * Test the Apex deployment Exception.
 *
 */
public class ApexDeploymentExceptionTest {

    @Test
    public void testDeploymentException() {
        ApexDeploymentException ade0 = new ApexDeploymentException("a message");
        assertNotNull(ade0);
        assertEquals("a message", ade0.getMessage());

        ApexDeploymentException ade1 = new ApexDeploymentException("a message", new IOException());
        assertNotNull(ade1);
        assertEquals("a message", ade0.getMessage());
    }
}
