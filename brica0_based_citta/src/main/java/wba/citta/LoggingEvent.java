package wba.citta;

import java.util.EventObject;

public class LoggingEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    int mask;
    String message;

    public int getMask() {
        return mask;
    }

    public String getMessage() {
        return message;
    }

    LoggingEvent(Object source, int mask, String message) {
        super(source);
        this.mask = mask;
        this.message = message;
    }

}
