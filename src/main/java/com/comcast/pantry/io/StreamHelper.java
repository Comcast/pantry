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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class to deal with Stream IO.
 *
 * @author Clark Malmgren
 */
public class StreamHelper {

    /**
     * The default buffer size to use when calls are made to
     * {@link #copy(InputStream, OutputStream)}.
     */
    public static final int DEFAULT_BUFFER_SIZE = 512;

    /**
     * Copy the entire stream from the input stream to the output stream using the default buffer
     * size ({@value #DEFAULT_BUFFER_SIZE} bytes).
     *
     * @param in
     *            the stream to copy from
     * @param out
     *            the stream to copy to
     *
     * @throws IOException
     *             if there was a problem with either stream
     */
    public void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy the entire stream from the input stream to the output stream using the specified buffer
     * size.
     *
     * @param in
     *            the stream to copy from
     * @param out
     *            the stream to copy to
     * @param bufferSize
     *            the size of the buffer to use when copying data
     *
     * @throws IOException
     *             if there was a problem with either stream
     */
    public void copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        int length = -1;
        byte[] buffer = new byte[bufferSize];

        while (true) {
            length = in.read(buffer);

            if (-1 == length) {
                break;
            } else {
                out.write(buffer, 0, length);
            }
        }

        buffer = null;
    }

    /**
     * Close a group of closable objects like an {@link InputStream} or {@link OutputStream}.
     *
     * @param targets
     *            the objects to close
     */
    public static void close(Closeable... targets) {
        for (Closeable target : targets) {
            if (target != null) {
                try {
                    target.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
