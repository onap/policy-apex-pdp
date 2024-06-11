/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.eventmodel.concepts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * Test event models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class EventModelTest {

    @Test
    void testEventModel() {
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

        assertNotEquals(0, model.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(model, model); // NOSONAR
        assertEquals(model, clonedModel);
        assertNotEquals(model, (Object) "Hello");
        assertNotEquals(model, new AxEventModel(new AxArtifactKey()));
        assertNotEquals(model, new AxEventModel(modelKey, new AxContextSchemas(), new AxKeyInformation(keyInfoKey),
            new AxEvents(eventsKey)));
        assertNotEquals(model, new AxEventModel(modelKey, new AxContextSchemas(schemasKey), new AxKeyInformation(),
            new AxEvents(eventsKey)));
        assertNotEquals(model, new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxEvents()));
        assertEquals(model, new AxEventModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey)));

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
