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

package org.onap.policy.apex.client.editor.rest.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * Test Apex Editor Rest Resource.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEditorRestResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(ApexEditorRestResource.class);
    }

    @Test
    public void testSessionCreate() {
        ApexApiResult result = target("editor/-2/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));

        result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());

        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        try {
            target("editor/" + corruptSessionId + "/Model/Analyse").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/Analyse").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Model/Analyse").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/Model/Analyse").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Model/Validate").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/Validate").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/-12345/Model/Validate").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/Model/Validate").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        final String modelString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002699\","
                + "\"description\"      : \"A description of the model\"" + "}";
        final Entity<String> csEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Model/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/-12345/Model/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Model/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Model/Create").request().post(csEntity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-12345/Model/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/-12345/Model/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Model/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Model/Update").request().put(csEntity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        try {
            result = target("editor/" + corruptSessionId + "/Model/GetKey").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/GetKey").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Model/GetKey").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/Model/GetKey").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        try {
            result = target("editor/" + corruptSessionId + "/Model/Get").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/Get").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Model/Get").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/Model/Get").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        String resultString = target("editor/" + corruptSessionId + "/Model/Download").request().get(String.class);
        assertEquals("", resultString);

        resultString = target("editor/" + sessionId + "/Model/Download").request().get(String.class);
        assertNotNull(resultString);

        resultString = target("editor/-12345/Model/Download").request().get(String.class);
        assertEquals("", resultString);

        resultString = target("editor/12345/Model/Download").request().get(String.class);
        assertEquals("", resultString);

        try {
            result = target("editor/" + corruptSessionId + "/KeyInformation/Get").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/KeyInformation/Get").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        result = target("editor/-12345/KeyInformation/Get").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/KeyInformation/Get").request().get(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());

        try {
            result = target("editor/" + corruptSessionId + "/Model/Delete").request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/Delete").request().delete(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Model/Delete").request().delete(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
        result = target("editor/12345/Model/Delete").request().delete(ApexApiResult.class);
        assertEquals(Result.FAILED, result.getResult());
    }

    @Test
    public void testContextSchema() throws IOException {
        ApexApiResult result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));

        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        result = target("editor/-12345/Validate/ContextSchema").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Validate/ContextSchema").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/ContextSchema").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/ContextSchema").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/ContextSchema").queryParam("name", "%%%$£")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        Entity<String> modelEntity = Entity.entity("Somewhere over the rainbow", MediaType.APPLICATION_JSON);
        result = target("editor/" + -12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + 12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity("", MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextSchema/Get").queryParam("name", (String) null)
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        String csString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"schemaFlavour\"    : \"Java\"," + "\"schemaDefinition\" : \"java.lang.String\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        Entity<String> csEntity = Entity.entity(csString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/ContextSchema/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/ContextSchema/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Create").request().post(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextSchema/Create").request().post(csEntity,
                    ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        csString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"schemaFlavour\"    : \"Java\"," + "\"schemaDefinition\" : \"my.perfect.String\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        csEntity = Entity.entity(csString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/ContextSchema/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/ContextSchema/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Update").request().put(csEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextSchema/Update").request().put(csEntity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/ContextSchema/Get").queryParam("name", "Hello")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Get").queryParam("name", "NonExistant")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = target("editor/-123345/ContextSchema/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/ContextSchema/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextSchema/Get").queryParam("name", "Hello")
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/ContextSchema").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextSchema/Delete").queryParam("name", "Hello")
                    .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-123345/ContextSchema/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/ContextSchema/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Delete").queryParam("name", "Hello")
                .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextSchema/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    public void testContextAlbum() throws IOException {
        ApexApiResult result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));
        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        result = target("editor/-12345/Validate/ContextAlbum").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Validate/ContextAlbum").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/ContextAlbum").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/ContextAlbum").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/ContextAlbum").queryParam("name", "%%%$£")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        Entity<String> modelEntity = Entity.entity("Somewhere over the rainbow", MediaType.APPLICATION_JSON);
        result = target("editor/" + -12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + 12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity("", MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String caString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"scope\"            : \"Domain\"," + "\"writeable\"        : false,"
                + "\"itemSchema\"       : {\"name\" : \"StringType\", \"version\" : \"0.0.1\"},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        Entity<String> caEntity = Entity.entity(caString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/ContextAlbum/Create").request().post(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/ContextAlbum/Create").request().post(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Create").request().post(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextAlbum/Create").request().post(caEntity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        caString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"scope\"            : \"Global\"," + "\"writeable\"        : false,"
                + "\"itemSchema\"       : {\"name\" : \"StringType\", \"version\" : \"0.0.1\"},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        caEntity = Entity.entity(caString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/ContextAlbum/Update").request().put(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/ContextAlbum/Update").request().put(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Update").request().put(caEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextAlbum/Update").request().put(caEntity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        try {
            target("editor/" + corruptSessionId + "/ContextAlbum/Get").queryParam("name", "Hello")
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/ContextAlbum/Get").queryParam("name", "Hello")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Get").queryParam("name", "IDontExist")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = target("editor/-123345/ContextAlbum/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/ContextAlbum/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = target("editor/" + sessionId + "/Validate/ContextAlbum").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/ContextAlbum/Delete").queryParam("name", (String) null)
                    .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-123345/ContextAlbum/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/ContextAlbum/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Delete").queryParam("name", "Hello")
                .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/ContextAlbum/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    public void testEvent() throws IOException {
        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        ApexApiResult result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));

        result = target("editor/-12345/Validate/Event").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = target("editor/" + sessionId + "/Validate/Event").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Validate/Event").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/Event").queryParam("name", "%%%$£")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        Entity<String> modelEntity = Entity.entity("Somewhere over the rainbow", MediaType.APPLICATION_JSON);
        result = target("editor/" + -12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + 12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity("", MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        Entity<String> entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : \"Hiya\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\"," + "\"parameters\"       : {},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\","
                + "\"parameters\"       : {\"Par0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"Par0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\"," + "\"parameters\"       : {\"Par0\" : null},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\","
                + "\"parameters\"       : {\"Par0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"Par0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\","
                + "\"source\"           : \"start\"," + "\"target\"           : \"finish\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Event/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Event/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Event/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Event/Update").request().put(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\","
                + "\"source\"           : \"start\"," + "\"target\"           : \"finish\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"NonExistantEvent\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\","
                + "\"source\"           : \"start\"," + "\"target\"           : \"finish\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Event/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Event/Get").queryParam("name", "Hello")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Get").queryParam("name", "IDontExist")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = target("editor/-123345/Event/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Event/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Event/Get").queryParam("name", "Hello")
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Event/Delete").queryParam("name", (String) null)
                    .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-123345/Event/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Event/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Event/Delete").queryParam("name", "Hello")
                .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    public void testTask() throws IOException {
        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        ApexApiResult result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));

        result = target("editor/-12345/Validate/Task").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = target("editor/" + sessionId + "/Validate/Task").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Validate/Task").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/Task").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Validate/Task").queryParam("name", "%%%$£")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        Entity<String> modelEntity = Entity.entity("Somewhere over the rainbow", MediaType.APPLICATION_JSON);
        result = target("editor/" + -12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + 12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity("", MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        Entity<String> entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : \"Hiya\"," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing\"," + "\"version\"          : \"0.0.2\","
                + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"IField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi\"," + "\"version\"          : \"0.0.2\","
                + "\"inputFields\"      : {\"IField0\" : null},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay\"," + "\"version\"          : \"0.0.2\","
                + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"IField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy\"," + "\"version\"          : \"0.0.2\","
                + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"NotIField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing2\"," + "\"version\"          : \"0.0.2\","
                + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"OField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi2\"," + "\"version\"          : \"0.0.2\","
                + "\"outputFields\"     : {\"OField0\" : null},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay2\"," + "\"version\"          : \"0.0.2\","
                + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\","
                + " \"localName\" : \"OField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy2\"," + "\"version\"          : \"0.0.2\","
                + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
                + "\"localName\" : \"NotOField0\", \"optional\" : false}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing3\"," + "\"version\"          : \"0.0.2\","
                + "\"taskLogic\"        : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons,"
                + " lots of lime\"}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi3\"," + "\"version\"          : \"0.0.2\","
                + "\"taskLogic\"        : null," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay3\"," + "\"version\"          : \"0.0.2\","
                + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
                + "\"target\"           : \"end\","
                + "\"taskLogic\"        : {\"logicFlavour\" : \"UNDEFINED\", \"logic\" : \"lots of lemons,"
                + " lots of lime\"}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy3\"," + "\"version\"          : \"0.0.2\","
                + "\"taskLogic\"        : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing4\"," + "\"version\"          : \"0.0.2\","
                + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"Par0\", "
                + "\"defaultValue\" : \"Parameter Defaultvalue\"}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi4\"," + "\"version\"          : \"0.0.2\","
                + "\"parameters\"       : {\"Par0\" : null},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay4\"," + "\"version\"          : \"0.0.2\","
                + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"NotPar0\", \"defaultValue\" : "
                + "\"Parameter Defaultvalue\"}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy4\"," + "\"version\"          : \"0.0.2\","
                + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"MyParameter\", \"defaultValue\" : null}},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing5\"," + "\"version\"          : \"0.0.2\","
                + "\"contexts\"         : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi5\"," + "\"version\"          : \"0.0.2\","
                + "\"contexts\"         : []," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay5\"," + "\"version\"          : \"0.0.2\","
                + "\"contexts\"         : [{\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\"}],"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy5\"," + "\"version\"          : \"0.0.2\","
                + "\"contexts\"         : [{\"name\" : null, \"version\" : \"0.0.1\"}],"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Task/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Task/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Task/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Task/Update").request().put(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"NonExistantEvent\"," + "\"version\"          : \"0.0.2\","
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Task/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Task/Get").queryParam("name", "Hello")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Task/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Task/Get").queryParam("name", "IDontExist")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = target("editor/-123345/Task/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Task/Get").queryParam("name", (String) null).queryParam("version", (String) null)
                .request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Task/Get").queryParam("name", "Hello")
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Task/Delete").queryParam("name", (String) null)
                    .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-123345/Task/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Task/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Task/Delete").queryParam("name", "Hello")
                .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Task/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    public void testPolicy() throws IOException {
        final int corruptSessionId = ApexEditorRestResource.createCorruptSession();

        ApexApiResult result = target("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = new Integer(result.getMessages().get(0));

        result = target("editor/-12345/Model/Validate").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = target("editor/" + sessionId + "/Model/Validate").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Model/Validate").request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Model/Validate").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = target("editor/" + sessionId + "/Model/Validate").queryParam("name", "%%%$£")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        Entity<String> modelEntity = Entity.entity("Somewhere over the rainbow", MediaType.APPLICATION_JSON);
        result = target("editor/" + -12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + 12345 + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity("", MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelEntity = Entity.entity(modelString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Model/Load").request().put(modelEntity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Event/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        Entity<String> entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : \"GoodTaSeeYa\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : null," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HelloAnother\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello2\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : null,"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello3\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : null," + "  \"stateOutputs\"   : {" + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello4\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"stateOutputs\"   : null," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
                + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello5\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : null" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello6\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello7\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : null" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello8\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello9\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {" + "    \"event\"        : null," + "    \"nextState\"    : null" + "   }"
                + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
                + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        System.err.println(result);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello10\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {"
                + "    \"task\"         : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello11\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : null," + "    \"outputType\"   : \"DIRECT\","
                + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello12\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"}," + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }"
                + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello13\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null},"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }"
                + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello14\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }"
                + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello15\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [{\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"}],"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }"
                + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello16\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [null]," + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }"
                + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello17\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
                + "  \"finalizers\"         : {"
                + "   \"sf0\"               : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"}"
                + "  }" + " }" + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello18\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
                + "  \"finalizers\"         : {" + "   \"sf0\"               : null" + "  }" + " }" + "},"
                + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello19\"," + "\"version\"              : \"0.0.2\","
                + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
                + "\"states\"               : {" + " \"state\"               : {"
                + "  \"name\"               : \"state\","
                + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
                + "lots of lime\"},"
                + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
                + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
                + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
                + "   \"tr0\"               : {"
                + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
                + "  \"finalizers\"         : {"
                + "   \"sf0\"               : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null}" + "  }" + " }"
                + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"          : \"A description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Create").request().post(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HelloAnother\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A better description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/-12345/Policy/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/1234545/Policy/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = target("editor/" + sessionId + "/Policy/Update").queryParam("firstStatePeriodic", "true").request()
                .put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Policy/Update").request().put(entity, ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A better description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"IDontExist\"," + "\"version\"          : \"0.0.2\","
                + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
                + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
                + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
                + "   \"so0\"           : {"
                + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
                + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {"
                + "   \"tr0\"           : {" + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
                + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
                + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
                + "\"description\"      : \"A better description of hello\"" + "}";
        entity = Entity.entity(entityString, MediaType.APPLICATION_JSON);
        result = target("editor/" + sessionId + "/Policy/Update").request().put(entity, ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = target("editor/" + sessionId + "/Policy/Get").queryParam("name", "Hello")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Get").queryParam("name", "IDontExist")
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/-123345/Policy/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Policy/Get").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Policy/Get").queryParam("name", "Hello")
                    .queryParam("version", (String) null).request().get(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/" + sessionId + "/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/-12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/12345/Validate/Event").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        try {
            target("editor/" + corruptSessionId + "/Policy/Delete").queryParam("name", (String) null)
                    .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = target("editor/-123345/Policy/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/123345/Policy/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Delete").queryParam("name", "Hello")
                .queryParam("version", "0.0.2").request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = target("editor/" + sessionId + "/Policy/Delete").queryParam("name", (String) null)
                .queryParam("version", (String) null).request().delete(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }
}
