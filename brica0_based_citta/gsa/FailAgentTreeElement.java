/**
 * FailAgentTreeElement.java
 * FailAgentTreeで扱う情報の単位
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package gsa;

import java.util.*;

/**
 * FailAgentTreeで扱う情報の単位<BR>
 * ツリーのノード<BR>
 */
public class FailAgentTreeElement {

	/**
	 * 親のノード(FailAgentTreeElement)
	 */
	public final FailAgentTreeElement parentElement;

	/**
	 * エージェントID
	 */
	public final int agid;

	/**
	 * ゴールの値
	 */
	public final Vector goal;

	/**
	 * 実行処理の結果
	 */
	public final int agr;

	/**
	 * 子のノード(FailAgentTreeElement)のリスト
	 */
	public final LinkedList next;

	/**
	 * コンストラクタ
	 * @param FailAgentTreeElement parentElement 親ノードへの参照
	 * @param int agid エージェントID
	 * @param Vector goal ゴール
	 * @param int agr 実行処理の結果を表すID
	 */
	public FailAgentTreeElement(FailAgentTreeElement parentElement, int agid,
	        Vector goal, int agr) {
		this.parentElement = parentElement;
		this.agid = agid;
		this.goal = goal;
		this.agr = agr;
		next = new LinkedList();
	}

	/**
	 * 引数のFailAgentTreeElementを子ノードのリストに追加します。
	 * @param FailAgentTreeElement nextElement 子となるノード
	 */
	public void addNext(FailAgentTreeElement nextElement) {
		next.add(nextElement);
	}

	/**
	 * 子ノードのリストから引数のFailAgentTreeElementを削除します。
	 * @param FailAgentTreeElement nextElement 削除するノード
	 */
	public void removeNext(FailAgentTreeElement nextElement) {
		next.remove(nextElement);
	}

	/**
	 * 子を全て削除します。
	 */
	public void removeNextAll() {
		next.clear();
	}

	/**
	 * 子ノードのリストから、引数で指定されたエージェントIDをもつノードの
	 * agrを取得します。<BR>
	 * 引数で指定されたエージェントの設定したノードがなければ-1を返します。<BR>
	 * @param int agid エージェントのID
	 * @return int 実行処理結果を表すID
	 */
	public int getChildAgr(int agid) {
		ListIterator li = next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement e = (FailAgentTreeElement)li.next();
			if( e.agid == agid ) {
				return e.agr;
			}
		}
		return -1;
	}


	/**
	 * このノードの文字列情報を取得します。
	 * @return String 
	 */
	public String toString() {
		return "id:" + agid + " goal:" + goal + " agr:" + agr;
	}


}

