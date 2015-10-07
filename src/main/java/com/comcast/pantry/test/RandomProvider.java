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

import java.util.Random;

/**
 * The RandomProvider is a test utility to generate random (but reproducible) artifacts for testing.
 * A seed is required so that the random data produced will be the same every time it is used
 * provided that the calls are still made in the same order. This means that multi-threaded tests
 * should not use a common RandomProvider.
 *
 * @author Clark Malmgren
 */
@SuppressWarnings("serial")
public class RandomProvider extends Random {

    public static final byte MIN_CHAR = ' ';
    public static final byte MAX_CHAR = '~';

    /**
     * Create a new RandomProvider.
     *
     * @param seed
     *            the seed
     */
    public RandomProvider(long seed) {
        super(seed);
    }

    /**
     * Get a random string with a length between (inclusive) <code>minSize</code> and
     * <code>maxSize</code>.
     *
     * @param minSize
     *            the minimum length of the string
     * @param maxSize
     *            the maximum length of the string
     *
     * @return a random string
     */
    public String nextString(int minSize, int maxSize) {
        return nextString(nextInt(minSize, maxSize));
    }

    /**
     * Get a random string with an exact length of <code>size</code>.
     *
     * @param size
     *            the length of the created string
     *
     * @return a random string
     */
    public String nextString(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (this.nextInt(MIN_CHAR, MAX_CHAR));
        }
        return new String(bytes);
    }

    /**
     * Get a random integer between (inclusive) <code>min</code> and <code>max</code>.
     *
     * @param min
     *            the minimum possible value
     * @param max
     *            the maximum possible value
     *
     * @return a random integer
     */
    public int nextInt(int min, int max) {
        if (min == max) {
            return min;
        } else {
            return nextInt(max - min) + min;
        }
    }

    /**
     * Get a random long between (inclusive) <code>min</code> and <code>max</code>.
     *
     * @param min
     *            the minimum possible value
     * @param max
     *            the maximum possible value
     *
     * @return a random long
     */
    public long nextLong(long min, long max) {
        if (min == max) {
            return min;
        } else {
            return Math.abs((nextLong() % (max - min))) + min;
        }
    }

    /**
     * Get a random byte array with an exact length of <code>size</code>.
     *
     * @param size
     *            the length of the created byte array
     *
     * @return a random byte array
     */
    public byte[] nextBytes(int size) {
        byte[] bytes = new byte[size];
        nextBytes(bytes);
        return bytes;
    }
}
