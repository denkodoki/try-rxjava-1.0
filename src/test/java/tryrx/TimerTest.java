package tryrx;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.TimeInterval;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

import static debug.Loggers.println;
import static debug.Loggers.printlnError;

public class TimerTest {

    final static class Tick {
        private final Long sequenceNumber;

        Tick(Long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public Long getSequenceNumber() {
            return sequenceNumber;
        }

        @Override
        public String toString() {
            return "Tick{" + sequenceNumber +'}';
        }
    };

    final static Func1<Long, Boolean> IS_EVEN = new Func1<Long, Boolean>() {
        @Override
        public Boolean call(Long aLong) {
            return aLong % 2 == 0;
        }
    };

    final static Func1<Long, Tick> TO_TICK = new Func1<Long, Tick>() {
        @Override
        public Tick call(Long aLong) {
            return new Tick(aLong);
        }
    };


    @Test
    public void testMergedInTick() throws InterruptedException {
        System.out.println("Testing merged in tick");
        final Observable<TimeInterval<Long>> inputStream = Observable
                .timer(0L, 500L, TimeUnit.MILLISECONDS)
                .filter(IS_EVEN)
                .timeInterval()
                .take(10);
        final Observable<TimeInterval<Tick>> timerStream = Observable
                .timer(300L, 300L, TimeUnit.MILLISECONDS)
                .map(TO_TICK).timeInterval()
                .take(10);
        final Observable<Object> evenOrTickStream = inputStream
                .cast(Object.class)
                .mergeWith(timerStream);
        evenOrTickStream
                .doOnNext(println())
                .doOnError(printlnError("error: "))
                .subscribe();
        evenOrTickStream
                .toBlocking()
                .lastOrDefault(null);
    }

    @Test
    public void testMutableTimer() throws InterruptedException {
        System.out.println("Testing mutable timer");
        PublishSubject<Observable<Tick>> tickObservableSubject = PublishSubject.create();
        final Observable<Tick> mutableTickStream = Observable.switchOnNext(tickObservableSubject);
        mutableTickStream.timeInterval().doOnNext(println()).doOnError(printlnError("Error in tick stream")).subscribe();
        tickObservableSubject.onNext(Observable.timer(0, 100, TimeUnit.MILLISECONDS).map(TO_TICK));
        Thread.sleep(350);
        tickObservableSubject.onNext(Observable.timer(0, 250, TimeUnit.MILLISECONDS).map(TO_TICK));
        Thread.sleep(800);
    }

}
