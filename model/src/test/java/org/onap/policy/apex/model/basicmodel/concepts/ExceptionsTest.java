/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.junit.jupiter.api.Test;


class ExceptionsTest {

    @Test
    void test() {
        assertConstructors();

        AxArtifactKey key = new AxArtifactKey();
        ApexException ae = new ApexException("Message", new IOException("IO exception message"), key);
        assertEquals("Message\ncaused by: Message\ncaused by: IO exception message", ae.getCascadedMessage());
        assertEquals(key, ae.getObject());

        AxArtifactKey rkey = new AxArtifactKey();
        ApexRuntimeException re = new ApexRuntimeException("Runtime Message",
                        new IOException("IO runtime exception message"), rkey);
        assertEquals("Runtime Message\ncaused by: Runtime Message\ncaused by: IO runtime exception message",
                        re.getCascadedMessage());
        assertEquals(key, re.getObject());

        AxArtifactKey ckey = new AxArtifactKey();
        ApexException ace = new ApexException("Concept Message", new IOException("IO concept exception message"), ckey);
        assertEquals("Concept Message\ncaused by: Concept Message\ncaused by: IO concept exception message",
                        ace.getCascadedMessage());
        assertEquals(ckey, ace.getObject());
    }

    private static void assertConstructors() {
        assertNotNull(new ApexException("Message"));
        assertNotNull(new ApexException("Message", new AxArtifactKey()));
        assertNotNull(new ApexException("Message", new IOException()));
        assertNotNull(new ApexException("Message", new IOException(), new AxArtifactKey()));

        assertNotNull(new ApexRuntimeException("Message"));
        assertNotNull(new ApexRuntimeException("Message", new AxArtifactKey()));
        assertNotNull(new ApexRuntimeException("Message", new IOException()));
        assertNotNull(new ApexRuntimeException("Message", new IOException(), new AxArtifactKey()));

        assertNotNull(new ApexConceptException("Message"));
        assertNotNull(new ApexConceptException("Message", new IOException()));
    }

}
