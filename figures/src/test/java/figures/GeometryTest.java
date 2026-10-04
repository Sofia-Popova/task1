package figures;

import static org.junit.jupiter.api.Assertions.*;

import figures.SegmentIntersection.AtPoint;
import figures.SegmentIntersection.Empty;
import figures.SegmentIntersection.Overlap;
import java.util.List;
import org.junit.jupiter.api.Test;

class GeometryTest {

    private static Segment seg(double x1, double y1, double x2, double y2) {
        return new Segment(new Point(x1, y1), new Point(x2, y2));
    }

    @Test
    void crossingSegmentsMeetAtPoint() {
        assertEquals(new AtPoint(new Point(2, 2)), Geometry.intersect(seg(0, 0, 4, 4), seg(0, 4, 4, 0)));
    }

    @Test
    void sharedEndpointIsSinglePoint() {
        assertEquals(new AtPoint(new Point(4, 4)), Geometry.intersect(seg(0, 0, 4, 4), seg(4, 4, 6, 1)));
    }

    @Test
    void tJunctionIsSinglePoint() {
        assertEquals(new AtPoint(new Point(2, 2)), Geometry.intersect(seg(0, 0, 4, 4), seg(2, 2, 5, 0)));
    }

    @Test
    void parallelSegmentsDoNotIntersect() {
        assertInstanceOf(Empty.class, Geometry.intersect(seg(0, 0, 4, 4), seg(0, 1, 4, 5)));
    }

    @Test
    void collinearOverlapGivesSharedPart() {
        assertEquals(new Overlap(seg(2, 2, 4, 4)), Geometry.intersect(seg(0, 0, 4, 4), seg(2, 2, 6, 6)));
        assertEquals(new Overlap(seg(1, 1, 3, 3)), Geometry.intersect(seg(0, 0, 4, 4), seg(3, 3, 1, 1)));   // противоположное направление
        assertEquals(new Overlap(seg(1, 1, 2, 2)), Geometry.intersect(seg(0, 0, 4, 4), seg(1, 1, 2, 2)));   // вложенный
        assertEquals(new Overlap(seg(0, 0, 4, 4)), Geometry.intersect(seg(0, 0, 4, 4), seg(0, 0, 4, 4)));   // совпадают
    }

    @Test
    void collinearTouchingAtEndIsPoint() {
        assertEquals(new AtPoint(new Point(4, 4)), Geometry.intersect(seg(0, 0, 4, 4), seg(4, 4, 7, 7)));
    }

    @Test
    void collinearDisjointIsEmpty() {
        assertInstanceOf(Empty.class, Geometry.intersect(seg(0, 0, 4, 4), seg(5, 5, 8, 8)));
    }

    @Test
    void nonParallelButNotReachingIsEmpty() {
        assertInstanceOf(Empty.class, Geometry.intersect(seg(0, 0, 4, 4), seg(0, 5, 1, 3)));
        assertInstanceOf(Empty.class, Geometry.intersect(seg(0, 0, 1, 0), seg(2, -1, 2, 1)));   // прямые пересекаются вне отрезков
    }

    @Test
    void intersectionIsSymmetric() {
        var a = seg(0, 0, 4, 4);
        var b = seg(2, 2, 5, 0);
        assertEquals(Geometry.intersect(a, b), Geometry.intersect(b, a));
    }

    @Test
    void orientationDetectsAllThreeCases() {
        assertEquals(Orientation.COUNTERCLOCKWISE, Geometry.orientation(Point.ORIGIN, new Point(1, 0), new Point(1, 1)));
        assertEquals(Orientation.CLOCKWISE, Geometry.orientation(Point.ORIGIN, new Point(1, 0), new Point(1, -1)));
        assertEquals(Orientation.COLLINEAR, Geometry.orientation(Point.ORIGIN, new Point(1, 1), new Point(5, 5)));
    }

    @Test
    void convexHullOfSquareWithInteriorPoints() {
        var pts = List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4),
                new Point(2, 2), new Point(1, 1), new Point(4, 4));
        assertEquals(List.of(new Point(0, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4)),
                Geometry.convexHull(pts));
    }

    @Test
    void convexHullExcludesPointsOnEdges() {
        var pts = List.of(new Point(0, 0), new Point(2, 0), new Point(4, 0), new Point(4, 4), new Point(0, 4));
        assertEquals(4, Geometry.convexHull(pts).size());
    }

    @Test
    void convexHullDegenerateInputs() {
        assertEquals(List.of(new Point(1, 1)), Geometry.convexHull(List.of(new Point(1, 1), new Point(1, 1))));
        var line = List.of(new Point(0, 0), new Point(2, 2), new Point(1, 1), new Point(3, 3));
        assertEquals(List.of(new Point(0, 0), new Point(3, 3)), Geometry.convexHull(line));
        assertInstanceOf(Segment.class, Geometry.convexHullShape(line));
        assertInstanceOf(Point.class, Geometry.convexHullShape(List.of(new Point(1, 1))));
        assertInstanceOf(Polygon.class, Geometry.convexHullShape(List.of(Point.ORIGIN, new Point(1, 0), new Point(0, 1))));
    }

    @Test
    void convexHullOfEmptySetThrows() {
        assertThrows(GeometryException.class, () -> Geometry.convexHull(List.of()));
    }

    @Test
    void hullContainsEveryInputPoint() {
        var rnd = new java.util.Random(7);
        var pts = new java.util.ArrayList<Point>();
        for (int i = 0; i < 200; i++) pts.add(new Point(rnd.nextInt(50), rnd.nextInt(50)));
        Shape hull = Geometry.convexHullShape(pts);
        assertTrue(pts.stream().allMatch(hull::contains));
    }
}
