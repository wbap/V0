package wba.citta.gsa;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSAIteration {
    final Logger logger = LoggerFactory.getLogger(GSAIteration.class);
    final GSA gsa;

    /** エージェント実行ポリシー */
    final AgentExecutionStrategy.Context strategyContext;

    /** まだ実行されていないエージェント */
    final List<Agent> unusedAgents;
    
    /** 実行されたエージェント */
    final List<Agent> usedAgents;

    /** 実行結果 */
    List<Integer> result;

    public GSAIteration(GSA gsa) {
        this.gsa = gsa;
        strategyContext = gsa.agentExecutionStrategy.createContext(this);
        unusedAgents = new LinkedList<Agent>(gsa.agents);
        usedAgents = new LinkedList<Agent>();
    }

    public List<Agent> getUnusedAgents() {
        return unusedAgents;
    }

    public List<Agent> getUsedAgents() {
        return usedAgents;
    }

    public GSA getGSA() {
        return gsa;
    }

    public boolean tryNext() {
        final Agent agent = strategyContext.nextAgent();
        assert usedAgents.indexOf(agent) < 0;
        usedAgents.add(agent);
        unusedAgents.remove(agent);
        /* エージェントの実行処理 */
        /* このエージェントがすでに失敗済みなら実行処理を行なわない */
        gsa.agentEventListeners.fire("agentBeingExecuted", new GSAAgentEvent(gsa, agent));
        Agent.Status status = Agent.Status.NONE;
        if (gsa.failAgentTree.getChildAgr(agent.getId()) != Agent.Status.NONE) {
            status = Agent.Status.AGR_FAIL_AGENT;
            logger.trace("{} already failed in this cycle", agent);
        } else {
            status = agent.exec();
            logger.trace("{} => {}", agent, status);
        }
        gsa.agentEventListeners.fire("agentExecuted", new GSAAgentEvent(gsa, agent, status));

        if (status == Agent.Status.AGR_SUCCESS) {
            /* ゴールをツリーに設定 */
            final List<SharedMemory.GoalStackElement> agentGoalElementArray = agent.getSelfSetGoalElementArray();
            final List<Integer> agentGoalValueArray = agent.getGoalValueArray(agentGoalElementArray);
            gsa.failAgentTree.addTreeNode(agent.getId(), agentGoalValueArray);

            return false;
        } else {
            /* 処理失敗のエージェントをツリーで管理 */
            gsa.failAgentTree.addTreeNode(agent.getId(), status);
            /* エージェント切り替え時に前エージェントの保持情報をクリア */
            agent.suspend();
            if (unusedAgents.size() == 0) {
                gsa.removeUnsolvedGoal();
                return false;
            } else {
                return true;
            }
        }
    }
}
