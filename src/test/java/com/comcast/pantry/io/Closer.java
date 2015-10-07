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

public class Closer extends Thread {

    private CircularBuffer cb;
    private Thread next;
    private long delay;

    public Closer(CircularBuffer cb, long delay) {
        this.cb = cb;
        this.next = null;
        this.delay = delay;
    }

    public void setNext(Thread next) {
        this.next = next;
    }

    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException iex) {
            // ignored
        }

        cb.close();

        if (null != next) {
            next.start();
        }
    }
}
