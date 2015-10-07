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

import org.apache.log4j.Logger;

/**
 * This is a circular buffer {@link #start} and {{@link #end} point to
 * the head and tail of the buffer respectively. The buffer acts a queue(FIFO)
 * Deletions are done from the head/start. Insertion are done from the tail/end
 *
 *
 * This is a fixed size buffer of size that is given in {@link CircularQueue#CircularQueue(int)}
 *
 * @author panand200
 *
 */
public class CircularQueue {

    /** Points to where Queue starts */
    private int start;

    /** Points to end, where the last value is stored */
    private int end;

    /**
     * <P>
     * Maintains the actual index of the data. That is, it keeps track of what
     * values could have possibly been overwritten. Example if we have a queue
     * of size 10 (index 0-9) and we add 15 lines. The actual range of valid data will now
     * be 5-15 where {@link #dataStart} will represent 5 and {@link #dataEnd} will represent 15.
     * <p>
     */
    private int dataStart;
    private int dataEnd;

    /** Maintains the number of elements currently in the queue */
    private int count;
    public static final int DEFAULT_MAX_SIZE = 20000;
    private int maxSize = DEFAULT_MAX_SIZE;
    // String buffer
    private String[] queue;

    private static Logger logger = Logger.getLogger(CircularQueue.class);

    /**
     * Create a circular queue with desired size
     *
     * @param size
     */
    public CircularQueue(int size) {
        maxSize = size;
        init();
    }

    /**
     * Creates a circular queue with default size as {@link #DEFAULT_MAX_SIZE}
     */
    public CircularQueue() {
        init();
    }

    /**
     * Initializes the indexes.
     */
    protected void init() {
        queue = new String[maxSize];
        start = 0;
        end = -1;
        dataStart = 0;
        dataEnd = -1; //After we've inserted data, we increment the value.
    }

    /**
     * Find if this index is in the range of the actual data in the queue. This
     * function will first check to see if the index is in range of where the
     * actual data lies. i.e. whether the index we're looking for was
     * overwritten or not See {{@link #dataEnd} and {{@link #dataEnd} It will
     * then get a relativeIndex value {{@link #getRelativeIndex(int)} incase  dataEnd
     * has surpassed the queueEnd, and finally it will check to
     * see if this index is in range with the current {@link #start} and {@link #end}
     *
     * @param no the index to check if it is in range
     * @return boolean
     */
    protected boolean inRange(int no) {

        /*
         * There can be three scenarios 1- start<end - This is when the tail
         * hasn't completed a circle yet and it is less than the max size
         *
         * 2- start>end This is when the tail hit the max size and the head
         * started deleting so it's completed a circle
         *
         * 3 - start == end this is when either the queue is empty or full
         */

        if (!isIndexInDataRange(no))
            return false;

        int index = getRelativeIndex(no);

        if (start < end) {
            return (start <= index && index <= end);
        }

        if (start > end) {
            // get the range between start and max size
            if (index <= (maxSize - 1) && index >= start) {
                return true;
            }

            if (index <= end && index >= 0) {
                return true;
            }
        }

        if (start == end && index == start) {
            return true;
        }

        return false;

    }

    /**
     * Checks to see if the queue is full. Return a boolean accordingly.
     *
     * @return
     *      boolean
     */
    public boolean isFull() {
        if (count == (this.maxSize)) {
            return true;
        }
        return false;
    }

    /**
     * Check to see if the queue is empty.
     *
     * @return
     *      boolean
     */
    public boolean isEmpty() {
        if (count == 0)
            return true;

        return false;
    }

    /**
     * Insert a value into the queue.
     * <p>
     * Null values are not inserted into the queue. Null is used to determine if
     * an index in the queue is empty or not
     * <p>
     *
     * <p>
     * If the queue is not full and the value is not null, it's inserted at the
     * end of the queue. If the queue is full and we've encountered a situation
     * for an overflow we will delete from the start as necessary and overwrite
     * the value.
     * <p>
     *
     * @param value
     *            to be inserted
     * @return
     *      true if value was inserted, false otherwise
     */
    public boolean add(String value) {

        if (value == null) {
            logger.info("Value being inserted into the queue is null");
            return false;
        }

        /*
         * Compute where we need to insert this new value. if we're at the end
         * of the queue, we start from the beginning, else increment end by 1.
         */
        if (end == (maxSize - 1)) {
            end = 0;
        } else {
            end++;
        }

        /* Check to see if the queue is full or not */
        if (!isFull()) {
            queue[end] = value;

        } else {
            delete();
            queue[end] = value;
        }

        dataEnd++;
        count++;

        return true;
    }

    /**
     * Deletes a value from the start of the queue.
     *
     * @return true if value was deleted, false otherwise
     */
    public boolean delete() {
        if (isEmpty()) {
            logger.info("Queue is empty cannot delete - returning");
            return false;
        }
        /* delete value */
        queue[start] = null;

        /* Make sure start now points to where the beginning of the data */
        if (start == (maxSize - 1)) {
            start = 0;
        } else {
            start++;
        }

        dataStart++;
        count--;

        return true;
    }

    /**
     * Returns the value at the specified index. If the index does not lie in
     * the range {@link #inRange(int)} of the actual data in the queue, it'll
     * throw an {@link IndexOutOfBoundsException}
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException
     */
    public String get(int index) throws IndexOutOfBoundsException {

        if (inRange(index)) {
            // make sure that it's the relative index
            int no = getRelativeIndex(index);
            return queue[no];

        } else {
            throw new IndexOutOfBoundsException("Index being accessed is "
                    + index);
        }
    }

    /**
     * Just to make sure that the index value stays within the 0-(size-1)
     * boundary. It will return a relative index, i.e. relative to the current
     * {@link #start} and {@link #end}
     * Example if index that is needed is 244 and it's well within the actual data range
     * this function will find out where within the queue it lies.
     *
     * @param indexToRead
     * @return
     */
    protected int getRelativeIndex(int indexToRead) {
        if (indexToRead >= maxSize) {
            indexToRead = indexToRead % maxSize;
        }
        return indexToRead;
    }

    /**
     * Getter method for {@link #dataStart}
     * @return int
     */
    public int getDataStartIndex() {
        return this.dataStart;
    }

    /**
     * Getter method for {@link #dataEnd}
     * @return
     */
    public int getDataEndIndex() {
        return this.dataEnd;
    }

    /**
     * Getter method for {@link #start}
     * @return
     */
    public int getQueueStartIndex() {
        return this.start;
    }

    /**
     * Getter method for {@link #end}
     * @return
     */
    public int getQueueEndIndex() {
        return this.end;
    }

    /**
     * Checks to see if the given index is between
     * {@link #dataStart} and {@link #dataEnd}
     * @param index
     * @return
     */
    protected boolean isIndexInDataRange(int index) {
        return (index >= dataStart && index <= dataEnd);
    }

    /**
     * Returns the current size of how much data is in this queue. {
     * {@link #count}
     *
     * @return
     */
    protected int getSize() {
        return this.count;
    }
}
