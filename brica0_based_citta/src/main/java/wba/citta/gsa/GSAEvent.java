package wba.citta.gsa;

import java.util.EventObject;

public class GSAEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public GSAEvent(GSAAgentEventSource source) {
        super(source);
    }

    public GSAAgentEventSource getSource() {
        return (GSAAgentEventSource)source;
    }
}
