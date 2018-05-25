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

package org.onap.policy.apex.model.basicmodel.concepts;

import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.utilities.Assertions;

/**
 * Implements concept getting from navigable maps.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <C> the type of concept on which the interface implementation is applied.
 */
public class AxConceptGetterImpl<C> implements AxConceptGetter<C> {

    // The map from which to get concepts
    private final NavigableMap<AxArtifactKey, C> conceptMap;

    /**
     * Constructor that sets the concept map on which the getter methods in the interface will operate..
     *
     * @param conceptMap the concept map on which the method will operate
     */
    public AxConceptGetterImpl(final NavigableMap<AxArtifactKey, C> conceptMap) {
        this.conceptMap = conceptMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.core.basicmodel.concepts.AxConceptGetter#get(com.
     * ericsson.apex.core.basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public C get(final AxArtifactKey conceptKey) {
        return conceptMap.get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang. String)
     */
    @Override
    public C get(final String conceptKeyName) {
        Assertions.argumentNotNull(conceptKeyName, "conceptKeyName may not be null");

        // The very fist key that could have this name
        final AxArtifactKey lowestArtifactKey = new AxArtifactKey(conceptKeyName, "0.0.1");

        // Check if we found a key for our name
        AxArtifactKey foundKey = conceptMap.ceilingKey(lowestArtifactKey);
        if (foundKey == null || !foundKey.getName().equals(conceptKeyName)) {
            return null;
        }

        // Look for higher versions of the key
        do {
            final AxArtifactKey nextkey = conceptMap.higherKey(foundKey);
            if (nextkey == null || !nextkey.getName().equals(conceptKeyName)) {
                break;
            }
            foundKey = nextkey;
        } while (true);

        return conceptMap.get(foundKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang. String, java.lang.String)
     */
    @Override
    public C get(final String conceptKeyName, final String conceptKeyVersion) {
        Assertions.argumentNotNull(conceptKeyName, "conceptKeyName may not be null");

        if (conceptKeyVersion != null) {
            return conceptMap.get(new AxArtifactKey(conceptKeyName, conceptKeyVersion));
        } else {
            return this.get(conceptKeyName);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java. lang.String)
     */
    @Override
    public Set<C> getAll(final String conceptKeyName) {
        return getAll(conceptKeyName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java. lang.String, java.lang.String)
     */
    @Override
    public Set<C> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        final Set<C> returnSet = new TreeSet<>();

        if (conceptKeyName == null) {
            returnSet.addAll(conceptMap.values());
            return returnSet;
        }

        // The very fist key that could have this name
        final AxArtifactKey lowestArtifactKey = new AxArtifactKey(conceptKeyName, "0.0.1");
        if (conceptKeyVersion != null) {
            lowestArtifactKey.setVersion(conceptKeyVersion);
        }

        // Check if we found a key for our name
        AxArtifactKey foundKey = conceptMap.ceilingKey(lowestArtifactKey);
        if (foundKey == null || !foundKey.getName().equals(conceptKeyName)) {
            return returnSet;
        }
        returnSet.add(conceptMap.get(foundKey));

        // Look for higher versions of the key
        do {
            foundKey = conceptMap.higherKey(foundKey);
            if (foundKey == null || !foundKey.getName().equals(conceptKeyName)) {
                break;
            }
            returnSet.add(conceptMap.get(foundKey));
        } while (true);

        return returnSet;
    }
}
