package it.menzani.yiupp;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import it.menzani.yiupp.core.Broadcast;
import it.menzani.yiupp.reporter.UncaughtException;
import it.menzani.yiupp.storage.DataFolder;
import it.menzani.yiupp.storage.Layout;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.TextManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Window {

    private final Collection<Runnable> runLaterQueue = new HashSet<>();
    private final AtomicBoolean beforeClose = new AtomicBoolean();
    private String currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();
    private JFrame frame;
    private WindowContent windowContent;
    private File currentOpenDialogDirectory;

    Window() {

    }

    /**
     * Registers a shortcut that will activate the given button when the window that contains it is focused.
     */
    public static void addShortcut(AbstractButton button, KeyStroke keyStroke) {
        addShortcut(button, keyStroke, null);
    }

    /**
     * Registers a shortcut that will activate the given button when the window that contains it is focused, and uses
     * the given description as the button's tooltip text.
     *
     * @param description if not null, indicates the text for the button's tooltip
     */
    public static void addShortcut(final AbstractButton button, KeyStroke keyStroke, @Nullable String description) {
        addShortcutHandler(button, keyStroke, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                button.requestFocusInWindow();
                // Needed to preserve normal focus handling
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        button.doClick();
                    }
                });
            }
        });
        if (description != null) {
            button.setToolTipText(description);
        }
    }

    /**
     * Registers an action to be executed for the given component when the specified keystroke is recognized, and the
     * focused window is the window that contains the component.
     * <p>
     * This is a convenience method that uses UUIDs as keys for the component action and input maps.
     */
    public static void addShortcutHandler(JComponent component, KeyStroke keyStroke, Action handler) {
        UUID key = UUID.randomUUID();
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
        component.getActionMap().put(key, handler);
    }

    public void useCrossPlatformLookAndFeel() {
        setCustomLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    public void setCustomLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
        } catch (Exception ex) {
            throw new UncaughtException(ex);
        }
        currentLookAndFeel = className;
    }

    public void useSystemLookAndFeel() {
        setCustomLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    public String getCurrentLookAndFeel() {
        return currentLookAndFeel;
    }

    /**
     * Used to execute logic after the GUI creation process has completed.
     * <p>
     * In certain circumstances, it is necessary to use this method. For example, when accessing another class from
     * inside the {@code core} package, or when using some Swing components that require such post-initialization to
     * function properly.
     */
    public void runLater(@NotNull Runnable task) {
        runLaterQueue.add(task);
    }

    /**
     * Allows to call the {@link #askUser(CharSequence)} method from non-GUI related code.
     * <p>
     * This method must not be invoked from the Event Dispatch Thread.
     */
    public int askUserConcurrently(final CharSequence question) {
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return askUser(question);
            }
        });
        SwingUtilities.invokeLater(task);
        try {
            return task.get();
        } catch (Exception ex) {
            throw new UncaughtException(ex);
        }
    }

    public int askUser(CharSequence question) {
        Broadcast.setMessage(question, Broadcast.MessageType.WARNING);
        return JOptionPane.showConfirmDialog(frame, TextManager.collapseHtml(question), frame.getTitle(),
                JOptionPane.YES_NO_OPTION);
    }

    public WindowContent getWindowContent() {
        return windowContent;
    }

    public File[] showOpenDialog(FileFilter filter) {
        JFileChooser fileChooser = new JFileChooser(currentOpenDialogDirectory);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);

        int resultId = fileChooser.showOpenDialog(frame);
        if (resultId == JFileChooser.APPROVE_OPTION) {
            currentOpenDialogDirectory = fileChooser.getCurrentDirectory();
            return fileChooser.getSelectedFiles();
        }
        return null;
    }

    /**
     * Performs pre-shutdown logic.
     * <p>
     * This method must be invoked once, usually before the {@link #close()} method.
     * If the client doesn't care about the order of execution, it will be invoked after that method.
     *
     * @throws IllegalStateException if the method is invoked more than once
     */
    public void beforeClose() {
        if (!beforeClose.compareAndSet(false, true)) {
            throw new IllegalStateException("This method can only be invoked once");
        }

        Yiupp.getInstance().getExecutorService().shutdown();

        Layout layout = DataFolder.getInstance().getLayout();
        layout.setWorkbenchSeparator(Yiupp.getWorkbench().getSeparatorLocation());
        int extendedState = frame.getExtendedState();
        if (extendedState == JFrame.ICONIFIED) {
            extendedState = JFrame.NORMAL;
        }
        layout.setExtendedState(extendedState);
        if (extendedState == JFrame.MAXIMIZED_BOTH) {
            layout.setLocation(null);
            layout.setDimension(null);
        } else {
            layout.setLocation(frame.getLocation());
            Point dimension = new Point();
            Dimension2D size = frame.getSize();
            dimension.setLocation(size.getWidth(), size.getHeight());
            layout.setDimension(dimension);
        }

        DataFolder dataFolder = DataFolder.getInstance();
        dataFolder.saveAll();
    }

    /**
     * Closes the main frame, thus terminating the whole application.
     * <p>
     * This method can be invoked from any thread, and is supposed to run only once.
     */
    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    /**
     * Creates the window in memory.
     */
    void create() {
        JFrame.setDefaultLookAndFeelDecorated(false);
        frame = new JFrame(Yiupp.DISPLAY_NAME.expand());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(ResourceMap.lookup(ResourceMap.WINDOW_ICON)).getImage());

        windowContent = new WindowContent();
        frame.setContentPane(windowContent.getDisplay());

        Layout layout = DataFolder.getInstance().getLayout();
        int extendedState = layout.getExtendedState();
        Point dimension = layout.getDimension();
        if (dimension == null) {
            frame.pack();
        } else if (extendedState != JFrame.MAXIMIZED_BOTH) {
            frame.setSize(dimension.x, dimension.y);
        }
        Point location = layout.getLocation();
        if (location == null) {
            frame.setLocationByPlatform(true);
        } else if (extendedState != JFrame.MAXIMIZED_BOTH) {
            frame.setLocation(location.x, location.y);
        }
        frame.setExtendedState(extendedState);

        frame.addWindowListener(new WindowHandler());
    }

    /**
     * Displays the window.
     */
    void display() {
        // Needed to delay execution a bit
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Runnable task : runLaterQueue) {
                    task.run();
                }
                // For better perceived performance
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisible(true);
                    }
                });
            }
        });
    }

    private final class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            if (beforeClose.get()) {
                return;
            }
            beforeClose();

            // May hang otherwise
            NativeInterface.close(); // Allow them to perform their shutdown logic
            Runtime.getRuntime().halt(0);
        }

    }

}
