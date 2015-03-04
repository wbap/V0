/**
 * 環境の描画、操作を行なうクラス
 * EnvironmentPanel.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.05 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;

/**
 * 環境の描画、操作を行なうクラスです。
 */
public class EnvironmentPanel extends Panel {

	/* 環境の描画を行なうクラス */
	private EnvironmentCanvas canvas;

	/* 地図情報から行動を管理するクラス */
	private ActionController actionController;

	/* 地図情報を管理するクラス */
	private MapController mapController;

	/* ロボットの位置 */
	private int[] robotState;

	/* 地図に対応した報酬のテーブル */
	private String[][] rewardMap;

	String[][] colorMap = null;

	/* カギを保持しているかどうか */
//	private boolean key = false;
	/* 持ち物 */
	public final int NOTHING = 0;
	public final int KEY = 1;
	public final int TELEPHON = 2;
public final int A = 3;
public final int B = 4;
public final int a = 5;
public final int b = 6;


	/* 環境の描画を行なうフラグ */
	private boolean isShow = true;

	/* クリック時に設定する状況 ""=空白 "W"=壁 "O"=報酬 "n"=設定しない */
	private String renewValue = "n";

	/* ゴールを複数設定可能にするか */
	private boolean flagGoals;

	///////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param String fileName Mapファイル名
	 */
	public EnvironmentPanel(String fileName) {
		/* 報酬の表示あり、ゴールは1つのみ */
		this(fileName, true, false);
	}

	/**
	 * コンストラクタ 報酬の表示についての設定可
	 * @param String fileName Mapファイル名
	 * @param boolean isShowReward 報酬を表示するかどうか
	 * @param boolean flagGoals    ゴールを複数設定可能にするか
	 *                             true:複数可能  false:複数不可 以前のを削除
	 */
	public EnvironmentPanel(String fileName, boolean isShowReward,
	        boolean flagGoals) {
		/* 地図を管理するクラスの生成 */
		mapController = new MapController(fileName);

		/* 地図情報から行動を管理するクラスの生成 */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ロボットの位置初期化 */
		initRobotPos();

		/* 描画部分の初期化 */
		initCanvas(isShowReward);

	}

	public EnvironmentPanel(String mapFileName, String colorMapFileName,
	        boolean isShowReward, boolean flagGoals) {
		/* 地図を管理するクラスの生成 */
		mapController = new MapController(mapFileName);

		MapFileToArray mapFileToArray = new MapFileToArray(colorMapFileName);
		colorMap = mapFileToArray.getFileArray();

		/* 地図情報から行動を管理するクラスの生成 */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ロボットの位置初期化 */
		initRobotPos();

		/* 描画部分の初期化 */
		initCanvas(isShowReward);
	}


	/**
	 * コンストラクタ 報酬の表示についての設定可
	 * @param String[][] Stringの配列での地図情報
	 * @param boolean isShowReward 報酬を表示するかどうか
	 * @param boolean flagGoals    ゴールを複数設定可能にするか
	 *                             true:複数可能  false:複数不可 以前のを削除
	 */
	public EnvironmentPanel(String[][] map, boolean isShowReward,
	        boolean flagGoals) {
		/* 地図を管理するクラスの生成 */
		mapController = new MapController(map);

		/* 地図情報から行動を管理するクラスの生成 */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ロボットの位置初期化 */
		initRobotPos();

		/* 描画部分の初期化 */
		initCanvas(isShowReward);
	}

	////////////////////////////////////////////////////////////
	// 初期化処理

	/**
	 * ロボットの位置の初期化
	 */
	public void initRobotPos() {
		int[] pos = mapController.getPos("S");
		robotState[0] = pos[0];
		robotState[1] = pos[1];
	}

	/**
	 * 描画処理部分の初期化
	 * @param boolean 報酬の表示を行なうかどうか
	 */
	private void initCanvas(boolean b) {
		canvas = new EnvironmentCanvas();
		canvas.addMouseListener(new CanvasMouseAdapter());
		/* 報酬の表示を行なう場合は報酬用のテーブルを初期化 */
		if(b) {
			int[] size = mapController.getSize();
			rewardMap = new String[size[0]][size[1]];
			/* 報酬の更新 */
			renewReward();
		}
//		canvas.initCanvas(mapController.getMap(), robotState, rewardMap);
		canvas.initCanvas(mapController.getMap(), robotState, rewardMap,
		        colorMap);
		setLayout(new BorderLayout());
		add(canvas);
	}

