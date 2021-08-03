/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * Test event models.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventModelTest {

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
        assertThat(model.toString()).startsWith("AxEventModel(super=AxEventModel:(key=AxArtifactKey:(nam");

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
