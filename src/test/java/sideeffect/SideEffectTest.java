package sideeffect;

import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static debug.Loggers.println;
import static org.junit.Assert.assertEquals;

public class SideEffectTest {

    @Test
    public void serialDoOnNextTest() {
        System.out.println("serialDoOnNextTest");

        final CountAction countAction = new CountAction();
        final AssertActionFactory assertActionFactory = new AssertActionFactory(countAction);

        final Observable<Integer> source = Observable
                .range(1, 10)
                .doOnNext(println("from source: "))
                .doOnNext(countAction);

        source.doOnCompleted(assertActionFactory.assertExpected(10)).subscribe();

        source.doOnCompleted(assertActionFactory.assertExpected(20)).subscribe();
    }

    @Test
    @Ignore
    public void multiThreadedDoOnNextTest() throws InterruptedException {
        System.out.println("multiThreadedDoOnNextTest");

        final CountAction countAction = new CountAction();
        final AssertActionFactory assertActionFactory = new AssertActionFactory(countAction);

        final Observable<Integer> source = Observable
                .range(1, 10)
                .doOnNext(println("from source: "))
                .doOnNext(countAction)
                .subscribeOn(Schedulers.io());

        source.doOnCompleted(assertActionFactory.assertExpected(10)).subscribe();

        source.doOnCompleted(assertActionFactory.assertExpected(20)).subscribe();

        source.toBlocking().last();
    }

    @Test
    public void multiThreadedSharedDoOnNextTest() throws InterruptedException {
        System.out.println("multiThreadedSharedDoOnNextTest");

        final CountAction countAction = new CountAction();
        final AssertActionFactory assertActionFactory = new AssertActionFactory(countAction);

        final Observable<Integer> source = Observable
                .range(1, 10)
                .doOnNext(println("from source: "))
                .doOnNext(countAction)
                .share()
                .subscribeOn(Schedulers.io());

        source.doOnCompleted(assertActionFactory.assertExpected(10)).subscribe();

        source.doOnCompleted(assertActionFactory.assertExpected(10)).subscribe();

        source.toBlocking().last();
    }

    private static class CountAction implements Action1<Integer> {
        private int count = 0;
        private void increment() { count += 1; }
        public int getCount() { return count; }
        @Override
        public void call(Integer integer) {
            increment();
        }
    }

    private class AssertActionFactory {
        private final CountAction countAction;
        public AssertActionFactory(CountAction countAction) { this.countAction = countAction; }
        public Action0 assertExpected(final int expected) {
            return new Action0() {
                @Override
                public void call() {
                    assertEquals(expected, countAction.getCount());
                    System.out.println("assert " + expected + " done");
                }
            };
        }
    }

}
