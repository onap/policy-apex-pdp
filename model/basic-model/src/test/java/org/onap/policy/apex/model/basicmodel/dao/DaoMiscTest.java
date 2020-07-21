/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Properties;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.dao.converters.CDataConditioner;
import org.onap.policy.apex.model.basicmodel.dao.converters.Uuid2String;

public class DaoMiscTest {

    @Test
    public void testUuid2StringMopUp() {
        final Uuid2String uuid2String = new Uuid2String();
        assertEquals("", uuid2String.convertToDatabaseColumn(null));
    }

    @Test
    public void testCDataConditionerMopUp() {
        assertNull(CDataConditioner.clean(null));
    }

    @Test
    public void testDaoFactory() {
        final DaoParameters daoParameters = new DaoParameters();

        daoParameters.setPluginClass("somewhere.over.the.rainbow");
        assertThatThrownBy(() -> new ApexDaoFactory().createApexDao(daoParameters))
            .hasMessage("Apex DAO class not found for DAO plugin \"somewhere.over.the.rainbow\"");
        daoParameters.setPluginClass("java.lang.String");
        assertThatThrownBy(() -> new ApexDaoFactory().createApexDao(daoParameters))
            .hasMessage("Specified Apex DAO plugin class \"java.lang.String\" "
                            + "does not implement the ApexDao interface");
    }

    @Test
    public void testDaoParameters() {
        final DaoParameters pars = new DaoParameters();
        pars.setJdbcProperties(new Properties());
        assertEquals(0, pars.getJdbcProperties().size());

        pars.setJdbcProperty("name", "Dorothy");
        assertEquals("Dorothy", pars.getJdbcProperty("name"));

        pars.setPersistenceUnit("Kansas");
        assertEquals("Kansas", pars.getPersistenceUnit());

        pars.setPluginClass("somewhere.over.the.rainbow");
        assertEquals("somewhere.over.the.rainbow", pars.getPluginClass());

        assertEquals("DAOParameters [pluginClass=somewhere.over.the.rainbow, "
                        + "persistenceUnit=Kansas, jdbcProperties={name=Dorothy}]", pars.toString());
    }
}
