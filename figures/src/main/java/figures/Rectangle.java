package figures;

import java.util.List;
import java.util.Objects;

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
}
