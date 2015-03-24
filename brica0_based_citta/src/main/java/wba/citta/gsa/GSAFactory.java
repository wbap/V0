package wba.citta.gsa;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wba.citta.util.EventPublisherSupport;

public class GSAFactory {
    /* ManualAgentのエージェントID */
    public static final int MANUAL_AGENT_ID = 10;

    int nodeNum;
    List<AgentInfo> agentInfoList;
    boolean useMana;
    SharedMemoryFactory sharedMemoryFactory = new DefaultSharedMemoryFactory();
    AgentExecutionStrategy agentExecutionStrategy = new RandomExecutionStrategy();
    AgentFactory agentFactory = new DefaultAgentFactory();
    
    final EventPublisherSupport<GSAFactoryEvent, GSAFactoryEventListener> gsaFactoryEventListeners = new EventPublisherSupport<>(GSAFactoryEvent.class, GSAFactoryEventListener.class);

    public void populateWithGSAProperty(GSAProperty props) {
        nodeNum = props.getNodeNum();
        agentInfoList = new ArrayList<AgentInfo>();
        agentInfoList.addAll(Arrays.asList(props.getAgentInfoList()));
        if (props.getUseMana()) {
            agentInfoList.add(new AgentInfo(
                MANUAL_AGENT_ID,
                AgentType.MANUAL,
                null,
                null
            ));
        }
    }

    public GSA createGSA() throws IOException {
        final List<AgentInfo> agentInfoList = Collections.unmodifiableList(this.agentInfoList);
        final ISharedMemory sharedMemory = sharedMemoryFactory.createInstance(nodeNum, agentInfoList);
        final FailAgentTree failAgentTree = new FailAgentTree();
        final GSA gsa = new GSA(agentFactory, agentInfoList, sharedMemory, failAgentTree, agentExecutionStrategy);
        gsaFactoryEventListeners.fire("gsaCreated", new GSAFactoryEvent(this, gsa));
        return gsa;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public boolean isUseMana() {
        return useMana;
    }

    public void setUseMana(boolean useMana) {
        this.useMana = useMana;
    }

    public void addGSAFactoryEventListener(GSAFactoryEventListener listener) {
        gsaFactoryEventListeners.addEventListener(listener);
    }

    public void removeGSAFactoryEventListener(GSAFactoryEventListener listener) {
        gsaFactoryEventListeners.removeEventListener(listener);
    }

    public SharedMemoryFactory getSharedMemoryFactory() {
        return sharedMemoryFactory;
    }

    public void setSharedMemoryFactory(SharedMemoryFactory sharedMemoryFactory) {
        this.sharedMemoryFactory = sharedMemoryFactory;
    }

    public AgentExecutionStrategy getAgentExecutionStrategy() {
        return agentExecutionStrategy;
    }

    public void setAgentExecutionStrategy(
            AgentExecutionStrategy agentExecutionStrategy) {
        this.agentExecutionStrategy = agentExecutionStrategy;
    }
}
