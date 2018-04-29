package it.menzani.yiupp;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import it.menzani.yiupp.core.Workbench;
import it.menzani.yiupp.launcher.Launcher;
import it.menzani.yiupp.launcher.MacLauncher;
import it.menzani.yiupp.launcher.RegularLauncher;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;
import it.menzani.yiupp.util.*;
import it.menzani.yiupp.util.System;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;

public final class Yiupp {

    // When updated, use Find in Path to search for strings and names in which the value is hardcoded
    public static final TemplateVariable DISPLAY_NAME = new TemplateVariable("name", "Yiupp");
    public static final TemplateVariable DESCRIPTION =
            new TemplateVariable("description", "Advanced Security Interface");

    // When updated, change these too: launch4jConfig.xml, Project Structure > Artifacts > Yiupp > Output Layout
    public static final TemplateVariable BUILD_NUMBER = new TemplateVariable("build", "1.6.1");

    private static Yiupp instance;
    private Window window;

    private Yiupp() {

    }

    public static Yiupp getInstance() {
        return instance;
    }

    public static Workbench getWorkbench() {
        return getInstance().getWindow().getWindowContent().getWorkbench();
    }

    public static void main(String[] args) throws Exception {
        // Uncomment on build
        // Thread.setDefaultUncaughtExceptionHandler(new YiuppUncaughtExceptionHandler());

        final SplashScreen splashScreen = new SplashScreen();

        System system = Systems.getSystem();
        Launcher launcher;
        if (system.getFamily() == SystemFamily.MAC) {
            launcher = new MacLauncher();
        } else {
            launcher = new RegularLauncher();
        }
        boolean success = launcher.beforeLaunch();
        splashScreen.setProgress(10);

        if (!success) {
            return;
        }
        launcher.launch();
        splashScreen.setProgress(20);

        instance = new Yiupp();

        Preferences preferences = DataFolder.getInstance().getPreferences();
        boolean defaultBrowser = preferences.getBrowserEngine() == Preferences.BrowserEngine.DEFAULT;
        if (defaultBrowser) {
            NativeInterface.open();
        } else {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    new JFXPanel();
                }
            });
        }
        splashScreen.setProgress(50);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                instance.createWindow(splashScreen);
            }
        });
        launcher.update();

        if (defaultBrowser) {
            NativeInterface.runEventPump();
        }
    }

    public Window getWindow() {
        return window;
    }

    private void createWindow(SplashScreen splashScreen) {
        window = new Window();

        Preferences preferences = DataFolder.getInstance().getPreferences();
        String lookAndFeelImpl = preferences.getTheme().getLookAndFeelImpl();
        switch (lookAndFeelImpl) {
            case "default":
                window.useSystemLookAndFeel();
                break;
            case "bright":
                window.useCrossPlatformLookAndFeel();
                break;
            default:
                window.setCustomLookAndFeel(lookAndFeelImpl);
        }
        splashScreen.setProgress(65);

        window.create();
        splashScreen.setProgress(100);

        window.display();
    }

}