	////////////////////////////////////////////////////////////
	// public

	/**
	 * ロボットを指定された方向へ移動します。
	 * @param int action  移動方向 ０〜７で設定
	 * @return boolean    true=移動成功 false=移動失敗
	 */
	public boolean run(int action) {

		int[] newState = actionController.move(robotState[0], robotState[1],
		        action);

		/* 実際に移動できたかチェックし 移動していれば状態を変化 */
		boolean isMove = true;
		if( (robotState[0] == newState[0]) && (robotState[1]==newState[1]) ) {
			isMove = false;
		}else{
			robotState[0] = newState[0];
			robotState[1] = newState[1];
			/* カギの位置かチェック */
//			checkKey(robotState[0], robotState[1]);
			// 2001.08.08 修正 miyamoto
			checkItem(robotState[0], robotState[1]);

			/* フラグにより描画処理を行なう */
			if(isShow) {
				canvas.repaint();
			}
		}
		return isMove;
	}


	/**
	 * ロボットを指定された方向へ移動します。
	 * @param int[] xy    移動先の座標
	 * @param int action  移動方向 ０〜７で設定
	 * @return boolean    true=引数で設定された座標に、引数で設定されたAction
	 *                         で移動成功
	 *                    false=引数で設定された座標に、引数で設定されたAction
	 *                         で移動失敗
	 */
	public boolean run(int[] xy, int action) {

		int[] newState = actionController.move(robotState[0], robotState[1],
		        action);

		/*
		 * 引数で指定された位置に実際に移動できるのかチェックし移動できれば
		 * 状態を変化
		 */
		boolean isMove = false;

		if( (xy[0] == newState[0]) && (xy[1]==newState[1]) ) {
			robotState[0] = newState[0];
			robotState[1] = newState[1];
			/* カギの位置かチェック */
//			checkKey(robotState[0], robotState[1]);
			// 2001.08.08 修正 miyamoto
			checkItem(robotState[0], robotState[1]);

			/* フラグにより描画処理を行なう */
			if(isShow) {
				canvas.repaint();
			}
			isMove = true;
		}

		return isMove;
	}


	/**
	 * 指定された座標をスタート位置にします。
	 * @param int x X座標
	 * @param int y Y座標
	 */
	public void setStart(int x, int y) {
		/* 前のスタートを消す */
		int[] pos = mapController.getPos("S");
		mapController.set(pos[0], pos[1], "");
		/* 新しいスタートを設定 */
		mapController.set(x, y, "S");
	}

	// 2001.09.05 追加 miyamoto
	public void setItem(int newItem) {
		robotState[2] = newItem;
		controlDoor(newItem);
	}

	/**
	 * 指定された座標にゴールを設定します。
	 * @param int x X座標
	 * @param int y Y座標
	 */
	public void setGoal(int x, int y) {

		/* ゴールが複数設定不可なら以前のゴールを削除 */
		if(!flagGoals) {
			int[] goalState = getXYGoalState();
			if(goalState != null) {
				mapController.set(goalState[0], goalState[1], "");
			}
		}
		mapController.set(x, y, "O(1)");

		/* 報酬の更新 */
		renewReward();
	}

	/**
	 * ゴールをクリアします。
	 */
	public void clearGoal() {
		int[] goalState = getXYGoalState();
		if(goalState != null) {
			mapController.set(goalState[0], goalState[1], "");
		}
	}

	/**
	 * 環境の描画領域をクリックして環境を変更するときの、変更する情報を
	 * 設定します
	 * @param String str  環境に設定する文字列
	 */
	public void setRenewValue(String str) {
		renewValue = str;
	}

	/**
	 * 描画を行なうかのフラグを設定します。
	 * @param boolean b  true:描画  false:描画なし
	 */
	public void setFlagShow(boolean b) {
		isShow = b;
	}


	/**
	 * ロボットの位置をセンサ情報で取得します。
	 * @return int[8] 
	 */
	public int[] getSenserState() {
		return mapController.getState(robotState[0], robotState[1]);
	}


