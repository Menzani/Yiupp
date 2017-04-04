package it.menzani.yiupp.util;

import java.util.EnumSet;
import java.util.Set;

public final class Desktops {

    private static Desktop desktop;

    static {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop awtDesktop = java.awt.Desktop.getDesktop();
            Set<java.awt.Desktop.Action> supportedActions = EnumSet.noneOf(java.awt.Desktop.Action.class);
            if (awtDesktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                supportedActions.add(java.awt.Desktop.Action.OPEN);
            }
            if (awtDesktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                supportedActions.add(java.awt.Desktop.Action.BROWSE);
            }
            if (awtDesktop.isSupported(java.awt.Desktop.Action.EDIT)) {
                supportedActions.add(java.awt.Desktop.Action.EDIT);
            }
            if (awtDesktop.isSupported(java.awt.Desktop.Action.MAIL)) {
                supportedActions.add(java.awt.Desktop.Action.MAIL);
            }
            if (awtDesktop.isSupported(java.awt.Desktop.Action.PRINT)) {
                supportedActions.add(java.awt.Desktop.Action.PRINT);
            }
            desktop = new Desktop(awtDesktop, supportedActions);
        }
    }

    private Desktops() {

    }

    public static Desktop getDesktop() {
        return desktop;
    }

}
