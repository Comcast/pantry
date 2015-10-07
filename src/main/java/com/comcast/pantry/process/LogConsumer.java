/**
 * Copyright 2015 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comcast.pantry.process;

/**
 * Consumer for logging output from a process started by the {@link ProcessFactory}.
 *
 * @author Clark Malmgren
 *
 * @see ProcessFactory#setLogConsumer(LogConsumer)
 */
public interface LogConsumer {

    /**
     * Handle a line that was printed to STDOUT.
     *
     * @param line
     *            the line that was printed
     */
    void logLine(String line);

    /**
     * Handle a line that was printed to STDERR.
     *
     * @param line
     *            the line that was printed
     */
    void logErrorLine(String line);
}
