package debug;

import rx.functions.Action0;
import rx.functions.Action1;

public enum Loggers {
    ;

    public static Action0 println0(final String message) {
        return new Action0() {
            @Override
            public void call() {
                System.out.println(Thread.currentThread().getName() + "; " + message);
            }
        };
    }

    public static <T> Action1<T> println(final String prefix) {
        return new Action1<T>() {
            @Override
            public void call(T t) {
                System.out.println(Thread.currentThread().getName() + "; " + prefix + t);
            }
        };
    }

    public static <T> Action1<T> println() {
        return println("");
    }

    public static Action1<Throwable> printlnError(final String prefix) {
        return new Action1<Throwable>() {

            @Override
            public void call(final Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + "; " + prefix + throwable);
            }
        };
    }
}
