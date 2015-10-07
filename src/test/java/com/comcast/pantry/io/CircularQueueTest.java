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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CircularQueueTest {

    @DataProvider(name = "inRangeData")
    public Object[][] getData() {

        return new Object[][] {
            { 0,  true,  true },
            { 6,  true,  true },
            { 9,  true,  true },
            { 10, false, true },
            { 10, false, true },
            { -1, false, true },
            // after we've inserted some more values in the queue check
            // indexes
            { 11, true,  false },
            { 5,  true,  false },
            { 14, true,  false },
            { 6,  true,  false },
            { 14, true,  false },
            { 0,  false, false },
            { 1,  false, false },
            { 4,  false, false },
            { 15, false, false },
        };

    }

    /**
     * For a Queue of size 10 - check to see for different values if they are in
     * index
     */
    @Test(dataProvider = "inRangeData")
    public void testInRange01(int index, boolean result, boolean before) throws Exception {
        CircularQueue q = new CircularQueue(10);
        q.init();

        prepare(q, 10);

        //before we've insert more values
        if (before) {
            Assert.assertEquals(q.inRange(index), result);
        } else {
            // add 5 more values
            prepare(q, 5);
            Assert.assertEquals(q.inRange(index), result);
        }
    }

    private CircularQueue prepare(CircularQueue q, int len) {
        for (int i = 0; i < len; i++) {
            q.add(Integer.toString(i));
        }
        return q;
    }

    /**
     * Test to verify that the queue returns the proper value when an index is
     * inrange.
     */
    @Test
    public void testInRange() {

        CircularQueue q = new CircularQueue(10);
        // no data has been inserted
        Assert.assertFalse(q.inRange(10));
        q.add("0");
        q.add("1");
        q.add("2");
        q.add("3");
        Assert.assertTrue(q.inRange(3));
        q.add("4");
        q.add("5");
        Assert.assertFalse(q.inRange(9));
        Assert.assertFalse(q.inRange(10));
        q.add("6");
        q.add("7");
        q.add("8");
        q.add("9");
        /*
         * At this moment the queue is full and the data range is as the start
         * and end of queue
         */
        Assert.assertTrue(q.inRange(0));
        // There is no value at index 10
        Assert.assertFalse(q.inRange(10));

        // starts looping
        q.add("10");
        q.add("11");
        q.add("12");
        q.add("13");
        q.add("14");
        q.add("15");
        // Now the index 0-5 should not be in range because the data at those
        // indexes has been over written
        Assert.assertFalse(q.inRange(0));
        Assert.assertFalse(q.inRange(4));
        Assert.assertFalse(q.inRange(5));
        Assert.assertFalse(q.inRange(26));

        Assert.assertTrue(q.inRange(11));
        Assert.assertTrue(q.inRange(6));
        Assert.assertTrue(q.inRange(15));
        Assert.assertFalse(q.inRange(16));
        Assert.assertFalse(q.inRange(-1));
    }

    @Test
    public void testIsFull() {
        CircularQueue q = new CircularQueue(2);
        q.add("0");
        Assert.assertFalse(q.isFull());
        q.add("1");
        Assert.assertTrue(q.isFull());
        q.add("2"); // when we perform a delete check queue is still full
        Assert.assertTrue(q.isFull());
    }

    @Test
    public void testIsEmpty() {
        CircularQueue q = new CircularQueue(2);
        Assert.assertTrue((q.isEmpty()));
        q.add("1");
        Assert.assertFalse(q.isEmpty());
        q.delete();
        Assert.assertTrue((q.isEmpty()));
        q.delete();
        Assert.assertTrue((q.isEmpty()));
    }

    @Test
    public void testGet() {
        int check = 0;
        CircularQueue q = new CircularQueue(10);
        // no data has been inserted
        try {
            q.get(0);
        } catch (IndexOutOfBoundsException e) {
            check++;
        }

        q.add("0");
        q.add("1");
        q.add("2");
        q.add("3");
        q.add("4");
        q.add("5");
        q.add("6");
        // no exception

        try {
            q.get(10);
        } catch (IndexOutOfBoundsException e) {
            check++;
        }
        Assert.assertTrue(Integer.toString(6).equals(q.get(6)));

        // starts looping
        q.add("10");
        q.add("11");
        q.add("12");
        q.add("13");
        q.add("14");
        q.add("15");

        try {
            q.get(0);
        } catch (IndexOutOfBoundsException e) {
            check++;
        }

        try {
            q.get(2);
        } catch (IndexOutOfBoundsException e) {
            check++;
        }

        Assert.assertTrue(Integer.toString(13).equals(q.get(10)));
        Assert.assertTrue(Integer.toString(10).equals(q.get(7)));
        Assert.assertTrue(Integer.toString(3).equals(q.get(3)));
        Assert.assertTrue(Integer.toString(15).equals(q.get(12)));

        Assert.assertTrue(check == 4);
    }

    @DataProvider(name = "addData")
    public Object[][] addData01() {
        return new Object[][] {
            { new String []{}, 0},
            { new String []{"0"}, 1},
            { new String []{"0","1"}, 2},
            { new String []{"0","1","2"}, 3},
            { new String []{"0","1","2","3"}, 4},
            { new String []{"0","1","2","3","4"}, 5},
        };
    }

    @Test(dataProvider = "addData")
    public void testAdd( String[] value, int result) {
        CircularQueue q = new CircularQueue(5);
        q.init();

        for(String val : value) {
            q.add(val);

        }

        Assert.assertEquals(result, q.getSize());
    }

    @Test void testNullAdd(){
        CircularQueue q = new CircularQueue();
        Assert.assertFalse(q.add(null));
    }

    @DataProvider(name = "addData00")
    public Object[][] addData00() {
        return new Object[][] {
            // no matter how many values you add - the size remains the same
            { new String []{"0","1","2","3","4"}, 5, 4,0},
            { new String []{"0","1","2","3","4","5"}, 5, 0,1 },
            { new String []{"0","1","2","3","4","5","6","7","8"}, 5,3, 4},
            { new String []{"0","1","2","3","4","5","6"}, 5,1,2},
            { new String []{"0","1","2","3","4","5","6","7","8","9"}, 5,4,0},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10"}, 5,0,1},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, 5,4,0},
        };
    }
    @Test(dataProvider = "addData00")
    public void testAddandDelete( String[] value, int size, int endQ, int startQ) {
        CircularQueue q = new CircularQueue(5);
        for(String val : value) {
            q.add(val);
            Assert.assertEquals((int)Integer.valueOf(val), q.getDataEndIndex());
        }
        Assert.assertEquals(Integer.valueOf(value[value.length-1])%size, q.getQueueEndIndex());
        Assert.assertEquals(startQ, q.getQueueStartIndex());
        Assert.assertEquals(size, q.getSize());
    }

    @Test
    public void testDeleteQEmpty() {
        CircularQueue q = new CircularQueue();
        Assert.assertFalse(q.delete());
    }

    @DataProvider(name = "deleteData01")
    public Object[][] deleteData01() {
        return new Object[][] {
            { new String []{"0","1","2","3","4"}, 5,0},
            { new String []{"0","1","2","3","4","5"}, 5,1 },
            { new String []{"0","1","2","3","4","5","6","7","8"}, 5,4},
            { new String []{"0","1","2","3","4","5","6"}, 5,2},
            { new String []{"0","1","2","3","4","5","6","7","8","9"}, 5,0},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10"}, 5,1},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, 5,0},
        };
    }

    @Test (dataProvider ="deleteData01")
    public void testDelete(String[]values, int size, int currentStart) {
        CircularQueue q = new CircularQueue(5);
        for(String val : values) {
            q.add(val);
        }
        q.delete();
        Assert.assertTrue(size > q.getSize());
        if(currentStart!=(size -1 )){
            Assert.assertTrue(q.getQueueStartIndex() > currentStart);
        }
        else if(currentStart == size-1 ){
            Assert.assertTrue(q.getQueueStartIndex() == 0);
        }
    }

    @Test
    public void testisIndexInDataRange(){
        CircularQueue q = new CircularQueue(4);
        q.add("p");
        Assert.assertFalse(q.isIndexInDataRange(1));
        Assert.assertFalse(q.isIndexInDataRange(2));
        q.add("r");
        q.add("a");
        q.add("m");
        //maximum value
        Assert.assertFalse(q.isIndexInDataRange(4));
        q.add("a");
        //after adding - it found relative index
        Assert.assertTrue(q.isIndexInDataRange(4));
    }

    @DataProvider(name = "relativeIndex")
    public Object[][] relativeIndex() {
        return new Object[][] {
            { new String []{"0","1","2","3","4","5"}, 4,1 },
            { new String []{"0","1","2","3","4","5","6","7","8"}, 5,2},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"},11,2},
        };
    }

    @Test (dataProvider ="relativeIndex")
    public void testgetRelativeIndex(String []values, int value, int expected ){
        CircularQueue q = new CircularQueue(3);
        for(String val :values){
            q.add(val);
        }

        Assert.assertTrue(q.getRelativeIndex(value) == expected);
    }

    @DataProvider(name = "dataLimits")
    public Object[][] dataLimits() {
        return new Object[][] {
            { new String []{"0"}},
            { new String []{"0","1"}},
            { new String []{"0","1","2","3"}},
            { new String []{"0","1","2","3","4"}},
            { new String []{"0","1","2","3","4","5"}, },
            { new String []{"0","1","2","3","4","5","6","7","8"}},
            { new String []{"0","1","2","3","4","5","6"}},
            { new String []{"0","1","2","3","4","5","6","7","8","9"}},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10"}},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"}},
            { new String []{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"}},
        };
    }

    //Test dataStart and dataEnd limits
    @Test (dataProvider ="dataLimits")
    public void testDataLimits(String[] values){
        int size = 5;
        CircularQueue q = new CircularQueue(size);
        for(String val: values) {
            q.add(val);
            Assert.assertTrue((int)Integer.valueOf(val) == q.getDataEndIndex() );
            if((int) Integer.valueOf(val) <= (size-1)) {
                Assert.assertTrue(0 == q.getDataStartIndex());
            }
            else {
                Assert.assertTrue((int)Integer.valueOf(val) - (size -1) == q.getDataStartIndex() );
            }
        }
    }

    @Test
    public void testInit() {
        CircularQueue q = new CircularQueue();
        q.init();
        Assert.assertTrue(q.getDataStartIndex() == 0);
        Assert.assertTrue(q.getDataEndIndex() == -1);
        Assert.assertTrue(q.getQueueStartIndex() == 0);
        Assert.assertTrue(q.getQueueEndIndex()== -1);
    }
}
