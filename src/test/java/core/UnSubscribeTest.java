package core;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

import static debug.Loggers.println;
import static debug.Loggers.printlnOnCompleted;

public class UnSubscribeTest {

    @Test
    public void unSubscribeTest() {
        final Observable<Long> infinite = Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                for (long l = 0; !subscriber.isUnsubscribed(); l++) {
                    subscriber.onNext(l);
                }
                subscriber.onCompleted();
            }
        });
        infinite.doOnNext(println("i= ")).doOnCompleted(printlnOnCompleted("infinite")).take(10).subscribe();
    }

}
