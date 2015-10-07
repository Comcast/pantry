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

import java.io.BufferedWriter;
import java.util.Collection;
import java.util.Random;

public class BufferedWriterThread extends Thread {

    private BufferedWriter writer;
    private Collection<String> lines;
    private Random r = new Random();

    public BufferedWriterThread(BufferedWriter writer, Collection<String> lines) {
        this.writer = writer;
        this.lines = lines;
    }

    public void run() {
        for (String line : lines) {
            try {
                this.writer.write(line);
                this.writer.newLine();
                this.writer.flush();
                Thread.sleep(r.nextInt(300));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
