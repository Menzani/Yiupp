package it.menzani.yiupp.launcher;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.*;

import java.lang.management.ManagementFactory;
import java.util.Collection;

public class MacLauncher extends RegularLauncher {

    @Override
    public boolean beforeLaunch() {
        Collection<String> parameters = ManagementFactory.getRuntimeMXBean().getInputArguments();
        if (parameters.contains("-XstartOnFirstThread")) {
            return true;
        }

        String message = ResourceMap.lookupString(ResourceMap.LAUNCHER_ERROR_TEXT);
        Origin origin = Origins.getOrigin();
        String defaultJarFile = "Yiupp-{BUILD}.jar";
        defaultJarFile = TextManager.fillTemplate(defaultJarFile, Yiupp.BUILD_NUMBER);
        String jarFile = origin.isJarFile() ? origin.getFile().toString() : defaultJarFile;
        message = TextManager.fillTemplate(message, Yiupp.DISPLAY_NAME, new TemplateVariable("jarfile", jarFile));
        Desktop desktop = Desktops.getDesktop();
        desktop.sendMessage(message);
        return false;
    }

}
