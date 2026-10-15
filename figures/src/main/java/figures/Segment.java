package figures;

import java.util.Objects;
/**
 * Класс отрезка, заданного двумя точками.
 * Позволяет находить длину отрезка, определять ограничивающий прямоугольник,
 * перемещать, поворачивать и масштабировать отрезок,
 * а также проверять, находится ли точка на отрезке.
 */
public record Segment(Point a, Point b) implements Shape {

    //проверяем концы
    public Segment {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (a.distanceTo(b) <= Geometry.EPS) {
            throw new GeometryException("концы совпадают " + a);
        }
    }

    //длина отрезка
    public double length() { return a.distanceTo(b); }

    //P и S
    @Override public double perimeter() { return length(); }

    @Override public double area() { return 0; }

    //минимальный прямоугольник, который полностью содержит фигуру
    @Override
    public BoundingBox boundingBox() {
        return new BoundingBox(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()),
                Math.max(a.x(), b.x()), Math.max(a.y(), b.y()));
    }

    //сдвиг
    @Override public Segment translate(double dx, double dy)
    { return new Segment(a.translate(dx, dy), b.translate(dx, dy)); }

    //ворот
    @Override public Segment rotate(double angle, Point pivot)
    { return new Segment(a.rotate(angle, pivot), b.rotate(angle, pivot)); }

    //маштабирование
    @Override public Segment scale(double factor, Point center)
    { return new Segment(a.scale(factor, center), b.scale(factor, center)); }

    //находится ли точка на этом отрезке
    @Override public boolean contains(Point p)
    { return Geometry.distanceToSegment(p, a, b) <= Geometry.EPS; }
}
