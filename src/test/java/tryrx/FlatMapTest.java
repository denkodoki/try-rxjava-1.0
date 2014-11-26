package tryrx;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;

import static debug.Loggers.println;

public class FlatMapTest {

    @Test
    public void flatMapTest() throws InterruptedException {
        Func1<Integer, Observable<String>> multiplier = new Func1<Integer, Observable<String>>() {

            @Override
            public Observable<String> call(final Integer x) {
                return Observable.range(1, x).map(new Func1<Integer, String>() {
                    @Override
                    public String call(final Integer y) {
                        return String.format("%d * %d = %d", x, y, x * y);
                    }
                });
            }
        };
        Observable
                .just(9, 8, 7, 6, 5, 4, 3, 2, 1)
                .flatMap(multiplier)
                .doOnNext(println())
                .subscribe();
    }
}
