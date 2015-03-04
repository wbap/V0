/**
 * TransitionCounter.java
 * 直接移動可能なノードへの遷移の回数を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.io.*;

/**
 * 直接移動可能なノードへの遷移の回数を管理するクラスです。
 */
public class TransitionCounter implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer nextNodeID;  /* 移動先のノードのID */
    private int trasitionCount;  /* そのノードへの移動回数 */

    /**
     * コンストラクタ
     * @param Integer nextNodeID  直接移動可能なノードのID
     */
    public TransitionCounter(Integer nextNodeID) {
        this.nextNodeID = nextNodeID;
        trasitionCount = 1;
    }


    /**
     * 移動回数を１増加させます。
     */
    public void count() {
        trasitionCount++;
    }

    /**
     * 移動回数を取得します。
     * @return int   移動回数
     */
    public int getCount() {
        return trasitionCount;
    }

    public Integer getNextNodeID() {
        return nextNodeID;
    }
}
