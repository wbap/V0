/**
 * AssociateAgent.java
 * 連想処理を行なうGSAのエージェント
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 連想処理を行なうGSAのエージェント
 */
public class AssociateAgent extends Agent{
    static class Pair {
        List<Integer> first;
        List<Integer> second;

        Pair(List<Integer> first, List<Integer> second) {
            this.first = first;
            this.second = second;
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(AssociateAgent.class);

    /* 経験した状態を保持するテーブル */
    private List<List<Integer>> stateList = null;
    private Set<List<Integer>> stateTable = null;

    /* 連想するためのキーとその評価値を保持するテーブル */
    private Map<Pair, Double> profitTable = null;

    /* 連想するためのキーの遷移を保持するテーブル */
    private List<Pair> keyBuffer = null;

    private Map<Pair, Integer> keyCountTable;
    


    //////////////////////////////////////////////////////////////////////////
    // コンストラクタ

    /**
     * コンストラクタ
     * @param int agid  エージェントID
     * @param boolean[] useNode  ノードの使用、不使用を設定した配列
     * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
     */
    public AssociateAgent(int agid, boolean[] useNode,
             SharedMemory sharedMemory) {
        super(agid, useNode, sharedMemory);

        stateList = new LinkedList<List<Integer>>();
        stateTable = new HashSet<List<Integer>>();
        profitTable = new HashMap<Pair, Double>();
        keyBuffer = new LinkedList<Pair>();
        keyCountTable = new HashMap<Pair, Integer>();
    }


    //////////////////////////////////////////////////////////////////////////
    // public

    /**
     * エージェント固有の学習処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param boolean flagGoalReach ゴールへの到達を表すフラグ
     * @param double profit 報酬
     */
    public void learn(List<Integer> state, boolean flagGoalReach, double profit) {
        stateLearning(state);
        if(flagGoalReach) {
            profitLearning(profit);
        }
    }


    /**
     * エージェント固有の実行処理を行ないます。<BR>
     * @param Vector state 現在の状態
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector サブゴール
     */
    public List<Integer> execProcess(List<Integer> state, List<SharedMemory.GoalStackElement> goalElement) {

        /* 設定エージェントごとに分解 */
        List<SharedMemory.GoalStackElement>[] subsetGoalsElement = getSubsetGoals(goalElement);

        /* キーを選択 */
        List<SharedMemory.GoalStackElement> selectedKeyElement
                =  selectKey(goalElement, subsetGoalsElement);

        /* キーに対応する状態を連想 */
        List<Integer> selectedKeyValue = getGoalValueArray(selectedKeyElement);
        List<Integer> subgoal = associateSubgoal(selectedKeyValue);

        /* 連想に使用したキーをスタックから削除 */
//        remove(selectedKeyElement);

        return subgoal;
    }

    /**
     * 学習結果をファイルに保存します。
     * @param String fileNameファイル名
     */
    public void save(String fileName) throws IOException {
        logger.info("Saving learning data to %s...", fileName);
        /* ストリームの作成 */
        FileOutputStream ostream = new FileOutputStream(fileName, false);
        try {
            ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);
    
            /* オブジェクトの書き込み */
            oOutputStream.writeObject(stateList);
    
            oOutputStream.flush();
            oOutputStream.close();
        } finally {
            ostream.close();
        }
    }

    /**
     * 学習結果をファイルから読み込みます。
     * @param String fileName ファイル名
     */
    @SuppressWarnings("unchecked")
    public void load(String fileName) throws IOException {
        logger.info("Loading learning data from %s...", fileName);
        /* ストリームの作成 */
        FileInputStream istream = new FileInputStream(fileName);
        try {
            ObjectInputStream oInputStream = new ObjectInputStream(istream);
            try {
                /* オブジェクトの書き込み */
                stateList = (List<List<Integer>>)oInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new GSAException(e);
            } finally {
                oInputStream.close();
            }
        } finally {
            istream.close();
        }

        /* stateListの要素をstateTableに設定 */
        for (List<Integer> state: stateList) {
            stateTable.add(state);
        }
    }

