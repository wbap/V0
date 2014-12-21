/**
 * LayeredAgent.java
 * CognitiveDistanceの１つの層についての処理を行うエージェントのクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;
import wba.citta.cognitivedistance.viewer.*;

/**
 * CognitiveDistanceの１つの層についての処理を行うエージェントのクラスです。
 */
public class LayeredAgent {

	/* このエージェントのレイヤID */
	private int layerID;

	/* 上位層のエージェント */
	private LayeredAgent upperLayerAgent;
	/* 複数のNodeをもつVector */
	private Vector nodes;
	/* 移動したノードID(Integer)の履歴 */
	private StateBuffer stateBuffer;

	/**
	 * 浅い探索を行なう最大の深さ
	 * 到達可能なノードが見つかるか、この値の深さまでの探索を行う
	 */
	public static int shallowSearchLngth = 3;

	/**
	 * 深い探索を行なう最大の深さ
	 * 到達可能なノードが見つかるか、この値の深さまでの探索を行う
	 */
	public static int deepSearchLngth = 200;

	/**
	 * 最小で行う探索の深さ
	 * ここで指定された深さまで探索を行い、最も近い距離のノードを選択
	 * (サブゴールにの探索にも共通)
	 */
	public static int minSearchLngth = 2;

	/**
	 * ランドマークを設定していく間隔
	 * この範囲内でランドマークを探索し、なければ自らをランドマークに設定
	 * (0の場合すべてをランドマーク化)
	 */
	public static int maxSegmentSize = 5;

	/**
	 * ランドマーク間の最小の間隔
	 * この範囲内に他のランドマークがある場合は現在のランドマークを削除する
	 * (0の場合は削除は行なわない)
	 */
	public static int minSegmentSize = 3;

	/**
	 * 新規探索によるサブゴールを設定するまでの間隔
	 */
	public static int maxFamiliarCount = 10;

	// 2001.05.22 追加 miyamoto 
	/**
	 * セグメント化のランドマーク探索の向きを切り換えるフラグ
	 * true:ForwardModelでセグメント化  false:InverseModelでセグメント化
	 */
	public static boolean flagLandmarkSearchDirection = false;


	/* セグメント数のカウント */
	private int segmentCount;

	/* ゴール探索に使用する変数 */
	/* 一つ前の上位層の次の状態 */
	private Integer id_Vu0;
	/* 一つ前のゴールの状態 */
	private Integer id_Gu0;
	/* 上位層を利用するかどうかのフラグ */
	private boolean useUpperFlag = true;

	/* 新規状態の探索に使用する変数 */
	/* 前サイクルの状態 */
	private Integer id_S0;
	/* 前サイクルの自らの層での新規探索によるサブゴール */
	private Integer id_F0;
	/* 前サイクルの新規探索処理で上位層から取得したサブゴール */
	private Integer id_FVu0;
	/* 知っている状態が連続した数のカウンター */
	private int familiarCount;

	/* テスト・実験用の変数 */
	/* 階層化・セグメント化に関しての情報の設定用 */
	private ExecInfo execInfo = new ExecInfo();
	/* ゴール探索時の情報の設定用 */
	private GoalSearchInfo goalSearchInfo = new GoalSearchInfo();

	/** 学習を行なうかどうかのフラグ */
	private boolean learningFlag = true;
	/* ランドマークの学習を行なうどうかのフラグ */
	private boolean landmarkLearningFlag = true;

	/////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param LayeredAgent layeredAgent  上位層の処理を行うエージェント
	 * @param int layerID               このエージェントが処理を行うレイヤのID
	 */
	public LayeredAgent(LayeredAgent layeredAgent, int layerID) {

		/* レイヤのID */
		this.layerID = layerID;
		/* 上位層のエージェント */
		upperLayerAgent = layeredAgent;
		/* 現在の層での状態を管理するVector */
//		nodes = new Vector();
		nodes = new Vector(10000);
		/* StateBufferの最大のサイズはＣＤの最大値＋１ */
		stateBuffer = new StateBuffer(Node.maxCDLngth+1);

		/* パラメータの表示 */
		System.out.println("LayerID " + layerID);
		System.out.println(" Max CD Length             " + Node.maxCDLngth);
		System.out.println(" Shallow Search Length     " + shallowSearchLngth);
		System.out.println(" Deep Search Length        " + deepSearchLngth);
		System.out.println(" Min Search Length         " + minSearchLngth);
		System.out.println(" Max Segment Size          " + maxSegmentSize);
		System.out.println(" Min Segment Size          " + minSegmentSize);

	}


	//////////////////////////////////////////////////////////////////////////
	// Nodeの操作

	/**
	 * 引数のIDに対応するノードオブジェクトを取得します。
	 * @param Integer id ノードのID
	 * @return Node      ノード
	 */
	public Node getNode(Integer id) {

		if(id == null) {
			return null;
		}

		int intID = id.intValue();
		if( nodes.size() <= intID ) {
			return null;
		}
		// 2001.05.24 修正 miyamoto 古いバージョンのjavaに対応
//		return (Node)nodes.get(intID);
		return (Node)nodes.elementAt(intID);
	}

	/**
	 * 新しいノードオブジェクトを生成します。
	 * @param Integer lowerID 下位層でのノードのID
	 * @return Node            生成されたノード
	 */
	public Node newNode(Integer lowerID) {
		Node node = new Node(new Integer(nodes.size()), lowerID);
		// 2001.05.24 修正 miyamoto 古いバージョンのjavaに対応
//		nodes.add(node);
		nodes.addElement(node);
		return node;
	}

	/**
	 * ノードを無効にします。
	 * @param Integer nodeID    無効にするノードID
	 * @param Integer refID 今後の参照先のID
	 * @param int refStep   参照先までの距離
	 * @return boolean      true 削除成功 false 削除失敗
	 */
	private boolean deleteNode(Integer nodeID, Integer refID, int refStep) {
		Node node = getNode(nodeID);
		return node.delete(refID, refStep);
	}

	/**
	 * 引数のノードIDに対応する上位層のノードIDを取得します。
	 * @param Integer nodeID ノードID
	 * @return Integer       上位層のノードID
	 */
	private Integer getUpperLayerNodeID(Integer nodeID) {

		if(nodeID == null) {
			return null;
		}
		/* 上位層のノードが無効になっていないかチェック・無効なら更新 */
		Integer upperLayerNodeID = renewUpperIDAndStep(nodeID);
		return upperLayerNodeID;
	}

	/**
	 * 引数のノードIDに対応する下位層のノードIDを取得します。
	 * @param Integer nodeID ノードID
	 * @return Integer       下位層のノードID
	 */
	private Integer getLowerLayerNodeID(Integer nodeID) {

		if(nodeID == null) {
			return null;
		}
		/* ノードから下位層のIDを取得 */
		Node node = getNode(nodeID);
		Integer lowerLayerNodeID = node.getLowerID();

		return lowerLayerNodeID;
	}

	/**
	 * ランドマークまでのステップ数を取得します。
	 * @param Integer currentNodeID 現在層での状態
	 * @return int                  ステップ数
	 */
	private int getToLandmarkStep(Integer nodeID) {
		if(nodeID == null) {
			return -1;
		}
		/* ノードの有効性をチェックし、情報を更新 */
		renewUpperIDAndStep(nodeID);
		Node node = getNode(nodeID);
		int step = node.getToLandmarkStep();
		return step;
	}


