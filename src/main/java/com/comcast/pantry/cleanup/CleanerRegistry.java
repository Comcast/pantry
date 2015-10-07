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

import java.util.ArrayList;
import java.util.List;

/**
 * Central repository for registering and invoking all {@link Cleaner}s.
 *
 * @author Clark Malmgren
 */
public class CleanerRegistry {

    /**
     * Internal registry. This is intentionally not a WeakHashMap because the cleaner may not be on
     * the running thread so we are not guaranteed that if there is cleaning to do, the cleaner will
     * still be referenced somewhere.
     */
    private static final List<Cleaner> registry = new ArrayList<Cleaner>();

    /**
     * Register a cleaner to be invoked at the end of the execution cycle for the running framework.
     *
     * @param cleaner
     *            the cleaner to register
     */
    public static void register(Cleaner cleaner) {
        if (!registry.contains(cleaner)) {
            registry.add(cleaner);
        }
    }

    /**
     * Remove a cleaner from the registry.
     *
     * @param cleaner
     *            the cleaner to remove from the registry
     */
    public static void remove(Cleaner cleaner) {
        registry.remove(cleaner);
    }

    /**
     * Invoke {@link Cleaner#cleanup()} on all {@link Cleaner}s that have been registered.
     */
    public static void cleanAll() {
        for (Cleaner cleaner : registry) {
            cleaner.cleanup();
        }
    }
}