	private int[] robotPos = new int[2];
	/**
	 * ロボットの位置をＸＹ座標で取得します。
	 * @return int[] 現在の座標
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYState() {
//		return robotState;
		robotPos[0] = robotState[0];
		robotPos[1] = robotState[1];
		return robotPos;
	}

	/**
	 * ゴールの位置をセンサ情報で取得します。
	 * @return int[] ゴールの位置
	 */
	public int[] getSensorGoalState() {
		int[] goalState = getXYGoalState();
		if(goalState == null) {
			return null;
		}
		return mapController.getState(goalState[0], goalState[1]);
	}

	/**
	 * ゴールの位置をＸＹ座標で取得します。
	 * @return int[] ゴールの座標
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYGoalState() {
		int[] goalState = mapController.getPos("O");
		return goalState;
	}

	/**
	 * キーの位置をＸＹ座標で取得します。
	 * @return int[] キーの位置
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYKeyState() {
		int[] keyState = mapController.getPos("K");
		return keyState;
	}

	/**
	 * スタートの位置をＸＹ座標で取得します。
	 * @return int[] スタートの位置
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYStartState() {
		int[] startState = mapController.getPos("S");
		return startState;
	}

	/**
	 * 報酬を取得します。
	 * @return double 報酬
	 */
	public double getReward() {
		String rewardStr = mapController.getReward(robotState[0],
		        robotState[1]);
		int reward = 0;
		if(!rewardStr.equals("")) {
			reward = Integer.parseInt(rewardStr);
		}
		return reward;
	}

	/**
	 * カギを取得しているか確認します。
	 * @param boolean  true:カギを持っている false:カギを持っていない
	 */
//	public boolean hasKey() {
//		return key;
//	}
	// 2001.08.08 追加 miyamoto
	/**
	 * 保持しているものを取得
	 * @return 
	 */
	public int getItem() {
//		return item;
		return robotState[2];
	}

	/**
	 * カギをなくします。
	 */
//	public void clearKey() {
//		key = false;
//	}
	public void clearItem() {
//		item = NOTHING;
		robotState[2] = NOTHING;
	}

	/**
	 * ドアが開いているか確認します。
	 * @param boolean  true:ドアが開いている false:ドアが閉じている
	 */
	public boolean isDoorOpen() {
		// 2001.08.08 修正 miyamoto
//		return key;
//		if(item == KEY ) {
		if(robotState[2] == KEY ) {
			return true;
		}
		return false;
	}

