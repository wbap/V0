/**
 * Agent.java
 * 認知距離による問題解決を行なうクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;
import wba.citta.cognitivedistance.viewer.*;

/**
 * 認知距離による問題解決を行なうクラスです。<BR><BR>
 * ＜コンストラクタで設定可能なパラメータのデフォルト値＞<BR>
 *  maxCDLngth                 10<BR>
 *  shallowSearchLngth          3<BR>
 *  deepSearchLngth           200<BR>
 *  minSearchLngth              2<BR>
 *  maxSegmentSize              5<BR>
 *  minSegmentSize              3(現在は機能しません)<BR>
 *  maxFamiliarCount           10<BR>
 *  flagNovelSearch          true<BR>
 *  flagLandmarkSearchDirection false<BR>
 */
public class Agent {

	/* 環境とのインターフェースの変換を行なうクラス */
//	private InterfaceAgent interfaceAgent;
	InterfaceAgent interfaceAgent;

	/* LayeredAgentの配列 */
	private LayeredAgent[] layeredAgentArray;

	/* 使用するLayeredAgent数 */
	private int layerNum;

	/* 新規探索の有無の切り換え用フラグ */
	private boolean flagNovelSearch = true;

	//////////////////////////////////////////////////////////////////
	// コンストラクタ、初期化処理

	/**
	 * コンストラクタ
	 * @param int layerNum 認知距離モジュールを階層化して使用する場合の
	 * レイヤ数。単層での使用する場合は１を指定。
	 */
	public Agent(int layerNum) {
		this.layerNum = layerNum;
		initAgent();
	}


	/**
	 * コンストラクタ
	 * @param int layerNum  認知距離モジュールを階層化して使用する場合の
	 * レイヤ数。単層での使用する場合は１を指定。
	 * @param int maxCDLngth  学習する最大の認知距離
	 * @param int shallowSearchLngth  ゴールを浅く探索する場合の最大の深さ
	 * @param int deepSearchLngth  ゴールを深く探索する場合の最大の深さ
	 * @param int minSearchLngth  ゴールを探索する最小の深さ。-1が指定された
	 * 場合は確率的に探索最小の深さを変化させる。深さは1・2・3・4のいづれかで、
	 * 順に8:4:2:1の割合で選択される。
	 * @param int maxSegmentSize  ランドマーク間の最大距離。ここで指定された
	 * 距離の範囲でランドマークを探索し、ランドマークが無ければ新たなランド
	 * マークを生成します。
	 * @param int minSegmentSize  ランドマーク間の最小距離(現在は機能しません)
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize) {
		this.layerNum = layerNum;
		Node.maxCDLngth = maxCDLngth;
		LayeredAgent.shallowSearchLngth = shallowSearchLngth;
		LayeredAgent.deepSearchLngth = deepSearchLngth;
		LayeredAgent.minSearchLngth = minSearchLngth;
		LayeredAgent.maxSegmentSize = maxSegmentSize;
		LayeredAgent.minSegmentSize = minSegmentSize;
		initAgent();
	}

	/**
	 * コンストラクタ
	 * @param int layerNum  認知距離モジュールを階層化して使用する場合の
	 * レイヤ数。単層での使用する場合は１を指定。
	 * @param int maxCDLngth  学習する最大の認知距離
	 * @param int shallowSearchLngth  ゴールを浅く探索する場合の最大の深さ
	 * @param int deepSearchLngth  ゴールを深く探索する場合の最大の深さ
	 * @param int minSearchLngth  ゴールを探索する最小の深さ。-1が指定された
	 * 場合は確率的に探索最小の深さを変化させる。深さは1・2・3・4のいづれかで、
	 * 順に8:4:2:1の割合で選択される。
	 * @param int maxSegmentSize  ランドマーク間の最大距離。ここで指定された
	 * 距離の範囲でランドマークを探索し、ランドマークが無ければ新たなランド
	 * マークを生成します。
	 * @param int minSegmentSize  ランドマーク間の最小距離(現在は機能しません)
	 * @param boolean flagNovelSearch  新規探索処理を行なうかどうか。
	 * true:行なう false:行なわない
	 * @param int maxFamiliarCount ここで指定された回数連続して、すでに移動
	 * 済みの状態へ移動すると、新規探索処理が行われます。
	 * @param boolean flagLandmarkSearchDirection  セグメント化を行なうために
	 * 行なうランドマークの探索の向き。 true:順方向 false:逆方向
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize, boolean flagNovelSearch, int maxFamiliarCount,
	        boolean flagLandmarkSearchDirection) {
		this(layerNum, maxCDLngth, shallowSearchLngth, deepSearchLngth,
		        minSearchLngth, maxSegmentSize, minSegmentSize);
		this.flagNovelSearch = flagNovelSearch;
		LayeredAgent.maxFamiliarCount = maxFamiliarCount;
		LayeredAgent.flagLandmarkSearchDirection = flagLandmarkSearchDirection;
	}

	/**
	 * Agentクラスの初期化処理。
	 * LayeredAgent,InterfaceAgentを生成します。
	 */
	private void initAgent() {

		/*
		 * LayeredAgentの生成 上位層への参照を下位層のコンストラクタに設定
		 */
		layeredAgentArray = new LayeredAgent[layerNum];
		for(int i = layerNum-1; i >= 0; i--) {
			if(i == layerNum-1) {
				/* 最上位の層には上位層への参照を設定しない */
				layeredAgentArray[i] = new LayeredAgent(null, i);
			}else{
				/* それ以外の層には上位層への参照を設定 */
				layeredAgentArray[i] = new LayeredAgent(layeredAgentArray[i+1],
				        i);
			}
		}

		/* インターフェースエージェントには最下層のlayeredAgentArrayを設定 */
		interfaceAgent = new InterfaceAgent(layeredAgentArray[0]);
	}


