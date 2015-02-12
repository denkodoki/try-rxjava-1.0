package timer;

import rx.Observable;

public class MutableTimer implements Observable.Transformer<Observable<Long>, Long> {
    @Override
    public Observable<Long> call(Observable<Observable<Long>> timerStream) {
        return Observable.switchOnNext(timerStream);
    }
}
