package wba.citta.gsa;

import java.util.EventListener;

public interface GSAAgentEventListener extends EventListener {
    public void agentExecuted(GSAAgentEvent evt);

    public void agentBeingExecuted(GSAAgentEvent evt);

    public void agentRemoved(GSAAgentEvent evt);
}
