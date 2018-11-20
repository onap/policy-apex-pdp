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

import com.google.gson.annotations.SerializedName;

/**
 * This class is a POJO representing an output event for load testing.
 */
public class OutputEvent extends InputEvent {
    @SerializedName(value = "TestMatchCaseSelected")
    private int testMatchCaseSelected;

    @SerializedName(value = "TestMatchStateTime")
    private long testMatchStateTime;

    @SerializedName(value = "TestEstablishCaseSelected")
    private int testEstablishCaseSelected;

    @SerializedName(value = "TestEstablishStateTime")
    private long testEstablishStateTime;

    @SerializedName(value = "TestDecideCaseSelected")
    private int testDecideCaseSelected;

    @SerializedName(value = "TestDecideStateTime")
    private long testDecideStateTime;

    @SerializedName(value = "TestActCaseSelected")
    private int testActCaseSelected;

    @SerializedName(value = "TestActStateTime")
    private long testActStateTime;

    private long testReceviedTimestamp = System.nanoTime();

    public int getTestMatchCaseSelected() {
        return testMatchCaseSelected;
    }

    public void setTestMatchCaseSelected(int testMatchCaseSelected) {
        this.testMatchCaseSelected = testMatchCaseSelected;
    }

    public long getTestMatchStateTime() {
        return testMatchStateTime;
    }

    public void setTestMatchStateTime(long testMatchStateTime) {
        this.testMatchStateTime = testMatchStateTime;
    }

    public int getTestEstablishCaseSelected() {
        return testEstablishCaseSelected;
    }

    public void setTestEstablishCaseSelected(int testEstablishCaseSelected) {
        this.testEstablishCaseSelected = testEstablishCaseSelected;
    }

    public long getTestEstablishStateTime() {
        return testEstablishStateTime;
    }

    public void setTestEstablishStateTime(long testEstablishStateTime) {
        this.testEstablishStateTime = testEstablishStateTime;
    }

    public int getTestDecideCaseSelected() {
        return testDecideCaseSelected;
    }

    public void setTestDecideCaseSelected(int testDecideCaseSelected) {
        this.testDecideCaseSelected = testDecideCaseSelected;
    }

    public long getTestDecideStateTime() {
        return testDecideStateTime;
    }

    public void setTestDecideStateTime(long testDecideStateTime) {
        this.testDecideStateTime = testDecideStateTime;
    }

    public int getTestActCaseSelected() {
        return testActCaseSelected;
    }

    public void setTestActCaseSelected(int testActCaseSelected) {
        this.testActCaseSelected = testActCaseSelected;
    }

    public long getTestActStateTime() {
        return testActStateTime;
    }

    public void setTestActStateTime(long testActStateTime) {
        this.testActStateTime = testActStateTime;
    }

    public long getTestReceviedTimestamp() {
        return testReceviedTimestamp;
    }

    public void setTestReceviedTimestamp(long testReceviedTimestamp) {
        this.testReceviedTimestamp = testReceviedTimestamp;
    }

    public int findBatchNumber() {
        return Integer.valueOf(getTestSlogan().substring(0, getTestSlogan().indexOf('-')));
    }

    public int findEventNumber() {
        return Integer.valueOf(
                        getTestSlogan().substring(getTestSlogan().indexOf('-') + 1, getTestSlogan().indexOf(':')));
    }
}
