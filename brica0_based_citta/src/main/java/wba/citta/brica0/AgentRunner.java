package wba.citta.brica0;

import java.util.List;

import wba.citta.gsa.AbstractGSAAgent;
import wba.citta.gsa.FailAgentTree;
import wba.citta.gsa.IGSAAgent;

public class AgentRunner extends BricaPerspective {
    final String successStatePort;
    boolean success;
    private FailAgentTree failAgentTree;

    @Override
    public void doFire() {
        success = false;
        /* エージェントの実行処理 */
        /* このエージェントがすでに失敗済みなら実行処理を行なわない */
        gsa.fireAgentBeingExecuted(agent);
        AbstractGSAAgent.Status status = IGSAAgent.Status.NONE;
        if (failAgentTree.getChildAgr(agent.getId()) != IGSAAgent.Status.NONE) {
            status = IGSAAgent.Status.AGR_FAIL_AGENT;
            logger.trace("{} already failed in this cycle", agent);
        } else {
            status = agent.exec();
            logger.info("{} => {}", agent, status);
        }
        gsa.fireAgentExecuted(agent, status);

        if (status == IGSAAgent.Status.AGR_SUCCESS) {
            /* ゴールをツリーに設定 */
            failAgentTree.addTreeNode(agent.getId(), agent.getLastSubgoal());
            success = true;
        } else {
            /* 処理失敗のエージェントをツリーで管理 */
            failAgentTree.addTreeNode(agent.getId(), status);
            /* エージェント切り替え時に前エージェントの保持情報をクリア */
            agent.suspend();
        }
        results.put(successStatePort, new short[] { (short) (success ? 1: 0) });
    }

    public AgentRunner(int size, String latchPort,
            String latchAvailPort, List<String> perNodeStackPorts,
            List<String> perNodeStackPushAvailPorts,
            List<String> perNodeStackTopPorts,
            List<String> perNodeStackRemoveAllOpPorts,
            List<String> perNodeStackRemoveOpPorts,
            List<String> perNodeStackTopDesignationStatePorts,
            FailAgentTree failAgentTree,
            String successStatePort) {
        super(size, latchPort, latchAvailPort, perNodeStackPorts,
                perNodeStackPushAvailPorts, perNodeStackTopPorts,
                perNodeStackRemoveAllOpPorts, perNodeStackRemoveOpPorts,
                perNodeStackTopDesignationStatePorts);
        this.failAgentTree = failAgentTree;
        this.successStatePort = successStatePort;
    }
}
