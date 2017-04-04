package it.menzani.yiupp.core;

import it.menzani.yiupp.Window;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.FileLoader;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.Desktop;
import it.menzani.yiupp.util.Desktops;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManager {

    // When updated, change this too: welcome.html
    private static final String COMMAND_ADD = "0";
    private static final String COMMAND_REMOVE = "1";
    private static final String COMMAND_SHOW_IN_EXTERNAL_FILE_MANAGER = "2";
    private static final String COMMAND_REFRESH = "3";

    private final Container display = new JPanel(new GridBagLayout());
    private final FileLoader loader = new FileLoader(DataFolder.RESOURCE_FOLDER);
    private final JList<String> listChooser = new JList<>();
    private final AbstractButton actionAdd =
            new JButton("Aggiungi", new ImageIcon(ResourceMap.lookup(ResourceMap.ADD_ICON)));
    private final AbstractButton actionRemove =
            new JButton("Rimuovi", new ImageIcon(ResourceMap.lookup(ResourceMap.REMOVE_ICON)));
    private final AbstractButton actionRefresh = new JButton("Aggiorna");
    private final ActionHandler actionHandler = new ActionHandler();

    FileManager() {
        display.setPreferredSize(new Dimension(0, 130));
        display.setMaximumSize(new Dimension(Short.MAX_VALUE, 130));

        listChooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Component listChooserScroller = new JScrollPane(listChooser);

        actionRemove.setIconTextGap(8);
        actionRemove.setEnabled(false);
        Component toolSeparator = new JSeparator();
        AbstractButton actionShowInExternalFileManager = new JButton("Gestisci");

        actionAdd.setActionCommand(COMMAND_ADD);
        actionRemove.setActionCommand(COMMAND_REMOVE);
        actionShowInExternalFileManager.setActionCommand(COMMAND_SHOW_IN_EXTERNAL_FILE_MANAGER);
        actionRefresh.setActionCommand(COMMAND_REFRESH);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1D;
        constraints.weighty = 1D;
        display.add(listChooserScroller, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        display.add(actionAdd, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 2, 2, 2);
        display.add(actionRemove, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 2, 2, 2);
        display.add(toolSeparator, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 2, 2, 2);
        display.add(actionShowInExternalFileManager, constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 2, 2, 2);
        constraints.anchor = GridBagConstraints.PAGE_START;
        display.add(actionRefresh, constraints);

        listChooser.addListSelectionListener(new ListSelectionHandler());
        actionAdd.addActionListener(actionHandler);
        actionRemove.addActionListener(actionHandler);
        actionShowInExternalFileManager.addActionListener(actionHandler);
        actionRefresh.addActionListener(actionHandler);

        Yiupp.getInstance().getWindow().runLater(new PostInitHandler());
    }

    public FileLoader getLoader() {
        return loader;
    }

    /**
     * Performs the command that corresponds to the specified ID.
     * <p>
     * This method is safe for use by any thread.
     */
    public void runCommand(final String commandId) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (commandId.equals(COMMAND_REMOVE) && !actionRemove.isEnabled()) {
                    return;
                }
                actionHandler.runAction(commandId);
            }
        });
    }

    public void updateCommands(boolean canOrganize) {
        actionAdd.setEnabled(canOrganize);
        actionRemove.setEnabled(!listChooser.isSelectionEmpty() && canOrganize);
        actionRefresh.setEnabled(canOrganize);
    }

    Component getDisplay() {
        return display;
    }

    private void refresh() {
        loader.refresh();

        int selectionIndex = listChooser.getSelectedIndex();
        String selectionValue = listChooser.getSelectedValue();
        listChooser.setListData(loader.getFiles());

        if (selectionIndex == -1) {
            listChooser.setSelectedIndex(0);
        } else {
            listChooser.setSelectedValue(selectionValue, true);
            if (listChooser.isSelectionEmpty()) {
                selectionIndex--;
                listChooser.setSelectedIndex(selectionIndex);
                listChooser.ensureIndexIsVisible(selectionIndex);
            }
        }
    }

    private final class PostInitHandler implements Runnable {

        @Override
        public void run() {
            refresh();
        }

    }

    private final class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }

            boolean selection = !listChooser.isSelectionEmpty();
            actionRemove.setEnabled(selection);

            Editor editor = Yiupp.getWorkbench().getControlPane().getAttackView().getEditor();
            if (selection) {
                editor.loadList(loader.getFile(listChooser.getSelectedValue()));
            } else {
                editor.loadList(null);
            }
        }

    }

    private final class ActionHandler implements ActionListener {

        private final Timer timer = new Timer(600, new TimerHandler());

        private ActionHandler() {
            timer.setRepeats(false);
        }

        private void runAction(String actionCommand) {
            switch (actionCommand) {
                case COMMAND_ADD:
                    Window window = Yiupp.getInstance().getWindow();
                    FileFilter filter = new FileNameExtensionFilter("Lista di parametri (*.alf)", "alf");
                    File[] filesToImport = window.showOpenDialog(filter);
                    if (filesToImport == null) {
                        return;
                    }
                    boolean unknown = false;
                    try {
                        for (File fileToImport : filesToImport) {
                            Files.copy(fileToImport.toPath(), loader.getFile(fileToImport.getName()).toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                            if (!filter.accept(fileToImport)) {
                                unknown = true;
                            }
                        }
                    } catch (IOException ex) {
                        throw new UncaughtException(ex);
                    }
                    refresh();
                    listChooser.setSelectedValue(filesToImport[0].getName(), true);
                    if (unknown) {
                        Broadcast.setMessage("Sono stati aggiunti file sconosciuti", Broadcast.MessageType.WARNING);
                    }
                    break;
                case COMMAND_REMOVE:
                    File fileToDelete = loader.getFile(listChooser.getSelectedValue());
                    boolean success = fileToDelete.delete();
                    if (!success) {
                        throw new UncaughtException(
                                new IOException("Could not delete resource file: " + fileToDelete.getAbsolutePath()));
                    }
                    int selectionIndex = listChooser.getSelectedIndex();
                    refresh();
                    listChooser.setSelectedIndex(selectionIndex);
                    listChooser.ensureIndexIsVisible(selectionIndex);
                    if (actionRemove.isEnabled()) {
                        actionRemove.requestFocusInWindow();
                    } else {
                        actionAdd.requestFocusInWindow();
                    }
                    break;
                case COMMAND_SHOW_IN_EXTERNAL_FILE_MANAGER:
                    actionRefresh.setBackground(Color.RED);
                    File folder = loader.getFolder();
                    Desktop desktop = Desktops.getDesktop();
                    desktop.openIfPossible(folder);
                    Broadcast.setMessage("Cartella: " + folder.getAbsolutePath());
                    break;
                case COMMAND_REFRESH:
                    refresh();
                    Broadcast.setMessage("Liste aggiornate");
                    if (!actionRefresh.getBackground().equals(Color.RED)) {
                        return;
                    }
                    actionRefresh.setBackground(new Color(0, 150, 0));
                    timer.start();
                    break;
            }

        }

        private final class TimerHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent event) {
                actionRefresh.setBackground(null);
            }

        }

        @Override
        public void actionPerformed(ActionEvent event) {
            runAction(event.getActionCommand());
        }

    }

}
