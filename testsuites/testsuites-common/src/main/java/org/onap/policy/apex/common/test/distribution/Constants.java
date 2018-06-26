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
package org.onap.policy.apex.common.test.distribution;

import java.util.TimeZone;

import org.onap.policy.apex.common.test.concepts.TestPolicyContextItem;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

public class Constants {
    public static final String MAP_CONTEXT_ALBUM = "MapContextAlbum";
    public static final String LONG_CONTEXT_ALBUM = "LongContextAlbum";
    public static final String DATE_CONTEXT_ALBUM = "DateContextAlbum";

    public static final String EXTERNAL_CONTEXT_ALBUM = "ExternalContextAlbum";
    public static final String GLOBAL_CONTEXT_ALBUM = "GlobalContextAlbum";
    public static final String POLICY_CONTEXT_ALBUM = "PolicyContextAlbum";

    public static final String APEX_DISTRIBUTOR = "ApexDistributor";
    public static final String VERSION = "0.0.1";
    public static final int INT_VAL = 0xFFFFFFFF;
    public static final int INT_VAL_2 = 2000;
    public static final int INT_VAL_3 = -1;
    public static final String EXCEPTION_MESSAGE = "Test should throw an exception";
    public static final byte BYTE_VAL = (byte) 0xFF;
    public static final double PI_VAL = Math.PI;
    public static final float FLOAT_VAL = 3.14159265359f;
    public static final String EXTERNAL_CONTEXT = "externalContext";
    public static final String GLOBAL_CONTEXT_KEY = "globalContext";
    public static final String STRING_GLOBAL_VAL = "This is a global context string";
    public static final String STRING_VAL = "This is a policy context string";
    public static final String STRING_EXT_VAL = "This is an external context string";
    public static final long LONG_VAL = 0xFFFFFFFFFFFFFFFFL;
    public static final String TEST_POLICY_CONTEXT_ITEM = TestPolicyContextItem.class.getName();
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Dublin");


    public static final AxArtifactKey[] USED_ARTIFACT_STACK_ARRAY = {new AxArtifactKey("testC-top", VERSION),
            new AxArtifactKey("testC-next", VERSION), new AxArtifactKey("testC-bot", VERSION)};


    private Constants() {}

}
