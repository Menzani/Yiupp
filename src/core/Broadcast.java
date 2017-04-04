package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Broadcast {

    private static final Pattern PARAGRAPH_END_TAG = Pattern.compile("</p>", Pattern.LITERAL);
    private static final Pattern ANY_TAG = Pattern.compile("<[^>]*>");
    private static final Pattern TWO_OR_MORE_SPACES = Pattern.compile("[ ]{2,}");

    private final JComponent display = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    private final JLabel message = new JLabel("Pronto");
    private final JProgressBar loadingIcon = new JProgressBar();

    public Broadcast() {
        display.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadingIcon.setIndeterminate(true);
        loadingIcon.setVisible(false);

        display.add(Box.createRigidArea(new Dimension(10, 0)));
        display.add(message);
        display.add(Box.createRigidArea(new Dimension(20, 23)));
        display.add(loadingIcon);
    }

    public static void setMessage(CharSequence text) {
        setMessage(text, MessageType.PLAIN_INFO);
    }

    /**
     * Displays a message to the user in the status bar.
     * <p>
     * This method is safe for use by any thread.
     */
    public static synchronized void setMessage(final CharSequence text, final MessageType type) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Broadcast broadcast = Yiupp.getInstance().getWindow().getWindowContent().getBroadcast();
                broadcast.message.setForeground(type.color);
                CharSequence temp = text;
                if (type != MessageType.PLAIN_INFO) {
                    // Strip redundant spaces
                    temp = TWO_OR_MORE_SPACES.matcher(temp).replaceAll("");
                    // Save spaces between paragraphs
                    temp = PARAGRAPH_END_TAG.matcher(temp).replaceAll(Matcher.quoteReplacement(" ")).trim();
                    // Strip HTML to disable multiline text
                    temp = ANY_TAG.matcher(temp).replaceAll("");
                }
                broadcast.message.setText(temp.toString());
                broadcast.loadingIcon.setVisible(type == MessageType.LOADING);
            }
        });
    }

    public Component getDisplay() {
        return display;
    }

    public enum MessageType {

        PLAIN_INFO(null),
        INFO(null),
        LOADING(null),
        WARNING(ConstHolder.THEME.getBroadcastWarningColor()),
        ERROR(ConstHolder.THEME.getBroadcastErrorColor());

        private final Color color;

        MessageType(Color color) {
            this.color = color;
        }

        private static final class ConstHolder {

            private static final Preferences.ThemeProvider THEME = DataFolder.getInstance().getPreferences().getTheme();

        }

    }
}
