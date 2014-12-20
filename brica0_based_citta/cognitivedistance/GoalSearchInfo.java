/**
 * GoalSearchInfo.java
 * ゴール探索時の情報を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.12 BSC miyamoto
 */
package cognitivedistance;

import java.util.*;

/**
 * ゴール探索時の情報を管理するクラスです。
 */
public class GoalSearchInfo {

	/* 各処理ごとの探索情報を保持する配列 */
	GoalSearchInfoSingle[] gsis;

	/**
	 * コンストラクタ
	 */
	public GoalSearchInfo() {
		/* 各処理ごとの探索情報を保持するクラスを生成 */
		gsis = new GoalSearchInfoSingle[4];
		for(int i = 0; i < 4; i++) {
			gsis[i] = new GoalSearchInfoSingle();
		}
	}

	/**
	 * 設定されている情報をクリアします。
	 */
	public void clear() {
		/* 各処理を行なったかどうかのフラグをfalseに設定 */
		for(int i = 0; i < 4 ; i++) {
			gsis[i].flagUsed = false;
		}
	}

	/**
	 * ゴール探索時の情報を設定します。
	 * 
	 */
	public void setGoalSearchInfo(int dx, Integer nodeID, Integer cdLngth,
	        int searchLngth, int searchStateNum) {
		/* 使用されたフラグを立て、各値を設定 */
		gsis[dx].flagUsed = true;
		gsis[dx].searchedNodeID = nodeID;
		gsis[dx].cdLngth = cdLngth;
		gsis[dx].searchLngth = searchLngth;
		gsis[dx].searchStateNum = searchStateNum;
	}


	/**
	 * 指定された処理(D1～D4)の探索時の情報を取得します。
	 * @param int dx  探索時の情報を取得する処理(0から3で指定)
	 * @return int[]  探索情報
	 *                指定された処理が行なわれていない場合はnullを返す。
	 *                int[0] 探索されたノードのID
	 *                       探索の結果見つからなかった場合-1
	 *                int[1] 探索されたノードからゴールまでのCDの長さ
	 *                       探索の結果見つからなかった場合-1
	 *                int[2] 探索れた深さ
	 *                int[3] 探索された状態数
	 */
	public int[] getGoalSearchInfo(int dx) {
		/* 指定された処理がD1～D4でない、またはその処理が行なわれていない */
		if( (dx > 3) || (dx < 0) || (gsis[dx].flagUsed == false ) ) {
			return null;
		}

		/* 戻り値として返す変数に設定 */
		int[] gsi = new int[4];
		if(gsis[dx].searchedNodeID != null) {
			gsi[0] = gsis[dx].searchedNodeID.intValue();
		}else {
			gsi[0] = -1;
		}
		if(gsis[dx].cdLngth != null) {
			gsi[1] = gsis[dx].cdLngth.intValue();
		}else {
			gsi[1] = -1;
		}
		gsi[2] = gsis[dx].searchLngth;
		gsi[3] = gsis[dx].searchStateNum;

		return gsi;
	}

	//////////////////////////////////////////////////////////////
	// 各処理ごとのゴール探索に関する情報を保持するインナークラス

	/**
	 * 各処理ごとのゴール探索に関する情報を保持するインナークラス
	 */
	class GoalSearchInfoSingle {

		/* この処理が行なわれたか示すフラグ */
		boolean flagUsed = false;
		/* 探索されたゴール到達可能なノードのID */
		Integer searchedNodeID;
		/* ゴールまでのCDの長さ */
		Integer cdLngth;
		/* 探索した深さ */
		int searchLngth;
		/* 探索した状態の数 */
		int searchStateNum;
	}

}


