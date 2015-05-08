package error;

import debug.LogError;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observable.Transformer;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;


public class ErrorHandlingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingTest.class);

    @Test
    public void testRetryOnHot() {
        final PublishSubject<Long> subject = PublishSubject.create();
        Action1<? super Long> failOnce = new Action1<Long>() {
            private boolean canFail = true;

            @Override
            public void call(Long n) {
                if (canFail && n > 3) {
                    canFail = false;
                    throw new RuntimeException("Test exception");
                }
            }
        };
        Action1<? super Long> print = new Action1<Long>() {
            @Override
            public void call(Long n) {
                LOGGER.debug("on hot item #{}", n);
            }
        };
        Action1<Throwable> printError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LOGGER.debug("Detected error: {}", throwable.toString());
            }
        };
        Action1<? super Long> publish = new Action1<Long>() {
            @Override
            public void call(Long n) {
                subject.onNext(n);
            }
        };


        subject.asObservable()
                .doOnNext(failOnce)
                .doOnError(printError)
                .retry()
                .doOnNext(print)
                .subscribe();
        Observable.timer(5, 5, TimeUnit.MILLISECONDS).take(10).toBlocking().forEach(publish);
    }

    @Test

    public void testRetryOnCold() {
        Action1<? super Integer> failOnce = new Action1<Integer>() {
            private boolean canFail = true;

            @Override
            public void call(Integer n) {
                if (canFail && n > 3) {
                    canFail = false;
                    throw new RuntimeException("Test exception");
                }
            }
        };
        Action1<? super Integer> print = new Action1<Integer>() {
            @Override
            public void call(Integer n) {
                LOGGER.debug("on cold item #{}", n);
            }
        };
        Action1<Throwable> printError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LOGGER.debug("Detected error: {}", throwable.toString());
            }
        };
        Observable.range(0, 10)
                .doOnNext(failOnce)
                .doOnError(printError)
                .retry()
                .toBlocking().forEach(print);
    }

    @Test
    @Ignore
    public void testSkipOnError() {
        Action1<? super Integer> failOnce = new Action1<Integer>() {
            private boolean canFail = true;

            @Override
            public void call(Integer n) {
                if (canFail && n > 3) {
                    canFail = false;
                    throw new RuntimeException("Test exception");
                }
            }
        };
        Action1<? super Integer> print = new Action1<Integer>() {
            @Override
            public void call(Integer n) {
                LOGGER.debug("on cold item #{}", n);
            }
        };
        Observable.range(0, 10)
                .doOnNext(failOnce)
                .compose(new SkipOnError<Integer>(LOGGER))
                .doOnError(new LogError("Unexpected error"))
                .toBlocking()
                .forEach(print);
    }

    private static class SkipOnError<T> implements Transformer<T, T> {
        private final Logger logger;

        public SkipOnError(Logger logger) {
            this.logger = logger;
        }

        @Override
        public Observable<T> call(Observable<T> from) {
            final PublishSubject<T> to = PublishSubject.create();
            final Bridge<T> bridge = new Bridge<>(from, to);
            return to.asObservable()
                    .serialize()
                    .doOnSubscribe(bridge.getOnSubscribe())
                    .retry()
                    .share()
                    .doOnUnsubscribe(bridge.getOnUnSubscribe())
                    .doOnError(createErrorLogger())
                    ;
        }

        private Action1<Throwable> createErrorLogger() {
            return new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    logger.error("Ignoring error.", throwable);
                }
            };
        }
    }

    private static class Bridge<T> {

        private final Action0 onSubscribe;
        private final Action0 onUnSubscribe;
        private Subscription subscription;

        private Bridge(final Observable<T> from, final PublishSubject<T> to) {
            onSubscribe = createOnSubscribe(from, to);
            onUnSubscribe = createOnUnSubscribe();
        }

        private Action0 createOnSubscribe(final Observable<T> from, final PublishSubject<T> to) {
            return new Action0() {
                @Override
                public void call() {
                    LOGGER.debug("Creating subscription");
                    subscription = from.doOnNext(new Action1<T>() {
                        @Override
                        public void call(T t) {
                            to.onNext(t);
                        }
                    }).subscribeOn(Schedulers.computation()).subscribe();
                }
            };
        }

        private Action0 createOnUnSubscribe() {
            return new Action0() {
                @Override
                public void call() {
                    LOGGER.debug("Un-subscribe");
                    if (subscription != null && subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                }
            };
        }

        public Action0 getOnSubscribe() {
            return onSubscribe;
        }

        public Action0 getOnUnSubscribe() {
            return onUnSubscribe;
        }
    }
}
