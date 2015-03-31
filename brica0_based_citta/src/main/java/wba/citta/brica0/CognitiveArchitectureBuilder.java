package wba.citta.brica0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wba.citta.gsa.AgentInfo;
import wba.citta.gsa.AgentType;
import wba.citta.gsa.CDAgent;
import wba.citta.gsa.FailAgentTree;
import wba.citta.gsa.GSAProperty;
import wba.citta.gsa.IGSAAgent;
import wba.citta.gsa.RandomExecutionStrategy;
import brica0.CognitiveArchitecture;
import brica0.VirtualTimeSyncScheduler;

public class CognitiveArchitectureBuilder {
    /* デフォルトのスケジュール間隔 */
    public static final double DEFAULT_SCHEDULER_INTERVAL = 1;
    public final String AGENT_MODULE_ID_PREFIX = "Agent[%d]";
    public final String AGENT_CONTROLLER_MODULE_ID = "AgentController";

    final CognitiveArchitecture cognitiveArchitecture;
    int nodeNum;
    List<AgentInfo> agentInfoList;
    final String stateLatchPortForGSARunner = "state[GSARunner]";
    final String stateLatchAvailPortForGSARunner = "stateAvail[GSARunner]";
    final StackPortNameBuilder snb = new StackPortNameBuilder();
    final AgentPortNameBuilder anb = new AgentPortNameBuilder();

    public CognitiveArchitectureBuilder(CognitiveArchitecture cognitiveArchitecture) {
        this.cognitiveArchitecture = cognitiveArchitecture;
    }

    public CognitiveArchitectureBuilder() {
        cognitiveArchitecture = new CognitiveArchitecture(new VirtualTimeSyncScheduler(DEFAULT_SCHEDULER_INTERVAL));
    }

    public void populateWithGSAProperty(GSAProperty props) {
        nodeNum = props.getNodeNum();
        agentInfoList = new ArrayList<AgentInfo>();
        agentInfoList.addAll(Arrays.asList(props.getAgentInfoList()));
    }

    CellBackedSharedMemory createCellBackedSharedMemory() {
        List<String> agentIds = new ArrayList<String>(agentInfoList.size());
        for (final AgentInfo agentInfo: agentInfoList) {
            agentIds.add(Integer.toString(agentInfo.getId()));
        }
        agentIds.add("GSARunner");
        return new CellBackedSharedMemory(cognitiveArchitecture, anb, nodeNum, agentIds);
    }
    
    void buildAgentControllerModule() throws Exception {
        AgentControllerModule agentController = new AgentControllerModule();
        cognitiveArchitecture.addModule(AGENT_CONTROLLER_MODULE_ID, agentController);
    }

    public GSARunner buildAgentModules() {
        final List<String> perNodeStackPorts = new ArrayList<String>();
        final List<String> perNodeStackPushAvailPorts = new ArrayList<String>();
        final List<String> perNodeStackTopPorts = new ArrayList<String>();
        final List<String> perNodeStackRemoveAllOpPorts = new ArrayList<String>();
        final List<String> perNodeStackRemoveOpPorts = new ArrayList<String>();
        final List<String> perNodeStackTopDesignationStatePorts = new ArrayList<String>();
        
        for (int i = 0; i < nodeNum; i++) {
            perNodeStackPorts.add(snb.getPerNodeStackPortNameFor(i));
            perNodeStackPushAvailPorts.add(snb.getPerNodeStackPushAvailPortNameFor(i));
            perNodeStackTopPorts.add(snb.getPerNodeStackTopPortNameFor(i));
            perNodeStackRemoveAllOpPorts.add(snb.getPerNodeStackRemoveAllOpPortNameFor(i));
            perNodeStackRemoveOpPorts.add(snb.getPerNodeStackRemoveOpPortNameFor(i));
            perNodeStackTopDesignationStatePorts.add(snb.getPerNodeStackTopDesignationStatePortNameFor(i));
        }
        final FailAgentTree failAgentTree = new FailAgentTree();
        final CellBackedSharedMemory sharedMemory = createCellBackedSharedMemory();
        final GSARunner gsaRunner = new GSARunner(
            nodeNum,
            "state",
            "stateAvail",
            perNodeStackPorts,
            perNodeStackPushAvailPorts,
            perNodeStackTopPorts,
            perNodeStackRemoveAllOpPorts,
            perNodeStackRemoveOpPorts,
            perNodeStackTopDesignationStatePorts,
            failAgentTree,
            sharedMemory,
            new RandomExecutionStrategy()
        );
        sharedMemory.bindAgentModule("GSARunner", gsaRunner);
        cognitiveArchitecture.addModule("GSARunner", gsaRunner);
        for (int i = 0; i < agentInfoList.size(); i++) {
            final AgentInfo info = agentInfoList.get(i);
            if (info.getType() != AgentType.CD)
                throw new UnsupportedOperationException();
            final AgentRunner perspective = new AgentRunner(
                nodeNum,
                "state",
                "stateAvail",
                perNodeStackPorts,
                perNodeStackPushAvailPorts,
                perNodeStackTopPorts,
                perNodeStackRemoveAllOpPorts,
                perNodeStackRemoveOpPorts,
                perNodeStackTopDesignationStatePorts,
                failAgentTree,
                "success"
            );
            final IGSAAgent agent = new CDAgent(info.getId(), info.getUseNode(), perspective);
            perspective.bind(agent);
            perspective.bind(gsaRunner);
            gsaRunner.addAgent(agent);
            sharedMemory.bindAgentModule(Integer.toString(agent.getId()), perspective);
            cognitiveArchitecture.addModule(String.format("Agent[%d]", agent.getId()), perspective);
        }
        return gsaRunner;
    }    
}
