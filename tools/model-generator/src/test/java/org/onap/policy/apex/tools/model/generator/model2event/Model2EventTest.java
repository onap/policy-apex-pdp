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

package org.onap.policy.apex.tools.model.generator.model2event;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;

/**
 * Test the Model2Event utility.
 */
public class Model2EventTest {
    @Test
    public void testModel2Event() {
        try {
            final String[] EventArgs =
                { "-h" };

            Model2EventMain.main(EventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testModel2EventNoOptions() {
        final String[] EventArgs = new String[]
            {};

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("gen-model2event: no model file given, cannot proceed (try -h for help)"));
    }

    @Test
    public void testModel2EventBadOptions() {
        final String[] EventArgs =
            { "-zabbu" };

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("usage: gen-model2event"));
    }

    @Test
    public void testModel2EventHelp() {
        final String[] EventArgs =
            { "-h" };

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("usage: gen-model2event"));
    }

    @Test
    public void testModel2EventVersion() {
        final String[] EventArgs =
            { "-v" };

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("gen-model2event"));
    }

    @Test
    public void testModel2EventNoType() {
        final String[] EventArgs =
            { "-m", "src/test/resources/models/AvroModel.json" };

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("gen-model2event: no event type given, cannot proceed (try -h for help)"));
    }

    @Test
    public void testModel2EventBadType() {
        final String[] EventArgs =
            { "-m", "src/test/resources/models/AvroModel.json", "-t", "Zooby" };

        final String outputString = runModel2Event(EventArgs);

        assertTrue(outputString.contains("gen-model2event: unknown type <Zooby>, cannot proceed (try -h for help)"));
    }

    @Test
    public void testModel2EventAadm() throws IOException {
        testModel2EventModel("AADMPolicyModel");
    }

    @Test
    public void testModel2EventAnomaly() {
        testModel2EventModel("AnomalyDetectionPolicyModel");
    }

    @Test
    public void testModel2EventAutoLearn() {
        testModel2EventModel("AutoLearnPolicyModel");
    }

    @Test
    public void testModel2EventMfp() {
        testModel2EventModel("MyFirstPolicyModel");
    }

    @Test
    public void testModel2EventSample() {
        testModel2EventModel("SamplePolicyModelJAVASCRIPT");
    }

    /**
     * Run the application.
     * 
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String runModel2Event(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        new Model2EventMain(eventArgs, new PrintStream(baosOut, true));

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString;
    }

    /**
     * Test Event generation.
     * 
     * @param modelName the name of the model file
     */
    private void testModel2EventModel(String modelName) {
        try {
            File tempFile = File.createTempFile(modelName, ".apex");
            tempFile.deleteOnExit();

            final String[] eventArgs0 =
                { "-m", "src/test/resources/models/" + modelName + ".json", "-t", "stimuli" };
            final String outputString0 = runModel2Event(eventArgs0);

            assertTrue(outputString0.contains("type: stimuli"));

            final String[] eventArgs1 = {"-m", "src/test/resources/models/" + modelName + ".json", "-t", "response" };
            final String outputString1 = runModel2Event(eventArgs1);

            assertTrue(outputString1.contains("type: response"));

            final String[] eventArgs2 = {"-m", "src/test/resources/models/" + modelName + ".json", "-t", "internal" };
            final String outputString2 = runModel2Event(eventArgs2);

            assertTrue(outputString2.contains("type: internal"));
        } catch (Exception e) {
            throw new ApexRuntimeException("test should not throw an exception", e);
        }
    }
}
