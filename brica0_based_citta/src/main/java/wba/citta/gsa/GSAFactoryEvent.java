package wba.citta.gsa;

import java.util.EventObject;

public class GSAFactoryEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private GSA gsa;

    public GSAFactoryEvent(GSAFactory source, GSA gsa) {
        super(source);
        this.gsa = gsa;
    }

    public GSAFactory getSource() {
        return (GSAFactory)source;
    }

    public GSA getGSA() {
        return gsa;
    }
}
