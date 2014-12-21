/**
 * Node.java
 * ノードの情報を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;

/**
 * ノードの情報を管理するクラスです。
 */
public class Node implements Serializable{

	private Integer ID;            /* 自らのID */
	private Integer upperID;       /* 上位層での状態 */
	private Integer lowerID;       /* 下位層での状態 */
	private int toLandmarkLngth;   /* ランドマークまでのステップ数 */

	private boolean valid;               /* この状態の有効性 */
	private Integer referenceNodeID;     /* このノードが無効な場合の参照先 */
	/* 参照先の下の層でのランドマークまでの距離 */
	private int referenceLowerToLandmarkLngth;

	/* ある状態までの距離を保持するテーブル Key=nodeID Element=距離 */
	private Hashtable cognitiveDistance;

	/* 直接移動可能な状態のリスト 移動可能なnodeIDのリスト*/
	private LinkedList forwardNodeIDList;  /* 順方向 */
	private LinkedList inverseNodeIDList;  /* 逆方向 */

	private int visitCount;              /* この状態への移動回数 */
	/* 状態遷移に関する情報のリスト */
	private Hashtable transitionCounterTable;

	/**
	 * 学習可能な最大の距離
	 * この長さ＋１がStateBufferのサイズ
	 */
	public static int maxCDLngth = 10;

	///////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param Integer ID      このノードのID
	 * @param Integer lowerID 下位層でのID
	 */
	public Node(Integer ID, Integer lowerID) {

		/* 値の初期化 */
		this.ID = ID;
		this.lowerID = lowerID;
		valid = true;
		toLandmarkLngth = -1;

//		cognitiveDistance = new Hashtable();
		cognitiveDistance = new Hashtable(1000); // 失敗
		forwardNodeIDList = new LinkedList();
		inverseNodeIDList = new LinkedList();
		transitionCounterTable = new Hashtable();
	}


	///////////////////////////////////////////////////////////
	// 情報の取得

	/**
	 * このノードのIDを取得します。
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * このノードの上位層でのIDを取得します。
	 * @param Integer   上位層でのID
	 */
	public Integer getUpperID() {
		return upperID;
	}

	/**
	 * このノードの下位層でのIDを取得します。
	 * @param Integer   下位層でのID
	 */
	public Integer getLowerID() {
		return lowerID;
	}

	/**
	 * ランドマークまでのステップ数を取得します。
	 * @return int ステップ数
	 */
	public int getToLandmarkStep() {
		return toLandmarkLngth;
	}


	/**
	 * 引数で設定されたノードまでの距離を取得します。
	 *	@param Integer nodeID 距離を取得するノードのID
	 *	@return int           ノードまでの距離(ステップ数)
	 *                        引数で設定されたノードへの距離が学習されていない
	 *                        場合は-1
	 *                        このノードと同じなら0を返す。
	 */
	public int getCognitiveDistance(Integer nodeID) {

		/* ２つの状態が同じなら距離を0にする */
		if(getID().equals(nodeID)) {
			return 0;
		}

		/* 対応する値があればその値を、なければ-1を返す */
		Integer distanceObj = (Integer)cognitiveDistance.get(nodeID);
		int distance = -1;
		if(distanceObj != null){
			distance = distanceObj.intValue();
		}

		/*
		 * maxCDLngthを動的に変化させた場合に、すでに学習済みのCDのから
		 * 変化させた後のmaxCDLngthより長いCDは使用しないようにするための処理
		 */
// 2001.09.07 ドアキーのデモ用に仮にコメントアウト
//		if(distance > maxCDLngth) {
//			distance = -1;
//		}

		return distance;
	}

	/**
	 * 順方向に直接移動可能なノードのIDのリストを取得します。
	 * @return LinkedList 直接移動可能なノードのIDのリスト
	 */
	public LinkedList getForwardNodeIDList() {
		return forwardNodeIDList;
	}

	/**
	 * 逆方向に直接移動可能なノードのIDのリストを取得します。
	 * @return LinkedList 直接移動可能なノードのIDのリスト
	 */
	public LinkedList getInverseNodeIDList() {
		return inverseNodeIDList;
	}

	/**
	 * このノードが有効かどうかをチェックします。
	 * @param boolean  true 有効  false 無効
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * 参照先のノードIDを取得します。
	 * @return Integer 参照先のノードID
	 */
	public Integer getReferenceNodeID() {
		return referenceNodeID;
	}

	/**
	 * 参照先のノードのランドマークまでのステップ数を取得します。
	 * @return int 参照先のノードのランドマークまでのステップ数
	 */
	public int getRefarenceStep() {
		return referenceLowerToLandmarkLngth;
	}


	/**
	 * このノードから距離の分かる(学習してある)ノードのIDをリストで取得します。
	 * @return LinkedList 距離の分かるノードのIDのリスト
	 */
	public LinkedList getCDKeys() {
// 別でLinkedListを持っていた方がよいか
		LinkedList ll = new LinkedList();
		/* ハッシュから全キーを取得 */
		Enumeration e = cognitiveDistance.keys();
		while(e.hasMoreElements()) {
			ll.add(e.nextElement());
		}
		return ll;
	} 

	/**
	 * このノードへの移動回数を取得します。
	 * @return int  移動回数
	 */
	public int getVisitCount() {
		return visitCount;
	}

	///////////////////////////////////////////////////////////
	// 情報の設定

