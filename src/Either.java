import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 失敗の可能性を表現するクラス
 * Left は失敗 (Exception) の型
 * Right は成功の型
 * Created by Atsushi Miyake on 2017/06/23.
 */
public class Either<Left extends Exception, Right> { // Error 系は拾わないように Throwable でなく Exception

    // どちらか一方は必ず不在なので Optional で wrap
    private Optional<Left> left   = Optional.empty();
    private Optional<Right> right = Optional.empty();

    // コンストラクタを外部公開しない
    private Either(Left left, Right right) {
        this.left  = Optional.ofNullable(left);
        this.right = Optional.ofNullable(right);
    }

    public static <Left extends Exception, Right> Either<Left, Right> left(Left left) {
        // 両方の不在を禁止 (型で null の代入を禁止できないとか...)
        Objects.requireNonNull(left);
        return new Either(left, null);
    }

    public Optional<Left> left() {
        return this.left;
    }

    public boolean isLeft() {
        return this.left.isPresent();
    }

    public static <Left extends Exception, Right> Either<Left, Right> right(Right right) {
        Objects.requireNonNull(right);
        return new Either(null, right);
    }

    public Optional<Right> right() {
        return this.right;
    }

    public boolean isRight() {
        return this.right.isPresent();
    }

    // Either は Functor
    public <U> Either<Left, U> map(ThrowableFunction<? super Right, ? extends U, Left> mapper) {

        Objects.requireNonNull(mapper);

        // Right が不在の場合、そのまま Left を返す
        if (!this.isRight()) {
            return new Either(this.left.get(), null);
        }

        try {
            return new Either(null, mapper.apply(this.right.get()));
        } catch (Exception exception) {
            return new Either(exception, null);
        }
    }

    public <U> Either<Left, U> flatMap(ThrowableFunction<? super Right, ? extends Either<? super Left, ? extends U>, Left> mapper) {

        Objects.requireNonNull(mapper);

        if (!this.isRight()) {
            return new Either(this.left.get(), null);
        }

        try {
            final Either<? super Left, ? extends U> either = mapper.apply(this.right.get());
            return either.fold(
                left  -> new Either(left, null),
                right -> new Either(null, right)
            );
        } catch (Exception exception) {
            return new Either(exception, null);
        }
    }

    // 失敗もしくは成功の処理を代行して実行
    public <U> U fold(Function<Left, ? extends U> mapper1, Function<? super Right, ? extends U> mapper2) {

        Objects.requireNonNull(mapper1);
        Objects.requireNonNull(mapper2);

        if (!this.isRight() && this.isLeft()) {
            return mapper1.apply(this.left.get());
        }

        return mapper2.apply(this.right.get());
    }

    // できなくて辛過ぎる。というか Consumer も Unit を返せばいいのに。
    // public void fold(Consumer<? super Left> mapper1, Consumer<? super Right> mapper2)
    public void foldConsumer(Consumer<? super Left> mapper1, Consumer<? super Right> mapper2) {

        Objects.requireNonNull(mapper1);
        Objects.requireNonNull(mapper2);

        if (!this.isRight() && this.isLeft()) {
            mapper1.accept(this.left.get());
        } else {
            mapper2.accept(this.right.get());
        }
    }
}
