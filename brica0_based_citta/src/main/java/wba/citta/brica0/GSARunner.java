package wba.citta.brica0;

import java.util.ArrayList;
import java.util.List;

import wba.citta.gsa.AgentExecutionStrategy;
import wba.citta.gsa.FailAgentTree;
import wba.citta.gsa.GSAAgentEvent;
import wba.citta.gsa.GSAAgentEventListener;
import wba.citta.gsa.GSAAgentEventSource;
import wba.citta.gsa.Goal;
import wba.citta.gsa.IGSAAgent;
import wba.citta.gsa.IGSAIteration;
import wba.citta.gsa.ISharedMemory;
import wba.citta.gsa.State;
import wba.citta.util.EventPublisherSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSARunner extends BricaPerspective implements GSAAgentEventSource {
    public final static int AGENT_COUNT = 8;
    public final static int DO_NOTHING = 0;
    public final static int EXEC = 1;

    private static Logger log = LoggerFactory.getLogger(GSARunner.class);

    /* 外部から設定されたゴール用の仮のエージェントID */
    private static final int GOAL_AGID = 0;
 
    /* 外部から設定されたゴール */
    private Goal target = null;

    /** GSAエージェントの配列 */
    List<IGSAAgent> gsaAgents = new ArrayList<IGSAAgent>();

    /* 実行エージェントの選択方法 0:配列の順 1:ランダム */
    int agentSelectMode = 1;

    /* 到達に失敗したゴールをツリーで管理するクラス */
    FailAgentTree failAgentTree = null;
   
    CellBackedSharedMemory sharedMemory = null;
    Latch stateLatch = null;
    AgentExecutionStrategy agentExecutionStrategy;

    final EventPublisherSupport<GSAAgentEvent, GSAAgentEventListener> agentEventListeners = new EventPublisherSupport<>(GSAAgentEvent.class, GSAAgentEventListener.class);

    public GSARunner(int size, String latchPort,
            String latchAvailPort, List<String> perNodeStackPorts,
            List<String> perNodeStackPushAvailPorts,
            List<String> perNodeStackTopPorts,
            List<String> perNodeStackRemoveAllOpPorts,
            List<String> perNodeStackRemoveOpPorts,
            List<String> perNodeStackTopDesignationStatePorts,
            FailAgentTree failAgentTree,
            CellBackedSharedMemory sharedMemory,
            AgentExecutionStrategy agentExecutionStrategy) {
        super(size, latchPort, latchAvailPort, perNodeStackPorts,
                perNodeStackPushAvailPorts, perNodeStackTopPorts,
                perNodeStackRemoveAllOpPorts, perNodeStackRemoveOpPorts,
                perNodeStackTopDesignationStatePorts);
        this.sharedMemory = sharedMemory;
        this.failAgentTree = failAgentTree;
        this.stateLatch = sharedMemory.getStateLatch();
        this.agentExecutionStrategy = agentExecutionStrategy;
    }

    public void addAgent(IGSAAgent agent) {
        gsaAgents.add(agent);
    }

    public Goal exec(Goal state) {
        /* 引数で設定された現在の状態をスタックに設定 */
        input(0.0);
        initState();
        setState(state);
        syncState();
        output(0.0);
        fireLatch();
        input(0.0);
        fire();
        output(0.0);
        fireLatch();
        fireStackArrays();
        input(0.0);
        fire();
        output(0.0);
        removeReachGoal();
        fireStackArrays();
        if (isReachTreeGoal()) {
            logger.debug("reachTreeGoal");
            clearGoalStackAndTree();
            fireStackArrays();
            setGoal(target);
        }
        /* エージェントの学習 */
        double reward = 0;
        learn(false/*ゴール到達フラグ*/, reward);
        fireAgents();
        fireLatch();
        fire();
        return getGoal();
    }

    private void fireAgents() {
        final List<IGSAAgent> unusedAgents = new ArrayList<IGSAAgent>(gsaAgents);
        final List<IGSAAgent> usedAgents = new ArrayList<IGSAAgent>();
        final IGSAIteration i = new IGSAIteration() {
            AgentExecutionStrategy.Context ctx = agentExecutionStrategy.createContext(this);
  
            @Override
            public List<IGSAAgent> getAgents() {
                return gsaAgents;
            }
            @Override
            public List<IGSAAgent> getUnusedAgents() {
                return unusedAgents;
            }

            @Override
            public List<IGSAAgent> getUsedAgents() {
                return usedAgents;
            }

            @Override
            public boolean tryNext() {
                if (unusedAgents.size() == 0)
                    return false;
                IGSAAgent agent = ctx.nextAgent();
                usedAgents.add(agent);
                unusedAgents.remove(agent);
                AgentRunner runner = ((AgentRunner)agent.getSharedMemory());
                runner.input(0.0);
                runner.fire();
                runner.output(0.0);
                fireLatch();
                fireStackArrays();
                return true;
            }
        };
        while (i.tryNext());
    }

    private Goal getGoal() {
        return getGoalStack().getGoalValueArray();
    }

    private void fireStackArrays() {
    	log.trace("fireStackArrays");
        sharedMemory.fireAll();
    }

    private void fireLatch() {
    	log.trace("fireLatch");
        stateLatch.input(0.0);
        stateLatch.fire();
        stateLatch.output(0.0);
    }

    /**
     * スタック、ツリーをクリアし、各エージェントのreset()メソッドを呼び出し
     * ます。<BR>
     * 学習結果はリセットされません。<BR>
     * 学習結果を残したまま再スタートする場合など、学習、実行処理の連続性が
     * 途切れる場合の処理を行ないます。
     */
    public void reset() {
        /* 各エージェントのreset()の呼び出し */
        for (IGSAAgent agent: gsaAgents) {
            agent.reset();
        }
        /* スタック、ツリーをクリア */
        failAgentTree.clear();
        initState();
        getGoalStack().removeAllGoal();
        syncState();
        output(0.0);
        fireStackArrays();
    }

    /**
     * ゴールを共有メモリに設定します。
     * @param Vector goal ゴール
     */
    public void setGoal(Goal goal) {
        if (goal.size() != size)
            throw new IllegalArgumentException("goal.size() != nodeNum");
        target = goal;
        boolean[] useNode = new boolean[size];
        for (int i = 0; i < goal.size(); i++) {
            useNode[i] = goal.get(i) != null;
        }
        input(0.0);
        initState();
        State state = new State(goal, useNode);
        getGoalStack().pushGoal(GOAL_AGID, useNode, state);
        syncState();
        output(0.0);
        fireStackArrays();
        if (goal != null) {
            failAgentTree.addTreeNode(GOAL_AGID, goal);
        }
    }

    /**
     * 共有メモリを取得します
     */
    public ISharedMemory getSharedMemory() {
        return sharedMemory;
    }

    /**
     * 失敗ツリーを取得します
     */
    public FailAgentTree getFailAgentTree() {
        return failAgentTree;
    }

    /**
     * エージェントをすべて取得します
     */
    public List<IGSAAgent> getGSAAgents() {
        return gsaAgents;
    }

    public void addAgentEventListener(GSAAgentEventListener listener) {
        agentEventListeners.addEventListener(listener);
    }

    public void removeAgentEventListener(GSAAgentEventListener listener) {
        agentEventListeners.removeEventListener(listener);
    }

    public void fireAgentBeingExecuted(IGSAAgent agent) {
        agentEventListeners.fire("agentBeingExecuted", new GSAAgentEvent(this, agent));
    }
    
    public void fireAgentExecuted(IGSAAgent agent, IGSAAgent.Status status) {
        agentEventListeners.fire("agentExecuted", new GSAAgentEvent(this, agent, status));
    }

    ////////////////////////////////////////////////////////////////
    // private

    /**
     * ツリーのカレントノードの子ノードに、全てのエージェントからのノードが
     * 設定されている場合、カレントノードのゴールを失敗ゴールとして、スタック
     * から削除します。<BR>
     * ツリーは現在の位置を移動させる。
     */
    void removeUnsolvedGoal() {
        // 現在はある時点で自己設定ゴールが取得できるエージェントは１つに特定されるが
        // 複数のエージェントが自己設定ゴールを取得できるような場合、現在の実装では
        // 問題が発生する可能性がある。
        for (IGSAAgent agent: gsaAgents) {
            if (agent.removeSelfSetGoal()) {
                failAgentTree.moveParent();
                break;
            }
        }
    }


    ////////////////////////////////////////////////////////////////
    // 実行エージェントの選択

    /**
     * スタックから到達したゴールを削除します。
     */
    private void removeReachGoal() {
        boolean flagRemove;
        do {
            flagRemove = false;
            for (final IGSAAgent agent: gsaAgents) {
                ((BricaPerspective)agent.getSharedMemory()).input(0.0);
            }
            for (final IGSAAgent agent: gsaAgents) {
                if (agent.removeReachGoal()) {
                    // 2001.09.26 追加 
                    /* 到達ゴール削除時にツリーも操作 */
                    failAgentTree.removeCurrent();
                    agentEventListeners.fire("agentRemoved", new GSAAgentEvent(this, agent));
                    flagRemove = true;
                }
            }
            for (final IGSAAgent agent: gsaAgents) {
                ((BricaPerspective)agent.getSharedMemory()).output(0.0);
            }
        } while (flagRemove);
    }

    /**
     * ツリー上のいづれかの状態に到達したかどうか判定します。
     * @return boolean true:到達 false:未到達
     */
    private boolean isReachTreeGoal() {
        for (IGSAAgent agent: gsaAgents) {
            if (failAgentTree.containsGoal(agent.getId(), agent.getState()))
                return true;
        }
        return false;
    }

    /**
     * ゴールスタックとツリーの要素をすべてクリアします。
     */
    private void clearGoalStackAndTree() {
        initState();
        getGoalStack().removeAllGoal();
        syncState();
        output(0.0);
        fireStackArrays();
        failAgentTree.clear();
    }

    /**
     * エージェントの学習を行います。
     * @param boolean flagGoalReach
     * @param double p
     */
    private void learn(boolean flagGoalReach, double p) {
        /* 全エージェントの学習処理 */
        for (IGSAAgent agent: gsaAgents) {
            agent.learn(flagGoalReach, p);
        }
    }

    @Override
    public void doFire() {
        // TODO Auto-generated method stub
        
    }
}
