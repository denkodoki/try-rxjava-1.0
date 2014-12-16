package rx;

public abstract class SubscriberAdapter<R,T> extends Subscriber<T> {

    private final Subscriber<? super R> delegate;

    protected SubscriberAdapter(Subscriber<? super R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onCompleted() {
        if (!delegate.isUnsubscribed()) {
            delegate.onCompleted();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if(!delegate.isUnsubscribed()) {
            delegate.onError(throwable);
        }
    }
}
