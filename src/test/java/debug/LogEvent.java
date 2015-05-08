package debug;

import rx.functions.Action0;

public class LogEvent implements Action0 {
    private final String eventDescription;

    public LogEvent(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LogEvent() {
        this("");
    }

    @Override
    public void call() {
        System.out.println(Thread.currentThread().getName() + "; " + eventDescription);
    }
}
