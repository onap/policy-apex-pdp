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

package org.onap.policy.apex.core.protocols;

/**
 * This interface is used to enforce a common type on actions in the Apex messasging protocol. Action types the Apex
 * messaging protocol supports implement this interface.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public interface Action {

    /**
     * Return a string representation of each action.
     *
     * @return the action string
     */
    String getActionString();
}
