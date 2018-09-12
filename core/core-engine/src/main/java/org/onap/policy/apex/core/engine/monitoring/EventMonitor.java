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

package org.onap.policy.apex.core.engine.monitoring;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to monitor event parameter gets and sets.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventMonitor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventMonitor.class);

    /**
     * Monitor get on an event parameter.
     *
     * @param eventParameter The event parameter to monitor
     * @param value the value of the event parameter
     * @param userArtifactStack the keys of the artifacts using the event at the moment
     */
    public void monitorGet(final AxField eventParameter, final Object value, final AxConcept[] userArtifactStack) {
        String monitorGetString = monitor("GET", userArtifactStack, eventParameter, value);
        LOGGER.trace(monitorGetString);
    }

    /**
     * Monitor set on an event parameter.
     *
     * @param eventParameter The event parameter to monitor
     * @param value the value of the event parameter
     * @param userArtifactStack the keys of the artifacts using the event at the moment
     */
    public void monitorSet(final AxField eventParameter, final Object value, final AxConcept[] userArtifactStack) {
        String monitorSetString = monitor("SET", userArtifactStack, eventParameter, value);
        LOGGER.trace(monitorSetString);
    }

    /**
     * Monitor remove on an event parameter.
     *
     * @param eventParameter The event parameter to monitor
     * @param removedValue the value of the event parameter
     * @param userArtifactStack the keys of the artifacts using the event at the moment
     */
    public void monitorRemove(final AxField eventParameter, final Object removedValue,
            final AxConcept[] userArtifactStack) {
        String monitorRemoveString = monitor("REMOVE", userArtifactStack, eventParameter, removedValue);
        LOGGER.trace(monitorRemoveString);
    }

    /**
     * Monitor the user artifact stack.
     *
     * @param preamble the preamble
     * @param userArtifactStack The user stack to print
     * @param eventParameter The event parameter that we are monitoring
     * @param value The value of the target object
     * @return the string
     */
    private String monitor(final String preamble, final AxConcept[] userArtifactStack, final AxField eventParameter,
            final Object value) {
        final StringBuilder builder = new StringBuilder();

        builder.append(preamble);
        builder.append(",[");

        if (userArtifactStack != null) {
            boolean first = true;
            for (final AxConcept stackKey : userArtifactStack) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                if (stackKey instanceof AxArtifactKey) {
                    builder.append(((AxArtifactKey) stackKey).getId());
                } else if (stackKey instanceof AxReferenceKey) {
                    builder.append(((AxReferenceKey) stackKey).getId());
                } else {
                    builder.append(stackKey.toString());
                }
            }
        }
        builder.append("],");

        builder.append(eventParameter.toString());
        builder.append("=");
        builder.append(value);

        return builder.toString();
    }
}
