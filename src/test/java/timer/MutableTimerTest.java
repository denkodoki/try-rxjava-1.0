package timer;

import debug.Loggers;
import org.junit.Test;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class MutableTimerTest {

    private static final long MAX_WAIT_MILLS = 5_000L;

    @Test
    public void test() {
        PublishSubject<Observable<Long>> timerSubject = PublishSubject.create();
        final Observable<Long> mutableTimer = timerSubject.asObservable().compose(new MutableTimer());
        mutableTimer.timeInterval().doOnNext(Loggers.println()).subscribe();

        final Observable<Long> timer1 = Observable.timer(10L, 100L, TimeUnit.MILLISECONDS);
        timerSubject.onNext(timer1);

        mutableTimer.take(10).timeout(MAX_WAIT_MILLS, TimeUnit.MILLISECONDS).toBlocking().last();
    }

}