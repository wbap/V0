/**
 * Agent.java
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
public abstract class Agent {
    private static final Logger log = LoggerFactory.getLogger(Agent.class);

    /*
     * エージェントの実行処理の結果を示すID
     * 成功以外はサブゴール未出力条件
     */
    public static enum Status {
        NONE(-1),
                
        /**
         * 実行処理成功
         */ 
        AGR_SUCCESS(0),
                
        /**
         * すでにFailAgentTreeに設定されていることによりサブゴール未出力
         */
        AGR_FAIL_AGENT(1),
    
        /**
         * ゴール到達によりサブゴール未出力
         */
        AGR_REACH_GOAL(2),
    
        /**
         * サブゴール未到達によりサブゴール未出力
         */
        AGR_UNREACH_SUBGOAL(3),

        /**
         * 探索不能によりサブゴール未出力
         */
        AGR_SEARCH_FAIL(4),
    
        /** 
         * 重複サブゴールによりサブゴール未出力
         */
        AGR_SAME_SUBGOAL(5),
    
        /**
         * 同一ゴールによりサブゴール未出力
         */
        AGR_SAME_GOAL(6);
        
        private int value;

        private Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * エージェントID
     */
    protected final int id;

    /* 共有メモリ */
    protected SharedMemory sharedMemory = null;

    /* エージェント毎のノードの使用、不使用を設定したbooleanの配列 */
    protected boolean[] useNode = null;

    /* エージェントが使用するノード数(useNodeのtrueの数) */
    protected int useNodeNum;

    /*
     * この抽象クラスの実行処理で前回出力したサブゴール
     * サブゴール未到達の判定に使用
     */
    private List<Integer> subgoalOld;

    /*
     * この抽象クラスの実装クラスの実行処理で前回出力したサブゴール
     * 実装クラスから出力されたサブゴールが必ずエージェントのサブゴール
     * として出力される分けではないので、subgoalOldとは異なる
     * 重複サブゴールの判定に使用
     */
    private List<Integer> impleAgSubgoalOld = null;


    ///////////////////////////////////////////////////////////////////
    // コンストラクタ

    /**
     * コンストラクタ
     * @param int agid  エージェントID
     * @param boolean[] useNode  ノードの使用、不使用を設定した配列
     * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
     */
    public Agent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
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
                learn(eventState, false, 0);
            }
        } finally {
            br.close();
            fr.close();
        }
        /* 通常の学習とは連続性がないのでリセットする */
        reset();
    }

    /**
     * 学習処理を行ないます。<BR>
     * (引数のflagGoalReach、profitは、連想エージェントの強化学習用。
     * ＣＤエージェントでは使用していない。)
     * @param flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public void learn(boolean flagGoalReach, double profit) {
        List<Integer> state = getState();
        learn(state, flagGoalReach, profit);
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
    public Status checkExec() {

        final List<Integer> state = getState();

        /* ゴールを取得 選択は内部で */
        final List<SharedMemory.GoalStackElement> goalElementArray = getGoalElementArray();

        /* 実行処理を行うか */
        return isExec(state, goalElementArray);
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

        List<Integer> state = getState();

        /* ゴールを取得 選択は内部で */
        List<SharedMemory.GoalStackElement> goalElementArray = getGoalElementArray();

        log.debug("[{}] state: {}", getId(), state);
        log.debug("[{}] goalElementArray: {}", getId(), goalElementArray);

        /* 実行処理を行うか */
        Status isExexMode = isExec(state, goalElementArray);
        if( isExexMode != Status.AGR_SUCCESS ) {
            subgoalOld = null;
            return isExexMode;
        }

        /* 抽象メソッド */
        List<Integer> subgoal = exec(state, goalElementArray);

        log.debug("[{}] subgoal: {}", getId(), subgoal);

        Status isReturnMode = isReturnSubgoal(subgoal, goalElementArray);
        if( isReturnMode != Status.AGR_SUCCESS ) {
            subgoalOld = null;
            return isReturnMode;
        }

        setSubgoal(subgoal);

        subgoalOld = subgoal;
        return Status.AGR_SUCCESS;
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

        List<SharedMemory.GoalStackElement> selfSetGoalElementArray = getSelfSetGoalElementArray();
        if (selfSetGoalElementArray == null)
            return false;
        List<Integer> selfSetGoalValueArray = getGoalValueArray(selfSetGoalElementArray);

        /* 自らが設定したサブゴールに到達していれば削除 */
        if( state.equals(selfSetGoalValueArray) ) {
            removeGoal();
            return true;
        }
        return false;
    }

    /**
     * 自己設定ゴール(接続ノード全てを自ら設定しているゴール)がゴールスタック
     * にあれば、そのゴールをスタックから削除します。
     * @return boolean true:自己設定ゴールがあり、削除できた場合<BR>
     * false:自己設定ゴールがないため、削除できなかった場合<BR>
     */
    public boolean removeSelfSetGoal() {
        final List<SharedMemory.GoalStackElement> selfSetGoalStackElement = getSelfSetGoalElementArray();
        if(selfSetGoalStackElement != null) {
            removeGoal();
            return true;
        }
        return false;
    }

    /**
     * 自ら設定したゴールの状態をスタックから取得します。<BR>
     * エージェントが接続しているノードの要素全てを自ら設定している場合に、
     * それらをエージェントの状態として取得します。<BR>
     * 他のエージェントが設定している要素があれば、nullが返ります。
     * @return Vector ゴールの状態<BR>
     */
