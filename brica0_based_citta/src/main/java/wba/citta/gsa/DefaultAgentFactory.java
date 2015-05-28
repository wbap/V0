package wba.citta.gsa;

public class DefaultAgentFactory implements AgentFactory {

    @Override
    public IGSAAgent createInstance(AgentType agentType, int agentId, boolean[] useNode,
            ISharedMemory sharedMemory) {
        IGSAAgent agent;
        if (agentType == AgentType.CD) {
            agent = new CDAgent(agentId, useNode, sharedMemory);
        } else if (agentType == AgentType.ASSOCIATE) {
            throw new GSAException("AssociateAgent is not supported.");
        } else if (agentType == AgentType.LOG) {
            agent = new LogAgent(agentId, useNode, sharedMemory);
        } else {
            throw new IllegalArgumentException();
        }
        return agent;
    }

}
