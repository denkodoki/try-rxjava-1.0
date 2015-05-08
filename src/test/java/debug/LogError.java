package debug;

import rx.functions.Action1;

public class LogError implements Action1<Throwable> {
    private final String prefix;

    public LogError(String prefix) {
        this.prefix = prefix;
    }

    public LogError() {
        this("");
    }

    @Override
    public void call(Throwable throwable) {
        System.out.println(Thread.currentThread().getName() + "; " + prefix + throwable);
    }
}
