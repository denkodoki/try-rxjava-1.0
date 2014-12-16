package elections;

import elections.model.PollUpdate;
import elections.model.Snapshot;
import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Func0;
import rx.functions.Func1;
import timer.Timers.Tick;

import java.util.List;

import static timer.Timers.timerFrom;

public class PollThrottling implements Transformer<PollUpdate, PollUpdate> {

    private final String stream;
    private final Observable<Tick> regularTicks;

    public PollThrottling(String stream, Observable<Tick> regularTicks) {
        this.stream = stream;
        this.regularTicks = regularTicks;
    }

    @Override
    public Observable<PollUpdate> call(Observable<PollUpdate> pollUpdateObservable) {
        Func0<Observable<Tick>> bufferClosingSelector = createBufferClosingSelector(pollUpdateObservable);
        Func1<List<PollUpdate>, PollUpdate> conflatingFunction = createConflationFunction();
        return pollUpdateObservable.buffer(bufferClosingSelector).map(conflatingFunction);
    }

    private Func0<Observable<Tick>> createBufferClosingSelector(Observable<PollUpdate> pollUpdateObservable) {
        final Observable<Tick> eventualTicks = timerFrom(pollUpdateObservable.ofType(Snapshot.class));
        return new Func0<Observable<Tick>>() {
            @Override
            public Observable<Tick> call() {
                return regularTicks.mergeWith(eventualTicks);
            }
        };
    }

    private Func1<List<PollUpdate>, PollUpdate> createConflationFunction() {
        return new Func1<List<PollUpdate>, PollUpdate>() {
            @Override
            public PollUpdate call(List<PollUpdate> pollUpdates) {
                final PollUpdateConflation conflation = new PollUpdateConflation(stream);
                for(PollUpdate pollUpdate: pollUpdates) {
                    conflation.add(pollUpdate);
                }
                return conflation.getConflated();
            }
        };
    }

}
