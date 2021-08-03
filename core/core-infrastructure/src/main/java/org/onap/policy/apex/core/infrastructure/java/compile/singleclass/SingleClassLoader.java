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

package org.onap.policy.apex.core.infrastructure.java.compile.singleclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The Class SingleClassLoader is responsible for class loading the single Java class being held in memory.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@AllArgsConstructor
public class SingleClassLoader extends ClassLoader {
    // The byte code of the class held in memory as byte code in a ByteCodeFileObject
    private final SingleClassByteCodeFileObject fileObject;

    /**
     * {@inheritDoc}.
     */
    @Override
    protected Class<?> findClass(final String className) throws ClassNotFoundException {
        // Creates a java Class that can be instantiated from the class defined in the byte code in the
        // ByteCodeFileObejct
        return defineClass(className, fileObject.getByteCode(), 0, fileObject.getByteCode().length);
    }
}
