/**
 * FailAgentTree.java
 * スタックのゴールの状態、解決できないゴール、ゴールを解決できないエージェント
 * についてツリー構造で管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package gsa;

import java.util.*;
import gsa.viewer.*;

/**
 * スタックのゴールの状態、失敗ゴール、サブゴール未出力エージェント
 * についてツリー構造で管理するクラス。<BR>
 * ツリー上の現在の位置をカレントとして処理を行います。<BR>
 * このツリーで扱うノードとして、FailAgentTreeElementを使用します。<BR>
 */
public class FailAgentTree {

	private FailAgentTreeElement currentElement = null;
	private FailAgentTreeElement rootElement = null;

	/* 失敗エージェントの状態を表示するviewer */
	private TreeViewer viewer = null;

	/**
	 * コンストラクタ
	 * @param boolean isShowViewer ツリーの状態をグラフィック表示するか
	 */
	public FailAgentTree(boolean isShowViewer) {
		rootElement = new FailAgentTreeElement(null, -1, null, 0);
		currentElement = rootElement;

		if(isShowViewer == true) {
			viewer = new TreeViewer(rootElement);
		}
	}


	//////////////////////////////////////////////////////////////////////
	// public

	/**
	 * カレントノードに子ノードを追加します。<BR>
	 * 追加ノードにはエージェントIDとゴールを設定します。agrには0を設定します。
	 * <BR>
	 * カレントの位置は、追加した新たなゴールに移動します。<BR>
	 * @param int agid エージェントのID
	 * @param Vector goal ゴール
	 */
	public void addTreeNode(int agid, Vector goal) {
		FailAgentTreeElement newFailAgentTreeElement
		        = new FailAgentTreeElement(currentElement, agid, goal, 0);
		currentElement.addNext(newFailAgentTreeElement);
		currentElement = newFailAgentTreeElement;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * カレントノードに子ノードを追加します。<BR>
	 * 追加するノードにはエージェントIDとagrを設定します。ゴールはnullになります	 * 。<BR>
	 * カレントの位置は移動しません。<BR>
	 * @param int agid エージェントのID
	 * @param int agr 実行処理の結果を表すID
	 */
	public void addTreeNode(int agid, int agr) {
		/* ルートには失敗エージェントを追加しない */
		if(currentElement != rootElement) {
			if(getChildAgr(agid) == -1) {
				FailAgentTreeElement newFailAgentTreeElement
				       = new FailAgentTreeElement(currentElement, agid, null,
				       agr);
				currentElement.addNext(newFailAgentTreeElement);
				if(viewer != null) {
					viewer.setCurrentElement(currentElement);
					viewer.repaint();
				}
			}
		}
	}

	/**
	 * ツリーのカレントノードを削除します。<BR>
	 * カレントは親ノードに移動します。<BR>
	 */
	public void removeCurrent() {
		FailAgentTreeElement parent = currentElement.parentElement;
		parent.removeNext(currentElement);
		currentElement = parent;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * ツリーのカレントノードを親へ移動します。<BR>
	 */
	public void moveParent() {
		currentElement = currentElement.parentElement;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * カレントノードの子ノードから、引数で指定されたエージェントIDをもつ
	 * ノードを探し、そのagrを取得します。<BR>
	 * 引数で指定されたエージェントIDをもつノードがなければ-1を返します。<BR>
	 * @param int agid エージェントのID
	 * @return int 実行処理結果を表すID
	 */
	public int getChildAgr(int agid) {
		if(currentElement != null) {
			return currentElement.getChildAgr(agid);
		}
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
		return -1;
	}

	/**
	 * ツリーのノードを全て削除します。
	 */
	public void clear() {
		currentElement = rootElement;
		currentElement.removeNextAll();
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}


	/**
	 * ツリー上にあるノードに、引数で指定されたエージェントIDとゴールをもつ
	 * ノードがあるかどうか判定します。<BR>
	 * ノードがあればtrue、なければfalseを返します。<BR>
	 * @param int agid エージェントID
	 * @param Vector goal ゴール
	 * @return boolean true：ノードがある false：ノードがない
	 */
	public boolean isContain(int agid, Vector goal) {
		boolean b = isContainChild(rootElement, agid, goal);
		return b;
	}


	/**
	 * ツリーの状態を表示します。
	 */
	public void printTree() {
		System.out.println("");
		System.out.println("[ goal tree ]");
		System.out.println(getTree(0, rootElement));
	}

	//////////////////////////////////////////////////////////////////////
	// private 

	/**
	 * 第一引数で指定されたノードの子に第二引数・第三引数のエージェントID、
	 * ゴールをもつノードがあればtrueを返す。<BR>
	 * 再帰的な呼び出しを行い、深い子ノードまでチェックする。<BR>
	 * @param FailAgentTreeElement Element 
	 * @param int agid 
	 * @param Vector goal
	 */
	private boolean isContainChild(FailAgentTreeElement element, int agid, 
	        Vector goal) {
// 現在の処理方法では、処理に時間がかかる。
// このクラスで、Agentの配列への参照をもち、各ノードのエージェントIDに対応する
// エージェントのみ処理を行なうようにすることで対応可能だが、Agentの配列への
// 参照を持たせることはあまり行ないたくない。
		ListIterator li = element.next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement nextElement = (FailAgentTreeElement)li.next();
			if( (nextElement.goal != null) && (nextElement.agid==agid) &&
			        (nextElement.goal.equals(goal)) ){
				return true;
			}
			boolean b = isContainChild(nextElement, agid, goal);
			if(b == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 引数で指定されたノードの子に関しての情報を文字列で取得します。
	 */
	private String getTree(int depth, FailAgentTreeElement goal) {
		StringBuffer stringBuffer = new StringBuffer();
		ListIterator li = goal.next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement nextGoal = (FailAgentTreeElement)li.next();
			stringBuffer.append(getString(depth, nextGoal));
			if(nextGoal == currentElement) {
				stringBuffer.append("<--- *");
			}
			stringBuffer.append("\n");
			stringBuffer.append(getTree(depth+1, nextGoal));
		}
		return stringBuffer.toString();
	}

	private String getString(int depth, FailAgentTreeElement goal) {
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < depth; i++) {
			stringBuffer.append("  ");
		}
		stringBuffer.append(goal.toString());
		return stringBuffer.toString();
	}

}

