package wba.citta.gsa;

import java.util.EventObject;

public class GSAEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public GSAEvent(GSA source) {
        super(source);
    }

    public GSA getSource() {
        return (GSA)source;
    }
}
