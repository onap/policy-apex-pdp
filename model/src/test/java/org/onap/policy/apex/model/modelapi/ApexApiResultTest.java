/*-
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

package org.onap.policy.apex.model.modelapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;

/**
 * Test API results.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ApexApiResultTest {

    @Test
    void testApiResult() {
        final ApexApiResult result =
            new ApexApiResult(Result.FAILED, "Result Message", new IOException("IO Exception message"));

        assertFalse(result.isOk());
        assertTrue(result.isNok());
        assertEquals(Result.FAILED, result.getResult());
        assertEquals("Result Message\nIO Exception message\njava.io.IOExce", result.getMessage().substring(0, 50));

        final ApexApiResult result2 = new ApexApiResult(Result.SUCCESS);
        result2.addMessage(null);
        assertEquals("", result2.getMessage());
        result2.addMessage("");
        assertEquals("", result2.getMessage());
        result2.addMessage("funky message");
        assertEquals("funky message\n", result2.getMessage());

        result2.setResult(Result.OTHER_ERROR);
        assertEquals(Result.OTHER_ERROR, result2.getResult());

        final String[] messages = {"First Message", "Second Message", "Third Message"};
        result2.setMessages(Arrays.asList(messages));
        assertEquals("First Message", result2.getMessages().get(0));
        assertEquals("Second Message", result2.getMessages().get(1));
        assertEquals("Third Message", result2.getMessages().get(2));

        assertEquals("result: OTHER_ERROR\nFirst Message\nSecond Message\nThird Message\n", result2.toString());
        assertEquals("""
            {
            "result": "OTHER_ERROR",
            "messages": [
            "First Message",
            "Second Message",
            "Third Message"]
            }
            """, result2.toJson());
    }
}
