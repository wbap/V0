/**
 * EnvironmentAgent.java
 * ドアキー課題の環境
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package doorkeydemo;

import environment.*;

import java.util.*;

/**
 * ドアキー課題の環境
 */
public class EnvironmentAgent {

	/* 環境 */
	private Environment environment = null;

	/* 環境の地図情報をもつファイル名 */
	private String fileName = null;
	/* ドアが開く条件 */
	private int doorOpenMode = 1;

	/* ランダムな行動に使用する乱数 */
	private Random randomMoveAction = new Random(0);
	/* 再スタート時のスタート位置をランダムに設定する乱数 */
	private Random randomStart = new Random(1);
	/* 再スタート時に保持しているアイテムをランダムに設定する乱数 */
	private Random randomItem = new Random(0);

	/* 手動で行動を設定するリスト */
	LinkedList actionList = new LinkedList();

	//////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param String fileName 環境の地図情報のファイル名
	 * @param int doorOpenMode ドアが開く条件 0:カギ 1:電話
	 * @param int agentNum エージェント数
	 */
	public EnvironmentAgent(String fileName, int doorOpenMode, int agentNum) {

		this.fileName = fileName;
		this.doorOpenMode = doorOpenMode;
		StringBuffer title = new StringBuffer();
		title.append("部屋：");
		if(fileName.equals("DoorKeyMap_a.csv")) {
			title.append("a");
		}else {
			title.append("b");
		}
		
		title.append("       ドアが開く条件：");
		if(doorOpenMode == 1) {
			title.append("カギ");
		}else {
			title.append("電話");
		}

		if(agentNum == 1) {
			title.append("        従来法");
		}else {
			title.append("        提案法");
		}

		environment = new Environment(fileName, title.toString());
		environment.changeDoorOpenItem(doorOpenMode);

		/* 実験開始時の行動を手動で設定 */
//		initActionList();
	}


	//////////////////////////////////////////////////
	// public

	/**
	 * 引数で設定されたactionで環境を動作させ、動作後の状態を取得します。
	 * actionが-1ならランダムな動作を行ないます。
	 * @param int action 行動
	 * @return Vector 状態
	 */
	public Vector move(int action) {

		if(action == -1) {
			/* 初期動作があれば利用 */
			Integer integer = getInitAction();
			if(integer != null) {
				action = integer.intValue();
			}else {
				action = getRandomAction();
			}
		}

		environment.run(action);

		return getState(action);
	}


	/**
	 * 再スタート時の処理。
	 * スタート位置をランダムに変更して、スタート位置に戻す。
	 * 保持しているアイテムをランダムに設定する。
	 */
	public void restart() {
		chageStart();
		environment.initRobotPos();
		environment.initMap();
		chageItem();
	}

	/**
	 * ゴール到達時の処理。
	 * 画面を点滅させ、ゴールへの到達回数をカウントする。
	 */
	public void goalReach() {
		environment.flash();
		environment.countGoalReachCount();
	}

	//////////////////////////////////////////////////
	// private

	/**
	 * 環境を引数で設定したactionで動作させ、動作後の状態を取得します。
	 * @param int action 行動
	 * @return Vector 状態
	 */
	private Vector getState(int action) {

		Vector state = new Vector();

		/* 位置を取得・設定 */
		int[] xystate = environment.getXYState();
		state.add(new Integer(xystate[0]));
		state.add(new Integer(xystate[1]));
		state.add(new Integer(action));

		/* 属性を取得・設定 */
		String str = environment.getMapInfo(xystate[0], xystate[1]);
		if(str.equals("T")) {
			state.add(new Integer(4));
		}else if(str.equals("d")) {
			state.add(new Integer(3));
		}else if(str.equals("K")) {
			state.add(new Integer(2));
		}else if(str.equals("O(1)")) {
			state.add(new Integer(1));
		}else {
			state.add(new Integer(0));
		}

		/* 保持しているアイテムを取得・設定 */
		state.add(new Integer(environment.getItem()) );

		/* 地図のID */
		if(fileName.equals("DoorKeyMap_a.csv")) {
			state.add(new Integer(1) );
		}else {
			state.add(new Integer(2) );
		}

		/* ドア開閉の条件 */
		state.add(new Integer(doorOpenMode) );

		return state;
	}

	/**
	 * スタート位置をランダムに変更します。
	 */
	private void chageStart() {
		/* スタート位置を変更 */
		int[] newStart = getRandomState(randomStart);
		environment.setStart(newStart[0], newStart[1]);
	}


	/**
	 * 保持しているアイテムをランダムに変更します。
	 */
	private void chageItem() {
		int newItem = randomItem.nextInt(3);
// アイテムを増やす場合の設定
//		int newItem = randomItem.nextInt(7);
		environment.setItem(newItem);
	}


	/**
	 * ランダムに生成された行動を取得します。
	 * @return int 行動
	 */
	private int getRandomAction() {
		/* 斜め移動なし */
		int randomNum = randomMoveAction.nextInt(4)*2;
		return randomNum;
	}

	/**
	 * ランダムに生成した状態を取得します。
	 * @return int[] ランダムに生成された状態
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	private int[] getRandomState(Random random) {

		int[] randomState = new int[2];

		/*
		 * ランダムに座標を取得、新たに指定された位置にすでに何か設定されて
		 * いる場合は再度座標を取得
		 */
		while(true) {
			/* 地図のサイズを取得 */
			int[] mapSize = environment.getMapSize();
			randomState[0] = random.nextInt(mapSize[0]-1) + 1;
			randomState[1] = random.nextInt(mapSize[1]-1) + 1;

			/* ドア内には設定しない */
			if(fileName.equals("DoorKeyMap_a.csv")) { // 環境a
				if( randomState[0] < 19 || randomState[1] < 19 ) {
					String newState = environment.getMapInfo(randomState[0],
					        randomState[1]);
					if(newState.equals("")) {
						break;
					}
				}
			}else { // 環境b
				if( randomState[0] < 17 ) {
					String newState = environment.getMapInfo(randomState[0],
					        randomState[1]);
					if(newState.equals("")) {
						break;
					}
				}
			}
		}

		return randomState;
	}

	/**
	 * 手動で設定されたactionを取得します。
	 */
	private Integer getInitAction() {
		Integer manualAction = null;
		if(actionList.size() > 0) {
			manualAction = (Integer)actionList.removeFirst();
		}
		return manualAction;
	}

	/**
	 * 行動を手動で設定します
	 */
	private void initActionList() {
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 5; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 11; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 9; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(2));
		}
		for(int i = 0; i < 16; i++) {
			actionList.add(new Integer(4));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 6; i++) {
			actionList.add(new Integer(4));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(6));
		}
	}


}

