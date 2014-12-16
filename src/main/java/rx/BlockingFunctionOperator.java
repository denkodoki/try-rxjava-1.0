package rx;

import rx.functions.Func0;

public class BlockingFunctionOperator<R,T> implements Observable.Operator<R,T> {

    private final Func0<R> blockingFunction;

    public BlockingFunctionOperator(Func0<R> blockingFunction) {
        this.blockingFunction = blockingFunction;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super R> delegate) {
        return new SubscriberAdapter<R,T>(delegate) {
            @Override
            public void onNext(T t) {
                if (!delegate.isUnsubscribed()) {
                    final R result = blockingFunction.call();
                    delegate.onNext(result);
                }
            }
        };
    }
}
