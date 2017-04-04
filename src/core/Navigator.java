package it.menzani.yiupp.core;

import it.menzani.yiupp.Window;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Navigator {

    private static final String COMMAND_PREVIOUS = "0";
    private static final String COMMAND_PLAY = "1";
    private static final String COMMAND_NEXT = "2";
    private static final String COMMAND_STOP = "3";
    private final JPanel display = new JPanel();
    private final AbstractButton actionPrevious =
            new JButton(new ImageIcon(ResourceMap.lookup(ResourceMap.PREVIOUS_ICON)));
    private final AbstractButton actionPlay = new JButton(new ImageIcon(ResourceMap.lookup(ResourceMap.PLAY_ICON)));
    private final AbstractButton actionNext = new JButton(new ImageIcon(ResourceMap.lookup(ResourceMap.NEXT_ICON)));
    private final AbstractButton actionStop = new JButton(new ImageIcon(ResourceMap.lookup(ResourceMap.STOP_ICON)));
    private final JProgressBar progressIndicator = new JProgressBar();
    private boolean editorStatus;
    private boolean walkerActive;

    Navigator() {
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
        display.setBorder(BorderFactory.createTitledBorder("Navigazione"));

        actionPrevious.setActionCommand(COMMAND_PREVIOUS);
        Window.addShortcut(actionPrevious, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Precedente");
        actionPlay.setActionCommand(COMMAND_PLAY);
        Window.addShortcut(actionPlay, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                "Entra in modalità Avanzamento automatico");
        actionNext.setActionCommand(COMMAND_NEXT);
        Window.addShortcut(actionNext, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Successivo");
        actionStop.setActionCommand(COMMAND_STOP);
        Window.addShortcut(actionStop, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "Torna in modalità Manuale");

        Container commands = new JPanel();
        commands.add(actionPrevious);
        commands.add(actionPlay);
        commands.add(actionNext);
        commands.add(actionStop);
        display.add(commands);
        display.add(progressIndicator);

        ActionListener actionHandler = new ActionHandler();
        actionPrevious.addActionListener(actionHandler);
        actionPlay.addActionListener(actionHandler);
        actionNext.addActionListener(actionHandler);
        actionStop.addActionListener(actionHandler);
    }

    public boolean isWalkerActive() {
        return walkerActive;
    }

    public void setWalkerActive(boolean flag) {
        boolean invertedFlag = !flag;

        boolean groupEnabled = editorStatus && invertedFlag;
        actionPrevious.setEnabled(groupEnabled);
        actionPlay.setEnabled(groupEnabled);
        actionNext.setEnabled(groupEnabled);

        FileManager fileManager = Yiupp.getWorkbench().getControlPane().getAttackView().getFileManager();
        fileManager.updateCommands(invertedFlag);

        actionStop.setEnabled(flag);

        if (invertedFlag) {
            progressIndicator.setValue(0);
        }

        walkerActive = flag;
    }

    public void updateCommands(boolean canSelect, int length) {
        actionPrevious.setEnabled(canSelect);
        actionPlay.setEnabled(canSelect);
        actionNext.setEnabled(canSelect);
        editorStatus = canSelect;
        progressIndicator.setMaximum(length);
    }

    Component getDisplay() {
        return display;
    }

    private void setProgress(int step) {
        progressIndicator.setValue(++step);
    }

    private final class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            Editor editor = Yiupp.getWorkbench().getControlPane().getAttackView().getEditor();

            switch (event.getActionCommand()) {
                case COMMAND_PREVIOUS:
                    editor.selectPrevious();
                    break;
                case COMMAND_PLAY:
                    Browser browser = Yiupp.getWorkbench().getBrowser();
                    if (!browser.isListenerInstalled()) {
                        browser.installListener(new BrowserHandler());
                    }

                    setWalkerActive(true);
                    int next = editor.selectNext();
                    setProgress(next);
                    break;
                case COMMAND_NEXT:
                    editor.selectNext();
                    break;
                case COMMAND_STOP:
                    setWalkerActive(false);
                    actionPlay.requestFocusInWindow();
                    break;
            }
        }

    }

    private final class BrowserHandler implements Browser.BrowserListener {

        private final Timer timer = new Timer(1000, new TimerHandler());

        private BrowserHandler() {
            timer.setRepeats(false);
        }

        @Override
        public boolean canLoadAgain() {
            return !timer.isRunning();
        }

        @Override
        public void run() {
            if (!isWalkerActive()) {
                return;
            }
            timer.start();
        }

        private final class TimerHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                if (!isWalkerActive()) {
                    return;
                }

                Editor editor = Yiupp.getWorkbench().getControlPane().getAttackView().getEditor();
                int next = editor.selectNext();

                setProgress(next);
            }

        }

    }

}
