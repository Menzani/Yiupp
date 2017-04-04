package it.menzani.yiupp.core;

import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Preferences;
import it.menzani.yiupp.storage.ResourceMap;

import java.awt.*;

class ThemeChooser {

    private final Chooser chooser = new Chooser("Tema:");

    ThemeChooser() {
        final Preferences preferences = DataFolder.getInstance().getPreferences();
        Preferences.ThemeProvider theme = preferences.getTheme();

        Chooser.Option brightOption = new Chooser.Option(theme == Preferences.ThemeProvider.BRIGHT);
        Chooser.Option darkOption = new Chooser.Option(theme == Preferences.ThemeProvider.DARK);
        Chooser.Option defaultOption = new Chooser.Option(theme == Preferences.ThemeProvider.DEFAULT, true);

        brightOption.setName("Chiaro")
                .setPreview(ResourceMap.lookupImage(ResourceMap.BRIGHT_THEME_PREVIEW))
                .setCommand(new Runnable() {
                    @Override
                    public void run() {
                        preferences.setTheme(Preferences.ThemeProvider.BRIGHT);
                    }
                })
                .setRestartRequired();
        darkOption.setName("Scuro")
                .setPreview(ResourceMap.lookupImage(ResourceMap.DARK_THEME_PREVIEW))
                .setCommand(new Runnable() {
                    @Override
                    public void run() {
                        preferences.setTheme(Preferences.ThemeProvider.DARK);
                    }
                })
                .setRestartRequired();
        defaultOption.setName("Predefinito")
                .setPreview(ResourceMap.lookupImage(ResourceMap.DEFAULT_THEME_PREVIEW))
                .setCommand(new Runnable() {
                    @Override
                    public void run() {
                        preferences.setTheme(Preferences.ThemeProvider.DEFAULT);
                    }
                })
                .setRestartRequired();

        chooser.addOption(brightOption);
        chooser.addOption(darkOption);
        chooser.addOption(defaultOption);
    }

    Component getDisplay() {
        return chooser.getDisplay();
    }

}
