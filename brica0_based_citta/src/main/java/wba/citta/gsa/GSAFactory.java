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
    
    AgentExecutionStrategy agentExecutionStrategy = new RandomExecutionStrategy();
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
        final SharedMemory sharedMemory = new SharedMemory(nodeNum);
        final FailAgentTree failAgentTree = new FailAgentTree();
        final GSA gsa = new GSA(Collections.unmodifiableList(agentInfoList), sharedMemory, failAgentTree, agentExecutionStrategy);
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
}
