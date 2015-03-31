package wba.citta.gsa;

public interface AgentFactory {
    IGSAAgent createInstance(AgentType type, int agentId, boolean[] useNode,
            ISharedMemory sharedMemory);

}
