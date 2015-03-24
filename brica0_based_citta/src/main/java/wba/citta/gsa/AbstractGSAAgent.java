/**
g * AbstractGSAAgent.java
 * エージェントに共通の処理(共有メモリとの情報の受渡し等)を行なうクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * エージェントに共通の処理(共有メモリとの情報の受渡し等)を行なうクラス
 */
public abstract class AbstractGSAAgent implements IAgent, IGSAAgent {
    private static final Logger log = LoggerFactory.getLogger(AbstractGSAAgent.class);

    /**
     * エージェントID
     */
    protected final int id;

    /* 共有メモリ */
    protected ISharedMemory sharedMemory = null;

    /* エージェント毎のノードの使用、不使用を設定したbooleanの配列 */
    protected boolean[] useNode = null;

    /* エージェントが使用するノード数(useNodeのtrueの数) */
    protected int useNodeNum;

    /*
     * この抽象クラスの実行処理で前回出力したサブゴール
     * サブゴール未到達の判定に使用
     */
    private State lastSubgoal;

    /*
     * この抽象クラスの実装クラスの実行処理で前回出力したサブゴール
     * 実装クラスから出力されたサブゴールが必ずエージェントのサブゴール
     * として出力される分けではないので、lastSubgoalとは異なる
     * 重複サブゴールの判定に使用
     */
    private State lastYieldedSubgoal = null;



    /**
     * コンストラクタ
     * @param int agid  エージェントID
     * @param boolean[] useNode  ノードの使用、不使用を設定した配列
     * @param EngineeredSharedMemory sharedMemory  state・goalを管理する共有メモリ
     */
    public AbstractGSAAgent(int agid, boolean[] useNode, ISharedMemory sharedMemory) {
        this.id = agid;
        this.useNode = useNode;
        this.sharedMemory = sharedMemory;
        useNodeNum = getUseNodeNum(sharedMemory, useNode);
    }

    public int getId() {
        return id;
    }

    ///////////////////////////////////////////////////////////////////
    // public
    // 2001.12.14 追加 miyamoto
    /**
     * イベント情報を学習データとして利用し、学習処理を行ないます。
     * @param String eventFileName イベント情報の記述されたファイル名
     */
    public void learnFromEventFile(String eventFileName) throws IOException {
        log.debug("Load Event File...");
        FileReader fr = new FileReader(eventFileName);
        BufferedReader br = new BufferedReader(fr);
        try {
            while( br.ready() ) {
                /* イベントファイルからイベントを１つ取得 */
                String event = br.readLine();

                /* 取得したイベントをエージェントの状態に変換 */
                StringTokenizer stringTokenizer
                         = new StringTokenizer(event, ",");
                List<Integer> eventState = new LinkedList<Integer>();
                while(stringTokenizer.hasMoreTokens()) {
                    eventState.add(Integer.valueOf(stringTokenizer.nextToken()));
                }

                /* イベントを学習処理 */
                learn(new State(eventState), false, 0);
            }
        } finally {
            br.close();
            fr.close();
        }
        /* 通常の学習とは連続性がないのでリセットする */
        reset();
    }

    /**
     * エージェント固有の学習処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param boolean flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public abstract void learn(State state, boolean flagGoalReach, double profit);

    /**
     * 学習処理を行ないます。<BR>
     * (引数のflagGoalReach、profitは、連想エージェントの強化学習用。
     * ＣＤエージェントでは使用していない。)
     * @param flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public void learn(boolean flagGoalReach, double profit) {
        learn(getState(), flagGoalReach, profit);
    }
   
    /**
     * エージェント固有の実行処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector サブゴール
     */
    public abstract State execProcess(State state, State goalElementArray);

    /**
     * 実行処理を行ないます。<BR>
     * 共有メモリからstate、goalを取得し、ユーザ定義の実行処理(protectedの
     * exec(Vector, Vector)経由で、abstructのexecProcess(Vector, Vector)を
     * 呼び出し)を行ない、ユーザ定義の実行処理で生成されたsubgoalを共有メモリ
     * に設定します。<BR>
     * @return int 実行処理の結果を示すID<BR>
     * AGR_SUCCESS、AGR_REACH_GOAL、AGR_UNREACH_SUBGOAL、AGR_SEARCH_FAIL、
     * AGR_SAME_SUBGOAL、AGR_SAME_GOALのいづれか
     */
    public Status checkExec() {

        final State state = getState();

        /* ゴールを取得 選択は内部で */
        final State goal = getGoal();

        /* 実行処理を行うか */
        return shouldExec(state, goal);
    }

    /**
     * 実行処理を行ないます。<BR>
     * 共有メモリからstate、goalを取得し、ユーザ定義の実行処理(protectedの
     * exec(Vector, Vector)経由で、abstructのexecProcess(Vector, Vector)を
     * 呼び出し)を行ない、ユーザ定義の実行処理で生成されたsubgoalを共有メモリ
     * に設定します。<BR>
     * @return int 実行処理の結果を示すID<BR>
     * AGR_SUCCESS、AGR_REACH_GOAL、AGR_UNREACH_SUBGOAL、AGR_SEARCH_FAIL、
     * AGR_SAME_SUBGOAL、AGR_SAME_GOALのいづれか
     */
    public Status exec() {
        State state = getState();

        /* ゴールを取得 選択は内部で */
        State goal = getGoal();

        log.debug("[{}] state: {}", getId(), state);
        log.debug("[{}] goalElementArray: {}", getId(), goal);

        /* 実行処理を行うか */
        Status isExexMode = shouldExec(state, goal);
        if( isExexMode != Status.AGR_SUCCESS ) {
            lastSubgoal = null;
            return isExexMode;
        }

        /* 抽象メソッド */
        State subgoal = exec(state, goal);

        log.debug("[{}] subgoal: {}", getId(), subgoal);

        Status isReturnMode = yieldsSubgoal(subgoal, goal);
        if (isReturnMode != Status.AGR_SUCCESS) {
            subgoal = null;
        }
        setSubgoal(subgoal);
        return isReturnMode;
    }

