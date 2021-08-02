/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is a POJO that holds properties for Apex DAOs.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
@ToString
public class DaoParameters {
    /** The default Apex DAO plugin class. */
    public static final String DEFAULT_PLUGIN_CLASS = "org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao";

    private String pluginClass = DEFAULT_PLUGIN_CLASS;
    private String persistenceUnit;

    private Properties jdbcProperties = new Properties();

    /**
     * Gets a single JDBC property.
     *
     * @param key the key of the property
     * @return the JDBC property
     */
    public String getJdbcProperty(final String key) {
        return jdbcProperties.getProperty(key);
    }

    /**
     * Sets a single JDBC property.
     *
     * @param key the key of the property
     * @param value the value of the JDBC property
     */
    public void setJdbcProperty(final String key, final String value) {
        jdbcProperties.setProperty(key, value);
    }
}
