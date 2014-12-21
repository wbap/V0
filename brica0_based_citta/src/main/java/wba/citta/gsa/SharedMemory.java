/**
 * SharedMemory.java
 * State・Goalを管理する共有メモリ
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import wba.citta.gsa.viewer.*;

/**
 * State・Goalを管理する共有メモリです。<BR>
 * Goalについてはスタックで管理します。<BR>
 * 共有メモリへの操作は、基本的にメソッドの引数で指定されたindexの要素ごとに
 * 行ないます。
 */
public class SharedMemory {

	/* State用 Integerの配列 */
	private Integer[] stateArray = null;

	/* Goal用 LinkedListの配列 LinkedListにはElementを設定 */
	private LinkedList[] goalStackArray = null;

	/**
	 * 共有メモリのノード数
	 */
	public final int LENGTH;

	/* ゴールスタックの状態を表示するviewer */
	private SharedMemoryViewer viewer;

	/**
	 * コンストラクタ
	 * @param int size ノード数（配列のサイズ）
	 * @param boolean isShowViewer スタックの状態をグラフィック表示するかどうか
	 */
	public SharedMemory(int size, boolean isShowViewer) {
		LENGTH = size;
		stateArray = new Integer[LENGTH];
		goalStackArray = new LinkedList[LENGTH];
		for(int i = 0; i < LENGTH; i++) {
			goalStackArray[i] = new LinkedList();
		}

		if(isShowViewer == true) {
			viewer = new SharedMemoryViewer(stateArray, goalStackArray);
		}
	}


	/**
	 * Stateの指定された位置(ノード)の値を取得します。
	 * @param int index 
	 * @return Integer  
	 */
	public Integer getState(int index) {
		return stateArray[index];
	}

	/**
	 * 指定された値をStateの指定された位置(ノード)に設定します。
	 * @param int index 
	 * @param Integer value 
	 */
	public void setState(int index, Integer value) {
		stateArray[index] = value;
	}

	/**
	 * Goalの指定された位置(ノード)の値をスタックからGETで取得します。
	 * @param int index
	 * @return GoalStackElement  ゴールの要素
	 */
	public GoalStackElement getGoal(int index) {
		GoalStackElement elm = null;
		if(goalStackArray[index].size() > 0) {
			elm = (GoalStackElement)goalStackArray[index].getLast();
		}
		return elm;
	}

	/**
	 * 指定されたゴールの要素をGoalの指定された位置(ノード)のスタックにPUSHで
	 * 設定します。
	 * @param int index
	 * @param GoalStackElement elm ゴールの要素
	 */
	public void pushGoal(int index, GoalStackElement elm) {
		goalStackArray[index].add(elm);
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * Goalの指定された位置(ノード)の値をスタックから削除します。
	 * @int index 
	 */
	public void removeGoal(int index) {
		goalStackArray[index].removeLast();
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * Goalの要素を全てクリアします。
	 */
	public void removeAllGoal() {
		for(int i = 0; i < LENGTH; i++) {
			goalStackArray[i].clear();
		}
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * 現在の状態をVectorで設定します。
	 * @param Vector state 現在の状態
	 */
	public void setState(Vector state) {
		for(int i = 0; i < LENGTH; i++) {
			setState(i, (Integer)state.get(i));
		}
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * 全ノードのゴールをVectorで取得します。
	 * @param Vector GoalValueのVector
	 */
	public Vector getGoalValueArray() {
		Vector goal = new Vector();
		for(int i = 0; i < LENGTH; i++) {
			GoalStackElement goalElement = getGoal(i);
			if(goalElement != null) {
				goal.add(new Integer(goalElement.value));
			}else {
				goal.add(null);
			}
		}
		return goal;
	}


	/**
	 * Stateの状態を出力します。<BR>
	 * 出力形式<BR>
	 * [shared stack]<BR>
	 *  state<BR>
	 *   index:0 val:22<BR>
	 *   index:1 val:15<BR>
	 *   index:2 val:0<BR>
	 *   index:3 val:0<BR>
	 *   index:4 val:1<BR>
	 */
	public void printState() {
		System.out.println("");
		System.out.println(" state");
		for(int i = 0; i < LENGTH; i++) {
			System.out.println("  index:" + i + " val:" + getState(i));
		}
	}

	/**
	 * Goalの状態を出力します。<BR>
	 * 出力形式<BR>
	 * [shared stack]<BR>
	 *  goal<BR>
	 *   index:0 | 26:200 | 18:100 |<BR>
	 *   index:1 | 1:200 | 10:100 |<BR>
	 *   index:2 | 6:100 |<BR>
	 *   index:3 | 1:1 | 2:101 |<BR>
	 *   index:4 | 1:101 | 1:101 |<BR>
	 */
	public void printGoalStack() {
		System.out.println("");
		System.out.println("[shared stack]");

		System.out.println(" goal");
		for(int i = 0; i < LENGTH; i++) {
			LinkedList goalStack = goalStackArray[i];
			int size = goalStack.size();
			StringBuffer sb = new StringBuffer();
			sb.append("  index:" + i + " | ");
			for(int m = 0; m < size; m++) {
				GoalStackElement elm = (GoalStackElement)goalStack.get(m);
				if(elm != null) {
					sb.append(elm.toString() + " | " );
				}else {
					sb.append(" no val | ");
				}
			}
			System.out.println(sb.toString());
		}
	}


}
