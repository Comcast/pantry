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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A simple helper class for assisting when building a TestNG <code>&#64;DataProvider</code>. The
 * {@link TestList} can be returned. As an example:
 *
 * <pre>
 * &#64;DataProvider(name = "tests")
 * public TestList getTests() {'
 *     TestList tests = new TestList();
 *
 *     test.add("apple", 1, true);
 *     test.add("apple", 2, true);
 *     test.add("apple", 3, true);
 *     test.add("bobcat", 1, false);
 *
 *     return tests;
 * }
 * </pre>
 *
 * @author Clark Malmgren
 */
public class TestList implements Iterator<Object[]> {

    private List<Object[]> tests = new ArrayList<Object[]>();
    private Iterator<Object[]> delegate = null;

    /**
     * Add the following arguments as a new set of arguments to pass to the test method.
     *
     * @param args
     *            the arguments pass to the test method
     */
    public void add(Object... args) {
        tests.add(args);
    }

    /**
     * Add all possible permutations of the given arguments. This takes an array of arrays where the
     * inner array should be all possible values for each argument. So a call as:
     *
     * <pre>
     * tests.permute(new Object[] { 1, 2 }, new Object[] { 'a', 'b' });
     * </pre>
     *
     * will generate the following possible values:
     *
     * <pre>
     * 1, a
     * 1, b
     * 2, a
     * 2, b
     * </pre>
     *
     * @param args
     *            all possible values for all arguments
     */
    public void permute(Object[]... args) {
        int[] indexes = new int[args.length];
        Arrays.fill(indexes, 0);
        boolean done = false;

        while (!done) {
            /* Add the current test */
            Object[] test = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                test[i] = args[i][indexes[i]];
            }
            this.tests.add(test);

            /* Increment the indexes */
            for (int i = indexes.length - 1; i >=0; i--) {
                indexes[i]++;
                if (indexes[i] < args[i].length) {
                    break;
                } else if (i == 0) {
                    done = true;
                } else {
                    indexes[i] = 0;
                }
            }
        }
    }

    /**
     * Gets a new iterator from the internal list. This can be used to potentially reuse a TestList.
     */
    public void reset() {
        this.delegate = null;
    }

    /**
     * Get the delegate iterator. If one does not yet exist, or reset has been called, a new
     * iterator will be allocated from the current internal list.
     *
     * @return the delegate iterator
     */
    public Iterator<Object[]> getIterator() {
        if (null == delegate) {
            delegate = tests.iterator();
        }
        return delegate;
    }

    /**
     * Get the number of tests that have been configured
     *
     * @return the number of tests
     */
    public int size() {
        return tests.size();
    }

    /**
     * Get a single argument for a single test.
     *
     * @param t
     *            the test index
     * @param i
     *            the argument index
     *
     * @return the argument
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int t, int i) {
        return (T) tests.get(t)[i];
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object[] next() {
        return getIterator().next();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        getIterator().remove();
    }
}
