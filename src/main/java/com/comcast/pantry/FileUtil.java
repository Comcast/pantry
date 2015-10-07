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

package com.comcast.pantry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Basic File Helping utility methods.
 *
 * @author Clark Malmgren
 */
public class FileUtil {

    /**
     * Recursively delete the directory or file.
     *
     * @param file
     *            the directory or file to delete
     */
    public static void deleteRecursive(File file) {

        if (file.isDirectory()) {

            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }

        file.delete();
    }

    /**
     * Copy the contents of an input stream to an output stream. This copies the data in upto 1kB
     * chunks. This method will also close both streams after copying the data. This is the same as
     * invoking <code>FileUtil.copy(in, out, true);</code>.
     *
     * @param in
     *            the stream to read from
     * @param out
     *            the stream to write to
     *
     * @throws IOException
     *             if there was a failure reading or writing the streams
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, true);
    }

    /**
     * Copy the contents of an input stream to an output stream. This copies the data in upto 1kB
     * chunks. This method will only close both streams after copying the data if the
     * <code>close</code> parameter is set to <code>true</code>.
     *
     * @param in
     *            the stream to read from
     * @param out
     *            the stream to write to
     * @param close
     *            if set to <code>true</code>, close both streams after copying the data
     *
     * @throws IOException
     *             if there was a failure reading or writing the streams
     */
    public static void copy(InputStream in, OutputStream out, boolean close) throws IOException {
        try {
            byte[] b = new byte[1024];
            int len = 0;

            while (-1 != (len = in.read(b))) {
                out.write(b, 0, len);
            }
        } finally {
            if (close) {
                in.close();
                out.close();
            }
        }
    }

    /**
     * Copy a set of files to the given destination directory.
     *
     * @param directory
     *            the directory to copy the files to
     * @param files
     *            a list of files to copy to the given directory
     *
     * @throws IOException
     *             if there was a problem copying the file
     */
    public static void copyTo(File directory, File... files) throws IOException {
        directory.mkdirs();

        for (File file : files) {
            copy(file, new File(directory, file.getName()));
        }
    }

    /**
     * Copy the source file to the destination file. If the source is a folder, it will copy
     * recursively.
     *
     * @param source
     *            the source file
     * @param dest
     *            the destination file
     *
     * @throws IOException
     *             if there was a problem copying the file
     */
    public static void copy(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            dest.mkdirs();
            for (File child : source.listFiles()) {
                copy(child, new File(dest, child.getName()));
            }
        } else {
            copy(new FileInputStream(source), new FileOutputStream(dest));
        }
    }

    /**
     * Read the entire input source file to a string and return that value.
     *
     * @param source
     *            the file to read
     *
     * @return the entire string contents of the given file
     *
     * @throws IOException
     *             if there was a problem reading the file
     */
    public static String readFully(File source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) source.length());
        InputStream in = new FileInputStream(source);

        copy(in, out, true);

        return out.toString();
    }
}
