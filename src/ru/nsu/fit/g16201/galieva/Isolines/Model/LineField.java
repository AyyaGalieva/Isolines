package ru.nsu.fit.g16201.galieva.Isolines.Model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LineField {
    private int gridSizeX, gridSizeY;

    private ArrayList<Color> levelColors;
    private Color isolineColor;

    private Function function;

    private double a, b, c, d;

    public LineField(int gridSizeX, int gridSizeY, ArrayList<Color> levelColors, Color isolineColor, Function function) {
        a = -3;
        b = 3;
        c = -3;
        d = 3;
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        this.levelColors = levelColors;
        this.isolineColor = isolineColor;
        this.function = function;
        function.findMinMax(a, b, c, d, gridSizeX, gridSizeY);
    }

    public Line getIsoline(double level) {
        double cellWidth = (b-a)/gridSizeX;
        double cellHeight = (d-c)/gridSizeY;

        ArrayList<Point2D.Double[]> isoline = new ArrayList<>();
        for (int y = 0; y < gridSizeY; ++y) {
            for (int x = 0; x < gridSizeX; ++x) {
                ArrayList<Point2D.Double[]> sides = new ArrayList<>();
                sides.add(new Point2D.Double[]{new Point2D.Double(a+cellWidth*x,c+cellHeight*y),
                        new Point2D.Double(a+cellWidth*x,c+cellHeight*(y+1))});
                sides.add(new Point2D.Double[]{new Point2D.Double(a+cellWidth*x,c+cellHeight*y),
                        new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*y)});
                sides.add(new Point2D.Double[]{new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*y),
                        new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*(y+1))});
                sides.add(new Point2D.Double[]{new Point2D.Double(a+cellWidth*x,c+cellHeight*(y+1)),
                        new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*(y+1))});

                ArrayList<Point2D.Double> intersectionPoints = getIntersectionPoints(sides, level);
                if (intersectionPoints.size() < 2) {
                    continue;
                }
                if (intersectionPoints.size() == 2) {
                    isoline.add(new Point2D.Double[]{intersectionPoints.get(0), intersectionPoints.get(1)});
                    continue;
                }
                if (intersectionPoints.size() == 4) {
                    Point2D.Double left = intersectionPoints.get(0);
                    Point2D.Double right = intersectionPoints.get(0);
                    Point2D.Double bottom = intersectionPoints.get(0);
                    Point2D.Double top = intersectionPoints.get(0);
                    for (Point2D.Double point : intersectionPoints) {
                        left = (left.x < point.x)?left:point;
                        right = (right.x > point.x)?right:point;
                        top = (top.y < point.y)?top:point;
                        bottom = (bottom.y > point.y)?bottom:point;
                    }

                    int[] f = new int[5];
                    f[0] = (function.getValue(new Point2D.Double(a+cellWidth*x,c+cellHeight*y)) < level-0.0001)?-1:1;
                    f[1] = (function.getValue(new Point2D.Double(a+cellWidth*x,c+cellHeight*(y+1))) < level-0.0001)?-1:1;
                    f[2] = (function.getValue(new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*(y+1))) < level-0.0001)?-1:1;
                    f[3] = (function.getValue(new Point2D.Double(a+cellWidth*(x+1),c+cellHeight*y)) < level-0.0001)?-1:1;
                    f[4] = (function.getValue(new Point2D.Double(a+cellWidth*(x+0.5),c+cellHeight*(y+0.5))) < level-0.0001)?-1:1;

                    if (((f[0] == 1 && f[1] == -1 && f[2] == 1 && f[3] == -1)&&(f[4]==-1))||((f[0]==-1 && f[1]==1 && f[2]==-1 && f[3]==1)&&(f[4]==1))) {
                        isoline.add(new Point2D.Double[]{left, top});
                        isoline.add(new Point2D.Double[]{right, bottom});
                    }
                    else if (((f[0] == 1 && f[1] == -1 && f[2] == 1 && f[3] == -1)&&(f[4]==1))||((f[0]==-1 && f[1]==1 && f[2]==-1 && f[3]==1)&&(f[4]==-1))) {
                            isoline.add(new Point2D.Double[]{left, bottom});
                            isoline.add(new Point2D.Double[]{right, top});
                    }
                }
            }
        }
        ArrayList<Point2D.Double> points = new ArrayList<>();
        for (Point2D.Double[] line : isoline) {
            if (!isInList(points, line[0]))
                points.add(line[0]);
            if (!isInList(points,line[1]))
                points.add(line[1]);
        }
        return new Line(points, isoline);
    }

    private boolean isInList(ArrayList<Point2D.Double> list, Point2D.Double point) {
        for (Point2D.Double p : list) {
            if (Math.abs(p.x-point.x) < 0.0001 && Math.abs(p.y-point.y) < 0.0001) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Point2D.Double> getIntersectionPoints(ArrayList<Point2D.Double[]> lines, double level) {
        ArrayList<Point2D.Double> intersectionPoints = new ArrayList<>();
        for (Point2D.Double[] line : lines) {
            double x1 = line[0].x;
            double y1 = line[0].y;
            double x2 = line[1].x;
            double y2 = line[1].y;
            double f1 = function.getValue(new Point2D.Double(x1, y1));
            double f2 = function.getValue(new Point2D.Double(x2, y2));
            Point2D.Double point = null;
            if (Math.max(f1, f2)>level && level>Math.min(f1,f2)) {
                if (Math.abs(f1-f2) < 0.0001) {
                    point = new Point2D.Double(x1, y1);
                }
                else point = new Point2D.Double(x1+(Math.abs(f1-level)/Math.abs(f1-f2))*Math.abs(x1-x2),
                        y1+(Math.abs(f1-level)/Math.abs(f1-f2))*Math.abs(y1-y2));
            }

            if (point != null)
                intersectionPoints.add(point);
        }
        return intersectionPoints;
    }

    public Color getColor(double f, boolean interpolationMode) {
        if (levelColors.size() == 1)
            return levelColors.get(0);

        double pos = (f - function.getMin()) / (function.getMax() - function.getMin());

        double levelColorCount = levelColors.size() - (interpolationMode ? 1 : 0);
        int levelColor = Math.max(0, Math.min((int) (pos * levelColorCount), levelColors.size() - 1));

        if (!interpolationMode)
            return levelColors.get(levelColor);

        double eps = (pos*levelColorCount - levelColor);
        Color color = levelColors.get(levelColor);
        Color nextColor = levelColors.get(levelColor + 1 < levelColors.size() ? levelColor + 1 : levelColor);
        return new Color(Math.max(0, Math.min(255, (int) (color.getRed() * (1.0 - eps) + nextColor.getRed() * eps))),
                Math.max(0, Math.min(255, (int) (color.getGreen() * (1.0 - eps) + nextColor.getGreen() * eps))),
                Math.max(0, Math.min(255, (int) (color.getBlue() * (1.0 - eps) + nextColor.getBlue() * eps))));
    }

    public int getLevelCount() {
        return levelColors.size();
    }

    public void setGrid(int sizeX, int sizeY) {
        this.gridSizeX = sizeX;
        this.gridSizeY = sizeY;
        function.findMinMax(a, b, c, d, sizeX, sizeY);
    }

    public Function getFunction() {
        return function;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    public void setRange(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        function.findMinMax(a, b, c, d, gridSizeX, gridSizeY);
    }

    public Color getIsolineColor() {
        return isolineColor;
    }
}
