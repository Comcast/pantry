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

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link InputStream} compatible reference to a {@link CircularBuffer}.
 *
 * @author Clark Malmgren
 */
public class CircularBufferInputStream extends InputStream {

    private CircularBuffer buffer;

    /**
     * Construct a new {@link CircularBufferInputStream} to read from the given
     * {@link CircularBuffer}.
     *
     * @param buffer
     *            the buffer to read from
     */
    public CircularBufferInputStream(CircularBuffer buffer) {
        this.buffer = buffer;
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int len = buffer.read(b);
        if (len != 1) {
            return -1;
        }
        return b[0];
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        buffer.close();
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        return buffer.read(b);
    }

    /*
     * (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        byte[] buff = new byte[len];
        int actual = buffer.read(buff);
        /* Only do the arraycopy if there was something to read */
        if (actual > 0) {
            System.arraycopy(buff, 0, b, off, actual);
        }
        return actual;
    }

}
