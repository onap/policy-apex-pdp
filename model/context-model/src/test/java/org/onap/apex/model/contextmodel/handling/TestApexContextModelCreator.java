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

package org.onap.apex.model.contextmodel.handling;

import java.util.UUID;

import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.apex.model.contextmodel.concepts.AxContextSchemas;

public class TestApexContextModelCreator implements TestApexModelCreator<AxContextModel> {

	@Override
	public AxContextModel getModel() {
		final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java", "com.ericsson.apex.concept.TestContextItem000");
		final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"),    "Java", "com.ericsson.apex.concept.TestContextItem00A");

		final AxContextSchemas contextSchemas = new AxContextSchemas(new AxArtifactKey("ContextSchemas", "0.0.1"));
		contextSchemas.getSchemasMap().put(schema0.getKey(), schema0);
		contextSchemas.getSchemasMap().put(schema1.getKey(), schema1);

		final AxContextAlbum contextAlbum0 = new AxContextAlbum(new AxArtifactKey("contextAlbum0", "0.0.1"), "APPLICATION", true,  schema0.getKey());
		final AxContextAlbum contextAlbum1 = new AxContextAlbum(new AxArtifactKey("contextAlbum1", "0.0.1"), "GLOBAL"     , false, schema1.getKey());

		final AxContextAlbums axContext = new AxContextAlbums(new AxArtifactKey("contextAlbums", "0.0.1"));
		axContext.getAlbumsMap().put(contextAlbum0.getKey(), contextAlbum0);
		axContext.getAlbumsMap().put(contextAlbum1.getKey(), contextAlbum1);

		final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
		final AxContextModel contextModel = new AxContextModel(new AxArtifactKey("ContextModel", "0.0.1"), contextSchemas, axContext, keyInformation);

		contextModel.setKeyInformation(keyInformation);
		contextModel.getKeyInformation().getKeyInfoMap().put(schema0       .getKey(), new AxKeyInfo(schema0       .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000001"), "axContextSchema0"));
		contextModel.getKeyInformation().getKeyInfoMap().put(schema1       .getKey(), new AxKeyInfo(schema1       .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000002"), "axContextSchema1"));
		contextModel.getKeyInformation().getKeyInfoMap().put(contextSchemas.getKey(), new AxKeyInfo(contextSchemas.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000003"), "ContextSchemas"));
		contextModel.getKeyInformation().getKeyInfoMap().put(contextAlbum0 .getKey(), new AxKeyInfo(contextAlbum0 .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000004"), "contextAlbum0"));
		contextModel.getKeyInformation().getKeyInfoMap().put(contextAlbum1 .getKey(), new AxKeyInfo(contextAlbum1 .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000005"), "contextAlbum1"));
		contextModel.getKeyInformation().getKeyInfoMap().put(axContext     .getKey(), new AxKeyInfo(axContext     .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000006"), "axContext"));
		contextModel.getKeyInformation().getKeyInfoMap().put(contextModel  .getKey(), new AxKeyInfo(contextModel  .getKey(), UUID.fromString("00000000-0000-0000-0000-000000000007"), "contextModel"));
		contextModel.getKeyInformation().getKeyInfoMap().put(keyInformation.getKey(), new AxKeyInfo(keyInformation.getKey(), UUID.fromString("00000000-0000-0000-0000-000000000008"), "keyInformation"));

		return contextModel;
	}

	@Override
	public AxContextModel getInvalidModel() {
		final AxContextModel contextModel = getModel();
		
		contextModel.getAlbums().get(new AxArtifactKey("contextAlbum0", "0.0.1")).setScope("UNDEFINED");

		contextModel.getSchemas().get(new AxArtifactKey("StringType", "0.0.1")).setSchema("");

		return contextModel;
	}

	public AxContextModel getMalstructuredModel() {
		final AxContextModel contextModel = getModel();
		
		contextModel.getAlbums().get(new AxArtifactKey("contextAlbum1", "0.0.1")).setKey(new AxArtifactKey("contextAlbum1", "0.0.2"));;
		contextModel.getSchemas().get(new AxArtifactKey("MapType", "0.0.1")).setKey(new AxArtifactKey("MapType", "0.0.2"));;

		return contextModel;
	}

	@Override
	public AxContextModel getObservationModel() {
		final AxContextModel contextModel = getModel();
		
		contextModel.getKeyInformation().get("contextAlbum1", "0.0.1").setDescription("");

		return contextModel;
	}

	@Override
	public AxContextModel getWarningModel() {
		final AxContextModel contextModel = getModel();
		
		contextModel.getKeyInformation().get("contextAlbum1", "0.0.1").setUuid(UUID.fromString("00000000-0000-0000-0000-000000000000"));

		return contextModel;
	}
}
