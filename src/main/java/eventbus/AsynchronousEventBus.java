package eventbus;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class AsynchronousEventBus<T> implements EventBus<T> {

    private PublishSubject<T> eventSubject = PublishSubject.create();
    private Observable<T> eventStream = eventSubject
            .serialize()
            .share()
            .observeOn(Schedulers.io());

    @Override
    public Observable<T> getEventStream() {
        return eventStream;
    }

    @Override
    public void publish(T event) {
        eventSubject.onNext(event);
    }
}
