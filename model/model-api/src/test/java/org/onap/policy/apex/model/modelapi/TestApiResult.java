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

package org.onap.policy.apex.model.modelapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;

/**
 * Test API results.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApiResult {

    @Test
    public void testApiResult() {
        assertNotNull(new ApexApiResult());

        for (final Result result : Result.values()) {
            assertNotNull(new ApexApiResult(result));
        }

        assertNotNull(new ApexApiResult(Result.SUCCESS, "Result Message"));
        assertNotNull(new ApexApiResult(Result.FAILED, new IOException("IO Exception message")));
        assertNotNull(new ApexApiResult(Result.FAILED, "Result Message", new IOException("IO Exception message")));

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
        assertEquals("{\n" + "\"result\": \"OTHER_ERROR\",\n" + "\"messages\": [\n" + "\"First Message\",\n"
                + "\"Second Message\",\n" + "\"Third Message\"]\n" + "}\n", result2.toJson());
    }
}
