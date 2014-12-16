package elections.model;

public final class PollId {

    private final String candidate;
    private final String constituency;

    public PollId(String candidate, String constituency) {
        this.candidate = candidate;
        this.constituency = constituency;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getConstituency() {
        return constituency;
    }

}
