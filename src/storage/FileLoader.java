package it.menzani.yiupp.storage;

import it.menzani.yiupp.reporter.UncaughtException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

public class FileLoader {

    public static final FileFilter YIUPP_LIST_FILTER = new YiuppListFilter();
    private final File folder;
    private Collection<String> files;

    public FileLoader(File folder) {
        folder.mkdirs();
        this.folder = folder;
    }

    public void refresh() {
        files = new TreeSet<>();
        File[] files = folder.listFiles(YIUPP_LIST_FILTER);
        if (files == null) {
            throw new UncaughtException(
                    new IOException("Could not retrieve folder content: " + folder.getAbsolutePath()));
        }
        for (File file : files) {
            this.files.add(file.getName());
        }
    }

    public String[] getFiles() {
        return files.toArray(new String[files.size()]);
    }

    public File getFile(String fileName) {
        return new File(folder, fileName);
    }

    public File getFolder() {
        return folder;
    }

    private static final class YiuppListFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return false;
            }
            String name = pathname.getName().toLowerCase();
            return name.endsWith(".alf");
        }

    }

}
