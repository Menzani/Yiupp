package it.menzani.yiupp.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class SplashScreen {

    private final java.awt.SplashScreen awtSplashScreen;
    private Graphics2D pen;
    private double y;
    private double total;

    public SplashScreen() {
        awtSplashScreen = java.awt.SplashScreen.getSplashScreen();
        if (awtSplashScreen == null) {
            return;
        }
        pen = awtSplashScreen.createGraphics();
        RectangularShape bounds = awtSplashScreen.getBounds();
        y = bounds.getHeight() - 15D;
        total = bounds.getWidth();
    }

    public synchronized void setProgress(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be a value between 0 and 100 inclusive.");
        }
        if (awtSplashScreen == null) {
            return;
        }
        double factor = percent / 100D;
        pen.fill(new Rectangle2D.Double(0D, y, factor * total, 1D));
        awtSplashScreen.update();
    }

}
