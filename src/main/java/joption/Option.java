package joption;

import rx.functions.Func0;
import rx.functions.Func1;

public abstract class Option<T> {

    protected Option() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> option(T t) {
        return (t == null) ? new None<>() : new Some(t);
    }

    public static <T> Option<T> none() {
        return new None<>();
    }

    public abstract <R> Option<R> map(Func1<? super T, ? extends R> f);

    public abstract <R> Option<R> flatMap(Func1<? super T, ? extends Option<R>> f);

    public abstract Option<T> orElse(Func0<? extends Option<T>> f);

    public abstract T getOrElse(T t);

    private static class None<T> extends Option<T> {

        @Override
        public <R> Option<R> map(Func1<? super T, ? extends R> f) {
            return none();
        }

        @Override
        public <R> Option<R> flatMap(Func1<? super T, ? extends Option<R>> f) {
            return none();
        }

        @Override
        public Option<T> orElse(Func0<? extends Option<T>> f) {
            return f.call();
        }

        @Override
        public T getOrElse(T t) {
            return t;
        }
    }

    private static class Some<T> extends Option<T> {

        private final T value;

        private Some(T value) {
            this.value = value;
        }

        @Override
        public <R> Option<R> map(Func1<? super T, ? extends R> f) {
            return option(f.call(value));
        }

        @Override
        public <R> Option<R> flatMap(Func1<? super T, ? extends Option<R>> f) {
            try {
                return f.call(value);
            } catch (Exception e) {
                return none();
            }
        }

        @Override
        public Option<T> orElse(Func0<? extends Option<T>> f) {
            return this;
        }

        @Override
        public T getOrElse(T t) {
            return value;
        }
    }

}