	//////////////////////////////////////////////////////////////////
	// public

	/**
	 * 現在の状態からゴールのへ経路探索を行ない、ゴールへ移動するための
	 * 次の状態を取得します。
	 * ゴールへの経路が見つからない場合はnullを返します。
	 * また引数で設定された現在の状態についての認知距離・ForwardModel・
	 * InverseMovelの学習を行ない、階層化されていればセグメント化も行ないます。
	 * @param Vector currentState 現在の状態
	 * @param Vector goalState    ゴールの状態
	 * @return Vector             次の状態
	 * @exception NullPointerException 引数で設定された現在の状態がnullの場合
	 * @exception ElementNumberException 現在の状態の要素数(Vectorのサイズ)が
	 * 不正な場合。状態の要素数は始めに入力された状態の要素数が基準となり、
	 * 以降に入力される状態の要素数は基準となる要素数と同じでなければなりません
	 */
	public Vector getNextState(Vector currentState, Vector goalState) 
	        throws ElementNumberException {

		/* 学習処理 */
		interfaceAgent.learn(currentState);

		/* 次の状態を取得 */
		Vector nextState = interfaceAgent.exec(currentState,goalState);

		/* 新規探索処理 フラグにより新規探索の有無の切り換え */
		if(flagNovelSearch) {
			if(nextState == null) {
				nextState = interfaceAgent.novelSearch(currentState);
			}else {
				interfaceAgent.counterReset();
			}
		}

		return nextState;
	}

	// 2001.04.19 追加 miyamoto
	/**
	 * 認知距離の学習を行ないます。
	 * @param Vector currentState 現在の状態
	 * @param Exception NullPointerException 現在の状態がnullの場合
	 * @param Exception ElementNumberException 現在の状態の要素数が不正な場合
	 */
	public void learn(Vector currentState) throws ElementNumberException {
		interfaceAgent.learn(currentState);
	}

	/**
	 * 実行処理を行ない、ゴールへ移動するための次の状態を取得します。
	 * @param Vector currentState 現在の状態
	 * @param Vector goalState    ゴールの状態
	 * @return Vector             次の状態
	 * @param Exception NullPointerException 現在の状態がnullの場合
	 * @param Exception ElementNumberException 現在の状態の要素数が不正な場合
	 */
	public Vector exec(Vector currentState, Vector goalState)
	        throws ElementNumberException {
		return interfaceAgent.exec(currentState, goalState);
	}
	// ここまで

