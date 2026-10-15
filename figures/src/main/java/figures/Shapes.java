package figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
/**
 * Вспомогательный класс для работы с геометрическими фигурами.
 * Позволяет определять тип фигуры, выводить её описание,
 * раскрывать группы фигур, определять глубину вложенности
 * и подсчитывать количество фигур каждого типа.
 */
public final class Shapes {

    private Shapes() { }

    //switch проверяет что находится внутри Shape
    public static ShapeKind kindOf(Shape s) {
        return switch (s) {
            case Point p -> ShapeKind.POINT;
            case Segment sg -> ShapeKind.SEGMENT;
            case Circle c -> ShapeKind.CIRCLE;
            case Rectangle r -> ShapeKind.RECTANGLE;
            case Polygon pg -> ShapeKind.POLYGON;
            case Group g -> ShapeKind.GROUP;
        };
    }

    //текстовое описание фигуры
    public static String describe(Shape s) {
        return describe(s, 0);
    }

    private static String describe(Shape s, int level) {
        String pad = "  ".repeat(level);
        return switch (s) {
            case Point(var x, var y) -> pad + "Точка (" + f(x) + "; " + f(y) + ")";
            case Segment(Point(var x1, var y1), Point(var x2, var y2)) ->
                    pad + "Отрезок (" + f(x1) + "; " + f(y1) + ") – (" + f(x2) + "; " + f(y2) + ")";
            case Circle(Point(var cx, var cy), var r) ->
                    pad + "Окружность центр (" + f(cx) + "; " + f(cy) + "), r=" + f(r);
            case Rectangle r when r.angle() == 0 ->
                    pad + "Прямоугольник " + f(r.width()) + "×" + f(r.height()) + " центр " + f(r.center().x()) + "; " + f(r.center().y());
            case Rectangle r ->
                    pad + "Прямоугольник " + f(r.width()) + "×" + f(r.height()) + " повёрнут на " + f(Math.toDegrees(r.angle())) + "°";
            case Polygon(var vs) -> pad + "Многоугольник, вершин: " + vs.size();
            case Group(var children) -> {
                var sb = new StringBuilder(pad + "Группа из " + children.size() + " фигур:");
                for (Shape c : children) sb.append('\n').append(describe(c, level + 1));
                yield sb.toString();
            }
        };
    }

    //раскрытие группы до опр фигуры
    public static List<Shape> flatten(Shape s) {
        List<Shape> out = new ArrayList<>();
        collect(s, out);
        return List.copyOf(out);
    }

    //собираем фигуры внутри группы
    private static void collect(Shape s, List<Shape> out) {
        switch (s) {
            case Group(var children) -> children.forEach(c -> collect(c, out));
            case Point p -> out.add(p);
            case Segment sg -> out.add(sg);
            case Circle c -> out.add(c);
            case Rectangle r -> out.add(r);
            case Polygon pg -> out.add(pg);
        }
    }

    //насколько глубоко вложены группы
    public static int depth(Shape s) {
        return switch (s) {
            case Group(var children) -> 1 + children.stream().mapToInt(Shapes::depth).max().orElse(0);
            case Point p -> 0;
            case Segment sg -> 0;
            case Circle c -> 0;
            case Rectangle r -> 0;
            case Polygon pg -> 0;
        };
    }

    //подсчет по типам
    public static Map<ShapeKind, Long> countByKind(Shape s) {
        Map<ShapeKind, Long> m = new EnumMap<>(ShapeKind.class);
        for (Shape leaf : flatten(s)) m.merge(kindOf(leaf), 1L, Long::sum);
        return Collections.unmodifiableMap(m);
    }

    //вывод красивого числа
    private static String f(double v) {
        if (Math.abs(v) < 0.005) v = 0;
        return String.format(java.util.Locale.ROOT, "%.2f", v);
    }
}
