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

package org.onap.policy.core.infrastructure.java.compile.singleclass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * The Class SingleClassCompilationUnit is a container for the source code of the single Java class in memory. The class
 * uses a {@link String} to hold the source code.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SingleClassCompilationUnit extends SimpleJavaFileObject {

    private final String source;

    /**
     * Instantiates a new compilation unit.
     *
     * @param className the class name for the source code
     * @param source the source code for the class
     */
    public SingleClassCompilationUnit(final String className, final String source) {
        // Create a URI for the source code of the class
        super(URI.create("file:///" + className + ".java"), Kind.SOURCE);
        this.source = source;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
     */
    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
        // Return the source code to toe caller, the compiler
        return source;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.SimpleJavaFileObject#openOutputStream()
     */
    @Override
    public OutputStream openOutputStream() {
        throw new IllegalStateException();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.SimpleJavaFileObject#openInputStream()
     */
    @Override
    public InputStream openInputStream() {
        // Return the source code as a stream
        return new ByteArrayInputStream(source.getBytes());
    }
}
