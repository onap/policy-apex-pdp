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

package org.onap.policy.apex.services.onappf.rest;

import org.onap.policy.common.endpoints.http.server.aaf.AafGranularAuthFilter;

/**
 * Class to manage aaf filters for services-onappf component.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterAafFilter extends AafGranularAuthFilter {

    public static final String AAF_NODETYPE = "policy-apex-pdp";
    public static final String AAF_ROOT_PERMISSION = DEFAULT_NAMESPACE + "." + AAF_NODETYPE;

    @Override
    public String getPermissionTypeRoot() {
        return AAF_ROOT_PERMISSION;
    }
}
