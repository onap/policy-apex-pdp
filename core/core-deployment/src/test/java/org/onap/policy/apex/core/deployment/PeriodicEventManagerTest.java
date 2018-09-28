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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Test the periodic event manager utility.
 */
public class PeriodicEventManagerTest {
    @Test
    public void testPeroidicEventManagerBad() {
        try {
            final String[] eventArgs =
                { "-h" };

            PeriodicEventManager.main(eventArgs);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("invalid arguments: [-h]", exc.getMessage().substring(0, 23));
        }
    }

    @Test
    public void testPeroidicEventManagerOk() {
        try {
            final String[] eventArgs =
                { "Host", "43443", "start", "1000" };

            PeriodicEventManager.main(eventArgs);
            fail("test should throw an exception");
        } catch (Exception exc) {
            assertEquals("periodic event setting failed on parameters Host 43443 true", exc.getMessage());
        }
    }

    @Test
    public void testPeroidicEventManagerNoOptions() {
        final String[] eventArgs = new String[]
            {};

        final String outputString = testPeriodicEventManagerConstructor(eventArgs);

        assertTrue(outputString.contains(
                        "usage: PeriodicEventManager <server address> <port address> <start/stop> <periods in ms>"));
    }

    @Test
    public void testPeroidicEventManagerBadOptions() {
        final String[] eventArgs =
            { "-zabbu" };

        final String outputString = testPeriodicEventManagerConstructor(eventArgs);

        assertTrue(outputString.contains(
                        "usage: PeriodicEventManager <server address> <port address> <start/stop> <periods in ms>"));
    }

    @Test
    public void testPeroidicEventManagerNonNumeric3() {
        final String[] eventArgs =
            { "aaa", "bbb", "ccc", "ddd" };

        final String outputString = testPeriodicEventManagerConstructor(eventArgs);

        assertTrue(outputString.contains("argument port is invalid"));
    }

    @Test
    public void testPeroidicEventManagerNonNumeric2() {
        final String[] eventArgs =
            { "aaa", "12345", "start", "stop" };

        final String outputString = testPeriodicEventManagerConstructor(eventArgs);

        assertTrue(outputString.contains("argument period is invalid"));
    }

    @Test
    public void testPeroidicEventManagerNotStartStop() {
        final String[] eventArgs =
            { "aaa", "12345", "1000", "1000" };

        final String outputString = testPeriodicEventManagerConstructor(eventArgs);

        assertTrue(outputString.contains("argument 1000 must be \"start\" or \"stop\""));
    }

    @Test
    public void testPeroidicEventManagerStart() {
        final String[] eventArgs =
            { "localhost", "12345", "start", "1000" };

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        PeriodicEventManager peManager = null;
        try {
            peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));
            peManager.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }

        try {
            peManager.init();
        } catch (ApexDeploymentException ade) {
            assertEquals("model deployment failed on parameters localhost 12345 true", ade.getMessage());
        }
        
        try {
            peManager.init();
        } catch (ApexDeploymentException ade) {
            ade.printStackTrace();
            fail("test should not throw an exception");
        }
        
        try {
            peManager.runCommand();
        } catch (ApexDeploymentException ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:12345", ade.getMessage());
        }
        
        try {
            peManager.runCommand();
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }
        
        peManager.close();
    }

    @Test
    public void testPeroidicEventManagerStop() {
        final String[] eventArgs =
            { "localhost", "12345", "stop", "1000" };

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        PeriodicEventManager peManager = null;
        try {
            peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));
            peManager.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }

        try {
            peManager.init();
        } catch (ApexDeploymentException ade) {
            assertEquals("model deployment failed on parameters localhost 12345 true", ade.getMessage());
        }
        
        try {
            peManager.init();
        } catch (ApexDeploymentException ade) {
            ade.printStackTrace();
            fail("test should not throw an exception");
        }
        
        try {
            peManager.runCommand();
        } catch (ApexDeploymentException ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:12345", ade.getMessage());
        }
        
        try {
            peManager.runCommand();
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }
        
        peManager.close();
    }

    @Test
    public void testPeroidicEventManagerStartUninitialized() {
        final String[] eventArgs =
            { "localhost", "12345", "start", "1000" };

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        PeriodicEventManager peManager = null;
        try {
            peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));
            peManager.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }

        try {
            peManager.runCommand();
            fail("test should throw an exception");
        } catch (ApexDeploymentException ade) {
            assertEquals("connection to apex is not initialized", ade.getMessage());
        }
        
        try {
            peManager.runCommand();
            fail("test should throw an exception");
        } catch (ApexDeploymentException ade) {
            assertEquals("connection to apex is not initialized", ade.getMessage());
            ade.printStackTrace();
        }
        
        peManager.close();
    }

    @Test
    public void testPeroidicEventManagerStopUninitialized() {
        final String[] eventArgs =
            { "localhost", "12345", "stop", "1000" };

        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();

        PeriodicEventManager peManager = null;
        try {
            peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));
            peManager.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));
        } catch (ApexDeploymentException ade) {
            fail("test should not throw an exception");
        }

        try {
            peManager.runCommand();
            fail("test should throw an exception");
        } catch (ApexDeploymentException ade) {
            assertEquals("connection to apex is not initialized", ade.getMessage());
        }
        
        peManager.close();
    }

    /**
     * Run the application.
     * 
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String testPeriodicEventManagerConstructor(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        String exceptionString = "";
        try {
            PeriodicEventManager peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));
            peManager.getEngineServiceFacade().setDeploymentClient(new DummyDeploymentClient("aHost", 54553));
        } catch (ApexDeploymentException ade) {
            exceptionString = ade.getCascadedMessage();
        }

        InputStream testInput = new ByteArrayInputStream("Test Data for Input to WS".getBytes());
        System.setIn(testInput);

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString + "\n*** exception ***\n"
                        + exceptionString;
    }
}
