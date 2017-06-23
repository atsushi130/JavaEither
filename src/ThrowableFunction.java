
/**
 * Created by Atsushi Miyake on 2017/06/23.
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {

    R apply(T t) throws E;

    static <T, E extends Exception> ThrowableFunction<T, T, E> identity() {
        return t -> t;
    }
}