	/**
	 * 上位層の状態が更新されていないかチェック、更新されていれば新しい状態
	 * に修正し、そのノードIDを取得
	 * @param Integer nodeID  現在の層でのノードID
	 * @return Integer        修正後の上位層のノードID
	 */
	private Integer renewUpperIDAndStep(Integer nodeID) {
		Node node = getNode(nodeID);
		/* 上位層のIDを取得 */
		Integer upperLayerNodeID = node.getUpperID();

		if(upperLayerNodeID == null) {
			return null;
		}

		/* 上位層のノードを取得 */
		Node upperLayerNode = upperLayerAgent.getNode(upperLayerNodeID);

		/* 上位層のノードの有効性をチェック 無効の場合は情報を参照先に修正*/
		boolean validty = upperLayerNode.isValid();
		if( validty == false ) {
			// 2000.11.30 修正
			// ランドマークが削除されている場合はセグメント内の状態は
			// どのセグメントにも属さないようにする
			node.setUpperIDAndStep(null, -1);
			upperLayerNodeID = null;
			/* 参照先に情報を置き換え */
//			Integer newUpperNodeID = upperNode.getReferenceNodeID();
//			int newLandmarkStep = upperNode.getRefarenceStep();
//			int nowStep = currentNode.getToLandmarkStep();
//			node.setUpperIDAndStep(newUpperNodeID,
//			        nowStep+newLandmarkStep);
//			/* 更新後の上位層のIDについてもチェック */
//			upperLayerNodeID = renewUpperIDAndStep(nodeID);
		}
		return upperLayerNodeID;
	}


