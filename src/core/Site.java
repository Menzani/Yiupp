package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.SiteHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Site {

    private final SiteHistory history = DataFolder.getInstance().getSiteHistory();
    private final JComboBox<String> display = new JComboBox<>();

    Site() {
        display.setPreferredSize(new Dimension(0, 20));
        display.setEditable(true);
        refresh();
        startTyping();
        display.getEditor().getEditorComponent().addFocusListener(new FocusHandler());
    }

    public void startTyping() {
        ComboBoxEditor editor = display.getEditor();
        editor.setItem(null);
        editor.getEditorComponent().requestFocusInWindow();
        if (display.isShowing() && !history.isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    display.setPopupVisible(true);
                }
            });
        }
    }

    public String getUrl() {
        String url = display.getEditor().getItem().toString();
        if (url.isEmpty()) {
            return null;
        }
        history.select(url);
        refresh();
        return url;
    }

    Component getDisplay() {
        return display;
    }

    private void refresh() {
        display.setModel(new DefaultComboBoxModel<>(history.getHistory()));
        display.setMinimumSize(new Dimension());
    }

    private static final class FocusHandler extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent event) {
            Editor editor = Yiupp.getWorkbench().getControlPane().getAttackView().getEditor();
            editor.exit();
        }

    }

}
