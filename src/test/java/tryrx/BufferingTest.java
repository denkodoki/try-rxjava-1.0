package tryrx;

import debug.Loggers;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BufferingTest {

    @Test
    public void testBuffering() {
        final Observable<Long> inputStream = Observable.interval(5L, TimeUnit.MILLISECONDS).take(100).observeOn(Schedulers.computation());
        final Observable<Long> selectorStream = Observable.switchOnNext(inputStream.filter(selectorFilter).map(mapToSelector));
        final Observable<List<Long>> bufferedStream = inputStream.buffer(selectorStream);
        bufferedStream.toBlocking().forEach(Loggers.println("buffered: "));
    }

    final static Func1<Long, Boolean> selectorFilter = new Func1<Long, Boolean>() {
        @Override
        public Boolean call(Long l) {
            return l % 7 == 0;
        }
    };
    final Func1<Long, Observable<Long>> mapToSelector = new Func1<Long, Observable<Long>>() {
        @Override
        public Observable<Long> call(final Long aLong) {
            return Observable.<Long>never().startWith(aLong);
        }
    };

}
