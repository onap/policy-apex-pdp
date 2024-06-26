/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ContextExceptionTest {

    @Test
    void testContextException() {
        assertEquals("context exception message", new ContextException("context exception message").getMessage());
        assertEquals("context exception message",
            new ContextException("context exception message", new IOException()).getMessage());
    }

    @Test
    void testContextRuntimeException() {
        assertEquals("context exception message",
            new ContextRuntimeException("context exception message").getMessage());
        assertEquals("context exception message",
            new ContextRuntimeException("context exception message", new IOException()).getMessage());
    }
}
