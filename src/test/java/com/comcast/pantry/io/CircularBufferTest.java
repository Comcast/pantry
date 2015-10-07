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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CircularBufferTest {

    private ByteArrayCreator creator;

    @BeforeClass
    public void setup() {
        creator = new ByteArrayCreator(1253456532l);
    }

    @Test
    public void testBufferThatFitsAll() throws InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(128);

        byte[] contents = creator.getBytes(100);
        byte[] actual = new byte[contents.length];
        cb.write(contents);
        cb.read(actual);

        Assert.assertEquals(contents, actual);
    }

    @Test
    public void testBufferThatOverflowsBoundary() throws InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(40);

        byte[] contents = creator.getBytes(35);
        byte[] actual = new byte[contents.length];
        cb.write(contents);
        cb.read(actual);

        Assert.assertEquals(contents, actual);

        byte[] contents2 = creator.getBytes(35);
        byte[] actual2 = new byte[contents2.length];
        cb.write(contents2);
        cb.read(actual2);

        Assert.assertEquals(contents2, actual2);
    }

    @Test
    public void testLargeData() throws InterruptedException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(128);
        Writer writer = new Writer(cb, contents);
        Reader reader = new Reader(cb, 128);

        writer.start();
        reader.start();

        writer.join();
        reader.join();

        Assert.assertEquals(128, reader.getSize());
        Assert.assertEquals(contents, reader.getContents());
    }

    @Test
    public void testFillBeforeReading() throws InterruptedException, InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(32);
        byte[] contents2 = creator.getBytes(32);
        Writer writer = new Writer(cb, contents2);
        Reader reader = new Reader(cb, 64);

        cb.write(contents);
        writer.start();
        reader.start();

        writer.join();
        reader.join();

        Assert.assertEquals(64, reader.getSize());
        Assert.assertEquals(concat(contents, contents2), reader.getContents());
    }

    @Test
    public void testLargeDataMultipleWriters() throws InterruptedException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(128);
        byte[] contents2 = creator.getBytes(64);
        Writer writer = new Writer(cb, contents);
        Writer writer2 = new Writer(cb, contents2);
        writer.setNext(writer2);
        Reader reader = new Reader(cb, 128 + 64);

        writer.start();
        reader.start();

        writer.join();
        writer2.join();
        reader.join();

        Assert.assertEquals(128 + 64, reader.getSize());
        Assert.assertEquals(concat(contents, contents2), reader.getContents());
    }

    @Test
    public void testLargeDataMultipleReadersAndWriters() throws InterruptedException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] c1 = creator.getBytes(128);
        byte[] c2 = creator.getBytes(64);
        byte[] c3 = creator.getBytes(132);
        byte[] c4 = creator.getBytes(65);

        Writer w1 = new Writer(cb, c1);
        Writer w2 = new Writer(cb, c2);
        Writer w3 = new Writer(cb, c3);
        Writer w4 = new Writer(cb, c4);

        w1.setNext(w2);
        w2.setNext(w3);
        w3.setNext(w4);

        Reader r1 = new Reader(cb, 122);
        Reader r2 = new Reader(cb, 23);
        Reader r3 = new Reader(cb, 67);
        Reader r4 = new Reader(cb, 84);
        Reader r5 = new Reader(cb, 93);

        r1.setNext(r2);
        r2.setNext(r3);
        r3.setNext(r4);
        r4.setNext(r5);

        w1.start();
        r1.start();

        w1.join();
        w2.join();
        w3.join();
        w4.join();

        r1.join();
        r2.join();
        r3.join();
        r4.join();
        r5.join();

        byte[] expected = concat(c1, c2, c3, c4);
        byte[] actual = concat(r1.getContents(), r2.getContents(), r3.getContents(),
                r4.getContents(), r5.getContents());
        Assert.assertEquals(expected, actual);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testClosingBeforeWriting() throws InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(8);
        cb.close();
        cb.write(contents);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testClosingWhileWriting() throws InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(50);
        Closer closer = new Closer(cb, 50);
        closer.start();
        cb.write(contents);
    }

    @Test
    public void testClosingBeforeReading() throws InterruptedException, InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(16);
        byte[] actual = new byte[16];

        cb.write(contents);
        cb.close();
        int length = cb.read(actual);

        Assert.assertEquals(-1, length);
    }

    @Test
    public void testClosingWhileReading() throws InterruptedException {
        CircularBuffer cb = new CircularBuffer(32);

        byte[] contents = creator.getBytes(16);
        Writer writer = new Writer(cb, contents);
        Closer closer = new Closer(cb, 100);
        writer.setNext(closer);

        Reader reader = new Reader(cb, 32);

        writer.start();
        reader.start();

        writer.join();
        reader.join();

        Assert.assertEquals(16, reader.getSize());

        byte[] actual = new byte[16];
        System.arraycopy(reader.getContents(), 0, actual, 0, 16);

        Assert.assertEquals(contents, actual);
    }

    @Test
    public void testClosingWhileWaitingForFirstRead() throws InterruptedException {
        CircularBuffer cb = new CircularBuffer(32);

        Closer closer = new Closer(cb, 100);
        Reader reader = new Reader(cb, 32);

        reader.start();
        closer.start();

        reader.join();
        closer.join();

        Assert.assertEquals(-1, reader.getSize());
    }

    @Test
    public void testCircularBufferInputStream() throws InterruptedException {
        List<String> lines = Arrays.asList("kevin", "lyle", "pearson");
        CircularBuffer cb = new CircularBuffer(15);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new CircularBufferInputStream(cb)));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new CircularBufferOutputStream(cb)));


        BufferedWriterThread bwt = new BufferedWriterThread(writer, lines);
        BufferedReaderThread brt = new BufferedReaderThread(reader);
        Closer closer = new Closer(cb, 3000);

        bwt.start();
        brt.start();
        closer.start();
        bwt.join();
        brt.join();
        closer.join();

        Assert.assertEquals(brt.getLines(), lines);

    }

    @Test
    public void testReadPartial() throws InterruptedIOException {
        CircularBuffer cb = new CircularBuffer(35, true);
        byte[] contents = creator.getBytes(16);
        cb.write(contents);
        byte[] read = new byte[32]; // ask to read more than what is written
        int n = cb.read(read);

        Assert.assertEquals(n, 16);
        byte[] truncated = new byte[16];
        System.arraycopy(read, 0, truncated, 0, 16);

        Assert.assertEquals(truncated, contents);
    }

    private static byte[] concat(byte[]... arrays) {
        int size = 0;
        for (byte[] array : arrays) {
            size += array.length;
        }

        int off = 0;
        byte[] concat = new byte[size];
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, concat, off, array.length);
            off += array.length;
        }

        return concat;
    }
}
