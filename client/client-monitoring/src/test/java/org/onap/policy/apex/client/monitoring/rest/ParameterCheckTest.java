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

package org.onap.policy.apex.client.monitoring.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the parameter check class.
 *
 */
public class ParameterCheckTest {

    @Test
    public void testStartStop() {
        assertEquals("START", ParameterCheck.StartStop.START.name());
        assertEquals("STOP", ParameterCheck.StartStop.STOP.name());
    }

    @Test
    public void testHostName() {
        assertNull(ParameterCheck.getHostName(null));
        
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        assertNull(ParameterCheck.getHostName(parameterMap));
        parameterMap.put("hostname", null);
        assertNull(ParameterCheck.getHostName(parameterMap));
        
        String[] hostnameBlankValue0 = {"", ""};
        parameterMap.put("hostname", hostnameBlankValue0);
        assertNull(ParameterCheck.getHostName(parameterMap));
        
        String[] hostnameBlankValue1 = {" ", " "};
        parameterMap.put("hostname", hostnameBlankValue1);
        assertNull(ParameterCheck.getHostName(parameterMap));
        
        String[] hostnameValue = {"hostname0", "hostname1"};
        parameterMap.put("hostname", hostnameValue);
        assertEquals("hostname0", ParameterCheck.getHostName(parameterMap));
    }

    @Test
    public void testPort() {
        assertEquals(-1, ParameterCheck.getPort(null));
        
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        assertEquals(-1, ParameterCheck.getPort(parameterMap));
        parameterMap.put("port", null);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));

        String[] portBlankValue0 = {"", ""};
        parameterMap.put("port", portBlankValue0);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));

        String[] portBlankValue1 = {" ", " "};
        parameterMap.put("port", portBlankValue1);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));
        
        String[] portValueBad = {"port", "value"};
        parameterMap.put("port", portValueBad);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));
        
        String[] portValueRange0 = {"-1", "-1"};
        parameterMap.put("port", portValueRange0);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));

        String[] portValueRange1 = {"65536", "65536"};
        parameterMap.put("port", portValueRange1);
        assertEquals(-1, ParameterCheck.getPort(parameterMap));

        String[] portValue = {"12344", "23221"};
        parameterMap.put("port", portValue);
        assertEquals(12344, ParameterCheck.getPort(parameterMap));
    }

    @Test
    public void testEngineKey() {
        assertEquals(null, ParameterCheck.getEngineKey(null));

        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        parameterMap.put("Zooby", null);
        assertEquals(null, ParameterCheck.getEngineKey(parameterMap));
        
        parameterMap.put("AxArtifactKey", null);
        assertEquals(null, ParameterCheck.getEngineKey(parameterMap));
        parameterMap.remove("AxArtifactKey");
        
        parameterMap.put("AxArtifactKey#zooby", null);
        assertEquals(null, ParameterCheck.getEngineKey(parameterMap));
        parameterMap.remove("AxArtifactKey#zooby");

        parameterMap.put("AxArtifactKey#zooby#looby", null);
        assertEquals(null, ParameterCheck.getEngineKey(parameterMap));
        parameterMap.remove("AxArtifactKey#zooby#looby");

        parameterMap.put("AxArtifactKey#Name:0.0.1", null);
        assertEquals(new AxArtifactKey("Name", "0.0.1"), ParameterCheck.getEngineKey(parameterMap));
    }

    @Test
    public void testStartStopValue() {
        assertEquals(null, ParameterCheck.getStartStop(null, null));
        
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, null));

        parameterMap.put("Zooby", null);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, null));

        AxArtifactKey engineKey = new AxArtifactKey("Engine", "0.0.1");

        parameterMap.put("Zooby", null);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, engineKey));

        String key = "AxArtifactKey#" + engineKey.getId();
        
        parameterMap.put(key, null);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, engineKey));

        String[] startStopBlankValue0 = {"", ""};
        parameterMap.put(key, startStopBlankValue0);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, engineKey));

        String[] startStopBlankValue1 = {" ", " "};
        parameterMap.put(key, startStopBlankValue1);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, engineKey));
        
        String[] startStopValueBad = {key, "value"};
        parameterMap.put(key, startStopValueBad);
        assertEquals(null, ParameterCheck.getStartStop(parameterMap, engineKey));
        
        String[] startValue = {"START", "STOP"};
        parameterMap.put(key, startValue);
        assertEquals(ParameterCheck.StartStop.START, ParameterCheck.getStartStop(parameterMap, engineKey));

        String[] stopValue = {"STOP", "START"};
        parameterMap.put(key, stopValue);
        assertEquals(ParameterCheck.StartStop.STOP, ParameterCheck.getStartStop(parameterMap, engineKey));
    }

    @Test
    public void testLong() {
        assertEquals(-1, ParameterCheck.getLong(null, null));
        
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        assertEquals(-1, ParameterCheck.getLong(parameterMap, null));
        
        parameterMap.put("long0", null);
        assertEquals(-1, ParameterCheck.getLong(parameterMap, "longx"));
        assertEquals(-1, ParameterCheck.getLong(parameterMap, "long0"));

        String[] longBlankValue0 = {"", ""};
        parameterMap.put("long1", longBlankValue0);
        assertEquals(-1, ParameterCheck.getLong(parameterMap, "long1"));

        String[] longBlankValue1 = {" ", " "};
        parameterMap.put("long2", longBlankValue1);
        assertEquals(-1, ParameterCheck.getLong(parameterMap, "long2"));
        
        String[] longValueBad = {"long", "value"};
        parameterMap.put("long3", longValueBad);
        assertEquals(-1, ParameterCheck.getLong(parameterMap, "long3"));
        
        String[] longValue = {"12345", "6789"};
        parameterMap.put("long4", longValue);
        assertEquals(12345, ParameterCheck.getLong(parameterMap, "long4"));
    }
}