    /**
     * 到達ゴールの削除を行ないます。<BR>
     * 自己設定ゴール(接続ノード全てを自ら設定しているゴール)に現在の状態が
     * 到達した場合、そのゴールをゴールスタックから削除します。
     * @return boolean true：自己設定ゴールに到達し、ゴールスタックから削除
     * した場合
     */
    public boolean removeReachGoal() {
        /* 判定に使用するだけなので、参照を取得 */
        List<Integer> state = getState();
        final State goal =  sharedMemory.getGoalStack().getCurrentGoal(id, useNode);
        /* 自らが設定したサブゴールに到達していれば削除 */
        if (state.equals(goal)) {
            return removeGoal();
        } else {
            return false;
        }
    }

    /**
     * 自己設定ゴール(接続ノード全てを自ら設定しているゴール)がゴールスタック
     * にあれば、そのゴールをスタックから削除します。
     * @return boolean true:自己設定ゴールがあり、削除できた場合<BR>
     * false:自己設定ゴールがないため、削除できなかった場合<BR>
     */
    public boolean removeSelfSetGoal() {
        return removeGoal();
    }


    public String toString() {
        final StringBuffer retval = new StringBuffer();
        retval.append(getClass().getName());
        retval.append('(');
        retval.append("id=");
        retval.append(getId());
        retval.append(",");
        retval.append("state=");
        retval.append(getState());
        retval.append(')');
        return retval.toString();
    }

    ////////////////////////////////////////////////////////////
    // protected

    /**
     * 実行処理を行ないます。
     * 
     */
    protected State exec(State state, State goalElementArray) {;
        return execProcess(state, goalElementArray);
    }


    ////////////////////////////////////////////////////////////
    // private

    /**
     * 実行処理を行うかどうかの判定
     * @param Vector state 現在の状態
     * @param Vector goal ゴールスタックの状態
     * @return int サブゴール未出力条件
     */
    private Status shouldExec(State state, State goal) {
        /*
         * ゴールに到達していれば実行処理を行わない
         * ゴールにはnullの要素がある可能性があるので、null以外の要素で判定
         */
        if (Util.equalsValidElement(state, goal) ) {
            return Status.AGR_REACH_GOAL;
        }

        /*
         * 前サイクルの処理が失敗したら実行処理を行わない
         * 処理エージェントを切り替えるため
         */
        if( lastSubgoal != null && !state.equals(lastSubgoal) ) {
            return Status.AGR_UNREACH_SUBGOAL;
        }

        return Status.AGR_SUCCESS;
    }

    /**
     * サブゴールを出力するかどうかの判定
     * @param Vector subgoal サブゴール
     * @param Vector goal 現在のゴール
     * @return Status サブゴール未出力条件
     */
    private Status yieldsSubgoal(State subgoal, State goal) {
        /* サブゴールを出力できない場合 */
        if (subgoal == null) {
            lastYieldedSubgoal = subgoal;
            return Status.AGR_SEARCH_FAIL;
        }

        /* サブゴールが前サイクルのサブゴールと同じ場合は出力しない */
        if ((lastYieldedSubgoal != null) &&
                (lastYieldedSubgoal.equals(subgoal))) {
            lastYieldedSubgoal = subgoal;
            return Status.AGR_SAME_SUBGOAL;
        }
        lastYieldedSubgoal = subgoal;

        /* サブゴールがゴールと同じ場合は出力しない */
        if(subgoal.equals(goal)) {
            return Status.AGR_SAME_GOAL;
        }

        return Status.AGR_SUCCESS;
    }


    /**
     * このエージェントの使用するノード数を取得します。
     * @param int 使用するノード数
     */
    private static int getUseNodeNum(ISharedMemory sharedMemory, boolean[] useNode) {
        int counter = 0;
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if(useNode[i]) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * 現在の状態(State)を共有メモリから取得します。
     * @return Vector 現在の状態
     */
    public State getState() {
        return sharedMemory.getLatch().getState(useNode);
    }

    public State getLastSubgoal() {
        return lastSubgoal;
    }


    /**
     * ゴールの削除
     * 接続先のノード全てから1要素づつ削除
     */
    private boolean removeGoal() {
        return sharedMemory.getGoalStack().removeGoal(id, useNode);
    }


    /**
     * ゴールを取得します。
     * 他のエージェントが設定したゴールがある場合は、他のエージェントが設定
     * したゴール。なければ、自らが設定したゴールとゴールとして利用します。
     * @return Vector SharedMemory.GoalStackElementのVector
     */
    private State getGoal() {
        final IGoalStack goalStack = sharedMemory.getGoalStack();
        return goalStack.getCurrentGoal(0, useNode);
    }

    /**
     * サブゴールを設定します。
     * @param Vector subgoal サブゴール(GoalValueArray)
     */
    private void setSubgoal(State subgoal) {
        if (subgoal != null) {
            sharedMemory.getGoalStack().pushGoal(id, useNode, subgoal);
        }
        lastSubgoal = subgoal;
    }

    public ISharedMemory getSharedMemory() {
        return sharedMemory;
    }
}
