package wba.citta;

import java.util.EventObject;


public class StatusEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public StatusEvent(CittaRunner source) {
        super(source);
    }

    public CittaRunner getSource() {
        return (CittaRunner)source;
    }
}
