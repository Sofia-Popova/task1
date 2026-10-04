package figures;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /**
         * Создаём фигуры
         * Кладём всё в один список
         * Периметр и площадь(вычисляем)
         * Ограничивающий прямоугольник (создаем)
         * создаем свое исключение
         */

        Point p1 = new Point(1, 1);
        Point p2 = new Point(5, 3);
        Segment seg1 = new Segment(new Point(0, 0), new Point(4, 4));
        Segment seg2 = new Segment(new Point(0, 4), new Point(4, 0));
        Circle circle1 = new Circle(new Point(0, 0), 5);
        Circle circle2 = new Circle(new Point(10, 10), 2);
        Rectangle rect1 = Rectangle.axisAligned(0, 0, 4, 2);
        Rectangle rect2 = new Rectangle(new Point(8, 3), 6, 2, Math.PI / 6);
        Polygon triangle = new Polygon(List.of(new Point(0, 0), new Point(4, 0), new Point(0, 3)));
        Polygon square = new Polygon(List.of(new Point(0, 0), new Point(2, 0), new Point(2, 2), new Point(0, 2)));

        Group group1 = Group.of(p1, seg1, circle2);
        Group group2 = Group.of(group1, rect1, triangle);   // группа внутри группы

        List<Shape> all = new ArrayList<>(List.of(p1, p2, seg1, seg2, circle1, circle2,
                rect1, rect2, triangle, square, group1, group2));
        for (int i = 1; i <= 7; i++) {
            all.add(new Point(i, -i));
            all.add(new Circle(new Point(i, i), i));
            all.add(Rectangle.axisAligned(i, 0, 2, i));
        }
        System.out.println("Всего фигур: " + all.size());

        System.out.println("\n--- Периметр и площадь ---");
        for (Shape shape : all) {
            System.out.println(Shapes.kindOf(shape).title()
                    + ": периметр " + round(shape.perimeter())
                    + ", площадь " + round(shape.area()));
        }

        System.out.println("\n--- Ограничивающий прямоугольник ---");
        System.out.println("Окружность: " + circle1.boundingBox());
        System.out.println("Повёрнутый прямоугольник: " + rect2.boundingBox());
        System.out.println("Вложенная группа: " + group2.boundingBox());

        System.out.println("\n--- Сдвиг, поворот, масштабирование ---");
        System.out.println("Было:    " + Shapes.describe(rect1));
        System.out.println("Сдвиг:   " + Shapes.describe(rect1.translate(3, 1)));
        System.out.println("Поворот: " + Shapes.describe(rect1.rotate(Math.PI / 2)));
        System.out.println("Масштаб: " + Shapes.describe(rect1.scale(2)));

        System.out.println("\n--- Принадлежность точки ---");
        Point inside = new Point(1, 1);
        Point outside = new Point(20, 20);
        System.out.println("Точка (1; 1) в круге: " + circle1.contains(inside));
        System.out.println("Точка (20; 20) в круге: " + circle1.contains(outside));
        System.out.println("Точка (1; 1) в треугольнике: " + triangle.contains(inside));
        System.out.println("Точка (1; 1) во вложенной группе: " + group2.contains(inside));

        System.out.println("\n--- Пересечение отрезков ---");
        Segment overlap = new Segment(new Point(2, 2), new Point(6, 6));
        Segment parallel = new Segment(new Point(0, 1), new Point(4, 5));
        System.out.println("Крест: " + Geometry.intersect(seg1, seg2));
        System.out.println("Наложение: " + Geometry.intersect(seg1, overlap));
        System.out.println("Параллельные: " + Geometry.intersect(seg1, parallel));

        System.out.println("\n--- Выпуклая оболочка ---");
        List<Point> points = List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4),
                new Point(0, 4), new Point(2, 2), new Point(1, 3));
        System.out.println(Geometry.convexHull(points));

        System.out.println("\n--- Исключение ---");
        try {
            new Circle(new Point(0, 0), -1);
        } catch (GeometryException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Округляет число до двух знаков после запятой, чтобы вывод был короче
    private static double round(double x) {
        return Math.round(x * 100) / 100.0;
    }
}
