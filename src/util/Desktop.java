package it.menzani.yiupp.util;

import it.menzani.yiupp.Window;
import it.menzani.yiupp.reporter.UncaughtException;

import java.io.File;
import java.io.IOException;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class Desktop {

    private final java.awt.Desktop awtDesktop;
    private final Collection<java.awt.Desktop.Action> supportedActions;

    Desktop(java.awt.Desktop awtDesktop, Collection<java.awt.Desktop.Action> supportedActions) {
        this.awtDesktop = awtDesktop;
        this.supportedActions = supportedActions;
    }

    /**
     * Sends a not-very-nice <b>error</b> message to the user through the default text editor.
     * <p>
     * This method could be useful when the GUI is inaccessible. Note that APIs in the {@link Window} class should be
     * preferred.
     */
    public void sendMessage(String text) {
        System.err.println(text);
        try {
            Path tempFile = Files.createTempFile(null, ".txt");
            Files.write(tempFile, text.getBytes());
            openIfPossible(tempFile);
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

    public void openIfPossible(Path path) {
        openIfPossible(path.toFile());
    }

    public void openIfPossible(File file) {
        String name = file.getName();
        if (name.endsWith(".exe")) {
            try {
                ProcessBuilder process = new ProcessBuilder("cmd.exe", "/c", "start", name);
                process.directory(file.getParentFile());
                process.start();
                return;
            } catch (IOException ex) {
                throw new UncaughtException(ex);
            }
        }
        if (!supportedActions.contains(java.awt.Desktop.Action.OPEN)) {
            return;
        }
        try {
            awtDesktop.open(file);
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

}
