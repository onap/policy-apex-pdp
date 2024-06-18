/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import lombok.NoArgsConstructor;

/**
 * A factory for creating TestInitialContext objects.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NoArgsConstructor
public class TestInitialContextFactory implements InitialContextFactory {

    private final Context context = new TestContext();

    /**
     * {@inheritDoc}.
     */
    @Override
    public Context getInitialContext(final Hashtable<?, ?> environment) {
        return context;
    }
}
