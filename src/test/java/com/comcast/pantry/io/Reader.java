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

import java.io.InterruptedIOException;

public class Reader extends Thread {

    private CircularBuffer cb;
    private byte[] contents;
    private int size;
    private Thread next;

    public Reader(CircularBuffer cb, int size) {
        this.cb = cb;
        this.contents = new byte[size];
        this.next = null;
    }

    public void setNext(Thread next) {
        this.next = next;
    }

    public void run() {
        try {
            size = cb.read(contents);

            if (null != next) {
                next.start();
            }
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getContents() {
        return contents;
    }

    public int getSize() {
        return size;
    }
}
