package ru.nsu.fit.g16201.galieva.Isolines.View;

import ru.nsu.fit.g16201.galieva.Isolines.Model.LineField;
import ru.nsu.fit.g16201.galieva.Isolines.Model.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {
    private ImagePanel imagePanel;
    private MenuBar menuBar;
    private JToolBar toolBar;
    private JLabel statusBar;

    private Model model;

    private Map<String, AbstractButton> buttonMap = new TreeMap<>();
    private Map<String, Menu> menuMap = new TreeMap<>();
    private Map<String, MenuItem> menuItemMap = new TreeMap<>();

    public GUI(Model model) {
        this.model = model;

        setTitle("Isolines");
        setSize(1200, 800);
        setMinimumSize(new Dimension(600, 400));
        setLocationByPlatform(true);

        menuBar = new MenuBar();
        toolBar = new JToolBar();
        this.setMenuBar(menuBar);

        statusBar = new JLabel();
        statusBar.setPreferredSize(new Dimension(150, 15));
        statusBar.setBackground(Color.white);

        imagePanel = new ImagePanel(model, statusBar);
        add(imagePanel, BorderLayout.CENTER);

        addButton("Load", "File", "Load line map", true, false,"/resources/load.png", () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/test/");
            fileChooser.setDialogTitle("Load state");
            int f = fileChooser.showOpenDialog(GUI.this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (model != null) {
                    model.loadLineField(file.getAbsolutePath());
                }
            }
        });

        toolBar.addSeparator();

        addButton("Settings", "View", "Show settings", true, false,"/resources/settings.jpg", () -> {
            LineField lineField = model.getLineField();

            SettingsDialog settingsDialog = new SettingsDialog(lineField.getA(), lineField.getB(), lineField.getC(), lineField.getD(), lineField.getGridSizeX(), lineField.getGridSizeY(), imagePanel, (a, b, c, d, sizeX, sizeY) -> {
                model.getLineField().setRange(a, b, c, d);
                model.getLineField().setGrid(sizeX, sizeY);
            });
            settingsDialog.setVisible(true);
        });

        toolBar.addSeparator();

        addButton("Show grid", "View", "Show grid", false, true,"/resources/grid.png", () -> {
            imagePanel.setGridMode(buttonMap.get("Show grid").isSelected());
            imagePanel.repaint();
        });

        addButton("Show isolines", "View", "Show isolines", false, true,"/resources/line.png", () -> {
            imagePanel.setLineMode(buttonMap.get("Show isolines").isSelected());
            imagePanel.repaint();
        });

        addButton("Clear", "View", "Clear added isolines", true, false,"/resources/clear.png", () -> {
            imagePanel.clearAddedIsolines();
            imagePanel.repaint();
        });

        addButton("Show points", "View", "Show entry points", false, false,"/resources/point.png", () -> {
            imagePanel.setShowPointsMode(buttonMap.get("Show points").isSelected());
            imagePanel.repaint();
        });

        addButton("Interpolation", "View", "Apply color interpolation", false, false,"/resources/interpolate.jpg", () -> {
            imagePanel.setInterpolationMode(buttonMap.get("Interpolation").isSelected());
            imagePanel.repaint();
        });

        toolBar.addSeparator();

        addButton("Info", "Help", "Show author's info", true, false,"/resources/info.jpg", () ->
                JOptionPane.showMessageDialog(null, "Isolines v.1.0\n" + "Author:\t Ayya Galieva, gr. 16201",
                        "Author info", JOptionPane.INFORMATION_MESSAGE));

        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
        EventQueue.invokeLater(() -> model.loadFirstField());
    }

    private void addButton(String name, String menuName, String toolTipText, boolean shutdown, boolean toggleValue, String imagePath, Runnable action) {
        AbstractButton button;
        MenuItem item;

        Image toolImage = null;
        try {
            toolImage = ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (shutdown) {
            if (toolImage != null) {
                button = new JButton();
                button.setIcon(new ImageIcon(toolImage));
            }
            else {
                button = new JButton(name);
            }
            item = new MenuItem(name);
            item.addActionListener(e -> {
                if (item.isEnabled()) {
                    action.run();
                }
            });
        }
        else {
            if (toolImage != null) {
                button = new JToggleButton();
                button.setIcon(new ImageIcon(toolImage));
                button.setSelected(toggleValue);
            }
            else {
                button = new JToggleButton(name);
            }
            CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(name);
            checkboxMenuItem.addItemListener(e -> {
                if (checkboxMenuItem.isEnabled())
                    action.run();
            });
            item = checkboxMenuItem;
        }

        button.setToolTipText(toolTipText);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean pressedOrEntered = false;
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled() && pressedOrEntered)
                    action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
                statusBar.setText(toolTipText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusBar.setText("");
                pressedOrEntered = false;
            }
        };

        button.addMouseListener(mouseAdapter);
        toolBar.add(button);

        if (!menuMap.containsKey(menuName)) {
            Menu menu = new Menu(menuName);
            menuMap.put(menuName, menu);
            menuBar.add(menu);
        }
        menuMap.get(menuName).add(item);
        menuItemMap.put(name, item);
        buttonMap.put(name, button);
    }

    public void showFileIncorrect() {
        JOptionPane.showMessageDialog(this, "File is incorrect", "error", JOptionPane.WARNING_MESSAGE);
    }

    public void update() {
        imagePanel.setEnable(model.getLineField()!=null);
        this.repaint();
    }
}
