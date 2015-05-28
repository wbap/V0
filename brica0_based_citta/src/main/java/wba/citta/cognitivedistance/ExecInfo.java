/**
 * ExecInfo.java
 * 実行時の情報を管理するクラスです
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.03 BSC miyamoto
 */
package wba.citta.cognitivedistance;

/**
 * 実行時の情報を管理するクラス
 */
public class ExecInfo {

    private Integer nodeID;           /* 現在の状態 */
    private Integer goalNodeID;       /* ゴールの状態 */
    private Integer subgoal;          /* 上位層からのサブゴールの状態  */
    private Integer nextNodeID;       /* 次の状態 */
    private int processID;            /* 次の状態を出力している処理のID */
    private boolean flagRenewSubgoal; /* サブゴールが変更された時のフラグ */

    /**
     * 実行時の情報を取得します。
     * @return int[] 実行時のノードに関する情報
     *               値のない状態については-1が設定される
     *               int[0] 現在の状態のID
     *               int[1] ゴールの状態のID
     *               int[2] 上位層からのサブゴールの状態のID
     *               int[3] 次の状態のID
     *               int[4] 次の状態を出力している処理のID
     *               int[5] サブゴールが更新されているか
     *                      0：更新されていない 1：更新されている
     */
    public int[] getExecInfo() {
        int[] execInfo = new int[6];

        if(nodeID != null) {
            execInfo[0] = nodeID.intValue();
        }else {
            execInfo[0] = -1;
        }

        if(goalNodeID != null) {
            execInfo[1] = goalNodeID.intValue();
        }else {
            execInfo[1] = -1;
        }

        if(subgoal != null) {
            execInfo[2] = subgoal.intValue();
        }else {
            execInfo[2] = -1;
        }

        if(nextNodeID != null) {
            execInfo[3] = nextNodeID.intValue();
        }else {
            execInfo[3] = -1;
        }

        execInfo[4] = processID;

        if(flagRenewSubgoal == false) {
            execInfo[5] = 0;
        }else {
            execInfo[5] = 1;
        }

        return execInfo;
    }

    /**
     * 現在の状態を設定します。
     * @param Integer nodeID 現在の状態のID
     */
    public void setNodeID(Integer nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * ゴールの状態を設定します。
     * @param Integer goalNodeID ゴールの状態のID
     */
    public void setGoalNodeID(Integer goalNodeID) {
        this.goalNodeID = goalNodeID;
    }

    /**
     * 次の状態と、その状態の取得に関する情報を設定します。
     * @param Integer nextNodeID       次の状態のID
     * @param int processID            次の状態を出力している処理のID
     * @param Integer subgoal          上位層からのサブゴールの状態のID
     * @param boolean flagRenewSubgoal サブゴールが更新されているか
     */
    public void setNextNodeID(Integer nextNodeID, int processID,
            Integer subgoal, boolean flagRenewSubgoal) {
        this.nextNodeID = nextNodeID;
        this.processID = processID;
        this.subgoal = subgoal;
        this.flagRenewSubgoal = flagRenewSubgoal;
    }

    /**
     * 各パラメータを初期化します。
     */
    public void paramReset() {
// 現在の状態はクリアしない 2001.03.08 修正 miyamoto 
//        nodeID = null;
        goalNodeID = null;
        subgoal = null;
        nextNodeID = null;
        flagRenewSubgoal = false;
    }

}
