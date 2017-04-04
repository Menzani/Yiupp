package it.menzani.yiupp.storage;

import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.util.System;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public final class ResourceMap {

    // Folders
    // These fields are unnecessarily public to prevent rearrangement by IDE
    public static final String LIBRARY_FOLDER = "libraries/";
    public static final String DJ_NATIVE_SWING_FOLDER = LIBRARY_FOLDER + "DJNativeSwing/";
    public static final String SWT_FOLDER = LIBRARY_FOLDER + "SWT/";
    public static final String RESOURCE_FOLDER = "resources/";
    public static final String ICON_FOLDER = RESOURCE_FOLDER + "icons/";
    public static final String PREVIEW_FOLDER = RESOURCE_FOLDER + "previews/";
    public static final String TEXT_FOLDER = RESOURCE_FOLDER + "text/";
    public static final String STYLE_FOLDER = RESOURCE_FOLDER + "styles/";
    public static final String SOUND_FOLDER = RESOURCE_FOLDER + "sounds/";

    // Libraries
    public static final String NULLITY_ANNOTATIONS_LIB = LIBRARY_FOLDER + "Nullity annotations/annotations.jar";
    public static final String CONCURRENCY_ANNOTATIONS_LIB =
            LIBRARY_FOLDER + "Concurrency annotations/jcip-annotations.jar";
    public static final String J_TATTOO_LIB = LIBRARY_FOLDER + "JTattoo/JTattoo-1.6.11.jar";
    public static final String GSON_LIB = LIBRARY_FOLDER + "Gson/gson-2.6.2.jar";
    public static final String DJ_NATIVE_SWING_LIB = DJ_NATIVE_SWING_FOLDER + "DJNativeSwing.jar";
    public static final String DJ_NATIVE_SWING_SWT_LIB = DJ_NATIVE_SWING_FOLDER + "DJNativeSwing-SWT.jar";

    // Icons
    public static final String WINDOW_ICON = ICON_FOLDER + "window.png";
    public static final String ADD_ICON = ICON_FOLDER + "add.png";
    public static final String REMOVE_ICON = ICON_FOLDER + "remove.png";
    public static final String PREVIOUS_ICON = ICON_FOLDER + "previous.png";
    public static final String NEXT_ICON = ICON_FOLDER + "next.png";
    public static final String PLAY_ICON = ICON_FOLDER + "play.png";
    public static final String STOP_ICON = ICON_FOLDER + "stop.png";
    public static final String PAUSE_ICON = ICON_FOLDER + "pause.png";
    public static final String RESUME_ICON = ICON_FOLDER + "resume.png";

    // Previews
    public static final String DEFAULT_THEME_PREVIEW = PREVIEW_FOLDER + "defaultTheme.png";
    public static final String BRIGHT_THEME_PREVIEW = PREVIEW_FOLDER + "brightTheme.png";
    public static final String DARK_THEME_PREVIEW = PREVIEW_FOLDER + "darkTheme.png";

    // Text
    public static final String LAUNCHER_ERROR_TEXT = TEXT_FOLDER + "launcherError.txt";
    public static final String INFO_TEXT = TEXT_FOLDER + "info.html";
    public static final String WELCOME_TEXT = TEXT_FOLDER + "welcome.html";
    public static final String UPDATER_TEXT = TEXT_FOLDER + "updater.html";
    public static final String CRASH_TEXT = TEXT_FOLDER + "crash.txt";

    // Styles
    public static final String WELCOME_STYLE = STYLE_FOLDER + "welcome.css";
    public static final String WELCOME_DARK_STYLE = STYLE_FOLDER + "darkWelcome.css";

    // Sounds
    public static final String HACKER_SOUND = SOUND_FOLDER + "hacker.wav";

    private ResourceMap() {

    }

    public static String getSWTImpl(System system) {
        return SWT_FOLDER + "swt_" + system + ".jar";
    }

    public static String lookupString(String resourceId) {
        try (Scanner scanner = new Scanner(lookup(resourceId).openStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

    public static BufferedImage lookupImage(String resourceId) {
        try {
            return ImageIO.read(lookup(resourceId));
        } catch (IOException ex) {
            throw new UncaughtException(ex);
        }
    }

    public static URL lookup(String resourceId) {
        return ClassLoader.getSystemResource(resourceId);
    }

}
