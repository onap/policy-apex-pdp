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

package org.onap.policy.apex.model.enginemodel.handling;

import java.util.UUID;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineState;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineStats;

public class TestApexEngineModelCreator implements TestApexModelCreator<AxEngineModel> {

    @Override
    public AxEngineModel getModel() {
        final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem000");
        final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem00A");

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("ContextSchemas", "0.0.1"));
        schemas.getSchemasMap().put(schema0.getKey(), schema0);
        schemas.getSchemasMap().put(schema1.getKey(), schema1);

        final AxContextAlbum album0 =
                new AxContextAlbum(new AxArtifactKey("contextAlbum0", "0.0.1"), "APPLICATION", true, schema1.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(album0.getKey(), album0);

        final AxEngineModel engineModel = new AxEngineModel(new AxArtifactKey("AnEngine", "0.0.1"));
        engineModel.setSchemas(schemas);
        engineModel.setAlbums(albums);
        engineModel.setTimestamp(System.currentTimeMillis());
        engineModel.setState(AxEngineState.EXECUTING);
        engineModel.setStats(new AxEngineStats(new AxReferenceKey(engineModel.getKey(), "EngineStats"),
                System.currentTimeMillis(), 100, 205, 200, 12345, 9876));
        engineModel.getKeyInformation().generateKeyInfo(engineModel);

        final AxValidationResult result = new AxValidationResult();
        engineModel.validate(result);

        return engineModel;
    }

    @Override
    public AxEngineModel getInvalidModel() {
        final AxEngineModel engineModel = getModel();

        engineModel.setTimestamp(System.currentTimeMillis());
        engineModel.setState(AxEngineState.UNDEFINED);
        engineModel.setStats(new AxEngineStats(new AxReferenceKey(engineModel.getKey(), "EngineStats"),
                System.currentTimeMillis(), 100, 205, 200, 12345, 9876));
        engineModel.getKeyInformation().generateKeyInfo(engineModel);

        return engineModel;
    }

    public AxEngineModel getMalstructuredModel() {
        final AxEngineModel engineModel = getModel();

        engineModel.setTimestamp(-1);
        engineModel.setState(AxEngineState.UNDEFINED);

        return engineModel;
    }

    @Override
    public AxEngineModel getObservationModel() {
        final AxEngineModel engineModel = getModel();

        final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem000");
        final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem00A");

        engineModel.getKeyInformation().getKeyInfoMap().put(schema0.getKey(),
                new AxKeyInfo(schema0.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000001"), ""));
        engineModel.getKeyInformation().getKeyInfoMap().put(schema1.getKey(),
                new AxKeyInfo(schema1.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000002"), ""));

        return engineModel;
    }

    @Override
    public AxEngineModel getWarningModel() {
        final AxEngineModel engineModel = getModel();

        final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem000");
        final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java",
                "org.onap.policy.apex.model.enginemodel.concepts.TestContextItem00A");

        engineModel.getKeyInformation().getKeyInfoMap().put(schema0.getKey(),
                new AxKeyInfo(schema0.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000000"), ""));
        engineModel.getKeyInformation().getKeyInfoMap().put(schema1.getKey(),
                new AxKeyInfo(schema1.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000001"), ""));

        return engineModel;
    }
}
