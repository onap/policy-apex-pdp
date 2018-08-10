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
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;

/**
 * This class creates sample Policy Models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestContextComparisonFactory {

    public AxContextModel getFullModel() {
        final AxContextSchema testContextSchema000 =
                new AxContextSchema(new AxArtifactKey("TestContextSchema000", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema000");
        final AxContextSchema testContextSchema00A =
                new AxContextSchema(new AxArtifactKey("TestContextSchema00A", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema00A");
        final AxContextSchema testContextSchema00C =
                new AxContextSchema(new AxArtifactKey("TestContextSchema00C", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema00C");

        final AxContextAlbum externalContextAlbum = new AxContextAlbum(
                new AxArtifactKey("ExternalContextAlbum", "0.0.1"), "EXTERNAL", false, testContextSchema000.getKey());
        final AxContextAlbum globalContextAlbum = new AxContextAlbum(new AxArtifactKey("GlobalContextAlbum", "0.0.1"),
                "GLOBAL", true, testContextSchema00A.getKey());
        final AxContextAlbum policy0ContextAlbum = new AxContextAlbum(new AxArtifactKey("Policy0ContextAlbum", "0.0.1"),
                "APPLICATION", true, testContextSchema00C.getKey());
        final AxContextAlbum policy1ContextAlbum = new AxContextAlbum(
                new AxArtifactKey("Policy1ContextAlbum ", "0.0.1"), "APPLICATION", true, testContextSchema00C.getKey());

        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("ContextModel", "0.0.1"));
        contextModel.getSchemas().getSchemasMap().put(testContextSchema000.getKey(), testContextSchema000);
        contextModel.getSchemas().getSchemasMap().put(testContextSchema00A.getKey(), testContextSchema00A);
        contextModel.getSchemas().getSchemasMap().put(testContextSchema00C.getKey(), testContextSchema00C);

        contextModel.getAlbums().getAlbumsMap().put(externalContextAlbum.getKey(), externalContextAlbum);
        contextModel.getAlbums().getAlbumsMap().put(globalContextAlbum.getKey(), globalContextAlbum);
        contextModel.getAlbums().getAlbumsMap().put(policy0ContextAlbum.getKey(), policy0ContextAlbum);
        contextModel.getAlbums().getAlbumsMap().put(policy1ContextAlbum.getKey(), policy1ContextAlbum);

        return contextModel;
    }

    public AxContextModel getEmptyModel() {
        return new AxContextModel(new AxArtifactKey("Context", "0.0.1"));
    }

    public AxContextModel getShellModel() {
        final AxContextSchema testContextSchema000 =
                new AxContextSchema(new AxArtifactKey("TestContextSchema000", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema000");
        final AxContextSchema testContextSchema00A =
                new AxContextSchema(new AxArtifactKey("TestContextSchema00A", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema00A");
        final AxContextSchema testContextSchema00C =
                new AxContextSchema(new AxArtifactKey("TestContextSchema00C", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema00C");

        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("ContextModel", "0.0.1"));
        contextModel.getSchemas().getSchemasMap().put(testContextSchema000.getKey(), testContextSchema000);
        contextModel.getSchemas().getSchemasMap().put(testContextSchema00A.getKey(), testContextSchema00A);
        contextModel.getSchemas().getSchemasMap().put(testContextSchema00C.getKey(), testContextSchema00C);

        return contextModel;
    }

    public AxContextModel getSingleEntryModel() {
        final AxContextSchema testContextSchema000 =
                new AxContextSchema(new AxArtifactKey("TestContextSchema000", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema000");

        final AxContextAlbum policy1ContextAlbum = new AxContextAlbum(
                new AxArtifactKey("Policy1ContextAlbum ", "0.0.1"), "APPLICATION", true, testContextSchema000.getKey());

        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("ContextModel", "0.0.1"));
        contextModel.getSchemas().getSchemasMap().put(testContextSchema000.getKey(), testContextSchema000);

        contextModel.getAlbums().getAlbumsMap().put(policy1ContextAlbum.getKey(), policy1ContextAlbum);

        return contextModel;
    }

    public AxContextModel getNoGlobalContextModel() {
        final AxContextSchema testContextSchema000 =
                new AxContextSchema(new AxArtifactKey("TestContextSchema000", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema000");
        final AxContextSchema testContextSchema00C =
                new AxContextSchema(new AxArtifactKey("TestContextSchema00C", "0.0.1"), "JAVA",
                        "org.onap.policy.apex.concept.TestContextSchema00C");

        final AxContextAlbum externalContextAlbum = new AxContextAlbum(
                new AxArtifactKey("ExternalContextAlbum", "0.0.1"), "EXTERNAL", false, testContextSchema000.getKey());
        final AxContextAlbum policy0ContextAlbum = new AxContextAlbum(new AxArtifactKey("Policy0ContextAlbum", "0.0.1"),
                "APPLICATION", true, testContextSchema00C.getKey());
        final AxContextAlbum policy1ContextAlbum = new AxContextAlbum(
                new AxArtifactKey("Policy1ContextAlbum ", "0.0.1"), "APPLICATION", true, testContextSchema00C.getKey());

        final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("ContextModel", "0.0.1"));
        contextModel.getSchemas().getSchemasMap().put(testContextSchema000.getKey(), testContextSchema000);
        contextModel.getSchemas().getSchemasMap().put(testContextSchema00C.getKey(), testContextSchema00C);

        contextModel.getAlbums().getAlbumsMap().put(externalContextAlbum.getKey(), externalContextAlbum);
        contextModel.getAlbums().getAlbumsMap().put(policy0ContextAlbum.getKey(), policy0ContextAlbum);
        contextModel.getAlbums().getAlbumsMap().put(policy1ContextAlbum.getKey(), policy1ContextAlbum);

        return contextModel;
    }
}
