package elections.model;

import java.util.Collection;

public interface PollUpdate {
    String getStream();
    Collection<Poll> getPolls();
}
