package ru.nsu.fit.g16201.galieva.Isolines.Model;

import java.awt.geom.Point2D;

public class Function {
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public Function(){}

    public void findMinMax(double a, double b, double c, double d, int dx, int dy) {
        for (int x = 0; x < dx; ++x) {
            for (int y = 0; y < dy; ++y) {
                double f = getValue(new Point2D.Double(a + ((double)x/dx)*(b-a), c + ((double)y/dy)*(d-c)));
                min = (f<min)?f:min;
                max = (f>max)?f:max;
            }
        }
    }

    public double getValue(Point2D.Double p) {
        return Math.sin(p.y)*Math.cos(p.x);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
