package it.menzani.yiupp.launcher;

import com.google.gson.Gson;
import it.menzani.yiupp.Window;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.core.Broadcast;
import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.*;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RegularLauncher implements Launcher {

    @Override
    public boolean beforeLaunch() {
        return true;
    }

    @Override
    public void launch() {
        DependencyManager dependencyManager = new DependencyManager();
        dependencyManager.addLibrary(ResourceMap.J_TATTOO_LIB);
        dependencyManager.addLibrary(ResourceMap.getSWTImpl(Systems.getSystem()));
        dependencyManager.addLibrary(ResourceMap.DJ_NATIVE_SWING_LIB);
        dependencyManager.addLibrary(ResourceMap.DJ_NATIVE_SWING_SWT_LIB);
        dependencyManager.addLibrary(ResourceMap.GSON_LIB);
        dependencyManager.loadLibraries();
    }

    @Override
    public void update() {
        Thread thread = new Thread(new UpdateHandler());
        thread.setDaemon(true);
        thread.start();
    }

    private static final class UpdateHandler implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                throw new UncaughtException(ex);
            }

            try {
                URL remote = new URL("https://www.menzani.eu/yiupp/downloads/");

                URL remoteInformation = new URL(remote, "versions.json");
                VersionInformation information;
                try (Reader reader = new InputStreamReader(remoteInformation.openStream())) {
                    information = new Gson().fromJson(reader, VersionInformation.class);
                } catch (IOException ex) {
                    return;
                }

                String actualBuildNumber = Yiupp.BUILD_NUMBER.expand();
                VersionInformation.Version current = information.getCurrent();
                String currentBuildNumber = current.getBuildNumber();
                if (actualBuildNumber.equals(currentBuildNumber)) {
                    return;
                }

                Window window = Yiupp.getInstance().getWindow();
                String updateText = ResourceMap.lookupString(ResourceMap.UPDATER_TEXT);
                updateText = TextManager.fillTemplate(updateText, new TemplateVariable("actual", actualBuildNumber),
                        new TemplateVariable("current", currentBuildNumber),
                        new TemplateVariable("description", current.getDescription()));
                int choiceToUpdate = window.askUserConcurrently(updateText);
                if (choiceToUpdate != JOptionPane.YES_OPTION) {
                    return;
                }

                Origin origin = Origins.getOrigin();
                if (!origin.isRecognized()) {
                    Broadcast.setMessage("Impossibile aggiornare", Broadcast.MessageType.ERROR);
                    return;
                }

                String name = "Yiupp-" + currentBuildNumber;
                name += origin.isJarFile() ? ".jar" : ".exe";
                Path localPackage = origin.getFile().getParent().resolve(name);

                if (Files.exists(localPackage)) {
                    String overwriteText = "<html>Il seguente file sarà sovrascritto. Continuare?<br>{FILE}</html>";
                    overwriteText = TextManager.fillTemplate(overwriteText, new TemplateVariable("file", localPackage));
                    int choiceToOverwrite = window.askUserConcurrently(overwriteText);
                    if (choiceToOverwrite != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                Broadcast.setMessage("Download aggiornamento...", Broadcast.MessageType.LOADING);
                URL remotePackage = new URL(remote, name);
                try (InputStream stream = remotePackage.openStream()) {
                    Files.copy(stream, localPackage, StandardCopyOption.REPLACE_EXISTING);
                }

                int choiceToRestart = window.askUserConcurrently("Aggiornamento scaricato. Vuoi usarlo ora?");
                if (choiceToRestart != JOptionPane.YES_OPTION) {
                    return;
                }

                window.beforeClose();
                Desktop desktop = Desktops.getDesktop();
                desktop.openIfPossible(localPackage);
                window.close();
            } catch (IOException ex) {
                throw new UncaughtException(ex);
            }
        }

    }

}
