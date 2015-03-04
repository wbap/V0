/**
 * ManualAgent.java
 * 手動で動作するエージェント
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.util.*;

/**
 * 手動で動作するエージェント
 */
public class ManualAgent extends Agent {

    private ManualAgentFrame manualAgentFrame = null;

    private SharedMemory sharedMemory = null;
    private boolean[] useNode = null;

    /* このクラスから出力したサブゴールのリスト */
    private List<List<Integer>> subgoalList = null;

    /**
     * コンストラクタ
     * @param int agid  エージェントID
     * @param boolean[] useNode  ノードの使用、不使用を設定した配列
     * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
     */
    public ManualAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
        super(agid, useNode, sharedMemory);


        this.sharedMemory = sharedMemory;
        this.useNode = useNode;

        subgoalList = new LinkedList<List<Integer>>();

        manualAgentFrame = new ManualAgentFrame(sharedMemory.getSize());

    }


    ///////////////////////////////////////////////////////////////////////
    // public

    /**
     * エージェント固有の実行処理を行ないます。<BR>
     * 実行処理を行なうエージェントとして選択されたときに、GUIに設定された
     * ゴール値を取得して、その値をサブゴールとして返します。
     * @param Vector state 現在の状態
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector サブゴール
     */
    public List<Integer> execProcess(List<Integer> state, List<SharedMemory.GoalStackElement> goalElement) {
        List<Integer> v = manualAgentFrame.getSubgoal();

        if(v != null) {
            subgoalList.add(v);
        }

        manualAgentFrame.clearSubgoal();
        return v;
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void learn(List<Integer> state, boolean flagGoalReach, double profit) {
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void reset() {
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void save(String fileName) { 
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void load(String fileName) {
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void suspend() {
    }



    /**
     * スタック上の自ら設定したゴールに到達した場合、そのゴールをスタック・
     * ツリーから削除します。<BR>
     * 通常のエージェントではゴールは接続ノード全てに出力するが、ManualAgent
     * ではゴールとして接続ノードの一部分を出力することがあるので到達判定を
     * 部分一致で行う必要がある。このためAgentクラスのメソッドをオーバーライド
     * し、処理内容を変えている。
     * @return boolean 到達した場合true 
     */
// 部分ゴールの判定のため、実際にスタックに出力したゴールと異なる場合でも、
// null以外の要素が同じであれば、到達とみなし削除してしまう。
// 対応方法：出力したサブゴールをリストで保持するようにし、保持してあるサブ
// ゴールとの一致を判定するようにする。
    public boolean removeReachGoal() {
        List<Integer> state = getState();

        List<SharedMemory.GoalStackElement> selfSetGoalElementArray = getSelfSetGoalElementArray();
        List<Integer> selfSetGoalValueArray = getGoalValueArray(selfSetGoalElementArray);

        if (subgoalList.size() > 0) {
            List<Integer> lastSetSubgoal = subgoalList.get(subgoalList.size() - 1);

            /* 自らが設定したサブゴールに到達していれば削除 */
            if( lastSetSubgoal.equals(selfSetGoalValueArray) ) {
                if( Util.equalsValidElement(state, selfSetGoalValueArray) ) {
                    /* 削除も自己設定部分のみ行う */
                    removeGoal(selfSetGoalValueArray);
                    subgoalList.remove(subgoalList.size() - 1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ゴールの削除を行ないます。
     * 引数で設定されたゴールの有効な要素が設定されているノードのみ
     * ゴールスタックから削除
     * @param Vector goal ゴールスタックから削除するゴール
     */
    private void removeGoal(List<Integer> goal) {
        int useNodeIndex = 0;
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            if( useNode[i] == true ) {
                if(goal.get(useNodeIndex) != null) {
                    sharedMemory.getGoalStack().removeGoal(i, id);
                }
                useNodeIndex++;
            }
        }
    }

}

