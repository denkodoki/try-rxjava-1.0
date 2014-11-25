package core;

import debug.Debugger;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func2;

import static org.junit.Assert.assertEquals;

public class AggregationTest {

    private final Func2<Integer, Integer, Integer> sum = new Func2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer a, Integer b) {
            return a + b;
        }
    };
    private Debugger<Integer> print(String prefix) { return new Debugger<>(prefix); }

    @Test
    public void scanTest() {
        Integer expected = 55;
        Integer result = Observable.range(1, 10).scan(0, sum)
                .lift(print("running sum")).toBlocking().last();
        assertEquals(expected,result);
    }

    @Test
    public void reduceTest() {
        Integer expected = 55;
        Integer result =Observable.range(1, 10).reduce(sum)
                .lift(print("sum: ")).toBlocking().single();
        assertEquals(expected,result);
    }
}
