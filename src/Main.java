
/**
 * Created by Atsushi Miyake on 2017/06/23.
 */
public class Main {
    public static void main(String[] args) {

        final Either<EitherException, Integer> isException = Either.right(10);
        final String string1 = isException.map(value -> value * 2)
                .map(value -> value + 5)
                .map(value -> value - 8)
                .map(value -> value * generateException()) // 検査例外発生 (検査例外はラムダで扱えないのではなく Stream で扱えない)
                .map(value -> value / 2.0) // 例外発生を考慮せずそのまま中間操作
                .fold( // 失敗時と成功時の処理を Function のラムダ式で渡す
                    // 中間操作で例外が発生しているため、Left の処理が実行される
                    // Either<EitherException, Integer> で Left に型を指定しているため、型安全に Left 時の処理が可能
                    exception -> exception.getMessage(),
                    value     -> String.valueOf(value)
                );

        final Either<Exception, Integer> isNumber = Either.right(10);
        final String string2 = isNumber.map(value -> value * 2)
                .map(value -> value + 5)
                .map(value -> value - 8)
                .map(value -> value / 2.0)
                .fold(
                    exception -> exception.getMessage(),
                    value     -> String.valueOf(value) // 例外が発生していないため、Right の処理が実行される
                );

        final Either<Exception, Integer> isNested = Either.right(10);
        final String string3 = isNested.map(value -> {
                    // Either をネストして返す
                    final Either<Exception, Integer> either = Either.right(value * 2);
                    return either;
                })
                .flatMap(ThrowableFunction.identity()) // flatten する
                .map(value -> value + 5) // ここから以下は Either<Exception, Integer> 型
                .map(value -> value - 8)
                .map(value -> value / 2.0)
                .fold(
                    exception -> exception.getMessage(),
                    value     -> String.valueOf(value) // 例外が発生していないため、Right の処理が実行される
                );

        final Either<Exception, Integer> isVoid = Either.right(10);
        isVoid.map(value -> {
            // Either をネストして返す
            final Either<Exception, Integer> either = Either.right(value * 2);
            return either;
        }).flatMap(ThrowableFunction.identity()) // flatten する
          .map(value -> value + 5) // ここから以下は Either<Exception, Integer> 型
          .map(value -> value - 8)
          .map(value -> value * generateException())
          .foldConsumer(
              exception -> System.out.println(exception.getMessage()),
              value     -> System.out.println(value) // 例外が発生していないため、Right の処理が実行される
          );

        System.out.println(string1);
        System.out.println(string2);
        System.out.println(string3);
    }

    public static Integer generateException() throws EitherException {
        /// 検査例外を Throw
        throw new EitherException();
    }
}
