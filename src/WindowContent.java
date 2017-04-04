package it.menzani.yiupp;

import it.menzani.yiupp.core.Broadcast;
import it.menzani.yiupp.core.DJNativeSwingBrowser;
import it.menzani.yiupp.core.JavaFXBrowser;
import it.menzani.yiupp.core.Workbench;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;

import javax.swing.*;
import java.awt.*;

public class WindowContent {

    private final JPanel display = new JPanel();
    private final Workbench workbench = new Workbench();
    private final Broadcast broadcast = new Broadcast();

    WindowContent() {
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
        Preferences preferences = DataFolder.getInstance().getPreferences();
        switch (preferences.getBrowserEngine()) {
            case DEFAULT:
                workbench.setBrowser(new DJNativeSwingBrowser());
                break;
            case SAFARI:
                workbench.setBrowser(new JavaFXBrowser());
                break;
        }
        display.add(workbench.getDisplay());
        display.add(broadcast.getDisplay());
    }

    public Workbench getWorkbench() {
        return workbench;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    Container getDisplay() {
        return display;
    }

}