	// 2001.08.03 追加 miyamoto
	/**
	 * 開いているドアを閉じる
	 */
	public void closeDoor() {
//	private void closeDoor() {
		/*
		 * 地図上の全位置をチェック
		 * 閉じているドア("D")があれば開いているドア("d")に換える
		 */
		int[] size = mapController.getSize();
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]; y++) {
				if( (mapController.getString(x, y)).equals("d") ) {
					mapController.set(x, y, "D");
				}
			}
		}
	}

	/**
	 * 地図上の指定された位置の情報を取得します。
	 * @param int x  x座標
	 * @param int y  y座標
	 */
	public String getMapInfo(int x, int y) {
		return mapController.getString(x, y);
	}

	public String getColorInfo(int x, int y) {
		if(colorMap == null) {
			return null;
		}
		return colorMap[x][y];
	}

	/**
	 * 地図のサイズを取得します。
	 * @return int[] int[0] x軸方向のサイズ
	 *               int[0] y軸方向のサイズ
	 */
	public int[] getMapSize() {
		return mapController.getSize();
	}

	public boolean isCollisionDoor() {
		return actionController.isCollisionDoor();
	}

	/**
	 * updateメソッドのオーバーライド
	 */
	public void update(Graphics g) {
		canvas.repaint();
		paint(g);
	}

	/**
	 * 報酬のテーブルを現在の地図に合わせて更新します。
	 */
	public void renewReward() {
		if( rewardMap != null) {
			int[] size = mapController.getSize();
			for(int x = 0; x < size[0]; x++) {
				for(int y = 0; y < size[1]; y++) {
					String rewardStr = mapController.getReward(x, y);
					rewardMap[x][y] = rewardStr;
				}
			}
		}
	}

	/**
	 * 点滅させます。
	 */
	public void flash() {
		canvas.flash();
	}

	//////////////////////////////////////////////////////////
	// private

	/**
	 * 引数で設定された位置にカギがあるかチェックし、
	 * カギがあれば閉じているドアを開きます。
	 * @param int x x座標
	 * @param int y y座標
	 */
	private void checkItem(int x, int y) {
		/* カギの場所 */
		if( (mapController.getString(x, y)).equals("K") ) {
			/*
			 * カギがなければ、カギ取得・ドア開く
			 * カギがあれば、カギをなくし・ドアを閉じる
			 */
			if(robotState[2] != KEY ) {
				robotState[2] = KEY;
//				openDoor();
			}else {
				robotState[2] = NOTHING;
//				closeDoor();
			}
//			controlDoor(robotState[2]);
		}
		/* 電話の場所 */
		if( (mapController.getString(x, y)).equals("T") ) {
			/*
			 * 電話がなければ、電話取得・ドア閉じる
			 * 電話があれば、電話をなくす
			 */
			if(robotState[2] != TELEPHON ) {
				robotState[2] = TELEPHON;
//				closeDoor();
			}else {
				robotState[2] = NOTHING;
			}
		}
// アイテムを増やした場合用
if((mapController.getString(x, y)).equals("A")) {
	if(robotState[2] != A ) {
		robotState[2] = A;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("B")) {
	if(robotState[2] != B ) {
		robotState[2] = B;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("a")) {
	if(robotState[2] != a ) {
		robotState[2] = a;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("b")) {
	if(robotState[2] != b ) {
		robotState[2] = b;
	}else {
		robotState[2] = NOTHING;
	}
}
// ここまで
		controlDoor(robotState[2]);
	}

	/* ドアを開けるアイテム */
	private int doorOpenItem = NOTHING;
	/**
	 * ドアを開けるアイテムを変更します。
	 * NOTHING = 0;
	 * KEY = 1;
	 * TELEPHON = 2;
	 */
	public void changeDoorOpenItem(int doorOpenItem) {
		this.doorOpenItem = doorOpenItem;
	}

	/**
	 * アイテムによってドアの制御を行います。
	 */
	private void controlDoor(int item) {
		if(item == doorOpenItem) {
			openDoor();
		}else {
			closeDoor();
		}
	}


	/**
	 * 閉じているドアを開ける
	 */
	private void openDoor() {
		/*
		 * 地図上の全位置をチェック
		 * 閉じているドア("D")があれば開いているドア("d")に換える
		 */
		int[] size = mapController.getSize();
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]; y++) {
				if( (mapController.getString(x, y)).equals("D") ) {
					mapController.set(x, y, "d");
				}
			}
		}
	}

	//////////////////////////////////////////////////
	// イベント処理

	/**
	 * マウスクリックのイベント処理を行うインナークラス
	 */
	class CanvasMouseAdapter extends MouseAdapter {

		/**
		 * マウスがクリックされた時の処理
		 */
		public void mouseClicked(MouseEvent e) {
			/* クリックされた位置の取得 */
			int xPos = e.getX();
			int yPos = e.getY();

			/* 間隔を取得 */
			int xSpace = canvas.getXSpace();
			int ySpace = canvas.getYSpace();

			int[] size = mapController.getSize();

			/* 地図の範囲内であれば処理を行う */
			if( ((xPos>xSpace)&&(yPos>ySpace)) &&
			        ((xPos<(xSpace*(size[0]+1))) &&
			        (yPos<(ySpace*(size[1]+1)))) ) {

				/* クリックされた位置を地図上の座標に変換 */
				int x = xPos / xSpace;
				int y = yPos / ySpace;

				if(!renewValue.equals("n")) {

					/* 複数のゴールを設定しない場合は以前の値を削除 */
					if(!flagGoals) {
						if(renewValue.equals("O(1)")) {
							int[] pos = mapController.getPos("O");
							if(pos != null) {
								mapController.set(pos[0], pos[1], "");
							}
						}
					}

					/* 地図情報の更新 */
					mapController.set(x-1, y-1, renewValue);

					/* 再描画 */
					canvas.repaint();
				}
				/* 報酬の更新 */
				renewReward();
			}
		}

	}


}