	/**
	 * 引数のノードIDから順方向に直接移動可能なノードのIDをリストで取得します。
	 * @param Integer nodeID ノードID
	 * @return LinkedList    引数のノードIDから順方向に直接移動可能なノードID
	 *                       のリスト
	 */
	private LinkedList getForwardNodeIDList(Integer nodeID) {
		Node node = getNode(nodeID);
		LinkedList forwardNodeIDList = node.getForwardNodeIDList();
// ランドマーク削除の処理については保留中
		/* リスト内の削除されたノードIDを参照先に置き換え */
//		renewNodeList(forwardNodeIDList);
		/* 自らと同じIDがリスト内にある場合は削除 */
		node.removeSameForwardNodeID();
		/* 置き換えなしで、削除のみ行なう */
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer forwardNodeID = (Integer)li.next();
			Node forwardNode = getNode(forwardNodeID);
			if(forwardNode.isValid() == false) {
				li.remove();
			}
		}
		return forwardNodeIDList;
	}

	/**
	 * 引数のノードIDから逆方向に直接移動可能なノードのIDをリストで取得します。
	 * @param Integer nodeID ノードのID
	 * @return LinkedList    引数のノードIDから逆方向に直接移動可能なノードID
	 *                       のリスト
	 */
	private LinkedList getInverseNodeIDList(Integer nodeID) {
		Node node = getNode(nodeID);
		LinkedList inversNodeIDList = node.getInverseNodeIDList();
// ランドマーク削除の処理については保留中
		/* リスト内の削除されたノードIDを参照先に置き換え */
//		renewNodeList(inversNodeIDList);
		/* 自らと同じIDがリスト内にある場合は削除 */
		node.removeSameInverseNodeID();
		/* 置き換えなしで、削除のみ行なう */
		ListIterator li = inversNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer inverseNodeID = (Integer)li.next();
			Node inverseNode = getNode(inverseNodeID);
			if(inverseNode.isValid() == false) {
				li.remove();
			}
		}
		return inversNodeIDList;
	}

	/**
	 * リスト内の各状態をチェックし、無効なノードについては参照先のノードに
	 * 置き換えます。
	 * @param LinkedList nodeIDList ノードIDのリスト
	 */
	private void renewNodeIDList(LinkedList nodeIDList) {
		ListIterator li = nodeIDList.listIterator();
		while(li.hasNext()) {
			Integer nodeID = (Integer)li.next();
			nodeID = renewNodeID(nodeID);
			li.set(nodeID);
		}
	}

	/**
	 * ノードの有効性をチェックし、無効ならIDを参照先のIDに置き換えます。
	 * @param Integer nodeID  有効性をチェックするノードID
	 * @return Integer        有効なノードに修正後のノードID
	 */
	private Integer renewNodeID(Integer nodeID) {
		Node node = getNode(nodeID);
		if(node.isValid() == false) {
			nodeID = node.getReferenceNodeID();
			/* 参照先のノードIDについても有効性をチェック */
			renewNodeID(nodeID);
		}
		return nodeID;
	}

	///////////////////////////////////////////////////////////////////////////
	// 処理の分離 2001.01.29 miyamoto

	/**
	 * 認知距離の学習を行ないます。
	 * @param Integer id_S 現在のノードID
	 */
	public void learn(Integer id_S) {

		/* フラグにより学習を制御 */
		if(learningFlag) {
			/* 状態が変化していなければ学習を行なわない */
			if( (id_S != null) && ( (stateBuffer.size() == 0) ||
			        (!id_S.equals(stateBuffer.getLast())) ) ) {
				/* この層の学習 */
//				System.out.println();
//				System.out.println("[layerID:" + layerID + "] learn");
//				System.out.println("  CurrentNodeID " + id_S);
				learning(id_S);

				/* 実行処理に関する情報を設定 */
				execInfo.setNodeID(id_S);

				/* 上位層があれば上位層の学習 */
				if(upperLayerAgent != null) {
					/* 状態を変換 */
					Integer id_Su = getUpperLayerNodeID(id_S);
					upperLayerAgent.learn(id_Su);
				}
			}
		}
	}


	/**
	 * 実行処理を行ないます。
	 * @param Integer id_S 現在のノードID
	 * @param Integer id_G ゴールのノードID
	 * @return Integer     次のノードID
	 */
	public Integer exec(Integer id_S, Integer id_G) {

		/* 現在の状態がなければ処理しない */
		/* 学習が十分でない状態で学習を停止した場合発生 */
		if( id_S == null) {
//			System.out.println("state == null");
			return null;
		}

		/* 画面表示 */
//		System.out.println("");
//		System.out.println("[layerID:" + layerID + "] exec");
//		System.out.println("  CurrentNodeID " + id_S);
//		System.out.println("  GoalNodeID    " + id_G);

		/* 実行処理に関する情報を設定 */
		execInfo.setGoalNodeID(id_G);

		/* 探索情報をクリア */
		/* (この層自体を使用しない場合のクリア処理はどうするか？) */
		goalSearchInfo.clear();
		// 2001.04.10 追加 miyamoto
		// ゴールの状態が無い場合に以下の変数の値がクリアされず、
		// 以前の値がgoalSearchInfoに設定されてしまう
		goalSearchCDLngth = null;
		goalSearchLngth = 0;
		goalSearchNum = 0;

		/* 上位層あり */
		if( upperLayerAgent != null ) {
			/* 上位層の状態に変換 */
			Integer id_Su = getUpperLayerNodeID(id_S);
			Integer id_Gu = getUpperLayerNodeID(id_G);

			/* 上位層の現在の状態がなければ処理しない */
			/* フェーズ分けにより発生する状況に対応 */
			if(id_Su == null) {
				return null;
			}

			/* ゴールの変化を表わすフラグ */
			boolean goalChangeFlag = true;

			/* 上位層利用の条件 */
			if( id_Gu != null ) {
				/* ゴールの変化をチェック */
				if( id_Gu.equals(id_Gu0) ) {
					goalChangeFlag = false;
				}

				if( id_Su.equals(id_Gu) ) {
					/*
					 * 上位層の状態(Su)がゴール(Gu)に到達した場合は
					 * 使用しない
					 */
					useUpperFlag = false;
				}else if(goalChangeFlag) {
					/*
					 *ゴール(Gu)が変更された場合は使用する
					 */
					useUpperFlag = true;
				}
			}else {
				/* 上位層のゴールがなければ使用しない */  
				useUpperFlag = false;
			}

			/* １サイクル前のゴールを保持 */
			id_Gu0 = id_Gu;

			if(useUpperFlag) {
				/* 自身の層の浅い探索処理でゴール(G)を探索 */
				Integer id_D1 = getNextNodeID(id_S, id_G, shallowSearchLngth);
				/* 探索情報を設定 */
				goalSearchInfo.setGoalSearchInfo(0, id_D1, goalSearchCDLngth,
				        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D1]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_G " + id_G);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D1 " + id_D1);
				if(id_D1 != null) {
					/* Vu0をクリア */
					id_Vu0 = null;
					/* 実行処理に関する情報を設定 */
					execInfo.setNextNodeID(id_D1, 0, null, false);
					return id_D1;
				}

				/*
				 * 以下のいづれかの場合新たにVuを取得する。
				 * ・前サイクルの上位層の実行処理によるサブゴール(Vu0)
				 *   がない
				 * ・前サイクルの上位層の実行処理によるサブゴール(Vu0)
				 *   に到達
				 * ・上位層のゴール(Gu)が前サイクルの上位層のゴール
				 *   (Gu0)と異なる
				 */
				if( ( !id_Su.equals(id_Vu0) ) && ( id_Vu0 != null) 
				        && (!goalChangeFlag) ) {

					/* V0を探索 */
					Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(
					        id_Vu0);
					Integer id_D2 = getNextNodeID(id_S, id_V0,
					        deepSearchLngth);
					/* 探索情報を設定 */
					goalSearchInfo.setGoalSearchInfo(1, id_D2,
					        goalSearchCDLngth, goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D2]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_V0 " + id_V0);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D2 " + id_D2);
					if(id_D2 != null) {
						/* 実行処理に関する情報を設定 */
						execInfo.setNextNodeID(id_D2, 1, id_V0, false);
						return id_D2;
					}
				}
				/* Vuを取得し、Vを探索 */
				id_Vu0 = upperLayerAgent.exec(id_Su, id_Gu);
				Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(id_Vu0);
				Integer id_D3 = getNextNodeID(id_S, id_V0,
				        deepSearchLngth);
				/* 探索の情報設定 */
				goalSearchInfo.setGoalSearchInfo(2, id_D3, goalSearchCDLngth,
				        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D3]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_V0 " + id_V0);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D3 " + id_D3);
				if(id_D3 != null) {
					/* 実行処理に関する情報を設定 */
					execInfo.setNextNodeID(id_D3, 2, id_V0, true);
					return id_D3;
				}
			}
		}

		/*
		 * 上位層を利用しない、または上位層を利用して次の状態を取得できない
		 * 場合はV0を保持しない
		 */
		id_Vu0 = null;

		/* ゴールについて深い探索処理を行なう */
		Integer id_D4 = getNextNodeID(id_S, id_G, deepSearchLngth);
		/* 探索の情報設定 */
		goalSearchInfo.setGoalSearchInfo(3, id_D4, goalSearchCDLngth,
		        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D4]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_G " + id_G);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D4 " + id_D4);
		if(id_D4 != null) {
			/* 実行処理に関する情報を設定 */
			execInfo.setNextNodeID(id_D4, 3, null, false);
			// 2001.09.06 追加 miyamoto
			// D4の処理を行なったらゴール到達までD4の処理を行なう
			useUpperFlag = false;
			return id_D4;
		}

		return null;
	}


	/**
	 * より知らない状態へ移動するための直接移動可能な状態を取得します。
	 * @param Integer id_S 現在のノードID
	 * @return Integer     次のノードID
	 */
	public Integer novelSearch(Integer id_S) {

		/* 画面表示 */
//		System.out.println();
//		System.out.println("[layerID:" + layerID + "] novelSearch");
//		System.out.println("  CurrentNodeID " + id_S);
//		System.out.println("  NodeID old" + id_S0);
//		System.out.println("  node num" + nodes.size());
//		System.out.println("  F0 " + id_F0 );
//		System.out.println("  FVu0 " + id_FVu0 );

		/* 状態の変化がなければカウント・リセット処理をしない */
		if(!id_S.equals(id_S0)) {
			/* 現在の状態への移動回数をチェック */
			Node node = getNode(id_S);
			if(node.getVisitCount() > 0) {
				/* すでに移動済みの状態ならカウント */
				familiarCount++;
			}else {
				/* 新しい状態なら現在の層とそれ以上の層のカウンタをリセット */
				resetUpperAndThisLayerFamiliarCount();
			}
		}

//		System.out.println("  familiarCount " + familiarCount);

		Integer id_D = novelSearchCore(id_S);

		/* Dが取得できている場合はカウントをリセット */
		if(id_D != null) {
			resetFamiliarCount();
		}

		/* 前サイクルの状態を保持 */
		id_S0 = id_S;

		return id_D;
	}


	/**
	 * 新規探索処理の主用部
	 * より知らない状態へ移動するための直接移動可能な状態を取得します。
	 * @param Integer id_S 現在の状態
	 * @return Integer     次の状態
	 */
	private Integer novelSearchCore(Integer id_S) {

		/* ① 前サイクルの新規探索のサブゴールに(F0)に到達していない */
		if( (id_F0 != null) && (!id_S.equals(id_F0)) ) {
			/* F0を探索 */
			Integer id_D8 = getNextNodeID(id_S, id_F0, 1);
			if(id_D8 != null) {
				return id_D8;
			}
		}
		/* 到達できない場合はクリア */
		id_F0 = null;

		/* 上位層がある場合の処理 */
		if( upperLayerAgent != null ) {
			/* 上位層の状態に変換 */
			Integer id_Su = getUpperLayerNodeID(id_S);

			/* ② 上位層の状態SuがFVu0に到達していない */
			if( (!id_Su.equals(id_FVu0)) && (id_FVu0 != null) ) {
				/* FVu0に対応するFV0を探索 */
				Integer id_FV0 = upperLayerAgent.getLowerLayerNodeID(
				        id_FVu0);
				Integer id_D5 = getNextNodeID(id_S, id_FV0,
				        deepSearchLngth);
				if(id_D5 != null) {
					return id_D5;
				}
			}else {
			/* ③ SuがFVu0に到達 または FVu0がない */
				/* 新たにFVuを取得し、対応するFVを探索 */
				Integer id_FVu = upperLayerAgent.novelSearch(id_Su);
				Integer id_FV = upperLayerAgent.getLowerLayerNodeID(id_FVu);
				Integer id_D6 = getNextNodeID(id_S, id_FV,
				        deepSearchLngth);
				if(id_D6 != null) {
					/* 新たに取得したサブゴールを保持 */
					id_FVu0 = id_FVu;
					return id_D6;
				}
			}
		}
		/* 到達できない場合はクリア */
		id_FVu0 = null;

		/* ④ 知っている状態が連続し、カウンタが貯まっている */
		if( familiarCount > maxFamiliarCount ) {
			/* 新規探索処理でサブゴール(F)を取得 */
			Integer id_F = getNovelNodeID(id_S);
			/* Fを探索 */
			Integer id_D7 = getNextNodeID(id_S, id_F, 1);
			if(id_D7 != null) {
				/* 新たに取得したFを保持 */
				id_F0 = id_F;
				return id_D7;
			}
		}

		return null;
	}

	// 2001.08.14 追加 miyamoto
	/**
	 * 状態 a から状態 b への到達可能性を調べます。
	 * @param Integer a
	 * @param Integer b
	 * @return boolean true 到達可能 false 到達不可能
	 */
	public boolean isReach(Integer a, Integer b) {
		boolean isReach = false;
		Node node = getNode(a);
		LinkedList forwardNodeIDList = node.getForwardNodeIDList();
		if( searchReachableState(forwardNodeIDList, b) ) {
			return true;
		}

		if( isReach == false ) {
			if( upperLayerAgent != null ) {
				Integer upperA = getUpperLayerNodeID(a);
				Integer upperB = getUpperLayerNodeID(b);
				isReach = upperLayerAgent.isReach(upperA, upperB);
			}else {
				// 深く探索
//				Integer subgoal = exec(a, b);
//				if(subgoal != null) {
//					isReach = true;
//				}
				if( isReachSearchDeep(forwardNodeIDList, b) ) {
					isReach = true;
				}
			}
		}

		return isReach;
	}

	Hashtable checkTable = null;
	private boolean isReachSearchDeep(LinkedList list,
	        Integer target) {
		checkTable = new Hashtable();
		for(int i = 0; i < deepSearchLngth; i++) {
			LinkedList nextList = getNextList(list);
			boolean isReach = searchReachableState(nextList, target);
			if(isReach) {
				return true;
			}
			list = nextList;
		}
		return false;
	}

	/**
	 * 引数で与えられたリスト内の状態から、引数で与えられた目的に到達可能な
	 * 状態があるか探索します。
	 */
	private boolean searchReachableState(LinkedList list, Integer target) {
		ListIterator li = list.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			Node node = getNode(id);
			int distance = node.getCognitiveDistance(target);
			if(distance != -1) {
				return true;
			}
		}
		return false;
	}

	private LinkedList getNextList(LinkedList list) {
		LinkedList nextList = new LinkedList();
		ListIterator li = list.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			Node node = getNode(id);
			LinkedList forwardNodeIDList = node.getForwardNodeIDList();
			ListIterator forwardNodeIDListIterator
			         = forwardNodeIDList.listIterator();
			while(forwardNodeIDListIterator.hasNext()) {
				Integer forwardNodeID
				        = (Integer)forwardNodeIDListIterator.next();
				if(checkTable.get(forwardNodeID) == null) {
					checkTable.put(forwardNodeID, forwardNodeID);
					nextList.add(forwardNodeID);
				}
			}
		}
		return nextList;
	}


	// ここまで
	///////////////////////////////////////////////////////////////////////////

	/**
	 * nodeIDからgoalNodeIDへ移動するのに、最短の距離をもつ直接移動可能な
	 * 状態を取得します。
	 * 移動可能な状態が見つからなければmaxSearchLengthで指定された深さまで
	 * 探索を行ないます。
	 * @param Integer nodeID       ノードID
	 * @param Integer goalNodeID   ゴールのID
	 * @param int maxSearchLength  探索を行なう最大の深さ
	 * @return Integer             次の状態のノードID
	 */
	private Integer getNextNodeID(Integer nodeID, Integer goalNodeID,
	        int maxSearchLength) {
		/* ゴールの状態がなければ処理しない */
		if( goalNodeID == null ) {
			return null;
		}

		/*
	 	 * ゴールへの最短距離を持つ状態と、その状態をもつStateListを取得
		 */
		Object[] selectedStateInfo = getNextNodeInfo(nodeID, goalNodeID,
		        maxSearchLength);

		/*
		 * 取得した状態が直接移動可能な状態でなければ、その状態に対しての
		 * 移動可能な状態を取得
		 */
		Integer nextNodeID = pathLearning(selectedStateInfo, goalNodeID);

		return nextNodeID;
	}

