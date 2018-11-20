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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

/**
 * This class is a POJO representing an input event for load testing.
 */
public class InputEvent {
    private String nameSpace = "org.onap.policy.apex.sample.events";
    private String name;
    private String version = "0.0.1";
    private String source = "EventGenerator";
    private String target = "Apex";
    
    @SerializedName(value = "TestSlogan")
    private String testSlogan;
    
    @SerializedName(value = "TestMatchCase")
    private int testMatchCase;

    @SerializedName(value = "TestTimestamp")
    private long testTimestamp = System.nanoTime();

    @SerializedName(value = "TestTemperature")
    private double testTemperature;

    /**
     * Constructor, assign default values to fields.
     */
    public InputEvent() {
        final Random rand = new Random();
        testMatchCase = rand.nextInt(4);
        name = "Event0" + rand.nextInt(2) + "00";
        testTemperature = rand.nextDouble() * 1000;
    }
    
    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTestSlogan() {
        return testSlogan;
    }

    public void setTestSlogan(String testSlogan) {
        this.testSlogan = testSlogan;
    }

    public int getTestMatchCase() {
        return testMatchCase;
    }

    public void setTestMatchCase(int testMatchCase) {
        this.testMatchCase = testMatchCase;
    }

    public long getTestTimestamp() {
        return testTimestamp;
    }

    public void setTestTimestamp(long testTimestamp) {
        this.testTimestamp = testTimestamp;
    }

    public double getTestTemperature() {
        return testTemperature;
    }

    public void setTestTemperature(double testTemperature) {
        this.testTemperature = testTemperature;
    }

    /**
     * Get a JSON representation of the input event.
     * 
     * @return the event in JSON format
     */
    public String asJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    } 
}
