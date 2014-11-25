package debug;

import rx.Observable;
import rx.Subscriber;

public class Debugger<T> implements Observable.Operator<T,T> {
    private final String message;

    public Debugger(String message) {
        this.message = message;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    print("Completed");
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (!subscriber.isUnsubscribed()) {
                    print(throwable);
                    subscriber.onError(throwable);
                }
            }

            @Override
            public void onNext(T t) {
                if (!subscriber.isUnsubscribed()) {
                    print(t);
                    subscriber.onNext(t);
                }
            }

            private void print(Object msg) {
                System.out.println(Thread.currentThread().getName() + " " + message + " " + msg);
            }
        };
    }
}
