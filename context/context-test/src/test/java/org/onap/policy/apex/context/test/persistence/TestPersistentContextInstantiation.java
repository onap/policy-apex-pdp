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
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.apex.context.test.concepts.TestContextItem003;
import org.onap.policy.apex.context.test.concepts.TestContextItem008;
import org.onap.policy.apex.context.test.concepts.TestContextItem00A;
import org.onap.policy.apex.context.test.concepts.TestContextItem00C;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.dao.ApexDao;
import org.onap.policy.apex.model.basicmodel.dao.ApexDaoFactory;
import org.onap.policy.apex.model.basicmodel.dao.DAOParameters;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;

/**
 * The Class TestContextInstantiation.
 *
 * @author Sergey Sachkov (sergey.sachkov@ericsson.com)
 */
public class TestPersistentContextInstantiation {
    // Logger for this class
    // private static final XLogger logger = XLoggerFactory.getXLogger(TestPersistentContextInstantiation.class);

    private Connection connection;

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

    @After
    public void afterTest() throws IOException {}

    @Test
    public void testContextPersistentInstantiation() throws ApexModelException, IOException, ApexException {

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.setPersistorParameters(new PersistorParameters());

        final AxArtifactKey distributorKey = new AxArtifactKey("AbstractDistributor", "0.0.1");
        final Distributor contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        final AxArtifactKey[] usedArtifactStackArray = { new AxArtifactKey("testC-top", "0.0.1"),
                new AxArtifactKey("testC-next", "0.0.1"), new AxArtifactKey("testC-bot", "0.0.1") };

        final DAOParameters daoParameters = new DAOParameters();
        daoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        daoParameters.setPersistenceUnit("DAOTest");
        final ApexDao apexDao = new ApexDaoFactory().createApexDao(daoParameters);
        apexDao.init(daoParameters);

        final AxContextModel someContextModel = TestContextAlbumFactory.createMultiAlbumsContextModel();

        // Context for Storing Map values
        final AxContextAlbum axContextAlbumForMap =
                someContextModel.getAlbums().getAlbumsMap().get(new AxArtifactKey("MapContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForMap);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForMap = contextDistributor.createContextAlbum(axContextAlbumForMap.getKey());
        assertNotNull(contextAlbumForMap);
        contextAlbumForMap.setUserArtifactStack(usedArtifactStackArray);

        final Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("key", "This is a policy context string");

        final Map<String, Object> valueMap0 = new HashMap<String, Object>();
        valueMap0.put("TestPolicyContextItem000", new TestContextItem00C(testMap));

        contextAlbumForMap.putAll(valueMap0);

        assertEquals(((TestContextItem00C) contextAlbumForMap.get("TestPolicyContextItem000")).getMapValue().get("key"),
                "This is a policy context string");

        contextAlbumForMap.flush();

        // Context for Storing Date values
        final AxContextAlbum axContextAlbumForDate =
                someContextModel.getAlbums().getAlbumsMap().get(new AxArtifactKey("DateContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForDate);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForDate = contextDistributor.createContextAlbum(axContextAlbumForDate.getKey());
        assertNotNull(contextAlbumForDate);
        contextAlbumForDate.setUserArtifactStack(usedArtifactStackArray);

        final TestContextItem008 testDate = new TestContextItem008(new Date());
        final TestContextItem00A tci00A = new TestContextItem00A();
        tci00A.setDateValue(testDate);
        tci00A.setTZValue(TimeZone.getTimeZone("Europe/Dublin").toString());
        tci00A.setDST(true);

        final Map<String, Object> valueMap1 = new HashMap<String, Object>();
        valueMap1.put("TestPolicyContextItem00A", tci00A);

        contextAlbumForDate.putAll(valueMap1);

        assertEquals(((TestContextItem00A) contextAlbumForDate.get("TestPolicyContextItem00A")).getDateValue(),
                testDate);
        assertEquals(((TestContextItem00A) contextAlbumForDate.get("TestPolicyContextItem00A")).getDST(), true);

        contextAlbumForDate.flush();

        // Context for Storing Long values
        final AxContextAlbum axContextAlbumForLong =
                someContextModel.getAlbums().getAlbumsMap().get(new AxArtifactKey("LTypeContextAlbum", "0.0.1"));
        apexDao.create(axContextAlbumForLong);
        contextDistributor.registerModel(someContextModel);
        final ContextAlbum contextAlbumForLong = contextDistributor.createContextAlbum(axContextAlbumForLong.getKey());
        assertNotNull(contextAlbumForLong);
        contextAlbumForLong.setUserArtifactStack(usedArtifactStackArray);

        final Map<String, Object> valueMap2 = new HashMap<String, Object>();
        valueMap2.put("TestPolicyContextItem0031", new TestContextItem003(0xFFFFFFFFFFFFFFFFL));
        valueMap2.put("TestPolicyContextItem0032", new TestContextItem003(0xFFFFFFFFFFFFFFFEL));
        valueMap2.put("TestPolicyContextItem0033", new TestContextItem003(0xFFFFFFFFFFFFFFFDL));
        valueMap2.put("TestPolicyContextItem0034", new TestContextItem003(0xFFFFFFFFFFFFFFFCL));
        valueMap2.put("TestPolicyContextItem0035", new TestContextItem003(0xFFFFFFFFFFFFFFFBL));

        contextAlbumForLong.putAll(valueMap2);

        assertEquals(((TestContextItem003) contextAlbumForLong.get("TestPolicyContextItem0031")).getLongValue(),
                0xFFFFFFFFFFFFFFFFL);
        assertEquals(((TestContextItem003) contextAlbumForLong.get("TestPolicyContextItem0032")).getLongValue(),
                0xFFFFFFFFFFFFFFFEL);
        assertEquals(((TestContextItem003) contextAlbumForLong.get("TestPolicyContextItem0033")).getLongValue(),
                0xFFFFFFFFFFFFFFFDL);
        assertEquals(((TestContextItem003) contextAlbumForLong.get("TestPolicyContextItem0034")).getLongValue(),
                0xFFFFFFFFFFFFFFFCL);
        assertEquals(((TestContextItem003) contextAlbumForLong.get("TestPolicyContextItem0035")).getLongValue(),
                0xFFFFFFFFFFFFFFFBL);

        contextAlbumForLong.flush();
        contextDistributor.clear();

    }
}
