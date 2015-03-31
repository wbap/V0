
/** 
 * GSA.java
 * GSAの本体
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.gsa;

import java.io.IOException;
import java.util.*;

import wba.citta.util.EventPublisherSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GSAの本体
 */
public class GSA implements GSAAgentEventSource {
    public final static int AGENT_COUNT = 8;
    public final static int DO_NOTHING = 0;
    public final static int EXEC = 1;

    private static Logger log = LoggerFactory.getLogger(GSA.class);

    /* 外部から設定されたゴール用の仮のエージェントID */
    private static final int GOAL_AGID = 0;
 
    /* 外部から設定されたゴール */
    private Goal target = null;

    /* ノード数 */
    private int nodeNum;

    /** 全エージェントの配列 */
    List<IAgent> agents = new ArrayList<IAgent>();

    /** GSAエージェントの配列 */
    List<IGSAAgent> gsaAgents = new ArrayList<IGSAAgent>();

    /* 実行エージェントの選択方法 0:配列の順 1:ランダム */
    int agentSelectMode = 1;

    /* 到達に失敗したゴールをツリーで管理するクラス */
    FailAgentTree failAgentTree = null;
   
    // SharedMemory Module
    /* State・Goalを管理する共有メモリ */
    ISharedMemory sharedMemory = null;

    AgentExecutionStrategy agentExecutionStrategy;

    EventPublisherSupport<GSAAgentEvent, GSAAgentEventListener> agentEventListeners = new EventPublisherSupport<>(GSAAgentEvent.class, GSAAgentEventListener.class);
    private AgentFactory agentFactory;    
    
    ////////////////////////////////////////////////////////////////
    // コンストラクタ 初期化メソッド

    /**
     * コンストラクタ
     * @param String propFileName GSAの設定ファイル名
     */
    public GSA(AgentFactory agentFactory, List<AgentInfo> agentInfoList, ISharedMemory sharedMemory, FailAgentTree failAgentTree, AgentExecutionStrategy agentExecutionStrategy) throws IOException {
        this.agentFactory = agentFactory;
        this.sharedMemory = sharedMemory;
        this.failAgentTree = failAgentTree;
        this.agentExecutionStrategy = agentExecutionStrategy;
        nodeNum = sharedMemory.getSize();
        buildGSAAgents(agentInfoList, nodeNum);
        sharedMemory.bind(this);
    }

    private static String stringizeUseNode(boolean[] useNode) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < useNode.length; i++) {
            if (i > 0)
                buf.append(',');
            buf.append(useNode[i] ? '1': '0');
        }
        return buf.toString();
    }

    /**
     * エージェントの初期化
     */
    private void buildGSAAgents(List<AgentInfo> agentInfoList, int nodeNum) throws IOException {
        for (AgentInfo agentInfo: agentInfoList) {
            final AgentType agentType = agentInfo.getType();
            final int agentId = agentInfo.getId();
            IGSAAgent agent = null;
            if (agentType != AgentType.MANUAL) {
                final boolean[] useNode = agentInfo.getUseNode();
                final String eventFileName = agentInfo.getEventFileName();
    
                log.info(String.format("agentID=%d, agentType=%s, useNode=%s", agentId, agentType, stringizeUseNode(useNode)));
                agent = agentFactory.createInstance(agentType, agentId, useNode, sharedMemory);
                if (eventFileName != null) {
                    log.info(String.format("loading learning data from eventFile: %s", eventFileName));
                    agent.learnFromEventFile(eventFileName);
                }
            } else {
                // マニュアルエージェント
                boolean[] allNode = new boolean[nodeNum];
                for(int i = 0; i < allNode.length; i++) {
                    allNode[i] = true;
                }
                agent = new ManualAgent(agentId, allNode, sharedMemory);
            }
            addAgent(agent);
        }
    }

    ////////////////////////////////////////////////////////////////
    // public

    public void addAgent(IAgent agent) {
        agents.add(agent);
        if (agent instanceof AbstractGSAAgent) {
            gsaAgents.add((AbstractGSAAgent)agent);
        }
    }

    /**
     * 実行処理を行ないます。
     * @param Vector state 現在の状態
     * @return Vector サブゴール
     */
    public Goal exec(Goal state) {
        /* 引数で設定された現在の状態をスタックに設定 */
        sharedMemory.getLatch().setState(state);

        /*
         * スタック上のゴールに到達した場合、そのゴールをスタック・ツリーから
         * 削除
         */
        removeReachGoal();

        /*
         * ツリー上のゴールに到達した場合、スタック、ツリーをクリア
         */
        if( isReachTreeGoal() ) {
            clearGoalStackAndTree();
            setGoal(target);
        }

        /* エージェントの学習 */
        double reward = 0;
        learn(false/*ゴール到達フラグ*/, reward);        
        
        /*
         * 実行可能なエージェントが選択されるか、すべてのエージェントが失敗
         * するまで繰り返し
         */
        IGSAIteration iteration = new GSAIteration(this);
        while (iteration.tryNext());

        /* ゴールを返す */
        return sharedMemory.getGoalStack().getGoalValueArray();
    }
    

    /**
     * 学習結果をファイルに保存します。<BR>
     * エージェントごとの学習結果保存処理を呼び出します。<BR>
     * 各エージェントのファイル名は引数で設定されたファイル名に各エージェントの
     * IDを追加したものになります。(fileName+agid.dat)
     * @param String fileName ファイル名
     */
    public void save(String fileName) throws IOException {
        for (IGSAAgent agent: gsaAgents) {
            agent.save(fileName + agent.getId() + ".dat");
        }
    }

    /**
     * 学習結果をファイルから読み込みます。<BR>
     * エージェントごとの学習結果読み込み処理を呼び出します。<BR>
     * 各エージェントのファイル名は引数で設定されたファイル名に各エージェントの
     * IDを追加したものになります。(fileName+agid.dat)
     * @param String fileName ファイル名
     */
    public void load(String fileName) throws IOException {
        for (IGSAAgent agent: gsaAgents) {
            agent.load(fileName + agent.getId() + ".dat");
        }
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
        sharedMemory.getGoalStack().removeAllGoal();
    }

    /**
     * ゴールを共有メモリに設定します。
     * @param Vector goal ゴール
     */
    public void setGoal(Goal goal) {
        if (goal.size() != nodeNum)
            throw new IllegalArgumentException("goal.size() != nodeNum");
        target = goal;
        boolean[] useNode = new boolean[nodeNum];
        for (int i = 0; i < goal.size(); i++) {
            useNode[i] = goal.get(i) != null;
        }
        State state = new State(goal, useNode);
        sharedMemory.getGoalStack().pushGoal(GOAL_AGID, useNode, state);
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
                if (agent.removeReachGoal()) {
                    // 2001.09.26 追加 
                    /* 到達ゴール削除時にツリーも操作 */
                    failAgentTree.removeCurrent();
                    agentEventListeners.fire("agentRemoved", new GSAAgentEvent(this, agent));
                    flagRemove = true;
                }
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
        sharedMemory.getGoalStack().removeAllGoal();
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
}
