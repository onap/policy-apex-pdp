/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.javascript;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.ContextAlbumImpl;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * Test the JavaTaskExecutor class.
 *
 */
public class JavascriptTaskExecutorTest {
    /**
     * Set ups everything for the test.
     */
    @BeforeClass
    public static void prepareForTest() {
        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters()
                .setPluginClass("org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager");

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        final SchemaParameters schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    /**
     * Clear down the test data.
     */
    @AfterClass
    public static void cleanUpAfterTest() {
        ParameterService.deregister(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.LOCKING_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.SCHEMA_GROUP_NAME);
        ParameterService.deregister(ContextParameterConstants.MAIN_GROUP_NAME);
        ParameterService.clear();
    }

    @Test
    public void testJavascriptTaskExecutor() throws Exception {
        JavascriptTaskExecutor jte = new JavascriptTaskExecutor();
        assertNotNull(jte);

        assertThatThrownBy(() -> {
            jte.prepare();
        }).isInstanceOf(NullPointerException.class);

        AxTask task = new AxTask();
        final ApexInternalContext internalContext = new ApexInternalContext(new AxPolicyModel());

        jte.setContext(null, task, internalContext);

        task.getTaskLogic().setLogic("return boolean;");
        jte.prepare();

        Map<String, Object> incomingParameters2 = new HashMap<>();
        assertThatThrownBy(() -> {
            jte.execute(-1, new Properties(), incomingParameters2);
        }).hasMessage("execute: logic failed to run for \"NULL:0.0.0\"");

        task.getTaskLogic().setLogic("var x = 5;");
        jte.prepare();

        assertThatThrownBy(() -> {
            jte.execute(-1, new Properties(), null);
        }).isInstanceOf(NullPointerException.class);

        Map<String, Object> incomingParameters = new HashMap<>();
        assertThatThrownBy(() -> {
            jte.execute(-1, new Properties(), incomingParameters);
        }).hasMessage("execute: logic failed to set a return value for \"NULL:0.0.0\"");

        task.getTaskLogic().setLogic("var returnValueType = Java.type(\"java.lang.Boolean\");\n"
                + "var returnValue = new returnValueType(false); ");

        assertThatThrownBy(() -> {
            jte.prepare();
            jte.execute(-1, new Properties(), incomingParameters);
        }).hasMessage("execute-post: task logic execution failure on task \"NULL\" in model NULL:0.0.0");

        task.getTaskLogic().setLogic("var returnValueType = Java.type(\"java.lang.Boolean\");\r\n"
                + "var returnValue = new returnValueType(true); ");

        jte.prepare();
        Map<String, Object> returnMap = jte.execute(0, new Properties(), incomingParameters);
        assertEquals(0, returnMap.size());
        jte.cleanUp();
    }

    @Test
    public void testJavascriptTaskExecutorLogic() throws Exception {
        JavascriptTaskExecutor jte = new JavascriptTaskExecutor();
        assertNotNull(jte);

        assertThatThrownBy(() -> {
            jte.prepare();
        }).isInstanceOf(NullPointerException.class);

        AxTask task = new AxTask(new AxArtifactKey("TestTask:0.0.1"));

        ContextAlbum contextAlbum = createTestContextAlbum();

        final ApexInternalContext internalContext = new ApexInternalContext(new AxPolicyModel());
        internalContext.getContextAlbums().put(contextAlbum.getKey(), contextAlbum);

        task.getContextAlbumReferences().add(contextAlbum.getKey());
        task.getOutputFields().put("par0", null);
        task.getOutputFields().put("par1", null);

        jte.setContext(null, task, internalContext);

        Map<String, Object> incomingParameters = new HashMap<>();
        incomingParameters.put("par0", "value0");

        task.getTaskLogic().setLogic(TextFileUtils.getTextFileAsString("src/test/resources/javascript/TestLogic00.js"));

        jte.prepare();
        jte.execute(-1, new Properties(), incomingParameters);

        task.getTaskLogic().setLogic(TextFileUtils.getTextFileAsString("src/test/resources/javascript/TestLogic01.js"));
        jte.prepare();

        Map<String, Object> outcomingParameters = jte.execute(-1, new Properties(), incomingParameters);

        assertEquals("returnVal0", outcomingParameters.get("par0"));
        assertEquals("returnVal1", outcomingParameters.get("par1"));
    }

    private ContextAlbum createTestContextAlbum() throws ContextException {
        AxContextSchemas schemas = new AxContextSchemas();
        AxContextSchema simpleStringSchema =
                new AxContextSchema(new AxArtifactKey("SimpleStringSchema", "0.0.1"), "JAVA", "java.lang.String");
        schemas.getSchemasMap().put(simpleStringSchema.getKey(), simpleStringSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxContextAlbum axContextAlbum = new AxContextAlbum(new AxArtifactKey("TestContextAlbum", "0.0.1"), "Policy",
                true, AxArtifactKey.getNullKey());

        axContextAlbum.setItemSchema(simpleStringSchema.getKey());
        Distributor distributor = new JvmLocalDistributor();
        distributor.init(axContextAlbum.getKey());
        return new ContextAlbumImpl(axContextAlbum, distributor, new LinkedHashMap<String, Object>());
    }
}
