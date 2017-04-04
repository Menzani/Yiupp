package it.menzani.yiupp.core;

import it.menzani.yiupp.Window;
import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.util.Desktop;
import it.menzani.yiupp.util.Desktops;
import it.menzani.yiupp.util.Origin;
import it.menzani.yiupp.util.Origins;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

class Chooser {

    private final Component title;
    private final Collection<Component> options = new ArrayList<>();
    private final ButtonGroup selectionHandler = new ButtonGroup();

    Chooser(String title) {
        this.title = new JLabel(title);
    }

    Chooser addOption(Option option) {
        options.add(option.getDisplay());
        selectionHandler.add(option.button);
        return this;
    }

    Component getDisplay() {
        JPanel display = new JPanel();
        display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
        display.setAlignmentX(Component.CENTER_ALIGNMENT);
        display.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComponent options = new JPanel(new GridLayout(0, 2, 15, 15));
        options.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Component option : this.options) {
            options.add(option);
        }

        display.add(title);
        display.add(Box.createRigidArea(new Dimension(0, 5)));
        display.add(options);
        return display;
    }

    static class Option {

        private final boolean selected;
        private final boolean defaultOption;
        private AbstractButton button;
        private JLabel preview;
        private Runnable task;
        private boolean restartRequired;

        Option() {
            this(false);
        }

        Option(boolean selected) {
            this(selected, false);
        }

        Option(boolean selected, boolean defaultOption) {
            this.selected = selected;
            this.defaultOption = defaultOption;
        }

        Option setName(String name) {
            button = new JRadioButton(name, selected);
            button.setFocusable(false);
            if (defaultOption) {
                Font font = button.getFont();
                button.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
            }
            return this;
        }

        Option setPreview(String preview) {
            this.preview = new JLabel("<html>" + preview + "</html>");
            this.preview.setVerticalAlignment(SwingConstants.TOP);
            this.preview.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            return this;
        }

        Option setPreview(Image preview) {
            this.preview = new PreviewImageJLabel(preview);
            return this;
        }

        Option setCommand(Runnable task) {
            this.task = task;
            return this;
        }

        Option setRestartRequired() {
            restartRequired = true;
            return this;
        }

        private Component getDisplay() {
            JPanel display = new JPanel();
            display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));

            preview.setMinimumSize(new Dimension(0, 50));
            preview.setPreferredSize(new Dimension(150, 90));
            preview.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

            button.addItemListener(new ItemHandler());
            preview.addMouseListener(new MouseHandler());

            display.add(button);
            display.add(Box.createRigidArea(new Dimension(0, 5)));
            display.add(preview);
            return display;
        }

        private final class PreviewImageJLabel extends JLabel {

            private final Image image;

            private PreviewImageJLabel(Image image) {
                super(new ImageIcon(image));
                this.image = image;
            }

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D pen = (Graphics2D) graphics;

                pen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                pen.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                pen.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                pen.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }

        }

        private final class ItemHandler implements ItemListener {

            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                task.run();
                if (!restartRequired) {
                    return;
                }

                Window window = Yiupp.getInstance().getWindow();
                int choiceToRestart = window.askUser("Riavvio necessario. Vuoi effettuarlo ora?");
                if (choiceToRestart != JOptionPane.YES_OPTION) {
                    return;
                }

                Origin origin = Origins.getOrigin();
                if (origin.isRecognized()) {
                    window.beforeClose();

                    Desktop desktop = Desktops.getDesktop();
                    desktop.openIfPossible(origin.getFile());
                }
                window.close();
            }

        }

        private final class MouseHandler extends MouseAdapter {

            @Override
            public void mouseClicked(MouseEvent event) {
                button.doClick();
            }

        }

    }

}
