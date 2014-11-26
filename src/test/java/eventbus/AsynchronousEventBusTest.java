package eventbus;

import org.junit.Test;
import rx.functions.Action1;
import rx.functions.Func1;

import static debug.Loggers.*;

public class AsynchronousEventBusTest {

    @Test
    public void singleRecursivePublish() throws InterruptedException {
        System.out.println("singleRecursivePublish");
        AsynchronousEventBus<Integer> eventBus = new AsynchronousEventBus<>();
        republishTenTimes(eventBus, oddFilter, "odd: ");
        eventBus.publish(1);
        Thread.sleep(500);
    }

    @Test
    public void doubleRecursivePublish() throws InterruptedException {
        System.out.println("doubleRecursivePublish");
        AsynchronousEventBus<Integer> eventBus = new AsynchronousEventBus<>();
        republishTenTimes(eventBus, oddFilter, " odd:  ");
        republishTenTimes(eventBus, evenFilter, "even: ");
        eventBus.publish(1);
        eventBus.publish(2);
        Thread.sleep(500);
    }

    private void republishTenTimes(EventBus<Integer> eventBus, Func1<Integer, Boolean> filter, String id) {
        eventBus.getEventStream()
                .filter(filter)
                .take(10)
                .doOnNext(println(id))
                .doOnNext(publishOn(eventBus))
                .doOnCompleted(println0(id + "completed"))
                .doOnError(printlnError(id))
                .subscribe();
    }

    private Func1<Integer, Boolean> evenFilter = new Func1<Integer, Boolean>() {
        @Override
        public Boolean call(Integer integer) {
            return integer % 2 == 0;
        }
    };

    private Func1<Integer, Boolean> oddFilter = new Func1<Integer, Boolean>() {
        @Override
        public Boolean call(Integer integer) {
            return integer % 2 != 0;
        }
    };

    private Action1<Integer> publishOn(final EventBus<Integer> eventBus) {
        return new Action1<Integer>() {
            @Override
            public void call(Integer item) {
                eventBus.publish(item + 2);
            }
        };
    }
}