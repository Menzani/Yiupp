package it.menzani.yiupp.util;

import java.nio.file.Path;

public final class Origin {

    private final Path file;
    private final String path;

    Origin(Path file) {
        this.file = file;
        path = file.toString().toLowerCase();
    }

    public boolean isRecognized() {
        return isJarFile() || isExeFile();
    }

    public boolean isJarFile() {
        return path.endsWith(".jar");
    }

    public boolean isExeFile() {
        return path.endsWith(".exe");
    }

    public Path getFile() {
        return file;
    }

}
