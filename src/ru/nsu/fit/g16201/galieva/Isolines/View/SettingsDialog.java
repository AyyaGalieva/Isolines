package ru.nsu.fit.g16201.galieva.Isolines.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsDialog extends JDialog {
    private JPanel panel;

    private JTextField aField, bField, cField, dField, gridSizeXField, gridSizeYField;

    public SettingsDialog(double a, double b, double c, double d, int gridSizeX, int gridSizeY, ImagePanel imagePanel, ChangeListener changeListener) {
        setSize(500, 500);
        setResizable(false);
        setTitle("Settings");

        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panel);
        panel.setLayout(new GridLayout(8, 2, 10, 2));

        addParameters(a, b, c, d, gridSizeX, gridSizeY);

        JButton ok = new JButton("OK");
        ok.addMouseListener(new MouseAdapter() {
            boolean pressedOrEntered = false;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkParameters() && pressedOrEntered) {
                    changeListener.run(Double.parseDouble(aField.getText()),
                            Double.parseDouble(bField.getText()),
                            Double.parseDouble(cField.getText()),
                            Double.parseDouble(dField.getText()),
                            Integer.parseInt(gridSizeXField.getText()),
                            Integer.parseInt(gridSizeYField.getText()));
                    panel.repaint();
                    SettingsDialog.this.dispose();
                    imagePanel.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pressedOrEntered = false;
            }
        });

        panel.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new MouseAdapter() {
            boolean pressedOrEntered = false;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pressedOrEntered)
                    SettingsDialog.this.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pressedOrEntered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pressedOrEntered = false;
            }
        });

        panel.add(cancel);
    }

    private void addParameters(double a, double b, double c, double d, int gridSizeX, int gridSizeY) {
        panel.add(new JLabel("a:"));
        aField = new JTextField();
        aField.setText(Double.toString(a));
        panel.add(aField);
        panel.add(new JLabel("b:"));
        bField = new JTextField();
        bField.setText(Double.toString(b));
        panel.add(bField);
        panel.add(new JLabel("c:"));
        cField = new JTextField();
        cField.setText(Double.toString(c));
        panel.add(cField);
        panel.add(new JLabel("d:"));
        dField = new JTextField();
        dField.setText(Double.toString(d));
        panel.add(dField);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel("k:"));
        gridSizeXField = new JTextField();
        gridSizeXField.setText(Integer.toString(gridSizeX));
        panel.add(gridSizeXField);
        panel.add(new JLabel("m:"));
        gridSizeYField = new JTextField();
        gridSizeYField.setText(Integer.toString(gridSizeY));
        panel.add(gridSizeYField);
    }

    private boolean checkParameters() {
        try {
            if (aField.getText().isEmpty() || bField.getText().isEmpty() || cField.getText().isEmpty() || dField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Function domain is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (gridSizeXField.getText().isEmpty() || gridSizeYField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Grid size is empty", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double c = Double.parseDouble(cField.getText());
            double d = Double.parseDouble(dField.getText());

            if (a > b - 0.0001) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "b must be greater then a", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (c > d - 0.0001) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "d must be greater then c", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            int gridSizeX = Integer.parseInt(gridSizeXField.getText());
            int gridSizeY = Integer.parseInt(gridSizeYField.getText());
            if (gridSizeX < 2 || gridSizeX > 600 || gridSizeY < 2 || gridSizeY > 600) {
                JOptionPane.showMessageDialog(SettingsDialog.this, "Grid size must be from 2 to 500", "error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(SettingsDialog.this, e.getMessage(), "error", JOptionPane.WARNING_MESSAGE);
        }
        return true;
    }
}