    /**
     * GSAクラスのreset()メソッドから呼び出されます。<BR>
     * 連想のキーとして使用したものの履歴をクリアします。<BR>
     */
    public void reset() {
        keyBuffer.clear();
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void suspend() {
    }


    /////////////////////////////////////////////////////////////
    // private

    /**
     * 状態の学習を行います。
     */
    private void stateLearning(List<Integer> state) {
        if (!stateTable.contains(state)) {
            stateList.add(state);
            /* 同じ状態を複数設定しないためのテーブル */
            stateTable.add(state);
        }
    }


    /**
     * 引数で指定されたゴールを設定したエージェント毎に分解。
     * 設定エージェント毎のゴールとしてVectorの配列で取得します。
     * @param Vector goal
     * @return Vector[] エージェント毎のゴール配列
     */
    private List<SharedMemory.GoalStackElement>[] getSubsetGoals(List<SharedMemory.GoalStackElement> goal) {

        if(goal == null) {
            return null;
        }

        int[] agids = getAgids(goal);

        /* エージェントID毎のVectorに分解 */
        @SuppressWarnings("unchecked")
        List<SharedMemory.GoalStackElement>[] subsetGoals = new List[agids.length];
        for(int i = 0; i < agids.length; i++) {
            subsetGoals[i] = new ArrayList<SharedMemory.GoalStackElement>();
            for(int m = 0; m < goal.size(); m++) {
                SharedMemory.GoalStackElement e = goal.get(m);
                if(e != null && e.agid == agids[i]) {
                    subsetGoals[i].add(e);
                }else {
                    subsetGoals[i].add(null);
                }
            }
        }

        return subsetGoals;
    }


    /**
     * 引数のgoalから全エージェントIDを取得します。
     * @return int[] エージェントIDの配列
     */
    private int[] getAgids(List<SharedMemory.GoalStackElement> goal) {
        Set<Integer> agidTable = new HashSet<Integer>();
        List<Integer> agidArray = new Vector<Integer>();
        for(int i = 0; i < goal.size(); i++) {
            SharedMemory.GoalStackElement e = (SharedMemory.GoalStackElement)goal.get(i);
            if( e != null ) {
                if (!agidTable.contains(e.agid)) {
                    agidTable.add(e.agid);
                    agidArray.add(e.agid);
                }
            }
        }
        int[] agids = new int[agidArray.size()];
        for(int i = 0; i < agidArray.size(); i++) {
            agids[i] = ((Integer)agidArray.get(i)).intValue();
        }
        return agids;
    }


    /**
     * 引数で設定された複数の部分ゴールからある部分ゴールを選択します。
     * @param Vector   Element
     * @param Vector[] Element
     * @return Vector  Element
     */
    private List<SharedMemory.GoalStackElement> selectKey(List<SharedMemory.GoalStackElement> goalElement, List<SharedMemory.GoalStackElement>[] subsetGoalsElement) {

        List<SharedMemory.GoalStackElement> selectedSubset = null;

        if(subsetGoalsElement != null) {

            List<Integer> goalValue = getGoalValueArray(goalElement);

            // 無出力をありにするかどうか？
            int loopNum = subsetGoalsElement.length + 1;
//            int loopNum = subsetGoalsElement.length;

            double[] profits = new double[loopNum];

            for(int i = 0; i < loopNum; i++) {

                /* 評価値が設定されている型を作成 無出力についても取得 */
                List<Integer> subsetGoalValue = null;
                if(i < subsetGoalsElement.length) {
                    subsetGoalValue = getGoalValueArray(subsetGoalsElement[i]);
                }
                Pair key = new Pair(goalValue, subsetGoalValue);
                /* 各評価値を配列に設定 */
                Double profitD = (Double)profitTable.get(key);
                if(profitD != null) {
                    profits[i] = profitD.doubleValue();
                }else{
                    // 初期値５０
                    profits[i] = 10;
                }
            }

            //// 評価値が最大のものを選択
            // int index = selectMaxProfit(profits);
            // 評価値が高いものを高確率で選択 */
            int index = selectProfit(profits);

            if (index < subsetGoalsElement.length) {
                selectedSubset = subsetGoalsElement[index];
            }

            /* 選択されたキーを履歴に登録 */
            List<Integer> selectedSubsetValue = getGoalValueArray(selectedSubset);
            keyBuffer.add(new Pair(goalValue, selectedSubsetValue));
        }

        return selectedSubset;
    }

    /*
     * 引数の評価値の配列から最大の評価値を持つインデックスを取得します。
    private int selectMaxProfit(double[] profits) {
        // 最大の評価値のものを選択
        int sameElmNum = 0;
        int[] sameElms = new int[profits.length];
        double selectedProfit = -1;
        for(int i = 0; i < profits.length; i++) {
            if(profits[i] > selectedProfit) {
                selectedProfit = profits[i];
                sameElms = new int[profits.length];
                sameElmNum = 0;
            }
            sameElms[sameElmNum] = i;
            sameElmNum++;
        }
        int randomValue = random.nextInt(sameElmNum);
        return sameElms[randomValue];
    }
    */


    /**
     * 引数の評価値の配列から確率的に評価値を選択しそのインデックスを
     * 取得します。
     * 評価値は高いものほど高確率で選択されます。
     */
    Random randomKeySelect = new Random(0);
    private int selectProfit(double[] profits) {

        // 評価値の合計
        double totalProfit = 0;
        for(int i = 0; i < profits.length; i++) {
            totalProfit += profits[i];
        }

        int r = randomKeySelect.nextInt(100);

        logger.info(" total  " + totalProfit);
        logger.info(" random " + r);

        double d = 0;
        int index = 0;
        for(; index < profits.length; index++) {
            logger.info(" profit " + index + " " +  profits[index]);
            d += profits[index] / totalProfit * 100;
            if(d > r) {
                break;
            }
        }

        logger.info(" select index " + index );

        return index;
    }

    // private final double T = 0.999;
    /*
     * 引数の評価値の配列から確率的に評価値を選択しそのインデックスを
     * 取得します。
     * 評価値は高いものほど高確率で選択されます。
    private int selectProfitExp(double[] profits) {
        // 評価値の合計
        double totalProfitExp = 0;
        for(int i = 0; i < profits.length; i++) {
            totalProfitExp += Math.exp(profits[i]/T);
            logger.info("profit " + profits[i]);
            logger.info("exp    " + Math.exp(profits[i]/T));
        }

        int r = randomKeySelect.nextInt(100);

        logger.info(" ########## select key ##########  ");
        logger.info("  total exp " + totalProfitExp);
        logger.info("  random " + r);

        double d = 0;
        int index = 0;
        for(; index < profits.length; index++) {
            logger.info("  index " + index);
            logger.info("   profit " + profits[index]);
            d += Math.exp(profits[index]/T) / totalProfitExp * 100;
            logger.info("   exp    " + Math.exp(profits[index]/T));
            if(d > r) {
                break;
            }
        }
        logger.info(" select index " + index );
        return index;
    }
    */

    /**
     * 引数で指定されたキーから経験済みの状態を連想
     * @param Vector key 連想するキー(value)
     * @return Vector    連想された状態
     */
    private List<Integer> associateSubgoal(List<Integer> key) {
        for (List<Integer> state: stateList) {
            if( Util.equalsValidElement(key, state) ) {
                return state;
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////
    // 

    /* 定数 */
    @SuppressWarnings("unused")
    private final double REWARD = 100; /* 報酬 */
    private final double GAMMA = 0.9;
    private final double BETA = 0.1;

    /**
     * 評価値の学習を行います。
     */
    private void profitLearning(double reward) {
        Set<Pair> checkTable = new HashSet<Pair>();
        ListIterator<Pair> li = keyBuffer.listIterator(keyBuffer.size());
        int i = 0;
        while (li.hasPrevious()) {
            Pair key = li.previous();
            Integer count = (Integer)keyCountTable.get(key);
            int newCount = 1;
            if(count != null) {
                newCount = count.intValue() + 1;
            }
            keyCountTable.put(key, new Integer(newCount));
            /* テーブルに同じ状態があればプロフィットの更新を行なわない */
            if (!checkTable.contains(key)) {
                checkTable.add(key);
                Double profitD = (Double)profitTable.get(key);
                double profit = 0;
                if(profitD != null) {
                    profit = profitD.doubleValue();
                }else {
                    // 初期値５０
                    profit = 10;
                }
                /* ΔS = β(γ^nP(t) - Si(t)) */
                profit = profit + BETA*(Math.pow(GAMMA,i)*reward - profit);
                profitTable.put(key, new Double(profit));
            }
            i++;
        }

        if (reward <= 0) {
            zeroRewardCount++;
        }
        // 全テーブルの状況を表示
        printProfitTable();
    }


    /**
     * 評価値のテーブルの状態を出力。
     */
    private int zeroRewardCount = 0;
    private int count = 0;
    private void printProfitTable() {
        count++;
        System.out.println(" ** Value Table ** ");
        System.out.println("  count " + count);
        System.out.println("  zero reward count " + zeroRewardCount);
        for (Pair key: profitTable.keySet()) {
            Double d = (Double)profitTable.get(key);
            Integer count = (Integer)keyCountTable.get(key);
            System.out.println("      key: " + key.first  + ":" + key.second
                       +  " profit:" + d.doubleValue() + " count:" + count );
        }
    }
}
