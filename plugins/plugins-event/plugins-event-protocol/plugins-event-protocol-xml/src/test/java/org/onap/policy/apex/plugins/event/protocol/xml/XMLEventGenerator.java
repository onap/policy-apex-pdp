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

package org.onap.policy.apex.plugins.event.protocol.xml;

import java.util.Random;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class XMLEventGenerator {
    private static int nextEventNo = 0;

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

    public static String xmlEvent() {
        final Random rand = new Random();

        final StringBuilder builder = new StringBuilder();

        int nextEventNo = rand.nextInt(2);
        final String eventName = (nextEventNo == 0 ? "Event0000" : "Event0100");
        final int nextMatchCase = rand.nextInt(4);
        final float nextTestTemperature = rand.nextFloat() * 10000;

        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        builder.append("<xmlApexEvent xmlns=\"http://www.ericsson.com/apexevent\">\n");

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
        builder.append("	<value>" + nextMatchCase + "</value>\n");
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

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("usage EventGenerator #events");
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.err.println("usage EventGenerator #events");
            e.printStackTrace();
            return;
        }

        System.out.println(xmlEvents(eventCount));
    }

    public static int getNextEventNo() {
        return nextEventNo;
    }
}
