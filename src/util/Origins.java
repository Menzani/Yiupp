package it.menzani.yiupp.util;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.reporter.UncaughtException;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Origins {

    private static final Origin origin;

    static {
        try {
            Path jarFile = Paths.get(Yiupp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            origin = new Origin(jarFile);
        } catch (URISyntaxException ex) {
            throw new UncaughtException(ex);
        }
    }

    private Origins() {

    }

    public static Origin getOrigin() {
        return origin;
    }

}
