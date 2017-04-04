package it.menzani.yiupp.core;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class AttackView {

    private final JPanel display = new JPanel();
    private final Site site = new Site();
    private final FileManager fileManager = new FileManager();
    private final Editor editor = new Editor();
    private final Navigator navigator = new Navigator();

    AttackView() {
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
        display.add(site.getDisplay());
        display.add(fileManager.getDisplay());
        display.add(editor.getDisplay());
        display.add(navigator.getDisplay());
    }

    public Site getSite() {
        return site;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Editor getEditor() {
        return editor;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    Component getDisplay() {
        return display;
    }

}
