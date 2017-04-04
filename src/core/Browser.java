package it.menzani.yiupp.core;

import it.menzani.yiupp.Window;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.TemplateVariable;
import it.menzani.yiupp.util.TextManager;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class Browser {

    private boolean listenerInstalled;
    private boolean hacked, navigated;

    protected static String getWelcomePage() {
        String text = ResourceMap.lookupString(ResourceMap.WELCOME_TEXT);
        Preferences preferences = DataFolder.getInstance().getPreferences();
        TemplateVariable style =
                new TemplateVariable("style", ResourceMap.lookupString(preferences.getTheme().getWelcomeStyleSheet()));

        text = TextManager.fillTemplate(text, Yiupp.DISPLAY_NAME, Yiupp.DESCRIPTION, style);
        return text;
    }

    public boolean isListenerInstalled() {
        return listenerInstalled;
    }

    public boolean hasNavigated() {
        return navigated;
    }

    public void installListener(BrowserListener listener) {
        listenerInstalled = true;
    }

    public void loadPage(String pageUrl) {
        navigated = true;
    }

    abstract Component getDisplay();

    protected void installHackListener(JComponent display) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_END,
                InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        Window.addShortcutHandler(display, keyStroke, new HackHandler());
    }

    protected abstract void loadWelcomePage();

    public interface BrowserListener extends Runnable {

        boolean canLoadAgain();

    }

    private final class HackHandler extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (hacked) {
                return;
            }
            hacked = true;
            if (hasNavigated()) {
                Broadcast.setMessage("Un bravo hacker non perde mai tempo", Broadcast.MessageType.ERROR);
                return;
            }
            try {
                Field valueField = TemplateVariable.class.getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(Yiupp.DESCRIPTION, "Advanced <font style=\"color: red;\">Hacker</font> Interface");

                loadWelcomePage();
                Field infoField = AboutView.class.getDeclaredField("info");
                infoField.setAccessible(true);
                AboutView aboutView = Yiupp.getWorkbench().getControlPane().getConfigView().getAboutView();
                JLabel label = (JLabel) infoField.get(aboutView);
                Method getInfoTextMethod = AboutView.class.getDeclaredMethod("getInfoText");
                getInfoTextMethod.setAccessible(true);
                String text = getInfoTextMethod.invoke(null).toString();
                label.setText(text);

                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(ResourceMap.lookup(ResourceMap.HACKER_SOUND)));
                clip.start();
            } catch (Exception ex) {
                throw new UncaughtException(ex);
            }
        }

    }

}
