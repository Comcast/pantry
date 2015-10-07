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
 * The <i>LogCollector</i> is a common implementation of a {@link LogConsumer} that captures logs
 * for later reuse. This can capture STDOUT and STDERR together or separately.
 *
 * @author Clark Malmgren
 */
public class LogCollector implements LogConsumer {

    private boolean combined;
    private StringBuilder std;
    private StringBuilder err;

    /**
     * Create a new {@link LogCollector} with a combined capturing of STDOUT and STDERR.
     */
    public LogCollector() {
        this(true);
    }

    /**
     * Create a new {@link LogCollector}.
     *
     * @param combined
     *            if <code>true</code>, this collector will combine the capturing of STDOUT and
     *            STDERR, otherwise they will be captured separately
     */
    public LogCollector(boolean combined) {
        this.combined = combined;
        this.std = new StringBuilder();
        if (!combined) {
            this.err = new StringBuilder();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.common.process.LogConsumer#logLine(java.lang.String)
     */
    public void logLine(String line) {
        std.append(line + '\n');
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.common.process.LogConsumer#logErrorLine(java.lang.String)
     */
    public void logErrorLine(String line) {
        (combined ? std : err).append(line + '\n');
    }

    /**
     * Get the captured standard log. If this is a combined collector, this will contain both STDOUT
     * and STDERR logs.
     *
     * @return the standard log
     */
    public String getLog() {
        return std.toString();
    }

    /**
     * Get the captured STDERR logs. If this is a combined collector, this will return
     * <code>null</code>.
     *
     * @return the captured STDERR logs
     */
    public String getErrorLog() {
        return (combined ? null : err.toString());
    }

    /**
     * Returns <code>true</code> if this collector is combining results.
     *
     * @return <code>true</code> if this collector is combining results, otherwise
     *         <code>false</code>
     */
    public boolean isCombined() {
        return combined;
    }

    /**
     * Returns the contents of the standard log. This simply returns the contents of calling
     * {@link #getLog()}.
     *
     * @return the contents of the standard log
     * @see #getLog()
     */
    @Override
    public String toString() {
        return getLog();
    }
}
