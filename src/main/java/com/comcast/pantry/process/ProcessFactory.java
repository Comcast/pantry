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

package com.comcast.pantry.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ProcessFactory provides a testable and more usable way of forking a process and gathering the
 * logs.
 *
 * @author Clark Malmgren
 */
public class ProcessFactory {

    private List<String> command = new ArrayList<String>();
    private LogConsumer consumer = new DefaultLogConsumer();
    private Map<String, String> environment = new HashMap<String, String>();
    private File baseDir = null;

    /**
     * Add a single argument.
     *
     * @param argument
     *            the argument to add
     */
    public void addArgument(String argument) {
        this.command.add(argument);
    }

    /**
     * Add a list of arguments.
     *
     * @param arguments
     *            the list of arguments to add
     */
    public void addArguments(List<String> arguments) {
        this.command.addAll(arguments);
    }

    /**
     * Set the {@link LogConsumer} to pipe the output from the forked process into.
     *
     * @param consumer
     *            the stream to pipe the output to
     */
    public void setLogConsumer(LogConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Add an environment variable in the forked process.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public void setEnvironmentVariable(String key, String value) {
        this.environment.put(key, value);
    }

    /**
     * Set the directory execute in. If not called, the current directory associated with execution
     * of this java process will be used.
     *
     * @param baseDir
     *            the new base directory
     */
    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Get the command list to pass into the {@link ProcessBuilder}. When specific processes
     * override this {@link ProcessFactory}, they should overload this method to add their specific
     * commands.
     *
     * @return the command list to pass into the {@link ProcessBuilder}
     */
    public List<String> getCommandList() {
        return command;
    }

    /**
     * Execute the process and wait for it to finish.
     *
     * @return the exit code
     *
     * @throws IOException
     *             if there was a problem creating the {@link ProcessBuilder}
     * @throws InterruptedException
     *             if the thread that calls this mehtod is interrupted while waiting for the forked
     *             process to complete or the output to finish piping to the output
     */
    public int execute() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(getCommandList());
        for (String key : environment.keySet()) {
            processBuilder.environment().put(key, environment.get(key));
        }
        if (null != baseDir) {
            processBuilder.directory(baseDir);
        }

        Process process = processBuilder.start();
        StreamGrabber standardGrabber = new StreamGrabber(process.getInputStream(), true);
        StreamGrabber errorGrabber = new StreamGrabber(process.getErrorStream(), false);

        standardGrabber.start();
        errorGrabber.start();

        standardGrabber.join();
        errorGrabber.join();
        return process.waitFor();
    }

    /**
     * Class to pipe from an {@link InputStream} to an {@link LogConsumer} in a separate thread.
     *
     * @author Clark Malmgren
     */
    public class StreamGrabber extends Thread {

        private final BufferedReader reader;
        private final boolean standard;

        /**
         * Create a new StreamGrabber.
         *
         * @param in
         *            the stream to read from
         */
        public StreamGrabber(InputStream in, boolean standard) {
            this(new BufferedReader(new InputStreamReader(in)), standard);
        }

        /**
         * Create a new StreamGrabber.
         *
         * @param reader
         *            the reader to read from
         */
        public StreamGrabber(BufferedReader reader, boolean standard) {
            this.reader = reader;
            this.standard = standard;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            try {
                String line = null;

                while (null != (line = reader.readLine())) {
                    if (standard) {
                        consumer.logLine(line);
                    } else {
                        consumer.logErrorLine(line);
                    }
                }
            } catch (IOException e) {}
        }
    }
}
