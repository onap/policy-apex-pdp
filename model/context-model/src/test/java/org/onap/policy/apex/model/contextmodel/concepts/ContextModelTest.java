/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.model.contextmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;

/**
 * Context model tests.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextModelTest {

    @Test
    public void test() {
        assertNotNull(new AxContextModel());
        assertNotNull(new AxContextModel(new AxArtifactKey()));
        assertNotNull(new AxContextModel(new AxArtifactKey(), new AxContextSchemas(), new AxKeyInformation()));
        assertNotNull(new AxContextModel(new AxArtifactKey(), new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation()));

        final AxArtifactKey modelKey = new AxArtifactKey("ModelKey", "0.0.1");
        final AxArtifactKey schemasKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey albumsKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey keyInfoKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxContextModel model = new AxContextModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxContextAlbums(albumsKey), new AxKeyInformation(keyInfoKey));
        model.register();

        model.clean();
        assertNotNull(model);
        assertEquals("AxContextModel:(AxContextModel:(key=AxArtifactKey:", model.toString().substring(0, 50));

        final AxContextModel clonedModel = new AxContextModel(model);

        assertNotEquals(0, model.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(model, model); // NOSONAR
        assertEquals(model, clonedModel);
        assertNotEquals(model, "Hello");
        assertNotEquals(model, new AxContextModel(new AxArtifactKey()));
        assertNotEquals(model, new AxContextModel(new AxArtifactKey(), new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation()));
        assertNotEquals(model, new AxContextModel(modelKey, new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation()));
        assertNotEquals(model, new AxContextModel(modelKey, new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation(keyInfoKey)));
        assertNotEquals(model, new AxContextModel(modelKey, new AxContextSchemas(schemasKey), new AxContextAlbums(),
                        new AxKeyInformation(keyInfoKey)));
        assertEquals(model, new AxContextModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxContextAlbums(albumsKey), new AxKeyInformation(keyInfoKey)));

        assertEquals(0, model.compareTo(model));
        assertEquals(0, model.compareTo(clonedModel));
        assertNotEquals(0, model.compareTo(new AxArtifactKey()));
        assertNotEquals(0, model.compareTo(new AxContextModel(new AxArtifactKey(), new AxContextSchemas(),
                        new AxContextAlbums(), new AxKeyInformation())));
        assertNotEquals(0, model.compareTo(new AxContextModel(modelKey, new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation())));
        assertNotEquals(0, model.compareTo(new AxContextModel(modelKey, new AxContextSchemas(), new AxContextAlbums(),
                        new AxKeyInformation(keyInfoKey))));
        assertNotEquals(0, model.compareTo(new AxContextModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxContextAlbums(), new AxKeyInformation(keyInfoKey))));
        assertEquals(0, model.compareTo(new AxContextModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxContextAlbums(albumsKey), new AxKeyInformation(keyInfoKey))));
    }
}
