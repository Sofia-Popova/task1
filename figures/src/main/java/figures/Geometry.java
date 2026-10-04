package figures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Geometry {

    //считает 0
    public static final double EPS = 1e-9;

    private Geometry() { }

    //маштабирование
    static void requireFactor(double k) {
        if (!Double.isFinite(k) || k <= 0) {
            throw new GeometryException("Коэффициент масштабирования должен быть положительным: " + k);
        }
    }

    //2 направления относительно друг друга
    public static double cross(Point o, Point b, Point c) {
        return (b.x() - o.x()) * (c.y() - o.y()) - (b.y() - o.y()) * (c.x() - o.x());
    }

    //Ориентация тройки точек
    public static Orientation orientation(Point a, Point b, Point c) {
        double v = cross(a, b, c);
        if (Math.abs(v) <= EPS) return Orientation.COLLINEAR;
        return v > 0 ? Orientation.COUNTERCLOCKWISE : Orientation.CLOCKWISE;
    }

    //самое мал расстояние
    public static double distanceToSegment(Point p, Point a, Point b) {
        double dx = b.x() - a.x(), dy = b.y() - a.y();
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) return p.distanceTo(a);
        double t = ((p.x() - a.x()) * dx + (p.y() - a.y()) * dy) / len2;
        //где находится ближайшая точка относительно отрезка
        t = Math.max(0, Math.min(1, t));
        return p.distanceTo(new Point(a.x() + t * dx, a.y() + t * dy));
    }

     //общая точка, Т-образное касание, совпадение концов, параллельные отрезки,
     //коллинеарные с перекрытием, касанием концами или без общих точек

    public static SegmentIntersection intersect(Segment s1, Segment s2) {
        Point p = s1.a(), q = s2.a();
        double rx = s1.b().x() - p.x(), ry = s1.b().y() - p.y();
        double sx = s2.b().x() - q.x(), sy = s2.b().y() - q.y();
        double lenR = Math.hypot(rx, ry), lenS = Math.hypot(sx, sy);
        double rxs = rx * sy - ry * sx;
        double qpx = q.x() - p.x(), qpy = q.y() - p.y();
        double qpxr = qpx * ry - qpy * rx;

        if (Math.abs(rxs) <= EPS * lenR * lenS) {            // параллельны
            if (Math.abs(qpxr) / lenR > EPS) return new SegmentIntersection.Empty();
            double rr = rx * rx + ry * ry;                    // коллинеарны
            double t0 = (qpx * rx + qpy * ry) / rr;
            double t1 = t0 + (sx * rx + sy * ry) / rr;
            double lo = Math.max(0, Math.min(t0, t1));
            double hi = Math.min(1, Math.max(t0, t1));
            double tol = EPS / lenR;
            //какая часть 2 отрезка попадает внутрь 1
            if (lo > hi + tol) return new SegmentIntersection.Empty();
            if (hi - lo <= tol) return new SegmentIntersection.AtPoint(at(p, rx, ry, lo));
            return new SegmentIntersection.Overlap(new Segment(at(p, rx, ry, lo), at(p, rx, ry, hi)));
        }
        //не параллельны
        double t = (qpx * sy - qpy * sx) / rxs;
        double u = qpxr / rxs;
        double tolT = EPS / lenR, tolU = EPS / lenS;
        if (t < -tolT || t > 1 + tolT || u < -tolU || u > 1 + tolU) return new SegmentIntersection.Empty();
        return new SegmentIntersection.AtPoint(at(p, rx, ry, Math.max(0, Math.min(1, t))));
    }

    private static Point at(Point p, double rx, double ry, double t) {
        return new Point(p.x() + rx * t, p.y() + ry * t);
    }

    //алгоритм Эдрю
    public static List<Point> convexHull(Collection<Point> points) {
        Objects.requireNonNull(points, "points");
        List<Point> pts = points.stream().distinct()
                .sorted(Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y)).toList();
        if (pts.isEmpty()) throw new GeometryException("Нельзя построить оболочку пустого набора точек");
        if (pts.size() <= 2) return pts;

        //2 точки + новая точка дают верный поворот
        List<Point> hull = new ArrayList<>();
        for (Point p : pts) {
            while (hull.size() >= 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= EPS) {
                hull.remove(hull.size() - 1);
            }
            hull.add(p);
        }
        int lower = hull.size() + 1;
        for (int i = pts.size() - 2; i >= 0; i--) {
            Point p = pts.get(i);
            while (hull.size() >= lower && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= EPS) {
                hull.remove(hull.size() - 1);
            }
            hull.add(p);
        }
        hull.remove(hull.size() - 1);
        return List.copyOf(hull);
    }

    //возврат не списка, а фигуры
    public static Shape convexHullShape(Collection<Point> points) {
        List<Point> hull = convexHull(points);
        return switch (hull.size()) {
            case 1 -> hull.get(0);
            case 2 -> new Segment(hull.get(0), hull.get(1));
            default -> new Polygon(hull);
        };
    }
}