	/**
	 * 学習データをファイルから読込みます。
	 * @param String fileName ファイル名
	 */
	public void load(String fileName) {
		System.out.println("Loading learning data....");
		try{
			FileInputStream istream = new FileInputStream(fileName);
			ObjectInputStream oInputStream = new ObjectInputStream(istream);

			/* オブジェクトの読込み */
			interfaceAgent.load(oInputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].load(oInputStream);
			}

			oInputStream.close();
			istream.close();

		}catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
	}


	/**
	 * 学習データをファイルに保存します。
	 * @param String fileName ファイル名
	 */
	public void save(String fileName) {
		System.out.println("Saving learning data....");
		try{
			/* ストリームの作成 */
			FileOutputStream ostream = new FileOutputStream(fileName, false);
			ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

			/* オブジェクトの読込み */
			interfaceAgent.save(oOutputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].save(oOutputStream);
			}

			oOutputStream.flush();

			oOutputStream.close();
			ostream.close();

		}catch(Exception e){
			System.out.println(e);
		}

	}


	///////////////////////////////////////////////////////////////////
	// デバック用の情報の取得、動作の制御に使用するメソッド

	/**
	 * 認知距離を学習するために保持している状態の履歴をクリアします。
	 */
	public void reset() {
		interfaceAgent.reset();
	}


	/**
	 * 認知距離・ForwardModel・InverseMovelの学習を行なうか、行なわないか
	 * 設定します。デフォルトでは全ての層の学習を行ないます。
	 * @param int layerID 設定するレイヤ (0〜)
	 * @param boolean flag  true：学習を行なう  false：学習を行なわない
	 */
	public void setLearningFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setLearningFlag(flag);
	}


	/**
	 * セグメント化(セグメント分割、セグメントのランドマークの設定)を行なうか
	 * 、行なわないか設定します。デフォルトでは全ての層のセグメント化を行ない
	 * ます。
	 * @param int layerID 設定するレイヤ (0〜)
	 * @param boolean flag  true：学習を行なう  false：学習を行なわない
	 */
	public void setSegmentationFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setSegmentationFlag(flag);
	}


	/**
	 * 使用するＣＤの最大サイズを変更します。
	 * (テスト用のメソッドでstateBufferへの影響は考慮していない）
	 * @param int lngth 新しい長さ
	 */
//	public void changeMaxCDLngth(int lngth) {
//		Node.maxCDLngth = lngth;
//	}


	/**
	 * 使用するレイヤ数を変更します。(レイヤ数を減らす事のみ可能）
	 * @param int newLayerNum  新しいレイヤ数
	 */
//	public void changeLayerNum(int newLayerNum) {
//		if(newLayerNum < layerNum) {
//			/* LayeredAgentの配列の内使用しないものも削除 */
//			for(int i = 0; i < layerNum; layerNum++) {
//				if(i < newLayerNum) {
//				}else {
//					layeredAgentArray[i] = null;
//				}
//			}
//			/* 新しい最上位層の上位層を削除 */
//			layeredAgentArray[newLayerNum-1].deleteUpperLayer();
//			layerNum = newLayerNum;
//		}
//	}


	/**
	 * 指定された状態・レイヤに対応するノードクラスのオブジェクトを取得します。
	 * @param Object state 環境側での状態
	 * @param int layer    レイヤ
	 * @return Node        ノード
	 */
	public Node getNode(Vector state, int layer) {
		return interfaceAgent.getNode(state, layer);
	}


	/**
	 * 行動済みの全状態を取得します。
	 * @return Vector 行動済みの全状態の設定されたVector
	 */
	public Vector getStateTable() {
		return interfaceAgent.getIdToState();
	}


	/**
	 * 引数で指定した層の実行時の処理に関する情報を取得します。
	 * @param int layerNum  情報を取得するレイヤ
	 * @return int[]        実行時の処理に関する情報<BR>
	 *                      値のない状態については-1が設定される<BR>
	 *                      int[0] 現在の状態のID<BR>
	 *                      int[1] ゴールの状態のID<BR>
	 *                      int[2] 上位層からのサブゴールの状態のID<BR>
	 *                      int[3] 次の状態のID<BR>
	 *                      int[4] 次の状態を出力している処理のID<BR>
	 *                      int[5] サブゴールが更新されているか
	 *                             0：更新されていない 1：更新されている<BR>
	 */
	public int[] getExecInfo(int layerNum) {
		ExecInfo ei = layeredAgentArray[layerNum].getExecInfo();
		return ei.getExecInfo();
	}

	/**
	 * 全ての層の実行時の処理に関する情報をクリアします。<BR>
	 * 各層の実行時の処理に関する情報はゴールが無くなった場合等、その層の処理が
	 * 行われなくなるため、以前の情報が残ってしまいます。
	 * このため必要に応じて情報をクリアします。
	 */
