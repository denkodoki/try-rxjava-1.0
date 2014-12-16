package tryrx;

import org.junit.Test;
import rx.BlockingActionOperator;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class AsyncTest {

    private static final java.lang.Object DUMMY = new Object() {
        @Override
        public String toString() {
            return "Done";
        }
    };
    private static final long COMPLETION_TIMEOUT = 3000L;
    private static final long COMPLETION_DELAY   = 5000L;
    private static final Object ERROR = new Object() {
        @Override
        public String toString() {
            return "Error";
        }
    };

    private final Action0 BLOCKING_ACTION = new Action0() {
        @Override
            public void call() {
            try {
                System.out.println("Blocking action started");
                Thread.sleep(COMPLETION_DELAY);
                System.out.println("Blocking action done");
            } catch (InterruptedException e) {
                System.out.println(e);
            }

        }
    };


    private final Action0 FAILING_ACTION = new Action0() {
        @Override
        public void call() {
            try {
                System.out.println("Blocking action started");
                Thread.sleep(COMPLETION_DELAY);
                System.out.println("Blocking action done");
                throw new RuntimeException("Unexpected exception");
            } catch (InterruptedException e) {
                System.out.println(e);
            }

        }
    };


    @Test
    public void testAsyncAction() throws InterruptedException {

        final Observable<Object> resultObservable = Observable
                .just(DUMMY)
                .subscribeOn(Schedulers.io())
                .lift(new rx.BlockingActionOperator<Object>(BLOCKING_ACTION))
                .share();
        resultObservable.subscribe();
        System.out.println("Asynchronous execution started");
        // resultObservable.toBlocking().lastOrDefault(null);
    }

    @Test
    public void testAsyncActionTimeout() throws InterruptedException {

        final Observable<Object> resultObservable = Observable
                .just(DUMMY)
                .subscribeOn(Schedulers.io())
                .lift(new rx.BlockingActionOperator<Object>(BLOCKING_ACTION))
                .timeout(COMPLETION_TIMEOUT, TimeUnit.MILLISECONDS)
                .share();
        resultObservable.subscribe();
        System.out.println("Asynchronous execution started");
        resultObservable.toBlocking().lastOrDefault(null);
    }

    @Test
    public void testAsyncActionError() throws InterruptedException {
        new Func1<Throwable, Object>() {
            @Override
            public Object call(Throwable throwable) {
                return ERROR;
            }
        };
        final Observable<Object> resultObservable = Observable
                .just(DUMMY)
                .subscribeOn(Schedulers.io())
                .lift(new BlockingActionOperator<Object>(FAILING_ACTION))
                .share();
        resultObservable.subscribe();
        System.out.println("Asynchronous execution started");
        resultObservable.toBlocking().lastOrDefault(null);
    }

}