// GSAクラス、ManualAgentクラスからも利用するためpublic化
    public List<SharedMemory.GoalStackElement> getSelfSetGoalElementArray() {
        final SharedMemory.GoalStack goalStack = sharedMemory.getGoalStack(); 
        List<SharedMemory.GoalStackElement> selfGoal = new ArrayList<SharedMemory.GoalStackElement>();
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if( useNode[i] == true ) {
                SharedMemory.GoalStackElement v = (SharedMemory.GoalStackElement)goalStack.getGoalForNode(i);
                if( (v != null) && (v.agid == id) ) {
                    selfGoal.add(v);
                }else {
                    selfGoal = null;
                    break;
                }
            }
        }
        return selfGoal;
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
    // 抽象メソッド

    /**
     * エージェント固有の学習処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param boolean flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public abstract void learn(List<Integer> state, boolean flagGoalReach,
            double profit);

    /**
     * エージェント固有の実行処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector サブゴール
     */
    public abstract List<Integer> execProcess(List<Integer> state, List<SharedMemory.GoalStackElement> goalElementArray);

    /**
     * 学習結果をファイルに保存します。
     * @param String fileNameファイル名
     * @throws IOException 
     */
    public abstract void save(String fileName) throws IOException;

    /**
     * 学習結果をファイルから読み込みます。
     * @param String fileName ファイル名
     */
    public abstract void load(String fileName) throws IOException;

    /**
     * GSAクラスのreset()メソッドから呼び出されます。<BR>
     * 状態遷移の履歴のクリア、前サイクルの保持情報のクリアなど、学習、
     * 実行処理の連続性が途切れる場合に行なう情報のクリアなどの処理を
     * 記述します。<BR>
     */
    public abstract void reset();

    /**
     * GSAクラスによって、実行処理を行なうエージェントが自身のエージェント
     * から他のエージェントに切り替えられたときに呼び出されます。<BR>
     * このため、各エージェントの実行処理で利用する前サイクルの保持情報
     * など、実行処理の連続性に依存して保持している情報のクリアなどの処理を
     * 記述します。<BR>
     */
    public abstract void suspend();

    ////////////////////////////////////////////////////////////
    // protected

    /**
     * 実行処理を行ないます。
     * 
     */
    protected List<Integer> exec(List<Integer> state, List<SharedMemory.GoalStackElement> goalElementArray) {;
        return execProcess(state, goalElementArray);
    }

    /**
     * SharedMemory.GoalStackElementのVectorからGoalValueのVectorを取得します。
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector            goalValueのVector
     */
    protected List<Integer> getGoalValueArray(List<SharedMemory.GoalStackElement> goalElementArray) {
        assert goalElementArray != null;
        List<Integer> goalValueArray = new ArrayList<Integer>();
        for(int i = 0; i < goalElementArray.size(); i++) {
            SharedMemory.GoalStackElement e = (SharedMemory.GoalStackElement)goalElementArray.get(i);
            if(e != null) {
                goalValueArray.add(e.value);
            }else {
                goalValueArray.add(null);
            }
        }
        return goalValueArray;
    }


    ////////////////////////////////////////////////////////////
    // private

    /**
     * 実行処理を行うかどうかの判定
     * @param Vector state 現在の状態
     * @param Vector goalElementArray ゴールスタックの状態
     * @return int サブゴール未出力条件
     */
    private Status isExec(List<Integer> state, List<SharedMemory.GoalStackElement> goalElementArray) {

        /*
         * ゴールに到達していれば実行処理を行わない
         * ゴールにはnullの要素がある可能性があるので、null以外の要素で判定
         */
        List<Integer> goalValue = getGoalValueArray(goalElementArray);
        if( Util.equalsValidElement(state, goalValue) ) {
            return Status.AGR_REACH_GOAL;
        }

        /*
         * 前サイクルの処理が失敗したら実行処理を行わない
         * 処理エージェントを切り替えるため
         */
        if( subgoalOld != null && !state.equals(subgoalOld) ) {
            return Status.AGR_UNREACH_SUBGOAL;
        }

        return Status.AGR_SUCCESS;
    }

    /**
     * サブゴールを出力するかどうかの判定
     * @param Vector sugoal サブゴール
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return int サブゴール未出力条件
     */
    private Status isReturnSubgoal(List<Integer> subgoal, List<SharedMemory.GoalStackElement> goalElementArray) {
        /* サブゴールを出力できない場合 */
        if(subgoal == null) {
            impleAgSubgoalOld = subgoal;
            return Status.AGR_SEARCH_FAIL;
        }

        /* サブゴールが前サイクルのサブゴールと同じ場合は出力しない */
        if( (impleAgSubgoalOld != null) &&
                (impleAgSubgoalOld.equals(subgoal)) ) {
            impleAgSubgoalOld = subgoal;
            return Status.AGR_SAME_SUBGOAL;
        }
        impleAgSubgoalOld = subgoal;

        /* サブゴールがゴールと同じ場合は出力しない */
        List<Integer> goal = getGoalValueArray(goalElementArray);
        if(subgoal.equals(goal)) {
            return Status.AGR_SAME_GOAL;
        }

        return Status.AGR_SUCCESS;
    }


    /**
     * このエージェントの使用するノード数を取得します。
     * @param int 使用するノード数
     */
    private static int getUseNodeNum(SharedMemory sharedMemory, boolean[] useNode) {
        int counter = 0;
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if(useNode[i]) {
                counter++;
            }
        }
        return counter;
    }


    /**
     * ゴールの削除
     * 接続先のノード全てから1要素づつ削除
     */
    private void removeGoal() {
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if( useNode[i] == true ) {
                sharedMemory.getGoalStack().removeGoal(i, id);
            }
        }
    }

    /**
     * 現在の状態(State)を共有メモリから取得します。
     * @return Vector 現在の状態
     */
    public List<Integer> getState() {
        // scatter & gather
        List<Integer> stateForAllNodes = sharedMemory.getLatch().getState();
        List<Integer> retval = new ArrayList<Integer>(useNodeNum);
        for (int i = 0; i < stateForAllNodes.size(); i++) {
            if (useNode[i])
                retval.add(stateForAllNodes.get(i));
        }
        return retval;
    }

    /**
     * ゴールを取得します。
     * 他のエージェントが設定したゴールがある場合は、他のエージェントが設定
     * したゴール。なければ、自らが設定したゴールとゴールとして利用します。
     * @return Vector SharedMemory.GoalStackElementのVector
     */
    private List<SharedMemory.GoalStackElement> getGoalElementArray() {
// 自己設定意図、他設定意図の区別を行なう設定
        List<SharedMemory.GoalStackElement> goalElementArray = getOtherSetGoalElementArray();
        if(goalElementArray == null)  {
            goalElementArray = getSelfSetGoalElementArray();
        }
        return goalElementArray;

// 自己設定意図、他設定意図の区別を行なわない設定
//        return getGoalElementArray2();
    }


    /**
     * 他のエージェントが設定したゴールの状態をスタックから取得します。
     * エージェントが接続しているノードから他のエージェントが設定したもの
     * のみを、エージェントの状態として取得します。自ら設定している要素はnull
     * を設定します。
     * @return Vector SharedMemory.GoalStackElementのVector
     * Vectorの全ての要素がnullならVector自体をnullに設定して返す。
     */
    private List<SharedMemory.GoalStackElement> getOtherSetGoalElementArray() {
        List<SharedMemory.GoalStackElement> otherGoal = new ArrayList<SharedMemory.GoalStackElement>();
        int nullNum = 0;
        final SharedMemory.GoalStack goalStack = sharedMemory.getGoalStack();
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if( useNode[i] == true ) {
                SharedMemory.GoalStackElement v = (SharedMemory.GoalStackElement)goalStack.getGoalForNode(i);
                if( (v != null) && (v.agid != id) ) {
                    otherGoal.add(v);
                }else {
                    otherGoal.add(null);
                    nullNum++;
                }
            }
        }
        /* 要素がすべてnullならVector自体をnullに設定 */
        if(nullNum == useNodeNum) {
            otherGoal = null;
        }

        return otherGoal;
    }

    /**
     * サブゴールを設定します。
     * @param Vector subgoal サブゴール(GoalValueArray)
     */
    private void setSubgoal(List<Integer> subgoal) {
        if(subgoal != null) {
            pushGoalToStack(subgoal);
        }
    }

    /**
     * サブゴールをスタックに設定します。
     * @param Vector subgoal サブゴール(GoalValueArray)
     */
    private void pushGoalToStack(List<Integer> subgoal) {
        int index = 0;
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if( useNode[i] == true ) {
                Integer integer = (Integer)subgoal.get(index);
                if(integer != null) {
                    int value = integer.intValue();
                    SharedMemory.GoalStackElement elm = new SharedMemory.GoalStackElement(value, id);
                    sharedMemory.getGoalStack().pushGoal(i, elm);
                }
                index++;
            }
        }
    }
}