private Random randomSearch = new Random(0);

	/**
	 * ゴールへの最短距離もつノードに関する情報を取得します。
	 * 探索はminSearchLngth内で行ない、そこにゴールへの最短距離を持つ状態が
	 * ない場合、maxSearchLngthまで探索処理を行ないます。
	 * @param Integer nodeID       ノードID
	 * @param Integer goalNodeID   ゴールのノードID
	 * @return Object[]            最短の距離を持つノードに関する情報
	 *                             Object[0] nodeID
	 *                             Object[1] Distance
	 *                             Object[2] StateList
	 */
	private Object[] getNextNodeInfo(Integer nodeID, Integer goalNodeID,
	        int maxSearchLength) {

		/* 同一状態を再度検索しないためのテーブル */
		Hashtable checkTable = new Hashtable();

		/* 自らをテーブルに設定 */
		checkTable.put(nodeID, nodeID);

		/* 探索を行なうノードのリストの配列 */
		StateList[] stateListArray = null;

		/* 選択されたノードの情報を設定 */
		Object[] selectedObj = null;
		int selectedLngth = 0;
		int currentLngth = 0;

		/* minSearchLngthまで探索 */
//		for( ; currentLngth < minSearchLngth; currentLngth++) {
		// 2001.04.16 修正 miyamoto
		/* 最小で行なう探索の深さをランダムに設定する */
		int minLoopLngth = minSearchLngth;
		if(minLoopLngth == -1) {
//			// 2001.05.25 修正 miyamoto 古いバージョンのjavaに対応
			int num = randomSearch.nextInt(15);
			if(num < 8) {
				minLoopLngth = 1;
			}else if(num < 12) {
				minLoopLngth = 2;
			}else if(num < 14) {
				minLoopLngth = 3;
			}else {
				minLoopLngth = 4;
			}
//			double num = randomSearch.nextDouble();
//			if(num < 0.51) {
//				minLoopLngth = 1;
//			}else if(num < 0.79) {
//				minLoopLngth = 2;
//			}else if(num < 0.83) {
//				minLoopLngth = 3;
//			}else {
//				minLoopLngth = 4;
//			}
			// ここまで
		}
		for( ; currentLngth < minLoopLngth; currentLngth++) {
		/* ここまで */

			/* 初めのstateListArrayの作成は特殊な処理 */
			if(stateListArray == null) {
				/* 直接移動可能な状態のリストを取得 */
				StateList nextNodeIDList = getChildList(nodeID, null,
				        checkTable);
				stateListArray = new StateList[1];
				stateListArray[0] = nextNodeIDList;
			}else {
				/* 次のStateListの配列を取得 */
				stateListArray = getChildListArray(stateListArray, checkTable);
			}

			Object[] obj = getNextNodeInfoFromStateListArray(stateListArray,
			        goalNodeID);
			/* 比較処理 */
			if( obj != null ) {
				if( selectedObj == null ) {
					selectedObj = obj;
				}else {
					/* 距離の短い方を設定 距離には探索した深さをたす */
					if( ((Integer)obj[1]).intValue() + currentLngth <=
					        (((Integer)selectedObj[1]).intValue()
					        + selectedLngth)){
						selectedLngth = currentLngth;
						selectedObj = obj;
					}
				}
			}
		}

		/*
		 * minSearchFNLngthでゴールへ到達可能なノードがない場合は、
		 * maxSearchFNdepthまで探索を行う
		 */
		for( ; currentLngth < maxSearchLength; currentLngth++) {
			if( selectedObj != null ) {
				break;
			}
			/* 初めのstateListArrayの作成は特殊な処理 */
			if(stateListArray == null) {
				/* 直接移動可能な状態のリストを取得 */
				StateList nextNodeIDList = getChildList(nodeID, null,
				        checkTable);
				stateListArray = new StateList[1];
				stateListArray[0] = nextNodeIDList;
			}else {
				/* 次のStateListの配列を取得 */
				stateListArray = getChildListArray(stateListArray, checkTable);
			}

			// 2001.04.20 追加 miyamoto
			/* 探索するリストが無ければ処理をしない */
			if(stateListArray.length == 0) {
				break;
			}
			selectedObj = getNextNodeInfoFromStateListArray(stateListArray,
			        goalNodeID);
		}

		/* テスト用  ゴール探索に関する情報を設定 */
		/* CDの長さ */
		if(selectedObj != null) {
			goalSearchCDLngth = (Integer)selectedObj[1];
		}else{
			goalSearchCDLngth = null;
		}
		/* 探索の深さ */
		goalSearchLngth = currentLngth;
		/* 探索した状態 */
		goalSearchNum = checkTable.size();

		return selectedObj;
	}

	/* ゴール探索の情報を保持 探索状況の取得用 */
	Integer goalSearchCDLngth; /* 探索されたCDの長さ */
	int goalSearchLngth;       /* 探索された深さ */
	int goalSearchNum;         /* 探索した状態数 */

	/**
	 * stateListの配列からゴールへの最短距離を持つ状態を取得します。
	 * @param StateList[] stateListArray StateListの配列
	 * @param Integer goalNodeID         ゴールのノードID
	 * @return Object[]                   最短の距離を持つノードに関する情報
	 *                                    Object[0] nodeID
	 *                                    Object[1] Distance
	 *                                    Object[2] StateList
	 */
	private Object[] getNextNodeInfoFromStateListArray(
	        StateList[] stateListArray, Integer goalNodeID) {

		/* 最短の距離を持つ状態を探索 */
		Object[] wkObj = null;
		StateList selectedList = null;
		for(int i = 0; i < stateListArray.length; i++) {
			Object[] obj = getNextNodeInfoFromStateList(stateListArray[i],
			        goalNodeID);
			if(obj != null) {
				if((wkObj == null) ||
				         ( ((Integer)wkObj[1]).intValue() >
				         ((Integer)obj[1]).intValue()) ){
					wkObj = obj;
					selectedList = stateListArray[i];
				}
			}
		}

		/* 最短距離を持つ状態のあるリストを追加 */
		Object[] selectedObj = null;
		if(wkObj != null) {
			selectedObj = new Object[3];
			selectedObj[0] = wkObj[0];
			selectedObj[1] = wkObj[1];
			selectedObj[2] = selectedList;
		}
		return selectedObj;
	}


	/**
	 * stateListからゴールへの最短距離を持つ状態を取得します。
	 * @param StateList stateList   StateList
	 * @param Integer goalNodeID    ゴールのノードID
	 * @return Object[]             最短の距離を持つノードに関する情報
	 *                              Object[0] nodeID
	 *                              Object[1] Distance
	 */
	private Object[] getNextNodeInfoFromStateList(StateList stateList,
	        Integer goalNodeID) {

		/* 選択された状態の設定用 */
		Integer selectedNodeID = null;       /* 最短距離をもつ状態 */
		int shortestDistance = -1;           /* 最短距離 */
		Object[] obj = null;

		/* StateListからゴールへの最短距離を持つ状態を取得 */ 
		ListIterator stateIterator
		        = stateList.listIterator();
		while(stateIterator.hasNext()) {
			Integer directAccessNodeID
			        = (Integer)stateIterator.next();
			/* ゴールとの距離を取得 */
			Node directAccessNode = getNode(directAccessNodeID);
			int distance = directAccessNode.getCognitiveDistance(
			        goalNodeID);
			/* ゴールへの最短距離と、その状態を保持 */
			if(distance != -1) {
				if((shortestDistance==-1)||(shortestDistance>=distance)) {
					selectedNodeID = directAccessNodeID;
					shortestDistance = distance;
				}
			}
		}

		if(selectedNodeID != null) {
			obj = new Object[2];
			obj[0] = selectedNodeID;
			obj[1] = new Integer(shortestDistance);
		}

		return obj;
	}

	/**
	 * StateListの配列の各要素から直接移動可能なノードIDをStateListの配列で
	 * 取得します。
	 * @param StateList[] stateListArray StateListの配列
	 * @return StateList[]   引数のStateListの配列の各要素から直接移動可能な
	 *                       StateListの配列
	 */
	private StateList[] getChildListArray(StateList[] stateListArray,
	        Hashtable checkTable) {

		/* 次の状態のリスト数をカウント */
		int nextStateCount = 0;
		for(int i = 0; i < stateListArray.length; i++) {
			nextStateCount += stateListArray[i].size();
		}

		/* 次の状態のリストを取得 */
		StateList[] nextStateList = new StateList[nextStateCount];
		int n = 0;
		for(int i = 0; i < stateListArray.length; i++) {
			ListIterator il = stateListArray[i].listIterator();
			while( il.hasNext() ) {
				Integer nodeID = (Integer)il.next();
				nextStateList[n] = getChildList(nodeID, stateListArray[i],
				        checkTable);
				n++;
			}
		}

		return nextStateList;
	}

	/**
	 * 指定された状態から直接移動可能な状態のリストを取得します。
	 * @param Integer parentNodeID 親となるノードID
	 * @param StateList stateList  親ノードのあるStateList
	 * @return StateList           直接移動可能なノードIDのリスト(子リスト)
	 */
	private StateList getChildList(Integer parentNodeID, StateList stateList, 
	        Hashtable checkTable) {

		/* 順方向に移動可能な状態を取得 */
		LinkedList forwardNodeIDList = getForwardNodeIDList(parentNodeID);
		/* すでに使用済みのノード以外を使用 */
		LinkedList checkedforwardNodeIDList = new LinkedList();
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer nodeID = (Integer)li.next();
			/* テーブルに登録されていない状態ならリスト、テーブルに登録 */
			if(!checkTable.contains(nodeID)) {
				checkedforwardNodeIDList.add(nodeID);
				checkTable.put(nodeID, nodeID);
			}
		}

		/* 新しいStateListを作成 */
		StateList childList = new StateList(checkedforwardNodeIDList,
		        parentNodeID, stateList);

		return childList;
	}


	/**
	 * すでに移動済みの状態への連続移動回数のカウンタを初期化します。
	 * 上位層のカウンタ・upperNextNodeID・novelSubgoalについても初期化
	 * を行ないます。
	 */
	public void resetUpperAndThisLayerFamiliarCount() {
		resetFamiliarCount();
		id_FVu0 = null;
		id_F0 = null;
		if(upperLayerAgent != null) {
			upperLayerAgent.resetUpperAndThisLayerFamiliarCount();
		}
	}

	/**
	 * すでに移動済みの状態への連続移動回数のカウンタを初期化します。
	 */
	private void resetFamiliarCount() {
		familiarCount = 0;
	}

	/**
	 * 引数のノードのＣＤ内でもっとも移動回数の少ない状態へ移動します。
	 * @param Integer nodeID 現在のノードID
	 * @return Integer       最も移動回数の少ないノードのID
	 */
	private Integer getNovelNodeID(Integer nodeID) {

		/* 最も移動回数の少ないノードID */
		Integer selectedNodeID = null;
		/* 最小の移動回数 */
		int minVisitCount = 0;

		/* ＣＤのキーとなっているノードのＩＤを取得 */
		Node node = getNode(nodeID);
		LinkedList ll = node.getCDKeys();
		/* 各ノードの移動回数をチェック */
		ListIterator li = ll.listIterator();
		while(li.hasNext()) {
			Integer cdNodeID = (Integer)li.next();
			Node cdNode = getNode(cdNodeID);
			int visitCount = cdNode.getVisitCount();
			/* 最も移動回数の少ない状態を保持 */
			if( (selectedNodeID == null) || (minVisitCount > visitCount) ) {
				selectedNodeID = cdNodeID;
				minVisitCount = visitCount;
			}
		}
		return selectedNodeID;
	}


	//////////////////////////////////////////////////////////////////////////
	// 学習

	/**
	 * CognitiveDistanceの学習を行います。
	 * @param Integer nodeID ノードID
	 */
	private void learning(Integer nodeID) {

		/* ノードの取得 */
		Node node = getNode(nodeID);

		/* バッファに現在のノードIDを追加 */
		stateBuffer.add(nodeID);

		/* 移動回数のカウント */
		int sbSize = stateBuffer.size();
		if( sbSize >= 2) {
			/*
			 * 移動先のノードについての情報も必要なため、一つ前のノード
			 * について処理を行なう
			 */
			Integer oldNodeID = (Integer)stateBuffer.get(sbSize - 2);
			Node oldNode = getNode(oldNodeID);
			/* 移動回数のカウントと移動先の設定 */
			oldNode.countVisitCount(nodeID);
		}

		/*
		 * stateBuffer内の各状態に、現在の状態をキーにその距離を登録
		 */
		int distance = 1;
		ListIterator stateBufferIterater
		        = stateBuffer.listIterator(stateBuffer.size());
		stateBufferIterater.previous();
		while(stateBufferIterater.hasPrevious()) {

			/* fromNodeの取得 */
			Integer fromNodeID = (Integer)stateBufferIterater.previous();
			Node fromNode = getNode(fromNodeID);

			/* ＣＤの学習 */
			fromNode.setCognitiveDistance(nodeID, distance);

			/*
			 * ノードから直接移動可能な状態についてもCognitiveDistanceを学習
			 */
			LinkedList forwardNodeIDList = getForwardNodeIDList(nodeID);
			ListIterator forwardNodeIDListIterater
			        = forwardNodeIDList.listIterator(0);
			while(forwardNodeIDListIterater.hasNext()) {
				Integer forwardNodeID
				        = (Integer)forwardNodeIDListIterater.next();
				fromNode.setCognitiveDistance(forwardNodeID, distance+1);
			}

			/* 距離が1のノードIDはMSMに登録 */
			if(distance == 1){
				/* 順方向に直接移動可能状態を学習 */
				fromNode.setForwardNode(nodeID);
				/* 逆方向に直接移動可能な状態を学習 */
				node.setInverseNode(fromNodeID);
			}

			distance++;

		}

		/* フラグによりランドマークの学習を制限 テスト用 */
		if(landmarkLearningFlag) {
			/* 上位層があればランドマークの学習 */
			if(upperLayerAgent != null) {
				/* Landmarkの学習 */
				landmarkLearning(nodeID);
			}
		}

	}


	/**
	 * Landmarkの学習を行います。
	 * @param Integer nodeID ノードID
	 */
	private void landmarkLearning(Integer nodeID) {

		/* 近くのランドマークを探索、そのIDとそこまでの距離を取得 */
		Object[] nearestLandmarkAndStep = getNearestLandmark(nodeID);
		Integer nearestLandmarkID = (Integer)nearestLandmarkAndStep[0];
		int shortestStep = ((Integer)nearestLandmarkAndStep[1]).intValue();

		/* 現在のランドマークのIDとそこまでのステップ数 */
		Integer currentLandmarkID = getUpperLayerNodeID(nodeID);
		int currentStep = getToLandmarkStep(nodeID);

		Node node = getNode(nodeID);
		if(nearestLandmarkID == null) {
			if(currentStep == -1) {
				/* 近くにランドマークがなく、自らにも設定されていない場合 */
				/* 自らをランドマークに設定 */
				Integer newID = new Integer(segmentCount);
				node.setUpperIDAndStep(newID, 0);
				/* 上位層のノードを作成 */
				upperLayerAgent.newNode(nodeID);
				segmentCount++;
			}
		}else{
			if(currentStep == -1) {
				/* 近くにランドマークがあり、自らに設定されていない場合 */
				/* 最短のランドマークを設定 */
				node.setUpperIDAndStep(nearestLandmarkID, shortestStep);
			}else if(currentStep == 0) {
				/* 近くにランドマークがあり、自らにも設定されている場合 */
				/* ランドマーク削除・近い方へ置き換え */
				if(shortestStep <= minSegmentSize) {
//					// 2000.11.30 修正 ランドマーク削除時に別のセグメントへの
//					// 統合を行なわない
//					/* ランドマーク(上位層)の削除処理 */
//					Integer upperID = getUpperLayerNodeID(nodeID);
//					// 置き換え
//					upperLayerAgent.deleteNode(upperID, nearestLandmarkID,
//					        shortestStep);
//					// 削除のみ
//					boolean b = upperLayerAgent.deleteNode(upperID, null, -1);
//					/*
//					 * 上位層の削除が正常に行なわれた場合は新たな最短の
//					 * ランドマークを設定
//					 */
//					if(b == true) {
//						node.setUpperIDAndStep(nearestLandmarkID,
//						        shortestStep);
//					}
				}
			}else{
				/*
				 * 近くにランドマークがあるが、現在は別のランドマークが設定
				 * されている場合
				 */
				/* 近い方へ置き換え */
				if(currentStep > shortestStep) {
					/* 最短のランドマークを設定 */
					node.setUpperIDAndStep(nearestLandmarkID,
					        shortestStep);
				}
			}
		}
	}

	/* ランドマークを設定せずに状態を保持しておくためのバッファ */
	LinkedList noLandmarkNodeIDBuffer = new LinkedList();
