package elections.model;

import java.util.Collection;

public class Snapshot implements PollUpdate {

    private final String stream;
    private final Collection<Poll> polls;

    public Snapshot(String stream, Collection<Poll> polls) {
        this.stream = stream;
        this.polls = polls;
    }

    public String getStream() {
        return stream;
    }

    public Collection<Poll> getPolls() {
        return polls;
    }
}
