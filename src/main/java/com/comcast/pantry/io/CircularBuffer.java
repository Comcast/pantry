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

import java.io.Closeable;
import java.io.InterruptedIOException;

import org.apache.log4j.Logger;

/**
 * A circular buffer that will allow for multi-threaded access to a shared circular buffer.
 * Note that if an instance of circular buffer is encapsulated into a BufferedReader,
 * e.g. new BufferedReader(new InputStreamReader(new CircularBufferInputStream(circularBuffer))),
 * then circularBuffer.close() must be called before calling bufferedReader.close() to avoid
 * deadlock situation
 *
 * @author Clark Malmgren
 * @author Kevin Pearson
 */
public class CircularBuffer implements Closeable {

    protected Logger logger = Logger.getLogger(CircularBuffer.class);

    private static final long WRITE_TIMEOUT_MS = (10 * 60 * 1000); // 10 min

    private int start;
    private int used;
    private byte[] buffer;
    private boolean closed;
    private boolean allowPartial = false;

    /**
     * The last thread that called read().
     */
    private Thread lastReaderThread = null;

    /**
     * Construct a new CircularBuffer of the given size.
     *
     * @param size
     *            the size of the given circular buffer
     */
    public CircularBuffer(int size) {
        this(size, false);
    }

    /**
     * Construct a new CircularBuffer of the given size.
     *
     * @param size
     *            the size of the given circular buffer
     * @param allowPartial
     *            true if the when reading data and there is not enough data to
     *            fill the whole read byte array, then a partial amount of data will be read
     *            if false, then the read will block until there is enough data to
     *            fill the read buffer
     */
    public CircularBuffer(int size, boolean allowPartial) {
        this.buffer = new byte[size];
        this.start = 0;
        this.used = 0;
        this.closed = false;
        this.allowPartial = allowPartial;
    }

    private void dumpStackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace != null) {
            for (int i = 0; i < stackTrace.length; i++) {
                logger.info(stackTrace[i]);
            }
        }
    }

    /**
     * Adds the contents of buf between <code>off</code> and <code>(off+len)</code> into the
     * circular buffer. If the buffer becomes full at any time, this method will block until more
     * space becomes becomes available and the entire input has finished writing.
     * @throws InterruptedIOException
     *
     * @see java.io.OutputStream#write(byte[])
     */
    public synchronized void write(byte[] b) throws InterruptedIOException {
        int off = 0;

        while (true) {
            /* If we are full, block until something is read */
            if (used == buffer.length) {
                try {
                    this.wait(WRITE_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
                if (!closed && (used == buffer.length)) {
                    logger.error("timed out waiting to write buffer: " + this);
                    // For trouble-shooting why the reader thread is not reading
                    if (lastReaderThread != null) {
                        this.dumpStackTrace(lastReaderThread.getStackTrace());
                    }
                    return;
                }

            }

            /* If we are closed, throw and IllegalStateException */
            if (closed) {
                throw new IllegalStateException("Cannot write to a closed stream");
            }

            /* Write as much as we can in larger chunks (take advantage of arraycopy) */
            while ((used < buffer.length) && (off < b.length)) {
                int len = buffer.length - (isWrapped() ?  used : start + used);
                len = Math.min(len, b.length - off);

                System.arraycopy(b, off, buffer, getEnd(), len);
                off += len;
                used += len;
            }

            /* Notify anything else that might be waiting */
            this.notify();

            /* If we finally wrote everything, return cause we are done! */
            if (off == b.length) {
                return;
            }
        }
    }

    /**
     * Reads data from the buffer into <code>buf</code>, starting at offset <code>off</code>. No
     * more than maxlen may be read at any time. This method may return any about of data of size &gt;
     * 0 but &lt;= maxlen at any time. The actual amount of data read is returned. If no input is
     * available, this method will wait until either (a) data is available or (b) {@link #close()}
     * is called.
     * @throws InterruptedIOException
     *
     * @see java.io.InputStream#read(byte[])
     */
    public synchronized int read(byte[] b) throws InterruptedIOException {

        this.lastReaderThread = Thread.currentThread();
        int off = 0;

        if (closed) {
            return -1;
        }

        while (true) {
            /* No data to read. If we have already read data and we allow returning partial data
             * then return the partial data */
            if (used == 0) {
                if (allowPartial && (off > 0)) {
                    return off;
                } else {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                }
            }

            /*
             * If we are closed, return the number of bytes currently read. If no bytes have yet
             * been read, we want this to return -1 instead of 0.
             */
            if (closed) {
                return (0 == off) ? -1 : off;
            }

            /* Read as much as we can in larger chunks (take advantage of arraycopy) */
            while ((used > 0) && off < b.length) {
                int len = isWrapped() ? buffer.length - start : used;
                len = Math.min(len, b.length - off);

                System.arraycopy(buffer, start, b, off, len);
                off += len;
                start = (start + len) % buffer.length;
                used -= len;
            }

            /* Notify anything else that might be waiting */
            this.notify();

            /* If we finally read everything, return cause we are done! */
            if (off == b.length) {
                return off;
            }
        }
    }

    /**
     * Gets the index of the end of the used data
     * @return
     */
    public int getEnd() {
        return (start + used) % buffer.length;
    }

    /**
     * Checks if the used data wraps around to the beginning of the buffer array.
     * @return
     */
    public boolean isWrapped() {
        return used >= (buffer.length - start);
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    public synchronized void close() {
        this.closed = true;
        this.notifyAll();
        this.lastReaderThread = null;
    }
}
