package wba.citta;

import java.util.EventListener;


public interface LoggingEventListener extends EventListener {
    public void logEntryAppended(LoggingEvent evt);
}
