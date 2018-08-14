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

package org.onap.policy.apex.auth.clicodegen;

import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.StringRenderer;

/**
 * String object renderer for the code generator.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author John Keeney (John.Keeney@ericsson.com)
 */
public class CgStringRenderer implements AttributeRenderer {

    /*
     * (non-Javadoc)
     *
     * @see org.stringtemplate.v4.AttributeRenderer#toString(java.lang.Object, java.lang.String,
     * java.util.Locale)
     */
    @Override
    public String toString(final Object obj, final String format, final Locale locale) {
        if ("doQuotes".equals(format)) {
            if (obj == null) {
                return null;
            }
            String ret = obj.toString();
            if (ret.length() == 0) {
                return "\"\"";
            }
            if (!ret.startsWith("\"")) {
                ret = "\"" + ret + "\"";
            }
            return ret;
        }

        if ("doDescription".equals(format)) {
            String ret = obj.toString();
            if (ret.contains("\n") || ret.contains("\"")) {
                ret = "LS" + "\n" + ret + "\n" + "LE";
            } else {
                ret = this.toString(obj, "doQuotes", locale);
            }
            return ret;
        }

        // return the default string renderer if we don't know otherwise
        return new StringRenderer().toString(obj, format, locale);
    }
}
