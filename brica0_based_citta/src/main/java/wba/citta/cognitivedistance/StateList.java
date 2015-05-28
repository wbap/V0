/**
 * StateList.java
 * ノードのリストと親子関係について管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;

/**
 * ノードのリストとその親子関係について管理するクラスです。
 */
public class StateList extends ArrayList<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer parentNodeID;   /* 親のノードID */
    private StateList parentList;   /* 親ノードの属するリスト */

    /**
     * コンストラクタ
     * @param LinkedList stateList       ノードIDのリスト
     * @param Integer parentNodeID 親のノードID
     * @param StateList parentList       親のノードが属するStateList
     */
    public StateList(List<Integer> stateList, Integer parentNodeID,
            StateList parentList) {
        super(stateList);
        this.parentNodeID = parentNodeID;
        this.parentList = parentList;
    }

    /**
     * 親のノードIDを取得します。
     * @return Integer 親のノードID
     */
    public Integer getParentNodeID() {
        return parentNodeID;
    }

    /**
     * 親のノードが属するStateListを取得します。
     * @return StateList
     */
    public StateList getParentList() {
        return parentList;
    }
}


