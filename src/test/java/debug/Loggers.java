package debug;

import rx.functions.Action0;
import rx.functions.Action1;

public enum Loggers {
    ;
    public static <T> Action1<T> println(final String prefix) {
        return new Action1<T>() {
            @Override
            public void call(T t) {
                System.out.println(prefix + t);
            }
        };
    }

    public static <T> Action1<T> println() {
        return println("");
    }

    public static Action0 printlnOnCompleted(final String streamName) {
        return new Action0() {
            @Override
            public void call() {
                System.out.println(streamName + " completed");
            }
        };
    }

}
