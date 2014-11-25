package eventbus;

import rx.Observable;

public interface EventBus<T> {
    Observable<T> getEventStream();
    void publish(T event);
}