	/**
	 * このノードの上位層での状態に関する情報を設定します。
	 * @param Integer upperID 上位層でのID
	 * @param int toUpperStep ランドマークまでのステップ数
	 */
	public void setUpperIDAndStep(Integer upperID, int toLandmarkLngth) {
		this.upperID = upperID;
		this.toLandmarkLngth = toLandmarkLngth;
	}


	/**
	 * 引数で設定されたノードまでの距離を学習します。
	 * @param Integer nodeID 距離を学習するノードのID
	 * @param int distance   ノードまでの距離
	 */
	public void setCognitiveDistance(Integer nodeID, int distance) {

// 探索時の長い距離も学習?
// 2001.09.07 ドアキーのデモ用に仮にコメントアウト
//		/* 学習するCognitiveDistanceの最大距離 */
//		if(distance > maxCDLngth) {
//			return;
//		}

		/* 同じ状態なら距離を０に設定 */ 
// 同じ状態でも距離をそのまま学習させる。同じ状態なら距離の取得時に０を返す。
//		if(getID().equals(nodeID)) {
//			cognitiveDistance.put(nodeID, new Integer(0));
//		}else{
			/* キーに対応する値がないか、値が小さい場合新しい距離を設定 */
			Integer distanceObj = (Integer)cognitiveDistance.get(nodeID);
			if((distanceObj==null) || (distanceObj.intValue()>distance)) {
				cognitiveDistance.put(nodeID, new Integer(distance));
			}
//		}
	}


	/**
	 * 順方向に直接移動可能なノードを学習します。
	 * @param Integer nodeID 直接移動可能なノードのID
	 */
	public void setForwardNode(Integer nodeID) {

		/* リストを探索し、すでに登録済みならリストに追加しない */
		ListIterator lIterater = forwardNodeIDList.listIterator(0);
		while(lIterater.hasNext()) {
			if(((Integer)lIterater.next()).equals(nodeID)) {
				return;
			}
		}

		/* 値をリストに追加 */
		forwardNodeIDList.add(nodeID);

	}


	/**
	 * 逆方向に直接移動可能なノードを学習します。
	 * @param Integer nodeID 直接移動可能なノードのID
	 */
	public void setInverseNode(Integer nodeID) {

		/* リストを検索し、すでに登録済みならリストに追加しない */
		ListIterator lIterater = inverseNodeIDList.listIterator(0);
		while(lIterater.hasNext()) {
			if(((Integer)lIterater.next()).equals(nodeID)) {
				return;
			}
		}

		/* 値をリストに追加 */
		inverseNodeIDList.add(nodeID);

	}


	/**
	 * このノードへの移動回数をカウントします。
	 * @param Integer nextNodeID このノードから移動した先のノードのID
	 */
	public void countVisitCount(Integer nextNodeID) {

		visitCount++;
		/* このノードから移動した先の状態の管理 */
		TransitionCounter tc
		        = (TransitionCounter)transitionCounterTable.get(nextNodeID);
		if(tc == null) {
			tc = new TransitionCounter(nextNodeID);
			transitionCounterTable.put(nextNodeID, tc);
		}else {
			/* 回数のカウント */
			tc.count();
		}
	}


	/**
	 * このノードを無効にします。
	 * このノードが上位層のランドマークの場合は無効にしません。
	 * @param Integer refID 今後の参照先のID
	 * @param int refStep   参照先のノードまでの距離
	 * @return boolean      true 無効にした場合 false 無効にできなかった場合
	 */
	public boolean delete(Integer referenceNodeID,
	         int referenceLowerToLandmarkLngth) {
		/* 自らが上位層へのランドマークの場合は削除しない */
		if(toLandmarkLngth == 0) {
			return false;
		}

		valid = false;
		/* 参照先の設定 */
		this.referenceNodeID = referenceNodeID;
		this.referenceLowerToLandmarkLngth = referenceLowerToLandmarkLngth;
		return true;
	}


	/**
	 * 順方向に移動可能なノードのリスト内にこのノードと同じノードがある場合に
	 * そのノードをリストから削除します。
	 */
	public void removeSameForwardNodeID() {
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			if(id.equals(ID)) {
				li.remove();
			}
		}
	}

	/**
	 * 逆方向に移動可能なノードのリスト内にこのノードと同じノードがある場合に
	 * そのノードをリストから削除します。
	 */
	public void removeSameInverseNodeID() {
		ListIterator li = inverseNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			if(id.equals(ID)) {
				li.remove();
			}
		}
	}


	/////////////////////////
	// テスト用の情報取得

	/**
	 * 距離を学習してあるノード数を取得します。
	 * @return int 距離を学習してあるノード数
	 */
	public int getCDSize() {

//		return cognitiveDistance.size();

		/*
		 * maxCDLngthを動的に変化させた場合に、変化させた後のmaxCDLngth
		 * で使用可能なCognitiveDistanceのサイズを取得します。
		 */
		int validCDSize = 0;
		Enumeration e = cognitiveDistance.elements();
		while(e.hasMoreElements()) {
			Integer distance = (Integer)e.nextElement();
			if(distance.intValue() <= maxCDLngth) {
				validCDSize++;
			}
		}

		return validCDSize;
	}

	/**
	 * 順方向に直接移動可能なノード数を取得します。
	 * @return int 直接移動可能なノード数
	 */
	public int getForwardNodeIDListSize() {
		return forwardNodeIDList.size();
	}


}
