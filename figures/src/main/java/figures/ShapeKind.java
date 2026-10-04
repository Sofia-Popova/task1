package figures;

//какой тип фигуры
public enum ShapeKind {
    //сколько измерений для описания объекта
    POINT("точка", 0),
    SEGMENT("отрезок", 1),
    CIRCLE("окружность", 2),
    RECTANGLE("прямоугольник", 2),
    POLYGON("многоугольник", 2),
    GROUP("группа", -1) {
        @Override
        public String dimensionLabel() {
            return "составная";
        }
    };

    private final String title; //константа - название
    private final int dimension; //хранит число

    ShapeKind(String title, int dimension) {
        this.title = title;
        this.dimension = dimension;
    }

    //сохраняем в поля
    public String title() {
        return title;
    }

    public int dimension() {
        return dimension;
    }

    public String dimensionLabel() {
        return dimension + "D";
    }
}
