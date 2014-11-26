package tryrx;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func2;

import static debug.Loggers.println;
import static org.junit.Assert.assertEquals;

public class AggregationTest {

    private final Func2<Integer, Integer, Integer> sum = new Func2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer a, Integer b) {
            return a + b;
        }
    };

    @Test
    public void scanTest() {
        Integer expected = 55;
        Integer result = Observable.range(1, 10).scan(0, sum)
                .doOnNext(println("running sum: ")).toBlocking().last();
        assertEquals(expected,result);
    }

    @Test
    public void reduceTest() {
        Integer expected = 55;
        Integer result =Observable.range(1, 10).reduce(sum)
                .doOnNext(println("sum: ")).toBlocking().single();
        assertEquals(expected,result);
    }
}
