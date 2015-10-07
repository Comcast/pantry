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

package com.comcast.pantry.test;

/**
 * A <i>WiringException</i> indicates that the {@link Wiring} class failed to wire a test class.
 *
 * @author Clark Malmgren
 */
public class WiringException extends RuntimeException {

    /** Generated Serial Version UID */
    private static final long serialVersionUID = 294920219232804663L;

    /**
     * Construct a new WiringException
     *
     * @param message
     *            the message
     */
    public WiringException(String message) {
        super(message);
    }

    /**
     * Construct a new WiringException with a default message.
     *
     * @param cause
     *            the cause
     */
    public WiringException(Throwable cause) {
        this("Failed to wire a test class", cause);
    }

    /**
     * Construct a new WiringException
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public WiringException(String message, Throwable cause) {
        super(message, cause);
    }
}
