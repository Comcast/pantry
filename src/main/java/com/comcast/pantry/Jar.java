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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * An abstract representation of a Jar file.
 *
 * @author Clark Malmgren
 */
public class Jar extends File {

    /**
     * {@inheritDoc}
     */
    public Jar(String pathname) {
        super(pathname);
    }

    /**
     * {@inheritDoc}
     */
    public Jar(URI uri) {
        super(uri);
    }

    /**
     * {@inheritDoc}
     */
    public Jar(String parent, String child) {
        super(parent, child);
    }

    /**
     * {@inheritDoc}
     */
    public Jar(File parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new <code>Jar</code> from a <code>File</code> object.
     *
     * @param file
     *            the {@link File} representation of the <code>Jar</code> to
     *            create
     * @throws NullPointerException
     *             if the specified <code>file</code> is <code>null</code>
     */
    public Jar(File file) throws NullPointerException {
        this(file.getAbsolutePath());
    }

    /**
     * Expands this <code>Jar</code> into a folder. If the folder does not
     * exist, the folder will be created.
     *
     * @param target
     *            the target folder to expand into
     * @throws IOException
     *             if there is an issue expanding
     */
    public void expand(File target) throws IOException {
        JarFile jarFile = new JarFile(this);

        /* First create all directories */
        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            if (entry.isDirectory()) {
                File dir = new File(target, entry.getName());
                dir.mkdirs();
            }
        }

        /* Now write the files */
        entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            if (false == entry.isDirectory()) {
                File outputFile = new File(target, entry.getName());

                /* Ensure that our directory exists because this Jar might not
                 * have included the directory setup*/
                File parent = outputFile.getParentFile();
                if ((null != parent) && (false == parent.exists())) {
                    parent.mkdirs();
                }

                InputStream is = jarFile.getInputStream(entry);
                OutputStream os = new BufferedOutputStream(
                        new FileOutputStream(outputFile));

                /* Do the copy itself */
                int c = -1;
                while ((c = is.read()) != -1) {
                    os.write(c);
                }

                /* Close the streams finalizing the written data */
                is.close();
                os.close();
            }
        }
        jarFile.close();
    }

    public void printContents(PrintStream ps) throws IOException {
        JarFile jarFile = new JarFile(this);

        /* First create all directories */
        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            ps.println(entry.getName());
        }
    }

    public String getContents() throws IOException {
        JarFile jarFile = new JarFile(this);
        StringBuffer sb = new StringBuffer();

        /* Get all elements */
        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            sb.append(entry.getName());
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Creates a new archive file. If this Jar already exists, it is deleted
     * first before adding the files contained within the specified
     * <code>baseDirectory</code>.
     *
     * @param baseDirectory
     *            the root directory specifying the files that should be added
     *            to the new archive
     * @throws IOException
     *             if there is an issue writing, adding to or compressing the
     *             archive
     */
    public void create(File baseDirectory) throws IOException {
        /* First delete the file if it exists */
        if (this.exists()) {
            if (false == this.delete()) {
                System.out.println("[WARN] Failed to delete existing file: " + this.getAbsolutePath());
            }
        }

        /* Generate JarOutputStream */
        JarOutputStream jar = new JarOutputStream(new FileOutputStream(this));
        addRecursive(jar, baseDirectory, baseDirectory);
        jar.close();
    }

    /**
     * Recursively add the files contained in <code>node</code> as relative
     * paths from <code>root</code> to the specified <code>jar</code>.
     *
     * @param jar
     *            the {@link JarOutputStream} to add to
     * @param node
     *            the base of the file system that is to be added to the
     *            specified <code>jar</code>
     * @param root
     *            the file to use to generate relative path names
     * @throws IOException
     *             if there was a problem writing to the jar file
     */
    private void addRecursive(JarOutputStream jar, File node, File root) throws IOException
    {
        String relativePath = node.getAbsolutePath().replace(
                root.getAbsolutePath(), "");

        /* The path should not start with a separator character */
        while (relativePath.startsWith(File.separator)) {
            relativePath = relativePath.substring(1);
        }

        /*
         * From here on out, the path names should all be the zip file specific
         * '/' instead of the platform dependent File.separator
         */
        relativePath = relativePath.replace(File.separatorChar, '/');

        if (node.isDirectory()) {
            /* In a zipfile, directories must end with a '/' */
            if (!relativePath.endsWith("/")) {
                relativePath = relativePath + '/';
            }

            jar.putNextEntry(new JarEntry(relativePath));
            jar.closeEntry();

            /* If this is a directory, parse over children */
            File[] children = node.listFiles();
            for (File child : children) {
                addRecursive(jar, child, root);
            }
        } else {
            /*
             * This is a file, so find the relative path from the root and then
             * add the element
             */
            jar.putNextEntry(new JarEntry(relativePath));

            /* Now write the data */
            FileInputStream in = new FileInputStream(node);
            int c = -1;
            while ((c = in.read()) != -1) {
                jar.write(c);
            }

            /* Clean up */
            jar.closeEntry();
            in.close();
        }
    }
}
