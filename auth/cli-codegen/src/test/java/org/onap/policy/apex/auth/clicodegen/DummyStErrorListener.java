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

package org.onap.policy.apex.auth.clicodegen;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

/**
 * Customized ST error listener.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class DummyStErrorListener implements STErrorListener {

    /**
     * Counts errors of the listener.
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int errorCount;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void IOError(final STMessage msg) {
        this.registerErrors(msg);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void compileTimeError(final STMessage msg) {
        this.registerErrors(msg);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void internalError(final STMessage msg) {
        this.registerErrors(msg);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void runTimeError(final STMessage msg) {
        switch (msg.error) {
            case NO_SUCH_PROPERTY, ARGUMENT_COUNT_MISMATCH, ANON_ARGUMENT_MISMATCH:
                break;
            default:
                this.registerErrors(msg);
        }
    }

    /**
     * Registers an error with the local error listener and increases the error count.
     *
     * @param msg error message
     */
    protected void registerErrors(final STMessage msg) {
        setErrorCount(getErrorCount() + 1);
        System.err.println("STG/ST (" + msg.error + ") " + msg.arg + " -> " + msg.cause);
    }
}
