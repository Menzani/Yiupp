package it.menzani.yiupp.core;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class ConfigView {

    private final JPanel display = new JPanel();
    private final BrowserEngineChooser browserEngineChooser = new BrowserEngineChooser();
    private final ThemeChooser themeChooser = new ThemeChooser();
    private final AboutView aboutView = new AboutView();

    ConfigView() {
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));

        display.add(browserEngineChooser.getDisplay());
        display.add(createLine());
        display.add(themeChooser.getDisplay());
        display.add(aboutView.getDisplay());
    }

    private static Component createLine() {
        Component line = new JSeparator();
        line.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));
        return line;
    }

    public BrowserEngineChooser getBrowserEngineChooser() {
        return browserEngineChooser;
    }

    public ThemeChooser getThemeChooser() {
        return themeChooser;
    }

    public AboutView getAboutView() {
        return aboutView;
    }

    Component getDisplay() {
        return display;
    }

}
