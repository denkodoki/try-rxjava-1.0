package elections.model;

public class Poll {

    private final PollId id;
    private final long votes;
    private final long increment;

    public Poll(PollId id, long votes, long increment) {
        this.id = id;
        this.votes = votes;
        this.increment = increment;
    }

    public PollId getId() {
        return id;
    }

    public long getIncrement() {
        return increment;
    }

    public long getVotes() {
        return votes;
    }

    public Poll add(long votes) {
        return new Poll(id, this.votes + votes, votes);
    }
}
