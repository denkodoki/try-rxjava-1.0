package rx;

import rx.functions.Action0;

public class BlockingActionOperator<T> implements Observable.Operator<T,T> {

    private final Action0 blockingAction;

    public BlockingActionOperator(Action0 blockingAction) {
        this.blockingAction = blockingAction;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> delegate) {
        return new SubscriberAdapter<T,T>(delegate) {
            @Override
            public void onNext(T t) {
                if (!delegate.isUnsubscribed()) {
                    blockingAction.call();
                    delegate.onNext(t);
                }
            }
        };
    }

}
