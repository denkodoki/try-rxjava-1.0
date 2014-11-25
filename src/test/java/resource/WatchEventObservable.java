package resource;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public enum WatchEventObservable {
    ;
    public static Observable<WatchEvent> from(
            final WatchService watchService,
            final Path dir,
            final long pollingInterval,
            final TimeUnit pollingTimeUnit,
            final WatchEvent.Kind... eventKinds
    ) {
        Func0<WatchKey> resourceFactory = new Func0<WatchKey>() {
            @Override
            public WatchKey call() {
                try {
                    return dir.register(watchService, eventKinds);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        final Func1<WatchKey, Observable<WatchEvent>> observableFactory = new Func1<WatchKey, Observable<WatchEvent>>() {
            @Override
            public Observable<WatchEvent> call(WatchKey watchKey) {
                return Observable.interval(pollingInterval, pollingTimeUnit).lift(new WatchEventPollingOperator(watchKey));
            }
        };
                final Action1<WatchKey> disposeAction = new Action1<WatchKey>() {

                    @Override
                    public void call(WatchKey watchKey) {
                if (watchKey.isValid()) {
                    watchKey.cancel();
                }
            }
        };

        return Observable.using(resourceFactory, observableFactory, disposeAction);
    }

    private static class WatchEventPollingOperator implements Observable.Operator<WatchEvent, Long> {

        private final WatchKey watchKey;

        private WatchEventPollingOperator(WatchKey watchKey) {
            this.watchKey = watchKey;
        }

        @Override
        public Subscriber<? super Long> call(final Subscriber<? super WatchEvent> subscriber) {
            return new Subscriber<Long>() {
                @Override
                public void onCompleted() {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onError(throwable);
                    }
                }

                @Override
                public void onNext(Long aLong) {
                    for(WatchEvent event: watchKey.pollEvents()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                    }
                    boolean valid = watchKey.reset();
                    if (!valid) {
                        subscriber.onCompleted();
                    }
                }
            };
        }
    }
}
