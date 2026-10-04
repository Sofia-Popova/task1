package figures;

//Результат пересечения двух отрезков
public sealed interface SegmentIntersection {

    //Отрезки не имеют общих точек
    record Empty() implements SegmentIntersection { }

    //пересечение в 1 точке
    record AtPoint(Point point) implements SegmentIntersection { }

    //частично совпадают
    record Overlap(Segment segment) implements SegmentIntersection { }
}
