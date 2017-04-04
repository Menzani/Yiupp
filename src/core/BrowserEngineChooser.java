package it.menzani.yiupp.core;

import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;

import java.awt.*;

class BrowserEngineChooser {

    private final Chooser chooser = new Chooser("Motore di rendering:");

    BrowserEngineChooser() {
        final Preferences preferences = DataFolder.getInstance().getPreferences();
        Preferences.BrowserEngine browserEngine = preferences.getBrowserEngine();

        Chooser.Option defaultOption = new Chooser.Option(browserEngine == Preferences.BrowserEngine.DEFAULT, true);
        Chooser.Option safariOption = new Chooser.Option(browserEngine == Preferences.BrowserEngine.SAFARI);

        defaultOption.setName("DJNativeSwing")
                .setPreview("Fornisce l'accesso a Internet Explorer su Windows, e a Mozilla Firefox sugli altri " +
                        "sistemi operativi. Sviluppato da terze parti.")
                .setCommand(new Runnable() {
                    @Override
                    public void run() {
                        preferences.setBrowserEngine(Preferences.BrowserEngine.DEFAULT);
                    }
                })
                .setRestartRequired();
        safariOption.setName("JavaFX")
                .setPreview("Lo stesso usato da Safari. Leggero. Per i fan di Apple.")
                .setCommand(new Runnable() {
                    @Override
                    public void run() {
                        preferences.setBrowserEngine(Preferences.BrowserEngine.SAFARI);
                    }
                })
                .setRestartRequired();

        chooser.addOption(defaultOption);
        chooser.addOption(safariOption);
    }

    Component getDisplay() {
        return chooser.getDisplay();
    }

}