//	public void resetExecInfo() {
//		for(int i = 0; i < layerNum; i++) {
//			layeredAgentArray[i].resetExecInfo();
//		}
//	}

	/**
	 * 引数で指定した層のゴール探索に関する情報を取得します。
	 * @param int layerNum 情報を取得するレイヤ
	 * @param int dx       情報を取得する処理 D1〜D4 (0〜3で指定)
	 * @return int[]       ゴールの探索に関する情報が設定された配列<BR>
	 *                     指定された処理が行なわれていない場合はnullを返す<BR>
	 *                     int[0] 探索されたノードのID
	 *                            探索の結果見つからなかった場合-1<BR>
	 *                     int[1] 探索されたノードからゴールまでのCDの長さ
	 *                            探索の結果見つからなかった場合-1<BR>
	 *                     int[2] 探索れた深さ<BR>
	 *                     int[3] 探索された状態数<BR>
	 */
//	public int[] getGoalSearchInfo(int layerNum, int dx) {
//		GoalSearchInfo gsi = layeredAgentArray[layerNum].getGoalSearchInfo();
//		return gsi.getGoalSearchInfo(dx);
//	}


	/**
	 * 引数で指定した層の学習状況に関する情報を取得します。
	 * @param int layerNum  情報を取得するレイヤ
	 * @return int[]        学習状況に関する情報<BR>
	 *                      int[0] 全状態のMoveableStateのサイズの合計<BR>
	 *                      int[1] 全状態のCognitiveDistanceのサイズの合計<BR>
	 *                      int[2] 全状態数<BR>
	 *                      int[3] 有効な状態数(ランドマークの削除時に影響)<BR>
	 */
//	public int[] getLearningInfo(int layerNum) {
//		return layeredAgentArray[layerNum].getLearningInfo();
//	}

	// 2001.08.09 追加 miyamoto
	/**
	 * 最小で探索を行う深さを取得します。
	 * @return int 最小で探索を行う深さ
	 */
	public int getMinSearchLngth() {
		return LayeredAgent.minSearchLngth;
	}

	// 2001.08.09 追加 miyamoto
	/**
	 * 最小で探索を行う深さを設定します。
	 * @param int minSearchLngth 最小で探索を行う深さ
	 */
	public void setMinSearchLngth(int minSearchLngth) {
		LayeredAgent.minSearchLngth = minSearchLngth;
	}


	// 2001.08.14 追加 miyamoto
	/**
	 * 状態 a から状態 b への到達可能性を調べます。
	 * @param Vector a
	 * @param Vector b
	 * @return boolean true 到達可能 false 到達不可能
	 */
	public boolean isReach(Vector a, Vector b) {
		return interfaceAgent.isReach(a, b);
	}

	// 2001.08.15 追加 miyamoto
	/**
	 * 各層で保持している、以前の状態・ゴールに関する情報をクリアします。
	 */
	public void resetOldValue() {
		interfaceAgent.resetOldValue();
	}


}
