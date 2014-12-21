/**
 * CDAgent.java
 * CognitiveDistanceの処理を行なうGSAのエージェント
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import wba.citta.cognitivedistance.*;

import java.util.*;

/**
 * CognitiveDistanceの処理を行なうGSAのエージェント
 */
public class CDAgent extends Agent {
//public class CDAgent extends LogOutAgent {

	/* CognitiveDistanceによる処理を行なうクラス */
	private wba.citta.cognitivedistance.Agent cognitiveDistance;

	/* CognitiveDistanceのパラメータ */
	private int cdLayerNum = 3;
	private int maxCDLngth = 9;
	private int shallowSearchLngth = 1;
	private int deepSearchLngth = 200;
	private int minSearchLngth = 1;
	private int maxSegmentSize = 3;
	private int minSegmentSize = 0;
	private boolean flagNovelSearch = false;
	private boolean flagSegmentation = false;

	/**
	 * コンストラクタ
	 * @param int agid  エージェントID
	 * @param boolean[] useNode  ノードの使用、不使用を設定した配列
	 * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
	 */
	public CDAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);

		cognitiveDistance = new wba.citta.cognitivedistance.Agent(cdLayerNum, maxCDLngth,
		        shallowSearchLngth, deepSearchLngth, minSearchLngth,
		        maxSegmentSize, minSegmentSize, flagNovelSearch, 10,
		        flagSegmentation);

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * エージェント固有の学習処理を行ないます。<BR>
	 * 第二、第三引数は、CDAgentの学習では使用しません。<BR>
	 * @param Vector state 現在の状態
	 * @param boolean flagGoalReach ゴールへの到達を表すフラグ
	 * @param double profit 報酬
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
		try {
			cognitiveDistance.learn(state);
		}catch(Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * エージェント固有の実行処理を行ないます。<BR>
	 * 引数で指定された、現在の状態から、ゴールへ移動するための次の状態を
	 * サブゴールとして出力します。<BR>
	 * @param Vector state 現在の状態
	 * @param Vector goalElementArray GoalStackElementのVector
	 * @return Vector サブゴール
	 */
	public Vector execProcess(Vector state, Vector goalElementArray) {

		/* ゴールの値のみを取得 */
		Vector goalValueArray = getGoalValueArray(goalElementArray);

//		System.out.println("   state:" + state);
//		System.out.println("   goal :" + goalValueArray);

		/* CognitiveDistanceで次の状態を取得 */
		Vector nextState = null;
		try {
			nextState = (Vector)cognitiveDistance.exec(state, goalValueArray);
		}catch(Exception e) {
			System.out.println(e);
		}

//		System.out.println("   next state :" + nextState);

		return nextState;

	}

	/**
	 * GSAクラスのreset()メソッドから呼び出されます。<BR>
	 * 認知距離を学習するために保持している状態の履歴と、各層で保持している、
	 * 以前の状態・ゴールに関する情報をクリアします。
	 */
	public void reset() {
		cognitiveDistance.reset();
	}

	/**
	 * GSAクラスによって、実行処理を行なうエージェントが自身のエージェント
	 * から他のエージェントに切り替えられたときに呼び出されます。<BR>
	 * CognitiveDistanceの各層で保持している、以前の状態・ゴールに関する情報を
	 * クリアします。
	 */
	public void suspend() {
		cognitiveDistance.resetOldValue();
	}

	/**
	 * 学習結果をファイルに保存します。
	 * @param String fileNameファイル名
	 */
	public void save(String fileName) { 
		cognitiveDistance.save(fileName);
	}

	/**
	 * 学習結果をファイルから読み込みます。
	 * @param String fileName ファイル名
	 */
	public void load(String fileName) {
		cognitiveDistance.load(fileName);
	}

}

