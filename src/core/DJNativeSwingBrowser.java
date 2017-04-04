package it.menzani.yiupp.core;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import it.menzani.yiupp.Yiupp;

import java.awt.*;

public class DJNativeSwingBrowser extends Browser {

    private final JWebBrowser display = new JWebBrowser();

    public DJNativeSwingBrowser() {
        display.setPreferredSize(new Dimension(800, 0));
        display.setBarsVisible(false);
        display.setDefaultPopupMenuRegistered(false);

        loadWelcomePage();
        installHackListener(display);
    }

    @Override
    public void installListener(BrowserListener listener) {
        super.installListener(null);
        display.addWebBrowserListener(new WebBrowserHandler(listener));
    }

    @Override
    public void loadPage(String pageUrl) {
        super.loadPage(null);
        display.navigate(pageUrl);
    }

    @Override
    Component getDisplay() {
        return display;
    }

    @Override
    protected void loadWelcomePage() {
        display.setHTMLContent(getWelcomePage());
        display.registerFunction(new RunCommandFunction());
    }

    private static final class WebBrowserHandler extends WebBrowserAdapter {

        private final BrowserListener listener;

        private WebBrowserHandler(BrowserListener listener) {
            this.listener = listener;
        }

        @Override
        public void locationChanged(WebBrowserNavigationEvent event) {
            if (!listener.canLoadAgain()) {
                return;
            }
            listener.run();
        }

    }

    private static final class RunCommandFunction extends WebBrowserFunction {

        public RunCommandFunction() {
            super("runCommand");
        }

        @Override
        public Object invoke(JWebBrowser browser, Object... objects) {
            if (objects.length == 0) {
                return null;
            }
            Yiupp.getWorkbench().getControlPane().getAttackView().getFileManager().runCommand(objects[0].toString());
            return null;
        }

    }

}
