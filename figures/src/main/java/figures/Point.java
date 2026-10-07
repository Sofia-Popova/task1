package figures;

public record Point(double x, double y) implements Shape {

    public static final Point ORIGIN = new Point(0, 0); //готовая точка

    public Point {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new GeometryException("Координаты точки должны быть конечными: (" + x + "; " + y + ")");
        }
        x += 0.0;
        y += 0.0;
    }

    //расстояние между точками по теореме пифагора
    public double distanceTo(Point o) {
        return Math.hypot(x - o.x, y - o.y);
    }

    //P и S
    @Override public double perimeter() { return 0; }

    @Override public double area() { return 0; }

    @Override public BoundingBox boundingBox() { return new BoundingBox(x, y, x, y); }

    //сдвиг точки
    @Override public Point translate(double dx, double dy) { return new Point(x + dx, y + dy); }

    //поворот точки
    @Override
    public Point rotate(double angle, Point pivot) {
        double c = Math.cos(angle), s = Math.sin(angle);
        double dx = x - pivot.x, dy = y - pivot.y;
        return new Point(pivot.x + dx * c - dy * s, pivot.y + dx * s + dy * c);
    }

    //маштабирование точки
    @Override
    public Point scale(double factor, Point center) {
        Geometry.requireFactor(factor);
        return new Point(center.x + (x - center.x) * factor, center.y + (y - center.y) * factor);
    }

    @Override public boolean contains(Point p) { return distanceTo(p) <= Geometry.EPS; }
