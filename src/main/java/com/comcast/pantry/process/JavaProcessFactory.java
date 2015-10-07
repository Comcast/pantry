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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles forking a java process. In order to operate correctly, the user must specify either the
 * exectuable jar (using either {@link #setExecJar(File)} or {@link #setExecJar(String)}) or the
 * executable main class (using {@link #setMainClass(String)}) but not both.
 *
 * @author Clark Malmgren
 */
public class JavaProcessFactory extends ProcessFactory {

    private List<String> classPath = new ArrayList<String>();
    private List<String> javaOptions = new ArrayList<String>();
    private String jar = null;
    private String mainClass = null;

    /**
     * Add the given path to the class path.
     *
     * @param path
     *            the entry to add
     */
    public void addToClassPath(String path) {
        classPath.add(path);
    }

    /**
     * Add the given file to the class path.
     *
     * @param file
     *            the file to add
     */
    public void addToClassPath(File file) {
        classPath.add(file.getAbsolutePath());
    }

    /**
     * Set the jar file to execute.
     *
     * @param jar
     *            the jar file to execute
     */
    public void setExecJar(File jar) {
        setExecJar(jar.getAbsolutePath());
    }

    /**
     * Set the jar file to execute.
     *
     * @param jar
     *            the jar file to execute
     */
    public void setExecJar(String jar) {
        if (null != mainClass) {
            throw new IllegalStateException("Cannot set a executable jar and class");
        }

        this.jar = jar;
    }

    /**
     * Set the main class to execute.
     *
     * @param mainClass
     *            the main class to execute
     */
    public void setMainClass(String mainClass) {
        if (null != jar) {
            throw new IllegalStateException("Cannot set a executable jar and class");
        }

        this.mainClass = mainClass;
    }

    /**
     * Add a option to pass to the java process.
     *
     * @param option
     *            the option to add
     */
    public void addJavaOption(String option) {
        javaOptions.add(option);
    }

    /**
     * Set the java initial memory size (-Xms).
     *
     * @param size
     *            the size to set
     */
    public void setInitialMemorySize(String size) {
        addJavaOption("-Xms" + size);
    }

    /**
     * Set the java maximum memory size (-Xmx).
     *
     * @param size
     *            the size to set
     */
    public void setMaximumMemorySize(String size) {
        addJavaOption("-Xmx" + size);
    }

    /**
     * Add the full classpath command (-cp classpath) to the command list
     *
     * @param command
     *            the command list to add it to
     */
    private void addClassPath(List<String> command) {
        if (classPath.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < classPath.size(); i++) {
            if (i != 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(classPath.get(i));
        }

        command.add("-cp");
        command.add(sb.toString());
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.common.process.ProcessFactory#getCommandList()
     */
    @Override
    public List<String> getCommandList() {
        ArrayList<String> command = new ArrayList<String>();

        command.add(getJavaPath());
        addClassPath(command);
        command.addAll(javaOptions);

        if (null != jar) {
            command.add("-jar");
            command.add(jar);
        } else if (null != mainClass) {
            command.add(mainClass);
        } else {
            throw new IllegalStateException("Must specify either a jar or main class");
        }

        command.addAll(super.getCommandList());
        return command;
    }

    /**
     * Find the java executable file. This will look in <code>${java.home}/bin</code> for an
     * executable java file. This can be in the form of <code>java</code>, <code>java.sh</code> or
     * <code>java.exe</code>.
     *
     * @return the path to the java executable
     */
    private static String getJavaPath() {
        File home = new File(System.getProperty("java.home"));
        File bin = new File(home, "bin");

        for (File child : bin.listFiles()) {
            String name = child.getName();
            if (name.matches("java(\\.sh|\\.exe)?")) {
                return child.getAbsolutePath();
            }
        }

        throw new RuntimeException("No valid java executable in " + bin.getAbsolutePath());
    }
}
