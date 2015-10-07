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

package com.comcast.pantry.cleanup;

/**
 * Common interface to be implemented by components that wish to be cleaned up by processes that do
 * not have specific shutdown hooks. This is particularly useful for adding cleanup hooks to the end
 * of a test cycle that is being run by test frameworks which do not rely upon surefire to cleanup
 * stray processes and memory.
 * <p>
 * All {@link Cleaner}s should be registed by invoking the {@link CleanerRegistry#register(Cleaner)}
 * method. The cleanup method will only be invoked however if this is being run from within a
 * process that will invoke the {@link CleanerRegistry#cleanAll()} method at the end of its
 * execution.
 * </p>
 * <p>
 * This will be invoked <b>prior</b> to JVM shutdown by the owning framework and should typically
 * used to shutdown non-daemon threads that would otherwise block the exit of the JVM. For normal
 * cleanup after JVM shutdown, users should use the {@link Runtime#addShutdownHook(Thread)} method.
 * </p>
 *
 * @author Clark Malmgren
 */
public interface Cleaner {

    /**
     * Do the cleanup.
     */
    void cleanup();
}
