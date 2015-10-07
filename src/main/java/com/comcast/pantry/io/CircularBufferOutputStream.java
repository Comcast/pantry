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
import java.io.OutputStream;

/**
 * A {@link OutputStream} compatible reference to a {@link CircularBuffer}.
 *
 * @author Clark Malmgren
 */
public class CircularBufferOutputStream extends OutputStream {

    private CircularBuffer buffer;

    /**
     * Construct a new {@link CircularBufferOutputStream} to write to the given
     * {@link CircularBuffer}.
     *
     * @param buffer
     *            the buffer to write to
     */
    public CircularBufferOutputStream(CircularBuffer buffer) {
        this.buffer = buffer;
    }

    /*
     * (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        byte[] buff = new byte[1];
        buff[0] = (byte) b;
        buffer.write(buff);
    }

    /*
     * (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        buffer.close();
    }

    /*
     * (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        buffer.write(b);
    }

    /*
     * (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] buff = new byte[len];
        System.arraycopy(b, off, buff, 0, len);
        write(buff);
    }

}
