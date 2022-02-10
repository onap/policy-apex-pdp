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

package org.onap.policy.apex.model.contextmodel.handling;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * This class compares the context in two AxContext objects and returns the differences. The
 * differences are returned in a {@link KeyedMapDifference} object that contains the left, equal,
 * and right context schemas or albums.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextComparer {

    /**
     * Compare two {@link AxContextAlbums} objects, comparing their context albums one after
     * another.
     *
     * @param left the left context
     * @param right the right context
     * @return the difference
     */
    public KeyedMapDifference<AxArtifactKey, AxContextAlbum> compare(final AxContextAlbums left,
            final AxContextAlbums right) {
        // Find the difference between the AxContext objects
        return new KeyedMapComparer<AxArtifactKey, AxContextAlbum>().compareMaps(left.getAlbumsMap(),
                right.getAlbumsMap());
    }

    /**
     * Compare two {@link AxContextSchema} objects, comparing their context schemas one after
     * another.
     *
     * @param left the left context
     * @param right the right context
     * @return the difference
     */
    public KeyedMapDifference<AxArtifactKey, AxContextSchema> compare(final AxContextSchemas left,
            final AxContextSchemas right) {
        // Find the difference between the AxContext objects
        return new KeyedMapComparer<AxArtifactKey, AxContextSchema>().compareMaps(left.getSchemasMap(),
                right.getSchemasMap());
    }

}
