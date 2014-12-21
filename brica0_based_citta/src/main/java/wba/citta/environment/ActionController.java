/**
 * ActionController.java
 * 地図情報を解析して行動について管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *    BSC miyamoto 2001.05
 */
package wba.citta.environment;

import java.util.*;

/**
 * 地図情報を解析して行動について管理するクラスです。
 */
public class ActionController {

	/* ファイルの情報の配列 */
	private String[][] mapArray;

	/**
	 * コンストラクタ
	 * @return String[][] mapArray
	 */
	public ActionController(String[][] mapArray) {
		this.mapArray = mapArray;
	}


	//////////////////////////////////////////////////////////////////
	// public

	/**
	 * 指定された位置・Actionからの移動先の位置を取得します。
	 * @param int x x座標
	 * @param int y y座標
	 * @param int action 移動方向
	 * @return int[] 移動先の座標
	 */
	public int[] move(int x, int y, int action) {

		/* ファイルから対応する位置の情報を取得 */
		String mapData = mapArray[x][y];

		int[] elem = null;

		if(mapData.length() != 0) { /* 情報が設定されている */
			String mapID = mapData.substring(0, 1);
			if( mapID.equals("S") ) {
				/* スタート */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("O") ) {
				/* 負の報酬 */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("d") ) {
				/* 開いているドア */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("K") ) {
				/* カギ */
				// 2001.03.26 追加 miyamoto
//				key = true;
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("T") ) {
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("J") ) {
				/* ジャンプ */
				elem = jampMove(x, y, action, mapData);
			}else if( mapID.equals("F") ) {
				/* 流れ */
				elem = flowMove(x, y, action, mapData);
			}else if( mapID.equals("C") ) {
				/* 崖 */
				elem = cliffMove(x, y, action, mapData);
			}else if( mapID.equals("R") ) {
				elem = randomMove(x, y, action, mapData);
			}else {
				// 2001.07.13 その他は通常の動作
				elem = nomalMove(x, y, action);
			}
		}else {                                 /* 空白 */
			elem = nomalMove(x, y, action);
		}

		/* 壁のチェック */
		if(!checkState(elem[0], elem[1])) {
			elem[0] = x;
			elem[1] = y;
		}

		return elem;
	}

	//////////////////////////////////////////////////////////////////
	// private

	/**
	 * 通常の動作を設定します。
	 * @param int x      ｘ座標
	 * @param int y      ｙ座標
	 * @param int action 行動
	 * @return int[]     移動先の座標
	 */
	private int[] nomalMove(int x, int y, int action) {
		/* 要素の取得 */
		int[] elem = getNextState(x, y, action);
		return elem;
	}


	/**
	 * ジャンプの行動をします。
	 * @param int x           ｘ座標
	 * @param int y           ｙ座標
	 * @param int action      行動
	 * @param String mapDate  地図情報
	 * @return int[]          移動先の座標
	 */
	private int[] jampMove(int x, int y, int action, String mapData) {

		/* ジャンプする先の情報を取得 */
		int startIndex = mapData.indexOf( "(" );
		int endIndex = mapData.indexOf( ")" );
		/* ()の中を取得 */
		String subStr = mapData.substring(startIndex+1, endIndex);
		StringTokenizer st = new StringTokenizer(subStr, ":");

		int toStateX = Integer.parseInt(st.nextToken());
		int toStateY = Integer.parseInt(st.nextToken());
		int jampAction = Integer.parseInt(st.nextToken());

		int[] elem = new int[2];
		if(action == jampAction) { /* ジャンプのアクション */
			elem[0] = toStateX;
			elem[1] = toStateY;
		}else {                    /* 通常の行動をするアクション */
			elem = getNextState(x, y, action);
		}

		return elem;

	}


