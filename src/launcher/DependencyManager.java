package it.menzani.yiupp.launcher;

import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.storage.ResourceMap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

class DependencyManager {

    // This should be a sandbox, cannot use DataFolder class
    private static final Path DEPENDENCY_FOLDER = Paths.get(System.getProperty("user.home"), "Yiupp", "libs");

    private final Collection<Library> libraries = new HashSet<>();

    DependencyManager() {
        try {
            Files.createDirectories(DEPENDENCY_FOLDER);
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

    void addLibrary(String resourceId) {
        libraries.add(new Library(ResourceMap.lookup(resourceId), Paths.get(resourceId).getFileName().toString()));
    }

    void loadLibraries() {
        try {
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            for (Library library : libraries) {
                library.importFromSource();
                method.invoke(classLoader, library.getImportedFile());
            }
        } catch (Exception ex) {
            throw new UncaughtException(ex);
        }
    }

    private static final class Library {

        private final URL source;
        private final Path file;

        private Library(URL source, String fileName) {
            this.source = source;
            file = DEPENDENCY_FOLDER.resolve(fileName);
        }

        private void importFromSource() {
            if (Files.exists(file)) {
                return;
            }
            try (InputStream stream = source.openStream()) {
                Files.copy(stream, file);
            } catch (IOException ex) {
                throw new UncaughtException(ex);
            }
        }

        private URL getImportedFile() {
            try {
                return file.toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new UncaughtException(ex);
            }
        }

    }

}
