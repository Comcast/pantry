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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.Test;

public class CleanerRegistryTest {

    @Test
    public void testAddingToRegistryGetsCallback() {
        Cleaner cleaner = createMock(Cleaner.class);
        cleaner.cleanup();
        expectLastCall().once();
        replay(cleaner);

        try {
            CleanerRegistry.register(cleaner);
            CleanerRegistry.cleanAll();
        } finally {
            CleanerRegistry.remove(cleaner);
        }

        verify(cleaner);
    }

    @Test
    public void testAddingToRegistryTwiceOnlyGetsOneCallback() {
        Cleaner cleaner = createMock(Cleaner.class);
        cleaner.cleanup();
        expectLastCall().once();
        replay(cleaner);

        try {
            CleanerRegistry.register(cleaner);
            CleanerRegistry.register(cleaner);

            CleanerRegistry.cleanAll();
        } finally {
            CleanerRegistry.remove(cleaner);
        }

        verify(cleaner);
    }

    @Test
    public void testAddingToRegistryThenRemovingGetsNoCallback() {
        Cleaner cleaner = createMock(Cleaner.class);
        replay(cleaner);

        CleanerRegistry.register(cleaner);
        CleanerRegistry.remove(cleaner);

        CleanerRegistry.cleanAll();

        verify(cleaner);
    }
}
