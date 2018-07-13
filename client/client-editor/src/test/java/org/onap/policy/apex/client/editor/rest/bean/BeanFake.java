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

package org.onap.policy.apex.client.editor.rest.bean;

import javax.xml.bind.annotation.XmlType;

/**
 * The Event Bean.
 */
@XmlType
public class BeanFake extends BeanBase {

    private String name = null, version = null, field1 = null;
    private int field2 = 0, field3 = 0;

    public String getName() {
        field1 = name;
        return field1;
    }

    public String getVersion() {
        return version;
    }

    public int getField2() {
        field3 = field2;
        return field3;
    }
}
