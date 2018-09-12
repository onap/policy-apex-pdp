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

package org.onap.policy.apex.context.monitoring;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to monitor context creates, deletes, gets, sets, locks and unlocks on context items in Apex
 * context albums.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextMonitor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContextMonitor.class);

    /**
     * Monitor an initiation on a context item.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param value The value of the item
     */
    public void monitorInit(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final Object value) {
        String monitorInitString = monitor("INIT", null, albumKey, schemaKey, name, value);
        LOGGER.trace(monitorInitString);
    }

    /**
     * Monitor an initiation on a context item.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param value The value of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorInit(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final Object value, final AxConcept[] userArtifactStack) {
        String monitorInitString = monitor("INIT", userArtifactStack, albumKey, schemaKey, name, value);
        LOGGER.trace(monitorInitString);
    }

    /**
     * Monitor a deletion on a context item.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param value The value of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorDelete(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final Object value, final AxConcept[] userArtifactStack) {
        String monitorDeleteString = monitor("DEL", userArtifactStack, albumKey, schemaKey, name, value);
        LOGGER.trace(monitorDeleteString);
    }

    /**
     * Monitor get on a context item.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param value The value of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorGet(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final Object value, final AxConcept[] userArtifactStack) {
        String monitorGetString = monitor("GET", userArtifactStack, albumKey, schemaKey, name, value);
        LOGGER.trace(monitorGetString);
    }

    /**
     * Monitor set on a context item.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param value The value of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorSet(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final Object value, final AxConcept[] userArtifactStack) {
        String monitorSetString = monitor("SET", userArtifactStack, albumKey, schemaKey, name, value);
        LOGGER.trace(monitorSetString);
    }

    /**
     * Monitor a read lock on a key.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorReadLock(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final AxConcept[] userArtifactStack) {
        String monitorReadLockString = monitor("READLOCK", userArtifactStack, albumKey, schemaKey, name, null);
        LOGGER.trace(monitorReadLockString);
    }

    /**
     * Monitor a write lock on a key.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorWriteLock(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final AxConcept[] userArtifactStack) {
        String writeLockMonitorString = monitor("WRITELOCK", userArtifactStack, albumKey, schemaKey, name, null);
        LOGGER.trace(writeLockMonitorString);
    }

    /**
     * Monitor a read unlock on a key.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorReadUnlock(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final AxConcept[] userArtifactStack) {
        String monitorReadUnlockString = monitor("READUNLOCK", userArtifactStack, albumKey, schemaKey, name, null);
        LOGGER.trace(monitorReadUnlockString);
    }

    /**
     * Monitor a write unlock on a key.
     *
     * @param albumKey The item album
     * @param schemaKey The item schema
     * @param name The name of the item
     * @param userArtifactStack the keys of the artifacts using the context map at the moment
     */
    public void monitorWriteUnlock(final AxArtifactKey albumKey, final AxArtifactKey schemaKey, final String name,
            final AxConcept[] userArtifactStack) {
        String monitorWriteUnlockString = monitor("WRITEUNLOCK", userArtifactStack, albumKey, schemaKey, name, null);
        LOGGER.trace(monitorWriteUnlockString);
    }

    /**
     * Monitor the user artifact stack.
     *
     * @param preamble the preamble
     * @param userArtifactStack The user stack to print
     * @param albumKey the album key
     * @param schemaKey the schema key
     * @param name the name
     * @param value the value
     * @return the string
     */
    private String monitor(final String preamble, final AxConcept[] userArtifactStack, final AxArtifactKey albumKey,
            final AxArtifactKey schemaKey, final String name, final Object value) {
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

        builder.append(albumKey.getId());
        builder.append(',');
        builder.append(schemaKey.getId());
        builder.append(',');
        builder.append(name);

        if (value != null) {
            builder.append(',');
            builder.append(value.toString());
        }

        return builder.toString();
    }
}
