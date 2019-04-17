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
import java.util.Stack;

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
    private boolean exactColorMode;
    private boolean isEnable;

    private static final Color backgroundColor = new Color(255, 255, 255);

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
    }

    private void drawPoints(double f, Graphics2D graphics2D, LineField lineField){
        double a = lineField.getA();
        double b = lineField.getB();
        double c = lineField.getC();
        double d = lineField.getD();
        double width = b - a;
        double height = d - c;

        Line isoline = lineField.getIsoline(f);
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

        graphics2D.setColor(lineField.getIsolineColor());
        graphics2D.drawLine(offset-1, offset-1, offset-1, fieldSizeY+offset+1);
        graphics2D.drawLine(offset-1, offset-1, fieldSizeX+offset+1, offset-1);
        graphics2D.drawLine(fieldSizeX+offset+1, offset-1, fieldSizeX+offset+1, fieldSizeY+offset+1);
        graphics2D.drawLine(offset-1, fieldSizeY+offset+1, fieldSizeX+offset+1, fieldSizeY+offset+1);

        if (exactColorMode) {
            for (int x = 0; x < fieldSizeX; ++x) {
                for (int y = 0; y < fieldSizeY; ++y) {
                    double f = function.getValue(new Point2D.Double(a + width * (double) x / fieldSizeX, c + height * (double) (fieldSizeY - y) / fieldSizeY));
                    image.setRGB(x + offset, y + offset, lineField.getColor(f, interpolationMode).getRGB());
                }
            }
        }

        if (interpolationMode && !exactColorMode) {
            int gridX = 0;
            int gridY = 0;
            int cellWidth = (int)Math.round((double)fieldSizeX / sizeX);
            int cellHeight = (int)Math.round((double)fieldSizeY / sizeY);
            double f1 = function.getValue(new Point2D.Double(a, d));
            double f2 = function.getValue(new Point2D.Double(a+width*(double)cellWidth/fieldSizeX, d));
            double f3 = function.getValue(new Point2D.Double(a, c + height*(double)(fieldSizeY - cellHeight) / fieldSizeY));
            double f4 = function.getValue(new Point2D.Double(a+width*(double)cellWidth/fieldSizeX, c + height*(double)(fieldSizeY - cellHeight) / fieldSizeY));

            for (int x = 0; x < fieldSizeX; ++x) {
                for (int y = 0; y < fieldSizeY; ++y) {
                    int newGridX = (int)(sizeX*(double)x/fieldSizeX);
                    int newGridY = (int)(sizeY*(double)y/fieldSizeY);
                    if (newGridX != gridX || newGridY != gridY) {
                        f1 = function.getValue(new Point2D.Double(a + width * (double) newGridX*cellWidth / fieldSizeX, c + height * (double)(fieldSizeY - newGridY*cellHeight) / fieldSizeY));
                        f2 = function.getValue(new Point2D.Double(a + width * (double) (newGridX + 1)*cellWidth / fieldSizeX, c + height * (double)(fieldSizeY - newGridY*cellHeight) / fieldSizeY));
                        f3 = function.getValue(new Point2D.Double(a + width * (double) newGridX*cellWidth / fieldSizeX, c + height * (double)(fieldSizeY - (newGridY + 1)*cellHeight) / fieldSizeY));
                        f4 = function.getValue(new Point2D.Double(a + width * (double) (newGridX + 1)*cellWidth / fieldSizeX, c + height * (double)(fieldSizeY - (newGridY + 1)*cellHeight) / fieldSizeY));
                        gridX = newGridX;
                        gridY = newGridY;
                    }
                    int x1 = gridX*cellWidth;
                    int x2 = (gridX + 1)*cellWidth;
                    double fr1 = f3*(double)(x2-x)/(x2-x1) + f4*(double)(x-x1)/(x2-x1);
                    double fr2 = f1*(double)(x2-x)/(x2-x1) + f2*(double)(x-x1)/(x2-x1);
                    int y1 = (gridY+1)*cellHeight;
                    int y2 = gridY*cellHeight;
                    double fp = fr1*(double)(y2-y)/(y2-y1) + fr2*(double)(y-y1)/(y2-y1);
                    image.setRGB(x+offset, y+offset, lineField.getColor(fp, interpolationMode).getRGB());
                }
            }
        }

        for (int i = 0; i < lineField.getLevelCount(); ++i) {
            graphics2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (lineMode) {
                graphics2D.setColor(lineField.getIsolineColor());
            } else {
                graphics2D.setColor(lineField.getColor(function.getMin() + ((double)i/lineField.getLevelCount())*(function.getMax()-function.getMin()), interpolationMode));
            }
            drawIsoline(function.getMin() + ((double)i/lineField.getLevelCount())*(function.getMax()-function.getMin()), graphics2D, lineField);
        }

        if (!exactColorMode && !interpolationMode) {
            graphics2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cellWidth = fieldSizeX / sizeX;
            int cellHeight = fieldSizeY / sizeY;
            for (int y = 0; y <= sizeY; ++y) {
                for (int x = 0; x <= sizeX; ++x) {
                    double f = function.getValue(new Point2D.Double(a + width * (double) x * cellWidth / fieldSizeX, c + height * (double) (fieldSizeY - y * cellHeight) / fieldSizeY));
                    graphics2D.setColor(lineField.getColor(f, false));
                    Point seed = new Point(x * cellWidth + offset, y * cellHeight + offset);
                    if (image.getRGB(seed.x, seed.y) == 0)
                        spanFilling(seed, graphics2D);
                }
            }

            for (int x = 0; x < fieldSizeX; ++x) {
                for (int y = 0; y < fieldSizeY; ++y) {
                    Point seed = new Point(x + offset, y + offset);
                    if (image.getRGB(seed.x, seed.y) == 0) {
                        double f = function.getValue(new Point2D.Double(a + width * (double) x / fieldSizeX, c + height * (double) (fieldSizeY - y) / fieldSizeY));
                        graphics2D.setColor(lineField.getColor(f, false));
                        spanFilling(seed, graphics2D);
                    }
                }
            }
        }

        for (int i = 0; i < lineField.getLevelCount(); ++i) {
            drawPoints(function.getMin() + ((double)i/lineField.getLevelCount())*(function.getMax()-function.getMin()), graphics2D, lineField);
        }

        graphics2D.setColor(lineField.getIsolineColor());
        if (drawLineMode && additionalLineLevel!=null) {
            drawIsoline(additionalLineLevel, graphics2D, lineField);
            drawPoints(additionalLineLevel, graphics2D, lineField);
        }

        for (double level : additionalLines) {
            drawIsoline(level, graphics2D, lineField);
            drawPoints(level, graphics2D, lineField);
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

    private Span defineSpan(Point seed, int color) {
        int leftX = seed.x;
        int rightX = seed.x;
        while ((leftX - 1 > 0)&&(image.getRGB(leftX - 1, seed.y) == color))
            --leftX;
        while ((rightX + 1 < image.getWidth())&&(image.getRGB(rightX+1, seed.y)==color))
            ++rightX;
        return new Span(seed.y, leftX, rightX);
    }

    public void spanFilling(Point seed, Graphics2D graphics2D) {
        int oldColor = image.getRGB(seed.x, seed.y);

        Stack<Span> spans = new Stack<>();
        spans.push(defineSpan(seed, oldColor));

        while (!spans.empty()) {
            Span span = spans.pop();
            graphics2D.drawLine(span.getLeftX(), span.getY(), span.getRightX(), span.getY());
            searchNextSpan(-1, span, oldColor, spans);
            searchNextSpan(1, span, oldColor, spans);
        }
    }

    private void searchNextSpan(int direction, Span curSpan, int oldColor, Stack<Span> spans) {
        if ((curSpan.getY() + direction > 0)&&(curSpan.getY() + direction < image.getHeight())) {
            for (int x = curSpan.getLeftX(); x < curSpan.getRightX(); ++x) {
                if (image.getRGB(x, curSpan.getY() + direction) == oldColor) {
                    Span newSpan = defineSpan(new Point(x, curSpan.getY() + direction), oldColor);
                    spans.push(newSpan);
                    x = newSpan.getRightX();
                }
            }
        }
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

    public class Span {
        private int y, leftX, rightX;

        public Span(int y, int leftX, int rightX) {
            this.y = y;
            this.leftX = leftX;
            this.rightX = rightX;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Span) {
                Span span = (Span)object;
                return (span.y == y)&&(span.leftX == leftX)&&(span.rightX == rightX);
            }
            return false;
        }

        public int getY() {
            return y;
        }

        public int getLeftX() {
            return leftX;
        }

        public int getRightX() {
            return rightX;
        }
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

    public void setExactColorMode(boolean exactColorMode) {
        this.exactColorMode = exactColorMode;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
