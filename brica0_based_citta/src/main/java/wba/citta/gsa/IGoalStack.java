package wba.citta.gsa;

import java.util.List;

public interface IGoalStack {

    /**
     * 共有メモリで扱うゴールの情報の単位
     */
    public static class GoalStackElement {
        /**
         * ゴールの値
         */
        public final int value;
    
        /**
         * ゴールを設定したエージェントID
         */
        public final int agid;
    
        /**
         * コンストラクタ
         * @param int value ゴールの値
         * @param int agid  設定したエージェントのID
         */
        public GoalStackElement(int agid, int value) {
            this.agid = agid;
            this.value = value;
        }
    
        /**
         * ゴールの情報を表示します。
         * @return String ゴールの情報<BR>
         * 表示形式  val:21 id:701
         */
        public String toString() {
            String str = "val:" + value + " id:" + agid;
                return str;
        }
    
    }

    /**
     * 現在のゴールスタックを取得します
     */
    public abstract List<IGoalStack.GoalStackElement> getGoalStackForNode(int i);

    /**
     * Goalの指定された位置(ノード)の値をスタックからGETで取得します。
     * @param int index
     * @return GoalStackElement  ゴールの要素
     */
    public abstract IGoalStack.GoalStackElement getGoalForNode(int index);

    /**
     * 指定されたゴールの要素をGoalの指定された位置(ノード)のスタックにPUSHで
     * 設定します。       
     * @param int index
     * @param GoalStackElement elm ゴールの要素
     */
    public abstract void pushGoal(int agid, boolean[] useNode, State state);

    /**
     * Goalの指定された位置(ノード)の値をスタックから削除します。
     * 指定されたエージェントの値がスタックに積まれていない時は false を返します。
     * 
     * @param index 
     */
    public abstract boolean removeGoal(int agid, boolean[] useNode);

    /**
     * Goalの要素を全てクリアします。
     */
    public abstract void removeAllGoal();

    /**
     * 全ノードのゴールをVectorで取得します。
     * @param Vector GoalValueのVector
     */
    public abstract Goal getGoalValueArray();

    /**
     * 全ノードのゴールをVectorで取得します。
     * @param Vector GoalValueのVector
     */
    public abstract State getCurrentGoal(int agid, boolean[] useNode);
}