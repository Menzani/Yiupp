package it.menzani.yiupp.storage;

import it.menzani.yiupp.core.Broadcast;

import java.awt.*;

interface Theme {

    /**
     * Returns the associated Look and Feel.
     *
     * @return the constants {@code "default"}, {@code "bright"}, or the fully qualified class name of the L&F
     * implementation
     */
    String getLookAndFeelImpl();

    /**
     * Returns the CSS stylesheet for styling the Welcome page.
     *
     * @return the resource ID of the stylesheet
     */
    String getWelcomeStyleSheet();

    /**
     * Returns the foreground color to apply to messages of the {@link Broadcast.MessageType#WARNING WARNING} type
     * displayed by the Broadcast API.
     */
    Color getBroadcastWarningColor();

    /**
     * Returns the foreground color to apply to messages of the {@link Broadcast.MessageType#ERROR ERROR} type displayed
     * by the Broadcast API.
     */
    Color getBroadcastErrorColor();

}
