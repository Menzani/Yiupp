package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.DataFolder;

import javax.swing.*;
import java.awt.*;

public class Workbench {

    private final JSplitPane display = new JSplitPane();
    private final ControlPane controlPane = new ControlPane();
    private Browser browser;

    public Workbench() {
        display.setContinuousLayout(true);
        display.setOneTouchExpandable(false);
        display.setLeftComponent(controlPane.getDisplay());
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        display.setRightComponent(browser.getDisplay());
        Yiupp.getInstance().getWindow().runLater(new PostInitHandler());
        this.browser = browser;
    }

    public ControlPane getControlPane() {
        return controlPane;
    }

    public int getSeparatorLocation() {
        return display.getDividerLocation();
    }

    public Component getDisplay() {
        return display;
    }

    private final class PostInitHandler implements Runnable {

        @Override
        public void run() {
            display.setDividerLocation(DataFolder.getInstance().getLayout().getWorkbenchSeparator());
        }

    }

}
