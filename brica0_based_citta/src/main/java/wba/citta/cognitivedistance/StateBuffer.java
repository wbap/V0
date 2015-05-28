/**
 * StateBuffer.java
 * 状態の履歴を保持するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto 
 */
package wba.citta.cognitivedistance;

import java.util.*;

/**
 *    状態の履歴を保持するクラスです。
 *    コンストラクタの引数で設定された数だけ、ノードIDを保持します。
 *    最大の要素数を越えた場合、古い順に削除されます。
 */
public class StateBuffer extends ArrayList<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /* 保持する最大の要素数 */
    private int maxCDLngth;

    /**
     * コンストラクタ
     * @param int maxCDLngth 保持する最大の要素数
     */
    public StateBuffer(int maxCDLngth) {
        this.maxCDLngth = maxCDLngth;
    }


    /**
     *    ノードIDをリストに追加します。
     *    @param Integer stateID ノードID
     * @return 
     */
    public boolean add(Integer stateID){
        /* リストに追加 */
        super.add(stateID);
        /* リストのサイズをMAXSIZEに調整 */
        if(size() > maxCDLngth){
            this.remove(0);
        }
        return true;
    }

    /* リスト内の要素を表示 */
// 2001.05.25 削除 miyamoto 古いバージョンのjavaに対応
// LinkedListをVectorに変更したためtoStringをオーバーライドできない(必要ない)
//    public String toString() {
//        StringBuffer sb = new StringBuffer();
//        ListIterator li = listIterator();
//        while(li.hasNext()) {
//            Integer id = (Integer)li.next();
//            sb.append(id + ",");
//        }
//        return sb.toString();
//    }
}

