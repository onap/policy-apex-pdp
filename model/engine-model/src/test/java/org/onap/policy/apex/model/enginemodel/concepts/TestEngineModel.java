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

package org.onap.policy.apex.model.enginemodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;

/**
 * Test engine models.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestEngineModel {

    @Test
    public void testEnginetModel() {
        assertNotNull(new AxEngineModel());
        assertNotNull(new AxEngineModel(new AxArtifactKey()));
        assertNotNull(new AxEngineModel(new AxArtifactKey(), new AxContextSchemas(), new AxKeyInformation(),
                new AxContextAlbums()));
        assertNotNull(new AxEngineModel(new AxArtifactKey(), new AxContextSchemas(), new AxKeyInformation(),
                new AxContextAlbums(), AxEngineState.UNDEFINED, new AxEngineStats()));

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

        try {
            model.setKey(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("key may not be null", e.getMessage());
        }

        model.setKey(modelKey);
        assertEquals("ModelName:0.0.1", model.getKey().getId());
        assertEquals("ModelName:0.0.1", model.getKeys().get(0).getId());

        final long timestamp = System.currentTimeMillis();
        model.setTimestamp(timestamp);
        assertEquals(timestamp, model.getTimestamp());
        model.setTimestamp(-1);
        assertTrue(model.getTimeStampString().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}"));

        try {
            model.setState(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("state may not be null", e.getMessage());
        }

        for (final AxEngineState state : AxEngineState.values()) {
            model.setState(state);
            assertEquals(state, model.getState());
        }

        model.setState(AxEngineState.READY);
        assertEquals(AxEngineState.READY, model.getState());

        try {
            model.setStats(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("stats may not be null", e.getMessage());
        }

        model.setStats(stats);
        assertEquals(stats, model.getStats());

        model.clean();
        assertNotNull(model);
        assertEquals("AxEngineModel:(AxEngineModel:(AxEngineModel:(key=A", model.toString().substring(0, 50));

        final AxEngineModel clonedModel = new AxEngineModel(model);

        assertFalse(model.hashCode() == 0);

        assertTrue(model.equals(model));
        assertTrue(model.equals(clonedModel));
        assertFalse(model.equals("Hello"));
        assertFalse(model.equals(new AxEngineModel(new AxArtifactKey())));
        assertFalse(model.equals(new AxEngineModel(new AxArtifactKey(), new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(), new AxKeyInformation(keyInfoKey),
                new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey), new AxKeyInformation(),
                new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(), AxEngineState.READY, stats)));
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.STOPPED, stats)));
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, otherStats)));
        model.setTimestamp(timestamp);
        assertFalse(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
        model.setTimestamp(0);
        assertTrue(model.equals(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));

        model.setTimestamp(-1);
        assertEquals(0, model.compareTo(model));
        assertEquals(0, model.compareTo(clonedModel));
        assertNotEquals(0, model.compareTo(new AxArtifactKey()));
        assertFalse(model.equals(new AxEngineModel(new AxArtifactKey())));
        assertNotEquals(0, model.compareTo(new AxEngineModel(new AxArtifactKey(), new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));
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
        model.setTimestamp(0);
        assertEquals(0, model.compareTo(new AxEngineModel(modelKey, new AxContextSchemas(schemasKey),
                new AxKeyInformation(keyInfoKey), new AxContextAlbums(albumKey), AxEngineState.READY, stats)));

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

        model.setState(AxEngineState.UNDEFINED);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.setState(AxEngineState.READY);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }
}
