package wba.citta.gsa;

import java.util.EventListener;

public interface GSAFactoryEventListener extends EventListener {
    public void gsaCreated(GSAFactoryEvent evt);
}
