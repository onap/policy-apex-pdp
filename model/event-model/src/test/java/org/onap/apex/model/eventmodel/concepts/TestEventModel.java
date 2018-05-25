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

package org.onap.apex.model.eventmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.apex.model.eventmodel.concepts.AxEventModel;
import org.onap.apex.model.eventmodel.concepts.AxEvents;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestEventModel {

    @Test
    public void testEventModel() {
        assertNotNull(new AxEventModel());
        assertNotNull(new AxEventModel(new AxArtifactKey()));
        assertNotNull(
                new AxEventModel(new AxArtifactKey(), new AxContextSchemas(), new AxKeyInformation(), new AxEvents()));

        final AxArtifactKey modelKey = new AxArtifactKey("ModelKey", "0.0.1");
        final AxArtifactKey schemasKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey eventsKey = new AxArtifactKey("EventsKey", "0.0.1");
        final AxArtifactKey keyInfoKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxEventModel model = new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey));
        model.register();

        model.clean();
        assertNotNull(model);
        assertEquals("AxEventModel:(AxEventModel:(key=AxArtifactKey:(nam", model.toString().substring(0, 50));

        final AxEventModel clonedModel = new AxEventModel(model);

        assertFalse(model.hashCode() == 0);

        assertTrue(model.equals(model));
        assertTrue(model.equals(clonedModel));
        assertFalse(model.equals("Hello"));
        assertFalse(model.equals(new AxEventModel(new AxArtifactKey())));
        assertFalse(model.equals(new AxEventModel(modelKey, new AxContextSchemas(), new AxKeyInformation(keyInfoKey),
                new AxEvents(eventsKey))));
        assertFalse(model.equals(new AxEventModel(modelKey, new AxContextSchemas(schemasKey), new AxKeyInformation(),
                new AxEvents(eventsKey))));
        assertFalse(model.equals(new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxEvents())));
        assertTrue(model.equals(new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey))));

        assertEquals(0, model.compareTo(model));
        assertEquals(0, model.compareTo(clonedModel));
        assertNotEquals(0, model.compareTo(new AxArtifactKey()));
        assertNotEquals(0, model.compareTo(new AxEventModel(modelKey, new AxContextSchemas(),
                new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey))));
        assertNotEquals(0, model.compareTo(new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(), new AxEvents(eventsKey))));
        assertNotEquals(0, model.compareTo(new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxEvents())));
        assertEquals(0, model.compareTo(new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey))));
    }
}
