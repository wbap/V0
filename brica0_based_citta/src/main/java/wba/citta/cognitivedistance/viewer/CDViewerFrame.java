/**
 * CDViewerFrame.java
 *  各層ごとのグラフィック表示をするクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package wba.citta.cognitivedistance.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  各層ごとのグラフィック表示をするクラスです。
 */
public class CDViewerFrame extends Frame {

	/* レイヤID */
	private int layerID;

	/* 描画を行なうキャンバス */
	private CDViewerCanvas canvas;
	/* ノードIDを表示するためのラベル */
	private Label lID;
	private Label lState;
	private Label lGoal;
	private Label lSubgoal;
	private Label lNextState;
	/* ラベルを設定するパネル */
	private Panel panel;


	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param int layerID レイヤID
	 * @param int xNum x軸方向のますの数
	 * @param int yNum y軸方向のますの数
	 */
	public CDViewerFrame(int layerID, int xNum, int yNum) {
//		super("Layer " + layerID);
//		this.layerID = layerID;
//		initViewerFrame(xNum, yNum);
		this(layerID, xNum, yNum, true);
	}

	public CDViewerFrame(int layerID, int xNum, int yNum,
	        boolean flagSeparate) {
		super("Layer " + layerID);
		this.layerID = layerID;
		initViewerFrame(xNum, yNum, flagSeparate);
	}


	/**
	 * 初期化処理
	 * @param int xNum
	 * @param int yNum
	 */
	private void initViewerFrame(int xNum, int yNum, boolean flagSeparate) {

		/* 各層のグラフィック表示を行なうキャンバスの生成 */
		canvas = new CDViewerCanvas(xNum, yNum, flagSeparate);
		panel = new Panel();
		lID = new Label(" [ Layer   ]");
		lState = new Label("   state          ");
		lGoal = new Label("   goal           ");
		lSubgoal = new Label("   subgoal        ");
		lNextState = new Label("   next state     ");

		/* 上の層から順に画面に設定 */
		panel.setLayout(new GridLayout(8, 1));
		panel.add(new Label());
		panel.add(lID);
		panel.add(new Label());
		panel.add(lState);
		panel.add(lGoal);
		panel.add(lSubgoal);
		panel.add(lNextState);
		panel.add(new Label());
		setLayout(new BorderLayout());
		add(panel, "West");
		add(canvas, "Center");

		setSize(400, 250);
		setVisible(true);
	}

	/**
	 * state,goal,upperSubgoal,currentSubgoalに属する環境の状態の
	 * リストを設定します。
	 * @param LinkedList currentStateList 現在の状態に属する状態
	 * @param LinkedList goalStateList ゴールの状態に属する状態
	 * @param LinkedList upperSubgoalList 上位層のサブゴールに属する状態
	 * @param LinkedList currentSubgoalList 現在の層のサブゴールに属する状態
	 */
	public void setSegmentInfo(LinkedList currentStateList, 
	        LinkedList goalStateList, LinkedList upperSubgoalList,
	        LinkedList currentSubgoalList, LinkedList optionList) {
		/* 対応する層のキャンバスに状態のリスト設定 */
		canvas.setCurrentStateList(currentStateList);
		canvas.setGoalStateList(goalStateList);
		canvas.setUpperSubgoalList(upperSubgoalList);
		canvas.setCurrentSubgoalList(currentSubgoalList);
		canvas.setOptionList(optionList);
	}


	/**
	 * ノードの情報を設定します。
	 * @param int[] nodeInfo
	 */
	public void setNodeInfo(int[] nodeInfo) {
		if(nodeInfo[5] == 1) {
			canvas.setRenewFlg(true);
		}
		lID.setText       (" [ Layer " + layerID + " ]");
		lState.setText    ("   state  " + nodeInfo[0]);
		lGoal.setText     ("   goal  " + nodeInfo[1]);
		lSubgoal.setText  ("   subgoal  " + nodeInfo[2]);
		lNextState.setText("   next state  " + nodeInfo[3]);
	}


	/**
	 * repaintのオーバーライド
	 */
	public void repaint() {
		canvas.repaint();
	}


}


