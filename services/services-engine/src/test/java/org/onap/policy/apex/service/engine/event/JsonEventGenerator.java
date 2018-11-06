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

package org.onap.policy.apex.service.engine.event;

/**
 * This class generates JSON event used for the test cases.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JsonEventGenerator {
    /**
     * Json event.
     *
     * @return the string
     */
    public static String jsonEvent() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12345\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no name.
     *
     * @return the string
     */
    public static String jsonEventNoName() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"namez\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12346\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad name.
     *
     * @return the string
     */
    public static String jsonEventBadName() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"%%%%\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12347\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex name.
     *
     * @return the string
     */
    public static String jsonEventNoExName() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"I_DONT_EXIST\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12348\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no version.
     *
     * @return the string
     */
    public static String jsonEventNoVersion() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"versiion\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12349\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad version.
     *
     * @return the string
     */
    public static String jsonEventBadVersion() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"#####\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12350\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex version.
     *
     * @return the string
     */
    public static String jsonEventNoExVersion() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"1.2.3\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12351\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no namespace.
     *
     * @return the string
     */
    public static String jsonEventNoNamespace() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpacee\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12352\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad namespace.
     *
     * @return the string
     */
    public static String jsonEventBadNamespace() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"hello.&&&&\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12353\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex namespace.
     *
     * @return the string
     */
    public static String jsonEventNoExNamespace() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"pie.in.the.sky\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12354\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no source.
     *
     * @return the string
     */
    public static String jsonEventNoSource() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"sourcee\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12355\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad source.
     *
     * @return the string
     */
    public static String jsonEventBadSource() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"%!@**@!\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"intPar\": 12356\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no target.
     *
     * @return the string
     */
    public static String jsonEventNoTarget() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"targett\": \"apex\",\n");
        builder.append("  \"intPar\": 12357\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad target.
     *
     * @return the string
     */
    public static String jsonEventBadTarget() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"KNIO(*S)A(S)D\",\n");
        builder.append("  \"intPar\": 12358\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event missing fields.
     *
     * @return the string
     */
    public static String jsonEventMissingFields() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\"\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event null fields.
     *
     * @return the string
     */
    public static String jsonEventNullFields() {
        final StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.events\",\n");
        builder.append("  \"name\": \"BasicEvent\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"Apex\",\n");
        builder.append("  \"intPar\": -1\n");
        builder.append("}");

        return builder.toString();
    }
}
