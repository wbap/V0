package wba.citta.gsa;

import brica0.CognitiveArchitecture;
import brica0.VirtualTimeSyncScheduler;

public class CognitiveArchitectureBuilder {
    /* デフォルトのスケジュール間隔 */
    public static final double DEFAULT_SCHEDULER_INTERVAL = 1;
    public final String AGENT_MODULE_ID_PREFIX = "agent_";
    public final String AGENT_CONTROLLER_MODULE_ID = "agentController";

    final AgentControllerModule agentController;
    final CognitiveArchitecture cognitiveArchitecture;

    public CognitiveArchitectureBuilder(CognitiveArchitecture cognitiveArchitecture) {
        this.cognitiveArchitecture = cognitiveArchitecture;
        this.agentController = new AgentControllerModule();
    }

    public CognitiveArchitectureBuilder() {
        cognitiveArchitecture = new CognitiveArchitecture(new VirtualTimeSyncScheduler(DEFAULT_SCHEDULER_INTERVAL));
        this.agentController = new AgentControllerModule();
    }

    void buildAgentControllerModule(GSA gsa) throws Exception {
        AgentControllerModule agentController = new AgentControllerModule();
        cognitiveArchitecture.addModule(AGENT_CONTROLLER_MODULE_ID, agentController);
        buildAgentModules(gsa, agentController);
    }
    
    void buildAgentModules(GSA gsa, AgentControllerModule agentController) throws Exception {
        /*
        short[] tmp = {0};
        List<Agent> agents = gsa.getAgents();
        for (Agent agent: agents) {
            agentController.makeOutPort("out" + String.valueOf(i), 1);
            agentController.setState("out" + String.valueOf(i), tmp);
        }
        
        for (Agent agent: agents) {
            String id = AGENT_MODULE_ID_PREFIX + String.valueOf(i);
            agent.makeOutPort("out", 1);
            agent.setState("out", tmp);
            cognitiveArchitecture.addModule(id, agents[i]);
        }
        
        for (Agent agent: agents) {
            agent.connect(agentController, "out" + String.valueOf(i), "in");
            agentController.connect(agents[i], "out", "in" + String.valueOf(i));
        }
        */
    }    
}
