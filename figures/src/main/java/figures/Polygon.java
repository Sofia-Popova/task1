package figures;

import java.util.List;
/**
 * Класс многоугольника, который хранит список своих вершин.
 * Проверяет правильность многоугольника, вычисляет его площадь,
 * периметр и ограничивающий прямоугольник, позволяет перемещать,
 * поворачивать и масштабировать его, а также проверять,
 * находится ли точка внутри многоугольника.
 */
public record Polygon(List<Point> vertices) implements Shape {

    //Копирует вершины и проверяет, что многоугольник существует
    public Polygon {
        vertices = List.copyOf(vertices);
        int n = vertices.size();
        if (n < 3) {
            throw new GeometryException("Многоугольник требует минимум 3 вершины, получено " + n);
        }
        for (int i = 0; i < n; i++) {
            if (vertices.get(i).distanceTo(vertices.get((i + 1) % n)) <= Geometry.EPS) {
                throw new GeometryException("Соседние вершины совпадают: " + vertices.get(i));
            }
        }
        if (Math.abs(signedArea(vertices)) <= Geometry.EPS) {
            throw new GeometryException("Многоугольник неправильный: нулевая площадь");
        }
    }

    //промежуточная величина, которая зависит от направления обхода
    private static double signedArea(List<Point> v) {
        double s = 0;
        for (int i = 0; i < v.size(); i++) {
            Point a = v.get(i), b = v.get((i + 1) % v.size());
            s += a.x() * b.y() - b.x() * a.y();
        }
        return s / 2;
    }

    @Override
    public double perimeter() {
        double sum = 0;
        for (int i = 0; i < vertices.size(); i++) {
            sum += vertices.get(i).distanceTo(vertices.get((i + 1) % vertices.size()));
        }
        return sum;
    }

    //площадь (метод Гаусса)
    @Override public double area() { return Math.abs(signedArea(vertices)); }

    //ищем min max
    @Override
    public BoundingBox boundingBox() {
        double minX = Double.POSITIVE_INFINITY, minY = minX, maxX = Double.NEGATIVE_INFINITY, maxY = maxX;
        for (Point p : vertices) {
            minX = Math.min(minX, p.x());
            maxX = Math.max(maxX, p.x());
            minY = Math.min(minY, p.y());
            maxY = Math.max(maxY, p.y());
        }
        return new BoundingBox(minX, minY, maxX, maxY);
    }

    //сдвиг
    @Override public Polygon translate(double dx, double dy)
    { return new Polygon(vertices.stream().map(p -> p.translate(dx, dy)).toList()); }

    //поворот
    @Override public Polygon rotate(double angle, Point pivot)
    { return new Polygon(vertices.stream().map(p -> p.rotate(angle, pivot)).toList()); }

    //маштабируем
    @Override
    public Polygon scale(double factor, Point center) {
        Geometry.requireFactor(factor);
        return new Polygon(vertices.stream().map(p -> p.scale(factor, center)).toList());
    }

    //находится ли точка внутри класса
    @Override
    public boolean contains(Point p) {
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            if (Geometry.distanceToSegment(p, vertices.get(i), vertices.get((i + 1) % n)) <= Geometry.EPS) {
                return true;
            }
        }
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point a = vertices.get(i), b = vertices.get(j);
            if ((a.y() > p.y()) != (b.y() > p.y())) {
                double xCross = (b.x() - a.x()) * (p.y() - a.y()) / (b.y() - a.y()) + a.x();
                if (p.x() < xCross) inside = !inside;
            }
        }
        return inside;
    }
}
