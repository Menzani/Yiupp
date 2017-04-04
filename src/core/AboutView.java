package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.TemplateVariable;
import it.menzani.yiupp.util.TextManager;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class AboutView {

    private final JComponent display = new JPanel(new BorderLayout());
    private final JLabel info;

    AboutView() {
        display.setPreferredSize(new Dimension(0, 160));
        display.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        display.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Informazioni"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        info = new JLabel(getInfoText());
        info.setVerticalAlignment(SwingConstants.TOP);
        display.add(info);
    }

    private static String getInfoText() {
        String text = ResourceMap.lookupString(ResourceMap.INFO_TEXT);
        text = TextManager.fillTemplate(text, Yiupp.DISPLAY_NAME, Yiupp.DESCRIPTION, Yiupp.BUILD_NUMBER,
                new TemplateVariable("year", Calendar.getInstance().get(Calendar.YEAR)));
        return text;
    }

    public JLabel getInfo() {
        return info;
    }

    Component getDisplay() {
        return display;
    }

}
