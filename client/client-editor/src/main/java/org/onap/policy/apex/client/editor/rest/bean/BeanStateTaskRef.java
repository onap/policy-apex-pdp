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
 * The State Task Reference Bean.
 */
@XmlType
public class BeanStateTaskRef extends BeanBase {
    private BeanKeyRef task = null;
    private String outputType = null;
    private String outputName = null;

    /**
     * Gets the task.
     *
     * @return the task
     */
    public BeanKeyRef getTask() {
        return task;
    }

    /**
     * Gets the output type.
     *
     * @return the output type
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * Gets the output name.
     *
     * @return the output name
     */
    public String getOutputName() {
        return outputName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "StateTaskRef [task=" + task + ", outputType=" + outputType + ", outputName=" + outputName + "]";
    }

}
