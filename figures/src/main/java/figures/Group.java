package figures;

import java.util.List;
import java.util.Objects;

//делает неизменяемую копию списка
public record Group(List<Shape> children) implements Shape {

    public Group {
        children = List.copyOf(Objects.requireNonNull(children, "children"));
    }


    public static Group of(Shape... shapes) {
        return new Group(List.of(shapes));
    }

    //сумма периметров вложенных фигур
    @Override public double perimeter()
    { return children.stream().mapToDouble(Shape::perimeter).sum(); }

    //сумма площадей (наложения учитываются дважды)
    @Override public double area()
    { return children.stream().mapToDouble(Shape::area).sum(); }

    //если группа пуста
    @Override
    public BoundingBox boundingBox() {
        return children.stream().map(Shape::boundingBox).reduce(BoundingBox::union)
                .orElseThrow(() -> new GeometryException("У пустой группы нет ограничивающего прямоугольника"));
    }

    //сдвиг
    @Override public Group translate(double dx, double dy)
    { return new Group(children.stream().map(s -> s.translate(dx, dy)).toList()); }

    //поворот
    @Override public Group rotate(double angle, Point pivot)
    { return new Group(children.stream().map(s -> s.rotate(angle, pivot)).toList()); }

    //маштаб относительно указанного центра
    @Override
    public Group scale(double factor, Point center) {
        Geometry.requireFactor(factor);
        return new Group(children.stream().map(s -> s.scale(factor, center)).toList());
    }

    //смотрим точку
    @Override public boolean contains(Point p)
    { return children.stream().anyMatch(s -> s.contains(p)); }
}