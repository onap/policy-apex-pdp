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

package org.onap.policy.apex.tools.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StrBuilder;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * A console for printing messages with functionality similar to loggers. The class provides a static instance for all
 * parts of an application. The default configuration is to not collect errors or warnings. The default types being
 * activated are errors, warnings, and info messages.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public final class Console {
    /** The console as static object. */
    public static final Console CONSOLE = new Console();

    /** Type for a quiet console, no messages being printed. */
    public static final int TYPE_QUIET = 0;

    /** Type for printing error messages. */
    public static final int TYPE_ERROR = 0b0001;

    /** Type for printing warning messages. */
    public static final int TYPE_WARNING = 0b0010;

    /** Type for printing information messages. */
    public static final int TYPE_INFO = 0b0100;

    /** Type for printing progress messages. */
    public static final int TYPE_PROGRESS = 0b1000;

    /** Type for printing debug messages. */
    public static final int TYPE_DEBUG = 0b001_0000;

    /** Type for printing trace messages. */
    public static final int TYPE_TRACE = 0b010_0000;

    /** Type for printing stack traces of caught exceptions. */
    public static final int TYPE_STACKTRACE = 0b110_0000;

    /** Type for a verbose console, activating all message types. */
    public static final int TYPE_VERBOSE = 0b111_1111;

    /** Configuration for a collecting error messages. */
    public static final int CONFIG_COLLECT_ERRORS = 0b0001;

    /** Configuration for a collecting warning messages. */
    public static final int CONFIG_COLLECT_WARNINGS = 0b0010;

    /** The setting for message types, set using type flags. */
    private int types;

    /** The console configuration, set using configuration flags. */
    private int configuration;

    /** A name for the application, if set used as prefix for messages. */
    private String appName;

    /** The list of errors, filled if error collection is activates. */
    private final List<String> errors;

    /** The list of warnings, filled if warning collection is activates. */
    private final List<String> warnings;

    /**
     * Creates a new console. The constructor is private since the class provides static access to an instance. The
     * default for types is verbose.
     */
    private Console() {
        types = TYPE_VERBOSE;

        configuration = 0;
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    /**
     * Sets the application name.
     *
     * @param appName new application name, use <code>null</code> to reset the application name, a non-blank string for
     *        a new name, blank strings are ignored
     */
    public void setAppName(final String appName) {
        if (appName == null) {
            this.appName = null;
        } else if (!StringUtils.isBlank(appName)) {
            this.appName = appName;
        }
    }

    /**
     * Returns the application name.
     *
     * @return application name, null if not set, non-blank string otherwise
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Activates a type.
     *
     * @param type the type to activate
     */
    public void activate(final int type) {
        types = types | type;
    }

    /**
     * Deactivates a type.
     *
     * @param type type to deactivate
     */
    public void deActivate(final int type) {
        types = types & ~type;
    }

    /**
     * Sets the type to the given type, effectively deactivating all other types.
     *
     * @param type new type
     */
    public void set(final int type) {
        types = type;
    }

    /**
     * Sets the type to the given types, effectively deactivating all other types.
     *
     * @param ts array of types to set
     */
    public void set(final int... ts) {
        this.types = 0;
        for (final int type : ts) {
            this.activate(type);
        }
    }

    /**
     * Configures the console. Use the configuration flags in combination for the required configuration. For instance,
     * to collect errors and warnings use <code>CONFIG_COLLECT_ERRORS | CONFIG_COLLECT_WARNINGS</code>.
     *
     * @param config the new configuration, overwrites the current configuration, 0 deactivates all settings
     */
    public void configure(final int config) {
        this.configuration = config;
    }

    /**
     * Prints an error message with message and objects if {@link #TYPE_ERROR} is set; and increases the error count.
     * Errors are collected (if configuration is set) and the error counter is increased regardless of the console error
     * type settings.
     *
     * @param message the error message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void error(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        final StrBuilder err = new StrBuilder();
        if (appName != null) {
            err.append(this.getAppName()).append(": ");
        }
        err.append("error: ");
        err.append(MessageFormatter.arrayFormat(message, objects).getMessage());

        if ((types & TYPE_ERROR) == TYPE_ERROR) {
            System.err.println(err.build());
        }
        if ((configuration & CONFIG_COLLECT_ERRORS) == CONFIG_COLLECT_ERRORS) {
            errors.add(err.build());
        }
    }

    /**
     * Prints a warning message with message and objects if {@link #TYPE_WARNING} is set; and increases the warning
     * count. Warnings are collected (if configuration is set) and the warning counter is increased regardless of the
     * console warning type settings.
     *
     * @param message the warning message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void warn(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        final StrBuilder warn = new StrBuilder();
        if (appName != null) {
            warn.append(this.getAppName()).append(": ");
        }
        warn.append("warning: ");
        warn.append(MessageFormatter.arrayFormat(message, objects).getMessage());

        if ((types & TYPE_WARNING) == TYPE_WARNING) {
            System.err.println(warn.build());
        }
        if ((configuration & CONFIG_COLLECT_WARNINGS) == CONFIG_COLLECT_WARNINGS) {
            warnings.add(warn.build());
        }
    }

    /**
     * Prints an info message with message and objects if {@link #TYPE_INFO} is set.
     *
     * @param message the warning message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void info(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        if ((types & TYPE_INFO) == TYPE_INFO) {
            if (appName != null) {
                System.err.print(appName + ": ");
            }
            System.err.println(MessageFormatter.arrayFormat(message, objects).getMessage());
        }
    }

    /**
     * Prints a progress message with message and objects if {@link #TYPE_PROGRESS} is set.
     *
     * @param message the warning message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void progress(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        if ((types & TYPE_PROGRESS) == TYPE_PROGRESS) {
            if (appName != null) {
                System.err.print(appName + ": ");
            }
            System.err.print("progress: ");
            System.err.println(MessageFormatter.arrayFormat(message, objects).getMessage());
        }
    }

    /**
     * Prints a debug message with message and objects if {@link #TYPE_DEBUG} is set.
     *
     * @param message the warning message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void debug(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        if ((types & TYPE_DEBUG) == TYPE_DEBUG) {
            if (appName != null) {
                System.err.print(appName + ": ");
            }
            System.err.print("debug: ");
            System.err.println(MessageFormatter.arrayFormat(message, objects).getMessage());
        }
    }

    /**
     * Prints a trace message with message and objects if {@link #TYPE_TRACE} is set.
     *
     * @param message the warning message, using the same format as the SLF4J MessageFormatter, nothing done if
     *        <code>blank</code>
     * @param objects the objects for substitution in the message
     */
    public void trace(final String message, final Object... objects) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        if ((types & TYPE_TRACE) == TYPE_TRACE) {
            if (appName != null) {
                System.err.print(appName + ": ");
            }
            System.err.print("trace: ");
            System.err.println(MessageFormatter.arrayFormat(message, objects).getMessage());
        }
    }

    /**
     * Prints message, cause, and stack trace for a given exception if {@link #TYPE_STACKTRACE} is set.
     *
     * @param exception the exception to print, ignored if <code>null</code>
     */
    public void stacktrace(final Exception exception) {
        if (exception == null) {
            return;
        }

        if ((types & TYPE_STACKTRACE) == TYPE_STACKTRACE) {
            if (appName != null) {
                System.err.print(appName + ": ");
            }
            System.err.println(" exception message: " + exception.getMessage());
            if (exception.getCause() != null) {
                System.err.println(" exception cause: " + exception.getCause());
            }
            System.err.println("for exception stack trace, please refer logs.");
            XLoggerFactory.getXLogger(Console.class).error("stacktrace", exception);
        }
    }

    /**
     * Resets the error counter and the list of errors.
     */
    public void resetErrors() {
        errors.clear();
    }

    /**
     * Resets the warning counter and the list of warnings.
     */
    public void resetWarnings() {
        warnings.clear();
    }

}
