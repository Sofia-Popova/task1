package figures;
//Все фигуры неизменяемы, преобразования возвращают новые объекты
//берем набор правил, которые должны соответствовать фигуре
public sealed interface Shape permits Point, Segment, Circle, Rectangle, Polygon, Group {

    double perimeter();

    double area();
    //должна построить свой ограничивающий прямоугольник
    BoundingBox boundingBox();

    Shape translate(double dx, double dy);

    Shape rotate(double angle, Point pivot);

    Shape scale(double factor, Point center);

    boolean contains(Point p);

    default Shape rotate(double angle) {
        return rotate(angle, Point.ORIGIN);
    }

    default Shape scale(double factor) {
        return scale(factor, Point.ORIGIN);
    }
}
