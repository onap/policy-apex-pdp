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

package org.onap.policy.apex.tools.model.generator;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Getter methods for policy keys.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */

public class KeyInfoGetter {

    /** The policy model for the getters. */
    private final AxPolicyModel model;

    /**
     * Creates a new key getter.
     *
     * @param model the policy model to use
     */
    public KeyInfoGetter(final AxPolicyModel model) {
        this.model = model;
    }

    /**
     * Returns the key name as string.
     *
     * @param key the key to transform
     * @return key name as string, null if key was null
     */
    public String getName(final AxArtifactKey key) {
        if (key == null) {
            return null;
        }
        return key.getName();
    }

    /**
     * Returns the version of an artifact key.
     *
     * @param key the key to extract version from
     * @return version of the key, null if key was null
     */
    public String getVersion(final AxArtifactKey key) {
        if (key == null) {
            return null;
        }
        return key.getVersion();
    }

    /**
     * Returns the parent name for the key.
     *
     * @param key the key to process
     * @return parent name, null if key was null
     */
    public String getPName(final AxReferenceKey key) {
        if (key == null) {
            return null;
        }
        return key.getParentKeyName();
    }

    /**
     * Returns the parent version for the key.
     *
     * @param key the key to process
     * @return parent version, null if key was null
     */
    public String getPVersion(final AxReferenceKey key) {
        if (key == null) {
            return null;
        }
        return key.getParentKeyVersion();
    }

    /**
     * Returns the local name for the key.
     *
     * @param key the key to process
     * @return local name, null if key was null
     */
    public String getLName(final AxReferenceKey key) {
        if (key == null) {
            return null;
        }
        return key.getLocalName();
    }

    /**
     * Returns the local name of the parent for the key.
     *
     * @param key the key to process
     * @return local name of the parent, null if key was null
     */
    public String getPlName(final AxReferenceKey key) {
        if (key == null) {
            return null;
        }
        return key.getParentLocalName();
    }

    /**
     * Returns the UUID of an artifact key.
     *
     * @param key the key to extract version from
     * @return UUID of the key, null if key was null
     */
    public String getUuid(final AxArtifactKey key) {
        if (key == null) {
            return null;
        }
        final AxKeyInfo ki = model.getKeyInformation().get(key);
        if (ki == null || ki.getUuid() == null) {
            return null;
        }
        return ki.getUuid().toString();
    }

    /**
     * Returns the description of an artifact key.
     *
     * @param key the key to extract version from
     * @return description of the key, null if key was null
     */
    public String getDesc(final AxArtifactKey key) {
        if (key == null) {
            return null;
        }
        final AxKeyInfo ki = model.getKeyInformation().get(key);
        if (ki == null || ki.getDescription() == null) {
            return null;
        }
        return ki.getDescription();
    }
}
