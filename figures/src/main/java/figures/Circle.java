package figures;

import java.util.Objects;

public record Circle(Point center, double radius) implements Shape {

    //проверка центра и радиуса
    public Circle {
        Objects.requireNonNull(center, "center");
        if (!Double.isFinite(radius) || radius <= 0) {
            throw new GeometryException("Радиус должен быть положительным: " + radius);
        }
    }

    //Р
    @Override public double perimeter() { return 2 * Math.PI * radius; }

    //Площадь
    @Override public double area() { return Math.PI * radius * radius; }

    @Override
    public BoundingBox boundingBox() {
        return new BoundingBox(center.x() - radius, center.y() - radius, center.x() + radius, center.y() + radius);
    }

    //сдвиг круга
    @Override public Circle translate(double dx, double dy)
    { return new Circle(center.translate(dx, dy), radius); }

    //поворот(поворачивается только центр)
    @Override public Circle rotate(double angle, Point pivot)
    { return new Circle(center.rotate(angle, pivot), radius); }

    //маштабирование
    @Override
    public Circle scale(double factor, Point c) {
        Geometry.requireFactor(factor);
        return new Circle(center.scale(factor, c), radius * factor);
    }

    //принадлежность точки
    @Override public boolean contains(Point p)
    { return center.distanceTo(p) <= radius + Geometry.EPS; }
}
