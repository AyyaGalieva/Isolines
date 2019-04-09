package ru.nsu.fit.g16201.galieva.Isolines.Model;

import java.awt.geom.Point2D;
import java.util.List;

public class Line {
    private List<Point2D.Double> points;
    private List<Point2D.Double[]> segments;

    public Line(List<Point2D.Double> points, List<Point2D.Double[]> segments) {
        this.points = points;
        this.segments = segments;
    }

    public List<Point2D.Double> getPoints() {
        for (Point2D.Double[] segment : segments) {
            if (!inList(points, segment[0]))
                points.add(segment[0]);
            if (!inList(points, segment[1]))
                points.add(segment[1]);
        }
        return points;
    }

    private boolean inList(List<Point2D.Double> list, Point2D.Double val) {
        for (Point2D.Double point : list) {
            if (Math.abs(val.x - point.x) < 0.0001 && Math.abs(val.y - point.y) < 0.0001)
                return true;
        }
        return false;
    }

    public List<Point2D.Double[]> getSegments() {
        return segments;
    }
}
