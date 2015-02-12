package debounce;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

public class DeBounceTest {
    @Test
    public void deBounceOrTimeout() {
        final DeBounceOrTimeout<Long> emissionRegulator = new DeBounceOrTimeout<>(100, 150);
        Action1<Object> loggerAction = new Action1<Object>() {
            @Override
            public void call(Object o) {
                System.out.println(o);
            }
        };

        Observable
                .timer(0, 27, TimeUnit.MILLISECONDS).take(20)
                .concatWith(Observable.timer(0, 435, TimeUnit.MILLISECONDS).take(5))
                .debounce(emissionRegulator)
                .timeInterval()
                .toBlocking()
                .forEach(loggerAction);
    }
}
