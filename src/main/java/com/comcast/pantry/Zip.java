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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * An abstract representation of a Zip file.
 *
 * @author Clark Malmgren
 * @author Peter Janes
 * @author hhur200
 *
 */
public class Zip extends File {

    /**
     * {@inheritDoc}
     */
    public Zip(String pathname) {
        super(pathname);
    }

    /**
     * {@inheritDoc}
     */
    public Zip(URI uri) {
        super(uri);
    }

    /**
     * {@inheritDoc}
     */
    public Zip(String parent, String child) {
        super(parent, child);
    }

    /**
     * {@inheritDoc}
     */
    public Zip(File parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new <code>Zip</code> from a <code>File</code> object.
     *
     * @param file
     *            the {@link File} representation of the <code>Zip</code> to
     *            create
     * @throws NullPointerException
     *             if the specified <code>file</code> is <code>null</code>
     */
    public Zip(File file) throws NullPointerException {
        this(file.getAbsolutePath());
    }

    /**
     * Expands this <code>Zip</code> into a folder. If the folder does not
     * exist, the folder will be created.
     *
     * @param target
     *            the target folder to expand into
     * @throws IOException
     *             if there is an issue expanding
     */
    public void expand(File target) throws IOException {
        ZipFile zipFile = new ZipFile(this);

        /* First create all directories */
        Enumeration entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
            if (entry.isDirectory()) {
                File dir = new File(target, entry.getName());
                dir.mkdirs();
            }
        }

        /* Now write the files */
        entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
            if (false == entry.isDirectory()) {
                File outputFile = new File(target, entry.getName());

                /* Ensure that our directory exists because this Zip might not
                 * have included the directory setup*/
                File parent = outputFile.getParentFile();
                if ((null != parent) && (false == parent.exists())) {
                    parent.mkdirs();
                }

                InputStream is = zipFile.getInputStream(entry);
                OutputStream os = new FileOutputStream(outputFile);

                /* Do the copy itself */
                byte[] b = new byte[1024];
                int len = -1;
                while (-1 != (len = is.read(b))) {
                    os.write(b, 0, len);
                }

                /* Close the streams finalizing the written data */
                is.close();
                os.close();
            }
        }
        zipFile.close();
    }

    public void printContents(PrintStream ps) throws IOException {
        ZipFile zipFile = new ZipFile(this);

        /* First create all directories */
        Enumeration entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
            ps.println(entry.getName());
        }
    }

    public String getContents() throws IOException {
        ZipFile zipFile = new ZipFile(this);
        StringBuffer sb = new StringBuffer();

        /* Get all elements */
        Enumeration entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
            sb.append(entry.getName());
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Creates a new archive file. If this Zip already exists, it is deleted
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

        /* Generate ZipArchiveOutputStream */
        ZipArchiveOutputStream zip = new ZipArchiveOutputStream(new FileOutputStream(this));
        /*
         * Add entries for all of the parent folders first.
         * This is necessary to preserve the lastModified
         * time of those folders.
         */
        addParentFolders(zip, baseDirectory, baseDirectory);
        addRecursive(zip, baseDirectory, baseDirectory);
        zip.close();
    }

    /**
     * Creates a new archive file. If this Zip already exists, it is deleted
     * first before adding the files contained within the specified
     * <code>baseDirectory</code>.
     *
     * @param baseDirectory
     *            the root directory specifying the files that should be added
     *            to the new archive
     * @param paths
     *            the paths to add to the zip file
     * @throws IOException
     *             if there is an issue writing, adding to or compressing the
     *             archive
     */
    public void create(File baseDirectory, List<String> paths) throws IOException {
        /* First delete the file if it exists */
        if (this.exists()) {
            if (false == this.delete()) {
                System.out.println("[WARN] Failed to delete existing file: " + this.getAbsolutePath());
            }
        }

        /* Generate ZipArchiveOutputStream */
        ZipArchiveOutputStream zip = new ZipArchiveOutputStream(new FileOutputStream(this));
        for (String path : paths) {
            File node = new File(baseDirectory, path);
            if (node.exists()) {
                /*
                 * Add entries for all of the parent folders first.
                 * This is necessary to preserve the lastModified
                 * time of those folders.
                 */
                addParentFolders(zip, node, baseDirectory);
                addRecursive(zip, node, baseDirectory);
            }
        }
        zip.close();
    }

    /**
     * Add the parent directories of <code>node</code>, up to but excluding <code>root</code>
     * as relative paths from <code>root</code> to the specified <code>zip</code>.
     *
     * @param zip
     *          the {@link ZipArchiveOutputStream} to add to
     * @param node
     *          the <code>File</code> element whose parent directories are to be
     *          added to the specified <code>zip</code>
     * @param root
     *          the file to use to generate relative path names
     * @throws IOException
     *          if there was a problem writing to the zip file
     */
    private void addParentFolders(ZipArchiveOutputStream zip, File node, File root)
        throws IOException
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

        /*
         * Add entries for the parent directories, one at a time, from
         * the topmost level on down.
         */
        Pattern regex = Pattern.compile("([^/]+)/");
        Matcher m = regex.matcher(relativePath);
        String dirTree = "";
        while (m.find()) {
            String directory = m.group(1);
            dirTree = dirTree + directory + "/";
            File tempNode = new File(root.getAbsolutePath() + "/" + dirTree);

            /*
             * Create new ZipArchiveEntry and explicitly set its lastModified time.
             */
            ZipArchiveEntry archiveEntry = new ZipArchiveEntry(dirTree);
            archiveEntry.setTime(tempNode.lastModified());
            zip.putArchiveEntry(archiveEntry);
            zip.closeArchiveEntry();
        }
    }

    /**
     * Recursively add the files contained in <code>node</code> as relative
     * paths from <code>root</code> to the specified <code>zip</code>.
     *
     * @param zip
     *            the {@link ZipArchiveOutputStream} to add to
     * @param node
     *            the base of the file system that is to be added to the
     *            specified <code>zip</code>
     * @param root
     *            the file to use to generate relative path names
     * @throws IOException
     *             if there was a problem writing to the zip file
     */
    private void addRecursive(ZipArchiveOutputStream zip, File node, File root)
        throws IOException
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
            /* Do not add an entry for the base directory, this breaks most GUIs */
            if (false == relativePath.isEmpty()) {
                /* In a zipfile, directories must end with a '/' */
                if (!relativePath.endsWith("/")) {
                    relativePath = relativePath + '/';
                }

                /*
                 * Create new ZipArchiveEntry and explicitly set its lastModified time.
                 */
                ZipArchiveEntry archiveEntry = new ZipArchiveEntry(relativePath);
                archiveEntry.setTime(node.lastModified());
                zip.putArchiveEntry(archiveEntry);
                zip.closeArchiveEntry();
            }

            /* If this is a directory, parse over children */
            File[] children = node.listFiles();
            for (File child : children) {
                addRecursive(zip, child, root);
            }
        } else {
            /*
             * This is a file, so find the relative path from the root and then
             * add the element.
             */
            /*
             * Create new ZipArchiveEntry and explicitly set its lastModified time.
             */
            ZipArchiveEntry archiveEntry = new ZipArchiveEntry(relativePath);
            archiveEntry.setTime(node.lastModified());
            zip.putArchiveEntry(archiveEntry);

            /* Now write the data */
            FileInputStream in = new FileInputStream(node);

            byte[] b = new byte[1024];
            int len = -1;
            while (-1 != (len = in.read(b))) {
                zip.write(b, 0, len);
            }

            /* Clean up */
            zip.closeArchiveEntry();
            in.close();
        }
    }
}
