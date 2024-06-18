/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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
        builder.append("  <name>").append(eventName).append("</name>\n");
        builder.append("  <version>0.0.1</version>\n");
        builder.append("  <nameSpace>org.onap.policy.apex.sample.events</nameSpace>\n");
        builder.append("  <source>test</source>\n");
        builder.append("  <target>apex</target>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestSlogan</key>\n");
        nextEventNo += 1;
        builder.append("    <value>Test slogan for External Event").append(nextEventNo).append("</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestMatchCase</key>\n");
        builder.append("    <value>").append(nextMatchCase).append("</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestTimestamp</key>\n");
        builder.append("    <value>").append(System.currentTimeMillis()).append("</value>\n");
        builder.append("  </data>\n");
        builder.append("  <data>\n");
        builder.append("    <key>TestTemperature</key>\n");
        builder.append("    <value>").append(nextTestTemperature).append("</value>\n");
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
        builder.append("  \"name\": \"").append(eventName).append("\",\n");
        builder.append("  \"version\": \"0.0.1\",\n");
        builder.append("  \"source\": \"test\",\n");
        builder.append("  \"target\": \"apex\",\n");
        nextEventNo += 1;
        builder.append("  \"TestSlogan\": \"Test slogan for External Event").append(nextEventNo).append("\",\n");
        builder.append("  \"TestMatchCase\": ").append(nextMatchCase).append(",\n");
        builder.append("  \"TestTimestamp\": ").append(System.currentTimeMillis()).append(",\n");
        builder.append("  \"TestTemperature\": ").append(nextTestTemperature).append("\n");
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

        int eventCount;
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
        }
    }
}
