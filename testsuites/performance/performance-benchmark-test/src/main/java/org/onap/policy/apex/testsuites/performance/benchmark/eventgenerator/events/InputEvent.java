/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import lombok.Getter;
import lombok.Setter;

/**
 * This class is a POJO representing an input event for load testing.
 */
@Getter
@Setter
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
        /*
         * This is not used for encryption/security, thus disabling sonar.
         */
        final Random rand = new Random();   // NOSONAR

        testMatchCase = rand.nextInt(4);
        name = "Event0" + rand.nextInt(2) + "00";
        testTemperature = rand.nextDouble() * 1000;
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
