package eventbus;

import debug.Debugger;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

public class AsynchronousEventBusTest {

    @Test
    public void singleRecursivePublish() throws InterruptedException {
        System.out.println("singleRecursivePublish");
        AsynchronousEventBus<Integer> eventBus = new AsynchronousEventBus<>();
        Subscription s1 = subscribe(eventBus, oddFilter, "#1");
        eventBus.publish(1);
        Thread.sleep(100);
        s1.unsubscribe();
    }

    @Test
    public void doubleRecursivePublish() throws InterruptedException {
        System.out.println("doubleRecursivePublish");
        AsynchronousEventBus<Integer> eventBus = new AsynchronousEventBus<>();
        Subscription s1 = subscribe(eventBus, oddFilter, "#1");
        Subscription s2 = subscribe(eventBus, evenFilter, "#2");
        eventBus.publish(1);
        eventBus.publish(2);
        Thread.sleep(100);
        s1.unsubscribe();
        s2.unsubscribe();
    }

    private Subscription subscribe(EventBus<Integer> eventBus, Func1<Integer, Boolean> filter, String id) {
        return eventBus.getEventStream()
                .filter(filter)
                .lift(new RecursivePublisher(eventBus))
                .lift(new Debugger<Integer>(id))
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

    private static class RecursivePublisher implements Observable.Operator<Integer,Integer> {

        private final EventBus<Integer> eventBus;

        private RecursivePublisher(EventBus<Integer> eventBus) {
            this.eventBus = eventBus;
        }


        @Override
        public Subscriber<? super Integer> call(final Subscriber<? super Integer> subscriber) {
            return new Subscriber<Integer>() {

                @Override
                public void onCompleted() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(throwable);
                    }
                }

                @Override
                public void onNext(final Integer t) {
                    if (!subscriber.isUnsubscribed()) {
                        if (t < 10) {
                            eventBus.publish(t + 2);
                            subscriber.onNext(t);
                        } else {
                            subscriber.onCompleted();
                        }
                    }
                }
            };
        }
    }
}