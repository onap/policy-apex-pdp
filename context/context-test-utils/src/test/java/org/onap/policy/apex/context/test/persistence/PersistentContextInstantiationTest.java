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

package org.onap.policy.apex.context.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.context.test.concepts.TestContextDateItem;
import org.onap.policy.apex.context.test.concepts.TestContextDateLocaleItem;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.concepts.TestContextTreeMapItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.dao.ApexDao;
import org.onap.policy.apex.model.basicmodel.dao.ApexDaoFactory;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.onap.policy.common.parameters.ParameterService;

/**
 * The Class TestContextInstantiation.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class PersistentContextInstantiationTest {
    // Logger for this class
    // private static final XLogger logger =
    // XLoggerFactory.getXLogger(TestPersistentContextInstantiation.class);

    private Connection connection;
    private SchemaParameters schemaParameters;
    private ContextParameters contextParameters;

    @Before
    public void setup() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");
    }

    @After
    public void teardown() throws Exception {
        connection.close();
        new File("derby.log").delete();
    }

    /**
     * Set up context for tests.
     */
    @Before
    public void beforeTest() {
        contextParameters = new ContextParameters();

        contextParameters.setName(ContextParameterConstants.MAIN_GROUP_NAME);
        contextParameters.getDistributorParameters().setName(ContextParameterConstants.DISTRIBUTOR_GROUP_NAME);
        contextParameters.getLockManagerParameters().setName(ContextParameterConstants.LOCKING_GROUP_NAME);
        contextParameters.getPersistorParameters().setName(ContextParameterConstants.PERSISTENCE_GROUP_NAME);

        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());

        schemaParameters = new SchemaParameters();
        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters);
    }

    /**
     * Clear down context for tests.
     */
    @After
    public void afterTest() {
        ParameterService.deregister(schemaParameters);

        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters);
    }

    @Test
    public void testContextPersistentInstantiation() throws ApexModelException, IOException, ApexException {

        final AxArtifactKey distributorKey = new AxArtifactKey("AbstractDistributor", "0.0.1");
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        final AxArtifactKey[] usedArtifactStackArray = {
            new AxArtifactKey("testC-top", "0.0.1"),
            new AxArtifactKey("testC-next", "0.0.1"),
            new AxArtifactKey("testC-bot", "0.0.1")
        };

        final DaoParameters DaoParameters = new DaoParameters();
        DaoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        DaoParameters.setPersistenceUnit("DAOTest");
        final ApexDao apexDao = new ApexDaoFactory().createApexDao(DaoParameters);
        apexDao.init(DaoParameters);

        final AxContextModel someContextModel = TestContextAlbumFactory.createMultiAlbumsContextModel();

        // Context for Storing Map values
        final AxContextAlbum axContextAlbumForMap = someContextModel.getAlbums().getAlbumsMap()
                        .get(new AxArtifactKey("MapContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForMap);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForMap = contextDistributor.createContextAlbum(axContextAlbumForMap.getKey());
        assertNotNull(contextAlbumForMap);
        contextAlbumForMap.setUserArtifactStack(usedArtifactStackArray);

        final Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("key", "This is a policy context string");

        final Map<String, Object> valueMap0 = new HashMap<String, Object>();
        valueMap0.put("TestPolicyContextItem000", new TestContextTreeMapItem(testMap));

        contextAlbumForMap.putAll(valueMap0);

        assertEquals("This is a policy context string",
                        ((TestContextTreeMapItem) contextAlbumForMap.get("TestPolicyContextItem000")).getMapValue()
                                        .get("key"));

        contextAlbumForMap.flush();

        // Context for Storing Date values
        final AxContextAlbum axContextAlbumForDate = someContextModel.getAlbums().getAlbumsMap()
                        .get(new AxArtifactKey("DateContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForDate);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForDate = contextDistributor.createContextAlbum(axContextAlbumForDate.getKey());
        assertNotNull(contextAlbumForDate);
        contextAlbumForDate.setUserArtifactStack(usedArtifactStackArray);

        final TestContextDateItem testDate = new TestContextDateItem(new Date());
        final TestContextDateLocaleItem tci00A = new TestContextDateLocaleItem();
        tci00A.setDateValue(testDate);
        tci00A.setTzValue(TimeZone.getTimeZone("Europe/Dublin").toString());
        tci00A.setDst(true);

        final Map<String, Object> valueMap1 = new HashMap<String, Object>();
        valueMap1.put("TestPolicyContextItem00A", tci00A);

        contextAlbumForDate.putAll(valueMap1);

        assertEquals(testDate, ((TestContextDateLocaleItem) contextAlbumForDate.get("TestPolicyContextItem00A"))
                        .getDateValue());
        assertEquals(true, ((TestContextDateLocaleItem) contextAlbumForDate.get("TestPolicyContextItem00A")).getDst());

        contextAlbumForDate.flush();

        // Context for Storing Long values
        final AxContextAlbum axContextAlbumForLong = someContextModel.getAlbums().getAlbumsMap()
                        .get(new AxArtifactKey("LTypeContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForLong);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForLong = contextDistributor.createContextAlbum(axContextAlbumForLong.getKey());
        assertNotNull(contextAlbumForLong);
        contextAlbumForLong.setUserArtifactStack(usedArtifactStackArray);

        final Map<String, Object> valueMap2 = new HashMap<String, Object>();
        valueMap2.put("TestPolicyContextItem0031", new TestContextLongItem(0xFFFFFFFFFFFFFFFFL));
        valueMap2.put("TestPolicyContextItem0032", new TestContextLongItem(0xFFFFFFFFFFFFFFFEL));
        valueMap2.put("TestPolicyContextItem0033", new TestContextLongItem(0xFFFFFFFFFFFFFFFDL));
        valueMap2.put("TestPolicyContextItem0034", new TestContextLongItem(0xFFFFFFFFFFFFFFFCL));
        valueMap2.put("TestPolicyContextItem0035", new TestContextLongItem(0xFFFFFFFFFFFFFFFBL));

        contextAlbumForLong.putAll(valueMap2);

        assertEquals(0xFFFFFFFFFFFFFFFFL,
                        ((TestContextLongItem) contextAlbumForLong.get("TestPolicyContextItem0031")).getLongValue());
        assertEquals(0xFFFFFFFFFFFFFFFEL,
                        ((TestContextLongItem) contextAlbumForLong.get("TestPolicyContextItem0032")).getLongValue());
        assertEquals(0xFFFFFFFFFFFFFFFDL,
                        ((TestContextLongItem) contextAlbumForLong.get("TestPolicyContextItem0033")).getLongValue());
        assertEquals(0xFFFFFFFFFFFFFFFCL,
                        ((TestContextLongItem) contextAlbumForLong.get("TestPolicyContextItem0034")).getLongValue());
        assertEquals(0xFFFFFFFFFFFFFFFBL,
                        ((TestContextLongItem) contextAlbumForLong.get("TestPolicyContextItem0035")).getLongValue());

        contextAlbumForLong.flush();
        contextDistributor.clear();
    }
}
