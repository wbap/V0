/**
 * CDViewer.java
 *  CognitiveDistanceの階層化・セグメント情報をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package wba.citta.cognitivedistance.viewer;

import java.util.*;

/**
 * CognitiveDistanceの階層化・セグメント情報をグラフィック表示するクラスです。
 */
public class CDViewer {

	/* 階層化のレイヤ数 */
	private int layerNum;
	/* 各層ごとの描画フレーム */
	private CDViewerFrame[] frame;


	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param int layerNum レイヤ数
	 * @param int xNum x軸方向のますの数
	 * @param int yNum y軸方向のますの数
	 */
	public CDViewer(int layerNum, int xNum, int yNum) {
		this(layerNum, xNum, yNum, true);
	}

	public CDViewer(int layerNum, int xNum, int yNum, boolean flagSeparate) {
		this.layerNum = layerNum;
		initViewer(xNum, yNum, flagSeparate);
	}

	/**
	 * 初期化処理
	 * @param int xNum x軸方向のますの数
	 * @param int yNum y軸方向のますの数
	 */
	private void initViewer(int xNum, int yNum, boolean flagSeparate) {
		/* レイヤ数分のフレームの作成 */
		frame = new CDViewerFrame[layerNum];
		for(int i = 0; i < layerNum; i++) {
			frame[i] = new CDViewerFrame(i+1, xNum, yNum, flagSeparate);
		}
	}


	/////////////////////////////////////////////////////////////////
	// public 

	/**
	 * 各層でのstate,goal,upperSubgoal,currentSubgoalに属する環境の状態の
	 * リストを設定します。
	 * @param LinkedList[] currentStateList 現在の状態に属する状態
	 * @param LinkedList[] goalStateList ゴールの状態に属する状態
	 * @param LinkedList[] upperSubgoalList 上位層のサブゴールに属する状態
	 * @param LinkedList[] currentSubgoalList 現在の層のサブゴールに属する状態
	 * @param LinkedList[]
	 */
	public void setSegmentInfo(LinkedList[] currentStateList, 
	        LinkedList[] goalStateList, LinkedList[] upperSubgoalList,
	        LinkedList[] currentSubgoalList, LinkedList[] optionList) {
		/* 対応する層のフレームに状態のリストを設定 */
		for(int i = 0; i < layerNum; i++) {
			frame[i].setSegmentInfo(currentStateList[i], goalStateList[i],
			        upperSubgoalList[i], currentSubgoalList[i], optionList[i]);
		}
	}


	/**
	 * レイヤごとの各フレームにノードの情報を設定
	 * @param int[][] nodeIdInfo int[] レイヤ分の配列
	 *                           int[][0] 現在の状態のID
	 *                           int[][1] ゴールの状態のID
	 *                           int[][2] 上位層からのサブゴールのID
	 *                           int[][3] 次の状態のID
	 *                           int[][5] 上位層からのサブゴールが更新されたか
	 *                                    示すフラグ
	 */
	public void setNodeInfo(int[][] nodeIdInfo) {
		for(int i = 0; i < layerNum; i++) {
			frame[i].setNodeInfo(nodeIdInfo[i]);
		}
	}


	/**
	 * レイヤごとの各フレームの描画処理を呼び出し
	 */
	public void repaint() {
		for(int i = 0; i < layerNum; i++) {
			frame[i].repaint();
		}
		try{
			Thread.sleep(20);
		}catch (Exception e) {
			System.out.println(e);
		}
	}


}


