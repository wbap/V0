package wba.citta.gsa;

import java.io.IOException;

public interface IGSAAgent extends IAgent {

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
     * 学習処理を行ないます。<BR>
     * (引数のflagGoalReach、profitは、連想エージェントの強化学習用。
     * ＣＤエージェントでは使用していない。)
     * @param flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public void learn(boolean flagGoalReach, double profit);

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
    public abstract Status exec();
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
    /**
     * 現在の状態(State)を共有メモリから取得します。
     * @return Vector 現在の状態
     */
    public abstract State getState();

    public abstract State getLastSubgoal();

    public void learnFromEventFile(String eventFileName) throws IOException;

    public boolean removeSelfSetGoal();

    public boolean removeReachGoal();

    public ISharedMemory getSharedMemory();
}