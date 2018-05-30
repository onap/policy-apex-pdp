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

package org.onap.policy.apex.model.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class TextFileUtils is class that provides useful functions for handling text files.
 * Functions to read and wrtie text files to strings and strings are provided.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class TextFileUtils {
    private static final int READER_CHAR_BUFFER_SIZE_4096 = 4096;

    private TextFileUtils() {
        // This class cannot be initialized
    }

    /**
     * Method to return the contents of a text file as a string.
     *
     * @param textFilePath The path to the file as a string
     * @return A string containing the contents of the file
     * @throws IOException on errors reading text from the file
     */
    public static String getTextFileAsString(final String textFilePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(textFilePath)));
    }

    /**
     * Method to write contents of a string to a text file.
     *
     * @param outString The string to write
     * @param textFilePath The path to the file as a string
     * @throws IOException on errors reading text from the file
     */
    public static void putStringAsTextFile(final String outString, final String textFilePath) throws IOException {
        final File textFile = new File(textFilePath);
        putStringAsFile(outString, textFile);
    }

    /**
     * Method to write contents of a string to a text file.
     *
     * @param outString The string to write
     * @param textFile The file to write the string to
     * @throws IOException on errors reading text from the file
     */
    public static void putStringAsFile(final String outString, final File textFile) throws IOException {
        Files.write(textFile.toPath(), outString.getBytes());
    }

    /**
     * Method to return the contents of a text steam as a string.
     *
     * @param textStream The stream
     * @return A string containing the output of the stream as text
     * @throws IOException on errors reading text from the file
     */
    public static String getStreamAsString(final InputStream textStream) throws IOException {
        return getReaderAsString(new BufferedReader(new InputStreamReader(textStream)));
    }

    /**
     * Method to return the contents of a reader steam as a string. This closes the reader after use
     *
     * @param textReader The reader
     * @return A string containing the output of the reader as text
     * @throws IOException on errors reading text from the file
     */
    public static String getReaderAsString(final BufferedReader textReader) throws IOException {

        final StringBuilder builder = new StringBuilder();
        int charsRead = -1;
        final char[] chars = new char[READER_CHAR_BUFFER_SIZE_4096];
        do {
            charsRead = textReader.read(chars, 0, chars.length);
            if (charsRead > 0) {
                builder.append(chars, 0, charsRead);
            }
        } while (charsRead > 0);
        return builder.toString();
    }
}
