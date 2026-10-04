package figures;
//геометр искл мои
public class GeometryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    //описание ошибки
    public GeometryException(String message) {
        super(message);
    }
}
