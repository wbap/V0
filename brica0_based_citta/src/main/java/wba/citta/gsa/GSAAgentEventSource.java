package wba.citta.gsa;

import java.util.List;

public interface GSAAgentEventSource {
	public List<IGSAAgent> getGSAAgents();
	
    public void addAgentEventListener(GSAAgentEventListener listener);

    public void removeAgentEventListener(GSAAgentEventListener listener);

    public void fireAgentBeingExecuted(IGSAAgent agent);
    
    public void fireAgentExecuted(IGSAAgent agent, IGSAAgent.Status status);
}
