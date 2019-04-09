package ru.nsu.fit.g16201.galieva.Isolines.View;

import ru.nsu.fit.g16201.galieva.Isolines.Model.Function;
import ru.nsu.fit.g16201.galieva.Isolines.Model.Line;
import ru.nsu.fit.g16201.galieva.Isolines.Model.LineField;
import ru.nsu.fit.g16201.galieva.Isolines.Model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private Model model;

    private int panelSizeX, panelSizeY, fieldSizeX, fieldSizeY;
    private static final int offset = 10;
    private static final int fontSize = 10;
    private static final int legendWidth = 20;

    private boolean gridMode;
    private boolean lineMode;
    private boolean drawLineMode;
    private boolean showPointsMode;
    private boolean interpolationMode;
    private boolean isEnable;

    private ArrayList<Double> additionalLines = new ArrayList<>();
    private Double additionalLineLevel;

    public ImagePanel(Model _model, JLabel statusBar) {
        setMinimumSize(new Dimension(500 + offset*2, 400 + offset*2));

        model = _model;
        isEnable = false;
        gridMode = true;
        lineMode = true;
        drawLineMode = false;
        showPointsMode = false;
        interpolationMode = false;
        additionalLineLevel = null;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (isEnable && isField(e) && lineMode) {
                    additionalLines.add(getLevel(e));
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (isEnable && isField(e) && lineMode) {
                    additionalLines.add(getLevel(e));
                }
                drawLineMode = false;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (isEnable) {
                    paintCoordinates(e);
                    if (isField(e) && lineMode) {
                        drawLineMode = true;
                        additionalLineLevel = getLevel(e);
                        repaint();
                    } else mouseReleased(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isEnable)
                paintCoordinates(e);
            }

            private Point2D.Double getFieldCoordinates(MouseEvent e) {
                LineField lineMap = model.getLineField();
                double a = lineMap.getA();
                double b = lineMap.getB();
                double c = lineMap.getC();
                double d = lineMap.getD();
                double width = b - a;
                double height = d - c;
                return new Point2D.Double(a + width*(e.getX()-offset)/fieldSizeX, c + height*(fieldSizeY-(e.getY()-offset))/fieldSizeY);
            }

            private double getLevel(MouseEvent e) {
                Function function = model.getLineField().getFunction();
                return function.getValue(getFieldCoordinates(e));
            }

            private boolean isField(MouseEvent e) {
                return (e.getX()>offset)&&(e.getX()<offset+fieldSizeX)&&(e.getY()>offset)&&(e.getY()<offset+fieldSizeY);
            }

            private void paintCoordinates(MouseEvent e) {
                if (isField(e)) {
                    statusBar.setText(String.format("x=%.2f y=%.2f f(x,y)=%.2f", getFieldCoordinates(e).x, getFieldCoordinates(e).y, getLevel(e)));
                } else statusBar.setText("");
            }
        };
        addMouseMotionListener(mouseAdapter);
        addMouseListener(mouseAdapter);
    }

    private void drawIsoline(double f, Graphics2D graphics2D, LineField lineField) {
        double a = lineField.getA();
        double b = lineField.getB();
        double c = lineField.getC();
        double d = lineField.getD();
        double width = b - a;
        double height = d - c;

        Line isoline = lineField.getIsoline(f);
        List<Point2D.Double[]> segments = isoline.getSegments();
        if (lineMode) {
            for (Point2D.Double[] segment : segments) {
                graphics2D.drawLine(offset+(int)(((segment[0].x - a)/width)*fieldSizeX),
                        offset+(int)(((height - (segment[0].y - c))/height)*fieldSizeY),
                        offset+(int)(((segment[1].x - a)/width)*fieldSizeX),
                        offset+(int)(((height - (segment[1].y - c))/height)*fieldSizeY));
            }
        }

        if (showPointsMode) {
            List<Point2D.Double> points = isoline.getPoints();
            for (Point2D.Double point : points) {
                graphics2D.setColor(Color.BLUE);
                graphics2D.fillOval(offset + (int) ((point.x - a) / width * fieldSizeX) - 1,
                        offset + (int) ((height - (point.y - c)) / height * fieldSizeY) - 1,
                        4, 4);
                graphics2D.setColor(Color.black);
                graphics2D.drawOval(offset + (int) ((point.x - a) / width * fieldSizeX) - 1,
                        offset + (int) ((height - (point.y - c)) / height * fieldSizeY) - 1,
                        4, 4);
            }
        }
    }

    private void drawField(Graphics2D graphics2D, LineField lineField) {
        Function function = lineField.getFunction();
        double a = lineField.getA();
        double b = lineField.getB();
        double c = lineField.getC();
        double d = lineField.getD();
        double width = b - a;
        double height = d - c;
        int sizeX = lineField.getGridSizeX();
        int sizeY = lineField.getGridSizeY();

        for (int x = 0; x < fieldSizeX; ++x) {
            for (int y = 0; y < fieldSizeY; ++y) {
                double f = function.getValue(new Point2D.Double(a+width*(double)x/fieldSizeX, c+height*(double)(fieldSizeY-y)/fieldSizeY));
                image.setRGB(x+offset, y+offset, lineField.getColor(f, interpolationMode).getRGB());
            }
        }

        graphics2D.setColor(lineField.getIsolineColor());
        for (int i = 0; i < lineField.getLevelCount(); ++i) {
            drawIsoline(function.getMin() + ((double)i/lineField.getLevelCount())*(function.getMax()-function.getMin()), graphics2D, lineField);
        }

        if (drawLineMode && additionalLineLevel!=null) {
            drawIsoline(additionalLineLevel, graphics2D, lineField);
        }

        for (double level : additionalLines) {
            drawIsoline(level, graphics2D, lineField);
        }

        graphics2D.setColor(Color.black);
        if (gridMode) {
            for (int i = 0; i <= sizeX; ++i) {
                int x = (int)(fieldSizeX*((double)i/sizeX))+offset;
                graphics2D.drawLine(x, offset, x, fieldSizeY+offset);
            }

            for (int i = 0; i <= sizeY; ++i) {
                int y = (int)(fieldSizeY*((double)i/sizeY))+offset;
                graphics2D.drawLine(offset, y, fieldSizeX+offset, y);
            }
        }
    }

    private void drawLegend(Graphics2D graphics2D, LineField lineField) {
        Function function = lineField.getFunction();
        for (int x = 0; x < fieldSizeX; ++x) {
            double pos = ((double)x/fieldSizeX)*(function.getMax()-function.getMin());
            Color color = lineField.getColor(function.getMin() + pos, interpolationMode);
            for (int y = 0; y < legendWidth; ++y) {
                image.setRGB(x + offset, y + fieldSizeY + offset*2, color.getRGB());
            }
        }
        graphics2D.setColor(Color.black);
        for (int i = 0; i < lineField.getLevelCount(); ++i) {
            double pos = ((double)i/lineField.getLevelCount())*(function.getMax()-function.getMin());
            int x = offset + (int)(fieldSizeX*((double)i/lineField.getLevelCount()));
            graphics2D.drawLine(x, fieldSizeY + offset*2, x, fieldSizeY + offset*2 + legendWidth - 1);
            graphics2D.drawString(String.format("%.2f", function.getMin() + (1.0/lineField.getLevelCount())*(function.getMax()-function.getMin())/2 + pos),
                    x + fieldSizeX/lineField.getLevelCount()/2 - fontSize, fieldSizeY + offset*2 + fontSize + 20);
        }
    }

    public void clearAddedIsolines() {
        additionalLines.clear();
        additionalLineLevel = null;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        panelSizeX = getWidth();
        panelSizeY = getHeight();
        fieldSizeX = panelSizeX - offset*2;
        fieldSizeY = panelSizeY - offset*2 - 40;
        setPreferredSize(new Dimension(panelSizeX, panelSizeY));

        image = new BufferedImage(panelSizeX, panelSizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = (Graphics2D) image.getGraphics();
        LineField lineField = model.getLineField();

        if (lineField != null) {
            drawField(graphics2D, lineField);
            drawLegend(graphics2D, lineField);
        }
        graphics.drawImage(image, 0, 0, panelSizeX, panelSizeY, this);
    }

    public void setGridMode(boolean gridMode) {
        this.gridMode = gridMode;
    }

    public void setLineMode(boolean lineMode) {
        this.lineMode = lineMode;
    }

    public void setShowPointsMode(boolean showPointsMode) {
        this.showPointsMode = showPointsMode;
    }

    public void setInterpolationMode(boolean interpolationMode) {
        this.interpolationMode = interpolationMode;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
