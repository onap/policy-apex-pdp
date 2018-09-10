/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.io.File;

/**
 * The Class DirectoryShutdownHook removes the contents of a directory and the directory itself at shutdown.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
final class DirectoryDeleteShutdownHook extends Thread {
    // The directory we are acting on
    private final File tempDir;

    /**
     * Constructor that defines the directory to act on at shutdown.
     *
     * @param tempDir The temporary directory to delete
     */
    DirectoryDeleteShutdownHook(final File tempDir) {
        this.tempDir = tempDir;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (tempDir.exists()) {
            // Empty and delete the directory
            DirectoryUtils.emptyDirectory(tempDir);
            tempDir.delete();
        }
    }
}
