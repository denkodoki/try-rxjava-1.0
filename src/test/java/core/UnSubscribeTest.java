package core;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class UnSubscribeTest {

    private Action1<Long> print(final String prefix) {
        return new Action1<Long>() {
            @Override
            public void call(Long i) {
                System.out.println(Thread.currentThread().getName() + "; " + prefix + i);
            }
        };
    }


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
        infinite.doOnNext(print("i= ")).take(10).subscribe();
    }

}
