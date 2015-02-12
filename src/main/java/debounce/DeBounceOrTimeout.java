package debounce;

import rx.Observable;
import rx.functions.Func1;

import java.util.concurrent.TimeUnit;

public class DeBounceOrTimeout<T> implements Func1<T, Observable<Object>> {

    private static final long IMMEDIATE_EMISSION_INTERVAL = 0;

    private final long deBounceInterval;
    private final long timeout;

    private long suppressElapseAccumulator = 0;
    private long prevItemTimestamp = 0;
    private boolean firstItem = true;

    public DeBounceOrTimeout(long deBounceInterval, long timeout) {
        this.deBounceInterval = deBounceInterval;
        this.timeout = timeout;
    }

    @Override
    public Observable<Object> call(T t) {
        return Observable.empty().delay(calcDeBounceIntervalForCurrentItem(), TimeUnit.MILLISECONDS);
    }

    private long calcDeBounceIntervalForCurrentItem() {
        if (firstItem) {
            firstItem = false;
            prevItemTimestamp = System.currentTimeMillis();
            return deBounceInterval;
        }

        final long interval = System.currentTimeMillis() - prevItemTimestamp;
        final boolean prevSuppressed = interval < deBounceInterval;
        prevItemTimestamp = System.currentTimeMillis();
        if (prevSuppressed) {
            suppressElapseAccumulator += interval;
            if (suppressElapseAccumulator > timeout) {
                suppressElapseAccumulator = 0;
                return IMMEDIATE_EMISSION_INTERVAL;
            } else {
                return deBounceInterval;
            }
        } else {
            suppressElapseAccumulator = 0;
            return IMMEDIATE_EMISSION_INTERVAL;
        }
    }
}
