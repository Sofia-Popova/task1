package figures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShapeTest {

    private static final double D = 1e-9;

    private static Polygon square() {
        return new Polygon(List.of(new Point(0, 0), new Point(2, 0), new Point(2, 2), new Point(0, 2)));
    }

    private static Polygon lShape() {
        return new Polygon(List.of(new Point(0, 0), new Point(3, 0), new Point(3, 1),
                new Point(1, 1), new Point(1, 3), new Point(0, 3)));
    }

    @Test
    void pointEqualsHashCodeContract() {
        var a = new Point(1, 2);
        var b = new Point(1, 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Point(2, 1));
        assertEquals(new Point(0.0, 0.0), new Point(-0.0, -0.0));
        assertEquals(new Point(0.0, 0.0).hashCode(), new Point(-0.0, -0.0).hashCode());
    }

    @Test
    void pointRejectsNonFiniteCoordinates() {
        assertThrows(GeometryException.class, () -> new Point(Double.NaN, 0));
        assertThrows(GeometryException.class, () -> new Point(0, Double.POSITIVE_INFINITY));
    }

    @Test
    void circleMetrics() {
        var c = new Circle(new Point(1, 1), 2);
        assertEquals(4 * Math.PI, c.perimeter(), D);
        assertEquals(4 * Math.PI, c.area(), D);
        assertEquals(new BoundingBox(-1, -1, 3, 3), c.boundingBox());
        assertTrue(c.contains(new Point(3, 1)));          // граница
        assertFalse(c.contains(new Point(3.1, 1)));
    }

    @Test
    void circleRejectsNonPositiveRadius() {
        assertThrows(GeometryException.class, () -> new Circle(Point.ORIGIN, 0));
        assertThrows(GeometryException.class, () -> new Circle(Point.ORIGIN, -1));
        assertThrows(NullPointerException.class, () -> new Circle(null, 1));
    }

    @Test
    void rectangleMetricsAndContains() {
        var r = Rectangle.axisAligned(0, 0, 4, 2);
        assertEquals(12, r.perimeter(), D);
        assertEquals(8, r.area(), D);
        assertTrue(r.contains(new Point(4, 2)));
        assertFalse(r.contains(new Point(4.01, 1)));
        assertThrows(GeometryException.class, () -> Rectangle.axisAligned(0, 0, 0, 2));
    }

    @Test
    void rotatedRectangleContainsAndBoundingBox() {
        var r = new Rectangle(Point.ORIGIN, 4, 2, 0).rotate(Math.PI / 2, Point.ORIGIN);
        assertTrue(r.contains(new Point(0.5, 1.9)));
        assertFalse(r.contains(new Point(1.5, 0)));
        var bb = r.boundingBox();
        assertEquals(2, bb.width(), 1e-9);
        assertEquals(4, bb.height(), 1e-9);
    }

    @Test
    void rectangleAngleNormalizationKeepsEquality() {
        var a = new Rectangle(new Point(1, 1), 3, 2, 0);
        var b = new Rectangle(new Point(1, 1), 3, 2, Math.PI);
        var c = new Rectangle(new Point(1, 1), 3, 2, -Math.PI);
        assertEquals(a, b);
        assertEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void polygonAreaAndPerimeter() {
        assertEquals(4, square().area(), D);
        assertEquals(8, square().perimeter(), D);
        assertEquals(5, lShape().area(), D);
    }

    @Test
    void polygonRejectsDegenerateInput() {
        assertThrows(GeometryException.class, () -> new Polygon(List.of(Point.ORIGIN, new Point(1, 1))));
        assertThrows(GeometryException.class,
                () -> new Polygon(List.of(Point.ORIGIN, new Point(1, 1), new Point(2, 2))));   // коллинеарные
        assertThrows(GeometryException.class,
                () -> new Polygon(List.of(Point.ORIGIN, Point.ORIGIN, new Point(1, 0), new Point(0, 1))));
    }

    @Test
    void concavePolygonContainsIncludingBoundary() {
        var l = lShape();
        assertTrue(l.contains(new Point(0.5, 2.5)));
        assertTrue(l.contains(new Point(2.5, 0.5)));
        assertFalse(l.contains(new Point(2, 2)));         // «вырез» буквы Г
        assertTrue(l.contains(new Point(1, 1)));          // вогнутая вершина
        assertTrue(l.contains(new Point(0, 0)));          // вершина
        assertTrue(l.contains(new Point(1.5, 1)));        // ребро
    }

    @Test
    void polygonDefensivelyCopiesVertices() {
        var src = new ArrayList<>(List.of(new Point(0, 0), new Point(2, 0), new Point(0, 2)));
        var p = new Polygon(src);
        src.add(new Point(5, 5));
        assertEquals(3, p.vertices().size());
        assertThrows(UnsupportedOperationException.class, () -> p.vertices().add(Point.ORIGIN));
    }

    @Test
    void segmentValidationAndContains() {
        assertThrows(GeometryException.class, () -> new Segment(new Point(1, 1), new Point(1, 1)));
        var s = new Segment(new Point(0, 0), new Point(4, 4));
        assertEquals(Math.sqrt(32), s.length(), D);
        assertTrue(s.contains(new Point(2, 2)));
        assertTrue(s.contains(new Point(4, 4)));
        assertFalse(s.contains(new Point(5, 5)));          // на прямой, но вне отрезка
    }

    @Test
    void groupSumsMetricsAndMergesBoundingBoxes() {
        var inner = Group.of(new Circle(new Point(10, 10), 1), new Point(-5, 0));
        var g = Group.of(square(), inner);
        assertEquals(4 + Math.PI, g.area(), D);
        assertEquals(8 + 2 * Math.PI, g.perimeter(), D);
        assertEquals(new BoundingBox(-5, 0, 11, 11), g.boundingBox());
        assertTrue(g.contains(new Point(10, 10.5)));
        assertFalse(g.contains(new Point(5, 5)));
    }

    @Test
    void emptyGroupBehaviour() {
        var g = Group.of();
        assertEquals(0, g.area(), D);
        assertEquals(0, g.perimeter(), D);
        assertFalse(g.contains(Point.ORIGIN));
        assertThrows(GeometryException.class, g::boundingBox);
    }

    @Test
    void groupEqualsHashCodeAndImmutability() {
        var list = new ArrayList<Shape>(List.of(new Point(1, 1)));
        var g1 = new Group(list);
        var g2 = Group.of(new Point(1, 1));
        list.add(new Point(2, 2));
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertThrows(UnsupportedOperationException.class, () -> g1.children().clear());
    }

    @Test
    void rotationAndTranslationPreserveMetrics() {
        Shape s = lShape();
        Shape moved = s.rotate(0.7, new Point(3, -2)).translate(5, 5);
        assertEquals(s.area(), moved.area(), 1e-9);
        assertEquals(s.perimeter(), moved.perimeter(), 1e-9);
    }

    @Test
    void scaleMultipliesAreaByKSquared() {
        Shape s = Group.of(square(), new Circle(new Point(5, 5), 1), Rectangle.axisAligned(0, 0, 1, 3));
        Shape big = s.scale(3, new Point(1, 1));
        assertEquals(s.area() * 9, big.area(), 1e-9);
        assertEquals(s.perimeter() * 3, big.perimeter(), 1e-9);
    }

    @Test
    void scaleWithNonPositiveFactorThrowsForEveryKind() {
        List<Shape> all = List.of(new Point(1, 1), new Segment(Point.ORIGIN, new Point(1, 0)),
                new Circle(Point.ORIGIN, 1), Rectangle.axisAligned(0, 0, 1, 1), square(), Group.of());
        for (Shape s : all) {
            assertThrows(GeometryException.class, () -> s.scale(0), s.getClass().getSimpleName());
            assertThrows(GeometryException.class, () -> s.scale(-2), s.getClass().getSimpleName());
        }
    }

    @Test
    void rotationOfPointAroundPivot() {
        var p = new Point(2, 1).rotate(Math.PI / 2, new Point(1, 1));
        assertEquals(1, p.x(), 1e-9);
        assertEquals(2, p.y(), 1e-9);
    }

    @Test
    void shapesUtilityDispatchesWithPatternMatching() {
        var city = Group.of(Group.of(new Point(0, 0), square()), new Circle(Point.ORIGIN, 1), Group.of());
        assertEquals(ShapeKind.GROUP, Shapes.kindOf(city));
        assertEquals(ShapeKind.CIRCLE, Shapes.kindOf(new Circle(Point.ORIGIN, 1)));
        assertEquals(3, Shapes.flatten(city).size());
        assertEquals(2, Shapes.depth(city));
        assertEquals(0, Shapes.depth(new Point(1, 1)));
        var counts = Shapes.countByKind(city);
        assertEquals(1L, counts.get(ShapeKind.POINT).longValue());
        assertEquals(1L, counts.get(ShapeKind.POLYGON).longValue());
        assertNull(counts.get(ShapeKind.SEGMENT));
        assertTrue(Shapes.describe(city).contains("Группа из 3 фигур"));
        assertTrue(Shapes.describe(new Rectangle(Point.ORIGIN, 2, 1, 0.5)).contains("повёрнут"));
    }

    @Test
    void shapeKindEnumFieldsAndConstantBody() {
        assertEquals("окружность", ShapeKind.CIRCLE.title());
        assertEquals("2D", ShapeKind.POLYGON.dimensionLabel());
        assertEquals("составная", ShapeKind.GROUP.dimensionLabel());
    }

    @Test
    void boundingBoxValidationAndUnion() {
        assertThrows(GeometryException.class, () -> new BoundingBox(1, 0, 0, 1));
        var u = new BoundingBox(0, 0, 1, 1).union(new BoundingBox(-1, 2, 0.5, 3));
        assertEquals(new BoundingBox(-1, 0, 1, 3), u);
        assertTrue(u.contains(new Point(1, 3)));
    }
}
