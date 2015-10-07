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
import static org.testng.Assert.assertSame;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class WiringTest {

    @Test
    public void testWireByName() {
        TestClassTwo o = new TestClassTwo();
        Wiring<TestClassTwo> wiring = new Wiring<TestClassTwo>(o);

        wiring.wire("c", "asdf");
        wiring.wire("a", "jkl;");

        assertEquals(o.c, "asdf");
        assertEquals(o.a, "jkl;");
    }

    @Test
    public void testAutowire() {
        TestClassTwo o = new TestClassTwo();
        Wiring<TestClassTwo> wiring = new Wiring<TestClassTwo>(o);

        wiring.autowire("asdf");
        wiring.autowire(36);

        assertEquals(o.a, "asdf");
        assertEquals((int) o.i, 36);
    }

    @Test
    public void testWireByNameWithStaticChaining() {
        TestClassTwo o = new TestClassTwo();
        Wiring.wire(o, "c", "asdf").wire("a", "jkl;");

        assertEquals(o.c, "asdf");
        assertEquals(o.a, "jkl;");
    }

    @Test
    public void testAutowireWithStaticChaining() {
        TestClassTwo o = new TestClassTwo();
        Wiring.autowire(o, "asdf").autowire(36);

        assertEquals(o.a, "asdf");
        assertEquals((int) o.i, 36);
    }

    @Test
    public void testAutowireLucky() {
        TestClassTwo o = new TestClassTwo();
        Wiring<TestClassTwo> wiring = new Wiring<TestClassTwo>(o);

        ArrayList<Object> list = new ArrayList<Object>();

        wiring.autowire(list);
        assertSame(o.arrayList, list);
    }

    @Test(expectedExceptions = WiringException.class)
    public void testWireMissingName() {
        TestClassTwo o = new TestClassTwo();
        Wiring<TestClassTwo> wiring = new Wiring<TestClassTwo>(o);

        wiring.wire("w", "asdf");
    }

    @Test(expectedExceptions = WiringException.class)
    public void testAutowireNoApplicableField() {
        TestClassTwo o = new TestClassTwo();
        Wiring<TestClassTwo> wiring = new Wiring<TestClassTwo>(o);

        wiring.autowire(new Object());
    }

    public class TestClassOne {

        @Autowired
        public String a;

        public String b;
    }

    public class TestClassTwo extends TestClassOne {

        public String c;

        @Autowired
        public Integer i;

        @Autowired
        public ArrayList<Object> arrayList;
    }

}
