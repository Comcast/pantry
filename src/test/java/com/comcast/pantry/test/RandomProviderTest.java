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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RandomProviderTest {

    public static final RandomProvider RANDOM = new RandomProvider(982759082734598701l);
    public static final int TEST_COUNT = 200;

    @DataProvider(name = "positiveRanges")
    public TestList getPositiveRanges() {
        TestList list = new TestList();

        list.add(0, 0);
        list.add(3, 3);
        list.add(0, 5);
        list.add(10, 30);
        list.add(100, 200);

        return list;
    }

    @DataProvider(name = "allRanges")
    public TestList getAllRanges() {
        TestList list = getPositiveRanges();

        list.add(-1, -1);
        list.add(-100, 100);
        list.add(-10, 10);
        list.add(-100, 10);

        return list;
    }

    @DataProvider(name = "positiveSizes")
    public TestList getPositiveSizes() {
        TestList list = new TestList();

        list.add(0);
        list.add(3);
        list.add(5);
        list.add(10);
        list.add(30);
        list.add(100);

        return list;
    }

    @DataProvider(name = "allSizes")
    public TestList getAllSizes() {
        TestList list = getPositiveSizes();

        list.add(-1);
        list.add(-100);
        list.add(-10);
        list.add(-1000);

        return list;
    }

    @Test(dataProvider = "positiveRanges")
    public void testRandomString(int min, int max) {
        for (int i = 0; i < TEST_COUNT; i++) {
            String string = RANDOM.nextString(min, max);
            assertTrue(string.length() >= min);
            assertTrue(string.length() <= max);
        }
    }

    @Test(dataProvider = "positiveSizes")
    public void testRandomString(int size) {
        for (int i = 0; i < TEST_COUNT; i++) {
            String string = RANDOM.nextString(size);
            assertEquals(string.length(), size);
        }
    }

    @Test(dataProvider = "positiveSizes")
    public void testRandomByteArray(int size) {
        for (int i = 0; i < TEST_COUNT; i++) {
            byte[] bytes = RANDOM.nextBytes(size);
            assertEquals(bytes.length, size);
        }
    }

    @Test(dataProvider = "allRanges")
    public void testNextInt(int min, int max) {
        for (int i = 0; i < TEST_COUNT; i++) {
            int value = RANDOM.nextInt(min, max);
            assertTrue(value >= min);
            assertTrue(value <= max);
        }
    }

    @Test(dataProvider = "allRanges")
    public void testNextLong(int min, int max) {
        for (int i = 0; i < TEST_COUNT; i++) {
            long value = RANDOM.nextLong(min, max);
            if (value < min || value > max) {
                System.out.println(String.format("Failure [%d,%d]: %d", min, max, value));
            }
            assertTrue(value >= min);
            assertTrue(value <= max);
        }
    }
}
