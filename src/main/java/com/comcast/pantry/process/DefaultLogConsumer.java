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
 * The DefaultLogConsumer will simply pipe the output from a started process into this
 * <code>System.out</code> and <code>System.err</code>.
 *
 * @author Clark Malmgren
 */
public class DefaultLogConsumer implements LogConsumer {

    /*
     * (non-Javadoc)
     * @see com.comcast.common.process.LogConsumer#logLine(java.lang.String)
     */
    public void logLine(String line) {
        System.out.println(line);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.common.process.LogConsumer#logErrorLine(java.lang.String)
     */
    public void logErrorLine(String line) {
        System.err.println(line);
    }
}
