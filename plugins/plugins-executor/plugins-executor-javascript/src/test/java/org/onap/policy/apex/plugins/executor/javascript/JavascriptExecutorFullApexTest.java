/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.javascript;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.resources.TextFileUtils;

public class JavascriptExecutorFullApexTest {

    @Test
    public void testFullApexPolicy() throws ApexException {
        final String[] args = {"src/test/resources/prodcons/File2File.json"};

        final File outFile0 = new File("src/test/resources/events/EventsOut0.json");
        final File outFile1 = new File("src/test/resources/events/EventsOut1.json");
        outFile0.deleteOnExit();
        outFile1.deleteOnExit();

        final ApexMain apexMain = new ApexMain(args);
        assertNotNull(apexMain);

        await().atMost(10, TimeUnit.SECONDS).until(() -> outFile0.exists());
        await().atMost(10, TimeUnit.SECONDS).until(() -> outFile1.exists());

        await().atMost(10, TimeUnit.SECONDS).until(() -> fileHasOccurencesOf(outFile0, "BasicEventOut0", 50));
        await().atMost(10, TimeUnit.SECONDS).until(() -> fileHasOccurencesOf(outFile1, "BasicEventOut1", 50));

        apexMain.shutdown();
    }

    private boolean fileHasOccurencesOf(final File file, final String token, final int occurenceCount)
        throws IOException {

        return occurenceCount == StringUtils.countMatches(TextFileUtils.getTextFileAsString(file.getAbsolutePath()),
            token);
    }
}