//
//	/**
//	 * Landmarkの学習を行います。(ForwardModel使用)
//	 * @param Integer nodeID ノードID
//	 */
//	private void landmarkLearning(Integer nodeID) {
//
//		/* 近くのランドマークを探索、そのIDとそこまでの距離を取得 */
//		Object[] nearestLandmarkAndStep = getNearestLandmark(nodeID);
//		Integer nearestLandmarkID = (Integer)nearestLandmarkAndStep[0];
//		int shortestStep = ((Integer)nearestLandmarkAndStep[1]).intValue();
//
//		/* 現在のランドマークのIDとそこまでのステップ数 */
//		Integer currentLandmarkID = getUpperLayerNodeID(nodeID);
//		int currentStep = getToLandmarkStep(nodeID);
//
//		/* 現在の状態がランドマーク */
//		if(currentStep == 0) {
//			/* バッファ内の状態に現在の状態をランドマークとして設定 */
//			setLandmarkToBuffer(nodeID, currentStep+1);
//		/* 現在の状態に何も設定されてない */
//		}else if(currentStep == -1) {
//			/* 近くにランドマーク有 */
//			if(nearestLandmarkID != null) {
//				/* バッファの状態 */
//				if(noLandmarkNodeIDBuffer.size() >= maxSegmentSize) {
//					/*
//					 * 現在の状態をランドマークにし、バッファ内の状態の
//					 * ランドマークとして設定
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* バッファサイズと距離のチェック */
//					if( shortestStep + noLandmarkNodeIDBuffer.size()
//					        > maxSegmentSize) {
//						/*
//						 * 現在の状態をランドマークにし、バッファ内の状態の
//						 * ランドマークとして設定
//						 */
//						makeLandmark(nodeID);
//						setLandmarkToBuffer(nodeID, 1);
//					}else {
//						/*
//						 * 現在の状態、バッファ内の状態に探索できた
//						 * ランドマークを設定
//						 */
//						Node node = getNode(nodeID);
//						node.setUpperIDAndStep(nearestLandmarkID,
//						        shortestStep);
//						setLandmarkToBuffer(nearestLandmarkID, shortestStep+1);
//					}
//				}
//			}else {
//				/* バッファの状態 */
//				if(noLandmarkNodeIDBuffer.size() >= maxSegmentSize) {
//					/*
//					 * 現在の状態をランドマークにし、バッファ内の状態の
//					 * ランドマークとして設定
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* 現在の状態をバッファに追加 */
//					noLandmarkNodeIDBuffer.add(nodeID);
//				}
//			}
//		/* 近くのランドマークが設定されている */
//		}else{
//			/* ランドマーク 有 */
//			if(nearestLandmarkID != null) {
//				/* 短い方のランドマークに置換え */
//				Integer renewLandmarkID = currentLandmarkID;
//				int renewStep = currentStep;
//				if(currentStep > shortestStep) {
//					Node node = getNode(nodeID);
//					node.setUpperIDAndStep(nearestLandmarkID,
//					        shortestStep);
//					renewLandmarkID = nearestLandmarkID;
//					renewStep = shortestStep;
//				}
//				/* バッファサイズと距離のチェック */
//				if( renewStep + noLandmarkNodeIDBuffer.size()
//				        > maxSegmentSize) {
//					/*
//					 * 現在の状態をランドマークにし、バッファ内の状態の
//					 * ランドマークとして設定
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* 更新後のランドマークをバッファ内の状態に設定 */
//					setLandmarkToBuffer(renewLandmarkID, renewStep);
//				}
//			}else {
//				/* バッファサイズと距離のチェック */
//				if( currentStep + noLandmarkNodeIDBuffer.size()
//				        > maxSegmentSize) {
//					/*
//					 * 現在の状態をランドマークにし、バッファ内の状態の
//					 * ランドマークとして設定
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* バッファ内の状態に現在の状態に設定されている */
//					/* ランドマークをランドマークとして設定         */
//					setLandmarkToBuffer(currentLandmarkID, currentStep);
//				}
//			}
//		}
//	}

	/**
	 * 自らの状態をランドマークにします。
	 * @param Integer nodeID ランドマークにする状態のID
	 */
