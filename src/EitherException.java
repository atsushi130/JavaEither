/**
 * Created by Atsushi Miyake on 2017/06/23.
 */
public class EitherException extends Exception {

    String message = "Eitherのサンプル検査例外！";

    public EitherException() {}

    public EitherException(Exception excetion) {
        this.initCause(excetion);
    }

    public String getMessage() {
        return this.message;
    }
}
