package timer;

import debug.LogError;
import debug.LogEvent;
import debug.LogValue;
import org.junit.Test;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class MutableTimerTest {

    @Test
    public void test() {
        PublishSubject<Observable<Long>> timerSubject = PublishSubject.create();
        final Observable<Long> mutableTimer = Observable
                .switchOnNext(timerSubject)
                .doOnCompleted(new LogEvent("Mutable timer completed"))
                .doOnError(new LogError("Mutable timer error: "))
                .share();

        mutableTimer.subscribe();

        final Observable<Long> timer1 = Observable
                .timer(10, 10, TimeUnit.MILLISECONDS)
                .doOnSubscribe(new LogEvent("timer1 is subscribed"));

        timerSubject.onNext(timer1);

        mutableTimer
                .take(10)
                .timeInterval()
                .doOnNext(new LogValue<>())
                .toList()
                .timeout(200, TimeUnit.MILLISECONDS)
                .toBlocking()
                .last();
    }

}