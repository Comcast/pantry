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

package com.comcast.pantry.io;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StreamHelperTest {

    @Test(dataProvider = "streamCopyTests")
    public void testStreamCopy(String data, int bufferSize) throws IOException {
        StreamHelper streamHelper = new StreamHelper();

        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (-1 == bufferSize) {
            streamHelper.copy(in, out);
        } else {
            streamHelper.copy(in, out, bufferSize);
        }

        assertEquals(out.toString(), data);
    }

    @DataProvider(name = "streamCopyTests")
    public Iterator<Object[]> getStreamCopyTests() {
        List<Object[]> tests = new ArrayList<Object[]>();

        tests.add(new Object[] { "blah", -1 });
        tests.add(new Object[] { "This is a longer string", 8 });

        return tests.iterator();
    }
}