	/**
	 * 流れのある場所の移動先を設定します。
	 * @param int x           ｘ座標
	 * @param int y           ｙ座標
	 * @param int action      行動
	 * @param String fileDate ファイルからの情報
	 * @return int[]          移動先の座標
	 */
	private int[] flowMove(int x, int y, int action, String mapData) {

		/* 流れの方向についての情報を取得 */
		int startIndex = mapData.indexOf( "(" );
		int endIndex = mapData.indexOf( ")" );
		String flow = mapData.substring(startIndex + 1, endIndex);

		/* 通常の移動先を取得 */
		int[] elem = getNextState(x, y, action);

		if(checkState(elem[0], elem[1])) {
			/* 流れを移動先の状態に追加 */
			if(flow.equals("U")) {
				elem[1] --; 
			}
			if(flow.equals("D")) {
				elem[1] ++; 
			}
			if(flow.equals("L")) {
				elem[0] --; 
			}
			if(flow.equals("R")) {
				elem[0] ++; 
			}
		}else {
			elem[0] = x;
			elem[1] = y;
		}

		return elem;
	}


	/**
	 * 崖になっている場所の移動先を設定します。
	 * @param int x           ｘ座標
	 * @param int y           ｙ座標
	 * @param int action      行動
	 * @param String fileDate ファイルからの情報
	 * @return int[]          移動先の座標
	 */
	private int[] cliffMove(int x, int y, int action, String mapData) {

		int index = mapData.indexOf( "(" );
		String savedMove = mapData.substring(index + 1, index + 2);

		int[] elem = new int[2];

		int saveAction = -1;
		if(savedMove.equals("U")) {
			saveAction = 0; 
		}
		if(savedMove.equals("D")) {
			saveAction = 4; 
		}
		if(savedMove.equals("L")) {
			saveAction = 2; 
		}
		if(savedMove.equals("R")) {
			saveAction = 6; 
		}

		if(action == saveAction) { /* 移動不可の方向への移動 */
			elem[0] = x;
			elem[1] = y;
		}else {                   /* その他の方向への移動 */
			elem = getNextState(x, y, action);
		}

		return elem;
	}

	// 2001.07.06 追加 miyamoto
	private Random randomMove = new Random(0);
	private int[] randomMove(int x, int y, int action, String mapData) {
		// 仮に４方向に限定
		int randomAction = randomMove.nextInt(4)*2;
		/* 要素の取得 */
		int[] elem = getNextState(x, y, randomAction);
		return elem;
	}
	// ここまで

	/**
	 * ｘ,ｙ座標の状態からactionを行なった場合の状態を取得します。
	 * @param int x      x座標
	 * @param int y      y座標
	 * @param int action 行動
	 * @return int[]     移動先の状態
	 */
	private int[] getNextState(int x, int y, int action) {
		int[] elem = new int[2];

		/* 停止 */
		if(action == -1) {
			elem[0] = x;
			elem[1] = y;
		}
		/* 上 */
		if(action == 0) {
			elem[0] = x;
			elem[1] = y-1;
		}
		/* 左上 */
		if(action == 1) {
			elem[0] = x-1;
			elem[1] = y-1;
		}
		/* 左 */
		if(action == 2) {
			elem[0] = x-1;
			elem[1] = y;
		}
		/* 左下 */
		if(action == 3) {
			elem[0] = x-1;
			elem[1] = y+1;
		}
		/* 下 */
		if(action == 4) {
			elem[0] = x;
			elem[1] = y+1;
		}
		/* 右下 */
		if(action == 5) {
			elem[0] = x+1;
			elem[1] = y+1;
		}
		/* 右 */
		if(action == 6) {
			elem[0] = x+1;
			elem[1] = y;
		}
		/* 右上 */
		if(action == 7) {
			elem[0] = x+1;
			elem[1] = y-1;
		}

		return elem;
	}

	private boolean collisionDoor = false;
	public boolean isCollisionDoor() {
		return collisionDoor;
	}

	/**
	 * 指定された位置が移動可能かチェックします。
	 * @param int x ｘ座標
	 * @param int y ｙ座標
	 * @param boolean  true 移動可能  false 移動不可能
	 */
	private boolean checkState(int x, int y) {

		collisionDoor = false;

		/* 地図の範囲内 */
		if( (x >= 0) && (x < mapArray.length) && (y >= 0) &&
		        (y < mapArray[0].length) ) {

			/*  壁 または 閉じているドア でない */ 
			if(!mapArray[x][y].equals("W") && !mapArray[x][y].equals("D")) {
				return true;
			}
			if(mapArray[x][y].equals("D")) {
				collisionDoor = true;
			}

		}
		return false;
	}


}
