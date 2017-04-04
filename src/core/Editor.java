package it.menzani.yiupp.core;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.reporter.UncaughtException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Editor {

    private final JComponent display = new JPanel(new BorderLayout());
    private final JList<String> manualNavigator = new JList<>();

    Editor() {
        display.setPreferredSize(new Dimension());
        display.setBorder(BorderFactory.createRaisedBevelBorder());

        manualNavigator.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JComponent manualNavigatorScroller = new JScrollPane(manualNavigator);
        manualNavigatorScroller.setBorder(null);
        display.add(manualNavigatorScroller);

        manualNavigator.addMouseListener(new MouseHandler());
        manualNavigator.addListSelectionListener(new ListSelectionHandler());
    }

    public void loadList(File yiuppListFile) {
        List<String> temp = new ArrayList<>();
        if (yiuppListFile != null) {
            try (Scanner scanner = new Scanner(yiuppListFile, "ISO-8859-1")) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.isEmpty()) {
                        continue;
                    }
                    temp.add(line);
                }
            } catch (FileNotFoundException ex) {
                throw new UncaughtException(ex);
            }
        }
        int length = temp.size();
        manualNavigator.setListData(temp.toArray(new String[length]));

        Navigator navigator = Yiupp.getWorkbench().getControlPane().getAttackView().getNavigator();
        navigator.updateCommands(length > 0, length);
    }

    public void selectPrevious() {
        int index = manualNavigator.getSelectedIndex();
        if (index <= 0) {
            index = manualNavigator.getModel().getSize();
        }
        index--;
        manualNavigator.setSelectedIndex(index);
        manualNavigator.ensureIndexIsVisible(index);
    }

    public int selectNext() {
        int index = manualNavigator.getSelectedIndex() + 1;
        if (index == manualNavigator.getModel().getSize()) {
            index = 0;
        }
        manualNavigator.setSelectedIndex(index);
        manualNavigator.ensureIndexIsVisible(index);
        return index;
    }

    public void exit() {
        Navigator navigator = Yiupp.getWorkbench().getControlPane().getAttackView().getNavigator();
        navigator.setWalkerActive(false);

        manualNavigator.clearSelection();
    }

    Component getDisplay() {
        return display;
    }

    private static final class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent event) {
            Navigator navigator = Yiupp.getWorkbench().getControlPane().getAttackView().getNavigator();
            if (navigator.isWalkerActive()) {
                navigator.setWalkerActive(false);
            }
        }

    }

    private final class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            if (manualNavigator.isSelectionEmpty()) {
                return;
            }

            Site site = Yiupp.getWorkbench().getControlPane().getAttackView().getSite();
            String siteUrl = site.getUrl();

            if (siteUrl == null) {
                exit();
                Broadcast.setMessage("Inserisci un sito per avviare i test", Broadcast.MessageType.ERROR);
                site.startTyping();
                return;
            }

            String pageUrl = siteUrl + manualNavigator.getSelectedValue();
            Broadcast.setMessage(pageUrl);
            Browser browser = Yiupp.getWorkbench().getBrowser();
            browser.loadPage(pageUrl);
        }

    }

}
