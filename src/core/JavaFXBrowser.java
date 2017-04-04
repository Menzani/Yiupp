package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.awt.*;

public class JavaFXBrowser extends Browser {

    private final JFXPanel display = new JFXPanel();
    private WebEngine engine;
    private volatile BrowserListener listener;

    public JavaFXBrowser() {
        display.setPreferredSize(new Dimension(800, 0));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView();
                webView.setContextMenuEnabled(false);

                engine = webView.getEngine();
                engine.getLoadWorker().stateProperty().addListener(new ChangeHandler());

                AnchorPane anchorPane = new AnchorPane();
                AnchorPane.setTopAnchor(webView, 0D);
                AnchorPane.setBottomAnchor(webView, 0D);
                AnchorPane.setLeftAnchor(webView, 0D);
                AnchorPane.setRightAnchor(webView, 0D);
                anchorPane.getChildren().add(webView);
                Scene scene = new Scene(anchorPane);
                display.setScene(scene);
            }
        });

        loadWelcomePage();
        installHackListener(display);
    }

    @Override
    public void installListener(BrowserListener listener) {
        super.installListener(null);
        this.listener = listener;
    }

    @Override
    public void loadPage(final String pageUrl) {
        super.loadPage(null);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.load(pageUrl);
            }
        });
    }

    @Override
    Component getDisplay() {
        return display;
    }

    @Override
    protected void loadWelcomePage() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.loadContent(getWelcomePage());
            }
        });
    }

    private final class ChangeHandler implements ChangeListener<Worker.State> {

        @Override
        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
                            Worker.State newValue) {
            if (newValue != Worker.State.SUCCEEDED) {
                return;
            }
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("commandRunner", new CommandRunner());
            if (listener == null) {
                return;
            }
            if (!listener.canLoadAgain()) {
                return;
            }
            listener.run();
        }

        public final class CommandRunner {

            public void runCommand(String commandId) {
                Yiupp.getWorkbench().getControlPane().getAttackView().getFileManager().runCommand(commandId);
            }

        }

    }

}
