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
public class StateList extends LinkedList {

	private Integer parentNodeID;   /* 親のノードID */
	private StateList parentList;   /* 親ノードの属するリスト */

	/**
	 * コンストラクタ
	 * @param LinkedList stateList       ノードIDのリスト
	 * @param Integer parentNodeID 親のノードID
	 * @param StateList parentList       親のノードが属するStateList
	 */
	public StateList(LinkedList stateList, Integer parentNodeID,
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

	/**
	 * このリストの全要素をStringで取得します。
	 * @return String
	 */
// 2001.05.25 削除 miyamoto 古いバージョンのjavaに対応
// LinkedListをVectorに変更したためtoStringをオーバーライドできない(必要ない)
//	public String toString(){
//		StringBuffer sb = new StringBuffer();
//		ListIterator li = listIterator();
//		while(li.hasNext()) {
//			Integer state = (Integer)li.next();
//			sb.append(" ");
//			sb.append(state);
//		}
//		return sb.toString();
//	}

}


