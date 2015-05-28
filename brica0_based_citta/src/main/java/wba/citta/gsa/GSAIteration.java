package wba.citta.gsa;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSAIteration implements IGSAIteration {
    final Logger logger = LoggerFactory.getLogger(GSAIteration.class);
    final GSA gsa;

    /** エージェント実行ポリシー */
    final AgentExecutionStrategy.Context strategyContext;

    /** 対象エージェント */
    final List<IGSAAgent> agents;   

    /** まだ実行されていないエージェント */
    final List<IGSAAgent> unusedAgents;
    
    /** 実行されたエージェント */
    final List<IGSAAgent> usedAgents;

    /** 実行結果 */
    IGSAAgent successfulAgent;

    public GSAIteration(GSA gsa) {
        this.gsa = gsa;
        strategyContext = gsa.agentExecutionStrategy.createContext(this);
        agents = gsa.getGSAAgents();
        unusedAgents = new LinkedList<IGSAAgent>(agents);
        usedAgents = new LinkedList<IGSAAgent>();
    }

    @Override
    public List<IGSAAgent> getAgents() {
        return agents;
    }

    public List<IGSAAgent> getUnusedAgents() {
        return unusedAgents;
    }

    public List<IGSAAgent> getUsedAgents() {
        return usedAgents;
    }

    public GSA getGSA() {
        return gsa;
    }

    public IGSAAgent getSuccessfulAgent() {
        return successfulAgent;
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.IGSAIteration#tryNext()
     */
    public boolean tryNext() {
        final IGSAAgent agent = strategyContext.nextAgent();
        assert usedAgents.indexOf(agent) < 0;
        usedAgents.add(agent);
        unusedAgents.remove(agent);
        /* エージェントの実行処理 */
        /* このエージェントがすでに失敗済みなら実行処理を行なわない */
        gsa.agentEventListeners.fire("agentBeingExecuted", new GSAAgentEvent(gsa, agent));
        AbstractGSAAgent.Status status = IGSAAgent.Status.NONE;
        if (gsa.failAgentTree.getChildAgr(agent.getId()) != IGSAAgent.Status.NONE) {
            status = IGSAAgent.Status.AGR_FAIL_AGENT;
            logger.trace("{} already failed in this cycle", agent);
        } else {
            status = agent.exec();
            logger.info("{} => {}", agent, status);
        }
        gsa.agentEventListeners.fire("agentExecuted", new GSAAgentEvent(gsa, agent, status));

        if (status == IGSAAgent.Status.AGR_SUCCESS) {
            /* ゴールをツリーに設定 */
            successfulAgent = agent;
            gsa.failAgentTree.addTreeNode(agent.getId(), agent.getLastSubgoal());
            return false;
        } else {
            /* 処理失敗のエージェントをツリーで管理 */
            gsa.failAgentTree.addTreeNode(agent.getId(), status);
            /* エージェント切り替え時に前エージェントの保持情報をクリア */
            agent.suspend();
            if (unusedAgents.size() == 0) {
                successfulAgent = null;
                return false;
            } else {
                return true;
            }
        }
    }
}
