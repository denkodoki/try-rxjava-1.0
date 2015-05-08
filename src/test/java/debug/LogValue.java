package debug;

import rx.functions.Action1;

public class LogValue<T> implements Action1<T> {
    private final String prefix;

    public LogValue(String prefix) {
        this.prefix = prefix;
    }

    public LogValue() {
        this("");
    }

    @Override
    public void call(T t) {
        System.out.println(Thread.currentThread().getName() + "; " + prefix + t);
    }
}
