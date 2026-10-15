package figures;

import java.util.List;
import java.util.Objects;

/**
 * Класс прямоугольника с заданным центром, шириной, высотой и углом поворота.
 * Проверяет правильность заданных значений, позволяет создавать прямоугольник
 * со сторонами, параллельными осям координат, и получать координаты его вершин.
 */
public record Rectangle(Point center, double width, double height, double angle)
        implements Shape {

    public Rectangle {
        Objects.requireNonNull(center, "center");
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new GeometryException("Стороны прямоугольника должны быть положительными: " + width + " x " + height);
        }
        if (!Double.isFinite(angle)) {
            throw new GeometryException("Угол должен быть конечным числом");
        }
        angle = angle % Math.PI;
        if (angle < 0) angle += Math.PI;
        if (angle < Geometry.EPS || Math.PI - angle < Geometry.EPS) angle = 0.0;
    }

    //стороны параллельно осям
    public static Rectangle axisAligned(double x, double y, double width, double height) {
        return new Rectangle(new Point(x + width / 2, y + height / 2), width, height, 0);
    }

    //против часовой стрелки
    public List<Point> corners() {
        double hx = width / 2, hy = height / 2;
        return List.of(
                new Point(center.x() - hx, center.y() - hy).rotate(angle, center),
                new Point(center.x() + hx, center.y() - hy).rotate(angle, center),
                new Point(center.x() + hx, center.y() + hy).rotate(angle, center),
                new Point(center.x() - hx, center.y() + hy).rotate(angle, center));
    }
    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public BoundingBox boundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Point p : corners()) {
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }

        return new BoundingBox(minX, minY, maxX, maxY);
    }

    @Override
    public Rectangle translate(double dx, double dy) {
        return new Rectangle(center.translate(dx, dy), width, height, angle);
    }

    @Override
    public Rectangle rotate(double angle, Point pivot) {
        return new Rectangle(center.rotate(angle, pivot), width, height, this.angle + angle);
    }

    @Override
    public Rectangle scale(double factor, Point center) {
        Geometry.requireFactor(factor);
        return new Rectangle(
                this.center.scale(factor, center),
                width * factor,
                height * factor,
                angle
        );
    }

    @Override
    public boolean contains(Point p) {
        Point local = p.rotate(-angle, center);

        return local.x() >= center.x() - width / 2 - Geometry.EPS
                && local.x() <= center.x() + width / 2 + Geometry.EPS
                && local.y() >= center.y() - height / 2 - Geometry.EPS
                && local.y() <= center.y() + height / 2 + Geometry.EPS;
    }
}
