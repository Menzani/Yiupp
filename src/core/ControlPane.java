package it.menzani.yiupp.core;

import java.awt.Component;
import javax.swing.JTabbedPane;

public class ControlPane {

    private final JTabbedPane display = new JTabbedPane();
    private final AttackView attackView = new AttackView();
    private final ConfigView configView = new ConfigView();

    ControlPane() {
        display.setFocusable(false);

        display.addTab("Attacco", attackView.getDisplay());
        display.addTab("Opzioni", configView.getDisplay());
    }

    public AttackView getAttackView() {
        return attackView;
    }

    public ConfigView getConfigView() {
        return configView;
    }

    Component getDisplay() {
        return display;
    }

}
