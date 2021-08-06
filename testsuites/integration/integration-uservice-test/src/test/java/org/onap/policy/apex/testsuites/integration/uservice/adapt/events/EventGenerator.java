/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.events;

import java.util.Random;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EventGenerator.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventGenerator.class);

    @Getter
    private static int nextEventNo = 0;

    /**
     * Xml events.
     *
     * @param eventCount the event count
     * @return the string
     */
    public static String xmlEvents(final int eventCount) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < eventCount; i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(xmlEvent());
        }

        return builder.toString();
    }

    /**
     * Json events.
     *
     * @param eventCount the event count
     * @return the string
     */
    public static String jsonEvents(final int eventCount) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < eventCount; i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(jsonEvent());
        }

        return builder.toString();
    }

    /**
     * Xml event.
     *
     * @return the string
     */
    public static String xmlEvent() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        builder.append("<xmlApexEvent xmlns=\"http://www.onap.org/policy/apex-pdp/apexevent\">\n");

        builder.append("  <name>" + eventName + "</name>\n");
        builder.append("  <version>0.0.1</version>\n");
        builder.append("  <nameSpace>org.onap.policy.apex.sample.events</nameSpace>\n");
        builder.append("  <source>test</source>\n");
        builder.append("  <target>apex</target>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestSlogan</key>\n");
        builder.append("    <value>Test slogan for External Event" + (nextEventNo++) + "</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestMatchCase</key>\n");
        builder.append("    <value>" + nextMatchCase + "</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestTimestamp</key>\n");
        builder.append("    <value>" + System.currentTimeMillis() + "</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestTemperature</key>\n");
        builder.append("    <value>" + nextTestTemperature + "</value>\n");
        builder.append("  </data>\n");
        builder.append("</xmlApexEvent>");

        return builder.toString();
    }

    /**
     * Json event.
     *
     * @return the string
     */
    public static String jsonEvent() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no name.
     *
     * @return the string
     */
    public static String jsonEventNoName() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"namez\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad name.
     *
     * @return the string
     */
    public static String jsonEventBadName() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"%%%%\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex name.
     *
     * @return the string
     */
    public static String jsonEventNoExName() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"I_DONT_EXIST\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no version.
     *
     * @return the string
     */
    public static String jsonEventNoVersion() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"versiion\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad version.
     *
     * @return the string
     */
    public static String jsonEventBadVersion() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"#####\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex version.
     *
     * @return the string
     */
    public static String jsonEventNoExVersion() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"Event0000\",\n");
        builder.append("  \"version\": \"1.2.3\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no namespace.
     *
     * @return the string
     */
    public static String jsonEventNoNamespace() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpacee\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad namespace.
     *
     * @return the string
     */
    public static String jsonEventBadNamespace() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"hello.&&&&\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no ex namespace.
     *
     * @return the string
     */
    public static String jsonEventNoExNamespace() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"pie.in.the.sky\",\n");
        builder.append("  \"name\": \"Event0000\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no source.
     *
     * @return the string
     */
    public static String jsonEventNoSource() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"sourcee\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad source.
     *
     * @return the string
     */
    public static String jsonEventBadSource() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"%!@**@!\",\n");
        builder.append("  \"target\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event no target.
     *
     * @return the string
     */
    public static String jsonEventNoTarget() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"targett\": \"apex\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Json event bad target.
     *
     * @return the string
     */
    public static String jsonEventBadTarget() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("{\n");
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"" + eventName + "\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"KNIO(*S)A(S)D\",\n");
        builder.append("  \"TestSlogan\": \"Test slogan for External Event" + (nextEventNo++) + "\",\n");
        builder.append("  \"TestMatchCase\": " + nextMatchCase + ",\n");
        builder.append("  \"TestTimestamp\": " + System.currentTimeMillis() + ",\n");
        builder.append("  \"TestTemperature\": " + nextTestTemperature + "\n");
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
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"Event0000\",\n");
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
        builder.append("  \"nameSpace\": \"org.onap.policy.apex.sample.events\",\n");
        builder.append("  \"name\": \"Event0000\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"Apex\",\n");
        builder.append("  \"TestSlogan\": null,\n");
        builder.append("  \"TestMatchCase\": -1,\n");
        builder.append("  \"TestTimestamp\": -1,\n");
        builder.append("  \"TestTemperature\": -1.0\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        if (args.length != 2) {
            LOGGER.error("usage EventGenerator #events XML|JSON");
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            LOGGER.error("usage EventGenerator #events XML|JSON");
            e.printStackTrace();
            return;
        }

        if (args[1].equalsIgnoreCase("XML")) {
            LOGGER.info(xmlEvents(eventCount));
        } else if (args[1].equalsIgnoreCase("JSON")) {
            LOGGER.info(jsonEvents(eventCount));
        } else {
            LOGGER.error("usage EventGenerator #events XML|JSON");
            return;
        }
    }
}
