package elections;

import elections.model.*;

import java.util.HashMap;
import java.util.Map;

public class PollUpdateConflation {

    private final String stream;
    private final Map<PollId, Poll> accumulators = new HashMap<>();
    private boolean isSnapshot = false;

    public PollUpdateConflation(String stream) {
        this.stream = stream;
    }

    public void add(PollUpdate update) {
        isSnapshot = isSnapshot | update instanceof Snapshot;
        for (Poll poll: update.getPolls()) {
            final Poll accumulator = accumulators.containsKey(poll.getId())? createAccumulated(poll) : poll;
            accumulators.put(accumulator.getId(),accumulator);
        }
    }

    private Poll createAccumulated(Poll poll) {
        return accumulators.get(poll.getId()).add(poll.getVotes());
    }

    public PollUpdate getConflated() {
        return isSnapshot ? new Snapshot(stream, accumulators.values()) : new Increment(stream, accumulators.values());
    }
}