//	private void makeLandmark(Integer nodeID) {
//		/* 自らをランドマークに設定 */
//		Integer newID = new Integer(segmentCount);
//		Node node = getNode(nodeID);
//		node.setUpperIDAndStep(newID, 0);
//		/* 上位層のノードを作成 */
//		upperLayerAgent.newNode(nodeID);
//		segmentCount++;
//	}

	/**
	 * ランドマークを未設定のバッファ内の状態にランドマークを設定します
	 * @param Integer landmarkID ランドマークのID
	 * @param int step           バッファの最新の状態からランドマークまでの
	 *                           ステップ数
	 */
	private void setLandmarkToBuffer(Integer landmarkID, int step) {
		/* リストの全状態にランドマークを設定 */
		ListIterator li = noLandmarkNodeIDBuffer.listIterator();
		while(li.hasPrevious()) {
			Integer nodeID = (Integer)li.previous();
			Node node = getNode(nodeID);
			node.setUpperIDAndStep(landmarkID, step);
			step++;
		}
		/* リストをクリア */
		noLandmarkNodeIDBuffer.clear();
	}

	// ここまで
	///////////////////////////


	/**
	 * maxSegmentSize内でもっとも近い位置のランドマークを探索します。
	 * @param Integer nodeID                ノードID
	 * @return Object[] nearestLandmarkInfo 最も近いランドマークに関する情報
	 *                                      Object[0] landmarkID
	 *                                      Object[1] stepNum
	 */
	private Object[] getNearestLandmark(Integer nodeID) {

		/* searchNodeIDListに同一の状態を再度選択しないためのテーブル */
		Hashtable checkTable = new Hashtable();

		/* 選択されたlandmarkの情報を設定 */
		Integer selectedNodeID = null;
		int selectedStep = -1;

		/* 探索範囲内のノードIDを取得 */
		LinkedList[] searchNodeIDList = new LinkedList[maxSegmentSize];

	// 2001.05.22 修正 miyamoto Modelの切り換えをフラグで行なう
//// 階層化の性能調査用 ForwardModel を使用
//		searchNodeIDList[0] = getInverseNodeIDList(nodeID);
////		searchNodeIDList[0] = getForwardNodeIDList(nodeID);
//		for(int i = 1; i < maxSegmentSize; i++) {
//// 階層化の性能調査用 ForwardModel を使用
//			searchNodeIDList[i] = getMoveableNodeList(searchNodeIDList[i-1],
//			        false, checkTable);
////			searchNodeIDList[i] =getMoveableNodeList(searchNodeIDList[i-1],
////			        true, checkTable);

		/* フラグによりランドマーク探索の向きを切り替え */
		if(!flagLandmarkSearchDirection) {
			/* InverseModelでのセグメント化 */
			searchNodeIDList[0] = getInverseNodeIDList(nodeID);
			for(int i = 1; i < maxSegmentSize; i++) {
				searchNodeIDList[i] = getMoveableNodeList(
				        searchNodeIDList[i-1], false, checkTable);
			}
		}else {
			/* ForwardModelでのセグメント化 */
			searchNodeIDList[0] = getForwardNodeIDList(nodeID);
			for(int i = 1; i < maxSegmentSize; i++) {
				searchNodeIDList[i] = getMoveableNodeList(
				        searchNodeIDList[i-1], true, checkTable);
			}
		}
		// ここまで

		/* 最短の状態を探索 */
		for(int i = 0; i < maxSegmentSize; i++) {
			/* リスト内の各状態についてランドマークまでの距離をチェック */
			ListIterator lIterator = searchNodeIDList[i].listIterator();
			while(lIterator.hasNext()) {

				/* ランドマークまでの距離を取得 */
				Integer searchNodeID = (Integer)lIterator.next();
				int toLandmarkStep = getToLandmarkStep(searchNodeID);
				/* ランドマークが設定済み */
				if( toLandmarkStep != -1 ) {
					/* 上位層が同じでないか */
					Integer upperNodeID = getUpperLayerNodeID(nodeID);
					Integer upperSearchNodeID
					        = getUpperLayerNodeID(searchNodeID);
					if( (upperNodeID == null) || (!upperNodeID.equals(
					        upperSearchNodeID)) ) {
						/* 探索の深さの分をステップ数に足す */
						toLandmarkStep = toLandmarkStep + i + 1;
						/* ランドマークまでのステップ数が探索範囲内 */
						if( toLandmarkStep <= maxSegmentSize ) {
							/* 最短の距離と比較 */
							if( (selectedStep == -1) || 
							        (selectedStep > toLandmarkStep) ) {
								/* 最短のランドマークの情報を設定 */
								selectedNodeID = upperSearchNodeID;
								selectedStep = toLandmarkStep;
							}
						}
					}
				}
			}
		}

		/* 最も近いランドマークの情報を設定 */
		Object[] nearestLandmarkInfo = new Object[2];
		nearestLandmarkInfo[0] = selectedNodeID;
		nearestLandmarkInfo[1] = new Integer(selectedStep);

		return nearestLandmarkInfo;
	}


	/**
	 * リスト内の各状態から移動可能な状態をリストで取得します。
	 * @param LinkedList nodeList  状態のリスト
	 * @param boolean direction    移動方向  true=forward  false=inverse
	 * @param Hashtable checkTable 同じ状態を複数設定しないためのテーブル
	 * @return LinkedList 引数のリストの各状態から移動可能な状態のリスト
	 */
	private LinkedList getMoveableNodeList(LinkedList nodeList,
	        boolean direction, Hashtable checkTable){

		/* 次に移動可能な状態のリスト設定用 */
		LinkedList nextNodeList = new LinkedList();

		/* 引数で取得したリストの各状態についての繰り返し */
		ListIterator nodeIterator = nodeList.listIterator();
		while(nodeIterator.hasNext()) {

			/* 各状態の取得 */
			Integer nodeID = (Integer)nodeIterator.next();

			/* 取得した状態からのMoveableStateListを取得 */
			LinkedList linkedList = null;
			if(direction) {
				linkedList = getForwardNodeIDList(nodeID);
			}else {
				linkedList = getInverseNodeIDList(nodeID);
			}

			ListIterator listIterator = linkedList.listIterator();
			while(listIterator.hasNext()) {
				/* 同一の状態は追加しない */
				Integer nextNodeID = (Integer)listIterator.next();
				if(!checkTable.containsKey(nextNodeID)) {
					nextNodeList.add(nextNodeID);
					checkTable.put(nextNodeID, nextNodeID);
				}
			}
		}

		return nextNodeList;
	}


	/**
	 * ゴール探索時の経路について学習し、ゴールへの最短の状態をもつ状態を取得
	 * ます。
	 * @param Object[] stateInfo 情報
	 * @param Integer goalNodeID ゴールのNodeID
	 * @return Integer           次に移動するノードのID
	 */
	private Integer pathLearning(Object[] stateInfo, Integer goalNodeID) {

		if(stateInfo == null) {
			return null;
		}

		/* 現在の状態に関しての情報 */
		Integer nodeID = (Integer)stateInfo[0];
		int distance = ((Integer)stateInfo[1]).intValue();
		StateList stateList = (StateList)stateInfo[2];

		/* 学習 */
		Node node = getNode(nodeID);
// 性能調査の場合にはゴール探索開始後の学習を行なわない用にするため
// パスの学習も行なわないようにする
		node.setCognitiveDistance(goalNodeID, distance);

		/* 親の情報を取得 */
		StateList parentList = stateList.getParentList();

		/*
		 * 親の状態があれば、その状態について学習・次の状態の取得、
		 * なければ自らの状態を返す。
		 */
		Integer parentNodeID = null;
		if(parentList != null) {
			Object[] parentObj = new Object[3];
			parentObj[0] = stateList.getParentNodeID();
			parentObj[1] = new Integer(distance+1);
			parentObj[2] = parentList;
			parentNodeID = pathLearning(parentObj, goalNodeID);
		}else {
			parentNodeID = nodeID;
		}
		return parentNodeID;
	}


	////////////////////////////////////////////////////////////////////
	// 学習状態の読込み・保存

	/**
	 * 学習データを読み込みます。
	 * @param ObjectInputStream oInputStream
	 */
	public void load(ObjectInputStream oInputStream) throws IOException,
	        ClassNotFoundException {
		nodes = (Vector)oInputStream.readObject();
		// 2001.06.07 追加 miyamoto
		/* セグメント数の読込み */
		segmentCount = ((Integer)oInputStream.readObject()).intValue();
	}

	/**
	 * 学習データを保存します。
	 * @param ObjectOutputStream oOutputStream
	 */
	public void save(ObjectOutputStream oOutputStream) throws IOException {
		oOutputStream.writeObject(nodes);
		// 2001.06.07 追加 miyamoto
		/* セグメント数の保存 */
		oOutputStream.writeObject(new Integer(segmentCount));
	}


	/////////////////////////////////////////////////////////////////////
	// テスト・実験用のメソッド

	/**
	 * 学習を行なうかどうかのフラグを設定します。
	 * @param boolean flag  true:学習を行なう  false:学習をしない
	 */
	public void setLearningFlag(boolean flag) {
		learningFlag = flag;
	}

	/**
	 * ランドマークの学習を行なうかどうかフラグを設定します。
	 * @param boolean flag   true:学習する  false:学習しない
	 */
	public void setSegmentationFlag(boolean flag) {
		landmarkLearningFlag = flag;
	}


	/**
	 * 第一引数で指定された状態に対応する、第二引数で指定された層でのノードを
	 * 取得します。
	 * 上位層でのidに変換し、ノードを取得します。
	 * @param Integer id 現在の層でのID
	 * @param int layer  取得するノードが現在の層より何層上の層か
	 * @return Node      ノード
	 */
	public Node getNode(Integer id, int layer) {
		Node node = null;
		if(layer == 0) { /* 自らのノードを取得 */
			node = getNode(id);
		}else{           /* 上位層のノードを取得 */
			if(upperLayerAgent == null) {
			}else{
				Integer upperID = getUpperLayerNodeID(id);
				node = upperLayerAgent.getNode(upperID, layer-1);
			}
		}
		return node;
	}


	/**
	 * CognitiveDistanceを学習するための移動した状態の履歴、一つ前のサイクル
	 * の状態を保持する変数をクリアします。
	 */
	public void reset() {
		/* StateBufferのクリア */
		stateBuffer.clear();

		/* 前回の状態の初期化 */
		id_Vu0 = null;
		id_Gu0 = null;

		// 2001.04.10 削除 miyamoto
		// テスト用 /* ゴール探索情報を設定するクラスを初期化 */
//		goalSearchInfo = new GoalSearchInfo();

		/* 上位層があれば、上位層のStateBufferもクリア */
		if(upperLayerAgent != null) {
			upperLayerAgent.reset();
		}
	}

	// 2001.04.05 追加 bsc miyamoto
	/**
	 * 保持している前サイクルの情報をクリアします。
	 */
	public void resetOldValue() {
		id_Vu0 = null;
		id_Gu0 = null;
		/* 上位層があれば、上位層の値もクリア */
		if(upperLayerAgent != null) {
			upperLayerAgent.resetOldValue();
		}
	}


	/**
	 * 上位層を削除します。
	 * 利用する最大の上位層を動的に変更するためのメソッド。
	 */
	public void deleteUpperLayer() {
		upperLayerAgent = null;
	}

	/**
	 * 実行時の情報について取得します。
	 * @return ExecInfo 実行時の情報
	 */
	public ExecInfo getExecInfo() {
		return execInfo;
	}

	// 2001.05.23 追加 miyamoto
	/**
	 * 実行時の情報をクリアします。
	 */
	public void resetExecInfo() {
		execInfo.paramReset();
	}

	/**
	 * ゴールの探索に関しての情報を取得します。
	 * @return GoalSearchInfo ゴール探索に関しての情報を設定したクラス
	 */
	public GoalSearchInfo getGoalSearchInfo() {
		return goalSearchInfo;
	}


	/**
	 * 学習状況に関しての情報を取得します。
	 * @return int[]   int[0]  MoveableStateのサイズ
	 *                 int[1]  CognitiveDsitanceのサイズ
	 *                 int[2]  ノード数
	 *                 int[3]  有効なノードの数
	 */
	public int[] getLearningInfo() {
		int msSize = 0;
		int cdSize = 0;
		int validNodeNum = 0;
		for(int i = 0; i < nodes.size(); i++) {
			// 2001.05.24 修正 miyamoto 古いバージョンのjavaに対応
//			Node node = (Node)nodes.get(i);
			Node node = (Node)nodes.elementAt(i);

			cdSize += node.getCDSize();
			msSize += node.getForwardNodeIDListSize();
			if(node.isValid()) {
				validNodeNum ++;
			}
		}
		int[] learningSize = new int[4];
		learningSize[0] = msSize;       /* MoveableStateのサイズ */
		learningSize[1] = cdSize;       /* CognitiveDsitanceのサイズ */
		learningSize[2] = nodes.size(); /* ノード数 */
		learningSize[3] = validNodeNum; /* 有効なノードの数 */
		return learningSize;
	}


}


