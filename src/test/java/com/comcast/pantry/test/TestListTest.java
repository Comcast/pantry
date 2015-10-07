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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TestListTest {

    @Test
    public void testPermute() {
        Object[] a = new Object[] { 1, 2, 3 };
        Object[] b = new Object[] { 'a', 'b' };

        TestList list = new TestList();

        list.permute(a, b);

        assertEquals(list.size(), 6);

        verify(list, 0, 1, 'a');
        verify(list, 1, 1, 'b');
        verify(list, 2, 2, 'a');
        verify(list, 3, 2, 'b');
        verify(list, 4, 3, 'a');
        verify(list, 5, 3, 'b');
    }

    private void verify(TestList list, int index, int a, char b) {
        assertEquals(list.get(index, 0), a);
        assertEquals(list.get(index, 1), b);
    }
}
