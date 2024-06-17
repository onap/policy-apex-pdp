/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;

class ApexPeriodicEventGeneratorTest {
    private final Random random = new Random();

    @Test
    void run() {

        final EngineServiceEventInterface engineServiceEventInterface = Mockito.mock(EngineServiceEventInterface.class);
        // don't want the timer to fire, so make it wait at least two seconds
        final int period = random.nextInt(2000) + 2000;
        final ApexPeriodicEventGenerator generator = new ApexPeriodicEventGenerator(engineServiceEventInterface,
            period);

        generator.run();
        Mockito
            .verify(engineServiceEventInterface, Mockito.times(1))
            .sendEvent(ArgumentMatchers.any(ApexEvent.class));
    }
}
