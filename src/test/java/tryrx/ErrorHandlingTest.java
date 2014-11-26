package tryrx;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;

import static debug.Loggers.println;
import static debug.Loggers.printlnError;

public class ErrorHandlingTest {
    private static final String OOPS = "Oops!";

    @Test
    public void onExceptionResumeNextTest() {
        Observable<Object> failingStream = Observable.error(new RuntimeException(OOPS));
        Observable<Integer> fallBackStream = Observable.range(1, 3);
        failingStream
                .onExceptionResumeNext(fallBackStream)
                .doOnNext(println("message :"))
                .doOnError(printlnError("error: "))
                .subscribe();
    }

    @Test
    public void onErrorReturnTest() {
        Observable<Object> failingStream = Observable.error(new RuntimeException(OOPS));
        failingStream.onErrorReturn(new Func1<Throwable, String>() {
            @Override
            public String call(Throwable throwable) {
                return throwable.toString();
            }
        })
                .doOnNext(println("returned :"))
                .doOnError(printlnError("error: "))
                .subscribe();
    }

    @Test
    public void onErrorResumeNextTest() {
        Observable<Object> failingStream = Observable.error(new RuntimeException(OOPS)).startWith("A");
        Observable<String> fallBackStream = Observable.just("B", "C");
        failingStream
                .onErrorResumeNext(fallBackStream)
                .doOnNext(println("returned: "))
                .doOnError(printlnError("error: "))
                .subscribe();
    }


    @Test
    public void resumeSelectorTest() {
        final Observable<?> failingStream = Observable.error(new RuntimeException(OOPS)).startWith("A");
        final Observable<?> fallBackStream = Observable.just("B", "C");
        final Observable<?> terminatingStream = Observable.error(new RuntimeException("Unexpected error!!!"));
        Func1<Throwable, Observable<?>> resumeSelector = new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable throwable) {
                return OOPS.equals(throwable.getMessage()) ? fallBackStream : terminatingStream;
            }
        };
        failingStream.cast(Object.class)
                .onErrorResumeNext(resumeSelector)
                .doOnNext(println("returned: "))
                .doOnError(printlnError("error: "))
                .subscribe();
    }

    @Test
    public void retryNTimesTest() {
        Observable<Object> failingStream = Observable.error(new RuntimeException(OOPS)).startWith("A");
        failingStream
                .retry(2)
                .doOnNext(println("returned: "))
                .doOnError(printlnError("error: "))
                .subscribe();
    }
}
