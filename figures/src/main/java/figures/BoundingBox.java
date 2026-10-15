package figures;
/**
 * Ограничивающий прямоугольник, заданный минимальными
 * и максимальными координатами по осям X и Y.
 * Позволяет вычислять ширину, высоту, площадь и центр прямоугольника,
 * объединять два прямоугольника и проверять принадлежность точки
 * прямоугольнику с учётом допустимой погрешности
 */

//ограничивающий прямоугольник
public record BoundingBox(double minX, double minY, double maxX, double maxY) {
    //нет ли ошибок
    public BoundingBox {
        if (!(minX <= maxX) || !(minY <= maxY)) {
            throw new GeometryException("Некорректные границы min должен быть <= max");
        }
    }

    //ширина
    public double width() {
        return maxX - minX;
    }

    //высота
    public double height() {
        return maxY - minY;
    }

    //площадь
    public double area() {
        return width() * height();
    }

    //центр
    public Point center() {
        return new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }

    //возьми 2 BoundingBoxи создай, который содержит оба
    public BoundingBox union(BoundingBox o) {
        return new BoundingBox(Math.min(minX, o.minX), Math.min(minY, o.minY),
                Math.max(maxX, o.maxX), Math.max(maxY, o.maxY));
    }

    //находится ли точка внутри
    public boolean contains(Point p) {
        double e = Geometry.EPS;
        return p.x() >= minX - e && p.x() <= maxX + e && p.y() >= minY - e && p.y() <= maxY + e;
    }
}
