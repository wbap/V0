package wba.citta;

import java.util.EventListener;

public interface IterationEventListener extends EventListener {

    public abstract void iterationStarted(IterationEvent evt);

    public abstract void iterationEnded(IterationEvent evt);

}