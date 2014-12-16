package elections.model;

import java.util.Collection;

public class Increment implements PollUpdate {
    final String stream;
    final Collection<Poll> polls;

    public Increment(String stream, Collection<Poll> polls) {
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
