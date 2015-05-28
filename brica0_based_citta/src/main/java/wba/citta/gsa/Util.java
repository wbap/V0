/**
 * Util.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package wba.citta.gsa;

import java.util.*;


/**
 *
 */
public class Util {

    /**
     * 引数で指定された２つのVectorの有効な要素が同じかどうか判定します。<BR>
     * ２つのVectorがnullでなく、要素数は同じで、Vectorの有効な要素
     * (nullでない要素)がすべて等しければtrueを返します。
     * @param Vector v1
     * @param Vector v2
     * @return boolean 
     */
    public static boolean equalsValidElement(List<Integer> v1, List<Integer> v2) {
        boolean b = true;
        if( (v1 != null) && (v2 != null) && (v1.size() == v2.size())) {
            Iterator<Integer> li1 = v1.iterator();
            Iterator<Integer> li2 = v2.iterator();
            while(li1.hasNext()) {
                Integer elm1 = (Integer)li1.next();
                Integer elm2 = (Integer)li2.next();
                if( (elm1 != null) && (elm2 != null) &&
                        (!elm1.equals(elm2)) ) {
                    b = false;
                }
            }
        }else {
            b = false;
        }
        return b;
    }



    ////////////////////////////////////////////////////////////////
    // 実行エージェントの選択

    /*  すでに取得された数を設定するbooleanの配列 */
    private boolean[] useIndex;

    /* 乱数のシード */
    private int seed = 1;
    /* 乱数 */
    private Random random = new Random(seed);

    /**
     * コンストラクタ
     * @param int num 利用する数
     */
    public Util(int num) {
        useIndex = new boolean[num];
    }

    /**
     * ランダムな値を取得します。<BR>
     * 全て取得済みなら-1を返す。
     * @return int 
     */
    public int getRandomNum() {
        return getRandomIndex();
    }

    /**
     * ランダムに取得した状態をクリアします。
     */
    public void reset() {
        for(int i = 0; i < useIndex.length; i++) {
            useIndex[i] = false;
        }
    }

    /**
     * 未使用なindexの数を取得します。
     * @return int 実行処理を行なっていないエージェント数
     */
    public int getNotUseNum() {
        int num = 0;
        for(int i = 0; i < useIndex.length; i++) {
            if(useIndex[i] == false) {
                num++;
            }
        }
        return num;
    }


    /**
     * 実行するエージェントの配列中のIndexを取得します。
     * @return int 実行するエージェントの配列中のIndex
     */
    private int getRandomIndex() {
        /* 未使用な数を取得 */
        int notUseNum = getNotUseNum();

        /* すべて使用されていれば全てを未使用に設定し全てから選択 */
        if(notUseNum == 0) {
//            clearUseFlag();
//            notUseAgentNum = getNotUseAgentNum();
            return -1;
        }

        int randomNum = random.nextInt(notUseNum);

        int index = 0;
        int falseNum = 0;
        for(; index < useIndex.length; index++) {
            if(useIndex[index] == false) {
                if(falseNum == randomNum) {
                    break;
                }
                falseNum++;
            }
        }
        return index;
    }

    /**
     * SharedMemory.GoalStackElementのVectorからGoalValueのVectorを取得します。
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector            goalValueのVector
     */
    public static State getGoalValueArray(List<IGoalStack.GoalStackElement> goalElementArray) {
        assert goalElementArray != null;
        State goalValueArray = new State(goalElementArray.size());
        for(int i = 0; i < goalElementArray.size(); i++) {
            IGoalStack.GoalStackElement e = (IGoalStack.GoalStackElement)goalElementArray.get(i);
            if(e != null) {
                goalValueArray.set(i, e.value);
            }else {
                goalValueArray.set(i, null);
            }
        }
        return goalValueArray;
    }

}
