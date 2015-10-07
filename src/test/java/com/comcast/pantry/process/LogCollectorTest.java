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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class LogCollectorTest {

    @Test
    public void testCombinedCollection() {
        LogCollector collector = new LogCollector();

        doLogging(collector);

        String expected = "alpha\nomega\nsilly\noh, poop\n";

        assertEquals(collector.getLog(), expected);
        assertNull(collector.getErrorLog());

        assertEquals(collector.toString(), expected);
        assertTrue(collector.isCombined());
    }

    @Test
    public void testSeparateCollection() {
        LogCollector collector = new LogCollector(false);

        doLogging(collector);

        String std = "alpha\nsilly\n";
        String err = "omega\noh, poop\n";

        assertEquals(collector.getLog(), std);
        assertEquals(collector.getErrorLog(), err);

        assertEquals(collector.toString(), std);
        assertFalse(collector.isCombined());
    }

    private void doLogging(LogConsumer out) {
        out.logLine("alpha");
        out.logErrorLine("omega");
        out.logLine("silly");
        out.logErrorLine("oh, poop");
    }
}
