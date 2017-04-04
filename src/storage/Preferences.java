package it.menzani.yiupp.storage;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;

import java.awt.*;

public class Preferences {

    private ThemeProvider theme = ThemeProvider.DEFAULT;
    private BrowserEngine browserEngine = BrowserEngine.DEFAULT;

    public ThemeProvider getTheme() {
        return theme;
    }

    public void setTheme(ThemeProvider theme) {
        this.theme = theme;
    }

    public BrowserEngine getBrowserEngine() {
        return browserEngine;
    }

    public void setBrowserEngine(BrowserEngine browserEngine) {
        this.browserEngine = browserEngine;
    }

    public enum ThemeProvider implements Theme {
        DEFAULT {
            @Override
            public String getLookAndFeelImpl() {
                return "default";
            }

            @Override
            public String getWelcomeStyleSheet() {
                return ResourceMap.WELCOME_STYLE;
            }

            @Override
            public Color getBroadcastWarningColor() {
                return new Color(255, 97, 3);
            }

            @Override
            public Color getBroadcastErrorColor() {
                return new Color(255, 48, 48);
            }
        },
        BRIGHT {
            @Override
            public String getLookAndFeelImpl() {
                return "bright";
            }

            @Override
            public String getWelcomeStyleSheet() {
                return ResourceMap.WELCOME_STYLE;
            }

            @Override
            public Color getBroadcastWarningColor() {
                return new Color(255, 165, 0);
            }

            @Override
            public Color getBroadcastErrorColor() {
                return new Color(255, 85, 85);
            }
        },
        DARK {
            @Override
            public String getLookAndFeelImpl() {
                return HiFiLookAndFeel.class.getName();
            }

            @Override
            public String getWelcomeStyleSheet() {
                return ResourceMap.WELCOME_DARK_STYLE;
            }

            @Override
            public Color getBroadcastWarningColor() {
                return new Color(255, 140, 0);
            }

            @Override
            public Color getBroadcastErrorColor() {
                return new Color(255, 64, 64);
            }
        }
    }

    public enum BrowserEngine {
        DEFAULT,
        SAFARI
    }

}
