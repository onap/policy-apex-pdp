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

package org.onap.policy.apex.core.infrastructure.java.compile.singleclass;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * The Class SingleFileManager is a {@link ForwardingJavaFileManager} which in turn implements {@code JavaFileManager}.
 * A {@code JavaFileManager} handles source files for Java language handling tools. A {@link ForwardingJavaFileManager}
 * is an implementation of {@code JavaFileManager} that forwards the {@code JavaFileManager} methods to a given file
 * manager.
 *
 * <p>This class instantiates and forwards those requests to a {@link StandardJavaFileManager} instance to act as a
 * {@code JavaFileManager} for a Java single file, managing class loading for the class.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SingleFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    // THe class loader for our single class
    private final SingleClassLoader singleClassLoader;

    /**
     * Instantiates a new single file manager.
     *
     * @param compiler the compiler we are using
     * @param byteCodeFileObject the byte code for the compiled class
     */
    public SingleFileManager(final JavaCompiler compiler, final SingleClassByteCodeFileObject byteCodeFileObject) {
        super(compiler.getStandardFileManager(null, null, null));
        singleClassLoader = new SingleClassLoader(byteCodeFileObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location,
     * java.lang.String, javax.tools.JavaFileObject.Kind, javax.tools.FileObject)
     */
    @Override
    public JavaFileObject getJavaFileForOutput(final Location notUsed, final String className,
            final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
        // Return the JavaFileObject to the compiler so that it can write byte code into it
        return singleClassLoader.getFileObject();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.ForwardingJavaFileManager#getClassLoader(javax.tools.JavaFileManager.Location)
     */
    @Override
    public SingleClassLoader getClassLoader(final Location location) {
        return singleClassLoader;
    }
}
