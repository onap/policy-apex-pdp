/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.starter.parameters;

import java.util.Map;
import java.util.TreeMap;

import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.utils.coder.Coder;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class CommonTestData {

    public static final String APEX_STARTER_GROUP_NAME = "ApexStarterParameterGroup";
    public static final int APEX_STARTER_TIME_INTERVAL = 5;

    private static final Coder coder = new StandardCoder();

    /**
     * Converts the contents of a map to a parameter class.
     *
     * @param source property map
     * @param clazz class of object to be created from the map
     * @return a new object represented by the map
     */
    public <T extends ParameterGroup> T toObject(final Map<String, Object> source, final Class<T> clazz) {
        try {
            return coder.decode(coder.encode(source), clazz);

        } catch (final CoderException e) {
            throw new RuntimeException("cannot create " + clazz.getName() + " from map", e);
        }
    }

    /**
     * Returns a property map for a ApexStarterParameterGroup map for test cases.
     *
     * @param name name of the parameters
     *
     * @return a property map suitable for constructing an object
     */
    public Map<String, Object> getApexStarterParameterGroupMap(final String name) {
        final Map<String, Object> map = new TreeMap<>();

        map.put("name", name);
        map.put("timeInterval", getTimeInterval(false));

        return map;
    }



    /**
     * Determines whether to return null or a valid time interval
     *
     * @param isNullField flag to determine what to return
     * @return time interval based on the flag
     */
    public Object getTimeInterval(final boolean isNullField) {
        if (isNullField) {
            return null;
        } else {
            return APEX_STARTER_TIME_INTERVAL;
        }

    }

}
