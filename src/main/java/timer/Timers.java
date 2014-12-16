package timer;

import rx.Observable;
import rx.functions.Func1;

public enum Timers {
    ;
    public final static class Tick {};
    public final static Tick TICK = new Tick();
    public static <T> Func1<T,Tick> toTick() {
        return new Func1<T,Tick>() {
            @Override
            public Tick call(T t) {
                return TICK;
            }
        };
    }
    public static Observable<Tick> timerFrom(Observable<?> o) {
        return o.map(toTick());
    }
}
