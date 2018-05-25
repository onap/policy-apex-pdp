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

package org.onap.policy.apex.model.basicmodel.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.dao.converters.CDATAConditioner;
import org.onap.policy.apex.model.basicmodel.dao.converters.UUID2String;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestDaoMisc {

    @Test
    public void testUUID2StringMopUp() {
        final UUID2String uuid2String = new UUID2String();
        assertEquals("", uuid2String.convertToDatabaseColumn(null));
    }

    @Test
    public void testCDataConditionerMopUp() {
        assertNull(CDATAConditioner.clean(null));
    }

    @Test
    public void testDaoFactory() {
        final DAOParameters daoParameters = new DAOParameters();

        daoParameters.setPluginClass("somewhere.over.the.rainbow");
        try {
            new ApexDaoFactory().createApexDao(daoParameters);
            fail("test shold throw an exception here");
        } catch (final Exception e) {
            assertEquals("Apex DAO class not found for DAO plugin \"somewhere.over.the.rainbow\"", e.getMessage());
        }

        daoParameters.setPluginClass("java.lang.String");
        try {
            new ApexDaoFactory().createApexDao(daoParameters);
            fail("test shold throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "Specified Apex DAO plugin class \"java.lang.String\" does not implement the ApexDao interface",
                    e.getMessage());
        }
    }

    @Test
    public void testDaoParameters() {
        final DAOParameters pars = new DAOParameters();
        pars.setJdbcProperties(new Properties());
        assertEquals(0, pars.getJdbcProperties().size());

        pars.setJdbcProperty("name", "Dorothy");
        assertEquals("Dorothy", pars.getJdbcProperty("name"));

        pars.setPersistenceUnit("Kansas");
        assertEquals("Kansas", pars.getPersistenceUnit());

        pars.setPluginClass("somewhere.over.the.rainbow");
        assertEquals("somewhere.over.the.rainbow", pars.getPluginClass());

        assertEquals(
                "DAOParameters [pluginClass=somewhere.over.the.rainbow, persistenceUnit=Kansas, jdbcProperties={name=Dorothy}]",
                pars.toString());
    }
}
