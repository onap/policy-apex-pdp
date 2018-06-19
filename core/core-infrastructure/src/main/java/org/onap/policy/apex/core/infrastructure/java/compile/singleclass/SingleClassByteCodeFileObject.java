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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * The Class SingleClassByteCodeFileObject is a specialization of the {@link SimpleJavaFileObject} class, which is
 * itself an implementation of the {@code JavaFileObject} interface, which provides a file abstraction for tools
 * operating on Java programming language source and class files. The {@link SimpleJavaFileObject} class provides simple
 * implementations for most methods in {@code JavaFileObject}. This class is designed to be sub classed and used as a
 * basis for {@code JavaFileObject} implementations. Subclasses can override the implementation and specification of any
 * method of this class as long as the general contract of {@code JavaFileObject} is obeyed.
 *
 * This class holds the byte code for a single class in memory.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SingleClassByteCodeFileObject extends SimpleJavaFileObject {

    // The ByteArrayOutputStream holds the byte code for the class
    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * Instantiates the byte code for the class in memory.
     *
     * @param className the class name is used to compose a URI for the class
     */
    public SingleClassByteCodeFileObject(final String className) {
        super(URI.create("byte:///" + className + ".class"), Kind.CLASS);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.SimpleJavaFileObject#openOutputStream()
     */
    @Override
    public OutputStream openOutputStream() {
        // Create the byte array output stream that will hold the byte code for the class, when the class source code is
        // compiled, this output stream is passed
        // to the compiler and the byte code for the class is written into the output stream.
        byteArrayOutputStream = new ByteArrayOutputStream();
        return byteArrayOutputStream;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.tools.SimpleJavaFileObject#openInputStream()
     */
    @Override
    public InputStream openInputStream() {
        // No input stream for streaming out the byte code
        return null;
    }

    /**
     * Gets the byte code of the class.
     *
     * @return the byte code of the class
     */
    public byte[] getByteCode() {
        return byteArrayOutputStream.toByteArray();
    }
}
