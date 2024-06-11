/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.enginemodel.concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * Test engine models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class EngineModelTest {

    @Test
    void testEngineModel() {
        final AxArtifactKey modelKey = new AxArtifactKey("ModelName", "0.0.1");
        final AxArtifactKey schemasKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey albumKey = new AxArtifactKey("AlbumKey", "0.0.1");
        final AxArtifactKey keyInfoKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxEngineStats stats = new AxEngineStats(new AxReferenceKey(modelKey, "EngineStats"));
        final AxEngineStats otherStats = new AxEngineStats();
        otherStats.setAverageExecutionTime(100);

        final AxEngineModel model = new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats);
        model.register();

        assertThatThrownBy(() -> model.setKey(null)).hasMessage("key may not be null");
        model.setKey(modelKey);
        assertEquals("ModelName:0.0.1", model.getKey().getId());
        assertEquals("ModelName:0.0.1", model.getKeys().get(0).getId());

        final long timestamp = System.currentTimeMillis();
        model.setTimestamp(timestamp);
        assertEquals(timestamp, model.getTimestamp());
        model.setTimestamp(-1);
        assertTrue(model.getTimeStampString().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}"));

        assertThatThrownBy(() -> model.setState(null)).hasMessage("state may not be null");
        for (final AxEngineState state : AxEngineState.values()) {
            model.setState(state);
            assertEquals(state, model.getState());
        }

        model.setState(AxEngineState.READY);
        assertEquals(AxEngineState.READY, model.getState());

        assertThatThrownBy(() -> model.setStats(null)).hasMessage("stats may not be null");
        model.setStats(stats);
        assertEquals(stats, model.getStats());

        model.clean();
        assertNotNull(model);
        assertThat(model.toString()).startsWith("AxEngineModel:(AxContextModel(super=AxEngineModel:(key=A");

        final AxEngineModel clonedModel = new AxEngineModel(model);

        assertNotEquals(0, model.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEqualsEngineModel(model, clonedModel, schemasKey, keyInfoKey, albumKey, stats, modelKey, otherStats,
            timestamp);

        model.setTimestamp(-1);
        assertEquals(0, model.compareTo(model));
        assertEquals(0, model.compareTo(clonedModel));
        assertNotEqualsEngineModels(model, schemasKey, keyInfoKey, albumKey, stats, modelKey, otherStats, timestamp);
        model.setTimestamp(0);
        assertEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));

        assertSetTimestampToEngineModel(model, timestamp);

        assertSetStateToEngineModel(model);
    }

    private static void assertSetStateToEngineModel(AxEngineModel model) {
        AxValidationResult result;
        model.setState(AxEngineState.UNDEFINED);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.setState(AxEngineState.READY);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }

    private static void assertSetTimestampToEngineModel(AxEngineModel model, long timestamp) {
        model.setTimestamp(timestamp);
        AxValidationResult result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        model.setTimestamp(-1);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.setTimestamp(timestamp);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }

    private static void assertNotEqualsEngineModels(AxEngineModel model, AxArtifactKey schemasKey,
                                                    AxArtifactKey keyInfoKey, AxArtifactKey albumKey,
                                                    AxEngineStats stats, AxArtifactKey modelKey,
                                                    AxEngineStats otherStats, long timestamp) {
        assertNotEquals(0, model.compareTo(new AxArtifactKey()));
        assertNotEquals(model, new AxEngineModel(new AxArtifactKey()));
        assertNotEquals(0, model.compareTo(new AxEngineModel(new AxArtifactKey(),
            new AxContextSchemas(schemasKey), new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey),
            AxEngineState.READY, stats)));
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(), AxEngineState.READY, stats)));
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.STOPPED, stats)));
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, otherStats)));
        model.setTimestamp(timestamp);
        assertNotEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
    }

    private static void assertEqualsEngineModel(AxEngineModel model, AxEngineModel clonedModel,
                                                AxArtifactKey schemasKey,
                                                AxArtifactKey keyInfoKey, AxArtifactKey albumKey, AxEngineStats stats,
                                                AxArtifactKey modelKey, AxEngineStats otherStats, long timestamp) {
        assertEquals(model, model); // NOSONAR
        assertEquals(model, clonedModel);
        assertNotEquals(model, (Object) "Hello");
        assertNotEquals(model, new AxEngineModel(new AxArtifactKey()));
        assertNotEquals(model, new AxEngineModel(new AxArtifactKey(), new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats));
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(), new AxKeyInformation(keyInfoKey),
            new AxContextAlbums(albumKey), AxEngineState.READY, stats));
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey), new AxKeyInformation(),
            new AxContextAlbums(albumKey), AxEngineState.READY, stats));
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(), AxEngineState.READY, stats));
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.STOPPED, stats));
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, otherStats));
        model.setTimestamp(timestamp);
        assertNotEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats));
        model.setTimestamp(0);
        assertEquals(model, new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
            new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats));
    }
}
